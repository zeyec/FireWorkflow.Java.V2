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
package org.fireflow.engine.modules.persistence;

import java.util.List;
import java.util.Map;

import org.fireflow.engine.entity.runtime.Scope;
import org.fireflow.engine.entity.runtime.Variable;

/**
 * @author 非也
 * @version 2.0
 */
public interface VariablePersister extends Persister {
	public Variable findVariable(String scopeId,String name);
	public List<Variable> findVariables(String scopeId);
	
	public Object findVariableValue(String scopeId,String name);
	public Map<String ,Object> findVariableValues(String scopeId);
	
	public Variable setVariable(Scope scope,String name,Object value);
	
	/**
	 * 将java对象序列化成字符串
	 * @param object
	 * @return
	 */
	public String serializeObject2String(Object object);
	
	/**
	 * 将字符串反序列化成java对象
	 * @param strValue
	 * @return
	 */
	public Object deserializeString2Object(String strValue);
}
