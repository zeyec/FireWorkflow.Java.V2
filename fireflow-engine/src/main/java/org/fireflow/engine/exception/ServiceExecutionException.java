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
package org.fireflow.engine.exception;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ServiceExecutionException extends Exception {
	String errorCode = null;
	
	/**
	 * 
	 */
	public ServiceExecutionException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ServiceExecutionException(String arg, Throwable arg0) {
		super(arg, arg0);
	}

	/**
	 * @param arg0
	 */
	public ServiceExecutionException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public ServiceExecutionException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	
}
