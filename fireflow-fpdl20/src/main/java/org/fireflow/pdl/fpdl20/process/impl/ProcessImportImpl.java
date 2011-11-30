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
package org.fireflow.pdl.fpdl20.process.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.model.AbstractModelElement;
import org.fireflow.model.ModelElement;
import org.fireflow.pdl.fpdl20.process.ProcessImport;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ProcessImportImpl<T  extends ModelElement> extends AbstractModelElement implements ProcessImport<T> {
	private String importType;
	private String location;
	private List<T> contents = new ArrayList<T>();
	private Map<String,String> extendAttributes = new HashMap<String,String>();
	
	public ProcessImportImpl(WorkflowProcess process,String name){
		super(process,name);
	}
	
	/**
	 * @return the importType
	 */
	public String getImportType() {
		return importType;
	}
	/**
	 * @param importType the importType to set
	 */
	public void setImportType(String importType) {
		this.importType = importType;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	public List<T> getContents(){
		return contents;
	}
	
	
	public T getContent(String id) {		
		if (this.importType == null)
			return null;
		for (T t : contents) {
			if (((ModelElement) t).getId().equals(id)) {
				return t;
			}
		}
		return null;
	}
	
	
	public Map<String, String> getExtendedAttributes() {		
		return extendAttributes;
	}
	
	public void setContents(List<T> contents){
		this.contents = contents;
	}
}
