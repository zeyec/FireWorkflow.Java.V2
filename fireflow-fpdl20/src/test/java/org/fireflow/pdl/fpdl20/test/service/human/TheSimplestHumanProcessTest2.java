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
package org.fireflow.pdl.fpdl20.test.service.human;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.FireWorkflowJunitEnviroment;
import org.fireflow.engine.Order;
import org.fireflow.engine.WorkflowQuery;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.WorkflowSessionFactory;
import org.fireflow.engine.WorkflowStatement;
import org.fireflow.engine.entity.repository.ProcessDescriptorProperty;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceProperty;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.impl.Restrictions;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.AssignmentStrategy;
import org.fireflow.model.binding.impl.ParameterAssignmentImpl;
import org.fireflow.model.binding.impl.ResourceBindingImpl;
import org.fireflow.model.binding.impl.ResourceRefImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.data.impl.InputImpl;
import org.fireflow.model.data.impl.PropertyImpl;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.resourcedef.ResourceType;
import org.fireflow.model.resourcedef.impl.ResolverDefImpl;
import org.fireflow.model.resourcedef.impl.ResourceImpl;
import org.fireflow.model.servicedef.impl.OperationImpl;
import org.fireflow.model.servicedef.impl.ServiceImpl;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
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
 * 首先将流程发布到数据库，然后从数据库中load流程定义来运行
 * 
 * @author 非也
 * @version 2.0
 */
public class TheSimplestHumanProcessTest2 extends FireWorkflowJunitEnviroment {

	protected static final String processName = "TheSimplestHumanProcessTest";
	
