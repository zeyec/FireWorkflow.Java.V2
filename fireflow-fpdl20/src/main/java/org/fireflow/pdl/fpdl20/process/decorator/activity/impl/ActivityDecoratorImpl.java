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
package org.fireflow.pdl.fpdl20.process.decorator.activity.impl;

import org.fireflow.model.misc.Duration;
import org.fireflow.model.servicedef.ServiceType;
import org.fireflow.pdl.fpdl20.process.decorator.activity.ActivityDecorator;

/**
 * @author 非也
 * @version 2.0
 */
public class ActivityDecoratorImpl implements ActivityDecorator {
	private ServiceType targetServiceType = null;
	/* (non-Javadoc)
	 * @see org.fireflow.model.process.decorator.activity.ActivityDecorator#getTargetServiceType()
	 */
	public ServiceType getTargetServiceType() {
		return targetServiceType;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.process.decorator.activity.ActivityDecorator#setTargetServiceType(org.fireflow.model.service.ServiceType)
	 */
	public void setTargetServiceType(ServiceType serviceType) {
		targetServiceType = serviceType;
	}

//	/* (non-Javadoc)
//	 * @see org.fireflow.model.process.decorator.activity.ActivityDecorator#getAssignmentStrategy()
//	 */
//	@Override
//	public String getAssignmentStrategy() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.fireflow.model.process.decorator.activity.ActivityDecorator#getCompletionStrategy()
//	 */
//	@Override
//	public String getCompletionStrategy() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.fireflow.model.process.decorator.activity.ActivityDecorator#getDuration()
//	 */
//	@Override
//	public Duration getDuration() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.fireflow.model.process.decorator.activity.ActivityDecorator#getPriority()
//	 */
//	@Override
//	public String getPriority() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.fireflow.model.process.decorator.activity.ActivityDecorator#setAssignmentStrategy(java.lang.String)
//	 */
//	@Override
//	public void setAssignmentStrategy(String s) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	/* (non-Javadoc)
//	 * @see org.fireflow.model.process.decorator.activity.ActivityDecorator#setCompletionStrategy(java.lang.String)
//	 */
//	@Override
//	public void setCompletionStrategy(String s) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	/* (non-Javadoc)
//	 * @see org.fireflow.model.process.decorator.activity.ActivityDecorator#setDuration(org.fireflow.model.misc.Duration)
//	 */
//	@Override
//	public void setDuration(Duration du) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	/* (non-Javadoc)
//	 * @see org.fireflow.model.process.decorator.activity.ActivityDecorator#setPriority(java.lang.String)
//	 */
//	@Override
//	public void setPriority(String s) {
//		// TODO Auto-generated method stub
//		
//	}

}
