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
package org.fireflow.service.human;

import java.util.List;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.exception.ServiceExecutionException;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.WorkItemPersister;
import org.fireflow.engine.service.AbsServiceExecutor;
import org.fireflow.engine.service.AssignmentHandler;
import org.fireflow.engine.service.ServiceExecutor;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class HumanServiceExecutor extends AbsServiceExecutor {
	public static final String SERVICE_TYPE = "HUMAN";
	
	private AssignmentHandler assignmentHandler = null;
	public void setAssignmentHandler(AssignmentHandler handler){
		assignmentHandler = handler;
	}
	
	public AssignmentHandler getAssignmentHandler(){
		return assignmentHandler;
	}
	
	public String getServiceType(){
		return SERVICE_TYPE;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.ServiceExecutor#executeService(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance, org.fireflow.model.binding.ServiceBinding, org.fireflow.model.binding.ResourceBinding)
	 */
	public boolean executeService(WorkflowSession session,
			ActivityInstance activityInstance, ServiceBinding serviceBinding,
			ResourceBinding resourceBinding) throws ServiceExecutionException {
		//1、首先检查有无设置DynamicAssignmentHandler
		AssignmentHandler _handler = ((WorkflowSessionLocalImpl)session).consumeDynamicAssignmentHandler(activityInstance.getNodeId());
		if (_handler!=null){
			_handler.assign(session, activityInstance, serviceBinding, resourceBinding);
			return false;
		}

		//TODO 2、然后检查是否是重做，如果是重做则应用重做策略
		
		
		//3、最后使用自带的AssignmentHandler
		this.assignmentHandler.assign(session,  activityInstance, serviceBinding, resourceBinding);
		
		
		//表示是异步调用
		return false;
	}

	
	/**
	 * 默认的规则是：只要activityInstance有活动的workItem则不能够停止。<br/>
	 * 可以定制该方法，以实现“按百分比决定活动是否结束”的业务需求。
	 */
	public int determineActivityCloseStrategy(WorkflowSession session,ActivityInstance activityInstance){
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, activityInstance.getProcessType());
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();

		List<WorkItem> workItems = workItemPersister.findWorkItemsForActivityInstance(activityInstance.getId());
		
		for (WorkItem wi : workItems){
			if (wi.getState().getValue()<WorkItemState.DELIMITER.getValue()){
				return ServiceExecutor.WAITING_FOR_CLOSE;
			}
		}
		return ServiceExecutor.CLOSE_ACTIVITY;
	}

}
