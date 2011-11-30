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
package org.fireflow.engine.modules.instancemanager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.entity.runtime.impl.AbsActivityInstance;
import org.fireflow.engine.entity.runtime.impl.AbsWorkItem;
import org.fireflow.engine.entity.runtime.impl.WorkItemImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.ServiceExecutionException;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.WorkItemManager;
import org.fireflow.engine.modules.instancemanager.event.EventType;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.WorkItemPersister;
import org.fireflow.engine.modules.process.ProcessUtil;
import org.fireflow.engine.service.ServiceExecutor;
import org.fireflow.engine.service.ServiceRegistry;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.AssignmentStrategy;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.servicedef.Service;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public abstract class AbsWorkItemManager  implements WorkItemManager {
	private static Log log = LogFactory.getLog(AbsWorkItemManager.class);
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.form.WorkItemManager#claimWorkItem(java.lang.String, java.lang.String)
	 */
	public WorkItem claimWorkItem(WorkflowSession currentSession,WorkItem workItem){
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		
		ActivityInstance activityInstance = workItem.getActivityInstance();
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, activityInstance.getProcessType());
		CalendarService calendarService = rtCtx.getEngineModule(CalendarService.class, activityInstance.getProcessType());
		
		ActivityInstancePersister activityInstancePersister = persistenceService.getActivityInstancePersister();
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();

		activityInstancePersister.lockActivityInstance(activityInstance.getId());
		


		// 0、首先修改workitem的状态
		((AbsWorkItem) workItem).setState(WorkItemState.RUNNING);
		((AbsWorkItem) workItem).setClaimedTime(calendarService.getSysDate());
		workItemPersister.saveOrUpdate(workItem);

		// 1、如果不是会签，则删除其他的workitem
		if (AssignmentStrategy.ASSIGN_TO_ANY.equals(workItem.getAssignmentStrategy())) {
			workItemPersister.deleteWorkItemsInInitializedState(activityInstance.getId(),workItem.getParentWorkItemId());
		}

		// 2、将TaskInstance的canBeWithdrawn字段改称false。即不允许被撤销
		((AbsActivityInstance)activityInstance).setCanBeWithdrawn(false);
		activityInstancePersister.saveOrUpdate(activityInstance);

		
		return workItem;
	}

	public List<WorkItem> disclaimWorkItem(WorkflowSession currentSession,
			WorkItem workItem)throws InvalidOperationException{
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		ServiceExecutor serviceExecutor = this.getServiceExecutor(rtCtx, workItem);
		ActivityInstance thisActivityInstance = workItem.getActivityInstance();
		if (serviceExecutor==null){
			throw new EngineException(thisActivityInstance,"ServiceExecutor not found!");
		}				
		
		ProcessKey pKey = new ProcessKey(thisActivityInstance.getProcessId(),thisActivityInstance.getVersion(),thisActivityInstance.getProcessType());

		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, thisActivityInstance.getProcessType());
		CalendarService calendarService = rtCtx.getEngineModule(CalendarService.class, thisActivityInstance.getProcessType());
		ProcessUtil processService = rtCtx.getEngineModule(ProcessUtil.class, thisActivityInstance.getProcessType());
		
		ServiceBinding serviceBinding = null;
		try{
			serviceBinding = processService.getServiceBinding(pKey, thisActivityInstance.getNodeId());
		}catch(InvalidModelException e){
			log.error(e);
			throw new InvalidOperationException(e);
		}
		ResourceBinding resourceBinding = null;
		try{
			resourceBinding = processService.getResourceBinding(pKey, thisActivityInstance.getNodeId());
		}catch(InvalidModelException e){
			log.error(e);
			throw new InvalidOperationException(e);
		}
		
		
		
		try {
			serviceExecutor.executeService(currentSession, thisActivityInstance,serviceBinding,resourceBinding);
			((AbsWorkItem)workItem).setState(WorkItemState.DISCLAIMED);
			((AbsWorkItem)workItem).setEndTime(calendarService.getSysDate());
			WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
			workItemPersister.saveOrUpdate(workItem);
			return currentSession.getLatestCreatedWorkItems();
		} catch (ServiceExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new EngineException(thisActivityInstance,e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.form.WorkItemManager#completeWorkItemAndJumpTo(org.fireflow.engine.entity.runtime.WorkItem, java.lang.String, java.lang.String)
	 */
	public void completeWorkItemAndJumpTo(WorkflowSession currentSession,
			WorkItem workItem, String targetActivityId) throws InvalidOperationException{
		
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		ServiceExecutor serviceExecutor = this.getServiceExecutor(rtCtx, workItem);
		ActivityInstance thisActivityInstance = workItem.getActivityInstance();
		if (serviceExecutor==null){
			throw new EngineException(thisActivityInstance,"ServiceExecutor not found!");
		}		

		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, thisActivityInstance.getProcessType());
		CalendarService calendarService = rtCtx.getEngineModule(CalendarService.class,  thisActivityInstance.getProcessType());
		
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
		
		if (workItem.getParentWorkItemId() != null && !workItem.getParentWorkItemId().trim().equals("")
				&& !workItem.getParentWorkItemId().trim().equals(
						WorkItem.NO_PARENT_WORKITEM)) {
			String reassignType = workItem.getReassignType();
			if (reassignType == null || reassignType.trim().equals("")
					|| reassignType.trim().equals(WorkItem.REASSIGN_AFTER_ME)) {//后加签
				AssignmentStrategy assignmentStrategy = workItem.getAssignmentStrategy();
				if (assignmentStrategy != null
						&& !assignmentStrategy.equals(
								AssignmentStrategy.ASSIGN_TO_ANY)) {

					List<WorkItem> workItemsWithSameParent = workItemPersister
							.findWorkItemsForActivityInstance(workItem
									.getActivityInstance().getId(), workItem
									.getParentWorkItemId());
					
					for(WorkItem wiTmp : workItemsWithSameParent){
						if (wiTmp.getState().getValue()<WorkItemState.DELIMITER.getValue()){
							throw new InvalidOperationException("Reassigned workitem can NOT jump to another activity.");
						}
					}
				}
			}
			else{//前加签
				throw new InvalidOperationException("Reassigned workitem can NOT jump to another activity.");
			}
		}

		ProcessInstance thisProcessInstance = thisActivityInstance.getProcessInstance(currentSession);
		
		((WorkflowSessionLocalImpl)currentSession).setCurrentProcessInstance(thisProcessInstance);
		((WorkflowSessionLocalImpl)currentSession).setCurrentActivityInstance(thisActivityInstance);
		

		((AbsWorkItem)workItem).setState(WorkItemState.COMPLETED);
		((AbsWorkItem)workItem).setEndTime(calendarService.getSysDate());
		workItemPersister.saveOrUpdate(workItem);
		
		if (workItem.getParentWorkItemId() == null
				|| workItem.getParentWorkItemId().trim().equals("")
				|| workItem.getParentWorkItemId().trim().equals(
						WorkItem.NO_PARENT_WORKITEM)) {

			currentSession.setAttribute(WorkItemManager.TARGET_ACTIVITY_ID, targetActivityId);
			serviceExecutor.onServiceCompleted(currentSession, thisActivityInstance);
		}

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.form.WorkItemManager#createWorkItem(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ProcessInstance, org.fireflow.engine.entity.runtime.ActivityInstance, java.lang.String)
	 */
	public WorkItem createWorkItem(WorkflowSession currentSession,
			ProcessInstance processInstance, ActivityInstance activityInstance,
			User user, Map<WorkItemProperty,Object> workitemPropertyValues) throws EngineException {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		CalendarService calendarService = ctx.getEngineModule(CalendarService.class, activityInstance.getProcessType());
		PersistenceService persistenceService = ctx.getEngineModule(PersistenceService.class, activityInstance.getProcessType());
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
		
		WorkItemImpl wi = new WorkItemImpl();
		wi.setActivityInstance(activityInstance);
		wi.setCreatedTime(calendarService.getSysDate());
		wi.setResponsiblePersonId(user.getId());
		wi.setResponsiblePersonName(user.getName());
		wi.setResponsiblePersonDeptId(user.getDeptId());
		wi.setResponsiblePersonDeptName(user.getDeptName());
		wi.setState(WorkItemState.INITIALIZED);
		wi.setOwnerId(user.getId());
		wi.setOwnerName(user.getName());
		wi.setOwnerDeptId(user.getDeptId());
		wi.setOwnerDeptName(user.getDeptName());
		
		if (workitemPropertyValues!=null){
			if (workitemPropertyValues.get(WorkItemProperty.STATE)!=null){
				wi.setState((WorkItemState)workitemPropertyValues.get(WorkItemProperty.STATE));
			}
			wi.setAssignmentStrategy((AssignmentStrategy)workitemPropertyValues.get(WorkItemProperty.ASSIGNMENT_STRATEGY));
			wi.setReassignType((String)workitemPropertyValues.get(WorkItemProperty.REASSIGN_TYPE));
			wi.setParentWorkItemId((String)workitemPropertyValues.get(WorkItemProperty.PARENT_WORKITEM_ID));
		}
		
		workItemPersister.saveOrUpdate(wi);
		
		//发布事件
		this.fireWorkItemEvent(currentSession, wi, EventType.ON_WORKITEM_CREATED);
		return wi;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.form.WorkItemManager#reassignWorkItemTo(org.fireflow.engine.entity.runtime.WorkItem, java.lang.String, java.lang.String)
	 */
    public List<WorkItem> reassignWorkItemTo(WorkflowSession currentSession,
			WorkItem workItem, List<User> users,String reassignType,AssignmentStrategy assignmentStrategy){
		if (users==null || users.size()==0)return null;
		

		ActivityInstance thisActivityInstance = workItem.getActivityInstance();
		ProcessInstance thisProcessInstance = thisActivityInstance.getProcessInstance(currentSession);

		((WorkflowSessionLocalImpl)currentSession).setCurrentActivityInstance(thisActivityInstance);
		((WorkflowSessionLocalImpl)currentSession).setCurrentProcessInstance(thisProcessInstance);
		
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, thisActivityInstance.getProcessType());
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
		
		CalendarService calendarService = rtCtx.getEngineModule(CalendarService.class, thisActivityInstance.getProcessType());
		
		List<WorkItem> result = new ArrayList<WorkItem>();
		for (User user : users){
			Map<WorkItemProperty,Object> values = new HashMap<WorkItemProperty,Object>();
			values.put(WorkItemProperty.REASSIGN_TYPE, reassignType);
			values.put(WorkItemProperty.ASSIGNMENT_STRATEGY, assignmentStrategy);
			values.put(WorkItemProperty.PARENT_WORKITEM_ID,workItem.getId());
			
			WorkItem wi = this.createWorkItem(currentSession, thisProcessInstance, thisActivityInstance, user, values);
			
			result .add(wi);
		}
		
		((AbsWorkItem)workItem).setState(WorkItemState.REASSIGNED);
		((AbsWorkItem)workItem).setEndTime(calendarService.getSysDate());
		workItemPersister.saveOrUpdate(workItem);
		
		return result;
	}

//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.service.form.WorkItemManager#rejectWorkItem(org.fireflow.engine.entity.runtime.WorkItem, java.lang.String)
//	 */
//	@Override
//	public void rejectWorkItem(WorkItem workItem, String comments)
//			throws InvalidOperationException {
//		// TODO Auto-generated method stub
//
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.form.WorkItemManager#withdrawWorkItem(org.fireflow.engine.entity.runtime.WorkItem)
	 */
	public WorkItem withdrawWorkItem(WorkflowSession currentSession,WorkItem workItem)
			throws InvalidOperationException {
		
		return null;
	}

	

	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.human.WorkItemManager#completeWorkItem(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.WorkItem, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void completeWorkItem(WorkflowSession currentSession,
			WorkItem workItem) throws InvalidOperationException {
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		ServiceExecutor serviceExecutor = this.getServiceExecutor(rtCtx, workItem);
		ActivityInstance thisActivityInstance = workItem.getActivityInstance();
		if (serviceExecutor==null){
			throw new EngineException(thisActivityInstance,"ServiceExecutor not found!");
		}


		ProcessInstance thisProcessInstance = thisActivityInstance.getProcessInstance(currentSession);
		
		((WorkflowSessionLocalImpl)currentSession).setCurrentProcessInstance(thisProcessInstance);
		((WorkflowSessionLocalImpl)currentSession).setCurrentActivityInstance(thisActivityInstance);
		
		

		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, thisActivityInstance.getProcessType());
		CalendarService calendarService = rtCtx.getEngineModule(CalendarService.class,  thisActivityInstance.getProcessType());
		
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
		
		((AbsWorkItem)workItem).setState(WorkItemState.COMPLETED);
		((AbsWorkItem)workItem).setEndTime(calendarService.getSysDate());
		workItemPersister.saveOrUpdate(workItem);
		
		if (workItem.getParentWorkItemId() == null
				|| workItem.getParentWorkItemId().trim().equals("")
				|| workItem.getParentWorkItemId().trim().equals(
						WorkItem.NO_PARENT_WORKITEM)) {

			
			serviceExecutor.onServiceCompleted(currentSession, thisActivityInstance);
		}else{
			//委派的工作项特殊处理
			String reassignType = workItem.getReassignType();
			if (reassignType == null || reassignType.trim().equals("")
					|| reassignType.trim().equals(WorkItem.REASSIGN_AFTER_ME)) {//后加签
				AssignmentStrategy assignmentStrategy = workItem.getAssignmentStrategy();
				if (assignmentStrategy == null
						|| assignmentStrategy.equals(
								AssignmentStrategy.ASSIGN_TO_ANY)) {
					
					serviceExecutor.onServiceCompleted(currentSession,
							thisActivityInstance);
				} else {
					List<WorkItem> workItemsWithSameParent = workItemPersister
							.findWorkItemsForActivityInstance(workItem
									.getActivityInstance().getId(), workItem
									.getParentWorkItemId());
					
					for(WorkItem wiTmp : workItemsWithSameParent){
						if (wiTmp.getState().getValue()<WorkItemState.DELIMITER.getValue()){
							return;//还有待处理的workitem
						}
					}
					
					serviceExecutor.onServiceCompleted(currentSession,
							thisActivityInstance);
				}
			}
			else{//前加签
				AssignmentStrategy assignmentStrategy = workItem.getAssignmentStrategy();
				if (assignmentStrategy == null
						|| assignmentStrategy.equals(
								AssignmentStrategy.ASSIGN_TO_ANY)) {
					WorkItem parentWorkItem = workItemPersister.find(WorkItem.class, workItem.getParentWorkItemId());
					WorkItem newParentWi = this.cloneWorkItem(parentWorkItem, calendarService);
					workItemPersister.saveOrUpdate(newParentWi);
				} else {
					List<WorkItem> workItemsWithSameParent = workItemPersister
							.findWorkItemsForActivityInstance(workItem
									.getActivityInstance().getId(), workItem
									.getParentWorkItemId());
					
					for(WorkItem wiTmp : workItemsWithSameParent){
						if (wiTmp.getState().getValue()<WorkItemState.DELIMITER.getValue()){
							return;//还有待处理的workitem
						}
					}
					
					WorkItem parentWorkItem = workItemPersister.find(WorkItem.class, workItem.getParentWorkItemId());
					WorkItem newParentWi = this.cloneWorkItem(parentWorkItem, calendarService);
					workItemPersister.saveOrUpdate(newParentWi);
				}
			}
		}
	}
	private WorkItem cloneWorkItem(WorkItem wi,CalendarService calendarService){
		AbsWorkItem tmp = (AbsWorkItem)((AbsWorkItem)wi).clone();
		tmp.setState(WorkItemState.RUNNING);
		tmp.setClaimedTime(calendarService.getSysDate());
		tmp.setEndTime(null);
		tmp.setCommentDetail(null);
		tmp.setCommentId(null);
		tmp.setCommentSummary(null);
		tmp.setCreatedTime(calendarService.getSysDate());
		return tmp;
	}

