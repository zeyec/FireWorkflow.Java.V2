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

import java.util.HashMap;
import java.util.Map;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.WorkflowSessionFactory;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.service.ServiceExecutor;
import org.fireflow.engine.service.ServiceRegistry;
import org.fireflow.model.servicedef.Service;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.service.callback.CallbackExecutor;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class MyInterfaceImpl implements MyInterface {
	RuntimeContext context;
	
	public MyInterfaceImpl(RuntimeContext context){
		this.context = context;
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.callbackexecutor.MyInterface#callProcess(java.lang.String, int)
	 */
	public int callProcess(String bizId, int b){
		Map<String,Object> inputs = new HashMap<String,Object>();
		inputs.put("bizId", bizId);
		inputs.put("b", b);
		
		Map<String,Object> callContext = new HashMap<String,Object>();
		callContext.put(CallbackExecutor.CTX_KEY_PROCESS_ID, "TheCallbackProcess1");
		callContext.put(CallbackExecutor.CTX_KEY_ACTIVITY_ID, "TheCallbackProcess1.Activity1");
		callContext.put(CallbackExecutor.CTX_KEY_CORRELATION, "currentProcessInstance.bizId==inputs.bizId");
		callContext.put(CallbackExecutor.CTX_KEY_PROCESS_TYPE, FpdlConstants.PROCESS_TYPE);
		callContext.put(CallbackExecutor.CTX_KEY_START_NEW_PROCESS, Boolean.FALSE);

		Map<String,Object> outputs = new HashMap<String,Object>();
		
		WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(context,FireWorkflowSystem.getInstance());
		ServiceRegistry serviceRegistry = context.getDefaultEngineModule(ServiceRegistry.class);

		CallbackExecutor callbackExecutor = 
			(CallbackExecutor)serviceRegistry.getServiceExecutor(CallbackExecutor.SERVICE_TYPE);
		
		callbackExecutor.onCalled(session, inputs, outputs, callContext);
		
		if (outputs.size()>0){
			Object obj = outputs.values().toArray()[0];
			return (Integer)obj;
		}
		
		return 0;
	}
	protected ServiceExecutor getServiceExecutor(RuntimeContext runtimeContext,Service service,String processType){
		ServiceExecutor serviceExecutor = null;

		String executorName = service.getExecutorName();
		if (executorName!=null && !executorName.trim().equals("")){
			BeanFactory beanFactory = runtimeContext.getEngineModule(BeanFactory.class,processType);
			serviceExecutor = (ServiceExecutor)beanFactory.getBean(executorName);
		}
		if (serviceExecutor==null){
			ServiceRegistry serviceRegistry = runtimeContext.getDefaultEngineModule(ServiceRegistry.class);
			
			String serviceType = service.getServiceType();
			serviceExecutor = serviceRegistry.getServiceExecutor(serviceType);
		}
		return serviceExecutor;
	}
}
