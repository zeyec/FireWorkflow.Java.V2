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
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.model.binding.AssignmentStrategy;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public abstract class AbsWorkItem implements WorkItem{
	protected String id = null;

	protected WorkItemState state = WorkItemState.INITIALIZED;
	
	
	protected Date createdTime;
    /**
     * 签收时间
     */
	protected Date claimedTime;
	protected Date endTime;

	protected String ownerId;
	protected String ownerName;
	protected String ownerDeptId;
	protected String ownerDeptName;
	protected String ownerType;
	
	protected String responsiblePersonId;
	protected String responsiblePersonName;
	protected String responsiblePersonOrgId;
	protected String responsiblePersonOrgName;
	
	protected String commentId;
	protected String commentSummary;
	protected String commentDetail;
	
	protected String parentWorkItemId = WorkItem.NO_PARENT_WORKITEM;
	protected String reassignType;
	
	protected ActivityInstance activityInstance;
    private AssignmentStrategy assignmentStrategy = AssignmentStrategy.ASSIGN_TO_ANY;
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
	 * @return the state
	 */
	public WorkItemState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(WorkItemState state) {
		this.state = state;
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
	 * @return the claimedTime
	 */
	public Date getClaimedTime() {
		return claimedTime;
	}

	/**
	 * @param claimedTime the claimedTime to set
	 */
	public void setClaimedTime(Date claimedTime) {
		this.claimedTime = claimedTime;
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
	 * @return the userId
	 */
	public String getOwnerId() {
		return ownerId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setOwnerId(String userId) {
		this.ownerId = userId;
	}

	/**
	 * @return the userName
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setOwnerName(String userName) {
		this.ownerName = userName;
	}

	/**
	 * @return the userOrgId
	 */
	public String getOwnerDeptId() {
		return ownerDeptId;
	}

	/**
	 * @param userOrgId the userOrgId to set
	 */
	public void setOwnerDeptId(String userOrgId) {
		this.ownerDeptId = userOrgId;
	}

	/**
	 * @return the userOrgName
	 */
	public String getOwnerDeptName() {
		return ownerDeptName;
	}

	/**
	 * @param userOrgName the userOrgName to set
	 */
	public void setOwnerDeptName(String userOrgName) {
		this.ownerDeptName = userOrgName;
	}
	
	public String getOwnerType(){
		return this.ownerType;
	}
	
	public void setOwnerType(String ownerType){
		this.ownerType = ownerType;
	}

	/**
	 * @return the responsiblePersonId
	 */
	public String getResponsiblePersonId() {
		return responsiblePersonId;
	}

	/**
	 * @param responsiblePersonId the responsiblePersonId to set
	 */
	public void setResponsiblePersonId(String responsiblePersonId) {
		this.responsiblePersonId = responsiblePersonId;
	}

	/**
	 * @return the responsiblePersonName
	 */
	public String getResponsiblePersonName() {
		return responsiblePersonName;
	}

	/**
	 * @param responsiblePersonName the responsiblePersonName to set
	 */
	public void setResponsiblePersonName(String responsiblePersonName) {
		this.responsiblePersonName = responsiblePersonName;
	}

	/**
	 * @return the responsiblePersonOrgId
	 */
	public String getResponsiblePersonDeptId() {
		return responsiblePersonOrgId;
	}

	/**
	 * @param responsiblePersonOrgId the responsiblePersonOrgId to set
	 */
	public void setResponsiblePersonDeptId(String responsiblePersonOrgId) {
		this.responsiblePersonOrgId = responsiblePersonOrgId;
	}

	/**
	 * @return the responsiblePersonOrgName
	 */
	public String getResponsiblePersonDeptName() {
		return responsiblePersonOrgName;
	}

	/**
	 * @param responsiblePersonOrgName the responsiblePersonOrgName to set
	 */
	public void setResponsiblePersonDeptName(String responsiblePersonOrgName) {
		this.responsiblePersonOrgName = responsiblePersonOrgName;
	}

	/**
	 * @return the commentId
	 */
	public String getCommentId() {
		return commentId;
	}

	/**
	 * @param commentId the commentId to set
	 */
	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	/**
	 * @return the commentSummary
	 */
	public String getCommentSummary() {
		return commentSummary;
	}

	/**
	 * @param commentSummary the commentSummary to set
	 */
	public void setCommentSummary(String commentSummary) {
		this.commentSummary = commentSummary;
	}

	/**
	 * @return the commentDetail
	 */
	public String getCommentDetail() {
		return commentDetail;
	}

	/**
	 * @param commentDetail the commentDetail to set
	 */
	public void setCommentDetail(String commentDetail) {
		this.commentDetail = commentDetail;
	}

	/**
	 * @return the parentWorkItemId
	 */
	public String getParentWorkItemId() {
		return parentWorkItemId;
	}

	/**
	 * @param parentWorkItemId the parentWorkItemId to set
	 */
	public void setParentWorkItemId(String parentWorkItemId) {
		this.parentWorkItemId = parentWorkItemId;
	}

	/**
	 * @return the reassignType
	 */
	public String getReassignType() {
		return reassignType;
	}

	/**
	 * @param reassignType the reassignType to set
	 */
	public void setReassignType(String reassignType) {
		this.reassignType = reassignType;
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
	

	public AssignmentStrategy getAssignmentStrategy() {
		
		return this.assignmentStrategy;
	}
	
	public void setAssignmentStrategy(AssignmentStrategy assignmentStrategy){
		this.assignmentStrategy = assignmentStrategy;
	}
	
	public WorkItem clone(){
		AbsWorkItem wi = null;
		if (this instanceof WorkItemImpl){
			wi = new WorkItemImpl();
		}else if (this instanceof WorkItemHistory){
			wi = new WorkItemHistory();
		}else {
			return null;
		}
		wi.setActivityInstance(activityInstance);
		wi.setAssignmentStrategy(assignmentStrategy);
		wi.setClaimedTime(claimedTime);
		wi.setCommentDetail(commentDetail);
		wi.setCommentId(commentId);
		wi.setCommentSummary(commentSummary);
		wi.setCreatedTime(createdTime);
		wi.setEndTime(endTime);
		wi.setParentWorkItemId(parentWorkItemId);
		wi.setReassignType(reassignType);
		wi.setResponsiblePersonDeptId(responsiblePersonOrgId);
		wi.setResponsiblePersonDeptName(responsiblePersonOrgName);
		wi.setResponsiblePersonId(responsiblePersonId);
		wi.setResponsiblePersonName(responsiblePersonName);
		wi.setState(state);
		wi.setOwnerDeptId(ownerDeptId);
		wi.setOwnerDeptName(ownerDeptName);
		wi.setOwnerId(ownerId);
		wi.setOwnerName(ownerName);
		return wi;
	}
}
