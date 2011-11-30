/**
 * Copyright 2007-2008 非也
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
package org.fireflow.engine.modules.instancemanager.event;

import org.fireflow.engine.entity.runtime.ActivityInstance;

/**
 *
 * @author chennieyun
 */
public class ActivityInstanceEvent {

	private ActivityInstance source = null;
	private EventType eventType = null;
	
	public ActivityInstance getSource(){
		return source;
	}
	
	public void setSource(ActivityInstance activityInstance){
		this.source = activityInstance;
	}
	
	public EventType getEventType(){
		return eventType;
	}
	
	public void setEventType(EventType eventType){
		this.eventType = eventType;
	}
}
