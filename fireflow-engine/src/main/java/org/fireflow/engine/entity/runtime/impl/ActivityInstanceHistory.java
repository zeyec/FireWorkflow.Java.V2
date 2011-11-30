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
package org.fireflow.engine.entity.runtime.impl;

import java.io.Serializable;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.pvm.kernel.KernelException;

/**
 * @author 非也
 * @version 2.0
 */
public class ActivityInstanceHistory extends AbsActivityInstance implements
		Serializable {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#abort(org.fireflow.engine.WorkflowSession)
	 */
	public void abort(WorkflowSession session) throws InvalidOperationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#restore(org.fireflow.engine.WorkflowSession)
	 */
	public void restore(WorkflowSession session)
			throws InvalidOperationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#skipOver(org.fireflow.engine.WorkflowSession)
	 */
	public void skipOver(WorkflowSession session) throws EngineException,
			KernelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#skipOver(org.fireflow.engine.WorkflowSession, java.lang.String)
	 */
	public void skipOver(WorkflowSession session, String targetActivityId)
			throws EngineException, KernelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#skipOver(org.fireflow.engine.WorkflowSession, java.lang.String, org.fireflow.engine.taskinstance.DynamicAssignmentHandler)
	 */
//	@Override
//	public void skipOver(WorkflowSession session, String targetActivityId,
//			DynamicAssignmentHandler dynamicAssignmentHandler)
//			throws EngineException, KernelException {
//		// TODO Auto-generated method stub
//
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#suspend(org.fireflow.engine.WorkflowSession)
	 */
	public void suspend(WorkflowSession session)
			throws InvalidOperationException {
		// TODO Auto-generated method stub

	}
	
	public void setVariableValue(WorkflowSession session ,String name ,Object value)throws InvalidOperationException{
		throw new InvalidOperationException("Can not set variable on the history object");
	}

}
