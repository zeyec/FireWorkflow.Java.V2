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

import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.servicedef.ServiceProp;
import org.fireflow.model.servicedef.ServicePropGroup;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ServicePropGroupImpl implements ServicePropGroup {
	private String name = null;
	private String displayName = null;
	List<ServiceProp> serviceProps = new ArrayList<ServiceProp>();
	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedef.ServicePropGroup#getDisplayName()
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedef.ServicePropGroup#getName()
	 */
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedef.ServicePropGroup#getServiceProp(java.lang.String)
	 */
	public ServiceProp getServiceProp(String propName) {
		if (this.serviceProps==null || this.serviceProps.size()==0){
			return null;
		}
		for (ServiceProp prop : serviceProps){
			if (prop.getName()!=null && prop.getName().equals(propName)){
				return prop;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedef.ServicePropGroup#getServiceProps()
	 */
	public List<ServiceProp> getServiceProps() {
		return this.serviceProps;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedef.ServicePropGroup#setDisplayName(java.lang.String)
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedef.ServicePropGroup#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedef.ServicePropGroup#setServiceProps(java.util.List)
	 */
	public void setServiceProps(List<ServiceProp> serviceProps) {
		this.serviceProps = serviceProps;
	}

}
