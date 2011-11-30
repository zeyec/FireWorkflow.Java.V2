/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.service.human.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.config.ReassignConfig;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.misc.ScriptContextVariableNames;
import org.fireflow.engine.misc.Utils;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.instancemanager.WorkItemManager;
import org.fireflow.engine.modules.ousystem.OUSystemAdapter;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ReassignConfigPersister;
import org.fireflow.engine.resource.ResourceResolver;
import org.fireflow.engine.service.AssignmentHandler;
import org.fireflow.model.binding.AssignmentStrategy;
import org.fireflow.model.binding.ParameterAssignment;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ResourceRef;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Expression;
import org.fireflow.model.data.Input;
import org.fireflow.model.resourcedef.ResolverDef;
import org.fireflow.model.resourcedef.Resource;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class DefaultAssignmentHandler implements AssignmentHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 772130701903585306L;

	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.human.AssignmentHandler#assign(org.fireflow.engine.WorkflowSession, org.fireflow.engine.service.human.WorkItemManager, org.fireflow.engine.entity.runtime.ActivityInstance, org.fireflow.model.binding.ServiceBinding, org.fireflow.model.binding.ResourceBinding)
	 */
	public List<WorkItem> assign(WorkflowSession session,
			ActivityInstance activityInstance,
			ServiceBinding serviceBinding, ResourceBinding resourceBinding)
			throws EngineException {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();

		WorkItemManager workItemManager = ctx.getEngineModule(WorkItemManager.class, activityInstance.getProcessType());

		ProcessInstance currentProcessInstance = session.getCurrentProcessInstance();
		
		List<ResourceRef> potentialOwnerDefs = null;
		if (resourceBinding!=null){
			potentialOwnerDefs = resourceBinding.getPotentialOwners();
		}
		List<User> potentialOwners =  resolveResources(session, currentProcessInstance,
				activityInstance, potentialOwnerDefs);	
		
		List<WorkItem> result = new ArrayList<WorkItem>();
		
		if (potentialOwners==null || potentialOwners.size()==0){
			//通知业务领导进行处理
			List<ResourceRef> administratorDefs = null;
			if (resourceBinding!=null){
				administratorDefs = resourceBinding.getAdministrators();
			}
			List<User> administrators = resolveResources(session, currentProcessInstance,
					activityInstance, administratorDefs);	
			if (administrators==null || administrators.size()==0){
				//TODO 赋值给Fireflow内置用户，并记录警告信息
				WorkItem wi = workItemManager.createWorkItem(session, currentProcessInstance, activityInstance, FireWorkflowSystem.getInstance(), null);
				result.add(wi);
			}else{
				Map<WorkItemProperty,Object> values = new HashMap<WorkItemProperty,Object>();
				values.put(WorkItemProperty.ASSIGNMENT_STRATEGY, AssignmentStrategy.ASSIGN_TO_ANY);
				
				for (User user : administrators) {
					//这种情况下，ASSIGNMENT_STRATEGY固定为WorkItem.ASSIGNMENT_ANY
					WorkItem wi = workItemManager.createWorkItem(session,
							currentProcessInstance, activityInstance, user, values);

					result.add(wi);
					
					List<User> agents = findReassignTo(ctx, activityInstance
							.getProcessId(), activityInstance.getProcessType(),
							activityInstance.getNodeId(), user.getId());
					if (agents != null && agents.size() != 0) {

						List<WorkItem> agentWorkItems = workItemManager
								.reassignWorkItemTo(session, wi, agents,
										WorkItem.REASSIGN_AFTER_ME,
										AssignmentStrategy.ASSIGN_TO_ANY);
						
						result.addAll(agentWorkItems);
					}

				}
			}
		}else{
			Map<WorkItemProperty,Object> values = new HashMap<WorkItemProperty,Object>();
			values.put(WorkItemProperty.ASSIGNMENT_STRATEGY, resourceBinding.getAssignmentStrategy());
			for (User user : potentialOwners) {
				
				WorkItem wi = workItemManager.createWorkItem(session,
						currentProcessInstance, activityInstance, user, values);
				result.add(wi);
				
				List<User> agents = findReassignTo(ctx, activityInstance
						.getProcessId(), activityInstance.getProcessType(),
						activityInstance.getNodeId(), user.getId());
				if (agents != null && agents.size() != 0) {

					List<WorkItem> agentWorkItems = workItemManager
							.reassignWorkItemTo(session, wi, agents,
									WorkItem.REASSIGN_AFTER_ME,
									AssignmentStrategy.ASSIGN_TO_ANY);
					
					result.addAll(agentWorkItems);
				}				
			}			
		}
		
		List<ResourceRef> readerDefs = null;
		if (resourceBinding!=null){
			readerDefs = resourceBinding.getReaders();
		}
		List<User> readers = resolveResources(session, currentProcessInstance,
				activityInstance, readerDefs);
		if (readers != null && readers.size() > 0) {
			Map<WorkItemProperty,Object> values = new HashMap<WorkItemProperty,Object>();
			values.put(WorkItemProperty.STATE, WorkItemState.READONLY);
			for (User user : readers) {
				WorkItem wi = workItemManager.createWorkItem(session,
						currentProcessInstance, activityInstance, user, values);

				result.add(wi);
			}
		}
		return result;
	}
	/**
	 * 解析参数
	 * @param parameterAssignment
	 * @return
	 */
	protected Map<String,Object> resolveParameters(WorkflowSession session,ProcessInstance processInstance,ActivityInstance activityInstance,Resource resource,List<ParameterAssignment> parameterAssignments){
		Map<String,Object> results = new HashMap<String,Object>();
		
		//首先初始化results 
		List<Input> parameters = resource.getResolver().getParameters();
		if (parameters!=null){
			for (Input parameter : parameters){
				String strValue = parameter.getDefaultValueAsString();
				if (strValue!=null && !strValue.trim().equals("")){
					Object value = Utils.string2Object(strValue, parameter.getDataType(), null);
					results.put(parameter.getName(), value);
				}else{
					results.put(parameter.getName(), null);
				}
			}
		}
		
		if (parameterAssignments==null || parameterAssignments.size()==0){
			return results;
		}
		
		Map<String,Object> fireVarCtx = Utils.fulfillScriptContext(session, processInstance, activityInstance);
		WorkflowSessionLocalImpl localSession = (WorkflowSessionLocalImpl) session;
		RuntimeContext ctx = localSession.getRuntimeContext();

		for (ParameterAssignment input : parameterAssignments) {
			Expression expression = input.getFrom();

			ScriptEngine scriptEngine = ctx.getScriptEngine(expression
					.getLanguage());
			ScriptContext scriptContext = new SimpleScriptContext();
			Bindings engineScope = scriptContext
					.getBindings(ScriptContext.ENGINE_SCOPE);
			engineScope.putAll(fireVarCtx);

			Object obj = null;

			try {
				obj = scriptEngine.eval(expression.getBody(), scriptContext);
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String inputName = input.getTo();
			
			int index = inputName.indexOf(ScriptContextVariableNames.INPUTS);
			if (index==0){
				inputName = inputName.substring(index+ScriptContextVariableNames.INPUTS.length()+1);
			}

			
			results.put(inputName, obj);

		}

		
		return results;
	}
	
	protected List<User> resolveResources(WorkflowSession session,
			ProcessInstance processInstance, ActivityInstance activityInstance,
			List<ResourceRef> resourceRefs) {
		if (resourceRefs == null || resourceRefs.size() == 0) {
			return null;
		}
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		BeanFactory beanFactory = ctx.getEngineModule(BeanFactory.class,
				activityInstance.getProcessType());

		List<User> users = new ArrayList<User>();

		for (ResourceRef resourceRef : resourceRefs) {
			Resource resource = resourceRef.getResource();
			if (resource == null) {
				// TODO 记录警告日志，
				break;
			}
			ResolverDef resolver = resource.getResolver();
			ResourceResolver resourceResolver = (ResourceResolver) beanFactory
					.getBean(resolver.getBeanName());
			List<ParameterAssignment> parameterAssignments = resourceRef
					.getParameterAssignments();
			Map<String, Object> parameterValues = this.resolveParameters(
					session, processInstance, activityInstance, resource,
					parameterAssignments);

			List<User> _users = resourceResolver.resolve(session,resource,
					parameterValues);
			users.addAll(_users);
		}
		return users;
	}
	
	protected List<User> findReassignTo(RuntimeContext rtCtx,String processId,String processType,String activityId,String userId){
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, processType);
		ReassignConfigPersister persister = persistenceService.getReassignConfigPersister();
		
		List<ReassignConfig> configs = persister.findReassignConfig(processId, processType, activityId,userId);
		
		if (configs==null || configs.size()==0) return null;
		
		List<User> agents = new ArrayList<User>();
		OUSystemAdapter ousystem = rtCtx.getEngineModule(OUSystemAdapter.class, processType);
		for (ReassignConfig config : configs){
			User u = ousystem.findUserById(config.getAgentId());
			agents.add(u);
		}
		return agents;
	}
}
