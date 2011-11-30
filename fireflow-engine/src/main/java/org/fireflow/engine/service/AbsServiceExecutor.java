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

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public abstract class AbsServiceExecutor implements ServiceExecutor {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.ServiceExecutor#complete(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance, java.lang.Object)
	 */
	public void onServiceCompleted(WorkflowSession session,
			ActivityInstance activityInstance) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		ActivityInstanceManager activityInstanceMgr = ctx.getEngineModule(ActivityInstanceManager.class,activityInstance.getProcessType());
		
		activityInstanceMgr.onServiceCompleted(session, activityInstance);

	}

	public int determineActivityCloseStrategy(WorkflowSession session,ActivityInstance activityInstance){
		return ServiceExecutor.CLOSE_ACTIVITY;
	}
	
}
