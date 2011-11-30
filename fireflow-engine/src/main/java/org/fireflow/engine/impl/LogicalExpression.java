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
package org.fireflow.engine.impl;

import org.fireflow.engine.Criterion;
import org.fireflow.engine.entity.EntityProperty;

/**
 * @author 非也
 * @version 2.0
 */
public class LogicalExpression implements Criterion {
	private final Criterion lhs;
	private final Criterion rhs;
	private final String op;

	protected LogicalExpression(Criterion lhs, Criterion rhs, String op) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.op = op;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.Criterion#toSqlString()
	 */
	public String toSqlString() {
		return '(' +
		lhs.toSqlString() +
		' ' +
		getOperation() +
		' ' +
		rhs.toSqlString() +
		')';
	}

	public String toString() {
		return lhs.toString() + ' ' + getOperation() + ' ' + rhs.toString();
	}
	
	public String getOperation(){
		return " "+op+" ";
	}
	public EntityProperty getEntityProperty(){
		return null;
	}
	public Object[] getValues(){
		return new Object[]{lhs,rhs};
	}	
}
