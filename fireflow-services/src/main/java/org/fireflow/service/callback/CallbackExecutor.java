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
package org.fireflow.service.callback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.WorkflowQuery;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.WorkflowStatement;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceProperty;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.ServiceExecutionException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.impl.Restrictions;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.misc.ScriptContextVariableNames;
import org.fireflow.engine.misc.Utils;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.engine.modules.process.ProcessUtil;
import org.fireflow.engine.service.AbsServiceExecutor;
import org.fireflow.engine.service.ServiceExecutor;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.InputAssignment;
import org.fireflow.model.binding.OutputAssignment;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Expression;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class CallbackExecutor extends AbsServiceExecutor implements ServiceExecutor {
	private static Log log = LogFactory.getLog(CallbackExecutor.class);
	
	public static final String SERVICE_TYPE = "INTERFACE";
	
	public static final String CTX_KEY_PROCESS_ID = "PROCESS_ID";
	public static final String CTX_KEY_ACTIVITY_ID = "ACTIVITY_ID";
	public static final String CTX_KEY_CORRELATION = "CORRELATION";
	public static final String CTX_KEY_START_NEW_PROCESS = "START_NEW_PROCESS";
//	public static final String CTX_KEY_SERVICE_BINDING = "SERVICE_BINDING";
//	public static final String CTX_KEY_RESOURCE_BINDING = "RESOURCE_BINDING";
//	public static final String CTX_KEY_ACTIVITY = "ACTIVITY";
	public static final String CTX_KEY_PROCESS_TYPE = "PROCESS_TYPE";
	
	public String getServiceType(){
		return SERVICE_TYPE;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.ServiceExecutor#execute(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance, org.fireflow.model.binding.ServiceBinding, org.fireflow.model.binding.ResourceBinding)
	 */
	public boolean executeService(WorkflowSession session,
			ActivityInstance activityInstance, ServiceBinding serviceBinding,
			ResourceBinding resourceBinding) throws ServiceExecutionException {
		// TODO 发布服务？
		
		return false;
	}

	public void onCalled(WorkflowSession session,Map<String,Object> inputs,Map<String,Object> outputs,Map<String,Object> ctx) throws EngineException{
		Boolean startNewProcess = (Boolean)ctx.get(CallbackExecutor.CTX_KEY_START_NEW_PROCESS);
		if (startNewProcess==null){
			startNewProcess = false;
		}
		
		String processId = (String)ctx.get(CallbackExecutor.CTX_KEY_PROCESS_ID);
		assert (processId!=null && !processId.trim().equals("") );
		
		String activityId = (String)ctx.get(CallbackExecutor.CTX_KEY_ACTIVITY_ID);
		assert (activityId!=null && !activityId.trim().equals(""));

		
		String processType = (String)ctx.get(CallbackExecutor.CTX_KEY_PROCESS_TYPE);

		String correlation = (String)ctx.get(CallbackExecutor.CTX_KEY_CORRELATION);
		assert (correlation!=null && !correlation.trim().equals(""));
		
//		Object activity = ctx.get(CallbackExecutor.CTX_KEY_ACTIVITY);
//		if (!startNewProcess){
//			assert (activity!=null);
//		}
		
//		ServiceBinding serviceBinding = (ServiceBinding)ctx.get(CallbackExecutor.CTX_KEY_SERVICE_BINDING);
//		assert(serviceBinding!=null);
		
		Map<String,Object> args = inputs;
		
		RuntimeContext fireRuntimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		
		if (!startNewProcess){//调用某个中间节点
			
			WorkflowQuery<ActivityInstance> query = session.createWorkflowQuery(ActivityInstance.class,processType);
			List<ActivityInstance> candidates = query.add(Restrictions.eq(ActivityInstanceProperty.PROCESS_ID, processId))
				.add(Restrictions.eq(ActivityInstanceProperty.NODE_ID, activityId))
				.add(Restrictions.eq(ActivityInstanceProperty.STATE, ActivityInstanceState.RUNNING))
				.list();
			
			//匹配correlation
			ActivityInstance theMatchedActivityInstance = null;//匹配上的activityInstance
			ProcessInstance theMatchedProcessInstance = null;
			if (candidates!=null && candidates.size()>0){
				for (ActivityInstance activityInstance : candidates){
					ProcessInstance processInstance = activityInstance.getProcessInstance(session);
					Map<String,Object> varContext = Utils.fulfillScriptContext(session, processInstance, activityInstance);
					varContext.put(ScriptContextVariableNames.INPUTS, args);

					ScriptEngine scriptEngine = fireRuntimeContext.getScriptEngine(fireRuntimeContext.getDefaultScript());
					
					ScriptContext scriptContext = new SimpleScriptContext();
					Bindings engineScope = scriptContext
							.getBindings(ScriptContext.ENGINE_SCOPE);
					engineScope.putAll(varContext);
					try {
						Object result = scriptEngine.eval(correlation, scriptContext);
						if (result!=null && (result instanceof Boolean)){
							if ((Boolean)result){
								theMatchedActivityInstance = activityInstance;
								theMatchedProcessInstance = processInstance;
								break;
							}
						}
					} catch (ScriptException e) {
						
						e.printStackTrace();
					}
				}
			}
			
			if (theMatchedActivityInstance!=null){
				//首先设置currentProcessInstance和CurrentActivityInstance
				((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(theMatchedActivityInstance);
				((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(theMatchedProcessInstance);
				
				//设置流程变量
				ProcessUtil processRepositoryService = fireRuntimeContext.getEngineModule(ProcessUtil.class, processType);
				ProcessKey processKey = new ProcessKey(theMatchedActivityInstance.getProcessId(),theMatchedActivityInstance.getVersion(),
						theMatchedActivityInstance.getProcessType());
				ServiceBinding serviceBinding = null;
				try{
					serviceBinding = processRepositoryService.getServiceBinding(processKey, theMatchedActivityInstance.getNodeId());
				}catch(InvalidModelException e){
					log.error(e);
					throw new EngineException(theMatchedActivityInstance,e.getMessage());
				}
				List<InputAssignment> inputAssignments = serviceBinding.getInputAssignments();
				Map<String,Object> allTheVars = Utils.fulfillScriptContext(session, theMatchedProcessInstance, theMatchedActivityInstance);
				for (InputAssignment assignment:inputAssignments){
					Expression expression = assignment.getFrom();
					ScriptEngine scriptEngine = fireRuntimeContext.getScriptEngine(expression
							.getLanguage());
					ScriptContext scriptContext = new SimpleScriptContext();
					Bindings engineScope = scriptContext
							.getBindings(ScriptContext.ENGINE_SCOPE);
					engineScope.putAll(allTheVars);
					engineScope.put(ScriptContextVariableNames.INPUTS, args);
					
					Object obj;
					try {
						obj = scriptEngine.eval(expression.getBody(), scriptContext);
						String varName = assignment.getTo();
						if (varName.indexOf(ScriptContextVariableNames.PROCESS_VARIABLES)>=0){
							varName = varName .substring(ScriptContextVariableNames.PROCESS_VARIABLES.length()+1);
						}
						theMatchedProcessInstance.setVariableValue(session, varName, obj);
					} catch (InvalidOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (ScriptException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
					}
				}

				//执行closeActivity的操作	
//				Object activity = processRepositoryService.getActivity(processKey, theMatchedActivityInstance.getNodeId());
				this.onServiceCompleted(session, theMatchedActivityInstance);

				//返回结果
				allTheVars = Utils.fulfillScriptContext(session, theMatchedProcessInstance, theMatchedActivityInstance);
				List<OutputAssignment> outputAssignments = serviceBinding.getOutputAssignments();
				if (outputAssignments!= null && outputAssignments.size()>0){
					for (OutputAssignment outAssignment : outputAssignments) {

						Expression exp = outAssignment.getFrom();

						ScriptEngine scriptEngine = fireRuntimeContext
								.getScriptEngine(exp.getLanguage());
						ScriptContext scriptContext = new SimpleScriptContext();
						Bindings engineScope = scriptContext
								.getBindings(ScriptContext.ENGINE_SCOPE);
						engineScope.putAll(allTheVars);

						Object obj = null;
						try {
							obj = scriptEngine.eval(exp.getBody(),
									scriptContext);
						} catch (ScriptException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						String outputName = outAssignment.getTo();
						if (outputName.indexOf(ScriptContextVariableNames.OUTPUTS)>=0){
							outputName = outputName.substring(ScriptContextVariableNames.OUTPUTS.length()+1);
						}
						outputs.put(outputName, obj);
					}					
				}
			}else{
				throw new EngineException("Process instance NOT found for the conditions as follows,processId="+processId+" and activityId="+activityId+" and correlation='"+correlation+"'");
			}
		}else{//启动新的流程实例
			PersistenceService persistenceService = fireRuntimeContext.getEngineModule(PersistenceService.class, processType);
			ProcessPersister persister = persistenceService.getProcessPersister();
			ProcessDescriptor descriptor = persister.findTheLatestVersionOfProcessDescriptor(processId, processType);
			ProcessKey processKey = new ProcessKey(descriptor.getProcessId(),descriptor.getVersion(),descriptor.getProcessType());
			
			ProcessUtil processRepositoryService = fireRuntimeContext.getEngineModule(ProcessUtil.class, processType);
			
			//设置流程变量
			ServiceBinding serviceBinding = null;
			try{
				serviceBinding = processRepositoryService.getServiceBinding(processKey, activityId);			
			}catch(InvalidModelException e){
				log.error(e);
				throw new EngineException(e);
			}
			List<InputAssignment> inputAssignments = serviceBinding.getInputAssignments();
			Map<String,Object> allTheVars = new HashMap<String,Object>();
			Map<String,Object> initParams = new HashMap<String,Object>();
			for (InputAssignment assignment:inputAssignments){
				Expression expression = assignment.getFrom();
				ScriptEngine scriptEngine = fireRuntimeContext.getScriptEngine(expression
						.getLanguage());
				ScriptContext scriptContext = new SimpleScriptContext();
				Bindings engineScope = scriptContext
						.getBindings(ScriptContext.ENGINE_SCOPE);
				engineScope.putAll(allTheVars);
				engineScope.put(ScriptContextVariableNames.INPUTS, args);
				
				Object obj;
				try {
					obj = scriptEngine.eval(expression.getBody(), scriptContext);
					String varName = assignment.getTo();
					if (varName.indexOf(ScriptContextVariableNames.PROCESS_VARIABLES)>=0){
						varName = varName .substring(ScriptContextVariableNames.PROCESS_VARIABLES.length()+1);
					}
					initParams.put(varName, obj);
				} 
				catch (ScriptException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
				}
			}
			
			//启动流程
			WorkflowStatement stmt = session.createWorkflowStatement(processType);
			try {
				ProcessInstance processInstance = stmt.startProcess(processId,correlation, initParams);
				
				//返回结果
				allTheVars = Utils.fulfillScriptContext(session, processInstance, null);
				List<OutputAssignment> outputAssignments = serviceBinding.getOutputAssignments();
				if (outputAssignments!= null && outputAssignments.size()>0){
					for (OutputAssignment outAssignment : outputAssignments) {

						Expression exp = outAssignment.getFrom();

						ScriptEngine scriptEngine = fireRuntimeContext
								.getScriptEngine(exp.getLanguage());
						ScriptContext scriptContext = new SimpleScriptContext();
						Bindings engineScope = scriptContext
								.getBindings(ScriptContext.ENGINE_SCOPE);
						engineScope.putAll(allTheVars);

						Object obj = null;
						try {
							obj = scriptEngine.eval(exp.getBody(),
									scriptContext);
						} catch (ScriptException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						String outputName = outAssignment.getTo();
						if (outputName.indexOf(ScriptContextVariableNames.OUTPUTS)>=0){
							outputName = outputName.substring(ScriptContextVariableNames.OUTPUTS.length()+1);
						}
						outputs.put(outputName, obj);
					}					
				}				
			} catch (InvalidModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WorkflowProcessNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}
	}
}
