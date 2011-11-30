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
package org.fireflow.pdl.fpdl20.test.wfcontrolpattern.timmer;

import java.util.List;

import org.fireflow.FireWorkflowJunitEnviroment;
import org.fireflow.engine.Order;
import org.fireflow.engine.WorkflowQuery;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.WorkflowSessionFactory;
import org.fireflow.engine.WorkflowStatement;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceProperty;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.impl.Restrictions;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.schedule.Scheduler;
import org.fireflow.engine.service.TimerOperationName;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.servicedef.impl.OperationImpl;
import org.fireflow.model.servicedef.impl.ServiceImpl;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.impl.TimerStartDecoratorImpl;
import org.fireflow.pdl.fpdl20.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl20.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl20.process.impl.WorkflowProcessImpl;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenProperty;
import org.fireflow.pvm.kernel.TokenState;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * 边定时器，仅触发一次；触发后所依附的ActivityInstance结束。
 * 定时器的触发时间是一个绝对时间。
 * 
 * @author 非也
 * @version 2.0
 */
public class BoundaryTimerTriggerOnlyOnceTest2 extends FireWorkflowJunitEnviroment{

	protected static final String processName = "BoundaryTimerTriggerOnlyOnceTest2";
	protected static final String bizId = "ThisIsAJunitTest";

