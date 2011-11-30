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
package org.fireflow.pdl.fpdl20.process;

import java.util.List;

import org.fireflow.model.ModelElement;
import org.fireflow.model.process.WorkflowElement;

/**
 * 引入其他的文件，一般是服务定义文件或者资源定义文件
 * 
 * @author 非也
 * @version 2.0
 */
public interface ProcessImport<T extends ModelElement> extends WorkflowElement{
	public static final String SERVICES_IMPORT = "org.fireflow.constants.SERVICES_IMPORT";
	public static final String RESOURCES_IMPORT = "org.fireflow.constants.RESOURCES_IMPORT";
	
	/**
	 * import的类型，取值为ProcessImport.SERVICES_IMPORT
	 * 或者ProcessImport.RESOURCES_IMPORT
	 * @return
	 */
	public String getImportType();
	public void setImportType(String type);
	
	/**
	 * import文件的classpath全路径文件名。
	 * @return
	 */
	public String getLocation();
	public void setLocation(String location);
	
	/**
	 * 返回import文件中的内容，如果import type为ProcessImport.SERVICES_IMPORT，则返回List<Service>;<br/>
	 * 如果import type为ProcessImport.RESOURCES_IMPORT，则返回List<Resource>；
	 * @return
	 */
	public List<T> getContents();
	
	public void setContents(List<T> contents);
	
	
	public T getContent(String id);
}
