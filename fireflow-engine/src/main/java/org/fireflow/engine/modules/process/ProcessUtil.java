/**
 * Copyright 2007-2008 非也
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
package org.fireflow.engine.modules.process;

import java.io.InputStream;

import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.context.RuntimeContextAware;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;

/**
 * 流程定义服务。
 * @author 非也，nychen2000@163.com
 *
 */
public interface ProcessUtil extends RuntimeContextAware,EngineModule {
	public String serializeProcess2Xml(Object process) throws InvalidModelException;
	
	public Object deserializeXml2Process(InputStream inStream)throws InvalidModelException;
	
	public ProcessRepository serializeProcess2ProcessRepository(Object process)throws InvalidModelException;

    public ServiceBinding getServiceBinding(ProcessKey processKey,String activityId)throws InvalidModelException;
    
    public ResourceBinding getResourceBinding(ProcessKey processKey,String activityId)throws InvalidModelException;
    
    public Object getActivity(ProcessKey processKey,String activityId)throws InvalidModelException;
       
}
