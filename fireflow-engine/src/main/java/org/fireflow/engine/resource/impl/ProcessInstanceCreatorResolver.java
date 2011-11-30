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
package org.fireflow.engine.resource.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.ousystem.Actor;
import org.fireflow.engine.modules.ousystem.OUSystemAdapter;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.ousystem.impl.UserImpl;
import org.fireflow.engine.resource.ResourceResolver;
import org.fireflow.model.resourcedef.Resource;

/**
 * 解析流程实例创建者
 * 
 * @author 非也
 * @version 2.0
 */
public class ProcessInstanceCreatorResolver implements ResourceResolver{

	/* (non-Javadoc)
	 * @see org.fireflow.engine.resource.ResourceResolver#resolve(org.fireflow.model.resourcedef.Resource, java.util.Map)
	 */
	public List<User> resolve(WorkflowSession session,Resource resource,
			Map<String, Object> parameterValues) {
		
		List<User> users = new ArrayList<User>();
		
		ProcessInstance processInstance = session.getCurrentProcessInstance();
		if (processInstance==null)return users;
		
		String userId = processInstance.getCreatorId();
		if (FireWorkflowSystem.getInstance().getId().equals(userId)){
			users.add(FireWorkflowSystem.getInstance());
			return users;
		}
		
		
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		OUSystemAdapter ouSystemAdapter = rtCtx.getEngineModule(OUSystemAdapter.class, processInstance.getProcessType());

//		User u = ouSystemAdapter.findUserById(userId);
		//不从数据库查询，而是构造一个User，提高效率
		UserImpl u = new UserImpl();
		Properties props = new Properties();
		props.put(User.ID, processInstance.getCreatorId());
		props.put(User.NAME, processInstance.getCreatorName());
		props.put(User.DEPT_ID, processInstance.getCreatorDeptId());
		props.put(User.DEPT_NAME, processInstance.getCreatorDeptName());
		u.setProperties(props);
		
		users.add(u);
		return users;
	}

}
