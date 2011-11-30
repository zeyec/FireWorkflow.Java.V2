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

import org.fireflow.model.binding.AssignmentStrategy;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ResourceRef;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ResourceBindingImpl implements ResourceBinding {
	private String name = null;
	private AssignmentStrategy assignmentStrategy = AssignmentStrategy.ASSIGN_TO_ANY;
	private List<ResourceRef> administrators = new ArrayList<ResourceRef>();
	private List<ResourceRef> readers = new ArrayList<ResourceRef>();
	private List<ResourceRef> potentialOwners = new ArrayList<ResourceRef>();
	
	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#getAdministrators()
	 */
	public List<ResourceRef> getAdministrators() {
		return administrators;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#setAdministrators(java.util.List)
	 */
	public void setAdministrators(List<ResourceRef> administrators){
		this.administrators = administrators;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#getName()
	 */
	public String getDisplayName() {
		return name;
	}
	
	public void setDisplayName(String nm){
		this.name = nm;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#getPotentialOwners()
	 */
	public List<ResourceRef> getPotentialOwners() {
		return this.potentialOwners;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#getReaders()
	 */
	public List<ResourceRef> getReaders() {
		return this.readers;
	}


	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#setPotentialOwners(java.util.List)
	 */
	public void setPotentialOwners(List<ResourceRef> potentialOwners) {
		this.potentialOwners = potentialOwners;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#setReaders(java.util.List)
	 */
	public void setReaders(List<ResourceRef> readers) {
		this.readers = readers;

	}
	/**
	 * @return the assignmentStrategy
	 */
	public AssignmentStrategy getAssignmentStrategy() {
		return assignmentStrategy;
	}
	/**
	 * @param assignmentStrategy the assignmentStrategy to set
	 */
	public void setAssignmentStrategy(AssignmentStrategy assignmentStrategy) {
		this.assignmentStrategy = assignmentStrategy;
	}

}
