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
package org.fireflow.pdl.fpdl20.process.decorator.startnode.impl;

import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.CatchFaultDecorator;

/**
 * @author 非也
 * @version 2.0
 */
public class CatchFaultDecoratorImpl implements CatchFaultDecorator {
	String errorCode = null;
	Activity referencedActivity = null;
	/* (non-Javadoc)
	 * @see org.fireflow.model.process.decorator.startnode.CatchExceptionDecorator#getActivityRef()
	 */
	public Activity getAttachedToActivity() {
		return referencedActivity;
	}
	
	public void setAttachedToActivity(Activity act){
		referencedActivity = act;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.process.decorator.startnode.CatchExceptionDecorator#getExceptionClassName()
	 */
	public String getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(String exceptionCode){
		this.errorCode = exceptionCode;
	}

}
