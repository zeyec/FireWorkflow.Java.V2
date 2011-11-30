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
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.ServiceExecutionException;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.event.ActivityInstanceEvent;
import org.fireflow.engine.modules.instancemanager.event.ActivityInstanceEventListener;
import org.fireflow.engine.modules.instancemanager.event.EventType;
import org.fireflow.engine.modules.instancemanager.impl.AbsActivityInstanceManager;
import org.fireflow.engine.service.ServiceExecutor;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.servicedef.Service;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.event.EventListenerDef;

/**
 * @author 非也
 * @version 2.0
 */
public class ActivityInstanceManagerFpdl20Impl extends
		AbsActivityInstanceManager {
	private Log log = LogFactory.getLog(ActivityInstanceManagerFpdl20Impl.class);

	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ActivityInstanceManager#createActivityInstance(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ProcessInstance, java.lang.Object)
	 */
	public ActivityInstance createActivityInstance(WorkflowSession session,
			ProcessInstance processInstance, Object activity) {
		CalendarService calendarService = this.runtimeContext.getDefaultEngineModule(CalendarService.class);
		
		
		Node node = (Node)activity;
		ActivityInstanceImpl actInst = new ActivityInstanceImpl();
		actInst.setName(node.getName());
		String displayName = node.getName();		
		actInst.setDisplayName((displayName==null || displayName.trim().equals(""))?node.getName():displayName);
		actInst.setState(ActivityInstanceState.INITIALIZED);
		
		actInst.setProcessName(processInstance.getName());
		actInst.setProcessDisplayName(processInstance.getDisplayName());
		actInst.setBizCategory(processInstance.getBizCategory());
		
		actInst.setProcessId(processInstance.getProcessId());
		actInst.setVersion(processInstance.getVersion());
		actInst.setProcessType(processInstance.getProcessType());
		actInst.setProcessInstanceId(processInstance.getId());
		actInst.setNodeId(node.getId());		
		actInst.setBizId(processInstance.getBizId());
		
		actInst.setParentScopeId(processInstance.getScopeId());
		
		Date now = calendarService.getSysDate();
		actInst.setCreatedTime(now);

		if (node instanceof Activity){
			Activity fpdl20Activity = (Activity)node;
			
			if (fpdl20Activity.getDuration()!=null && fpdl20Activity.getDuration().getValue()>0){
				Date expiredDate = calendarService.dateAfter(now, fpdl20Activity.getDuration());
				actInst.setExpiredTime(expiredDate);
			}
			
		}
		
		return actInst;
	}

	public void fireActivityInstanceEvent(WorkflowSession session,ActivityInstance actInstance,Object workflowElement,EventType eventType){
		if (!(workflowElement instanceof Activity))return ;
		ActivityInstanceEvent event = new ActivityInstanceEvent();
		event.setSource(actInstance);
		event.setEventType(eventType);
		
		Activity activity = (Activity)workflowElement;
		List<EventListenerDef> eventListeners = activity.getEventListeners();
		if (eventListeners != null) {
			for (EventListenerDef eventListenerDef : eventListeners) {
				fireEvent(session, eventListenerDef, event);
			}
		}
	}
	
	private void fireEvent(WorkflowSession session,EventListenerDef eventListenerDef,ActivityInstanceEvent event){
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		BeanFactory beanFactory = runtimeContext.getEngineModule(BeanFactory.class, FpdlConstants.PROCESS_TYPE);
		
		String referencedBeanId = eventListenerDef.getBeanName();
		if (referencedBeanId!=null){
			try{
				Object _listener = beanFactory.getBean(referencedBeanId);
				if (_listener!=null && (_listener instanceof ActivityInstanceEventListener)){
					((ActivityInstanceEventListener)_listener).onActivityInstanceEventFired(event);
				}
			}catch(Exception e){
				log.error(e.getMessage(), e);
			}
		}
	}



	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ActivityInstanceManager#runActivityInstance(org.fireflow.engine.WorkflowSession, java.lang.Object, org.fireflow.engine.entity.runtime.ActivityInstance)
	 */
	public boolean runActivityInstance(WorkflowSession session,
			Object workflowElement, ActivityInstance activityInstance)
			throws ServiceExecutionException {
		Activity activity = (Activity)workflowElement;	
		
		//调用ServiceExecutor
		WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;
		RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();

		ServiceBinding serviceBinding = activity.getServiceBinding();
		if (serviceBinding!=null){
			Service service = serviceBinding.getService();
			ServiceExecutor serviceExecutor = this.getServiceExecutor(runtimeContext, service, activityInstance.getProcessType());

			if (serviceExecutor==null){
				throw new EngineException(session.getCurrentActivityInstance(),
						"Unregisted service type : "+service.getServiceType());
			}

			boolean b = serviceExecutor.executeService(sessionLocalImpl, session.getCurrentActivityInstance(), serviceBinding, activity.getResourceBinding());
				
			return b;
		}else{
			return true;
		}

	}
	
	public int tryCloseActivityInstance(WorkflowSession session,ActivityInstance activityInstance,Object workflowElement){
		Activity activity = (Activity)workflowElement;		
		//调用ServiceExecutor
		WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;
		RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();

		ServiceBinding serviceBinding = activity.getServiceBinding();
		if (serviceBinding!=null){
			String serviceType = serviceBinding.getService().getServiceType();
			ServiceExecutor serviceExecutor = this.getServiceExecutor(runtimeContext, serviceBinding.getService(), activityInstance.getProcessType());
			
			if (serviceExecutor==null){
				throw new EngineException(session.getCurrentActivityInstance(),
						"Unregisted service type : "+serviceType);
			}
			int b = serviceExecutor.determineActivityCloseStrategy(sessionLocalImpl, activityInstance);
			
			return b;
		}else{
			return ServiceExecutor.CLOSE_ACTIVITY;
		}

	}	
	
	
}
