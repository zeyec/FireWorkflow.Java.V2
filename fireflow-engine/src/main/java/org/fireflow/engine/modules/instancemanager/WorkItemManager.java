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
package org.fireflow.engine.modules.instancemanager;

import java.util.List;
import java.util.Map;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.modules.instancemanager.event.EventType;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.model.binding.AssignmentStrategy;


/**
 * @author 非也
 * @version 2.0
 */
public interface WorkItemManager extends EngineModule{
	public static final String TARGET_ACTIVITY_ID = "org.fireflow.constants.TARGET_ACTIVITY_ID";
    /**
     * 根据TaskInstance创建workItem。
     * @param activityInstance
     * @param user
     * @param workitemPropertyValues workitem 属性值，<br/>
     * 只有当workitem的属性值不能从activityInstance,user这两个参数获取时，才尝试从这个参数获取。
     * @return
     * @throws org.fireflow.engine.exception.EngineException
     */
	public WorkItem createWorkItem(WorkflowSession currentSession,
			ProcessInstance processInstance, ActivityInstance activityInstance,
			User user, Map<WorkItemProperty,Object> workitemPropertyValues) throws EngineException;

    /**
	 * 签收WorkItem。
	 * 
	 * @param workItem
	 */
	public WorkItem claimWorkItem(WorkflowSession currentSession,WorkItem workItem);

	/**
	 * 退签收，工单重新回到“工单池”，只有AssignmentStrategy==ANY的WorkItem才可以退签收。
	 * @param currentSession
	 * @param workItem
	 * @return
	 */
	public List<WorkItem> disclaimWorkItem(WorkflowSession currentSession,
			WorkItem workItem)throws InvalidOperationException;

	/**
	 * 结束工作项，
	 * @param currentSession
	 * @param workItem
	 * @throws InvalidOperationException
	 */
	public void completeWorkItem(WorkflowSession currentSession,
			WorkItem workItem)
			throws InvalidOperationException;
	
//	/**
//	 * 结束工作项
//	 * @param currentSession
//	 * @param workItemId
//	 * @param commentSummary
//	 * @param commentDetail
//	 * @param commentId
//	 * @throws InvalidOperationException
//	 */
//	public void completeWorkItem(WorkflowSession currentSession,
//			String workItemId, String commentSummary, String commentDetail,String commentId,String processType)
//			throws InvalidOperationException;	

    /**
     * 结束工单并跳转
     * @param workItem
     * @param targetActivityId
     */
    public void completeWorkItemAndJumpTo(WorkflowSession currentSession,WorkItem workItem,String targetActivityId)throws InvalidOperationException  ;


    /**
     * 撤销刚才执行的Complete动作，系统将创建并返回一个新的Running状态的WorkItem
     * @param workItem
     * @return 新创建的工作项
     * @throws org.fireflow.engine.exception.EngineException
     * @throws org.fireflow.kernel.KernelException
     */
    public WorkItem withdrawWorkItem(WorkflowSession currentSession,WorkItem workItem) throws InvalidOperationException ;

    /**
     * 拒收
     * @param workItem
     * @param comments
     * @throws EngineException
     * @throws KernelException
     */
//    public void rejectWorkItem(WorkItem workItem,String comments) throws  InvalidOperationException ;

    /**
     * 将工作项位派给其他人，自己的工作项变成REASSIGNED 状态。返回新创建的WorkItem.
     * @param workItem 我的WorkItem
     * @return 新创建的工作项
     */
    public List<WorkItem> reassignWorkItemTo(WorkflowSession currentSession,
			WorkItem workItem, List<User> users,String reassignType,AssignmentStrategy assignmentStrategy) ;
    
    
	public void fireWorkItemEvent(WorkflowSession session,WorkItem workItem,EventType eventType);
}
