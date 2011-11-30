/**
 * Copyright 2007-2011 非也
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
package org.fireflow.pdl.fpdl20.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.service.TimerOperationName;
import org.fireflow.model.binding.AssignmentStrategy;
import org.fireflow.model.binding.impl.InputAssignmentImpl;
import org.fireflow.model.binding.impl.OutputAssignmentImpl;
import org.fireflow.model.binding.impl.ParameterAssignmentImpl;
import org.fireflow.model.binding.impl.PropOverrideImpl;
import org.fireflow.model.binding.impl.ResourceBindingImpl;
import org.fireflow.model.binding.impl.ResourceRefImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.Expression;
import org.fireflow.model.data.Property;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.data.impl.PropertyImpl;
import org.fireflow.model.io.Dom4JResourceParser;
import org.fireflow.model.io.Dom4JServiceParser;
import org.fireflow.model.io.ParserException;
import org.fireflow.model.io.Util4Parser;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.process.WorkflowElement;
import org.fireflow.model.resourcedef.Resource;
import org.fireflow.model.servicedef.Operation;
import org.fireflow.model.servicedef.Service;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.misc.LoopStrategy;
import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.EndNode;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.ProcessImport;
import org.fireflow.pdl.fpdl20.process.Router;
import org.fireflow.pdl.fpdl20.process.StartNode;
import org.fireflow.pdl.fpdl20.process.Transition;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.decorator.Decorator;
import org.fireflow.pdl.fpdl20.process.decorator.endnode.impl.ThrowCompensationDecoratorImpl;
import org.fireflow.pdl.fpdl20.process.decorator.endnode.impl.ThrowFaultDecoratorImpl;
import org.fireflow.pdl.fpdl20.process.decorator.endnode.impl.ThrowTerminationDecoratorImpl;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.CatchCompensationDecorator;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.CatchFaultDecorator;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.TimerStartDecorator;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.impl.CatchCompensationDecoratorImpl;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.impl.CatchFaultDecoratorImpl;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.impl.TimerStartDecoratorImpl;
import org.fireflow.pdl.fpdl20.process.event.EventListenerDef;
import org.fireflow.pdl.fpdl20.process.event.impl.EventListenerDefImpl;
import org.fireflow.pdl.fpdl20.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl20.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.ProcessImportImpl;
import org.fireflow.pdl.fpdl20.process.impl.RouterImpl;
import org.fireflow.pdl.fpdl20.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl20.process.impl.WorkflowProcessImpl;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



/**
 * @author 非也 nychen2000@163.com
 */
public class Dom4JFPDLParser implements IFPDLParser {
    private static DocumentFactory df = new DocumentFactory(); 
    private static ImportLoader defaultImportLoader = new ImportLoaderClasspathImpl();
    
    protected ImportLoader importLoader = defaultImportLoader;
    
	/**
	 * @return the importLoader
	 */
	public ImportLoader getImportLoader() {
		return importLoader;
	}

	/**
	 * @param importLoader the importLoader to set
	 */
	public void setImportLoader(ImportLoader importLoader) {
		this.importLoader = importLoader;
	}

	public WorkflowProcess parse(InputStream in) throws IOException,
			ParserException {
		try {
			SAXReader reader = new SAXReader(new DocumentFactory());
			reader.setEntityResolver(new EntityResolver() {

				String emptyDtd = "";
				ByteArrayInputStream bytels = new ByteArrayInputStream(emptyDtd
						.getBytes());

				public InputSource resolveEntity(String publicId,
						String systemId) throws SAXException, IOException {
					return new InputSource(bytels);
				}
			});
			Document document = reader.read(in);

			WorkflowProcess wp = parse(document);// 解析
			return wp;
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new ParserException("Error parsing document.", e);
		} finally {
		}
		// return parse(new InputStreamReader(in));
	}
	
	public WorkflowProcess parse(InputStream in,RuntimeContext ctx){
		return null;
	}

