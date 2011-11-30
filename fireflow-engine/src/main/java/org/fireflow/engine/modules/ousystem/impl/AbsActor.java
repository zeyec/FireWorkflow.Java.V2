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
package org.fireflow.engine.modules.ousystem.impl;

import java.util.Properties;

import org.fireflow.engine.modules.ousystem.Actor;


/**
 * @author 非也
 * @version 2.0
 */
public abstract class AbsActor implements Actor {
	Properties properties = new Properties(); 
	/* (non-Javadoc)
	 * @see org.fireflow.engine.resource.Participant#getId()
	 */
	public String getId() {
		
		return properties==null?null:(String)properties.get(Actor.ID);
	}
	
	public void setId(String id){
		properties.put(Actor.ID, id);
	}


	/* (non-Javadoc)
	 * @see org.fireflow.engine.resource.Participant#getName()
	 */
	public String getName() {

		return properties==null?null:(String)properties.get(Actor.NAME);
	}
	
	public void setName(String name){
		properties.put(Actor.NAME, name);
	}
	
	
	public String getProperty(String key){
		return properties==null?null:(String)properties.get(key);
	}
	
	public Properties getProperties(){
		return properties;
	}

	public void setProperties(Properties properties){
		this.properties = properties;
	}
}
