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

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.instancemanager.WorkItemManager;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.service.AssignmentHandler;
import org.fireflow.model.binding.AssignmentStrategy;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ResourceRef;
import org.fireflow.model.binding.ServiceBinding;

/**
 * 用于退签收时进行工作项分配。
 * 
 * @author 非也
 * @version 2.0
 */
public class DisclaimAssignmentHandler extends DefaultAssignmentHandler implements AssignmentHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2833396919970082210L;

	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.human.AssignmentHandler#assign(org.fireflow.engine.WorkflowSession, org.fireflow.engine.service.human.WorkItemManager, org.fireflow.engine.entity.runtime.ActivityInstance, org.fireflow.model.binding.ServiceBinding, org.fireflow.model.binding.ResourceBinding)
	 */
	@Override
	public List<WorkItem> assign(WorkflowSession session, ActivityInstance activityInstance,
			ServiceBinding serviceBinding, ResourceBinding resourceBinding)
			throws EngineException {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		WorkItemManager workItemManager = ctx.getEngineModule(WorkItemManager.class, activityInstance.getProcessType());
		
		ProcessInstance currentProcessInstance = session.getCurrentProcessInstance();
		
		List<ResourceRef> potentialOwnerDefs = resourceBinding.getPotentialOwners();
		List<User> potentialOwners =  resolveResources(session, currentProcessInstance,
				activityInstance, potentialOwnerDefs);	
		
		List<WorkItem> result = new ArrayList<WorkItem>();
		
		if (potentialOwners==null || potentialOwners.size()==0){
			//通知业务领导进行处理
			List<ResourceRef> administratorDefs = resourceBinding.getAdministrators();
			List<User> administrators = resolveResources(session, currentProcessInstance,
					activityInstance, administratorDefs);	
			if (administrators==null || administrators.size()==0){
				//赋值给Fireflow内置用户，并记录警告信息
				WorkItem wi = workItemManager.createWorkItem(session, currentProcessInstance, activityInstance, FireWorkflowSystem.getInstance(), null);
				result.add(wi);
			}else{
				Map<WorkItemProperty,Object> values = new HashMap<WorkItemProperty,Object>();
				values.put(WorkItemProperty.ASSIGNMENT_STRATEGY, AssignmentStrategy.ASSIGN_TO_ANY);

				for (User user : administrators) {
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
		return result;
	}

}
