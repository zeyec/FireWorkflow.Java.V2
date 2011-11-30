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
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.ScheduleJobImpl;
import org.fireflow.engine.entity.runtime.impl.WorkItemImpl;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.ScheduleJobPersister;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.engine.modules.persistence.WorkItemPersister;
import org.fireflow.engine.modules.schedule.Scheduler;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.Transition;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.CancellationHandler;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.ExecuteResult;

/**
 * @author 非也
 * @version 2.0
 */
public abstract class AbsSynchronizerBehavior extends AbsNodeBehavior{
	protected CancellationHandler cancellationHandler = new SynchronizerCancellationHandler();
	
	/**
	 * 汇聚计算，这是fpdl1.0,和fpdl2.0本质区别的地方。
	 * @param session
	 * @param token
	 * @param thisNode
	 * @return
	 */
	protected boolean hasAlivePreviousNode(WorkflowSession session,Token token,Node thisNode){
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceStrategy = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE);
		TokenPersister tokenPersister = persistenceStrategy.getTokenPersister();
		
		List<Transition> enteringTransitions = thisNode.getEnteringTransitions();		
		if (enteringTransitions==null || enteringTransitions.size()==0){
			return false;
		}
		
		for (Transition transition:enteringTransitions){
			if (!transition.isLoop()){//排除循环的情况
				Node fromNode = transition.getFromNode();
				
				int aliveCount = tokenPersister.countAliveToken(token.getProcessInstanceId(), fromNode.getId(),token.getOperationContextName());
				if (aliveCount>0){
					return true;
				}
				
				boolean b = hasAlivePreviousNode(session,token,fromNode);
				
				if (b){
					return true;
				}
			}
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#execute(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ExecuteResult execute(WorkflowSession session, Token token,
			Object workflowElement) {
	
		ExecuteResult result = new ExecuteResult();
		result.setStatus(BusinessStatus.COMPLETED);
		return result;

	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#prepare(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public Boolean prepare(WorkflowSession session, Token token,
			Object workflowElement) {
		Node node = (Node)workflowElement;
//		int count = 0;
//		
//		//只有多于1条输入边时，才需要判断ActivityInstance是否已经创建，0条或者1条边的情况下，必然需要创建activityInstance。
//		if (node.getEnteringTransitions() != null
//				&& node.getEnteringTransitions().size() > 1) {
//			RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
//					.getRuntimeContext();
//			PersistenceService persistenceStrategy = ctx.getEngineModule(
//					PersistenceService.class, FpdlConstants.PROCESS_TYPE);
//			ActivityInstancePersister activityInstancePersister = persistenceStrategy
//					.getActivityInstancePersister();
//
//			count = activityInstancePersister.countAliveActivityInstance(token
//					.getProcessInstanceId(), node.getId());
//		}
//		if (count==0){
//			//创建环节实例
//			super.prepare(session, token, workflowElement);
//		}
//		else{
//			//TODO 将tokenId归并到ActivityInstance.tokenId字段
//		}
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		PersistenceService persistenceStrategy = ctx.getEngineModule(
				PersistenceService.class, FpdlConstants.PROCESS_TYPE);
		TokenPersister tokenPersister = persistenceStrategy.getTokenPersister();

		boolean multiEnteringTransitions = false;//表示是否有多条输入边
		if (node.getEnteringTransitions() != null
				&& node.getEnteringTransitions().size() > 1) {
			multiEnteringTransitions = true;
		}
		
		// 1、通过elementInstanceId判断是否已经被执行
		if (multiEnteringTransitions) {
			Token tokenFromDB = tokenPersister.find(Token.class, token.getId());
			if (tokenFromDB.getElementInstanceId() != null
					&& !tokenFromDB.getElementInstanceId().trim().equals("")) {
				// 说明已经执行过
				tokenFromDB.setState(TokenState.COMPLETED);
				tokenPersister.saveOrUpdate(tokenFromDB);
				return false;
			}
		}
		
		//2、判断汇聚是否完成
		boolean canBeFired = false;
		if (multiEnteringTransitions) {

			if (this.hasAlivePreviousNode(session, token, node)) {
				canBeFired = false;
			} else {
				canBeFired = true;
			}
		} else {// 只有一条输入边的synchronizer直接启动
			canBeFired = true;
		}

		//3、如果汇聚完成，则创建对应的ActivityInstance

		if (canBeFired){
			int stepNumber = token.getStepNumber();
			List<Token> siblings = null;
			if (multiEnteringTransitions){
				siblings = tokenPersister.findSiblings(token);
				if (siblings!=null){
					for (Token sibling : siblings){
						if (sibling.getStepNumber()>stepNumber){
							stepNumber = sibling.getStepNumber();
						}
					}
				}
			}
			
			ActivityInstanceManager activityInstanceMgr = ctx.getEngineModule(ActivityInstanceManager.class, FpdlConstants.PROCESS_TYPE);
			ActivityInstancePersister actInstPersistSvc = persistenceStrategy.getActivityInstancePersister();
			ProcessInstance processInstance = session.getCurrentProcessInstance();
			//1、创建并保存活动实例
			ActivityInstanceImpl activityInstance = (ActivityInstanceImpl)activityInstanceMgr.createActivityInstance(session, processInstance, workflowElement);
			
			activityInstance.setStepNumber(stepNumber);
			activityInstance.setTokenId(token.getId());
			actInstPersistSvc.saveOrUpdate(activityInstance);
			
			//2、设置session和token
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(activityInstance);
			token.setElementInstanceId(activityInstance.getId());	
			tokenPersister.saveOrUpdate(token);
			
			if (siblings!=null && siblings.size()>0){
				for (Token sibling : siblings){
					if (!sibling.getId().equals(token.getId())){
						sibling.setElementInstanceId(activityInstance.getId());
						sibling.setState(TokenState.COMPLETED);
						tokenPersister.saveOrUpdate(sibling);
					}
				}
			}
		}
		
		return canBeFired;
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#continueOn(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ContinueDirection continueOn(WorkflowSession session, Token token,
			Object workflowElement) {
		//检验currentActivityInstance和currentProcessInstance的一致性
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

		
		if (token.getState().getValue()<TokenState.DELIMITER.getValue() && 
				token.getState().getValue()!=TokenState.RUNNING.getValue()){
			return ContinueDirection.closeMe();
		}

		List<PObjectKey> nextPObjectKeys = determineNextPObjectKeys(session,token,workflowElement);
		
		ContinueDirection direction = ContinueDirection.closeMe();
		if (nextPObjectKeys.size()>0){
			direction.setNextProcessObjectKeys(nextPObjectKeys);
		}
		
		((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(oldProcInst);
		((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(oldActInst);
		return direction;
	}
	
	public CancellationHandler getCancellationHandler(){
		return this.cancellationHandler;
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