	protected static final String bizId = "ThisIsAJunitTest";
	@Test
	public void testStartProcess(){
		final WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(runtimeContext,FireWorkflowSystem.getInstance());
		final WorkflowStatement stmt = session.createWorkflowStatement(FpdlConstants.PROCESS_TYPE);
		
		//1 首先上传流程定义
		transactionTemplate.execute(new TransactionCallback(){
			public Object doInTransaction(TransactionStatus arg0) {
				//1.1发布一条流程
				WorkflowProcess process = getWorkflowProcess();
				Map<ProcessDescriptorProperty,Object> props = new HashMap<ProcessDescriptorProperty,Object>();
				props.put(ProcessDescriptorProperty.PUBLISH_STATE, Boolean.TRUE);//true表示流程是发布状态
				try {
					stmt.uploadProcess(process, props);
				} catch (InvalidModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//1.2 上传一个流程，不发布
				props = new HashMap<ProcessDescriptorProperty,Object>();
				try {
					stmt.uploadProcess(process, props);
				} catch (InvalidModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		});
		
		//2 然后运行流程
		transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {


				
				//启动流程
				try {
					ProcessInstance processInstance = stmt.startProcess(processName, bizId, null);
					
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
	 * Start-->Activity(human service)-->End
	 * @return
	 */
	public WorkflowProcess createWorkflowProcess(){
		//构造流程
		WorkflowProcessImpl process = new WorkflowProcessImpl(processName);
		Duration du = new Duration(3,"DAY");
		process.setDuration(du);
		
		PropertyImpl property = new PropertyImpl(process,"x");//流程变量x
		property.setDataType("java.lang.Integer");
		property.setInitialValueAsString("1");
		process.getProperties().add(property);
		
		property = new PropertyImpl(process,"y");//流程变量x
		property.setDataType("java.lang.Integer");
		property.setInitialValueAsString("2");
		process.getProperties().add(property);
		
		property = new PropertyImpl(process,"z");//流程变量x
		property.setDataType("java.lang.Integer");
		property.setInitialValueAsString("3");
		process.getProperties().add(property);
		
		StartNodeImpl startNode = new StartNodeImpl(process,"Start");
		ActivityImpl activity = new ActivityImpl(process,"Activity1");
		activity.setDuration(du);
		EndNodeImpl endNode = new EndNodeImpl(process,"End");
		
		process.setEntry(startNode);
		process.getStartNodes().add(startNode);
		process.getActivities().add(activity);
		process.getEndNodes().add(endNode);
		
		TransitionImpl transition1 = new TransitionImpl(process,"start2activity");
		transition1.setFromNode(startNode);
		transition1.setToNode(activity);
		startNode.getLeavingTransitions().add(transition1);
		activity.getEnteringTransitions().add(transition1);
		
		TransitionImpl transition2 = new TransitionImpl(process,"activity2end");
		transition2.setFromNode(activity);
		transition2.setToNode(endNode);
		activity.getLeavingTransitions().add(transition2);
		endNode.getEnteringTransitions().add(transition2);
		
		process.getTransitions().add(transition1);
		process.getTransitions().add(transition2);
		
		//构造Human service	
		String opName = "xyz/Application.jsp";
		OperationImpl operation = new OperationImpl();
		operation.setOperationName(opName);
		
		ServiceImpl humanService = new ServiceImpl();
		humanService.setServiceType("Human");
		humanService.setName("Application");
		humanService.setDisplayName("申请");
		humanService.setOperation(operation);
		
		process.getLocalServices().add(humanService);

		
		//将service绑定到activity
		ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
		serviceBinding.setService(humanService);
		serviceBinding.setServiceId(humanService.getId());
		serviceBinding.setOperation(humanService.getOperation(opName));
		serviceBinding.setOperationName(opName);		

		activity.setServiceBinding(serviceBinding);
		
		//构造Resource以及ResourceBinding
		ResourceImpl resource = new ResourceImpl();
		resource.setName("Tester");
		resource.setDisplayName("测试用户");
		resource.setResourceType(ResourceType.CUSTOM);
		ResolverDefImpl resolver = new ResolverDefImpl();
		resolver.setBeanName("org.fireflow.pdl.fpdl20.test.service.human.CustomerResourceResolver");
		InputImpl input = new InputImpl();
		input.setName("flag");
		input.setDisplayName("标志");
		input.setDataType("java.lang.Integer");
		resolver.getParameters().add(input);
		resource.setResolver(resolver);
		
		process.getLocalResources().add(resource);
		
		//resourceBinding
		ResourceBindingImpl resourceBinding = new ResourceBindingImpl();
		resourceBinding.setAssignmentStrategy(AssignmentStrategy.ASSIGN_TO_ALL);
		resourceBinding.setDisplayName("审批科");
		
		activity.setResourceBinding(resourceBinding);
		
		//业务领导
		ResourceRefImpl resourceRef = new ResourceRefImpl();
		resourceRef.setResourceId("Tester");
		resourceRef.setResource(resource);		
		ParameterAssignmentImpl paramAssign = new ParameterAssignmentImpl();
		ExpressionImpl exp = new ExpressionImpl();
		exp.setLanguage("JEXL");
		exp.setBody("processVars.x");
		paramAssign.setFrom(exp);
		paramAssign.setTo("flag");
		resourceRef.getParameterAssignments().add(paramAssign);
		resourceBinding.getAdministrators().add(resourceRef);
		
		//操作者
		resourceRef = new ResourceRefImpl();
		resourceRef.setResourceId("Tester");
		resourceRef.setResource(resource);		
		paramAssign = new ParameterAssignmentImpl();
		exp = new ExpressionImpl();
		exp.setLanguage("JEXL");
		exp.setBody("processVars.y");
		paramAssign.setFrom(exp);
		paramAssign.setTo("flag");
		resourceRef.getParameterAssignments().add(paramAssign);
		resourceBinding.getPotentialOwners().add(resourceRef);
		
		//抄送人
		resourceRef = new ResourceRefImpl();
		resourceRef.setResourceId("Tester");
		resourceRef.setResource(resource);		
		paramAssign = new ParameterAssignmentImpl();
		exp = new ExpressionImpl();
		exp.setLanguage("JEXL");
		exp.setBody("processVars.z");
		paramAssign.setFrom(exp);
		paramAssign.setTo("flag");
		resourceRef.getParameterAssignments().add(paramAssign);
		resourceBinding.getReaders().add(resourceRef);
		
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
		Assert.assertEquals(ProcessInstanceState.RUNNING, procInst.getState());
		Assert.assertEquals(Boolean.FALSE, procInst.isSuspended());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getId(),procInst.getCreatorId());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getName(), procInst.getCreatorName());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getDeptId(), procInst.getCreatorDeptId());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getDeptName(),procInst.getCreatorDeptName());
		Assert.assertNotNull(procInst.getCreatedTime());
		Assert.assertNull(procInst.getEndTime());
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
		Assert.assertEquals(4, tokenList.size());
		
		Token procInstToken = tokenList.get(0);
		Assert.assertEquals(processName,procInstToken.getElementId() );
		Assert.assertEquals(processInstanceId,procInstToken.getElementInstanceId());
		Assert.assertEquals(processName,procInstToken.getProcessId());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE, procInstToken.getProcessType());
		Assert.assertEquals(new Integer(1), procInstToken.getVersion());
		Assert.assertEquals(TokenState.RUNNING, procInstToken.getState());
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
		Assert.assertNull(activityInstance.getEndTime());
		Assert.assertNotNull(activityInstance.getExpiredTime());
		Assert.assertNotNull( activityInstance.getTokenId());
		Assert.assertEquals(activity1Token.getId(), activityInstance.getTokenId());
		Assert.assertEquals(activity1Token.getElementId(), activityInstance.getNodeId());
		Assert.assertEquals(activity1Token.getElementInstanceId(), activityInstance.getId());
		Assert.assertNotNull(activityInstance.getScopeId());
		
		Assert.assertEquals(new Integer(1),activityInstance.getVersion());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE,activityInstance.getProcessType());
		Assert.assertEquals(procInst.getName(), activityInstance.getProcessName());
		Assert.assertEquals(procInst.getDisplayName(), activityInstance.getProcessDisplayName());
		
		//验证Activity1的WorkItem
		WorkflowQuery<WorkItem> q4WorkItem = session.createWorkflowQuery(WorkItem.class);
		q4WorkItem.add(Restrictions.eq(WorkItemProperty.ACTIVITY_INSTANCE_$_ACTIVITY_ID, processName+".Activity1"));
		List<WorkItem> workItemList = q4WorkItem.list();
		Assert.assertNotNull(workItemList);
		Assert.assertEquals(2,workItemList.size());
	}
}
