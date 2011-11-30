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

package org.fireflow.model.binding;

import java.util.List;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public interface ResourceBinding {
	/**
	 * 资源的名称
	 * @return
	 */
	public String getDisplayName();
	
	
	/**
	 * 工作项分配策略
	 * @return
	 */
	public AssignmentStrategy getAssignmentStrategy();
	
	public void setAssignmentStrategy(AssignmentStrategy strategy);
	
	/**
	 * 业务领导
	 * @return
	 */
	public List<ResourceRef> getAdministrators();
	
	public void setAdministrators(List<ResourceRef> admins);
	
	/**
	 * 潜在所有者，即参与者
	 * @return
	 */
	public List<ResourceRef> getPotentialOwners();
	
	public void setPotentialOwners(List<ResourceRef> potentialOwners);
	
	/**
	 * 抄送人
	 * @return
	 */
	public List<ResourceRef> getReaders();
	
	public void setReaders(List<ResourceRef> readers);
}
