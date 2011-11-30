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
import java.util.Map;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.WorkflowStatement;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;


/**
 * @author 非也
 * @version 2.0
 */
public abstract class AbsActivityInstance implements ActivityInstance {

    private String id = null;
    private String name = null;
    private String displayName = null;
    private String nodeId = null;
    
    private String processId = null;
    private Integer version = null;
    private String processType = null;   
    private String processName = null;
    private String processDisplayName = null;
    private String bizCategory = null;
    private String serviceId = null;
    private String serviceType = null;
    
    private String bizId = null;
    private String subBizId = null;

    private ActivityInstanceState state = ActivityInstanceState.INITIALIZED;
    private Boolean suspended = Boolean.FALSE;
    private Date createdTime = null;
    private Date startedTime = null;
    private Date expiredTime = null;
    private Date endTime = null;
    


    private String processInstanceId = null;
    private String parentScopeId = null;
    private String tokenId = null;
    private Integer stepNumber = null;
    

    private String targetActivityId = null;
    private String fromActivityId = null;
    private Boolean canBeWithdrawn = true;

    private String note = null;
    



//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getActivity(org.fireflow.engine.WorkflowSession)
//	 */
//	@Override
//	public Object getActivity(WorkflowSession session) throws EngineException {
//
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getActivityId()
	 */
	public String getNodeId() {
		
		return this.nodeId;
	}
	
	public void setNodeId(String nodeid){
		this.nodeId = nodeid;
	}



	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getBizId()
	 */
	public String getBizId() {
		return this.bizId;
	}
	
	public void setBizId(String bizId){
		this.bizId = bizId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getCreatedTime()
	 */
	public Date getCreatedTime() {
		return this.createdTime;
	}
	
	public void setCreatedTime(Date createdTime){
		this.createdTime = createdTime;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getDisplayName()
	 */
	public String getDisplayName() {
		
		return this.displayName;
	}
	
	public void setDisplayName(String dispName){
		this.displayName = dispName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getEndTime()
	 */
	public Date getEndTime() {
		
		return this.endTime;
	}
	
	public void setEndTime(Date endTime){
		this.endTime = endTime;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getExpiredTime()
	 */
	public Date getExpiredTime() {
		
		return this.expiredTime;
	}
	
	public void setExpiredTime(Date expiredTime){
		this.expiredTime = expiredTime;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getId()
	 */
	public String getId() {
		
		return this.id;
	}
	public void setId(String id){
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getName()
	 */
	public String getName() {
		
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getNote()
	 */
	public String getNote() {
		
		return this.note;
	}
	
	public void setNote(String note){
		this.note = note;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getProcessId()
	 */
	public String getProcessId() {
		
		return this.processId;
	}
	
	public void setProcessId(String processId){
		this.processId = processId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getProcessInstance(org.fireflow.engine.WorkflowSession)
	 */
	public ProcessInstance getProcessInstance(WorkflowSession session) {
		WorkflowStatement statement = session.createWorkflowStatement(this.getProcessType());
		return statement.getEntity(this.getProcessInstanceId(), ProcessInstance.class);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getProcessInstanceId()
	 */
	public String getProcessInstanceId() {
		
		return this.processInstanceId;
	}
	
	public void setProcessInstanceId(String processInstanceId){
		this.processInstanceId = processInstanceId;
	}
	
	public String getProcessType(){
		return this.processType;
	}
	
	public void setProcessType(String processType){
		this.processType = processType;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getServiceId()
	 */
	public String getServiceId() {
		
		return this.serviceId;
	}

	public void setServiceId(String serviceId){
		this.serviceId = serviceId;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getServiceType()
	 */
	public String getServiceType() {
		
		return this.serviceType;
	}
	
	public void setServiceType(String serviceType){
		this.serviceType = serviceType;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getStartedTime()
	 */
	public Date getStartedTime() {
		return this.startedTime;
	}
	
	public void setStartedTime(Date startedTime){
		this.startedTime = startedTime;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getState()
	 */
	public ActivityInstanceState getState() {
		return this.state;
	}
	
	public void setState(ActivityInstanceState state){
		this.state = state;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getStepNumber()
	 */
	public Integer getStepNumber() {
		return this.stepNumber;
	}
	
	public void setStepNumber(Integer i){
		this.stepNumber = i;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getSubBizId()
	 */
	public String getSubBizId() {
		return this.subBizId;
	}
	
	public void setSubBizId(String s){
		this.subBizId = s;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getTargetActivityId()
	 */
	public String getTargetActivityId() {
		return this.targetActivityId;
	}
	
	public void setTargetActivityId(String s){
		this.targetActivityId = s;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getVersion()
	 */
	public Integer getVersion() {
		
		return this.version;
	}
	
	public void setVersion(Integer v){
		this.version = v;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getWorkflowProcess(org.fireflow.engine.WorkflowSession)
	 */
	public Object getWorkflowProcess(WorkflowSession session)
	throws InvalidModelException{
		WorkflowStatement stmt = session.createWorkflowStatement(this.getProcessType());
		ProcessKey pk = new ProcessKey(this.processId,this.version,this.processType);
		return stmt.getWorkflowProcess(pk);
	}
	


	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#isSuspended()
	 */
	public Boolean isSuspended() {
		
		return this.suspended;
	}
	
	public void setSuspended(Boolean b){
		this.suspended = b;
	}

	public String getScopeId(){
		return this.id;
	}
	public String getParentScopeId(){
		return this.parentScopeId;
	}
	
	public void setParentScopeId(String pscopeId){
		this.parentScopeId = pscopeId;
	}
	
	public Object getVariableValue(WorkflowSession session,String name){
		WorkflowStatement stmt = session.createWorkflowStatement(this.getProcessType());
		return stmt.getVariableValue(this, name);
	}
	public void setVariableValue(WorkflowSession session ,String name ,Object value)throws InvalidOperationException{
		WorkflowStatement stmt = session.createWorkflowStatement(this.getProcessType());
		stmt.setVariableValue(this, name,value);
	}
	public Map<String,Object> getVariableValues(WorkflowSession session){
		WorkflowStatement stmt = session.createWorkflowStatement(this.getProcessType());
		return stmt.getVariableValues(this);
	}	
	
	public void setTokenId(String tokenId){
		this.tokenId = tokenId;
	}
	
	public String getTokenId(){
		return this.tokenId;
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
	 * @return the canBeWithdrawn
	 */
	public Boolean getCanBeWithdrawn() {
		return canBeWithdrawn;
	}

	/**
	 * @param canBeWithdrawn the canBeWithdrawn to set
	 */
	public void setCanBeWithdrawn(Boolean canBeWithdrawn) {
		this.canBeWithdrawn = canBeWithdrawn;
	}

	public String getBizCategory() {
		return bizCategory;
	}

	public void setBizCategory(String bizCategory) {
		this.bizCategory = bizCategory;
	}
	
	
}
