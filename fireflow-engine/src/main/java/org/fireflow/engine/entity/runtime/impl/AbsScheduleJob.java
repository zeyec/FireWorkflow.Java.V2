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
package org.fireflow.engine.entity.runtime.impl;

import java.util.Date;

import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ScheduleJob;
import org.fireflow.engine.entity.runtime.ScheduleJobState;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public abstract class AbsScheduleJob implements ScheduleJob{
	private String id;
	private String name = null;
	private String displayName = null;
	private Date createdTime;
	private Integer triggeredTimes = 0;
	private Date latestTriggeredTime = null;
	
	private String triggerType;
	private String triggerExpression;
	private Date endTime;

	private ScheduleJobState state = ScheduleJobState.RUNNING;
	private ActivityInstance activityInstance;
	private String processId;
	private String processType;
	private Integer version;
	private Boolean createNewProcessInstance = false;
	private Boolean cancelAttachedToActivity=false;
	private String note;
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/**
	 * @return the triggedTimes
	 */
	public Integer getTriggeredTimes() {
		return triggeredTimes;
	}
	/**
	 * @param triggedTimes the triggedTimes to set
	 */
	public void setTriggeredTimes(Integer triggedTimes) {
		this.triggeredTimes = triggedTimes;
	}
	/**
	 * @return the latestTriggedTime
	 */
	public Date getLatestTriggeredTime() {
		return latestTriggeredTime;
	}
	/**
	 * @param latestTriggedTime the latestTriggedTime to set
	 */
	public void setLatestTriggeredTime(Date latestTriggedTime) {
		this.latestTriggeredTime = latestTriggedTime;
	}	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the cron
	 */
	public String getTriggerExpression() {
		return triggerExpression;
	}
	/**
	 * @param cron the cron to set
	 */
	public void setTriggerExpression(String expression) {
		this.triggerExpression = expression;
	}
	/**
	 * @return the state
	 */
	public ScheduleJobState getState() {
		return state;
	}
	
	
	/**
	 * @return the triggerType
	 */
	public String getTriggerType() {
		return triggerType;
	}
	/**
	 * @param triggerType the triggerType to set
	 */
	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(ScheduleJobState state) {
		this.state = state;
	}
	/**
	 * @return the activityInstance
	 */
	public ActivityInstance getActivityInstance() {
		return activityInstance;
	}
	/**
	 * @param activityInstance the activityInstance to set
	 */
	public void setActivityInstance(ActivityInstance activityInstance) {
		this.activityInstance = activityInstance;
	}
	/**
	 * @return the processId
	 */
	public String getProcessId() {
		return processId;
	}
	/**
	 * @param processId the processId to set
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	/**
	 * @return the processType
	 */
	public String getProcessType() {
		return processType;
	}
	/**
	 * @param processType the processType to set
	 */
	public void setProcessType(String processType) {
		this.processType = processType;
	}
	/**
	 * @return the version
	 */
	public Integer getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}
	/**
	 * @return the createNewProcessInstance
	 */
	public Boolean isCreateNewProcessInstance() {
		return createNewProcessInstance;
	}
	/**
	 * @param createNewProcessInstance the createNewProcessInstance to set
	 */
	public void setCreateNewProcessInstance(Boolean createNewProcessInstance) {
		this.createNewProcessInstance = createNewProcessInstance;
	}
	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}
	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}
	/**
	 * @return the createdTime
	 */
	public Date getCreatedTime() {
		return createdTime;
	}
	/**
	 * @param createdTime the createdTime to set
	 */
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}
	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Boolean isCancelAttachedToActivity(){
		return this.cancelAttachedToActivity;
	}
	
	public void setCancelAttachedToActivity(Boolean b){
		this.cancelAttachedToActivity = b;
	}
}