	@SuppressWarnings("static-access")
	protected WorkflowProcess parse(Document document)
			throws ParserException,IOException {
		Element workflowProcessElement = document.getRootElement();
		// 流程ID
		WorkflowProcessImpl wp = new WorkflowProcessImpl(workflowProcessElement
				.attributeValue(NAME));
		wp.setSn(UUID.randomUUID().toString());// 使用UUID作为流程实例ID
		// 流程整体描述
		wp.setDescription(Util4Parser.elementAsString(workflowProcessElement,
				DESCRIPTION));

		// 流程显示名称
		wp.setDisplayName(workflowProcessElement.attributeValue(DISPLAY_NAME));
		wp.setBizCategory(workflowProcessElement.attributeValue(BIZ_CATEGORY));

		//解析Import
		this.loadProcessImport(wp,Util4Parser.children(workflowProcessElement, IMPORT));
		
		
		//解析Service
		Dom4JServiceParser serviceParser = new Dom4JServiceParser();
        QName qname = df.createQName(
        		SERVICES, SERVICE_NS_PREFIX, SERVICE_NS_URI);
        serviceParser.loadServices(wp.getLocalServices(),Util4Parser.child(workflowProcessElement, qname));
		
		//解析Resource
        Dom4JResourceParser resourceParser = new Dom4JResourceParser();
        qname = df.createQName(
        		RESOURCES, RESOURCE_NS_PREFIX, RESOURCE_NS_URI);
        resourceParser.loadResources(wp.getLocalResources(),Util4Parser.child(workflowProcessElement, qname));
		
		// 解析datafields
		this.loadProperties(wp, wp.getProperties(), Util4Parser.child(
				workflowProcessElement, this.PROPERTIES));
		// 解析duration
		wp.setDuration(this.createDuration(Util4Parser.child(workflowProcessElement, DURATION)));


		// 所有业务节点,同时将这个节点的所有的属性都解析出来保存到节点信息中。
		loadActivities(wp, wp.getActivities(), Util4Parser.child(
				workflowProcessElement, ACTIVITIES));
		
		// 工作流同步器节点
		loadRouters(wp, wp.getRouters(), Util4Parser.child(
				workflowProcessElement, ROUTERS));
		// 结束节点
		loadEndNodes(wp, wp.getEndNodes(), Util4Parser.child(
				workflowProcessElement, END_NODES));
		

		// 开始节点
		loadStartNodes(wp, wp.getStartNodes(), Util4Parser.child(
				workflowProcessElement, START_NODES));
		
		
		// 转移线
		loadTransitions(wp, wp.getTransitions(),Util4Parser.child(workflowProcessElement,
				TRANSITIONS));
		
		//设置entry
		String entryNodeId = workflowProcessElement.attributeValue(ENTRY);
		WorkflowElement entryNode = wp.findWFElementById(entryNodeId);
		if (entryNode==null){
			throw new ParserException("Can't find the Entry Node , entry node id =["+entryNodeId+"]");
		}else{
			wp.setEntry((Node)entryNode);
		}
		
		//设置activity的attached nodes
		List<StartNode> startNodes = wp.getStartNodes();
		for (StartNode startNode : startNodes){
			Decorator decorator = startNode.getDecorator();
			if (decorator!=null){
				if (decorator instanceof TimerStartDecorator){
					Activity act = ((TimerStartDecorator) decorator).getAttachedToActivity();
					if (act!=null){
						act.getAttachedStartNodes().add(startNode);
					}
				}
				else if (decorator instanceof CatchFaultDecorator){
					Activity act = ((CatchFaultDecorator) decorator).getAttachedToActivity();
					if (act!=null){
						act.getAttachedStartNodes().add(startNode);
					}
				}
				
				else if (decorator instanceof CatchCompensationDecorator){
					Activity act = ((CatchCompensationDecorator) decorator).getAttachedToActivity();
					if (act!=null){
						act.getAttachedStartNodes().add(startNode);
					}
				}
			}
		}
		
		// 所有的监听器
		loadEventListeners(wp.getEventListeners(), Util4Parser.child(
				workflowProcessElement, EVENT_LISTENERS));
		// 加载扩展属性
		Map<String, String> extAttrs = wp.getExtendedAttributes();
		loadExtendedAttributes(extAttrs, Util4Parser.child(
				workflowProcessElement, EXTENDED_ATTRIBUTES));

		return wp;

	}
	


	protected void loadProcessImport(WorkflowProcess wp,List<Element> importElems)throws ParserException,IOException{
		if (importElems==null){
			return ;
		}
		
		for (Element importElm : importElems){
			String type = importElm.attributeValue(IMPORT_TYPE);
			if (ProcessImport.SERVICES_IMPORT.equals(type)){
				String name = importElm.attributeValue(NAME);
				ProcessImportImpl<Service> serviceImport = new ProcessImportImpl<Service>(wp,name);
				serviceImport.setImportType(type);
				serviceImport.setDisplayName(importElm.attributeValue(DISPLAY_NAME));
				serviceImport.setDescription(Util4Parser.elementAsString(importElm, DESCRIPTION));
				serviceImport.setLocation(importElm.attributeValue(LOCATION));
				
				serviceImport.setSn(UUID.randomUUID().toString());
				
				List<Service> services = this.importLoader.loadServices(serviceImport.getLocation());
				serviceImport.setContents(services);
				
				wp.getProcessImportForServices().add(serviceImport);
			}
			else if (ProcessImport.RESOURCES_IMPORT.equals(type)){
				String name = importElm.attributeValue(NAME);
				ProcessImportImpl<Resource> resourceImport = new ProcessImportImpl<Resource>(wp,name);
				resourceImport.setImportType(type);
				resourceImport.setDisplayName(importElm.attributeValue(DISPLAY_NAME));
				resourceImport.setDescription(Util4Parser.elementAsString(importElm, DESCRIPTION));
				resourceImport.setLocation(importElm.attributeValue(LOCATION));
				
				resourceImport.setSn(UUID.randomUUID().toString());
				
				List<Resource> resources = this.importLoader.loadResources(resourceImport.getLocation());
				resourceImport.setContents(resources);
				
				wp.getProcessImportForResources().add(resourceImport);
			}
		}
	}
	
