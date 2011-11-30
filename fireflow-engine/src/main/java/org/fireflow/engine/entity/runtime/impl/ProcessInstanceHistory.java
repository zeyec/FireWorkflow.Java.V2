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
import java.util.Map;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;

/**
 * @author 非也
 * @version 2.0
 */
public class ProcessInstanceHistory extends AbsProcessInstance implements
		ProcessInstance, Serializable {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#abort(org.fireflow.engine.WorkflowSession)
	 */
	public void abort(WorkflowSession session) throws EngineException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getProcessInstanceVariable(org.fireflow.engine.WorkflowSession, java.lang.String)
	 */
	@Override
	public Object getVariableValue(WorkflowSession session,
			String name) {
		// TODO Auto-generated method stub
		return null;
	}




	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#restore(org.fireflow.engine.WorkflowSession)
	 */
	public void restore(WorkflowSession session) throws EngineException {
		// TODO Auto-generated method stub

	}




	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#suspend(org.fireflow.engine.WorkflowSession)
	 */
	public void suspend(WorkflowSession session) throws EngineException {
		// TODO Auto-generated method stub

	}

	public void setVariableValue(WorkflowSession session ,String name ,Object value)throws InvalidOperationException{
		throw new InvalidOperationException("Can not set variable on the history object");
	}
}
