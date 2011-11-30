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

import org.fireflow.engine.entity.repository.ServiceDescriptor;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ServiceDescriptorImpl implements ServiceDescriptor{
    protected String id; //主键
    protected String serviceId;//服务id
    
    protected String name; //服务英文名称
    protected String displayName;//服务显示名称
    protected String description;//服务业务说明
    protected String bizCategory ;//业务类别
    protected String fileName = null;//服务文件在classpath中的全路径名

    protected Date latestEditTime = null;
    protected String latestEditor = null;//最后编辑服务的操作者姓名

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
	 * @return the serviceId
	 */
	public String getServiceId() {
		return serviceId;
	}
	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
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
	 * @return the bizCategory
	 */
	public String getBizCategory() {
		return bizCategory;
	}
	/**
	 * @param bizCategory the bizCategory to set
	 */
	public void setBizCategory(String bizCategory) {
		this.bizCategory = bizCategory;
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
