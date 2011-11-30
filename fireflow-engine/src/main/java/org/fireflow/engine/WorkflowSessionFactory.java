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
package org.fireflow.engine;

import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.ousystem.User;

/**
 * @author 非也
 * @version 2.0
 */
public class WorkflowSessionFactory {
	/**
	 * 获得本地Session
	 * @param currentUser
	 * @return
	 */
	public static WorkflowSession createWorkflowSession(RuntimeContext runtimeContext,User currentUser){
		WorkflowSessionLocalImpl localSession = new WorkflowSessionLocalImpl();
		localSession.setCurrentUser(currentUser);
		localSession.setRuntimeContext(runtimeContext);
		return localSession;
	}
	
	/**
	 * 获得远程Session
	 * @param url
	 * @param userId
	 * @param password
	 * @return
	 */
//	public static WorkflowSession createWorkflowSession(String url,String userId,String password){
//		WorkflowServer serverStub = null;//获得WorkflowServer的远程代理
//		WorkflowSessionRemoteImpl remoteSession = serverStub.login(userId, password);
//		remoteSession.setWorkflowServer(serverStub);
//		return remoteSession;
//	}
}
