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
package org.fireflow.pdl.fpdl20.test.wfcontrolpattern.router;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.FireWorkflowJunitEnviroment;
import org.fireflow.engine.Order;
import org.fireflow.engine.WorkflowQuery;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.WorkflowSessionFactory;
import org.fireflow.engine.WorkflowStatement;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceProperty;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.VariableProperty;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.impl.Restrictions;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.misc.Duration;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl20.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.RouterImpl;
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
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class TheConditionSplitRouter extends FireWorkflowJunitEnviroment {
	protected static final String processName = "TheConditionSplitRouterProcess";
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
				try {
					Map<String ,Object> vars = new HashMap<String,Object>();
					vars.put("x", new Integer(11));
					ProcessInstance processInstance = stmt.startProcess(process, bizId, vars);
					
					if (processInstance!=null){
						processInstanceId = processInstance.getId();
					}
					
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
		
		assertResult(session);
	}
	/**
	 * Start-->Router-condition1->Activity1-->End1
	 *            |--condition2-->Activity2-->End2
	 */
	public WorkflowProcess createWorkflowProcess(){
		WorkflowProcessImpl process = new WorkflowProcessImpl(processName);
		process.setDuration(new Duration(5,Duration.MINUTE));
		
		StartNodeImpl startNode = new StartNodeImpl(process,"Start");
		RouterImpl router = new RouterImpl(process,"Router");
		
		ActivityImpl activity1 = new ActivityImpl(process,"Activity1");
		activity1.setDuration(new Duration(6,Duration.DAY));
		
		ActivityImpl activity2 = new ActivityImpl(process,"Activity2");

		EndNodeImpl endNode1 = new EndNodeImpl(process,"End1");
		EndNodeImpl endNode2 = new EndNodeImpl(process,"End2");
		
		process.setEntry(startNode);
		process.getStartNodes().add(startNode);
		process.getRouters().add(router);
		process.getActivities().add(activity1);
		process.getActivities().add(activity2);
		process.getEndNodes().add(endNode1);
		process.getEndNodes().add(endNode2);
		
		TransitionImpl transition1 = new TransitionImpl(process,"start_router");
		transition1.setFromNode(startNode);
		transition1.setToNode(router);
		startNode.getLeavingTransitions().add(transition1);
		router.getEnteringTransitions().add(transition1);
		
		TransitionImpl transition2 = new TransitionImpl(process,"router_activity1");
		transition2.setFromNode(router);
		transition2.setToNode(activity1);
		router.getLeavingTransitions().add(transition2);
		activity1.getEnteringTransitions().add(transition2);
		transition2.setCondition(new ExpressionImpl("JEXL","processVars.x>10"));
		
		TransitionImpl transition3 = new TransitionImpl(process,"router_activity2");
		transition3.setFromNode(router);
		transition3.setToNode(activity2);
		router.getLeavingTransitions().add(transition3);
		activity2.getEnteringTransitions().add(transition3);
		transition3.setCondition(new ExpressionImpl("JEXL","processVars.x<=10"));
		
		TransitionImpl transition4 = new TransitionImpl(process,"activity1_end1");
		transition4.setFromNode(activity1);
		transition4.setToNode(endNode1);
		activity1.getLeavingTransitions().add(transition4);
		endNode1.getEnteringTransitions().add(transition4);
		
		TransitionImpl transition5 = new TransitionImpl(process,"activity2_end2");
		transition5.setFromNode(activity2);
		transition5.setToNode(endNode2);
		activity2.getLeavingTransitions().add(transition5);
		endNode2.getEnteringTransitions().add(transition5);
		
		process.getTransitions().add(transition1);
		process.getTransitions().add(transition2);
		process.getTransitions().add(transition3);
		process.getTransitions().add(transition4);
		process.getTransitions().add(transition5);
		
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
		Assert.assertNotNull(procInst.getEndTime());
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
		Assert.assertEquals(8, tokenList.size());
		
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
		Assert.assertNotNull(activityInstance.getEndTime());
		Assert.assertNotNull(activityInstance.getExpiredTime());
		Assert.assertNotNull( activityInstance.getTokenId());
		Assert.assertNotNull(activityInstance.getTokenId());
		Assert.assertNotNull(activityInstance.getScopeId());
		
		Assert.assertEquals(new Integer(1),activityInstance.getVersion());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE,activityInstance.getProcessType());
		Assert.assertEquals(procInst.getName(), activityInstance.getProcessName());
		Assert.assertEquals(procInst.getDisplayName(), activityInstance.getProcessDisplayName());
		
		//Activity2的实例应该为空
		q4ActInst.reset();
		q4ActInst.add(Restrictions.eq(ActivityInstanceProperty.PROCESS_INSTANCE_ID, processInstanceId))
		.add(Restrictions.eq(ActivityInstanceProperty.NODE_ID, processName+".Activity2"));
		actInstList = q4ActInst.list();
		Assert.assertTrue(actInstList==null || actInstList.size()==0);
		
		
		//检查流程变量
		WorkflowQuery<Variable> q4Variable = session.createWorkflowQuery(Variable.class);
		q4Variable.add(Restrictions.eq(VariableProperty.SCOPE_ID, processInstanceId));
		List<Variable> vars = q4Variable.list();
		Assert.assertNotNull(vars);
		Assert.assertEquals(1, vars.size());
		
	}	
}
