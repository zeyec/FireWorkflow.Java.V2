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
package org.fireflow.model.binding.impl;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.binding.ParameterAssignment;
import org.fireflow.model.binding.ResourceRef;
import org.fireflow.model.resourcedef.Resource;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ResourceRefImpl implements ResourceRef {
	String resourceId = null;
	Resource resource = null;
//	String resourceRoleName = null;
	List<ParameterAssignment> parameterAssignments = new ArrayList<ParameterAssignment>();

	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceRef#getParameterAssignments()
	 */
	public List<ParameterAssignment> getParameterAssignments() {
		return parameterAssignments;
	}
	
	public void setParameterAssignment(List<ParameterAssignment> assignments){
		this.parameterAssignments = assignments;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceRef#getResource()
	 */
	public Resource getResource() {
		return resource;
	}
	
	public void setResource(Resource rs){
		this.resource = rs;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceRef#getResourceId()
	 */
	public String getResourceId() {
		return resourceId;
	}
	
	public void setResourceId(String resourceId){
		this.resourceId = resourceId;
	}

//	/* (non-Javadoc)
//	 * @see org.fireflow.model.binding.ResourceRef#getResourceRoleName()
//	 */
//	@Override
//	public String getResourceRole() {
//		return resourceRoleName;
//	}
//	
//	public void setResourceRole(String role){
//		this.resourceRoleName = role;
//	}

}
