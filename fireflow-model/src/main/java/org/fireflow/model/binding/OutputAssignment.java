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

import org.fireflow.model.data.Expression;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public interface OutputAssignment {
	/**
	 * 将该表达式计算出来的结果赋值给流程变量。
	 * 该表达式一般是一个基于Service输出的表达式。
	 * @return
	 */
	public Expression getFrom();
	public void setFrom(Expression exp);
	
	/**
	 * 流程变量的名字
	 * @return
	 */
	public String getTo();
	public void setTo(String inputName);
}
