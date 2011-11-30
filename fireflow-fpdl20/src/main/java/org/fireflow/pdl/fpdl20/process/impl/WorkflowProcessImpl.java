/**
 * Copyright 2003-2008 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
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
package org.fireflow.pdl.fpdl20.process.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.model.AbstractModelElement;
import org.fireflow.model.ModelElement;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Property;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.process.WorkflowElement;
import org.fireflow.model.process.lifecycle.InstanceCreatorDef;
import org.fireflow.model.process.lifecycle.InstanceExecutorDef;
import org.fireflow.model.process.lifecycle.InstanceTerminatorDef;
import org.fireflow.model.resourcedef.Resource;
import org.fireflow.model.servicedef.Service;
import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.EndNode;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.ProcessImport;
import org.fireflow.pdl.fpdl20.process.Router;
import org.fireflow.pdl.fpdl20.process.StartNode;
import org.fireflow.pdl.fpdl20.process.Transition;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.event.EventListenerDef;

@SuppressWarnings("serial")
public class WorkflowProcessImpl extends AbstractModelElement implements
		WorkflowProcess {
	// ****************首先建立对业务逻辑库和资源库的引用*********************/

	private List<Service> services = new ArrayList<Service>();
	private List<Resource> resources = new ArrayList<Resource>();
	private List<ProcessImport<Resource>> processImportForResources = new ArrayList<ProcessImport<Resource>>();
	private List<ProcessImport<Service>> processImportForServices = new ArrayList<ProcessImport<Service>>();

	private String bizCategory = null;
