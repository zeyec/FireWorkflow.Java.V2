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
package org.fireflow.service.human.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.instancemanager.WorkItemManager;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.service.AssignmentHandler;
import org.fireflow.model.binding.AssignmentStrategy;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class DynamicAssignmentHandler implements AssignmentHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 675889705207600732L;

	private List<User> potentialOwners = null;
	
	private List<User> readers = null;
	
	private AssignmentStrategy assignmentStrategy = null;
	
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.human.AssignmentHandler#assign(org.fireflow.engine.WorkflowSession, org.fireflow.engine.service.human.WorkItemManager, org.fireflow.engine.entity.runtime.ActivityInstance, org.fireflow.model.binding.ServiceBinding, org.fireflow.model.binding.ResourceBinding)
	 */
	public List<WorkItem> assign(WorkflowSession session, ActivityInstance activityInstance,
			ServiceBinding serviceBinding, ResourceBinding resourceBinding)
			throws EngineException {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		WorkItemManager workItemManager = ctx.getEngineModule(WorkItemManager.class, activityInstance.getProcessType());

		ProcessInstance currentProcessInstance = session.getCurrentProcessInstance();
		List<WorkItem> result = new ArrayList<WorkItem>();
		
		
		Map<WorkItemProperty,Object> values = new HashMap<WorkItemProperty,Object>();
		if (this.assignmentStrategy==null){
			values.put(WorkItemProperty.ASSIGNMENT_STRATEGY, resourceBinding.getAssignmentStrategy());
		}else {
			values.put(WorkItemProperty.ASSIGNMENT_STRATEGY, this.assignmentStrategy);
		}
		
		
		for (User user : potentialOwners) {
			WorkItem wi = workItemManager.createWorkItem(session,
					currentProcessInstance, activityInstance, user, values);
			result.add(wi);
		}		
		
		if (readers != null && readers.size() > 0) {
			values.clear();
			values.put(WorkItemProperty.STATE, WorkItemState.READONLY);
			for (User user : readers) {
				WorkItem wi = workItemManager.createWorkItem(session,
						currentProcessInstance, activityInstance, user, values);

				result.add(wi);
			}
		}
		return result;
	}

	/**
	 * 获得潜在的工作参与者列表
	 * @return
	 */
	public List<User> getPotentialOwners(){
		return this.potentialOwners;
	}
	
	public void setPotentialOwners(List<User> owners){
		this.potentialOwners = owners;
	}
	
	/**
	 * 获得抄送人列表
	 * @return
	 */
	public List<User> getReaders(){
		return this.readers;
	}
	
	public void setReaders(List<User> users){
		this.readers = users;
	}

	/**
	 * @return the assignmentStrategy
	 */
	public AssignmentStrategy getAssignmentStrategy() {
		return assignmentStrategy;
	}

	/**
	 * @param assignmentStrategy the assignmentStrategy to set
	 */
	public void setAssignmentStrategy(AssignmentStrategy assignmentStrategy) {
		this.assignmentStrategy = assignmentStrategy;
	}
	
	
}
