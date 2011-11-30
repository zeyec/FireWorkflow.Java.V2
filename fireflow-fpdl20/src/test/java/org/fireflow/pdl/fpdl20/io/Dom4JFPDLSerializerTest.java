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

import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.servicedef.impl.OperationImpl;
import org.fireflow.model.servicedef.impl.ServiceImpl;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.decorator.endnode.impl.ThrowCompensationDecoratorImpl;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.impl.CatchCompensationDecoratorImpl;
import org.fireflow.pdl.fpdl20.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl20.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.RouterImpl;
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
public class Dom4JFPDLSerializerTest {

	/**
	 * Test method for {@link org.fireflow.pdl.fpdl20.io.Dom4JFPDLSerializer#serialize(org.fireflow.pdl.fpdl20.process.WorkflowProcess, java.io.OutputStream)}.
	 */
	@Test
	public void testSerialize() {
		try{
			File f = new File("src/test/java/org/fireflow/pdl/fpdl20/io/Dom4JFPDLSerializerTest_process.xml");
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
		WorkflowProcessImpl process = new WorkflowProcessImpl("TheProcess4TestSerializer_1");
		
		StartNodeImpl startNode = new StartNodeImpl(process,"Start");
		
		RouterImpl router1 = new RouterImpl(process,"Router");
		
		router1.setDescription("路由1");
		router1.setDisplayName("router1");
		
		//////////////////////////////////////////////////////////////
		//////////  Activity1 及其 异常处理分支，以及EndNode1////////////
		//////////////////////////////////////////////////////////////
		ActivityImpl activity1 = new ActivityImpl(process,"Activity1");
		activity1.setDescription("This is activity1");
		
		//补偿捕获节点
		StartNodeImpl catchCompensationNode = new StartNodeImpl(process,"CatchCompensation");
		CatchCompensationDecoratorImpl catchCompensationDecorator = new CatchCompensationDecoratorImpl();
		catchCompensationDecorator.setAttachedToActivity(activity1);
		catchCompensationNode.setDecorator(catchCompensationDecorator);
		
		activity1.getAttachedStartNodes().add(catchCompensationNode);
		
		ActivityImpl handleCompensationNode = new ActivityImpl(process,"HandleCompensation");
		
		TransitionImpl transition0 = new TransitionImpl(process,"catchCompensation2HandleCompensation");
		transition0.setDescription("This is transition0");
		transition0.setDisplayName("转移0");
		transition0.setFromNode(catchCompensationNode);
		transition0.setToNode(handleCompensationNode);
		catchCompensationNode.getLeavingTransitions().add(transition0);
		handleCompensationNode.getEnteringTransitions().add(transition0);
		
		
		EndNodeImpl endNode1 = new EndNodeImpl(process,"End1");
		ThrowCompensationDecoratorImpl compensationDecorator = new ThrowCompensationDecoratorImpl();
		compensationDecorator.addCompensationCode("TheCompensationActivity");//只有CompensationCode=‘TheCompensationActivity’的catch compensation decorator才会被激发。
		endNode1.setDecorator(compensationDecorator);
		
		//////////////////////////////////////////////////////////////
		//////////  Activity1以及EndNode2                 ////////////
		//////////////////////////////////////////////////////////////			
		ActivityImpl activity2 = new ActivityImpl(process,"Activity2");
		//构造Human service	
		String opName = "xyz/Application.jsp";
		OperationImpl operation = new OperationImpl();
		operation.setOperationName(opName);
		
		ServiceImpl humanService = new ServiceImpl();
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
		
		process.getLocalServices().add(humanService);
		return process;
	}

}
