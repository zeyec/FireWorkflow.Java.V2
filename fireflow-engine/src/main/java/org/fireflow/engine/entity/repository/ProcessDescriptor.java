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
package org.fireflow.engine.entity.repository;

import java.util.Date;

import org.fireflow.engine.entity.WorkflowEntity;

/**
 * @author 非也
 * @version 2.0
 */
public interface ProcessDescriptor extends WorkflowEntity{
	public static final String OPERATION_UPLOAD = "UPLOAD";
	public static final String OPERATION_UPDATE = "UPDATE";
	public static final String OPERATION_PUBLISH = "PUBLISH";
	public static final String OPERATION_UNPUBLISH = "UNPUBLISH";
	public static final String OPERATION_UPLOAD_PUBLISH = "UPLOAD_PUBLISH";
	public static final String OPERATION_UPDATE_PUBLISH = "UPDATE_PUBLISH";
	public static final String OPERATION_UPDATE_UNPUBLISH = "UPDATE_UNPUBLISH";

	/**
	 * repository id
	 * @return
	 */
    public String getId();

    /**
     * 流程Id
     * @return
     */
    public String getProcessId();

    /**
     * 流程版本号
     * @return
     */
    public Integer getVersion();
    
    
    /**
     * 流程类别，可能是Fireworkflow流程，BPMN2.0流程或者BPEL流程
     * @return
     */
	public String getProcessType() ;
	
//	public ProcessKey getProcessKey();
	
    /**
     * 流程名称
     * @return
     */
    public String getName();
    
    /**
     * 流程中文名
     * @return
     */
    public String getDisplayName();

    /**
     * 流程描述信息
     * @return
     */
    public String getDescription() ;

    /**
     * 获得业务流程业务类别
     * @return
     */
    public String getBizCategory();

    /**
     * 流程发布状态
     * @return
     */
    public Boolean getPublishState();

    //////////////////////////////////////////////////////
    ////////// 下面是流程存储库的管理字段    ////////////////
    /////////////////////////////////////////////////////
    

    
    /**
     * 流程文件在classpath中的全路径名
     * @return
     */
    public String getFileName();
    
    /**
     * 流程所属部门的Id
     * @return
     */
    public String getOwnerDeptId();
    
    /**
     * 流程所属部门的名称
     * @return
     */
    public String getOwnerDeptName();
    

    /**
     * 批准人
     * @return
     */
    public String getApprover() ;
    
    
    /**
     * 批准时间
     * @return
     */
    public Date getApprovedTime() ;
    

    /**
     * 最后修改人姓名
     * @return
     */
	public String getLatestEditor() ;

	/**
	 * 最后修改时间
	 * @return
	 */
	public Date getLatestEditTime();

	/**
	 * 最后一次修改操作的内容，可以是UPLOAD,UPDATE,PUBLISH
	 * @return
	 */
	public String getLatestOperation();
}
