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

import org.fireflow.engine.entity.runtime.ProcessInstance;

/**
 * 流程实例事件
 * @author 非也,nychen2000@163.com
 *
 */
public class ProcessInstanceEvent {
    EventType eventType = null;
    ProcessInstance source = null;

    /**
     * 返回触发事件的流程实例
     * @return
     */
    public ProcessInstance getSource() {
        return source;
    }

    public void setSource(ProcessInstance source) {
        this.source = source;
    }

    /**
     * 返回事件类型，取值为BEFORE_PROCESS_INSTANCE_RUN或者AFTER_PROCESS_INSTANCE_COMPLETE
     * @return
     */
    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