	protected Duration createDuration(Element durationElem){
		if (durationElem==null) return null;

		String _v = durationElem.attributeValue(VALUE);
		int intV = -1;
		if (_v!=null){
			try{
				intV = Integer.parseInt(_v);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		String unit = durationElem.attributeValue(UNIT);
		
		String _isBizTime = durationElem.attributeValue(IS_BUSINESS_TIME);
		boolean isBizTime = false;
		if (_isBizTime!=null){
			try{
				isBizTime = Boolean.parseBoolean(_isBizTime);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		Duration du = new Duration(intV,unit);
		du.setBusinessTime(isBizTime);
		return du;
	}

	/**
	 * @param listeners
	 * @param element
	 */
	protected void loadEventListeners(List<EventListenerDef> listeners,
			Element element) {
		listeners.clear();
		if (element == null) {
			return;
		}
		if (element == null) {
			return;
		}
		List<Element> listenerElms = Util4Parser.children(element,
				EVENT_LISTENER);
		Iterator<Element> iter = listenerElms.iterator();
		while (iter.hasNext()) {
			Element elm = iter.next();
			EventListenerDefImpl listener = new EventListenerDefImpl();
			
			listener.setName(elm.attributeValue(NAME));
			listener.setDisplayName(elm.attributeValue(DISPLAY_NAME));
			listener.setBizCategory(elm.attributeValue(BIZ_CATEGORY));
			listener.setDescription(Util4Parser.elementAsString(elm, DESCRIPTION));
			listener.setBeanName(elm.attributeValue(BEAN_NAME));

			listeners.add(listener);
		}
	}

	/**
	 * @param wp
	 * @param element
	 * @throws ParserException
	 */
	protected void loadStartNodes(WorkflowProcess wp,
			List<StartNode> startNodes, Element startNodesElem)
			throws ParserException {
		if (startNodesElem == null) {
			return;
		}
		List<Element> startNodeElements = Util4Parser.children(startNodesElem,
				START_NODE);
		startNodes.clear();
		Iterator<Element> iter = startNodeElements.iterator();
		while (iter.hasNext()) {
			Element startNodeElem = iter.next();

			StartNode startNode = new StartNodeImpl(wp, startNodeElem
					.attributeValue(NAME));
			startNode.setSn(UUID.randomUUID().toString());
			startNode.setDescription(Util4Parser.elementAsString(startNodeElem,
					DESCRIPTION));
			startNode.setDisplayName(startNodeElem.attributeValue(DISPLAY_NAME));
			
			Element decoratorElem = Util4Parser.child(startNodeElem, DECORATOR);
			if (decoratorElem!=null){
				boolean find = false;
				Element normalStartDecoratorElm = Util4Parser.child(decoratorElem, NORMAL_START_DECORATOR);
				if (normalStartDecoratorElm!=null){
					//默认值，无需更多设置
					find = true;
				}
				
				Element catchCompensationDecoratorElm = Util4Parser.child(decoratorElem, CATCH_COMPENSATION_DECORATOR);
				if (catchCompensationDecoratorElm!=null && !find){
					CatchCompensationDecoratorImpl catchCompensationDecorator = new CatchCompensationDecoratorImpl();
					catchCompensationDecorator.setCompensationCode(catchCompensationDecoratorElm.attributeValue(COMPENSATION_CODE));
					
					String attachedToActId = catchCompensationDecoratorElm.attributeValue(ATTACHED_TO_ACTIVITY);
					if (attachedToActId!=null){
						catchCompensationDecorator.setAttachedToActivity((Activity)wp.findWFElementById(attachedToActId));
					}
					
					startNode.setDecorator(catchCompensationDecorator);
					find=true;
				}
				
				Element catchFaultDecoratorElm = Util4Parser.child(decoratorElem, CATCH_FAULT_DECORATOR);
				if (catchFaultDecoratorElm!=null && !find){
					CatchFaultDecoratorImpl catchFaultDecorator = new CatchFaultDecoratorImpl();
					catchFaultDecorator.setErrorCode(catchFaultDecoratorElm.attributeValue(ERROR_CODE));
					
					String attachedToActId = catchFaultDecoratorElm.attributeValue(ATTACHED_TO_ACTIVITY);
					if (attachedToActId!=null){
						catchFaultDecorator.setAttachedToActivity((Activity)wp.findWFElementById(attachedToActId));
					}
					
					startNode.setDecorator(catchFaultDecorator);
					find = true;
				}
				
				Element timerStartDecoratorElm = Util4Parser.child(decoratorElem, TIMER_START_DECORATOR);
				if (timerStartDecoratorElm!=null && !find){
					TimerStartDecoratorImpl timerStartDec = new TimerStartDecoratorImpl();
					
					String timerOperationName = timerStartDecoratorElm.attributeValue(TIMER_OPERATION_NAME);
					timerStartDec.setTimerOperationName(TimerOperationName.fromValue(timerOperationName));
					
					String attachedToActId = timerStartDecoratorElm.attributeValue(ATTACHED_TO_ACTIVITY);
					if (attachedToActId!=null){
						timerStartDec.setAttachedToActivity((Activity)wp.findWFElementById(attachedToActId));
					}
					
					String cancelAttachedToAct = timerStartDecoratorElm.attributeValue(IS_CANCEL_ATTACHED_TO_ACTIVITY);
					if (cancelAttachedToAct!=null){
						try{
							timerStartDec.setCancelAttachedToActivity(Boolean.parseBoolean(cancelAttachedToAct));
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					
					Element startTimeElem = Util4Parser.child(timerStartDecoratorElm, START_TIME_EXPRESSION);
					if (startTimeElem!=null){
						timerStartDec.setStartTimeExpression(this.createExpression(Util4Parser.child(startTimeElem, EXPRESSION)));
					}
					
					Element endTimeElem = Util4Parser.child(timerStartDecoratorElm, END_TIME_EXPRESSION);
					if (endTimeElem!=null){
						timerStartDec.setEndTimeExpression(this.createExpression(Util4Parser.child(endTimeElem, EXPRESSION)));
					}
					
					Element intervalElem = Util4Parser.child(timerStartDecoratorElm, REPEAT_INTERVAL_EXPRESSION);
					if (intervalElem!=null){
						timerStartDec.setRepeatIntervalExpression(this.createExpression(Util4Parser.child(intervalElem, EXPRESSION)));
					}
					
					Element repeatCountElem = Util4Parser.child(timerStartDecoratorElm, REPEAT_COUNT_EXPRESSION);
					if (repeatCountElem!=null){
						timerStartDec.setRepeatCountExpression(this.createExpression(Util4Parser.child(repeatCountElem, EXPRESSION)));
					}
					
					Element cronElem = Util4Parser.child(timerStartDecoratorElm, CRON_EXPRESSION);
					if (cronElem!=null){
						timerStartDec.setCronExpression(this.createExpression(Util4Parser.child(cronElem, EXPRESSION)));
					}
					startNode.setDecorator(timerStartDec);
					find = true;
				}
			}
			
			loadExtendedAttributes(startNode.getExtendedAttributes(),
					Util4Parser.child(startNodeElem, EXTENDED_ATTRIBUTES));

			startNodes.add(startNode);
		}
	}

	/**
	 * @param wp
	 * @param endNodes
	 * @param element
	 * @throws ParserException
	 */
	protected void loadEndNodes(WorkflowProcess wp, List<EndNode> endNodes,
			Element endNodesElem) throws ParserException {

		if (endNodesElem == null) {
			return;
		}
		endNodes.clear();
		
		List<Element> endNodesElms = Util4Parser.children(endNodesElem, END_NODE);
		Iterator<Element> iter = endNodesElms.iterator();
		while (iter.hasNext()) {
			Element elm = iter.next();
			EndNode endNode = new EndNodeImpl(wp, elm.attributeValue(NAME));
			endNode.setSn(UUID.randomUUID().toString());
			endNode.setDescription(Util4Parser.elementAsString(elm,
					DESCRIPTION));
			endNode.setDisplayName(elm.attributeValue(DISPLAY_NAME));
			
			Element decoratorElem = Util4Parser.child(elm, DECORATOR);
			if (decoratorElem!=null){
				boolean find = false;
				Element normalEndDecoratorElm = Util4Parser.child(decoratorElem, NORMAL_END_DECORATOR);
				if (normalEndDecoratorElm!=null){
					//默认值，无需更多设置
					find = true;
				}
				
				Element throwCompensationDecoratorElm = Util4Parser.child(decoratorElem, THROW_COMPENSATION_DECORATOR);
				if (throwCompensationDecoratorElm!=null && !find){
					ThrowCompensationDecoratorImpl throwCompensationDecorator = new ThrowCompensationDecoratorImpl();
					String compensationCodes = throwCompensationDecoratorElm.attributeValue(COMPENSATION_CODES);
					if (compensationCodes!=null){
						StringTokenizer tokenizer = new StringTokenizer(compensationCodes,",");
						while(tokenizer.hasMoreTokens()){
							throwCompensationDecorator.addCompensationCode(tokenizer.nextToken());
						}
					}else{
						//添加缺省的compensation code
						throwCompensationDecorator.addCompensationCode(FpdlConstants.DEFAULT_COMPENSATION_CODE);
					}
					endNode.setDecorator(throwCompensationDecorator);
					find=true;
				}
				
				Element throwFaultDecoratorElm = Util4Parser.child(decoratorElem, THROW_FAULT_DECORATOR);
				if (throwFaultDecoratorElm!=null && !find){
					ThrowFaultDecoratorImpl throwFaultDecorator = new ThrowFaultDecoratorImpl();
					throwFaultDecorator.setErrorCode(throwFaultDecoratorElm.attributeValue(ERROR_CODE));
					
					endNode.setDecorator(throwFaultDecorator);
					find = true;
				}
				
				Element throwTerminationDecoratorElm = Util4Parser.child(decoratorElem, THROW_TERMINATION_DECORATOR);
				if (throwTerminationDecoratorElm!=null && !find){
					endNode.setDecorator(new ThrowTerminationDecoratorImpl());
					find = true;
				}
			}
			
			loadExtendedAttributes(endNode.getExtendedAttributes(), Util4Parser
					.child(elm, EXTENDED_ATTRIBUTES));
			endNodes.add(endNode);
		}
	}

	/**
	 * @param wp
	 * @param routers
	 * @param element
	 * @throws ParserException
	 */
	protected void loadRouters(WorkflowProcess wp,
			List<Router> routers, Element element)
			throws ParserException {

		if (element == null) {
			return;
		}
		routers.clear();
		
		List<Element> synchronizerElms = Util4Parser.children(element,
				ROUTER);
		Iterator<Element> iter = synchronizerElms.iterator();
		while (iter.hasNext()) {
			Element elm = iter.next();
			Router synchronizer = new RouterImpl(wp, elm
					.attributeValue(NAME));
			synchronizer.setSn(UUID.randomUUID().toString());
			synchronizer.setDescription(Util4Parser.elementAsString(elm,
					DESCRIPTION));
			synchronizer.setDisplayName(elm.attributeValue(DISPLAY_NAME));

			loadExtendedAttributes(synchronizer.getExtendedAttributes(),
					Util4Parser.child(elm, EXTENDED_ATTRIBUTES));

			routers.add(synchronizer);
		}
	}

	/**
	 * @param wp
	 * @param activities
	 * @param element
	 * @throws ParserException
	 */
	protected void loadActivities(WorkflowProcess wp,
			List<Activity> activities, Element element)
			throws ParserException {

		if (element == null) {
			// log.debug("Activites element was null");
			return;
		}

		List<Element> activitElements = Util4Parser.children(element, ACTIVITY);
		activities.clear();
		Iterator<Element> iter = activitElements.iterator();
		while (iter.hasNext()) {
			Element activityElement = iter.next();

			ActivityImpl activity = new ActivityImpl(wp, activityElement
					.attributeValue(NAME));
			activity.setSn(UUID.randomUUID().toString());
			activity.setDisplayName(activityElement
					.attributeValue(DISPLAY_NAME));
			activity.setDescription(Util4Parser.elementAsString(
					activityElement, DESCRIPTION));
			
			Duration du = this.createDuration(Util4Parser.child(activityElement, DURATION));
			activity.setDuration(du);
			activity.setPriority(activityElement.attributeValue(PRIORITY));
			String loopStrategy = activityElement.attributeValue(LOOP_STRATEGY);
			activity.setLoopStrategy(LoopStrategy.fromValue(loopStrategy));
			
			activity.setSn(UUID.randomUUID().toString());
			
			//Load Service Binding
			loadServiceBinding(wp,activity,Util4Parser.child(activityElement, SERVICE_BINDING));
			
			//Load Resource Binding
			loadResourceBinding(wp,activity,Util4Parser.child(activityElement, RESOURCE_BINDING));
	
			loadEventListeners(activity.getEventListeners(), Util4Parser.child(
					activityElement, EVENT_LISTENERS));
			loadExtendedAttributes(activity.getExtendedAttributes(),
					Util4Parser.child(activityElement, EXTENDED_ATTRIBUTES));


			activities.add(activity);
		}
	}
	
	protected void loadResourceBinding(WorkflowProcess wp,Activity activity,Element resourceBindingElem)throws ParserException{
		if (resourceBindingElem==null) return;
		
		ResourceBindingImpl resourceBinding = new ResourceBindingImpl();
		resourceBinding.setDisplayName(resourceBindingElem.attributeValue(DISPLAY_NAME));
		String assignmentStrategy = resourceBindingElem.attributeValue(ASSIGNMENT_STRATEGY);
		resourceBinding.setAssignmentStrategy(AssignmentStrategy.fromValue(assignmentStrategy));
		
		Element administratorsElem = Util4Parser.child(resourceBindingElem, ADMINISTRATORS);
		if (administratorsElem!=null){
			List<Element> resourceRefElems = Util4Parser.children(administratorsElem, RESOURCE_REF);
			if (resourceRefElems!=null){
				for (Element elm : resourceRefElems){
					ResourceRefImpl resourceRef = new ResourceRefImpl();
					resourceRef.setResourceId(elm.attributeValue(RESOURCE_ID));
					
					Resource resource = wp.getResource(resourceRef.getResourceId());
					if (resource==null){
						throw new ParserException("Resource not found,resource id = "+resourceRef.getResourceId());
					}
					resourceRef.setResource(resource);
					
					Element prameterAssignmentsElem = Util4Parser.child(elm, PARAMETER_ASSIGNMENTS);
					if (prameterAssignmentsElem!=null){
						List<Element> parameterAssignmentElems = Util4Parser.children(prameterAssignmentsElem, PARAMETER_ASSIGNMENT);
						for (Element parameterAssignmentElm : parameterAssignmentElems){
							ParameterAssignmentImpl assignment = new ParameterAssignmentImpl();
							Element fromElm = Util4Parser.child(parameterAssignmentElm, FROM);
							
							assignment.setFrom(this.createExpression(Util4Parser.child(fromElm, EXPRESSION)));
							assignment.setTo(Util4Parser.elementAsString(parameterAssignmentElm,TO));
							
							resourceRef.getParameterAssignments().add(assignment);
						}
					}
					
					resourceBinding.getAdministrators().add(resourceRef);
				}//for (Element elm : resourceRefElems)
			}
		}
		
		Element potentialOwnersElem = Util4Parser.child(resourceBindingElem, POTENTIAL_OWNERS);
		if (potentialOwnersElem!=null){
			List<Element> resourceRefElems = Util4Parser.children(potentialOwnersElem, RESOURCE_REF);
			if (resourceRefElems!=null){
				for (Element elm : resourceRefElems){
					ResourceRefImpl resourceRef = new ResourceRefImpl();
					resourceRef.setResourceId(elm.attributeValue(RESOURCE_ID));
					
					Resource resource = wp.getResource(resourceRef.getResourceId());
					if (resource==null){
						throw new ParserException("Resource not found,resource id = "+resourceRef.getResourceId());
					}
					resourceRef.setResource(resource);
					
					Element prameterAssignmentsElem = Util4Parser.child(elm, PARAMETER_ASSIGNMENTS);
					if (prameterAssignmentsElem!=null){
						List<Element> parameterAssignmentElems = Util4Parser.children(prameterAssignmentsElem, PARAMETER_ASSIGNMENT);
						for (Element parameterAssignmentElm : parameterAssignmentElems){
							ParameterAssignmentImpl assignment = new ParameterAssignmentImpl();
							Element fromElm = Util4Parser.child(parameterAssignmentElm, FROM);
							
							assignment.setFrom(this.createExpression(Util4Parser.child(fromElm, EXPRESSION)));
							assignment.setTo(Util4Parser.elementAsString(parameterAssignmentElm,TO));
							
							resourceRef.getParameterAssignments().add(assignment);
						}
					}
					
					resourceBinding.getPotentialOwners().add(resourceRef);
				}//for (Element elm : resourceRefElems)
			}
		}
		
		Element readersElem = Util4Parser.child(resourceBindingElem, READERS);
		if (readersElem!=null){
			List<Element> resourceRefElems = Util4Parser.children(readersElem, RESOURCE_REF);
			if (resourceRefElems!=null){
				for (Element elm : resourceRefElems){
					ResourceRefImpl resourceRef = new ResourceRefImpl();
					resourceRef.setResourceId(elm.attributeValue(RESOURCE_ID));
					
					Resource resource = wp.getResource(resourceRef.getResourceId());
					if (resource==null){
						throw new ParserException("Resource not found,resource id = "+resourceRef.getResourceId());
					}
					resourceRef.setResource(resource);
					
					Element prameterAssignmentsElem = Util4Parser.child(elm, PARAMETER_ASSIGNMENTS);
					if (prameterAssignmentsElem!=null){
						List<Element> parameterAssignmentElems = Util4Parser.children(prameterAssignmentsElem, PARAMETER_ASSIGNMENT);
						for (Element parameterAssignmentElm : parameterAssignmentElems){
							ParameterAssignmentImpl assignment = new ParameterAssignmentImpl();
							Element fromElm = Util4Parser.child(parameterAssignmentElm, FROM);
							
							assignment.setFrom(this.createExpression(Util4Parser.child(fromElm, EXPRESSION)));
							assignment.setTo(Util4Parser.elementAsString(parameterAssignmentElm,TO));
							
							resourceRef.getParameterAssignments().add(assignment);
						}
					}
					
					resourceBinding.getReaders().add(resourceRef);
				}//for (Element elm : resourceRefElems)
			}
		}	
		
		activity.setResourceBinding(resourceBinding);
	}
	
	protected void loadServiceBinding(WorkflowProcess wp,Activity activity,Element serviceBindingElem)throws ParserException{
		if (serviceBindingElem==null) return;
		
		ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
		serviceBinding.setServiceId(serviceBindingElem.attributeValue(SERVICE_ID));
		serviceBinding.setOperationName(serviceBindingElem.attributeValue(OPERATION_NAME));

		Service service = wp.getService(serviceBinding.getServiceId());
		if (service==null){
			throw new ParserException("Service not found ,id=["+serviceBinding.getServiceId()+"]");
		}
		serviceBinding.setService(service);
		
		Operation op = service.getOperation(serviceBinding.getOperationName());
		if (op==null){
			throw new ParserException("Operation not found ,service id=["+serviceBinding.getServiceId()+"],opreation name=["+serviceBinding.getOperationName()+"]");
		}
		serviceBinding.setOperation(op);
		
		Element inputAssignmentsElem = Util4Parser.child(serviceBindingElem, INPUT_ASSIGNMENTS);
		if (inputAssignmentsElem!=null){
			List<Element> inputAssignmentElems = Util4Parser.children(inputAssignmentsElem, INPUT_ASSIGNMENT);
			if (inputAssignmentElems!=null){
				for (Element inputAssignmentElm : inputAssignmentElems){
					InputAssignmentImpl inputAssignment = new InputAssignmentImpl();
					Element fromElm = Util4Parser.child(inputAssignmentElm, FROM);
					Expression from = this.createExpression(Util4Parser.child(fromElm, EXPRESSION));
					inputAssignment.setFrom(from);
					inputAssignment.setTo(Util4Parser.elementAsString(inputAssignmentElm, TO));
					
					serviceBinding.getInputAssignments().add(inputAssignment);
				}
			}
		}
		
		Element outputAssignmentsElem = Util4Parser.child(serviceBindingElem, OUTPUT_ASSIGNMENTS);
		if (outputAssignmentsElem!=null){
			List<Element> outputAssignmentElems = Util4Parser.children(outputAssignmentsElem, OUTPUT_ASSIGNMENT);
			if (outputAssignmentElems!=null){
				for (Element outputAssignmentElm : outputAssignmentElems){
					OutputAssignmentImpl outputAssignment = new OutputAssignmentImpl();
					Element fromElm = Util4Parser.child(outputAssignmentElm, FROM);
					Expression from = this.createExpression(Util4Parser.child(fromElm, EXPRESSION));
					
					outputAssignment.setFrom(from);
					outputAssignment.setTo(Util4Parser.elementAsString(outputAssignmentElm, TO));
					
					serviceBinding.getOutputAssignments().add(outputAssignment);
				}
			}
		}
		
		Element propOverridesElem = Util4Parser.child(serviceBindingElem, PROP_OVERRIDES);
		if (propOverridesElem!=null){
			List<Element> propOverrideElems = Util4Parser.children(propOverridesElem, PROP_OVERRIDE);
			if (propOverrideElems!=null){
				for (Element propOverrideElm : propOverrideElems){
					PropOverrideImpl propOverride = new PropOverrideImpl();
					propOverride.setPropGroupName(propOverrideElm.attributeValue(PROP_GROUP_NAME));
					propOverride.setPropName(propOverrideElm.attributeValue(PROP_NAME));
					propOverride.setValue(propOverrideElm.attributeValue(VALUE));
					
					serviceBinding.getPropOverrides().add(propOverride);
				}
			}
		}
		
		activity.setServiceBinding(serviceBinding);
	}




	/**
	 * @param wp
	 * @param transitionsElement
	 * @throws ParserException
	 */
	protected void loadTransitions(WorkflowProcess wp,List<Transition> transitions,
			Element transitionsElement) throws ParserException {

		if (transitionsElement == null) {
			return;
		}

		transitions.clear();
		
		List<Element> transitionElements = Util4Parser.children(transitionsElement, TRANSITION);

		Iterator<Element> iter = transitionElements.iterator();
		while (iter.hasNext()) {
			Element transitionElement = iter.next();
			Transition transition = createTransition(wp, transitionElement);
			transitions.add(transition);
			Node fromNode = transition.getFromNode();
			Node toNode = transition.getToNode();
			if (fromNode != null ) {
				fromNode.getLeavingTransitions().add(
						transition);
			}
			if (toNode != null ) {
				toNode.getEnteringTransitions()
				.add(transition);
			}
		}
	}


	/**
	 * @param wp
	 * @param element
	 * @return
	 * @throws ParserException
	 */
	protected Transition createTransition(WorkflowProcess wp, Element element)
			throws ParserException {
		String fromNodeId = element.attributeValue(FROM);
		String toNodeId = element.attributeValue(TO);
		Node fromNode = (Node) wp.findWFElementById(fromNodeId);
		Node toNode = (Node) wp.findWFElementById(toNodeId);

		TransitionImpl transition = new TransitionImpl(wp,
				element.attributeValue(NAME), fromNode, toNode);
		transition.setSn(UUID.randomUUID().toString());

		transition.setDisplayName(element.attributeValue(DISPLAY_NAME));
		transition.setDescription(Util4Parser.elementAsString(element,
				DESCRIPTION));
		
		String isLoop = element.attributeValue(IS_LOOP);
		if (isLoop!=null){
			try{
				transition.setIsLoop(Boolean.parseBoolean(isLoop));
			}catch(Exception e){
				
			}
		}
		
		String isDefault = element.attributeValue(IS_DEFAULT);
		if (isDefault!=null){
			try{
				transition.setDefault(Boolean.parseBoolean(isDefault));
			}catch(Exception e){
				
			}
		}
		
		
		Element conditionElement = Util4Parser.child(element, CONDITION);
		
		if (conditionElement!=null){
			Element expressionElem = Util4Parser.child(conditionElement, EXPRESSION);
			transition.setCondition(createExpression(expressionElem));
		}

		// load extended attributes
		Map<String, String> extAttrs = transition.getExtendedAttributes();
		loadExtendedAttributes(extAttrs, Util4Parser.child(element,
				EXTENDED_ATTRIBUTES));

		return transition;
	}
	
	private Expression createExpression(Element expressionElement){
		if (expressionElement!=null){
			ExpressionImpl exp = new ExpressionImpl();
			exp.setLanguage(expressionElement.attributeValue(LANGUAGE));
			exp.setName(expressionElement.attributeValue(NAME));
			exp.setDisplayName(expressionElement.attributeValue(DISPLAY_NAME));
			exp.setDataType(expressionElement.attributeValue(DATA_TYPE));
			
			exp.setBody(Util4Parser.elementAsString(expressionElement, BODY));
			return exp;
		}
		return null;
	}

	/**
	 * @param wp
	 * @param dataFields
	 * @param element
	 * @throws ParserException
	 */
	protected void loadProperties(WorkflowProcess wp,
			List<Property> dataFields, Element element)
			throws ParserException {

		if (element == null) {
			return;
		}

		List<Element> datafieldsElement = Util4Parser.children(element,
				PROPERTY);
		dataFields.clear();
		Iterator<Element> iter = datafieldsElement.iterator();
		while (iter.hasNext()) {
			Element dataFieldElement = iter.next();
			dataFields.add(createProperty(wp, dataFieldElement));
		}
	}

	/**
	 * @param wp
	 * @param element
	 * @return
	 * @throws ParserException
	 */
	protected Property createProperty(WorkflowProcess wp, Element element)
			throws ParserException {
		if (element == null) {
			return null;
		}
		String dataType = element.attributeValue(DATA_TYPE);
		if (dataType == null) {
			dataType = "java.lang.String";
		}

		Property dataField = new PropertyImpl(wp, element.attributeValue(NAME));

		dataField.setSn(UUID.randomUUID().toString());
		dataField.setDataType(dataType);

		dataField.setDisplayName(element.attributeValue(DISPLAY_NAME));
		dataField.setInitialValueAsString(element.attributeValue(INIT_VALUE));
		dataField.setDescription(Util4Parser.elementAsString(element,
				DESCRIPTION));

		return dataField;
	}

	/**
	 * @param extendedAttributes
	 * @param element
	 * @throws ParserException
	 */
	protected void loadExtendedAttributes(
			Map<String, String> extendedAttributes, Element element)
			throws ParserException {

		if (element == null) {
			return;
		}
		extendedAttributes.clear();
		List<Element> extendAttributeElementsList = Util4Parser.children(
				element, EXTENDED_ATTRIBUTE);
		Iterator<Element> iter = extendAttributeElementsList.iterator();
		while (iter.hasNext()) {
			Element extAttrElement = iter.next();
			String name = extAttrElement.attributeValue(NAME);
			String value = extAttrElement.attributeValue(VALUE);

			extendedAttributes.put(name, value);

		}
	}
}