//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.service.human.WorkItemManager#completeWorkItem(org.fireflow.engine.WorkflowSession, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
//	 */
//	@Override
//	public void completeWorkItem(WorkflowSession currentSession,
//			String workItemId, String commentSummary, String commentDetail,
//			String commentId,String processType) throws InvalidOperationException {
//		
//		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
//		
//		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, processType);
//		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
//
//		WorkItem wi = workItemPersister.find(WorkItem.class, workItemId);
//		
//		this.completeWorkItem(currentSession, wi, commentSummary, commentDetail, commentId);
//		
//	}

	protected ServiceExecutor getServiceExecutor(RuntimeContext runtimeContext,WorkItem workItem){
		ActivityInstance activityInstance = workItem.getActivityInstance();
		String processType = activityInstance.getProcessType();
		
		ProcessUtil processUtil = runtimeContext.getEngineModule(ProcessUtil.class,processType);
		ServiceBinding serviceBinding = null;
		try{
			serviceBinding = processUtil.getServiceBinding(new ProcessKey(activityInstance.getProcessId(),activityInstance.getVersion(),processType), activityInstance.getNodeId());
		}catch(InvalidModelException e){
			log.error(e);
		}
		if (serviceBinding==null)return null;
		
		Service service = serviceBinding.getService();
		if (service==null) return null;
		
		ServiceExecutor serviceExecutor = null;
		String executorName = service.getExecutorName();
		if (executorName!=null && !executorName.trim().equals("")){
			BeanFactory beanFactory = runtimeContext.getEngineModule(BeanFactory.class,processType);
			serviceExecutor = (ServiceExecutor)beanFactory.getBean(executorName);
		}
		if (serviceExecutor==null){
			ServiceRegistry serviceRegistry = runtimeContext.getDefaultEngineModule(ServiceRegistry.class);
			
			String serviceType = service.getServiceType();
			serviceExecutor = serviceRegistry.getServiceExecutor(serviceType);
		}
		return serviceExecutor;
	}
	

}
