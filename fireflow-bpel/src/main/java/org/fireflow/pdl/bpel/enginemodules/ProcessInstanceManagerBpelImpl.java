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
package org.fireflow.pdl.bpel.enginemodules;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.event.EventType;
import org.fireflow.engine.modules.instancemanager.impl.AbsProcessInstanceManager;
import org.fireflow.engine.modules.ousystem.User;

/**
 * @author 非也
 * @version 2.0
 */
public class ProcessInstanceManagerBpelImpl extends AbsProcessInstanceManager {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ProcessInstanceManager#createProcessInstance(org.fireflow.engine.WorkflowSession, java.lang.Object, java.lang.String, java.util.Map, org.fireflow.engine.entity.repository.ProcessDescriptor, org.fireflow.engine.entity.runtime.ActivityInstance)
	 */
	public ProcessInstance createProcessInstance(WorkflowSession session,
			Object workflowProcess, String bizId,ProcessDescriptor descriptor,
			ActivityInstance parentActivityInstance) {
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
		processInstance.setDisplayName(descriptor.getDisplayName());
		processInstance.setState(ProcessInstanceState.INITIALIZED);

		processInstance.setCreatedTime(calendarService.getSysDate());

		processInstance.setCreatorId(u.getId());
		processInstance.setCreatorName(u.getName());
		processInstance.setCreatorDeptId(u.getDeptId());
		processInstance.setCreatorDeptName(u.getDeptName());
		
//		processInstance.setExpiredTime(time);
		
		

		
		
		return processInstance;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ProcessInstanceManager#fireProcessInstanceEvent(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ProcessInstance, java.lang.Object, int)
	 */
	public void fireProcessInstanceEvent(WorkflowSession session,
			ProcessInstance processInstance, Object workflowElement,
			EventType eventType) {
		// TODO Auto-generated method stub
		
	}



}
