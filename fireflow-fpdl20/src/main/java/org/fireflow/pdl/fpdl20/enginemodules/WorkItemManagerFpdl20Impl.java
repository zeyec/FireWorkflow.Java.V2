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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.instancemanager.event.EventType;
import org.fireflow.engine.modules.instancemanager.event.WorkItemEvent;
import org.fireflow.engine.modules.instancemanager.event.WorkItemEventListener;
import org.fireflow.engine.modules.instancemanager.impl.AbsWorkItemManager;
import org.fireflow.engine.modules.process.ProcessUtil;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.event.EventListenerDef;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class WorkItemManagerFpdl20Impl extends AbsWorkItemManager {
	private Log log = LogFactory.getLog(WorkItemManagerFpdl20Impl.class);

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.instancemanager.WorkItemManager#fireWorkItemEvent(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.WorkItem, org.fireflow.engine.modules.instancemanager.event.EventType)
	 */
	public void fireWorkItemEvent(WorkflowSession session, WorkItem workItem,
			EventType eventType) {
		ActivityInstance activityInstance = workItem.getActivityInstance();
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		ProcessUtil processUtil = ctx.getEngineModule(ProcessUtil.class, activityInstance.getProcessType());
		ProcessKey pk = new ProcessKey(activityInstance.getProcessId(),activityInstance.getVersion(),activityInstance.getProcessType());
		
		Activity activity =  null;
		try{
			activity = (Activity)processUtil.getActivity(pk, activityInstance.getNodeId());
		}catch(InvalidModelException e){
			log.error(e);
			
		}
		if (activity == null){
			return;
		}
		WorkItemEvent event = new WorkItemEvent();
		event.setSource(workItem);
		event.setEventType(eventType);
		List<EventListenerDef> eventListeners = activity.getEventListeners();
		if (eventListeners != null) {
			for (EventListenerDef eventListenerDef : eventListeners) {
				fireEvent(session, eventListenerDef, event);
			}
		}
	}
	
	private void fireEvent(WorkflowSession session,EventListenerDef eventListenerDef,WorkItemEvent event){
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		BeanFactory beanFactory = runtimeContext.getEngineModule(BeanFactory.class, FpdlConstants.PROCESS_TYPE);
		
		String referencedBeanId = eventListenerDef.getBeanName();
		if (referencedBeanId!=null){
			try{
				Object _listener = beanFactory.getBean(referencedBeanId);
				if (_listener!=null && (_listener instanceof WorkItemEventListener)){
					((WorkItemEventListener)_listener).onWorkItemEventFired(event);
				}
			}catch(Exception e){
				log.error(e.getMessage(), e);
			}
		}
	}

}
