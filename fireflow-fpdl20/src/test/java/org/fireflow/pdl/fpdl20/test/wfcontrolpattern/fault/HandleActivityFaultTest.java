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

import org.fireflow.FireWorkflowJunitEnviroment;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.WorkflowSessionFactory;
import org.fireflow.engine.WorkflowStatement;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
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
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.impl.CatchFaultDecoratorImpl;
import org.fireflow.pdl.fpdl20.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl20.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl20.process.impl.WorkflowProcessImpl;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * Activity1上的异常被捕获。
 * 
 * @author 非也
 * @version 2.0
 */
public class HandleActivityFaultTest extends FireWorkflowJunitEnviroment {
	protected static final String processId = "HandleActivityFaultTest";

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
					ProcessInstance processInstance = stmt.startProcess(process, null, null);
					
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
		this.assertResult(session);
	}
	
	/**
	 * Start-->Activity1-->End
	 *             |-->catchFault-->HandleFault
	 */
	public WorkflowProcess createWorkflowProcess(){
		//构造流程
		WorkflowProcessImpl process = new WorkflowProcessImpl(processId);
		
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
		
		property = new PropertyImpl(process,"m");//流程变量m
		property.setDataType("java.lang.Integer");
		property.setInitialValueAsString("0");
		process.getProperties().add(property);		
		
		StartNodeImpl startNode = new StartNodeImpl(process,"Start");
		ActivityImpl activity1 = new ActivityImpl(process,"Activity1");
		EndNodeImpl endNode = new EndNodeImpl(process,"End");
		
		process.setEntry(startNode);
		process.getStartNodes().add(startNode);
		process.getActivities().add(activity1);
		process.getEndNodes().add(endNode);
		
		TransitionImpl transition1 = new TransitionImpl(process,"start_activity1");
		transition1.setFromNode(startNode);
		transition1.setToNode(activity1);
		startNode.getLeavingTransitions().add(transition1);
		activity1.getEnteringTransitions().add(transition1);
		
		
		TransitionImpl transition2 = new TransitionImpl(process,"activity1_end");
		transition2.setFromNode(activity1);
		transition2.setToNode(endNode);
		activity1.getLeavingTransitions().add(transition2);
		endNode.getEnteringTransitions().add(transition2);
		
		process.getTransitions().add(transition1);
		process.getTransitions().add(transition2);
		
		
		//构造错误处理子流程
		StartNodeImpl catchFaultStart = new StartNodeImpl(process,"catchFaultStart");
		CatchFaultDecoratorImpl decorator = new CatchFaultDecoratorImpl();
		decorator.setAttachedToActivity(activity1);
		catchFaultStart.setDecorator(decorator);
		
		activity1.getAttachedStartNodes().add(catchFaultStart);
		
		ActivityImpl handleFaultAct = new ActivityImpl(process,"handleFaultAct");
		
		TransitionImpl t_catchFault_handleFault = new TransitionImpl(process,"t_catchFault_handleFault");
		t_catchFault_handleFault.setFromNode(catchFaultStart);
		t_catchFault_handleFault.setToNode(handleFaultAct);
		catchFaultStart.getLeavingTransitions().add(t_catchFault_handleFault);
		handleFaultAct.getEnteringTransitions().add(t_catchFault_handleFault);
		
		process.getStartNodes().add(catchFaultStart);
		process.getActivities().add(handleFaultAct);
		process.getTransitions().add(t_catchFault_handleFault);
		
		
		//构造java service	
		ServiceImpl javaService = new ServiceImpl();
		process.getLocalServices().add(javaService);
		javaService.setServiceType("Java");
		javaService.setName("MathOperationBean");
		javaService.setDisplayName("数学运算操作");
		
		//service的加法操作
		String opName = "add";
		IOSpecificationImpl ioSpec = new IOSpecificationImpl();
		InputImpl input = new InputImpl();
		input.setName("a");
		input.setDataType("int");
		ioSpec.addInput(input);
		
		input = new InputImpl();
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
		
		javaService.setOperation(operation);
	
		
		ServicePropGroup servicePropGroup = javaService.getServicePropGroup(ServicePropGroup.COMMON_PROPERTIES_GROUP);
		ServicePropImpl serviceProp = new ServicePropImpl();
		serviceProp.setName("JavaClassName");
		serviceProp.setDisplayName("java类名");
		//设置一个错误的java类名
		serviceProp.setValue("org.fireflow.pdl.fpdl20.test.service.javaexecutor.MathOperationBean_ERROR");
		servicePropGroup.getServiceProps().add(serviceProp);
		
		//将service绑定到activity1
		ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
		serviceBinding.setService(javaService);
		serviceBinding.setServiceId(javaService.getId());
		serviceBinding.setOperation(javaService.getOperation(opName));
		serviceBinding.setOperationName(opName);
		
		ExpressionImpl expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setBody("processVars.x");
		InputAssignmentImpl inputAssignment = new InputAssignmentImpl();
		inputAssignment.setFrom(expression);
		inputAssignment.setTo("inputs.a");
		
		serviceBinding.getInputAssignments().add(inputAssignment);
		
		expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setBody("processVars.y");
		inputAssignment = new InputAssignmentImpl();
		inputAssignment.setFrom(expression);
		inputAssignment.setTo("inputs.b");
		
		serviceBinding.getInputAssignments().add(inputAssignment);
		
		expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setBody("outputs.out");
		OutputAssignmentImpl outputAssignment = new OutputAssignmentImpl();
		outputAssignment.setFrom(expression);
		outputAssignment.setTo("processVars.z");
		serviceBinding.getOutputAssignments().add(outputAssignment);

		activity1.setServiceBinding(serviceBinding);

		return process;
	}
}