	@Test
	public void testStartProcess(){
		final WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(runtimeContext,FireWorkflowSystem.getInstance());
		final WorkflowStatement stmt = session.createWorkflowStatement(FpdlConstants.PROCESS_TYPE);
		transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				//构建流程定义
				WorkflowProcess process = getWorkflowProcess();
				
				//启动流程
				ProcessInstance processInstance = null;
				try {
					processInstance = stmt.startProcess(process, bizId, null);
					
					if (processInstance!=null){
						processInstanceId = processInstance.getId();
					}
					
					return processInstance;
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
				return null;
			}
			
		});
		
		//等待调度器结束
		Scheduler scheduler = runtimeContext.getEngineModule(Scheduler.class, FpdlConstants.PROCESS_TYPE);
		boolean hasJobInSchedule = scheduler.hasJobInSchedule(runtimeContext);
		System.out.println();
		while(hasJobInSchedule){
			System.out.print("...");
			try {
				Thread.currentThread().sleep(3*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hasJobInSchedule = scheduler.hasJobInSchedule(runtimeContext);
		}
		
		
		assertResult(session);
	}
	/**
	 * Start-->Activity(with timer start)-->End
	 *           |-->TimerHandler
	 * @return
	 */
	public WorkflowProcess createWorkflowProcess(){
		//1、构造主干流程
		WorkflowProcessImpl process = new WorkflowProcessImpl(processName);
		process.setDuration(new Duration(5,Duration.MINUTE));
		
		StartNodeImpl startNode = new StartNodeImpl(process,"Start");
		ActivityImpl activity1 = new ActivityImpl(process,"Activity1");
		activity1.setDuration(new Duration(6,Duration.DAY));
		EndNodeImpl endNode = new EndNodeImpl(process,"End");
		
		process.setEntry(startNode);
		process.getStartNodes().add(startNode);
		process.getActivities().add(activity1);
		process.getEndNodes().add(endNode);
		
		TransitionImpl transition1 = new TransitionImpl(process,"start2activity");
		transition1.setFromNode(startNode);
		transition1.setToNode(activity1);
		startNode.getLeavingTransitions().add(transition1);
		activity1.getEnteringTransitions().add(transition1);
		
		TransitionImpl transition2 = new TransitionImpl(process,"activity2end");
		transition2.setFromNode(activity1);
		transition2.setToNode(endNode);
		activity1.getLeavingTransitions().add(transition2);
		endNode.getEnteringTransitions().add(transition2);
		
		process.getTransitions().add(transition1);
		process.getTransitions().add(transition2);
		
		//2、构造Human service	
		String opName = "xyz/Application.jsp";
		OperationImpl operation = new OperationImpl();
		operation.setOperationName(opName);
		
		ServiceImpl humanService = new ServiceImpl();
		process.getLocalServices().add(humanService);
		humanService.setServiceType("Human");
		humanService.setName("Application");
		humanService.setDisplayName("申请");
		humanService.setOperation(operation);
		
		//将service绑定到activity1
		ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
		serviceBinding.setService(humanService);
		serviceBinding.setServiceId(humanService.getId());
		serviceBinding.setOperation(humanService.getOperation(opName));
		serviceBinding.setOperationName(opName);		

		activity1.setServiceBinding(serviceBinding);
		
		//3、构造一个定时器节点和相应的handler
		StartNodeImpl timerStartImpl = new StartNodeImpl(process,"timerStart");
		TimerStartDecoratorImpl timerStartDecorator = new TimerStartDecoratorImpl();
		timerStartDecorator.setCancelAttachedToActivity(true);
		timerStartDecorator.setTimerOperationName(TimerOperationName.TRIGGERED_ONLY_ONCE);
		
		ExpressionImpl expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setDataType("java.util.Date");
		expression.setBody("dateTimeUtil.dateAfter(currentActivityInstance.startedTime,1,'mi')");
		
		timerStartDecorator.setStartTimeExpression(expression);
		timerStartDecorator.setAttachedToActivity(activity1);
		
		timerStartImpl.setDecorator(timerStartDecorator);
		
		activity1.getAttachedStartNodes().add(timerStartImpl);
		
		ActivityImpl timerHandler = new ActivityImpl(process,"timerHandler");
		
		TransitionImpl t_timerStart_timerHandler = new TransitionImpl(process ,"t_timerStart_timerHandler");
		
		t_timerStart_timerHandler.setFromNode(timerStartImpl);
		t_timerStart_timerHandler.setToNode(timerHandler);
		timerStartImpl.getLeavingTransitions().add(t_timerStart_timerHandler);
		timerHandler.getEnteringTransitions().add(t_timerStart_timerHandler);
		
		process.getStartNodes().add(timerStartImpl);
		process.getActivities().add(timerHandler);
		process.getTransitions().add(t_timerStart_timerHandler);
		
		
		return process;
	}
	
	public void assertResult(WorkflowSession session){
		super.assertResult(session);
		
		//验证ProcessInstance信息
		WorkflowQuery<ProcessInstance> q4ProcInst = session.createWorkflowQuery(ProcessInstance.class, FpdlConstants.PROCESS_TYPE);
		ProcessInstance procInst = q4ProcInst.get(processInstanceId);
		Assert.assertNotNull(procInst);
		
		Assert.assertEquals(bizId,procInst.getBizId());
		Assert.assertEquals(processName, procInst.getProcessId());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE, procInst.getProcessType());
		Assert.assertEquals(new Integer(1), procInst.getVersion());
		Assert.assertEquals(processName, procInst.getName());//name 为空的情况下默认等于processId,
		Assert.assertEquals(processName, procInst.getDisplayName());//displayName为空的情况下默认等于name
		Assert.assertEquals(ProcessInstanceState.COMPLETED, procInst.getState());
		Assert.assertEquals(Boolean.FALSE, procInst.isSuspended());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getId(),procInst.getCreatorId());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getName(), procInst.getCreatorName());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getDeptId(), procInst.getCreatorDeptId());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getDeptName(),procInst.getCreatorDeptName());
		Assert.assertNotNull(procInst.getCreatedTime());
		Assert.assertNotNull(procInst.getExpiredTime());
		Assert.assertNull(procInst.getParentActivityInstanceId());
		Assert.assertNull(procInst.getParentProcessInstanceId());
		Assert.assertNull(procInst.getParentScopeId());
		Assert.assertNull(procInst.getNote());
		
		//验证Token信息
		WorkflowQuery<Token> q4Token = session.createWorkflowQuery(Token.class, FpdlConstants.PROCESS_TYPE);
		q4Token.add(Restrictions.eq(TokenProperty.PROCESS_INSTANCE_ID, processInstanceId))
				.addOrder(Order.asc(TokenProperty.STEP_NUMBER));
		
		List<Token> tokenList = q4Token.list();
		Assert.assertNotNull(tokenList);
		Assert.assertEquals(7, tokenList.size());
		
		Token procInstToken = tokenList.get(0);
		Assert.assertEquals(processName,procInstToken.getElementId() );
		Assert.assertEquals(processInstanceId,procInstToken.getElementInstanceId());
		Assert.assertEquals(processName,procInstToken.getProcessId());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE, procInstToken.getProcessType());
		Assert.assertEquals(new Integer(1), procInstToken.getVersion());
		Assert.assertEquals(TokenState.COMPLETED, procInstToken.getState());
		Assert.assertNull(procInstToken.getParentTokenId());
		Assert.assertTrue(procInstToken.isBusinessPermitted());
		Assert.assertEquals(procInst.getTokenId(), procInstToken.getId());
		
		Token startNodeToken = tokenList.get(1);
		Assert.assertEquals(processName, startNodeToken.getProcessId());
		Assert.assertEquals(new Integer(1), startNodeToken.getVersion());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE, startNodeToken.getProcessType());
		Assert.assertEquals(procInstToken.getId(), startNodeToken.getParentTokenId());
		Assert.assertTrue(startNodeToken.isBusinessPermitted());
		
		Token activity1Token = tokenList.get(3);
		
		
		//验证ActivityInstance信息
		WorkflowQuery<ActivityInstance> q4ActInst = session.createWorkflowQuery(ActivityInstance.class, FpdlConstants.PROCESS_TYPE);
		q4ActInst.add(Restrictions.eq(ActivityInstanceProperty.PROCESS_INSTANCE_ID, processInstanceId))
				.add(Restrictions.eq(ActivityInstanceProperty.NODE_ID, processName+".Activity1"));
		List<ActivityInstance> actInstList = q4ActInst.list();
		Assert.assertNotNull(actInstList);
		Assert.assertEquals(1, actInstList.size());
		ActivityInstance activityInstance = actInstList.get(0);
		Assert.assertEquals(bizId, activityInstance.getBizId());
		Assert.assertEquals("Activity1", activityInstance.getName());
		Assert.assertEquals("Activity1", activityInstance.getDisplayName());
		Assert.assertEquals(processInstanceId, activityInstance.getParentScopeId());
		Assert.assertNotNull(activityInstance.getCreatedTime());
		Assert.assertNotNull(activityInstance.getStartedTime());
		Assert.assertNotNull(activityInstance.getExpiredTime());
		Assert.assertNotNull( activityInstance.getTokenId());
		Assert.assertEquals(activity1Token.getId(), activityInstance.getTokenId());
		Assert.assertEquals(activity1Token.getElementId(), activityInstance.getNodeId());
		Assert.assertEquals(activity1Token.getElementInstanceId(), activityInstance.getId());
		Assert.assertNotNull(activityInstance.getScopeId());
		Assert.assertEquals(ActivityInstanceState.CANCELLED, activityInstance.getState());
		Assert.assertEquals(new Integer(1),activityInstance.getVersion());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE,activityInstance.getProcessType());
		Assert.assertEquals(procInst.getName(), activityInstance.getProcessName());
		Assert.assertEquals(procInst.getDisplayName(), activityInstance.getProcessDisplayName());
		
		q4ActInst.reset();
		q4ActInst = session.createWorkflowQuery(ActivityInstance.class, FpdlConstants.PROCESS_TYPE);
		q4ActInst.add(Restrictions.eq(ActivityInstanceProperty.PROCESS_INSTANCE_ID, processInstanceId))
				.add(Restrictions.eq(ActivityInstanceProperty.NODE_ID, processName+".timerStart"));
		ActivityInstance timerStartActInst = q4ActInst.unique();
		Assert.assertNotNull(timerStartActInst);
		Assert.assertEquals(ActivityInstanceState.CANCELLED, timerStartActInst.getState());//边上的时间节点由主ActivityInstance来终结
		
		q4ActInst.reset();
		q4ActInst = session.createWorkflowQuery(ActivityInstance.class, FpdlConstants.PROCESS_TYPE);
		q4ActInst.add(Restrictions.eq(ActivityInstanceProperty.PROCESS_INSTANCE_ID, processInstanceId))
				.add(Restrictions.eq(ActivityInstanceProperty.NODE_ID, processName+".timerHandler"));
		ActivityInstance timerHandlerActInst = q4ActInst.unique();
		Assert.assertNotNull(timerHandlerActInst);
		Assert.assertEquals(ActivityInstanceState.COMPLETED, timerHandlerActInst.getState());
	}
}
