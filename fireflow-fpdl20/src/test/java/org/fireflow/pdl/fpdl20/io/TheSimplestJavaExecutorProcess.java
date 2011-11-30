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
package org.fireflow.pdl.fpdl20.io;

import java.io.File;
import java.io.FileOutputStream;

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
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl20.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl20.process.impl.WorkflowProcessImpl;
import org.junit.Test;

/**
 * 本测试的WorkflowProcess 来自test.service.javaexecutor.TheSimplestJavaExecutorTest
 * 
 * @author 非也
 * @version 2.0
 */
public class TheSimplestJavaExecutorProcess {
	protected static final String processId = "TheSimplestJavaExecutorProcess";
	@Test
	public void testSerialize() {
		try{
			File f = new File("src/test/java/org/fireflow/pdl/fpdl20/io/TheSimplestJavaExecutorProcess.xml");
			if (f.exists()){
				f.delete();
			}
			f.createNewFile();
			
			FileOutputStream out = new FileOutputStream(f);
			Dom4JFPDLSerializer ser = new Dom4JFPDLSerializer();
			ser.serialize(this.getWorkflowProcess(), out);
			out.flush();
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	/**
	 * Start-->Activity1-->Activity2-->End
	 */
	public WorkflowProcess getWorkflowProcess(){
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
		ActivityImpl activity2 = new ActivityImpl(process,"Activity2");
		EndNodeImpl endNode = new EndNodeImpl(process,"End");
		
		process.setEntry(startNode);
		process.getStartNodes().add(startNode);
		process.getActivities().add(activity1);
		process.getActivities().add(activity2);
		process.getEndNodes().add(endNode);
		
		TransitionImpl transition1 = new TransitionImpl(process,"start_activity1");
		transition1.setFromNode(startNode);
		transition1.setToNode(activity1);
		startNode.getLeavingTransitions().add(transition1);
		activity1.getEnteringTransitions().add(transition1);
		
		TransitionImpl t_act1_act2 = new TransitionImpl(process,"activity1_activity2");
		t_act1_act2.setFromNode(activity1);
		t_act1_act2.setToNode(activity2);
		activity1.getLeavingTransitions().add(t_act1_act2);
		activity2.getEnteringTransitions().add(t_act1_act2);
		
		TransitionImpl transition2 = new TransitionImpl(process,"activity2_end");
		transition2.setFromNode(activity2);
		transition2.setToNode(endNode);
		activity2.getLeavingTransitions().add(transition2);
		endNode.getEnteringTransitions().add(transition2);
		
		process.getTransitions().add(transition1);
		process.getTransitions().add(transition2);
		process.getTransitions().add(t_act1_act2);
		
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
		serviceProp.setValue("org.fireflow.pdl.fpdl20.test.service.javaexecutor.MathOperationBean");
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
		
		
		//构造java service	2
		javaService = new ServiceImpl();
		process.getLocalServices().add(javaService);
		
		javaService.setServiceType("Java");
		javaService.setName("MathOperationBean");
		javaService.setDisplayName("数学运算操作");
		
		//乘法操作 
		opName = "multiply";
		ioSpec = new IOSpecificationImpl();
		input = new InputImpl();
		input.setName("a");
		input.setDataType("int");
		ioSpec.addInput(input);
		
		input = new InputImpl();
		input.setName("b");
		input.setDataType("int");
		ioSpec.addInput(input);
		
		output = new OutputImpl();
		output.setName("out");
		output.setDataType("int");
		ioSpec.addOutput(output);
		
		
		operation = new OperationImpl();
		operation.setOperationName(opName);
		operation.setIOSpecification(ioSpec);
		
		javaService.setOperation(operation);
		
		servicePropGroup = javaService.getServicePropGroup(ServicePropGroup.COMMON_PROPERTIES_GROUP);
		serviceProp = new ServicePropImpl();
		serviceProp.setName("JavaBeanName");
		serviceProp.setDisplayName("Java Bean 名称");
		serviceProp.setValue("#MathOperationBean");//井号开头表示从容器中查找该bean
		servicePropGroup.getServiceProps().add(serviceProp);
		
		//将service2绑定到activity2
		serviceBinding = new ServiceBindingImpl();
		serviceBinding.setService(javaService);
		serviceBinding.setServiceId(javaService.getId());
		serviceBinding.setOperation(javaService.getOperation(opName));
		serviceBinding.setOperationName(opName);
		
		expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setBody("processVars.y");
		inputAssignment = new InputAssignmentImpl();
		inputAssignment.setFrom(expression);
		inputAssignment.setTo("inputs.a");
		
		serviceBinding.getInputAssignments().add(inputAssignment);
		
		expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setBody("processVars.z");
		inputAssignment = new InputAssignmentImpl();
		inputAssignment.setFrom(expression);
		inputAssignment.setTo("inputs.b");
		
		serviceBinding.getInputAssignments().add(inputAssignment);
		
		expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setBody("outputs.out");
		outputAssignment = new OutputAssignmentImpl();
		outputAssignment.setFrom(expression);
		outputAssignment.setTo("processVars.m");
		serviceBinding.getOutputAssignments().add(outputAssignment);

		activity2.setServiceBinding(serviceBinding);		
		return process;
	}
}
