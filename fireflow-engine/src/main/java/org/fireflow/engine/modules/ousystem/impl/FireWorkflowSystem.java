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
import org.fireflow.engine.modules.ousystem.User;


/**
 * @author 非也
 * @version 2.0
 */
public class FireWorkflowSystem extends UserImpl implements User {
	private static FireWorkflowSystem fireWorkflowSystem = null;
	public static synchronized FireWorkflowSystem getInstance(){
		if (fireWorkflowSystem==null){
			fireWorkflowSystem = new FireWorkflowSystem();
		}
		return fireWorkflowSystem;
	}
	
	private FireWorkflowSystem(){
		this.properties = new Properties();
		this.properties.setProperty(Actor.ID, "FireWorkflowSystem");
		this.properties.setProperty(Actor.NAME, "Fire Workflow System");
	}
}
