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
package org.fireflow.pdl.fpdl20.enginemodules;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.event.EventType;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEvent;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEventListener;
import org.fireflow.engine.modules.instancemanager.impl.AbsProcessInstanceManager;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.event.EventListenerDef;

/**
 * @author 非也
 * @version 2.0
 */
public class ProcessInstanceManagerFpdl20Impl extends AbsProcessInstanceManager {
	private Log log = LogFactory.getLog(ProcessInstanceManagerFpdl20Impl.class);
	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ProcessInstanceManager#createProcessInstance(org.fireflow.engine.WorkflowSession, java.lang.Object, java.lang.String, java.util.Map, org.fireflow.engine.entity.repository.ProcessDescriptor, org.fireflow.engine.entity.runtime.ActivityInstance)
	 */
	public ProcessInstance createProcessInstance(WorkflowSession session,
			Object workflowProcess, String bizId,ProcessDescriptor descriptor,
			ActivityInstance parentActivityInstance) {
		WorkflowProcess fpdl20Process = (WorkflowProcess)workflowProcess;
		WorkflowSessionLocalImpl sessionLocal = (WorkflowSessionLocalImpl)session;
		RuntimeContext context = sessionLocal.getRuntimeContext();
		CalendarService calendarService = context.getDefaultEngineModule(CalendarService.class);
		User u = sessionLocal.getCurrentUser();
		
		ProcessInstanceImpl processInstance = new ProcessInstanceImpl();
		processInstance.setProcessId(descriptor.getProcessId());
		processInstance.setVersion(descriptor.getVersion());
		processInstance.setProcessType(descriptor.getProcessType());
		processInstance.setBizId(bizId);
		processInstance.setName(descriptor.getName());
		String displayName = descriptor.getDisplayName();
		processInstance.setDisplayName((displayName==null||displayName.trim().equals(""))?descriptor.getName():displayName);
		processInstance.setBizCategory(fpdl20Process.getBizCategory());
		processInstance.setState(ProcessInstanceState.INITIALIZED);

		Date now = calendarService.getSysDate();
		processInstance.setCreatedTime(now);
		processInstance.setCreatorId(u.getId());
		processInstance.setCreatorName(u.getName());
		processInstance.setCreatorDeptId(u.getDeptId());
		processInstance.setCreatorDeptName(u.getDeptName());
		
		if (parentActivityInstance!=null){
			processInstance.setParentActivityInstanceId(parentActivityInstance.getId());
			processInstance.setParentProcessInstanceId(parentActivityInstance.getProcessInstanceId());
			processInstance.setParentScopeId(parentActivityInstance.getScopeId());
		}
		
		if (fpdl20Process.getDuration()!=null && fpdl20Process.getDuration().getValue()>0){
			Date expiredDate = calendarService.dateAfter(now, fpdl20Process.getDuration());
			processInstance.setExpiredTime(expiredDate);
		}

//		processInstance.setExpiredTime(time);
		
		return processInstance;
	}

	public void fireProcessInstanceEvent(WorkflowSession session,ProcessInstance processInstance,Object workflowElement,EventType eventType){
		if (!(workflowElement instanceof WorkflowProcess))return ;
		ProcessInstanceEvent event = new ProcessInstanceEvent();
		event.setSource(processInstance);
		event.setEventType(eventType);
		
		WorkflowProcess fpdl20Process = (WorkflowProcess)workflowElement;
		List<EventListenerDef> eventListeners = fpdl20Process.getEventListeners();
		if (eventListeners != null) {
			for (EventListenerDef eventListenerDef : eventListeners) {
				fireEvent(session, eventListenerDef, event);
			}
		}
	}
	
	private void fireEvent(WorkflowSession session,EventListenerDef eventListenerDef,ProcessInstanceEvent event){
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		BeanFactory beanFactory = runtimeContext.getEngineModule(BeanFactory.class, FpdlConstants.PROCESS_TYPE);
		
		String referencedBeanId = eventListenerDef.getBeanName();
		if (referencedBeanId!=null){
			try{
				Object listener = beanFactory.getBean(referencedBeanId);
				if (listener!=null && (listener instanceof ProcessInstanceEventListener)){
					((ProcessInstanceEventListener)listener).onProcessInstanceEventFired(event);
				}
			}catch(Exception e){
				log.error(e.getMessage(), e);
			}

		}
		
	}
	
}
