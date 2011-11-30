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
package org.fireflow.engine.modules.instancemanager.impl;

import java.util.List;
import java.util.Map;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.context.RuntimeContextAware;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.impl.InternalSessionAttributeKeys;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;
import org.fireflow.engine.modules.instancemanager.ProcessInstanceManager;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;

/**
 * @author 非也
 * @version 2.0
 */
public abstract class AbsProcessInstanceManager implements ProcessInstanceManager,RuntimeContextAware{
	protected RuntimeContext runtimeContext = null;

	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ProcessInstanceManager#runProcessInstance(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ProcessInstance)
	 */
	public ProcessInstance startProcess(WorkflowSession session,String workflowProcessId, int version,String processType,
			String bizId, Map<String, Object> variables)
			throws InvalidModelException,
			WorkflowProcessNotFoundException, InvalidOperationException{
		assert (session instanceof WorkflowSessionLocalImpl);
		
		session.setAttribute(InternalSessionAttributeKeys.BIZ_ID, bizId);
		session.setAttribute(InternalSessionAttributeKeys.VARIABLES, variables);
		RuntimeContext context = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = context.getDefaultEngineModule(KernelManager.class);			
		kernelManager.startPObject(session, new PObjectKey(workflowProcessId,version,processType,workflowProcessId));
		
		return session.getCurrentProcessInstance();
	}
	
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ProcessInstanceManager#abortProcessInstance(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ProcessInstance)
	 */
	public ProcessInstance abortProcessInstance(WorkflowSession session,
			ProcessInstance processInstance) {		
		RuntimeContext context = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
//		PersistenceService persistenceService = context.getEngineModule(PersistenceService.class, processInstance.getProcessType());
		
//		//1、首先abort相应的activityInstance
//		ActivityInstancePersister actInstPersister = persistenceService.getActivityInstancePersister();
//		List<ActivityInstance> activityInstanceList = actInstPersister.findActivityInstances(processInstance.getId());
//		ActivityInstanceManager actInstMgr = context.getEngineModule(ActivityInstanceManager.class, processInstance.getProcessType());
//		if(activityInstanceList!=null){
//			for (ActivityInstance activityInstance : activityInstanceList){
//				if (activityInstance.getState().getValue()<ActivityInstanceState.DELIMITER.getValue()){
//					actInstMgr.abortActivityInstance(session, activityInstance);
//				}
//			}
//		}
//		
//		//2、然后abort processInstance
//		CalendarService calendarService = context.getEngineModule(CalendarService.class, processInstance.getProcessType());
//		ProcessInstancePersister persister = persistenceService.getProcessInstancePersister();
//		((ProcessInstanceImpl)processInstance).setState(ProcessInstanceState.ABORTED);
//		((ProcessInstanceImpl)processInstance).setEndTime(calendarService.getSysDate());
//		persister.saveOrUpdate(processInstance);
		
		KernelManager kernelManager = context.getDefaultEngineModule(KernelManager.class);		
		Token token = kernelManager.getToken(processInstance.getTokenId(), processInstance.getProcessType());

		kernelManager.fireTerminationEvent(session, token, null);

		return processInstance;
	}



	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ProcessInstanceManager#restoreProcessInstance(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ProcessInstance)
	 */
	public ProcessInstance restoreProcessInstance(WorkflowSession session,
			ProcessInstance processInstance) {
		RuntimeContext context = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceService = context.getEngineModule(PersistenceService.class, processInstance.getProcessType());
		
		//1、首先suspend相应的activityInstance
		ActivityInstancePersister actInstPersister = persistenceService.getActivityInstancePersister();
		List<ActivityInstance> activityInstanceList = actInstPersister.findActivityInstances(processInstance.getId());
		ActivityInstanceManager actInstMgr = context.getEngineModule(ActivityInstanceManager.class, processInstance.getProcessType());
		if(activityInstanceList!=null){
			for (ActivityInstance activityInstance : activityInstanceList){
				if (activityInstance.getState().getValue()<ActivityInstanceState.DELIMITER.getValue()){
					actInstMgr.restoreActivityInstance(session, activityInstance);
				}
			}
		}
		
		//2、然后suspend processInstance
		
		ProcessInstancePersister persister = persistenceService.getProcessInstancePersister();
		((ProcessInstanceImpl)processInstance).setSuspended(false);
		persister.saveOrUpdate(processInstance);
		return processInstance;
	}



	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ProcessInstanceManager#suspendProcessInstance(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ProcessInstance)
	 */
	public ProcessInstance suspendProcessInstance(WorkflowSession session,
			ProcessInstance processInstance) {
		RuntimeContext context = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceService = context.getEngineModule(PersistenceService.class, processInstance.getProcessType());
		
		//1、首先suspend相应的activityInstance
		ActivityInstancePersister actInstPersister = persistenceService.getActivityInstancePersister();
		List<ActivityInstance> activityInstanceList = actInstPersister.findActivityInstances(processInstance.getId());
		ActivityInstanceManager actInstMgr = context.getEngineModule(ActivityInstanceManager.class, processInstance.getProcessType());
		if(activityInstanceList!=null){
			for (ActivityInstance activityInstance : activityInstanceList){
				if (activityInstance.getState().getValue()<ActivityInstanceState.DELIMITER.getValue()){
					actInstMgr.suspendActivityInstance(session, activityInstance);
				}
			}
		}
		
		//2、然后suspend processInstance
		
		ProcessInstancePersister persister = persistenceService.getProcessInstancePersister();
		((ProcessInstanceImpl)processInstance).setSuspended(true);
		persister.saveOrUpdate(processInstance);
		return processInstance;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#getRuntimeContext()
	 */
	public RuntimeContext getRuntimeContext() {
		return runtimeContext;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#setRuntimeContext(org.fireflow.engine.context.RuntimeContext)
	 */
	public void setRuntimeContext(RuntimeContext ctx) {
		runtimeContext = ctx;		
	}

}
