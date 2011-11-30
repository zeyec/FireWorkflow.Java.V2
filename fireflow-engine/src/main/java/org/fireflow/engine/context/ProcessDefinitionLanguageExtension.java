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
package org.fireflow.engine.context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author 非也
 * @version 2.0
 */
public class ProcessDefinitionLanguageExtension implements RuntimeContextAware{
	private RuntimeContext runtimeContext = null;
	
	
	/**
	 * 流程定义语言的名称，取值如XPDL,FPDL,
	 */
	private String processType = null;
	
	/**
	 * 描述信息
	 */
	private String description = null;
	
	/**
	 * 该语言特定的引擎模块
	 */
	private Map<String,EngineModule> engineModules = new HashMap<String,EngineModule>();
	
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#getRuntimeContext()
	 */
	public RuntimeContext getRuntimeContext() {
		return this.runtimeContext;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#setRuntimeContext(org.fireflow.engine.context.RuntimeContext)
	 */
	public void setRuntimeContext(RuntimeContext ctx) {
		this.runtimeContext = ctx;
		Iterator<EngineModule> iterator = this.engineModules.values().iterator();
		while(iterator.hasNext()){
			EngineModule module = iterator.next();
    		if (module instanceof RuntimeContextAware){
    			((RuntimeContextAware)module).setRuntimeContext(runtimeContext);
    		}
		}
	}
	
	
    @SuppressWarnings("unchecked")
	public <T extends EngineModule> T getEngineModule(Class<T> interfaceClass){
    	EngineModule module = this.engineModules.get(interfaceClass.getName());
    	return (T)module;
    }
    
    public void setEngineModules(Map<String,EngineModule> _engineModules){
    	if (_engineModules==null){
    		return ;
    	}
    	this.engineModules.putAll(_engineModules);
    }

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, EngineModule> getEngineModules() {
		return engineModules;
	}


}
