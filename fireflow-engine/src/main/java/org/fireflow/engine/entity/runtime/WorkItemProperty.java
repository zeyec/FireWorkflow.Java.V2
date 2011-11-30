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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.fireflow.engine.entity.EntityProperty;

/**
 * @author 非也
 * @version 2.0
 */
public enum WorkItemProperty implements EntityProperty {
	ID("id"),
	STATE("state"),
	USER_ID("userId"),
	USER_NAME("userName"),
	USER_DEPT_ID("userDeptId"),
	USER_DEPT_NAME("userDeptName"),
	CREATED_TIME("claimedTime"),
	CLAIMED_TIME("startedTime"),
	END_TIME("endTime"),
	COMMENT_ID("commentId"),
	COMMENT_SUMMARY("commentSummary"),
	COMMENT_DETAIL("commentDetail"),
	RESPONSIBLE_PERSON_ID("responsiblePersonId"),
	RESPONSIBLE_PERSON_NAME("responsiblePersonName"),
	RESPONSIBLE_PERSON_DEPT_ID("responsiblePersonDeptId"),
	RESPONSIBLE_PERSON_DEPT_NAME("responsiblePersonDeptName"),
	PARENT_WORKITEM_ID("parentWorkItemId"),
	REASSIGN_TYPE("reassignType"),
	ASSIGNMENT_STRATEGY("assignmentStrategy"),
	
	ACTIVITY_INSTANCE_$_ID("activityInstance"), 
	ACTIVITY_INSTANCE_$_PROCESSINSTANCE_ID(	"activityInstance.processInstanceId"),
	ACTIVITY_INSTANCE_$_BIZ_ID(	"activityInstance.bizId"), 
	ACTIVITY_INSTANCE_$_ACTIVITY_ID("activityInstance.nodeId"),

	ACTIVITY_INSTANCE_$_SUSPENDED("activityInstance.suspended"),

	ACTIVITY_INSTANCE_$_PROCESSS_ID("activityInstance.processId"),
	ACTIVITY_INSTANCE_$_PROCESS_NAME("activityInstance.processName"),
	ACTIVITY_INSTANCE_$_PROCESS_DISPLAY_NAME("activityInstance.processDisplayName"),
	ACTIVITY_INSTANCE_$_STEP_NUMBER("activityInstance.stepNumber");
	
	;
	
	
	private String propertyName = null;
	private WorkItemProperty(String propertyName){
		this.propertyName = propertyName;
	}
	
	public String getPropertyName(){
		return this.propertyName;
	}
	
	public String getColumnName(){
		return this.name();
	}
	
	public String getDisplayName(Locale locale){
		ResourceBundle resb = ResourceBundle.getBundle("myres", locale);
		return resb.getString(this.name());
	}
	
	public String getDisplayName(){
		return this.getDisplayName(Locale.getDefault());
	}
	
	public List<EntityProperty> getAllProperties(){
		List<EntityProperty> all = new ArrayList<EntityProperty>();
		all.add(ID);
		return all;
	}

}
