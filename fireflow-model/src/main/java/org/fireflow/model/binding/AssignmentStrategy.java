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
package org.fireflow.model.binding;


/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public enum AssignmentStrategy {
	ASSIGN_TO_ALL("org.fireflow.constants.ASSIGN_TO_ALL"),
	ASSIGN_TO_ANY("org.fireflow.constants.ASSIGN_TO_ANY");
	
	private String value = null;
	private AssignmentStrategy(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	public static AssignmentStrategy fromValue(String v){
		AssignmentStrategy[] values =  AssignmentStrategy.values();
		for (AssignmentStrategy strategy : values){
			if (strategy.getValue().equals(v)){
				return strategy;
			}
		}
		return null;
	}	
}