//	private String fileName = null;

	private Duration duration = null;
	/**
	 * 流程数据项，运行时转换为流程变量进行存储。
	 */
	private List<Property> properties = new ArrayList<Property>();

	private Node entry = null;

	/**
	 * 流程环节
	 */
	private List<Activity> activities = new ArrayList<Activity>();

	/**
	 * 转移
	 */
	private List<Transition> transitions = new ArrayList<Transition>();

	/**
	 * 路由器
	 */
	private List<Router> routers = new ArrayList<Router>();

	/**
	 * 开始节点
	 */
	private List<StartNode> startNodes = new ArrayList<StartNode>();

	/**
	 * 结束节点
	 */
	private List<EndNode> endNodes = new ArrayList<EndNode>();

	/**
	 * 事件监听器注册表
	 */
	private List<EventListenerDef> eventListenerDefs = new ArrayList<EventListenerDef>();

	/**
	 * 所绑定的服务
	 */
	private ServiceBinding serviceRef = null;

	private Map<String, String> extendAttributes = new HashMap<String, String>();

	private InstanceCreatorDef instanceCreatorDef = null;
	private InstanceExecutorDef instanceExecutorDef = null;
	private InstanceTerminatorDef instanceTerminatorDef = null;

	// ********************************************************************
	// ********************************************************************

	/**
	 * 构造函数
	 * 
	 * @param id
	 * @param name
	 * @param pkg
	 */
	public WorkflowProcessImpl(String name) {
		super(null, name);
	}

	/**
	 * 返回所有的流程数据项
	 * 
	 * @return
	 */
	public List<Property> getProperties() {
		return properties;
	}

	/**
	 * 返回所有的环节
	 * 
	 * @return
	 */
	public List<Activity> getActivities() {
		return activities;
	}

	/**
	 * 返回所有的转移
	 * 
	 * @return
	 */
	public List<Transition> getTransitions() {
		return transitions;
	}

	/**
	 * 返回开始节点
	 * 
	 * @return
	 */
	public List<StartNode> getStartNodes() {
		return startNodes;
	}

	/**
	 * 返回所有的结束节点
	 * 
	 * @return
	 */
	public List<EndNode> getEndNodes() {
		return endNodes;
	}

	/**
	 * 通过ID查找该流程中的任意元素
	 * 
	 * @param id
	 *            元素的Id
	 * @return 流程元素，如：Activity,Task,Synchronizer等等
	 */
	public WorkflowElement findWFElementById(String id) {
		if (this.getId().equals(id)) {
			return this;
		}
		int i = 0;
		
		List<ProcessImport<Service>> processImports1 = this.getProcessImportForServices();
		for (ProcessImport<Service> procImport : processImports1){
			if (id.equals(procImport.getId())){
				return procImport;
			}
		}
		
		List<ProcessImport<Resource>> processImports2 = this.getProcessImportForResources();
		for (ProcessImport<Resource> procImport : processImports2){
			if (id.equals(procImport.getId())){
				return procImport;
			}
		}
		
		List<StartNode> startNodes = this.getStartNodes();
		for (StartNode startNode : startNodes){
			if (id.equals(startNode.getId())){
				return startNode;
			}
		}
		
		List<Activity> activityList = this.getActivities();
		for (i = 0; i < activityList.size(); i++) {
			Activity activity = activityList.get(i);
			if (activity.getId().equals(id)) {
				return activity;
			}
		}

		List<Router> routers = this.getRouters();
		for (Router router : routers){
			if (id.equals(router.getId())){
				return router;
			}
		}
		
		List<EndNode> endNodeList = this.getEndNodes();
		for (i = 0; i < endNodeList.size(); i++) {
			EndNode endNode =endNodeList.get(i);
			if (endNode.getId().equals(id)) {
				return endNode;

			}
		}

		List<Transition> transitionList = this.getTransitions();
		for (i = 0; i < transitionList.size(); i++) {
			Transition transition = transitionList.get(i);
			if (transition.getId().equals(id)) {
				return transition;
			}
		}
		return null;

	}

	/**
	 * 通过Id查找任意元素的序列号
	 * 
	 * @param id
	 *            流程元素的id
	 * @return 流程元素的序列号
	 */
	public String findSnById(String id) {
		ModelElement elem = this.findWFElementById(id);
		if (elem != null) {
			return elem.getSn();
		}
		return null;
	}

	/**
	 * 验证workflow process是否完整正确。
	 * 
	 * @return null表示流程正确；否则表示流程错误，返回值是错误原因
	 */
	public String validate() {

		String errHead = "Workflow process is invalid：";
		/*
		 * if (this.getStartNode() == null) { return errHead +
		 * "must have one start node"; } if
		 * (this.getStartNode().getLeavingTransitions().size() == 0) { return
		 * errHead + "start node must have leaving transitions."; }
		 * 
		 * List<ActivityImpl> activities = this.getActivities(); for (int i = 0;
		 * i < activities.size(); i++) { ActivityImpl activity =
		 * activities.get(i); String theName = (activity.getDisplayName() ==
		 * null || activity .getDisplayName().equals("")) ? activity.getName() :
		 * activity.getDisplayName(); if (activity.getEnteringTransition() ==
		 * null) { return errHead + "activity[" + theName +
		 * "] must have entering transition."; } if
		 * (activity.getLeavingTransition() == null) { return errHead +
		 * "activity[" + theName + "] must have leaving transition."; }
		 * 
		 * // check tasks List<AbstractTask> taskList = activity.getTasks(); for
		 * (int j = 0; j < taskList.size(); j++) { AbstractTask task =
		 * taskList.get(j); if (task.getType() == null) { return errHead +
		 * "task[" + task.getId() + "]'s taskType can Not be null."; } else if
		 * (task.getType().equals(org.fireflow.model.service.impl.FORM)) {
		 * FormTask formTask = (FormTask) task; if (formTask.getPerformer() ==
		 * null) { return errHead + "FORM-task[id=" + task.getId() +
		 * "] must has a performer."; } } else if
		 * (task.getType().equals(org.fireflow.model.service.impl.TOOL)) {
		 * ToolTask toolTask = (ToolTask) task; if (toolTask.getApplication() ==
		 * null) { return errHead + "TOOL-task[id=" + task.getId() +
		 * "] must has a application."; } } else if
		 * (task.getType().equals(org.fireflow.model.service.impl.SUBFLOW)) {
		 * SubflowTask subflowTask = (SubflowTask) task; if
		 * (subflowTask.getSubWorkflowProcess() == null) { return errHead +
		 * "SUBFLOW-task[id=" + task.getId() + "] must has a subflow."; } } else
		 * { return errHead + " unknown task type of task[" + task.getId() +
		 * "]"; } } }
		 * 
		 * List<Synchronizer> synchronizers = this.getSynchronizers(); for (int
		 * i = 0; i < synchronizers.size(); i++) { Synchronizer synchronizer =
		 * synchronizers.get(i); String theName = (synchronizer.getDisplayName()
		 * == null || synchronizer .getDisplayName().equals("")) ?
		 * synchronizer.getName() : synchronizer.getDisplayName(); if
		 * (synchronizer.getEnteringTransitions().size() == 0) { return errHead
		 * + "synchronizer[" + theName + "] must have entering transition."; }
		 * if (synchronizer.getLeavingTransitions().size() == 0) { return
		 * errHead + "synchronizer[" + theName +
		 * "] must have leaving transition."; } }
		 * 
		 * List<EndNodeImpl> endnodes = this.getEndNodes(); for (int i = 0; i <
		 * endnodes.size(); i++) { EndNodeImpl endnode = endnodes.get(i); String
		 * theName = (endnode.getDisplayName() == null || endnode
		 * .getDisplayName().equals("")) ? endnode.getName() : endnode
		 * .getDisplayName(); if (endnode.getEnteringTransitions().size() == 0)
		 * { return errHead + "end node[" + theName +
		 * "] must have entering transition."; } }
		 * 
		 * List<TransitionImpl> transitions = this.getTransitions(); for (int i
		 * = 0; i < transitions.size(); i++) { TransitionImpl transition =
		 * transitions.get(i); String theName = (transition.getDisplayName() ==
		 * null || transition .getDisplayName().equals("")) ?
		 * transition.getName() : transition.getDisplayName(); if
		 * (transition.getFromNode() == null) { return errHead + "transition[" +
		 * theName + "] must have from node.";
		 * 
		 * } if (transition.getToNode() == null) { return errHead +
		 * "transition[" + theName + "] must have to node."; } }
		 * 
		 * // check datafield List<DataField> dataFieldList =
		 * this.getDataFields(); for (int i = 0; i < dataFieldList.size(); i++)
		 * { DataField df = dataFieldList.get(i); if (df.getDataType() == null)
		 * { return errHead + "unknown data type of datafield[" + df.getId() +
		 * "]"; } }
		 */
		return null;
	}

	/**
	 * 判断是否可以从from节点到达to节点
	 * 
	 * @param fromNodeId
	 *            from节点的id
	 * @param toNodeId
	 *            to节点的id
	 * @return
	 */
	public boolean isReachable(String fromNodeId, String toNodeId) {
		if (fromNodeId == null || toNodeId == null) {
			return false;
		}
		if (fromNodeId.equals(toNodeId)) {
			return true;
		}
		List<NodeImpl> reachableList = this.getReachableNodes(fromNodeId);

		for (int j = 0; reachableList != null && j < reachableList.size(); j++) {
			NodeImpl node = reachableList.get(j);
			if (node.getId().equals(toNodeId)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 判断两个Activity是否在同一个执行线上
	 * 
	 * @param activityId1
	 * @param activityId2
	 * @return true表示在同一个执行线上，false表示不在同一个执行线上
	 */
	public boolean isInSameLine(String activityId1, String activityId2) {
		NodeImpl node1 = (NodeImpl) this.findWFElementById(activityId1);
		NodeImpl node2 = (NodeImpl) this.findWFElementById(activityId2);
		if (node1 == null || node2 == null)
			return false;
		List<NodeImpl> connectableNodes4Activity1 = new ArrayList<NodeImpl>();
		connectableNodes4Activity1.add(node1);
		connectableNodes4Activity1.addAll(getReachableNodes(activityId1));
		connectableNodes4Activity1.addAll(getEnterableNodes(activityId1));

		List<NodeImpl> connectableNodes4Activity2 = new ArrayList<NodeImpl>();
		connectableNodes4Activity2.add(node2);
		connectableNodes4Activity2.addAll(getReachableNodes(activityId2));
		connectableNodes4Activity2.addAll(getEnterableNodes(activityId2));
		/*
		 * System.out.println("===Inside WorkflowProcess.isInSameLine()::connectableNodes4Activity1.size()="
		 * +connectableNodes4Activity1.size());System.out.println(
		 * "===Inside WorkflowProcess.isInSameLine()::connectableNodes4Activity2.size()="
		 * +connectableNodes4Activity2.size());
		 * System.out.println("-----------------------activity1--------------");
		 * for (int i=0;i<connectableNodes4Activity1.size();i++){ Node node =
		 * (Node)connectableNodes4Activity1.get(i);
		 * System.out.println("node.id of act1 is "+node.getId()); }
		 * 
		 * 
		 * 
		 * System.out.println("---------------------activity2--------------------"
		 * ); for (int i=0;i<connectableNodes4Activity2.size();i++){ Node node =
		 * (Node)connectableNodes4Activity2.get(i);
		 * System.out.println("node.id of act2 is "+node.getId()); }
		 */

		if (connectableNodes4Activity1.size() != connectableNodes4Activity2
				.size()) {
			return false;
		}

		for (int i = 0; i < connectableNodes4Activity1.size(); i++) {
			NodeImpl node = connectableNodes4Activity1.get(i);
			boolean find = false;
			for (int j = 0; j < connectableNodes4Activity2.size(); j++) {
				NodeImpl tmpNode = connectableNodes4Activity2.get(j);
				if (node.getId().equals(tmpNode.getId())) {
					find = true;
					break;
				}
			}
			if (!find)
				return false;
		}
		return true;
	}

	//在计算可到达节点和可进入节点时，默认节点本身不会形成环路，即节点不能同时是一条连线的输入又是这一条连线的输出
	
	/**
	 * 获取可以到达的节点,相比较于fireflow1.0，2.0中activity和activty之间可以直接有连线，那么应当重写该方法
	 * 
	 * @param nodeId
	 * @return
	 */
	public List<NodeImpl> getReachableNodes(String nodeId) {
		List<NodeImpl> reachableNodes = new ArrayList<NodeImpl>();
		NodeImpl location = (NodeImpl)findWFElementById(nodeId);
		//先把自身添加到可到达节点列表
		reachableNodes.add(location);
		List<Transition> outLines = location.getLeavingTransitions();
		for(Transition outLine:outLines){
			Node nextNode = outLine.getToNode();
			if(!reachableNodes.contains(nextNode)){
				reachableNodes.add((NodeImpl)nextNode);
				for(Transition nextNodeOutLine:nextNode.getLeavingTransitions()){
					//防止连线形成环
					if(!reachableNodes.contains(nextNodeOutLine.getToNode())){
						getReachableNodes(nextNodeOutLine.getToNode().getId());
					}
				}
			}
		}
		return reachableNodes;
		//-----------------------------------------------------------------
		/*
		 * List<NodeImpl> reachableNodesList = new ArrayList<NodeImpl>();
		 * NodeImpl node = (NodeImpl) this.findWFElementById(nodeId); if (node
		 * instanceof ActivityImpl) { ActivityImpl activity = (ActivityImpl)
		 * node; TransitionImpl leavingTransition =
		 * activity.getLeavingTransition(); if (leavingTransition != null) {
		 * NodeImpl toNode = leavingTransition.getToNode(); if (toNode != null)
		 * { reachableNodesList.add(toNode);
		 * reachableNodesList.addAll(getReachableNodes(toNode.getId())); } } }
		 * else if (node instanceof Synchronizer) { Synchronizer synchronizer =
		 * (Synchronizer) node; List<TransitionImpl> leavingTransitions =
		 * synchronizer.getLeavingTransitions(); for (int i = 0;
		 * leavingTransitions != null && i < leavingTransitions.size(); i++) {
		 * TransitionImpl leavingTransition = leavingTransitions.get(i); if
		 * (leavingTransition != null) { NodeImpl toNode = (NodeImpl)
		 * leavingTransition.getToNode(); if (toNode != null) {
		 * reachableNodesList.add(toNode);
		 * reachableNodesList.addAll(getReachableNodes(toNode.getId())); }
		 * 
		 * } } } //剔除重复节点 List<NodeImpl> tmp = new ArrayList<NodeImpl>();
		 * boolean alreadyInTheList = false; for (int i = 0; i <
		 * reachableNodesList.size(); i++) { NodeImpl nodeTmp =
		 * reachableNodesList.get(i); alreadyInTheList = false; for (int j = 0;
		 * j < tmp.size(); j++) { NodeImpl nodeTmp2 = tmp.get(j); if
		 * (nodeTmp2.getId().equals(nodeTmp.getId())) { alreadyInTheList = true;
		 * break; } } if (!alreadyInTheList) { tmp.add(nodeTmp); } }
		 * reachableNodesList = tmp; return reachableNodesList;
		 */
//		return null;
	}

	/**
	 * 获取进入的节点(activity 或者synchronizer)
	 * 
	 * @param nodeId
	 * @return
	 */
	public List<NodeImpl> getEnterableNodes(String nodeId) {
		List<NodeImpl> enterableNodes = new ArrayList<NodeImpl>();
		NodeImpl location = (NodeImpl)findWFElementById(nodeId);
		//先把自身加入到可进入节点列表
		enterableNodes.add(location);
		List<Transition> inLines = location.getEnteringTransitions();
		for(Transition inLine:inLines){
			Node preNode = inLine.getFromNode();
			if(!enterableNodes.contains(preNode)){
				enterableNodes.add((NodeImpl)preNode);
				for(Transition preNodeInLine:preNode.getEnteringTransitions()){
					//防止连线形成环
					if(!enterableNodes.contains(preNodeInLine.getFromNode())){
						getEndNode(preNodeInLine.getFromNode().getId());
					}
				}
			}
		}
		return enterableNodes;
		//-----------------------------------------------------
		/*
		 * List<NodeImpl> enterableNodesList = new ArrayList<NodeImpl>();
		 * NodeImpl node = (NodeImpl) this.findWFElementById(nodeId); if (node
		 * instanceof ActivityImpl) { ActivityImpl activity = (ActivityImpl)
		 * node; TransitionImpl enteringTransition =
		 * activity.getEnteringTransition(); if (enteringTransition != null) {
		 * NodeImpl fromNode = enteringTransition.getFromNode(); if (fromNode !=
		 * null) { enterableNodesList.add(fromNode);
		 * enterableNodesList.addAll(getEnterableNodes(fromNode.getId())); } } }
		 * else if (node instanceof Synchronizer) { Synchronizer synchronizer =
		 * (Synchronizer) node; List<TransitionImpl> enteringTransitions =
		 * synchronizer.getEnteringTransitions(); for (int i = 0;
		 * enteringTransitions != null && i < enteringTransitions.size(); i++) {
		 * TransitionImpl enteringTransition = enteringTransitions.get(i); if
		 * (enteringTransition != null) { NodeImpl fromNode =
		 * enteringTransition.getFromNode(); if (fromNode != null) {
		 * enterableNodesList.add(fromNode);
		 * enterableNodesList.addAll(getEnterableNodes(fromNode.getId())); }
		 * 
		 * } } }
		 * 
		 * //剔除重复节点 //TODO mingjie.mj 20091018 改为使用集合是否更好? List<NodeImpl> tmp =
		 * new ArrayList<NodeImpl>(); boolean alreadyInTheList = false; for (int
		 * i = 0; i < enterableNodesList.size(); i++) { NodeImpl nodeTmp =
		 * enterableNodesList.get(i); alreadyInTheList = false; for (int j = 0;
		 * j < tmp.size(); j++) { NodeImpl nodeTmp2 = tmp.get(j); if
		 * (nodeTmp2.getId().equals(nodeTmp.getId())) { alreadyInTheList = true;
		 * break; } } if (!alreadyInTheList) { tmp.add(nodeTmp); } }
		 * enterableNodesList = tmp; return enterableNodesList;
		 */
//		return null;
	}


	public Node getEntry() {
		return this.entry;
	}


	public List<Router> getRouters() {
		return this.routers;
	}

	public void setEntry(Node start) {
		this.entry = start;
	}


	public List<EventListenerDef> getEventListeners() {
		return eventListenerDefs;
	}

//	@Override
//	public ServiceBinding getServiceRef() {
//		return serviceRef;
//	}
//
//	@Override
//	public void setServiceRef(ServiceBinding serviceRef) {
//		this.serviceRef = serviceRef;
//	}


	public Map<String, String> getExtendedAttributes() {
		return extendAttributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pdl.fpdl20.process.WorkflowProcess#getActivity(java.lang
	 * .String)
	 */
	public Activity getActivity(String activityId) {
		if (this.activities != null) {
			for (Activity elm : this.activities) {
				if (elm.getId().equals(activityId)) {
					return elm;
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pdl.fpdl20.process.WorkflowProcess#getEndNode(java.lang.
	 * String)
	 */
	public EndNode getEndNode(String endNodeId) {
		if (this.endNodes != null) {
			for (EndNode elm : this.endNodes) {
				if (elm.getId().equals(endNodeId)) {
					return elm;
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pdl.fpdl20.process.WorkflowProcess#getRouter(java.lang.String
	 * )
	 */
	public Router getRouter(String routerId) {
		if (this.routers != null) {
			for (Router elm : this.routers) {
				if (elm.getId().equals(routerId)) {
					return elm;
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pdl.fpdl20.process.WorkflowProcess#getStartNode(java.lang
	 * .String)
	 */
	public StartNode getStartNode(String startNodeId) {
		if (this.startNodes != null) {
			for (StartNode startNode : this.startNodes) {
				if (startNode.getId().equals(startNodeId)) {
					return startNode;
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pdl.fpdl20.process.WorkflowProcess#getTransition(java.lang
	 * .String)
	 */
	public Transition getTransition(String transitionId) {
		if (this.transitions != null) {
			for (Transition transition : this.transitions) {
				if (transition.getId().equals(transitionId)) {
					return transition;
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.pdl.fpdl20.process.WorkflowProcess#getDuration()
	 */
	public Duration getDuration() {

		return this.duration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pdl.fpdl20.process.WorkflowProcess#setDuration(org.fireflow
	 * .model.misc.Duration)
	 */
	public void setDuration(Duration du) {
		this.duration = du;

	}
	
	public String getBizCategory(){
		return bizCategory;
	}
	
	public void setBizCategory(String bizCategory){
		this.bizCategory = bizCategory;
	}	

//	/**
//	 * @return the fileName
//	 */
//	public String getFileName() {
//		return fileName;
//	}
//
//	/**
//	 * @param fileName
//	 *            the fileName to set
//	 */
//	public void setFileName(String fileName) {
//		this.fileName = fileName;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pdl.fpdl20.process.WorkflowProcess#getResource(java.lang
	 * .String)
	 */
	public Resource getResource(String resourceId) {
		if (resourceId == null || resourceId.trim().equals(""))
			return null;
		List<Resource> allResource = this.getResources();
		for (Resource resource : allResource) {
			if (resourceId.equals(resource.getId())) {
				return resource;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.pdl.fpdl20.process.WorkflowProcess#getResources()
	 */
	public List<Resource> getResources() {
		PrivateList<Resource> privateList = new PrivateList<Resource>();
		privateList.privateAddAll(this.resources);
		// 将import进来的service也加入到列表中
		for (ProcessImport<Resource> processImport : this.processImportForResources) {
			List<Resource> content = (List<Resource>) processImport
					.getContents();
			privateList.privateAddAll(content);
		}
		return privateList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pdl.fpdl20.process.WorkflowProcess#getService(java.lang.
	 * String)
	 */
	public Service getService(String serviceId) {
		if (serviceId == null || serviceId.trim().equals(""))
			return null;
		List<Service> allServices = this.getServices();
		for (Service service : allServices) {
			if (serviceId.equals(service.getId())) {
				return service;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.pdl.fpdl20.process.WorkflowProcess#getServices()
	 */
	public List<Service> getServices() {
		PrivateList<Service> privateList = new PrivateList<Service>();
		privateList.privateAddAll(services);
		// 将import进来的service也加入到列表中
		for (ProcessImport<Service> processImport : this.processImportForServices) {
			List<Service> content = (List<Service>) processImport.getContents();
			privateList.privateAddAll(content);
		}
		return privateList;
	}

	public List<Service> getLocalServices() {
		return this.services;
	}

	public List<Resource> getLocalResources() {
		return this.resources;
	}


	@SuppressWarnings("unchecked")
	public ProcessImport getProcessImportByLocation(String location) {
		if (location == null || location.trim().equals(""))
			return null;
		for (ProcessImport processImport : processImportForResources) {
			if (processImport.getLocation().equals(location)) {
				return processImport;
			}
		}
		for (ProcessImport processImport : this.processImportForServices){
			if (processImport.getLocation().equals(location)){
				return processImport;
			}
		}
		return null;
	}

	public List<ProcessImport<Service>> getProcessImportForServices() {
		return this.processImportForServices;
	}

	public List<ProcessImport<Resource>> getProcessImportForResources() {
		return this.processImportForResources;
	}

}

class PrivateList<T> extends ArrayList<T> {
	public boolean add(T element) {
		throw new UnsupportedOperationException(
				"Can not add element  to this List.");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#add(int, java.lang.Object)
	 */
	@Override
	public void add(int arg0, T arg1) {
		throw new UnsupportedOperationException(
				"Can not add element  to this List.");
	}

	protected void privateAddAll(Collection<? extends T> arg0) {
		super.addAll(arg0);
	}
	
	protected boolean privateAdd(T arg0){
		return super.add(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends T> arg0) {
		throw new UnsupportedOperationException(
				"Can not add element  to this List.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll(int arg0, Collection<? extends T> arg1) {
		throw new UnsupportedOperationException(
				"Can not add element  to this List.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#clear()
	 */
	@Override
	public void clear() {
		throw new UnsupportedOperationException("Can not clear this List.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#remove(int)
	 */
	@Override
	public T remove(int arg0) {
		throw new UnsupportedOperationException(
				"Can not remove element from this List.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException(
				"Can not remove element from this List.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#removeRange(int, int)
	 */
	@Override
	protected void removeRange(int arg0, int arg1) {
		throw new UnsupportedOperationException(
				"Can not remove element from this List.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#set(int, java.lang.Object)
	 */
	@Override
	public T set(int arg0, T arg1) {
		throw new UnsupportedOperationException(
				"Can not add element  to this List.");
	}


}
