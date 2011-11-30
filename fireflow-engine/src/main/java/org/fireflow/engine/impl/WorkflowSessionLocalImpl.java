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
package org.fireflow.engine.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.engine.WorkflowQuery;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.WorkflowStatement;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.context.RuntimeContextAware;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.service.AssignmentHandler;


/**
 * @author chennieyun
 * 
 */
public class WorkflowSessionLocalImpl implements WorkflowSession,RuntimeContextAware{

	protected Map<String,Object> attributes = new HashMap<String,Object>();
	protected Map<String,AssignmentHandler> dynamicAssignmentHandlers = new HashMap<String,AssignmentHandler>();
	protected List<WorkItem> latestCreatedWorkItems = new ArrayList<WorkItem>();
	protected RuntimeContext context = null;
	protected User currentUser = null;
	

	public void clearAttributes() {
		this.attributes.clear();
		
	}

	public Object getAttribute(String name) {
		return this.attributes.get(name);
	}

	public Object removeAttribute(String name) {
		return this.attributes.remove(name);
	}

	public WorkflowSession setAttribute(String name, Object attr) {
		this.attributes.put(name, attr);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.api.WorkflowSession#getCurrentUser()
	 */
	public User getCurrentUser() {
		
		return this.currentUser;
	}
	
	public void setCurrentUser(User currentUser){
		this.currentUser  = currentUser;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#createWorkflowQuery(java.lang.Object)
	 */
	public <T extends WorkflowEntity> WorkflowQuery<T> createWorkflowQuery(Class<T> c,String processType) {
		WorkflowQueryImpl<T> query = new WorkflowQueryImpl<T>(this,c,processType);
		return query;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#createWorkflowStatement()
	 */
	public WorkflowStatement createWorkflowStatement(String processType) {
		WorkflowStatementLocalImpl statement = new WorkflowStatementLocalImpl(this);
		statement.setProcessType(processType);
		return statement;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#getCurrentActivityInstance()
	 */
	public ActivityInstance getCurrentActivityInstance() {
		return (ActivityInstance)this.attributes.get(CURRENT_ACTIVITY_INSTANCE);
	}
	
	public void setCurrentActivityInstance(ActivityInstance activityInstance){
		this.setAttribute(CURRENT_ACTIVITY_INSTANCE, activityInstance);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#getCurrentProcessInstance()
	 */
	public ProcessInstance getCurrentProcessInstance() {
		
		return (ProcessInstance)this.attributes.get(CURRENT_PROCESS_INSTANCE);
	}
	
	public void setCurrentProcessInstance(ProcessInstance processInstance){
		this.setAttribute(CURRENT_PROCESS_INSTANCE, processInstance);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#getLatestCreatedWorkItems()
	 */
	public List<WorkItem> getLatestCreatedWorkItems() {
		
		return latestCreatedWorkItems;
	}
	
	public void setLatestCreatedWorkItems(List<WorkItem> workItems){
		if (workItems!=null){
			latestCreatedWorkItems.addAll(workItems);
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#putAllAttributes(java.util.Map)
	 */
	public WorkflowSession setAllAttributes(Map<String, Object> attributes) {
		if (attributes!=null){
			this.attributes.putAll(attributes);
		}
		return this;
	}
    /**
     * @param ctx
     */
    public void setRuntimeContext(RuntimeContext ctx){
    	this.context = ctx;
    }
    
    /**
     * @return
     */
    public RuntimeContext getRuntimeContext(){
    	return this.context;
    }

	/**
	 * 取得活动id等于activityId的动态分配句柄，并从session中将其删除。
	 * @param activityId
	 * @return
	 */
	public AssignmentHandler consumeDynamicAssignmentHandler(String activityId) {
		return dynamicAssignmentHandlers.remove(activityId);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#setDynamicAssignmentHandler(java.lang.String, org.fireflow.engine.service.human.AssignmentHandler)
	 */
	public WorkflowSession setDynamicAssignmentHandler(String activityId,
			AssignmentHandler assignmentHandler) {
		dynamicAssignmentHandlers.put(activityId, assignmentHandler);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#getAttributes()
	 */
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#getDynamicAssignmentHandler()
	 */
	public Map<String, AssignmentHandler> getDynamicAssignmentHandler() {
		return this.dynamicAssignmentHandlers;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#createWorkflowQuery(java.lang.Class)
	 */
	public <T extends WorkflowEntity> WorkflowQuery<T> createWorkflowQuery(Class<T> c) {
		WorkflowQueryImpl<T> query = new WorkflowQueryImpl<T>(this,c,context.getDefaultProcessType());
		return query;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#createWorkflowStatement()
	 */
	public WorkflowStatement createWorkflowStatement() {
		WorkflowStatementLocalImpl statement = new WorkflowStatementLocalImpl(this);
		statement.setProcessType(context.getDefaultProcessType());
		return statement;
	}
}
