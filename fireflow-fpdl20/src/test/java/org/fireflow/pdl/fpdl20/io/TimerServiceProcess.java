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

import org.fireflow.engine.misc.ScriptContextVariableNames;
import org.fireflow.engine.service.TimerArgs;
import org.fireflow.engine.service.TimerOperationName;
import org.fireflow.model.binding.impl.InputAssignmentImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.data.impl.InputImpl;
import org.fireflow.model.data.impl.PropertyImpl;
import org.fireflow.model.servicedef.impl.IOSpecificationImpl;
import org.fireflow.model.servicedef.impl.OperationImpl;
import org.fireflow.model.servicedef.impl.ServiceImpl;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl20.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl20.process.impl.WorkflowProcessImpl;
import org.junit.Test;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class TimerServiceProcess {
	protected static final String processId = "TimerServiceProcess";
	@Test
	public void testSerialize() {
		try{
			File f = new File("src/test/java/org/fireflow/pdl/fpdl20/io/TimerServiceProcess.xml");
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
		
		//构造Timer service	
		String opName = TimerOperationName.TRIGGERED_ONLY_ONCE.name();		
		IOSpecificationImpl ioSpec = new IOSpecificationImpl();
		InputImpl input = new InputImpl();
		input.setName(TimerArgs.START_TIME.name());
		input.setDataType("java.util.Date");
		ioSpec.addInput(input);
		
		OperationImpl operation = new OperationImpl();
		operation.setOperationName(opName);
		operation.setIOSpecification(ioSpec);
		
		ServiceImpl timerService = new ServiceImpl();
		process.getLocalServices().add(timerService);
		
		timerService.setServiceType("Timer");
		timerService.setName("TRIGGERED_ONLY_ONCE");
		timerService.setDisplayName("定时启动");
		timerService.setOperation(operation);
		
		
		//将service绑定到activity
		ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
		serviceBinding.setService(timerService);
		serviceBinding.setServiceId(timerService.getId());
		serviceBinding.setOperation(timerService.getOperation(opName));
		serviceBinding.setOperationName(opName);
		
		ExpressionImpl expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setBody("dateTimeUtil.dateAfter(currentActivityInstance.startedTime,1,\"mi\")");
		InputAssignmentImpl inputAssignment = new InputAssignmentImpl();
		inputAssignment.setFrom(expression);
		inputAssignment.setTo(ScriptContextVariableNames.INPUTS+"."+TimerArgs.START_TIME.name());
		
		serviceBinding.getInputAssignments().add(inputAssignment);
		
		activity.setServiceBinding(serviceBinding);
		
		return process;
	}
}
