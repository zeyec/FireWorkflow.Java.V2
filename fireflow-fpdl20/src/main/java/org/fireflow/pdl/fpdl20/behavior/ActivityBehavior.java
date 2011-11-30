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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ScheduleJob;
import org.fireflow.engine.entity.runtime.ScheduleJobState;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.ScheduleJobImpl;
import org.fireflow.engine.entity.runtime.impl.WorkItemImpl;
import org.fireflow.engine.exception.ServiceExecutionException;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;
import org.fireflow.engine.modules.instancemanager.event.EventType;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.ScheduleJobPersister;
import org.fireflow.engine.modules.persistence.WorkItemPersister;
import org.fireflow.engine.modules.schedule.Scheduler;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.StartNode;
import org.fireflow.pdl.fpdl20.process.decorator.Decorator;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.TimerStartDecorator;
import org.fireflow.pvm.kernel.BookMark;
import org.fireflow.pvm.kernel.ExecutionEntrance;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.kernel.impl.TokenImpl;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.CancellationHandler;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.ExecuteResult;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;

/**
 * @author 非也
 * @version 2.0
 */
public class ActivityBehavior extends AbsNodeBehavior implements WorkflowBehavior {
	
	Log log = LogFactory.getLog(ActivityBehavior.class);
	
	private CancellationHandler cancellationHandler = new ActivityCancellationHandler();
	
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#prepare(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public Boolean prepare(WorkflowSession session, Token token,
			Object workflowElement) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		ActivityInstanceManager activityInstanceMgr = ctx.getEngineModule(ActivityInstanceManager.class, FpdlConstants.PROCESS_TYPE);
		PersistenceService persistenceStrategy = ctx.getEngineModule(PersistenceService.class, token.getProcessType());
		ActivityInstancePersister actInstPersistSvc = persistenceStrategy.getActivityInstancePersister();
		
		ProcessInstance processInstance = session.getCurrentProcessInstance();
		//1、创建并保存环节实例
		ActivityInstanceImpl activityInstance = (ActivityInstanceImpl)activityInstanceMgr.createActivityInstance(session, processInstance, workflowElement);
		
		activityInstance.setStepNumber(token.getStepNumber());
		activityInstance.setTokenId(token.getId());
		actInstPersistSvc.saveOrUpdate(activityInstance);
		
		//2、设置session和token
		((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(activityInstance);
		
		token.setElementInstanceId(activityInstance.getId());		
	
		//3、初始化活动的流程变量
		//TODO 保留
		
		//4、发布事件
		activityInstanceMgr.fireActivityInstanceEvent(session, activityInstance, workflowElement, EventType.ON_ACTIVITY_INSTANCE_CREATED);
		

		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#execute(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ExecuteResult execute(WorkflowSession session, Token token,
			Object workflowElement) {
		Activity activity = (Activity)workflowElement;
		log.debug("Activity[id="+activity.getId()+"] Behavior executed!");
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();

		//1、检验currentActivityInstance和currentProcessInstance的一致性
		ProcessInstance oldProcInst = session.getCurrentProcessInstance();
		ActivityInstance oldActInst = session.getCurrentActivityInstance();

		PersistenceService persistenceStrategy = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE);
		ActivityInstancePersister actInstPersistenceService = persistenceStrategy.getActivityInstancePersister();
		ProcessInstancePersister processInstancePersister = persistenceStrategy.getProcessInstancePersister();
		
		if (oldProcInst==null || !oldProcInst.getId().equals(token.getProcessInstanceId())){
			ProcessInstance procInst = processInstancePersister.find(ProcessInstance.class, token.getProcessInstanceId());
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);
		}
		if (oldActInst==null || !oldActInst.getId().equals(token.getElementInstanceId())){
			ActivityInstance actInst = actInstPersistenceService.find(ActivityInstance.class, token.getElementInstanceId());
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
		}
		
		//2、执行业务操作
		ActivityInstanceManager activityInstanceMgr = ctx.getEngineModule(ActivityInstanceManager.class, FpdlConstants.PROCESS_TYPE);
		ActivityInstance currentActivityInstance = session.getCurrentActivityInstance();
		
		try {
			boolean b = activityInstanceMgr.runActivityInstance(session, workflowElement, currentActivityInstance);
			
			if (b){
				ExecuteResult result = new ExecuteResult();
				result.setStatus(BusinessStatus.COMPLETED);
				return result;
			}else{
				//只有在这种情况下，启动边上的Timer节点才有意义
				List<StartNode> attachedStartNodes = activity.getAttachedStartNodes();
				if (attachedStartNodes!=null){
					for (StartNode startNode : attachedStartNodes){
						Decorator decorator = startNode.getDecorator();
						//启动timer类型的边节点
						if (decorator instanceof TimerStartDecorator){
							
							Token childToken = new TokenImpl(token);
							childToken.setElementId(startNode.getId());
							childToken.setParentTokenId(token.getParentTokenId());
							childToken.setAttachedToToken(token.getId());
							
							BookMark bookMark = new BookMark();
							bookMark.setToken(childToken);
							bookMark.setExtraArg(BookMark.SOURCE_TOKEN, token);
							bookMark.setExecutionEntrance(ExecutionEntrance.TAKE_TOKEN);
							
							KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
							
							kernelManager.addBookMark(bookMark);
						}
					}
				}				
				
				
				ExecuteResult result = new ExecuteResult();
				result.setStatus(BusinessStatus.RUNNING);
				return result;
			}
		} catch (ServiceExecutionException e) {
			// TODO Auto-generated catch block
			//TODO 进行异常处理，记录日志
			e.printStackTrace();
			
			ExecuteResult result = new ExecuteResult();
			result.setErrorCode(e.getErrorCode());
			result.setStatus(BusinessStatus.FAULTING);
			return result;
		}finally{
			
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(oldProcInst);
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(oldActInst);
		}
		

	}
	
	public ContinueDirection continueOn(WorkflowSession session, Token token,
			Object workflowElement) {
		if (token.getState().getValue()<TokenState.DELIMITER.getValue() && 
				token.getState().getValue()!=TokenState.RUNNING.getValue()){
			return ContinueDirection.closeMe();
		}
		
		//1、检验currentActivityInstance和currentProcessInstance的一致性
		ProcessInstance oldProcInst = session.getCurrentProcessInstance();
		ActivityInstance oldActInst = session.getCurrentActivityInstance();

		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceStrategy = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE);
		ActivityInstancePersister actInstPersistenceService = persistenceStrategy.getActivityInstancePersister();
		ProcessInstancePersister processInstancePersister = persistenceStrategy.getProcessInstancePersister();
		
		if (oldProcInst==null || !oldProcInst.getId().equals(token.getProcessInstanceId())){
			ProcessInstance procInst = processInstancePersister.find(ProcessInstance.class, token.getProcessInstanceId());
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);
		}
		if (oldActInst==null || !oldActInst.getId().equals(token.getElementInstanceId())){
			ActivityInstance actInst = actInstPersistenceService.find(ActivityInstance.class, token.getElementInstanceId());
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
		}
		
		//2、执行业务操作
		try{
			ActivityInstanceManager activityInstanceMgr = ctx.getEngineModule(ActivityInstanceManager.class, FpdlConstants.PROCESS_TYPE);
			ActivityInstance currentActivityInstance = session.getCurrentActivityInstance();
			int direction = activityInstanceMgr.tryCloseActivityInstance(session, currentActivityInstance,workflowElement);
			if (direction == ContinueDirection.WAITING_FOR_CLOSE) {
				return ContinueDirection.waitingForClose();// 继续等待；
			} 
			else if (direction==ContinueDirection.START_NEXT_AND_WAITING_FOR_CLOSE){
				//计算后续路由
				List<PObjectKey> nextPObjectKeys = determineNextPObjectKeys(session,token,workflowElement);
				
				ContinueDirection directionObj = ContinueDirection.startNextAndWaitingForClose();
				if (nextPObjectKeys.size()>0){
					directionObj.setNextProcessObjectKeys(nextPObjectKeys);
				}
				return directionObj;
			}
			else if (direction == ContinueDirection.CLOSE_ME) {

				//计算后续路由
				List<PObjectKey> nextPObjectKeys = determineNextPObjectKeys(session,token,workflowElement);
				
				ContinueDirection directionObj = ContinueDirection.closeMe();
				if (nextPObjectKeys.size()>0){
					directionObj.setNextProcessObjectKeys(nextPObjectKeys);
				}

				return directionObj;
			}
			else{
				//TODO 抛出异常
				throw new RuntimeException();
			}
		}finally{
			
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(oldProcInst);
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(oldActInst);
		}

	}
	
	
	public CancellationHandler getCancellationHandler(){
		return cancellationHandler;
	}
	
	public void abort(WorkflowSession session,Token token,Object workflowElement){
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
						((WorkItemImpl)wi).setState(WorkItemState.ABORTED);
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
						
						((ScheduleJobImpl)job).setState(ScheduleJobState.ABORTED);
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
