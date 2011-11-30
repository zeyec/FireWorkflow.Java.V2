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
package org.fireflow.model.data.impl;

import org.fireflow.model.data.Expression;


/**
 * @author 非也
 * @version 2.0
 */
public class ExpressionImpl extends AbsDataElement implements Expression {
	
	String body = null;
	String language =null;
	public ExpressionImpl(){
		
	}
	public ExpressionImpl(String language,String body){
		this.language = language;
		this.body = body;
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.model.data.Expression#getBody()
	 */
	public String getBody() {
		
		return body;
	}
	
	public void setBody(String body){
		this.body = body;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.data.Expression#getLanguage()
	 */
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String l){
		this.language = l;
	}

}
