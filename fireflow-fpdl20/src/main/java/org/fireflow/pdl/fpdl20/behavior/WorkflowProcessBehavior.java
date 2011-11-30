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
package org.fireflow.pdl.fpdl20.behavior;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.impl.AbsVariable;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.impl.InternalSessionAttributeKeys;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.misc.Utils;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.ProcessInstanceManager;
import org.fireflow.engine.modules.instancemanager.event.EventType;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.engine.modules.persistence.VariablePersister;
import org.fireflow.model.data.Property;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.CancellationHandler;
import org.fireflow.pvm.pdllogic.CompensationHandler;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.ExecuteResult;
import org.fireflow.pvm.pdllogic.FaultHandler;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;

/**
 * @author 非也
 * @version 2.0
 */
public class WorkflowProcessBehavior implements WorkflowBehavior {
	public CompensationHandler getCompensationHandler(String compensationCode){
		return null;
	}
	
	public CancellationHandler getCancellationHandler(){
		return null;
	}
	
	public FaultHandler getFaultHandler(String errorCode){
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#continueOn(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ContinueDirection continueOn(WorkflowSession session,
			Token token, Object workflowElement) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		List<Token> childTokenList = kernelManager.getChildren(token);
		if (childTokenList==null || childTokenList.size()==0){
			return ContinueDirection.closeMe();
		}else{
			for (Token tk : childTokenList){
				if (tk.getState().getValue()<TokenState.DELIMITER.getValue()){
					return ContinueDirection.waitingForClose();
				}
			}
		}
		return ContinueDirection.closeMe();
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#execute(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ExecuteResult execute(WorkflowSession session, Token parentToken,
			Object workflowElement) {
		WorkflowProcess workflowProcess = (WorkflowProcess)workflowElement;
		Node entry = workflowProcess.getEntry();
		
		PObjectKey pobjectKey = new PObjectKey(parentToken.getProcessId(),parentToken.getVersion(),parentToken.getProcessType(),entry.getId());
		
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		kernelManager.fireChildPObject(session, pobjectKey, parentToken);
		
		ExecuteResult result = new ExecuteResult();
		result.setStatus(BusinessStatus.RUNNING);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#onTokenStateChanged(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public void onTokenStateChanged(WorkflowSession session, Token token,
			Object workflowElement) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceStrategy = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE);
		ProcessInstancePersister procInstPersistenceService = persistenceStrategy.getProcessInstancePersister();
		CalendarService calendarService = ctx.getEngineModule(CalendarService.class,FpdlConstants.PROCESS_TYPE);
		
		ProcessInstance oldProcInst = session.getCurrentProcessInstance();
		ProcessInstance procInst = oldProcInst;
		if (oldProcInst==null || !oldProcInst.getId().equals(token.getElementInstanceId())){
			procInst = procInstPersistenceService.find(ProcessInstance.class, token.getElementInstanceId());
			
		}

		try{
			ProcessInstanceState state = ProcessInstanceState.valueOf(token.getState().name());
			((ProcessInstanceImpl)procInst).setState(state);
			if (state.getValue()>ProcessInstanceState.DELIMITER.getValue()){
				((ProcessInstanceImpl)procInst).setEndTime(calendarService.getSysDate());
				
			}
			procInstPersistenceService.saveOrUpdate(procInst);
			
			if (state.getValue() > ProcessInstanceState.DELIMITER.getValue()) {
				// 发布AFTER_PROCESS_INSTANCE_END事件
				ProcessInstanceManager processInstanceManager = ctx
						.getEngineModule(ProcessInstanceManager.class,
								FpdlConstants.PROCESS_TYPE);
				processInstanceManager.fireProcessInstanceEvent(session, procInst,
						workflowElement, EventType.AFTER_PROCESS_INSTANCE_END);
			}
		}finally{
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(oldProcInst);
		}

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#prepare(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public Boolean prepare(WorkflowSession session, Token token,
			Object workflowElement) {
		WorkflowProcess workflowProcess = (WorkflowProcess)workflowElement;
		
		WorkflowSessionLocalImpl sessionLocal = (WorkflowSessionLocalImpl)session;
		RuntimeContext context = sessionLocal.getRuntimeContext();
		ProcessInstanceManager processInstanceManager = context.getEngineModule(ProcessInstanceManager.class,FpdlConstants.PROCESS_TYPE);
		PersistenceService persistenceService = context.getEngineModule(PersistenceService.class, token.getProcessType());
		ProcessPersister processRepositoryPersister = persistenceService.getProcessPersister();
		ProcessKey pk = ProcessKey.valueOf(token);
		ProcessDescriptor processDescriptor = processRepositoryPersister.findProcessDescriptorByProcessKey(pk);

		PersistenceService persistenceStrategy = context.getEngineModule(PersistenceService.class,FpdlConstants.PROCESS_TYPE);
		VariablePersister variableService = persistenceStrategy.getVariablePersister();
		ProcessInstancePersister procInstPersistSvc = persistenceStrategy.getProcessInstancePersister();
		
		//1、创建流程实例，设置初始化参数
		String bizId = (String)session.getAttribute(InternalSessionAttributeKeys.BIZ_ID);
		Map<String, Object> variables = (Map<String, Object>) session
				.getAttribute(InternalSessionAttributeKeys.VARIABLES);
		ActivityInstance parentActivityInstance = session
				.getCurrentActivityInstance();
		ProcessInstance parentProcessInstance = session
				.getCurrentProcessInstance();

		ProcessInstance newProcessInstance = processInstanceManager
				.createProcessInstance(sessionLocal, workflowElement, bizId,
						processDescriptor,parentActivityInstance);
		((ProcessInstanceImpl)newProcessInstance).setTokenId(token.getId());
		procInstPersistSvc.saveOrUpdate(newProcessInstance);
		
		token.setProcessInstanceId(newProcessInstance.getId());
		token.setElementInstanceId(newProcessInstance.getId());
		TokenPersister tokenPersister = persistenceService.getTokenPersister();
		tokenPersister.saveOrUpdate(token);
		
		//2、初始化流程变量
		List<Property> processProperties = workflowProcess.getProperties();
		Map<String,Variable> initVars = new HashMap<String,Variable>();
		if (processProperties!=null){
			for (Property property:processProperties){
				String valueAsStr = property.getInitialValueAsString();
				Object value = null;
				if (valueAsStr!=null && valueAsStr.trim()!=null){
					value = Utils.string2Object(property.getInitialValueAsString(), property.getDataType(), property.getDataPattern());
				}

				Variable v = variableService.setVariable(newProcessInstance, property.getName(), value);
				initVars.put(v.getName(), v);
			}
		}
		
		if (variables != null && variables.size() > 0) {
			Iterator<Entry<String, Object>> it = variables.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<String, Object> entry = it.next();

				Variable v = initVars.get(entry.getKey());
				if (v!=null){
					((AbsVariable)v).setValue(entry.getValue());
					((AbsVariable)v).setDataType(entry.getValue()==null?"":entry.getValue().getClass().getName());
					variableService.saveOrUpdate(v);
				}else{
					
					variableService.setVariable(newProcessInstance, entry.getKey(), entry.getValue());
				}
			}
		}
		
		//3、发布事件
		processInstanceManager.fireProcessInstanceEvent(session, newProcessInstance, workflowElement, EventType.ON_PROCESS_INSTANCE_CREATED);
		
		//4、设置session和token
		token.setProcessInstanceId(newProcessInstance.getId());
		token.setElementInstanceId(newProcessInstance.getId());
		
		sessionLocal.setCurrentProcessInstance(newProcessInstance);

		return true;//true表示告诉虚拟机，“我”已经准备妥当了。
	}

	public void abort(WorkflowSession session,Token thisToken,Object workflowElement){

	}
}
