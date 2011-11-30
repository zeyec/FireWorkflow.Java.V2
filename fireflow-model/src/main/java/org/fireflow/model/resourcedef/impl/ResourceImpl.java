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
package org.fireflow.model.resourcedef.impl;

import org.fireflow.model.AbstractModelElement;
import org.fireflow.model.resourcedef.ResolverDef;
import org.fireflow.model.resourcedef.Resource;
import org.fireflow.model.resourcedef.ResourceType;

/**

 * 
 * @author 非也
 * @version 2.0
 */
public class ResourceImpl extends AbstractModelElement implements Resource{
	private ResourceType resourceType = ResourceType.PROCESS_INSTANCE_CREATOR;
	private ResolverDef resolver = null;
//	private String fileName = null;
	
	public ResourceType getResourceType() {
		return resourceType;
	}
	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}
	public ResolverDef getResolver() {
		return resolver;
	}
	public void setResolver(ResolverDef resolver) {
		this.resolver = resolver;
	}
	
//	/**
//	 * @return the fileName
//	 */
//	public String getFileName() {
//		return fileName;
//	}
//	/**
//	 * @param fileName the fileName to set
//	 */
//	public void setFileName(String fileName) {
//		this.fileName = fileName;
//	}
}
