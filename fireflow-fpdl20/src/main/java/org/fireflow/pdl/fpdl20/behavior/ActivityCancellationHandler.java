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

import java.util.List;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ScheduleJob;
import org.fireflow.engine.entity.runtime.ScheduleJobState;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.entity.runtime.impl.ScheduleJobImpl;
import org.fireflow.engine.entity.runtime.impl.WorkItemImpl;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.ScheduleJobPersister;
import org.fireflow.engine.modules.persistence.WorkItemPersister;
import org.fireflow.engine.modules.schedule.Scheduler;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.pdllogic.CancellationHandler;

/**
 * @author 非也
 * @version 2.0
 */
public class ActivityCancellationHandler implements CancellationHandler {

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.CancellationHandler#handleCancellation(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public void handleCancellation(WorkflowSession session, Token token,
			Object workflowEement) {		
		//1、检验currentActivityInstance和currentProcessInstance的一致性
		ProcessInstance oldProcInst = session.getCurrentProcessInstance();
		ActivityInstance oldActInst = session.getCurrentActivityInstance();

		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceService = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE);
		ActivityInstancePersister actInstPersistenceService = persistenceService.getActivityInstancePersister();
		ProcessInstancePersister processInstancePersister = persistenceService.getProcessInstancePersister();
		
		if (oldProcInst==null || !oldProcInst.getId().equals(token.getProcessInstanceId())){
			ProcessInstance procInst = processInstancePersister.find(ProcessInstance.class, token.getProcessInstanceId());
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);
		}
		if (oldActInst==null || !oldActInst.getId().equals(token.getElementInstanceId())){
			ActivityInstance actInst = actInstPersistenceService.find(ActivityInstance.class, token.getElementInstanceId());
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
		}
		
		try{
			ActivityInstance thisActInst = session.getCurrentActivityInstance();
			// 将Workitem设置为Cancelled状态，
			WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
			List<WorkItem> workItems = workItemPersister.findWorkItemsForActivityInstance(thisActInst.getId());
			if (workItems!=null && workItems.size()>0){
				for (WorkItem wi : workItems){
					if (wi.getState().getValue()<WorkItemState.DELIMITER.getValue()){
						((WorkItemImpl)wi).setState(WorkItemState.CANCELLED);
						workItemPersister.saveOrUpdate(wi);
					}
				}
			}
			
			//将调度器中的timmer删除		
			ScheduleJobPersister scheduleJobPersister = persistenceService.getScheduleJobPersister();
			List<ScheduleJob> scheduleJobs = scheduleJobPersister.findScheduleJob4ActivityInstance(thisActInst.getId());
			if (scheduleJobs!=null && scheduleJobs.size()>0){
				for (ScheduleJob job : scheduleJobs){
					if (job.getState().getValue()<ScheduleJobState.DELIMITER.getValue()){
						Scheduler scheduler = ctx.getEngineModule(Scheduler.class, thisActInst.getProcessType());
						scheduler.unSchedule(job, ctx);
						
						((ScheduleJobImpl)job).setState(ScheduleJobState.CANCELLED);
						scheduleJobPersister.saveOrUpdate(job);
					}
				}
			}
		}finally{
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(oldProcInst);
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(oldActInst);
		}
	}

}
