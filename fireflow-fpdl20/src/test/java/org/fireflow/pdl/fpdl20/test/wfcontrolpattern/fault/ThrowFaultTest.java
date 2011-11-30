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
package org.fireflow.pdl.fpdl20.test.wfcontrolpattern.fault;

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
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.impl.Restrictions;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.servicedef.impl.OperationImpl;
import org.fireflow.model.servicedef.impl.ServiceImpl;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.decorator.endnode.impl.ThrowFaultDecoratorImpl;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.impl.CatchCompensationDecoratorImpl;
import org.fireflow.pdl.fpdl20.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl20.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.RouterImpl;
import org.fireflow.pdl.fpdl20.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl20.process.impl.WorkflowProcessImpl;
import org.fireflow.pvm.kernel.KernelException;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenProperty;
import org.fireflow.pvm.kernel.TokenState;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * Fault未被处理，将抛出异常。
 * 
 * @author 非也
 * @version 2.0
 */
public class ThrowFaultTest  extends FireWorkflowJunitEnviroment {
	protected static final String processName = "ThrowFaultTest";
	protected static final String bizId = "ThisIsAJunitTest";

	@Test
	public void testStartProcess(){
		final WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(runtimeContext,FireWorkflowSystem.getInstance());
		final WorkflowStatement stmt = session.createWorkflowStatement(FpdlConstants.PROCESS_TYPE);
		String result = (String)transactionTemplate.execute(new TransactionCallback(){
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
				catch(KernelException e){
					e.printStackTrace();
					return "EXCEPTION";
				}
				return "OK";
			}
			
		});
		Assert.assertEquals("EXCEPTION", result);
	}
	
	/**
	 * Start-->Router-->Activity1(with catch compenstion)-->End(throw fault)
	 *       	  |         |-->HandleCompensation
	 *            |-->Activity2-->End2
	 */
	public WorkflowProcess createWorkflowProcess(){
		WorkflowProcessImpl process = new WorkflowProcessImpl(processName);
		
		StartNodeImpl startNode = new StartNodeImpl(process,"Start");
		
		RouterImpl router1 = new RouterImpl(process,"Router");
		
		//////////////////////////////////////////////////////////////
		//////////  Activity1 及其 异常处理分支，以及EndNode1////////////
		//////////////////////////////////////////////////////////////
		ActivityImpl activity1 = new ActivityImpl(process,"Activity1");
		
		//补偿捕获节点
		StartNodeImpl catchCompensationNode = new StartNodeImpl(process,"CatchCompensation");
		CatchCompensationDecoratorImpl catchCompensationDecorator = new CatchCompensationDecoratorImpl();
		catchCompensationDecorator.setAttachedToActivity(activity1);
		catchCompensationNode.setDecorator(catchCompensationDecorator);
		
		activity1.getAttachedStartNodes().add(catchCompensationNode);
		
		ActivityImpl handleCompensationNode = new ActivityImpl(process,"HandleCompensation");
		
		TransitionImpl transition0 = new TransitionImpl(process,"catchCompensation2HandleCompensation");
		transition0.setFromNode(catchCompensationNode);
		transition0.setToNode(handleCompensationNode);
		catchCompensationNode.getLeavingTransitions().add(transition0);
		handleCompensationNode.getEnteringTransitions().add(transition0);
		
		
		EndNodeImpl endNode1 = new EndNodeImpl(process,"End1");
		ThrowFaultDecoratorImpl decorator = new ThrowFaultDecoratorImpl();
		endNode1.setDecorator(decorator);
		
		//////////////////////////////////////////////////////////////
		//////////  Activity1以及EndNode2                 ////////////
		//////////////////////////////////////////////////////////////			
		ActivityImpl activity2 = new ActivityImpl(process,"Activity2");
		//构造Human service	
		String opName = "xyz/Application.jsp";
		OperationImpl operation = new OperationImpl();
		operation.setOperationName(opName);
		
		ServiceImpl humanService = new ServiceImpl();
		process.getLocalServices().add(humanService);
		humanService.setServiceType("Human");
		humanService.setName("Application");
		humanService.setDisplayName("申请");
		humanService.setOperation(operation);
		
		//将service绑定到activity
		ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
		serviceBinding.setService(humanService);
		serviceBinding.setServiceId(humanService.getId());
		serviceBinding.setOperation(humanService.getOperation(opName));
		serviceBinding.setOperationName(opName);		

		activity2.setServiceBinding(serviceBinding);
		
		EndNodeImpl endNode2 = new EndNodeImpl(process,"End2");
		
		
		////////////////////////////////////////////////
		/////////   转移   ///////////////////////////////
		////////////////////////////////////////////////
		TransitionImpl t_start_router = new TransitionImpl(process,"start_router1");
		t_start_router.setFromNode(startNode);
		t_start_router.setToNode(router1);
		startNode.getLeavingTransitions().add(t_start_router);
		router1.getEnteringTransitions().add(t_start_router);
		
		TransitionImpl t_router1_activity1 = new TransitionImpl(process,"router1_activity1");
		t_router1_activity1.setFromNode(router1);
		t_router1_activity1.setToNode(activity1);
		router1.getLeavingTransitions().add(t_router1_activity1);
		activity1.getEnteringTransitions().add(t_router1_activity1);
		
		TransitionImpl t_activity1_end1 = new TransitionImpl(process,"activity1_end1");
		t_activity1_end1.setFromNode(activity1);
		t_activity1_end1.setToNode(endNode1);
		activity1.getLeavingTransitions().add(t_activity1_end1);
		endNode1.getEnteringTransitions().add(t_activity1_end1);
		
		TransitionImpl t_router1_activity2 = new TransitionImpl(process,"router1_activity2");
		t_router1_activity2.setFromNode(router1);
		t_router1_activity2.setToNode(activity2);
		router1.getLeavingTransitions().add(t_router1_activity2);
		activity2.getEnteringTransitions().add(t_router1_activity2);		
		
		TransitionImpl t_activity2_end2 = new TransitionImpl(process,"activity2_end2");
		t_activity2_end2.setFromNode(activity1);
		t_activity2_end2.setToNode(endNode1);
		activity2.getLeavingTransitions().add(t_activity2_end2);
		endNode2.getEnteringTransitions().add(t_activity2_end2);
		
		process.setEntry(startNode);
		process.getStartNodes().add(startNode);
		process.getRouters().add(router1);
		process.getActivities().add(activity1);
		process.getActivities().add(activity2);
		process.getEndNodes().add(endNode1);
		process.getEndNodes().add(endNode2);
		process.getStartNodes().add(catchCompensationNode);
		process.getActivities().add(handleCompensationNode);
		
		process.getTransitions().add(transition0);
		process.getTransitions().add(t_start_router);
		process.getTransitions().add(t_router1_activity1);
		process.getTransitions().add(t_activity1_end1);
		process.getTransitions().add(t_router1_activity2);
		process.getTransitions().add(t_activity2_end2);
		return process;
	}
	
	public void assertResult(WorkflowSession session){
		//***************************************************************/
		//*********下面的断言尚没有针对该用例进行修正 **********************/
		/*
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
		Assert.assertEquals(ProcessInstanceState.ABORTED, procInst.getState());
		Assert.assertEquals(Boolean.FALSE, procInst.isSuspended());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getId(),procInst.getCreatorId());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getName(), procInst.getCreatorName());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getDeptId(), procInst.getCreatorDeptId());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getDeptName(),procInst.getCreatorDeptName());
		Assert.assertNotNull(procInst.getCreatedTime());
		Assert.assertNotNull(procInst.getEndTime());
		Assert.assertNull(procInst.getExpiredTime());
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
		Assert.assertEquals(10, tokenList.size());
		
		Token procInstToken = tokenList.get(0);
		Assert.assertEquals(processName,procInstToken.getElementId() );
		Assert.assertEquals(processInstanceId,procInstToken.getElementInstanceId());
		Assert.assertEquals(processName,procInstToken.getProcessId());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE, procInstToken.getProcessType());
		Assert.assertEquals(new Integer(1), procInstToken.getVersion());
		Assert.assertEquals(TokenState.ABORTED, procInstToken.getState());
		Assert.assertNull(procInstToken.getParentTokenId());
		Assert.assertTrue(procInstToken.isBusinessPermitted());
		Assert.assertEquals(procInst.getTokenId(), procInstToken.getId());
		
		Token startNodeToken = tokenList.get(1);
		Assert.assertEquals(processName, startNodeToken.getProcessId());
		Assert.assertEquals(new Integer(1), startNodeToken.getVersion());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE, startNodeToken.getProcessType());
		Assert.assertEquals(procInstToken.getId(), startNodeToken.getParentTokenId());
		Assert.assertTrue(startNodeToken.isBusinessPermitted());
		

		//检验fromToken的有效性
		for (Token t:tokenList){
			if (t!=procInstToken){
				Assert.assertNotNull(t.getFromToken());
			}
		}
		
		//验证ActivityInstance信息
		WorkflowQuery<ActivityInstance> q4ActInst = session.createWorkflowQuery(ActivityInstance.class, FpdlConstants.PROCESS_TYPE);
		q4ActInst.add(Restrictions.eq(ActivityInstanceProperty.PROCESS_INSTANCE_ID, processInstanceId))
				.add(Restrictions.eq(ActivityInstanceProperty.ACTIVITY_ID, processName+".Activity1"));
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
		Assert.assertNotNull(activityInstance.getEndTime());
		Assert.assertNull(activityInstance.getExpiredTime());
		Assert.assertNotNull( activityInstance.getTokenId());
		Assert.assertNotNull(activityInstance.getScopeId());
		//由于Activity1上的compensationCode与endnode1 中的compensationCode不匹配，Activity1实际上没有被补偿
		Assert.assertEquals(ActivityInstanceState.COMPLETED, activityInstance.getState());
		
		Assert.assertEquals(new Integer(1),activityInstance.getVersion());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE,activityInstance.getProcessType());
		Assert.assertEquals(procInst.getName(), activityInstance.getProcessName());
		Assert.assertEquals(procInst.getDisplayName(), activityInstance.getProcessDisplayName());
		
		q4ActInst.reset();
		q4ActInst.add(Restrictions.eq(ActivityInstanceProperty.PROCESS_INSTANCE_ID, processInstanceId))
		.add(Restrictions.eq(ActivityInstanceProperty.ACTIVITY_ID, processName+".Activity2"));
		ActivityInstance actInst2 = q4ActInst.unique();
		Assert.assertEquals(ActivityInstanceState.ABORTED, actInst2.getState());
		
		WorkflowQuery<WorkItem> q4WorkItem = session.createWorkflowQuery(WorkItem.class);
		q4WorkItem.add(Restrictions.eq(WorkItemProperty.ACTIVITY_INSTANCE_$_PROCESSINSTANCE_ID, processInstanceId))
		.add(Restrictions.eq(WorkItemProperty.ACTIVITY_INSTANCE_$_ACTIVITY_ID, processName+".Activity2"));
		WorkItem wi = q4WorkItem.unique();
		Assert.assertNotNull(wi);
		Assert.assertEquals(WorkItemState.ABORTED, wi.getState());
		*/
	}	
}
