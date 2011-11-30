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
package org.fireflow.engine.modules.instancemanager.event;

import org.fireflow.engine.exception.EngineException;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public abstract class AbsWorkItemEventListener implements WorkItemEventListener {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.instancemanager.event.WorkItemEventListener#onWorkItemEventFired(org.fireflow.engine.modules.instancemanager.event.WorkItemEvent)
	 */
	final public void onWorkItemEventFired(WorkItemEvent e) {
		EventType type = e.getEventType();
		if (type.equals(EventType.ON_WORKITEM_CREATED)){
			this.onWorkItemCreated(e);
		}
		//暂时不发布这个事件，貌似没有意义，2011-02-06
//		else if (type.equals(EventType.AFTER_WORKITEM_END)){
//			this.afterWorkItemEnd(e);
//		}

	}

	public void onWorkItemCreated(WorkItemEvent e){
		
	}
	
	public void afterWorkItemEnd(WorkItemEvent e){
		
	}
}
