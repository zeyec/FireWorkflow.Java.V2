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
package org.fireflow.engine.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.context.RuntimeContextAware;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ServiceRegistryImpl implements RuntimeContextAware,ServiceRegistry {
	RuntimeContext runtimeContext = null;
	
	Map<String ,ServiceExecutor> serviceTypeRegistry = new HashMap<String,ServiceExecutor>();
	
	public ServiceExecutor getServiceExecutor(String serviceType){
		return serviceTypeRegistry.get(serviceType.toUpperCase());
	}
	
	public void registServiceExecutor(ServiceExecutor executor){
		this.serviceTypeRegistry.put(executor.getServiceType(), executor);
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#getRuntimeContext()
	 */
	public RuntimeContext getRuntimeContext() {
		return runtimeContext;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#setRuntimeContext(org.fireflow.engine.context.RuntimeContext)
	 */
	public void setRuntimeContext(RuntimeContext ctx) {
		runtimeContext = ctx;

	}
	
	public void setServiceExecutors(List<ServiceExecutor> serviceExecutors){
		if (serviceExecutors!=null){
			for (ServiceExecutor serviceExecutor:serviceExecutors){
				this.serviceTypeRegistry.put(serviceExecutor.getServiceType(), serviceExecutor);
			}
		}
	}

}
