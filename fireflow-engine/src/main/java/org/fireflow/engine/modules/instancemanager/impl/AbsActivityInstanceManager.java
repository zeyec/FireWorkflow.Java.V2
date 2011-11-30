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
package org.fireflow.engine.modules.instancemanager.impl;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.context.RuntimeContextAware;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;
import org.fireflow.engine.service.ServiceExecutor;
import org.fireflow.engine.service.ServiceRegistry;
import org.fireflow.model.servicedef.Service;
import org.fireflow.pvm.kernel.BookMark;
import org.fireflow.pvm.kernel.ExecutionEntrance;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.Token;

/**
 * @author 非也
 * @version 2.0
 */
public abstract class AbsActivityInstanceManager implements
		ActivityInstanceManager,RuntimeContextAware {
	protected RuntimeContext runtimeContext = null;
	


	public RuntimeContext getRuntimeContext() {
		return runtimeContext;
	}


	public void setRuntimeContext(RuntimeContext ctx) {
		runtimeContext = ctx;		
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ActivityInstanceManager#completeActivityInstance(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance)
	 */
	public void onServiceCompleted(WorkflowSession session,
			ActivityInstance activityInstance) {
		WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;
		RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();

		
		KernelManager kernelManager = runtimeContext.getDefaultEngineModule(KernelManager.class);
		
		Token token = kernelManager.getToken(activityInstance.getTokenId(), activityInstance.getProcessType());
		BookMark bookMark = new BookMark();
		bookMark.setToken(token);
		bookMark.setExtraArg(BookMark.SOURCE_TOKEN, token);
		bookMark.setExecutionEntrance(ExecutionEntrance.FORWARD_TOKEN);
		kernelManager.addBookMark(bookMark);
		
		kernelManager.execute(sessionLocalImpl);
		
	}	
	
	public ActivityInstance abortActivityInstance(WorkflowSession session , ActivityInstance activityInstance){
		RuntimeContext context = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = context.getDefaultEngineModule(KernelManager.class);		
		Token token = kernelManager.getToken(activityInstance.getTokenId(), activityInstance.getProcessType());

		kernelManager.fireTerminationEvent(session, token, null);

		return activityInstance;
	}
	
	public ActivityInstance suspendActivityInstance(WorkflowSession session , ActivityInstance activityInstance){
		return null;
	}
	
	public ActivityInstance restoreActivityInstance(WorkflowSession session , ActivityInstance activityInstance){
		return null;
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
