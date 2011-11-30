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
package org.fireflow.engine.entity.repository.impl;

import java.util.Date;

import org.fireflow.engine.entity.repository.ResourceDescriptor;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ResourceDescriptorImpl implements ResourceDescriptor {
    protected String id; //主键
    protected String resourceId;//资源id
    
    protected String resourceType;//资源类别
    
    protected String name; //资源英文名称
    protected String displayName;//资源显示名称
    protected String description;//资源业务说明

    protected String fileName = null;//资源文件在classpath中的全路径名

    protected Date latestEditTime = null;
    protected String latestEditor = null;//最后编辑资源的操作者姓名
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the resourceId
	 */
	public String getResourceId() {
		return resourceId;
	}
	/**
	 * @param resourceId the resourceId to set
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	/**
	 * @return the resourceType
	 */
	public String getResourceType() {
		return resourceType;
	}
	/**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the latestEditTime
	 */
	public Date getLatestEditTime() {
		return latestEditTime;
	}
	/**
	 * @param latestEditTime the latestEditTime to set
	 */
	public void setLatestEditTime(Date latestEditTime) {
		this.latestEditTime = latestEditTime;
	}
	/**
	 * @return the latestEditor
	 */
	public String getLatestEditor() {
		return latestEditor;
	}
	/**
	 * @param latestEditor the latestEditor to set
	 */
	public void setLatestEditor(String latestEditor) {
		this.latestEditor = latestEditor;
	}
 
    
}
