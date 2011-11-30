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

import java.util.List;

import org.fireflow.engine.context.EngineModule;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public interface ServiceRegistry extends EngineModule{
	public ServiceExecutor getServiceExecutor(String serviceType);
	public void registServiceExecutor(ServiceExecutor executor);
	public void setServiceExecutors(List<ServiceExecutor> serviceExecutors);
}
