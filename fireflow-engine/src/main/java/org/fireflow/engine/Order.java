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
package org.fireflow.engine;

import org.fireflow.engine.entity.EntityProperty;

/**
 * @author 非也
 * @version 2.0
 */
public class Order {

	private boolean ascending = true;
	private boolean ignoreCase;
	private EntityProperty property;
	
	public String toString() {
		return property.getColumnName() + ' ' + (ascending?"asc":"desc");
	}
	
//	public Order ignoreCase() {
//		ignoreCase = true;
//		return this;
//	}

	/**
	 * Constructor for Order.
	 */
	protected Order(EntityProperty propertyName, boolean ascending) {
		this.property = propertyName;
		this.ascending = ascending;
	}

	/**
	 * Render the SQL fragment
	 *
	 */
	public String toSqlString() {
		return property.getColumnName() + ' ' + (ascending?"asc":"desc");
	}

	/**
	 * Ascending order
	 *
	 * @param propertyName
	 * @return Order
	 */
	public static Order asc(EntityProperty propertyName) {
		return new Order(propertyName, true);
	}

	/**
	 * Descending order
	 *
	 * @param propertyName
	 * @return Order
	 */
	public static Order desc(EntityProperty propertyName) {
		return new Order(propertyName, false);
	}
	
	public EntityProperty getEntityProperty(){
		return this.property;
	}
	
	public Boolean isAscending(){
		return this.ascending;
	}
}
