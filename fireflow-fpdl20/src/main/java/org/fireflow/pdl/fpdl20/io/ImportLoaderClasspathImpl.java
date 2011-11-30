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
package org.fireflow.pdl.fpdl20.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.fireflow.model.io.Dom4JResourceParser;
import org.fireflow.model.io.Dom4JServiceParser;
import org.fireflow.model.io.ParserException;
import org.fireflow.model.resourcedef.Resource;
import org.fireflow.model.servicedef.Service;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ImportLoaderClasspathImpl implements ImportLoader {

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.io.ImportLoader#loadResources(java.lang.String)
	 */
	public List<Resource> loadResources(String resourceLocation) throws ParserException,IOException{
		if (resourceLocation==null || resourceLocation.trim().equals("")){
			return null;
		}
		String fileName = resourceLocation;
		if (resourceLocation.startsWith("/") || resourceLocation.startsWith("\\")){
			fileName = resourceLocation.substring(1);
		}
		
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(fileName);	
		Dom4JResourceParser parser = new Dom4JResourceParser();
		
		return parser.parse(inStream);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.io.ImportLoader#loadServices(java.lang.String)
	 */
	public List<Service> loadServices(String serviceFileName) throws ParserException,IOException{
		if (serviceFileName==null || serviceFileName.trim().equals("")){
			return null;
		}
		String fileName = serviceFileName;
		if (serviceFileName.startsWith("/") || serviceFileName.startsWith("\\")){
			fileName = serviceFileName.substring(1);
		}
		
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(fileName);	
		Dom4JServiceParser parser = new Dom4JServiceParser();
		
		return parser.parse(inStream);
	}

}
