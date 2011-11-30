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
package org.fireflow.engine;

import java.util.List;
import java.util.Map;

import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.service.AssignmentHandler;

/**
 * WorkflowSession是所有工作流操作的入口，相当于Jdbc的connection对象。
 * 通过WorkflowSession可以创建IProcessInstance，查询ITaskInstance,IWorkItem等等。
 * 该类采用了Template设计模式
 * ，所有的方法最终都是调用IWorkflowSession.execute(IWorkflowSessionCallback callback)实现的。
 * 这样做的好处是，有很多公共的功能都可以放在execute中，不需要每个方法重写一遍。<br>
 * 缺省的WorkflowSession提供的方法不多，您可以利用IWorkflowSession.execute(
 * IWorkflowSessionCallback callback)实现更多的流程操作。
 * 
 * @author 非也,nychen2000@163.com
 * 
 */
public interface WorkflowSession {
	public static final String CURRENT_PROCESS_INSTANCE = "CURRENT_PROCESS_INSTANCE";
	public static final String CURRENT_ACTIVITY_INSTANCE = "CURRENT_ACTIVITY_INSTANCE";
	public static final String LATEST_CREATED_WORKITEMS = "LATEST_CREATED_WORKITEMS";
	public static final String CURRENT_SCOPE = "CURRENT_SCOPE";
	
	
	
	/**
	 * 返回当前连接BPM子系统的用户
	 * @return
	 */
	public User getCurrentUser();
	
	/**
	 * 设置属性
	 * @param name
	 * @param value
	 */
	public WorkflowSession setAttribute(String name,Object value);
	
	/**
	 * 类似HttpServletRequest的getAttribute,用于流程操作中传递参数。
	 * @param name
	 * @return
	 */
	public Object getAttribute(String name);
	
	/**
	 * 清空workflowSession中的所有参数
	 */
	public void clearAttributes();
	
	/**
	 * 清空workflowSession中指定名称的参数，并返回该参数。
	 * @param name
	 */
	public Object removeAttribute(String name);
	
	/**
	 * 
	 * @param attributes
	 */
	public WorkflowSession setAllAttributes(Map<String ,Object> attributes);
	
	/**
	 * 返回当前session中的所有的attributes属性
	 */
	public Map<String,Object> getAttributes();
	
	/**
	 * 获得当前的流程实例
	 * @return
	 */
	public ProcessInstance getCurrentProcessInstance();
	
	/**
	 * 获得当前的活动实例
	 * @return
	 */
	public ActivityInstance getCurrentActivityInstance();
	
	/**
	 * 获得最近一次流程操作所创建的所有的工作项
	 * @return
	 */
	public List<WorkItem> getLatestCreatedWorkItems();	
	
	/**
	 * 创建Statement
	 * @return
	 */
	public WorkflowStatement createWorkflowStatement(String processType);
	
	/**
	 * 创建Statement，使用缺省的流程类别，即"FPDL20";缺省流程类别可以在RuntimeContext中设置。
	 * @return
	 */
	public WorkflowStatement createWorkflowStatement();
	
	
	/**
	 * 创建Query
	 * @param <T> 需要查询的Entity的class类
	 * @param t 流程类别名称，如"FPDL20"。对于Fpdl 2.0，可以用常量FpdlConstants.PROCESS_TYPE。
	 * @return
	 */
	public <T extends WorkflowEntity> WorkflowQuery<T> createWorkflowQuery(Class<T> c,String processType);
	
	/**
	 * 创建Query，使用缺省的流程类别，即"FPDL20";缺省流程类别可以在RuntimeContext中设置。
	 * @param <T> 需要查询的Entity的class类
	 * @param c 
	 * @return
	 */
	public <T extends WorkflowEntity> WorkflowQuery<T> createWorkflowQuery(Class<T> c);
	
	/**
	 * 为活动id等于activityId的实例指定一个动态的工作项分配句柄。
	 * @param activityId
	 * @param assignmentHandler
	 */
	public WorkflowSession setDynamicAssignmentHandler(String activityId,AssignmentHandler assignmentHandler);
	
	/**
	 * 返回当前session中保存的所有的动态工作项分配句柄
	 * @return
	 */
	public Map<String,AssignmentHandler> getDynamicAssignmentHandler();
}
