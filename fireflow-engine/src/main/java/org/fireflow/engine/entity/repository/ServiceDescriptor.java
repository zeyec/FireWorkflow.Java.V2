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
public interface ServiceDescriptor extends WorkflowEntity {
	/**
	 * repository id
	 * @return
	 */
    public String getId();

    /**
     * 服务Id
     * @return
     */
    public String getServiceId();

    
    /**
     * 服务名称
     * @return
     */
    public String getName();
    
    /**
     * 服务中文名
     * @return
     */
    public String getDisplayName();

    /**
     * 服务描述信息
     * @return
     */
    public String getDescription() ;

    //////////////////////////////////////////////////////
    ////////// 下面是服务存储库的管理字段    ////////////////
    /////////////////////////////////////////////////////
    /**
     * 服务的业务类别，表现为"某OA系统/某模块/某子模块"
     */
    public String getBizCategory();
    
    
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
