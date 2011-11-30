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
public class InExpression implements Criterion {
	private final EntityProperty property;
	private final Object[] values;

	protected InExpression(EntityProperty property, Object[] values) {
		this.property = property;
		this.values = values;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.Criterion#toSqlString()
	 */
	public String toSqlString() {
		return property.getColumnName() + " in (" + getPlaceHolder(values) + ')';
	}

	public String toString() {
		return property.getColumnName() + " in (" + getValuesString(values) + ')';
	}
	
	private String getValuesString(Object[] values){
		StringBuffer buf = new StringBuffer();
		for (int i=0;i<values.length;i++){
			Object v = values[i];
			buf.append(v.toString());
			if (i<values.length-1){
				buf.append(",");
			}
		}
		return buf.toString();
	}
	
	private String getPlaceHolder(Object[] values){
		StringBuffer buf = new StringBuffer();
		for (int i=0;i<values.length;i++){
			Object v = values[i];
			buf.append("?");
			if (i<values.length-1){
				buf.append(",");
			}
		}
		return buf.toString();
	}
	
	public String getOperation(){
		return " in ";
	}
	public EntityProperty getEntityProperty(){
		return property;
	}
	public Object[] getValues(){
		return values;
	}	
}
