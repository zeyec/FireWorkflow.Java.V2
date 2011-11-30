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
package org.fireflow.model.servicedef.impl;

import org.fireflow.model.servicedef.ServiceProp;


/**
 * 
 * @author 非也
 * @version 2.0
 */
public class ServicePropImpl implements ServiceProp {
	private String name = null;
	private String displayName = null;
	private String description = null;
	private String value = null;
	
	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedef.ServiceProp#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedef.ServiceProp#getDisplayName()
	 */
	public String getDisplayName() {
		return displayName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedef.ServiceProp#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedef.ServiceProp#getValue()
	 */
	public String getValue() {
		return value;
	}
	
	public void setValue(String v){
		this.value = v;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedef.ServiceProp#setDescription(java.lang.String)
	 */
	public void setDescription(String description) {
		this.description = description;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedef.ServiceProp#setDisplayName(java.lang.String)
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedef.ServiceProp#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

}
