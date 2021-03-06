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
package org.fireflow.engine.entity.runtime.impl;

// Generated Feb 23, 2008 12:04:21 AM by Hibernate Tools 3.2.0.b9
import java.util.Date;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.context.RuntimeContextAware;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.pvm.kernel.KernelException;

/**
 * WorkItem generated by hbm2java
 */
@SuppressWarnings("serial")
public class WorkItemImpl extends AbsWorkItem implements WorkItem, java.io.Serializable {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.WorkItem#claim()
	 */
	public WorkItem claim() throws InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.WorkItem#complete()
	 */
	public void complete() throws EngineException, KernelException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.WorkItem#complete(java.lang.String)
	 */
	public void complete(String comments) throws EngineException,
			KernelException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.WorkItem#disclaim(java.lang.String)
	 */
	public void disclaim(String commentDetail) throws InvalidOperationException {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.WorkItem#jumpTo(java.lang.String)
	 */
	public void jumpTo(String targetActivityId) throws EngineException,
			KernelException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.WorkItem#jumpTo(java.lang.String, java.lang.String)
	 */
	public void jumpTo(String targetActivityId, String comments)
			throws EngineException, KernelException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.WorkItem#reassignTo(java.lang.String)
	 */
	public WorkItem reassignTo(String actorId) throws EngineException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.WorkItem#reassignTo(java.lang.String, java.lang.String)
	 */
	public WorkItem reassignTo(String actorId, String comments)
			throws EngineException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.WorkItem#reject()
	 */
	public void reject() throws EngineException, KernelException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.WorkItem#reject(java.lang.String)
	 */
	public void reject(String comments) throws EngineException, KernelException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.WorkItem#withdraw()
	 */
	public WorkItem withdraw() throws EngineException, KernelException {
		// TODO Auto-generated method stub
		return null;
	}

}
