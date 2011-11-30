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
package org.fireflow.model.resourcedef.impl;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.data.Input;
import org.fireflow.model.resourcedef.ResolverDef;


/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ResolverDefImpl implements ResolverDef {
	private String beanName = null;
//	private String className = null;
	
	List<Input> parameters = new ArrayList<Input>();
	
	/* (non-Javadoc)
	 * @see org.fireflow.model.resource.Resolver#getBeanName()
	 */
	public String getBeanName() {
		return beanName;
	}

//	/* (non-Javadoc)
//	 * @see org.fireflow.model.resource.Resolver#getClassName()
//	 */
//	@Override
//	public String getClassName() {
//		return className;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.resource.Resolver#getParameters()
	 */
	public List<Input> getParameters() {
		return parameters;
	}

	/**
	 * @param beanName the beanName to set
	 */
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
//
//	/**
//	 * @param className the className to set
//	 */
//	public void setClassName(String className) {
//		this.className = className;
//	}

	/**
	 * @param inputs the parameters to set
	 */
	public void setParameters(List<Input> inputs) {
		this.parameters = inputs;
	}
}
