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
package org.fireflow.pdl.fpdl20.process.decorator.endnode;

import org.fireflow.pdl.fpdl20.process.decorator.Decorator;

/**
 * 流程中异常结束所抛出的异常被谁处理呢？
 * 
 * @author 非也
 * @version 2.0
 */
public interface ThrowFaultDecorator extends Decorator{

	/**
	 * 被监听的异常类的名称
	 * @return
	 */
	public String getErrorCode();
	
	public void setErrorCode(String errorCode);
}
