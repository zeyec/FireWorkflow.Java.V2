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
package org.fireflow.pdl.fpdl20.test.service.callbackexecutor;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
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
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.impl.Restrictions;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.impl.InputAssignmentImpl;
import org.fireflow.model.binding.impl.OutputAssignmentImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.data.impl.InputImpl;
import org.fireflow.model.data.impl.OutputImpl;
import org.fireflow.model.data.impl.PropertyImpl;
import org.fireflow.model.servicedef.ServicePropGroup;
import org.fireflow.model.servicedef.impl.IOSpecificationImpl;
import org.fireflow.model.servicedef.impl.OperationImpl;
import org.fireflow.model.servicedef.impl.ServiceImpl;
import org.fireflow.model.servicedef.impl.ServicePropImpl;
import org.fireflow.pdl.fpdl20.io.Dom4JFPDLParser;
import org.fireflow.pdl.fpdl20.io.Dom4JFPDLSerializer;
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
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class CallbackExecutorTest extends FireWorkflowJunitEnviroment{
	protected static final String processName = "TheCallbackProcess1";
	protected static final String bizId = "TheJunitTest";

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
					Map<String,Object> vars = new HashMap<String ,Object>();
					vars.put("m",100);
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

	}
	

	
	@Test
	public void testCallback(){
		transactionTemplate.execute(new TransactionCallback(){
			public Object doInTransaction(TransactionStatus arg0) {
				MyInterfaceImpl interfaceImpl = new MyInterfaceImpl(runtimeContext);
				int result = interfaceImpl.callProcess(bizId, 10);
				assertEquals(11,result);
				return null;
			}
		});

		
		WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(runtimeContext,FireWorkflowSystem.getInstance());
		assertResult(session);
	}
	
	
	/**
	 *  start-->activity1-->end
	 *  
	 *  在activity1上面暴露一个对外调用接口。
	 * @return
	 */
	public WorkflowProcess createWorkflowProcess(){
		//构造流程
		WorkflowProcessImpl process = new WorkflowProcessImpl(processName);
		
		PropertyImpl property = new PropertyImpl(process,"x");//流程变量x
		property.setDataType("java.lang.Integer");
		property.setInitialValueAsString("1");
		process.getProperties().add(property);
		
		property = new PropertyImpl(process,"y");//流程变量x
		property.setDataType("java.lang.Integer");
		property.setInitialValueAsString("5");
		process.getProperties().add(property);
		
		property = new PropertyImpl(process,"z");//流程变量x
		property.setDataType("java.lang.Integer");
		property.setInitialValueAsString("0");
		process.getProperties().add(property);
		
		StartNodeImpl startNode = new StartNodeImpl(process,"Start");
		ActivityImpl activity = new ActivityImpl(process,"Activity1");
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
		
		//构造interface
		String opName = "callProcess";
		IOSpecificationImpl ioSpec = new IOSpecificationImpl();
	
		InputImpl input = new InputImpl();
		input.setName("b");
		input.setDataType("int");
		ioSpec.addInput(input);
		
		OutputImpl output = new OutputImpl();
		output.setName("out");
		output.setDataType("int");
		ioSpec.addOutput(output);
		
		
		OperationImpl operation = new OperationImpl();
		operation.setOperationName(opName);
		operation.setIOSpecification(ioSpec);
		
		ServiceImpl javaService = new ServiceImpl();
		
		process.getLocalServices().add(javaService);
		
		javaService.setServiceType("Interface");
		javaService.setName("The Expose Interface");
		javaService.setDisplayName("对外接口");
		javaService.setOperation(operation);
		
		ServicePropGroup servicePropGroup = javaService.getServicePropGroup(ServicePropGroup.COMMON_PROPERTIES_GROUP);
		ServicePropImpl serviceProp = new ServicePropImpl();
		serviceProp.setName("JavaClassName");
		serviceProp.setDisplayName("java类名");
		serviceProp.setValue("org.fireflow.pdl.fpdl20.callbackexecutor.MyInterface");
		servicePropGroup.getServiceProps().add(serviceProp);
		
		serviceProp = new ServicePropImpl();
		serviceProp.setName("CreateNewProcessInstance");
		serviceProp.setDisplayName("是否启动新的流程实例");
		serviceProp.setValue("false");
		servicePropGroup.getServiceProps().add(serviceProp);
		
		serviceProp = new ServicePropImpl();
		serviceProp.setName("correlation");
		serviceProp.setDisplayName("流程实例匹配规则");
		serviceProp.setValue("currrentProcessInstance.bizId=args.bizId ");
		servicePropGroup.getServiceProps().add(serviceProp);
		
		//将service绑定到activity
		ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
		serviceBinding.setService(javaService);
		serviceBinding.setServiceId(javaService.getId());
		serviceBinding.setOperation(javaService.getOperation(opName));
		serviceBinding.setOperationName(opName);
		
		//设置参数映射
	
		ExpressionImpl expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setBody("inputs.b");
		InputAssignmentImpl inputAssignment = new InputAssignmentImpl();
		inputAssignment.setFrom(expression);
		inputAssignment.setTo("processVars.y");
		
		serviceBinding.getInputAssignments().add(inputAssignment);
		
		expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setBody("processVars.x+processVars.y");
		OutputAssignmentImpl outputAssignment = new OutputAssignmentImpl();
		outputAssignment.setFrom(expression);
		outputAssignment.setTo("outputs.out");
		serviceBinding.getOutputAssignments().add(outputAssignment);

		activity.setServiceBinding(serviceBinding);
		
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
		Assert.assertEquals(6, tokenList.size());
		
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
		Assert.assertNotNull(activityInstance.getEndTime());
		Assert.assertNotNull( activityInstance.getTokenId());
		Assert.assertEquals(activity1Token.getId(), activityInstance.getTokenId());
		Assert.assertEquals(activity1Token.getElementId(), activityInstance.getNodeId());
		Assert.assertEquals(activity1Token.getElementInstanceId(), activityInstance.getId());
		Assert.assertNotNull(activityInstance.getScopeId());
		
		Assert.assertEquals(new Integer(1),activityInstance.getVersion());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE,activityInstance.getProcessType());
		Assert.assertEquals(procInst.getName(), activityInstance.getProcessName());
		Assert.assertEquals(procInst.getDisplayName(), activityInstance.getProcessDisplayName());
		
	}
}
