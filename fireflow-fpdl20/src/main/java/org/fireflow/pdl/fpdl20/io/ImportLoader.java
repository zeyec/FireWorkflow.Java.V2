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
import java.util.List;

import org.fireflow.model.io.ParserException;
import org.fireflow.model.resourcedef.Resource;
import org.fireflow.model.servicedef.Service;

/**
 * 负责load “import” 标签的具体内容
 * 
 * @author 非也
 * @version 2.0
 */
public interface ImportLoader {
	public List<Service> loadServices(String serviceLocation)throws ParserException,IOException;
	public List<Resource> loadResources(String resourceLocation)throws ParserException,IOException;
}
