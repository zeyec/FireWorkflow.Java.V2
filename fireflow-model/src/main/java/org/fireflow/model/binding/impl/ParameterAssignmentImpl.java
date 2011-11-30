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
package org.fireflow.model.binding.impl;

import org.fireflow.model.binding.ParameterAssignment;
import org.fireflow.model.data.Expression;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ParameterAssignmentImpl implements ParameterAssignment {

	private Expression from = null;
	private String to = null;
	/**
	 * @return the from
	 */
	public Expression getFrom() {
		return from;
	}
	/**
	 * @param from the from to set
	 */
	public void setFrom(Expression from) {
		this.from = from;
	}
	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}
	/**
	 * @param to the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}
	

}
