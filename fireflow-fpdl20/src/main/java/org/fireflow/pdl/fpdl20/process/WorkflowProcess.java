package org.fireflow.pdl.fpdl20.process;

import java.util.List;

import org.fireflow.model.data.Property;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.process.WorkflowElement;
import org.fireflow.model.resourcedef.Resource;
import org.fireflow.model.servicedef.Service;
import org.fireflow.pdl.fpdl20.process.event.EventListenerDef;


/**
 * 业务流程。<br/>
 * 这是Fire workflow工作流模型的最顶层元素。
 * <br/>
 * 从2.0版本中开始，这个类从org.fireflow.model.WorkflowProcess重构为
 * org.fireflow.model.process.Process。
 * <br/>
 * @author 非也,nychen2000@163.com
 * 
 */
public interface WorkflowProcess extends WorkflowElement{
	public Duration getDuration();
	public void setDuration(Duration du);
	/**
	 * 获得流程变量声明列表
	 * @return
	 */
	public List<Property> getProperties();

	
	/**
	 * 
	 * 流程的入口节点
	 * @return
	 */
	public Node getEntry();
	
	public void setEntry(Node start);
	
	/**
	 * 获得所有的开始节点
	 * @return
	 */
	public List<StartNode> getStartNodes();
	public StartNode getStartNode(String startNodeId);

	
	/**
	 * 获得流程图中的所有的活动
	 * @return
	 */
	public List<Activity> getActivities();
	public Activity getActivity(String activityId);

	
	/**
	 * 获得所有的路由节点
	 * @return
	 */
	public List<Router> getRouters();
	public Router getRouter(String routerId);

	
	/**
	 * 所有的结束节点
	 * @return
	 */
	public List<EndNode> getEndNodes();
	public EndNode getEndNode(String endNodeId);

	/**
	 * 所有的转移
	 * @return
	 */
	public List<Transition> getTransitions();
	public Transition getTransition(String transitionId);
	
	
	/**
	 * 获得该流程作用域内的所有的Service
	 * @return
	 */
	public List<Service> getServices();
	public Service getService(String serviceId);
	/**
	 * 获得本流程局部的Service
	 * @return
	 */
	public List<Service> getLocalServices();
	/**
	 * 获得该流程作用域内的所有的资源定义。
	 * @return
	 */
	public List<Resource> getResources();
	public Resource getResource(String resourceId);
	/**
	 * 获得本流程局部的Resource
	 * @return
	 */
	public List<Resource> getLocalResources();

	
	public ProcessImport getProcessImportByLocation(String location);
	
	/**
	 * 服务import列表
	 * @return
	 */
	public List<ProcessImport<Service>> getProcessImportForServices();
	
	/**
	 * 资源import列表
	 * @return
	 */
	public List<ProcessImport<Resource>> getProcessImportForResources();
	
	/**
	 * 业务类别
	 * @return
	 */
	public String getBizCategory();
	
	public WorkflowElement findWFElementById(String id);

	//**************************************************************
	//**************************************************************
	//*************  服务接入或者服务输出****************************
	//**************************************************************
	//**************************************************************
	
	/**
	 * 本活动所引用的服务
	 * TODO 待研究，2011-02-05
	 * @return
	 */
//	public ServiceBinding getServiceRef();
	
//	public void setServiceRef(ServiceBinding serviceRef) ;
	
	/**
	 * 事件监听器接入点
	 */
	public List<EventListenerDef> getEventListeners();
	
}
