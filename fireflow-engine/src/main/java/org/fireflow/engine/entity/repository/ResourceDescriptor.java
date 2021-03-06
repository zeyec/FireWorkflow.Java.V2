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
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public interface ResourceDescriptor extends WorkflowEntity{
	/**
	 * repository id
	 * @return
	 */
    public String getId();

    /**
     * 资源Id
     * @return
     */
    public String getResourceId();

    /**
     * 资源类别
     * @return
     */
    public String getResourceType();
    
    /**
     * 资源名称
     * @return
     */
    public String getName();
    
    /**
     * 资源中文名
     * @return
     */
    public String getDisplayName();

    /**
     * 资源描述信息
     * @return
     */
    public String getDescription() ;



    //////////////////////////////////////////////////////
    ////////// 下面是资源存储库的管理字段    ////////////////
    /////////////////////////////////////////////////////

    
    /**
     * 资源定义对应的文件名
     * @return
     */
    public String getFileName();
   

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

}
