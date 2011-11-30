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
package org.fireflow.engine.resource;

import java.util.List;
import java.util.Map;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.model.resourcedef.Resource;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public interface ResourceResolver {
	/**
	 * 将资源解析成实际的User对象列表。
	 * @param resource
	 * @param inputValues
	 * @return
	 */
	public List<User> resolve(WorkflowSession session,Resource resource,Map<String,Object> inputValues); 
}
