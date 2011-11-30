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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.ousystem.OUSystemAdapter;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.resource.ResourceResolver;
import org.fireflow.model.resourcedef.Resource;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class VariableImplicationResolver implements ResourceResolver {
	private static Log log = LogFactory.getLog(VariableImplicationResolver.class);

	public static final String VARIABLE_NAME = "variableName";
	/* (non-Javadoc)
	 * @see org.fireflow.engine.resource.ResourceResolver#resolve(org.fireflow.engine.WorkflowSession, org.fireflow.model.resourcedef.Resource, java.util.Map)
	 */
	public List<User> resolve(WorkflowSession session, Resource resource,
			Map<String, Object> parameterValues) {
		List<User> users = new ArrayList<User>();
		
		ProcessInstance processInstance = session.getCurrentProcessInstance();
		if (processInstance==null){
			log.error("Current process instance is null,can NOT retrieve the actors");
			return users;
		}
		
		String variableName = parameterValues==null?null:(String)parameterValues.get(VARIABLE_NAME);
		if (variableName==null || variableName.trim().equals("")){
			log.error("The parameter value of 'variableName' is null,can NOT retrieve the actors");
			return users;
		}
		
		Object value = processInstance.getVariableValue(session, variableName);
		
		if (value==null ){
			log.error("The variable value of "+variableName+" is null,can NOT retrieve the actors");
			return users;
		}
		if (! (value instanceof String)){
			log.error("The variable value of "+variableName+" is NOT a String,can NOT retrieve the actors");
			return users;
		}
		String userId = (String)value;
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		OUSystemAdapter ouSystemAdapter = rtCtx.getEngineModule(OUSystemAdapter.class, processInstance.getProcessType());
		
		User u = ouSystemAdapter.findUserById(userId);
		users.add(u);
		return users;
	}

}
