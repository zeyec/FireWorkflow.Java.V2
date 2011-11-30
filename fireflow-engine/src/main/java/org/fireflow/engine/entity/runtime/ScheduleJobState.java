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
package org.fireflow.engine.entity.runtime;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public enum ScheduleJobState {
	/**
	 * 运行状态
	 */
	RUNNING(1),
	
	/**
	 * 调度器被暂停，但是仍然有效
	 * TODO 什么场景用到呢?
	 */
	SUSPENDED(3),
	/**
	 * 活动属性和非活动属性的分界线
	 */
	DELIMITER(10),
	
	/**
	 * 已经结束
	 */
	COMPLETED(11),
	
	/**
	 * 因错误而终止
	 */
	FAULTED(15),
	
	/**
	 * 被取消
	 */
	CANCELLED(16),
	/**
	 * 被中止
	 */
	ABORTED(19),
	/**
	 * 已经失效的，
	 */
	OUT_OF_DATE(98);
	
	private int value = 0;
	private ScheduleJobState(int value){
		this.value = value;
	}
	
	public String getDisplayName(Locale locale){
		ResourceBundle resb = ResourceBundle.getBundle("EngineMessages", locale);
		return resb.getString(this.name());
	}
	
	public String getDisplayName(){
		return this.getDisplayName(Locale.getDefault());
	}
	
	public int getValue(){
		return value;
	}
	
	public static ScheduleJobState valueOf(Integer v){
		ScheduleJobState[] states =  ScheduleJobState.values();
		for (ScheduleJobState state : states){
			if (state.getValue()== v){
				return state;
			}
		}
		return null;
	}	
}
