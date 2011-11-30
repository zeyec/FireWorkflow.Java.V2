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
package org.fireflow.model.resourcedef;

import org.fireflow.model.ModelElement;

/**
 * 
 * <Resources>
 * 	<Resource id="org.fireflow.model.resourcedef.PROCESS_INSTANCE_CREATOR" name="Process Creator"
 *  	displayName="流程创建者" type="ProcessInstanceCreator" pid="">
 *   	<Resolver bean="org.fireflow.engine.resource.impl.ProcessInstanceCreatorResolver">
 *   	</Resolver>
 * 	</Resource>
 * 	<Resource id="dept_research" name="dept_research"
 *  	displayName="研发部" type="Department" pid="">
 *   	<Resolver bean="org.fireflow.engine.resource.impl.DepartmentResolver">
 *   		<Parameters>
 *   			<Parameter name="" displayName="" defaultValueAsString=""/>
 *   		</Parameters>
 *   	</Resolver>
 * 	</Resource>
 * 	<Resource id="role_manager" name="role_manager"
 *  	displayName="部门经理" type="Role" pid="">
 *   	<Resolver bean="#RoleResolver">
 *   		<Parameters>
 *   			<Parameter name="departmentId" displayName="所属部门Id" defaultValueAsString=""/>
 *   		</Parameters>
 *   	</Resolver>
 * 	</Resource>
 * 
 * 
 * </Resources>
 * 
 * @author 非也
 * @version 2.0
 */
public interface Resource extends ModelElement{
	public ResourceType getResourceType() ;

	/**
	 * 资源xml定义文件的classpath全路径名
	 * (fileName在这里不合理,2011-02-17)
	 * @return
	 */
//	public String getFileName();
	public ResolverDef getResolver();
}
