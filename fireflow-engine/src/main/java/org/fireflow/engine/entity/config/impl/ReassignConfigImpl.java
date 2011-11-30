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
package org.fireflow.engine.entity.config.impl;

import java.util.Date;

import org.fireflow.engine.entity.config.ReassignConfig;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ReassignConfigImpl implements ReassignConfig{
	protected String id;
	protected String processId;
	protected String processName;
	protected String processDisplayName;
	protected String processType;
	protected String activityId;
	protected String activityName ;
	protected String activityDisplayName;
	
	protected String grantorId;
	protected String grantorName;
	protected String grantorDeptId;
	protected String grantorDeptName;
	
	protected String agentId;
	protected String agentName;
	protected String agentType;
	
	protected Date startTime;
	protected Date endTime;
	
	protected Boolean alive;

	
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
	 * @return the processName
	 */
	public String getProcessName() {
		return processName;
	}

	/**
	 * @param processName the processName to set
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	/**
	 * @return the processDisplayName
	 */
	public String getProcessDisplayName() {
		return processDisplayName;
	}

	/**
	 * @param processDisplayName the processDisplayName to set
	 */
	public void setProcessDisplayName(String processDisplayName) {
		this.processDisplayName = processDisplayName;
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
	 * @return the activityId
	 */
	public String getActivityId() {
		return activityId;
	}

	/**
	 * @param activityId the activityId to set
	 */
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	/**
	 * @return the activityName
	 */
	public String getActivityName() {
		return activityName;
	}

	/**
	 * @param activityName the activityName to set
	 */
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	/**
	 * @return the activityDisplayName
	 */
	public String getActivityDisplayName() {
		return activityDisplayName;
	}

	/**
	 * @param activityDisplayName the activityDisplayName to set
	 */
	public void setActivityDisplayName(String activityDisplayName) {
		this.activityDisplayName = activityDisplayName;
	}

	/**
	 * @return the grantorId
	 */
	public String getGrantorId() {
		return grantorId;
	}

	/**
	 * @param grantorId the grantorId to set
	 */
	public void setGrantorId(String grantorId) {
		this.grantorId = grantorId;
	}

	/**
	 * @return the grantorName
	 */
	public String getGrantorName() {
		return grantorName;
	}

	/**
	 * @param grantorName the grantorName to set
	 */
	public void setGrantorName(String grantorName) {
		this.grantorName = grantorName;
	}

	/**
	 * @return the grantorDeptId
	 */
	public String getGrantorDeptId() {
		return grantorDeptId;
	}

	/**
	 * @param grantorDeptId the grantorDeptId to set
	 */
	public void setGrantorDeptId(String grantorDeptId) {
		this.grantorDeptId = grantorDeptId;
	}

	/**
	 * @return the grantorDeptName
	 */
	public String getGrantorDeptName() {
		return grantorDeptName;
	}

	/**
	 * @param grantorDeptName the grantorDeptName to set
	 */
	public void setGrantorDeptName(String grantorDeptName) {
		this.grantorDeptName = grantorDeptName;
	}

	/**
	 * @return the agentId
	 */
	public String getAgentId() {
		return agentId;
	}

	/**
	 * @param agentId the agentId to set
	 */
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	/**
	 * @return the agentName
	 */
	public String getAgentName() {
		return agentName;
	}

	/**
	 * @param agentName the agentName to set
	 */
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	/**
	 * @return the agentType
	 */
	public String getAgentType() {
		return agentType;
	}

	/**
	 * @param agentType the agentType to set
	 */
	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
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

	/**
	 * @return the alive
	 */
	public Boolean getAlive() {
		return alive;
	}

	/**
	 * @param alive the alive to set
	 */
	public void setAlive(Boolean alive) {
		this.alive = alive;
	}
	
	
}
