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
package org.fireflow.model.servicedef;

import org.fireflow.model.ModelElement;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public interface ServiceProp {
    /**
     * 返回工作流元素的名称
     * @return 元素名称
     */
    public String getName();

    
    public void setName(String name);

    /**
     * 返回工作流元素的显示名
     * @return 显示名
     */
    public String getDisplayName();

    public void setDisplayName(String displayName);

    /**
     * 返回流程元素的描述
     * @return 流程元素描述
     */
    public String getDescription();

    public void setDescription(String description);
    
	public String getValue();
	public void setValue(String v);
}
