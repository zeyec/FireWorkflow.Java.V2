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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.fireflow.model.binding.InputAssignment;
import org.fireflow.model.binding.OutputAssignment;
import org.fireflow.model.binding.ParameterAssignment;
import org.fireflow.model.binding.PropOverride;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ResourceRef;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Expression;
import org.fireflow.model.data.Property;
import org.fireflow.model.io.Dom4JResourceSerializer;
import org.fireflow.model.io.Dom4JServiceSerializer;
import org.fireflow.model.io.SerializerException;
import org.fireflow.model.io.Util4Serializer;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.resourcedef.Resource;
import org.fireflow.model.servicedef.Service;
import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.EndNode;
import org.fireflow.pdl.fpdl20.process.ProcessImport;
import org.fireflow.pdl.fpdl20.process.Router;
import org.fireflow.pdl.fpdl20.process.StartNode;
import org.fireflow.pdl.fpdl20.process.Transition;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.decorator.Decorator;
import org.fireflow.pdl.fpdl20.process.decorator.endnode.NormalEndDecorator;
import org.fireflow.pdl.fpdl20.process.decorator.endnode.ThrowCompensationDecorator;
import org.fireflow.pdl.fpdl20.process.decorator.endnode.ThrowFaultDecorator;
import org.fireflow.pdl.fpdl20.process.decorator.endnode.ThrowTerminationDecorator;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.CatchCompensationDecorator;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.CatchFaultDecorator;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.NormalStartDecorator;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.TimerStartDecorator;
import org.fireflow.pdl.fpdl20.process.event.EventListenerDef;

/**
 * @author 非也 nychen2000@163.com
 * @version 2.0
 */
public class Dom4JFPDLSerializer implements IFPDLSerializer {

    public static final String DEFAULT_FPDL_VERSION = "2.0";
    public static final String DEFAULT_VENDOR = "www.firesoa.com";

    private static DocumentFactory df = new DocumentFactory(); 

    public void serialize(WorkflowProcess workflowProcess, OutputStream out)
            throws IOException, SerializerException {

        Document document = workflowProcessToDom(workflowProcess);

        // write the document to the output stream
        OutputFormat format = new OutputFormat("    ", true);
        format.setEncoding("UTF-8");

        XMLWriter writer = new XMLWriter(out, format);

        writer.write(document);
        out.flush();
    }


    public Document workflowProcessToDom(WorkflowProcess workflowProcess)
            throws SerializerException {


        // serialize the Package
        Element workflowProcessElement = df.createElement(new QName(
                WORKFLOW_PROCESS, FPDL_NS));
        workflowProcessElement.addNamespace(FPDL_NS_PREFIX, FPDL_NS_URI);
        workflowProcessElement.addNamespace(SERVICE_NS_PREFIX, SERVICE_NS_URI);
        workflowProcessElement.addNamespace(RESOURCE_NS_PREFIX, RESOURCE_NS_URI);
        workflowProcessElement.addNamespace(XSD_NS_PREFIX, XSD_URI);
        workflowProcessElement.addNamespace(XSI_NS_PREFIX, XSI_URI);

        QName qname = df.createQName(
        	      "schemaLocation", "xsi", XSI_URI);
        workflowProcessElement.addAttribute(qname, FPDL_SCHEMA_LOCATION+" "+SERVICE_SCHEMA_LOCATION+" "+RESOURCE_SCHEMA_LOCATION);
      
      
        workflowProcessElement.addAttribute(ID, workflowProcess.getId());
        workflowProcessElement.addAttribute(NAME, workflowProcess.getName());
        workflowProcessElement.addAttribute(DISPLAY_NAME, workflowProcess.getDisplayName());
        workflowProcessElement.addAttribute(ENTRY, workflowProcess.getEntry().getId());
        workflowProcessElement.addAttribute(BIZ_CATEGORY, workflowProcess.getBizCategory());

        Util4Serializer.addElement(workflowProcessElement, DESCRIPTION,
                workflowProcess.getDescription());
 
        
        writerProcessImports4Service(workflowProcess.getProcessImportForServices(),workflowProcessElement);
        writerProcessImports4Resource(workflowProcess.getProcessImportForResources(),workflowProcessElement);

        Dom4JServiceSerializer serviceSerializer = new Dom4JServiceSerializer();
        serviceSerializer.writeServices(workflowProcess.getServices(),workflowProcessElement);
        
        Dom4JResourceSerializer resourceSerializer = new Dom4JResourceSerializer();
        resourceSerializer.writeResources(workflowProcess.getResources(),workflowProcessElement);
        
        writeProperties(workflowProcess.getProperties(), workflowProcessElement);
        
        this.writeDuration(workflowProcess.getDuration(), workflowProcessElement);
        writeStartNodes(workflowProcess.getStartNodes(), workflowProcessElement);


        writeActivities(workflowProcess.getActivities(), workflowProcessElement);
        writeRouters(workflowProcess.getRouters(),
                workflowProcessElement);
        writeEndNodes(workflowProcess.getEndNodes(), workflowProcessElement);
        writeTransitions(workflowProcess.getTransitions(),
                workflowProcessElement);


        writeEventListeners(workflowProcess.getEventListeners(), workflowProcessElement);

        writeExtendedAttributes(workflowProcess.getExtendedAttributes(),
                workflowProcessElement);

        Document document = df.createDocument(workflowProcessElement);


        return document;

    }

    public String workflowProcessToXMLString(WorkflowProcess workflowProcess)
            throws IOException, SerializerException {
        Document dom = workflowProcessToDom(workflowProcess);
        OutputFormat format = new OutputFormat("    ", true);
        format.setEncoding("utf-8");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        XMLWriter writer = new XMLWriter(out, format);

        writer.write(dom);
        return out.toString();

    }
    
    protected void writerProcessImports4Service(List<ProcessImport<Service>> processImports,Element parentElement){
    	if (processImports==null || processImports.size()==0){
    		return ;
    	}
    	for (ProcessImport<Service> processImport : processImports){
            Element processImportElem = Util4Serializer.addElement(
            		parentElement, IMPORT);
            
            processImportElem.addAttribute(ID, processImport.getId());
            if (processImport.getName()!=null && !processImport.getName().trim().equals("")){
            	processImportElem.addAttribute(NAME, processImport.getName());
            }
            if (processImport.getDisplayName()!=null && !processImport.getDisplayName().trim().equals("")){
            	processImportElem.addAttribute(DISPLAY_NAME, processImport.getDisplayName());
            }
            processImportElem.addAttribute(IMPORT_TYPE, ProcessImport.SERVICES_IMPORT);
            processImportElem.addAttribute(LOCATION, processImport.getLocation());


            if (processImport.getDescription()!=null && !processImport.getDescription().trim().equals("")){
                Util4Serializer.addElement(processImportElem, DESCRIPTION,
                        processImport.getDescription());
            }
            
    	}
    }
    
    protected void writerProcessImports4Resource(List<ProcessImport<Resource>> processImports,Element parentElement){
    	if (processImports==null || processImports.size()==0){
    		return ;
    	}
    	for (ProcessImport<Resource> processImport : processImports){
            Element processImportElem = Util4Serializer.addElement(
            		parentElement, IMPORT);
            
            processImportElem.addAttribute(ID, processImport.getId());
            if (processImport.getName()!=null && !processImport.getName().trim().equals("")){
            	processImportElem.addAttribute(NAME, processImport.getName());
            }
            if (processImport.getDisplayName()!=null && !processImport.getDisplayName().trim().equals("")){
            	processImportElem.addAttribute(DISPLAY_NAME, processImport.getDisplayName());
            }
            processImportElem.addAttribute(IMPORT_TYPE, ProcessImport.RESOURCES_IMPORT);
            processImportElem.addAttribute(LOCATION, processImport.getLocation());


            if (processImport.getDescription()!=null && !processImport.getDescription().trim().equals("")){
                Util4Serializer.addElement(processImportElem, DESCRIPTION,
                        processImport.getDescription());
            }
            
    	}
    }    

    protected void writeEventListeners(List<EventListenerDef> eventListeners, Element parentElement) {
        if (eventListeners == null || eventListeners.size() == 0) {
            return;
        }

        Element eventListenersElm =
                Util4Serializer.addElement(parentElement,
                EVENT_LISTENERS);
        for (int i = 0; i < eventListeners.size(); i++) {
        	EventListenerDef listener = (EventListenerDef) eventListeners.get(i);
            Element eventListenerElm = Util4Serializer.addElement(
                    eventListenersElm, EVENT_LISTENER);
            
            eventListenerElm.addAttribute(ID, listener.getId());
            if (listener.getName()!=null && !listener.getName().trim().equals("")){
            	eventListenerElm.addAttribute(NAME, listener.getName());
            }
            if (listener.getDisplayName()!=null && !listener.getDisplayName().trim().equals("")){
            	eventListenerElm.addAttribute(DISPLAY_NAME, listener.getDisplayName());
            }
            eventListenerElm.addAttribute(BEAN_NAME, listener.getBeanName());
        }
    }

    protected void writeProperties(List<Property> dataFields, Element parent)
            throws SerializerException {

        if (dataFields == null || dataFields.size() == 0) {
            return;
        }

        Element dataFieldsElement = Util4Serializer.addElement(parent,
                PROPERTIES);
        Iterator<Property> iter = dataFields.iterator();
        while (iter.hasNext()) {
            Property dataField = iter.next();
            Element dataFieldElement = Util4Serializer.addElement(
                    dataFieldsElement, PROPERTY);

            dataFieldElement.addAttribute(ID, dataField.getId());
            dataFieldElement.addAttribute(NAME, dataField.getName());
            dataFieldElement.addAttribute(DISPLAY_NAME, dataField.getDisplayName());
            dataFieldElement.addAttribute(DATA_TYPE, dataField.getDataType());

            dataFieldElement.addAttribute(INIT_VALUE,
                    dataField.getInitialValueAsString());

            if (dataField.getDescription()!=null && !dataField.getDescription().trim().equals("")){
                Util4Serializer.addElement(dataFieldElement, DESCRIPTION, dataField.getDescription());
            }
        }
    }

    protected void writeEndNodes(List<EndNode> endNodes, Element parent) {
        Element endNodesElement = Util4Serializer.addElement(parent, END_NODES);
        Iterator<EndNode> iter = endNodes.iterator();

        while (iter.hasNext()) {
            writeEndNode( iter.next(), endNodesElement);
        }
    }

    protected void writeEndNode(EndNode endNode, Element parent) {
        Element endNodeElement = Util4Serializer.addElement(parent, END_NODE);
        endNodeElement.addAttribute(ID, endNode.getId());
        endNodeElement.addAttribute(NAME, endNode.getName());
        if (endNode.getDisplayName()!=null && !endNode.getDisplayName().trim().equals("")){
        	endNodeElement.addAttribute(DISPLAY_NAME, endNode.getDisplayName());
        }
        if (endNode.getDescription()!=null && !endNode.getDescription().trim().equals("")){
        	Util4Serializer.addElement(endNodeElement, DESCRIPTION, endNode.getDescription());
        }
        
        Decorator dec = endNode.getDecorator();
        
        if (dec!=null){
        	Element decElem = Util4Serializer.addElement(endNodeElement, DECORATOR);
        	
        	writeEndNodeDecorator(dec,decElem);
        }

        writeExtendedAttributes(endNode.getExtendedAttributes(), endNodeElement);

    }

    protected void writeStartNodes(List<StartNode> startNodes, Element parent)
            throws SerializerException {
        if (startNodes == null || startNodes.size()==0) {
            return;
        }
        Element startNodesElement = Util4Serializer.addElement(parent, START_NODES);
        
        for (StartNode startNode : startNodes){
            Element startElement = Util4Serializer.addElement(startNodesElement, START_NODE);
            startElement.addAttribute(ID, startNode.getId());
            startElement.addAttribute(NAME, startNode.getName());

            if (startNode.getDisplayName()!=null && !startNode.getDisplayName().trim().equals("")){
            	startElement.addAttribute(DISPLAY_NAME, startNode.getDisplayName());	
            }
            
            if (startNode.getDescription()!=null && !startNode.getDescription().trim().equals("")){
            	Util4Serializer.addElement(startElement, DESCRIPTION, startNode.getDescription());
            }
            
            Decorator dec = startNode.getDecorator();
            
            if (dec!=null){
            	Element decElement = Util4Serializer.addElement(startElement, DECORATOR);
            	writeStartNodeDecorator(dec,decElement);           	
            	
            }
            
            writeExtendedAttributes(startNode.getExtendedAttributes(), startElement);
        }
    }
    
    protected void writeEndNodeDecorator(Decorator dec,Element parent){
    	if (dec instanceof ThrowCompensationDecorator){
    		ThrowCompensationDecorator compensationDec = (ThrowCompensationDecorator)dec;
    		Element catchCompensationElem = Util4Serializer.addElement(parent, THROW_COMPENSATION_DECORATOR);
    		List<String> compensationCodes = compensationDec.getCompensationCodes();
    		StringBuffer compensationCodesStr = new StringBuffer("");
    		if (compensationCodes!=null){
    			int i=0;
    			for (String code : compensationCodes){
    				
    				compensationCodesStr.append(code);
    				if (i<compensationCodes.size()-1){
    					compensationCodesStr.append(",");
    				}
    				i++;
    			}
    		}
    		catchCompensationElem.addAttribute(COMPENSATION_CODES, compensationCodesStr.toString());
    	}
    	
    	else if (dec instanceof ThrowFaultDecorator){
    		ThrowFaultDecorator catchFaultDec = (ThrowFaultDecorator)dec;
    		Element catchFaultElem = Util4Serializer.addElement(parent, THROW_FAULT_DECORATOR);
    		
        	catchFaultElem.addAttribute(ERROR_CODE, catchFaultDec.getErrorCode());

    	}
    	else if (dec instanceof ThrowTerminationDecorator){
    		Util4Serializer.addElement(parent, THROW_TERMINATION_DECORATOR);
    	}
    	else if (dec instanceof NormalEndDecorator){
    		Util4Serializer.addElement(parent, NORMAL_END_DECORATOR);
    	}
    }
    protected void writeStartNodeDecorator(Decorator dec,Element parent){
    	if (dec instanceof TimerStartDecorator){
        	TimerStartDecorator timerDec = (TimerStartDecorator)dec;
        	Element timerDecElem = Util4Serializer.addElement(parent, TIMER_START_DECORATOR);
        	if (timerDec.getAttachedToActivity()!=null){
        		timerDecElem.addAttribute(ATTACHED_TO_ACTIVITY, timerDec.getAttachedToActivity().getId());
        		timerDecElem.addAttribute(IS_CANCEL_ATTACHED_TO_ACTIVITY, Boolean.toString(timerDec.getCancelAttachedToActivity()));
        	}
        	timerDecElem.addAttribute(TIMER_OPERATION_NAME, timerDec.getTimerOperationName().getValue());
        	Expression cronExp = timerDec.getCronExpression();
        	if(cronExp!=null){
        		Element expElem =  Util4Serializer.addElement(timerDecElem, CRON_EXPRESSION);
                this.writeExpression(cronExp, expElem);
        	}
        	
        	Expression startTimeExp = timerDec.getStartTimeExpression();
        	if (startTimeExp!=null){
        		Element expElem =  Util4Serializer.addElement(timerDecElem, START_TIME_EXPRESSION);
                this.writeExpression(startTimeExp, expElem);
        	}
        	
        	Expression endTimeExp = timerDec.getEndTimeExpression();
        	if (endTimeExp!=null){
        		Element expElem =  Util4Serializer.addElement(timerDecElem, END_TIME_EXPRESSION);
                this.writeExpression(endTimeExp, expElem);
        	}
        	
        	Expression intervalExp = timerDec.getRepeatIntervalExpression();
        	if (intervalExp!=null){
        		Element expElem =  Util4Serializer.addElement(timerDecElem, REPEAT_INTERVAL_EXPRESSION);
                this.writeExpression(intervalExp, expElem);
        	}
        	
        	Expression countExp = timerDec.getRepeatCountExpression();
        	if (countExp!=null){
        		Element expElem =  Util4Serializer.addElement(timerDecElem, REPEAT_COUNT_EXPRESSION);
                this.writeExpression(countExp, expElem);
        	}
    	}
    	
    	else if (dec instanceof CatchCompensationDecorator){
    		CatchCompensationDecorator catchCompensationDec = (CatchCompensationDecorator)dec;
    		Element catchCompensationElem = Util4Serializer.addElement(parent, CATCH_COMPENSATION_DECORATOR);
        	if (catchCompensationDec.getAttachedToActivity()!=null){
        		catchCompensationElem.addAttribute(ATTACHED_TO_ACTIVITY, catchCompensationDec.getAttachedToActivity().getId());
        	}
    		catchCompensationElem.addAttribute(COMPENSATION_CODE, catchCompensationDec.getCompensationCode());

    	}
    	
    	else if (dec instanceof CatchFaultDecorator){
    		CatchFaultDecorator catchFaultDec = (CatchFaultDecorator)dec;
    		Element catchFaultElem = Util4Serializer.addElement(parent, CATCH_FAULT_DECORATOR);
    		
        	if (catchFaultDec.getAttachedToActivity()!=null){
        		catchFaultElem.addAttribute(ATTACHED_TO_ACTIVITY, catchFaultDec.getAttachedToActivity().getId());
        	}
        	catchFaultElem.addAttribute(ERROR_CODE, catchFaultDec.getErrorCode());

    	}
    	else if (dec instanceof NormalStartDecorator){
    		NormalStartDecorator normalDec = (NormalStartDecorator)dec;
    		Util4Serializer.addElement(parent, NORMAL_START_DECORATOR);
    	}
    	//TODO MessageStartDecorator需完善~~~
    }

    protected void writeRouters(List<Router> synchronizers, Element parent)
            throws SerializerException {
        if (synchronizers == null || synchronizers.size() == 0) {
            return;
        }
        Element synchronizersElement = Util4Serializer.addElement(parent,
                ROUTERS);

        Iterator<Router> iter = synchronizers.iterator();

        while (iter.hasNext()) {
            writeRouter( iter.next(), synchronizersElement);
        }
    }

    protected void writeRouter(Router router, Element parent)
            throws SerializerException {
        Element synchronizerElement = Util4Serializer.addElement(parent,
                ROUTER);
        synchronizerElement.addAttribute(ID, router.getId());
        synchronizerElement.addAttribute(NAME, router.getName());
        
        if (router.getDisplayName()!=null && !router.getDisplayName().trim().equals("")){
            synchronizerElement.addAttribute(DISPLAY_NAME, router.getDisplayName());
        }
        if (router.getDescription()!=null && !router.getDescription().equals("")){
            Util4Serializer.addElement(synchronizerElement, DESCRIPTION,
            		router.getDescription());
        }
        Decorator dec = router.getDecorator();
        if (dec!=null){
        	//TODO 有待进一步完善Router的decorator
        }
        writeExtendedAttributes(router.getExtendedAttributes(),
                synchronizerElement);
    }

    protected void writeActivities(List<Activity> activities, Element parent)
            throws SerializerException {

        if (activities == null || activities.size() == 0) {
            return;
        }

        Element activitiesElement = Util4Serializer.addElement(parent,
                ACTIVITIES);

        Iterator<Activity> iter = activities.iterator();
        while (iter.hasNext()) {
            writeActivity(  iter.next(), activitiesElement);
        }
    }

    protected void writeActivity(Activity activity, Element parent)
            throws SerializerException {

        Element activityElement = Util4Serializer.addElement(parent, ACTIVITY);

        activityElement.addAttribute(ID, activity.getId());
        activityElement.addAttribute(NAME, activity.getName());
        if (activity.getDisplayName()!=null && !activity.getDisplayName().trim().equals("")){
        	activityElement.addAttribute(DISPLAY_NAME, activity.getDisplayName());
        }
        if (activity.getPriority()!=null && !activity.getPriority().trim().equals("")){
        	activityElement.addAttribute(PRIORITY, activity.getPriority());
        }        
        
        if (activity.getLoopStrategy()!=null){
        	activityElement.addAttribute(LOOP_STRATEGY, activity.getLoopStrategy().getValue());
        }        
        if (activity.getDescription()!=null){
        	Util4Serializer.addElement(activityElement, DESCRIPTION, activity.getDescription());
        }
        
        Decorator dec = activity.getDecorator();
        if (dec!=null){
        	//TODO Activity是否需要decorator?
        }
        
        this.writeProperties(activity.getProperties(), activityElement);
        this.writeDuration(activity.getDuration(), activityElement);
        
        this.writeServiceBinding(activity.getServiceBinding(),activityElement);
        this.writeResourceBinding(activity.getResourceBinding(),activityElement);
        
        writeEventListeners(activity.getEventListeners(), activityElement);
        writeExtendedAttributes(activity.getExtendedAttributes(),
                activityElement);
    }
    
    protected void writeResourceBinding(ResourceBinding resourceBinding,Element parent){
    	if (resourceBinding==null)return;
    	Element resourceBindingElem = Util4Serializer.addElement(parent, RESOURCE_BINDING);
    	resourceBindingElem.addAttribute(DISPLAY_NAME, resourceBinding.getDisplayName());
    	resourceBindingElem.addAttribute(ASSIGNMENT_STRATEGY, resourceBinding.getAssignmentStrategy().getValue());
    	
    	List<ResourceRef> administrators = resourceBinding.getAdministrators();
    	if (administrators!=null && administrators.size()>0){
    		Element administratorsElem = Util4Serializer.addElement(resourceBindingElem, ADMINISTRATORS);
    		for (ResourceRef resourceRef : administrators){
    			Element resourceRefElem = Util4Serializer.addElement(administratorsElem, RESOURCE_REF);
    			resourceRefElem.addAttribute(RESOURCE_ID, resourceRef.getResourceId());
    			
    			List<ParameterAssignment> paramAssignments = resourceRef.getParameterAssignments();
    			if (paramAssignments!=null && paramAssignments.size()>0){
    				Element paramAssignmentsElem = Util4Serializer.addElement(resourceRefElem, PARAMETER_ASSIGNMENTS);
    				for (ParameterAssignment paramAssignment : paramAssignments){
    					Element paramAssignmentElm = Util4Serializer.addElement(paramAssignmentsElem, PARAMETER_ASSIGNMENT);
    	    			Element fromElement = Util4Serializer.addElement(paramAssignmentElm, FROM);
    	    			Expression fromExp = paramAssignment.getFrom();
    	    			this.writeExpression(fromExp, fromElement);
    	                
    	                Util4Serializer.addElement(paramAssignmentElm, TO,paramAssignment.getTo());

    				}
    			}
    		}
    	}
    	
    	//潜在操作者
    	List<ResourceRef> potentialOwners = resourceBinding.getPotentialOwners();
    	if (potentialOwners!=null && potentialOwners.size()>0){
    		Element potentialOwnersElem = Util4Serializer.addElement(resourceBindingElem, POTENTIAL_OWNERS);
    		for (ResourceRef resourceRef : potentialOwners){
    			Element resourceRefElem = Util4Serializer.addElement(potentialOwnersElem, RESOURCE_REF);
    			resourceRefElem.addAttribute(RESOURCE_ID, resourceRef.getResourceId());
    			
    			List<ParameterAssignment> paramAssignments = resourceRef.getParameterAssignments();
    			if (paramAssignments!=null && paramAssignments.size()>0){
    				Element paramAssignmentsElem = Util4Serializer.addElement(resourceRefElem, PARAMETER_ASSIGNMENTS);
    				for (ParameterAssignment paramAssignment : paramAssignments){
    					Element paramAssignmentElm = Util4Serializer.addElement(paramAssignmentsElem, PARAMETER_ASSIGNMENT);
    	    			Element fromElement = Util4Serializer.addElement(paramAssignmentElm, FROM);
    	    			Expression fromExp = paramAssignment.getFrom();
    	    			this.writeExpression(fromExp, fromElement);
    	                
    	                Util4Serializer.addElement(paramAssignmentElm, TO,paramAssignment.getTo());

    				}
    			}
    		}
    	}
    	
    	List<ResourceRef> readers = resourceBinding.getReaders();
    	if (readers!=null && readers.size()>0){
    		Element potentialOwnersElem = Util4Serializer.addElement(resourceBindingElem, READERS);
    		for (ResourceRef resourceRef : readers){
    			Element resourceRefElem = Util4Serializer.addElement(potentialOwnersElem, RESOURCE_REF);
    			resourceRefElem.addAttribute(RESOURCE_ID, resourceRef.getResourceId());
    			
    			List<ParameterAssignment> paramAssignments = resourceRef.getParameterAssignments();
    			if (paramAssignments!=null && paramAssignments.size()>0){
    				Element paramAssignmentsElem = Util4Serializer.addElement(resourceRefElem, PARAMETER_ASSIGNMENTS);
    				for (ParameterAssignment paramAssignment : paramAssignments){
    					Element paramAssignmentElm = Util4Serializer.addElement(paramAssignmentsElem, PARAMETER_ASSIGNMENT);
    	    			Element fromElement = Util4Serializer.addElement(paramAssignmentElm, FROM);
    	    			Expression fromExp = paramAssignment.getFrom();
    	    			this.writeExpression(fromExp, fromElement);
    	                
    	                Util4Serializer.addElement(paramAssignmentElm, TO,paramAssignment.getTo());

    				}
    			}
    		}
    	}    	
    }
    
    protected void writeServiceBinding(ServiceBinding serviceBinding,Element parent){
    	if (serviceBinding==null)return;
    	Element serviceBindingElem = Util4Serializer.addElement(parent, SERVICE_BINDING);
    	
    	serviceBindingElem.addAttribute(SERVICE_ID, serviceBinding.getServiceId());
    	serviceBindingElem.addAttribute(OPERATION_NAME, serviceBinding.getOperationName());
    	
    	List<InputAssignment> inputAssignments = serviceBinding.getInputAssignments();
    	if (inputAssignments!=null && inputAssignments.size()>0){
    		Element inputAssignmentsElem = Util4Serializer.addElement(serviceBindingElem, INPUT_ASSIGNMENTS);
    		
    		for (InputAssignment inputAssignment : inputAssignments){
    			Element inputAssignmentElem = Util4Serializer.addElement(inputAssignmentsElem, INPUT_ASSIGNMENT);
    			Element fromElement = Util4Serializer.addElement(inputAssignmentElem, FROM);
    			Expression fromExp = inputAssignment.getFrom();
    			this.writeExpression(fromExp, fromElement);
                
                Util4Serializer.addElement(inputAssignmentElem, TO,inputAssignment.getTo());
    		}
    	}
    	
    	List<OutputAssignment> outputAssignments = serviceBinding.getOutputAssignments();
    	if (outputAssignments!=null && outputAssignments.size()>0){
    		Element outputAssignmentsElem = Util4Serializer.addElement(serviceBindingElem, OUTPUT_ASSIGNMENTS);
    		
    		for (OutputAssignment outputAssignment : outputAssignments){
    			Element outputAssignmentElem = Util4Serializer.addElement(outputAssignmentsElem, OUTPUT_ASSIGNMENT);
    			Element fromElement = Util4Serializer.addElement(outputAssignmentElem, FROM);
    			Expression fromExp = outputAssignment.getFrom();
    			this.writeExpression(fromExp, fromElement);
                
                Util4Serializer.addElement(outputAssignmentElem, TO,outputAssignment.getTo());
    		}
    	}
    	
    	List<PropOverride> propOverrides = serviceBinding.getPropOverrides();
    	if (propOverrides!=null && propOverrides.size()>0){
    		Element propOverridesElem = Util4Serializer.addElement(serviceBindingElem, PROP_OVERRIDES);
    		
    		for (PropOverride propOverride : propOverrides){
    			Element propOverrideElem = Util4Serializer.addElement(propOverridesElem, PROP_OVERRIDE);
    			propOverrideElem.addAttribute(PROP_GROUP_NAME, propOverride.getPropGroupName());
    			propOverrideElem.addAttribute(PROP_NAME, propOverride.getPropName());
    			propOverrideElem.addAttribute(VALUE, propOverride.getValue());
    		}
    	}
    }






    

    


    protected void writeDuration(Duration duration, Element parent) {
        if (duration == null) {
            return;
        }
        Element durationElement = Util4Serializer.addElement(parent, DURATION);
        durationElement.addAttribute(VALUE, Integer.toString(duration.getValue()));
        durationElement.addAttribute(UNIT, duration.getUnit());
        durationElement.addAttribute(IS_BUSINESS_TIME, Boolean.toString(duration.isBusinessTime()));
    }



    protected void writeTransitions(List<Transition> transitions, Element parent)
            throws SerializerException {

        if (transitions == null || transitions.size() == 0) {
            return;
        }

        Element transitionsElement = Util4Serializer.addElement(parent,
                TRANSITIONS);

        Iterator<Transition>  iter = transitions.iterator();
        while (iter.hasNext()) {
            writeTransition( iter.next(), transitionsElement);
        }
    }

    protected void writeTransition(Transition transition, Element parent)
            throws SerializerException {

        Element transitionElement = Util4Serializer.addElement(parent,
                TRANSITION);

        transitionElement.addAttribute(ID, transition.getId());
        transitionElement.addAttribute(FROM, transition.getFromNode().getId());
        transitionElement.addAttribute(TO, transition.getToNode().getId());
        transitionElement.addAttribute(NAME, transition.getName());
        if (transition.getDisplayName()!=null){
        	transitionElement.addAttribute(DISPLAY_NAME, transition.getDisplayName());
        }        
        
        transitionElement.addAttribute(IS_LOOP, Boolean.toString(transition.isLoop()));
        transitionElement.addAttribute(IS_DEFAULT, Boolean.toString(transition.isDefault()));

        if (transition.getDescription()!=null){
        	Util4Serializer.addElement(transitionElement, DESCRIPTION,transition.getDescription());
        }
        
        
        
        Expression condition = transition.getCondition();
        if(condition!=null){
            Element conditionElement = Util4Serializer.addElement(transitionElement, CONDITION);
            writeExpression(condition,conditionElement);
        }


        writeExtendedAttributes(transition.getExtendedAttributes(),
                transitionElement);
    }
    
    protected void writeExpression(Expression exp,Element parent){
    	Element expressionElem = Util4Serializer.addElement(parent, EXPRESSION);
        if (exp.getName()!=null && !exp.getName().trim().equals("")){
        	expressionElem.addAttribute(NAME, exp.getName());
        }
        if (exp.getDisplayName()!=null && !exp.getDisplayName().trim().equals("")){
        	expressionElem.addAttribute(DISPLAY_NAME, exp.getDisplayName());
        }
        expressionElem.addAttribute(LANGUAGE, exp.getLanguage());
        Util4Serializer.addElement(expressionElem, BODY,exp.getBody());
           
    }

    protected Element writeExtendedAttributes(Map<String,String> extendedAttributes,
            Element parent) {

        if (extendedAttributes == null || extendedAttributes.size() == 0) {
            return null;
        }

        Element extendedAttributesElement =
                Util4Serializer.addElement(parent,
                EXTENDED_ATTRIBUTES);
//                        parent
//				.addElement(EXTENDED_ATTRIBUTES);

        Iterator<String> keys = extendedAttributes.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = extendedAttributes.get(key);

            Element extendedAttributeElement = Util4Serializer.addElement(
                    extendedAttributesElement, EXTENDED_ATTRIBUTE);
            extendedAttributeElement.addAttribute(NAME, key.toString());
            if (value != null) {
                extendedAttributeElement.addAttribute(VALUE, value.toString());
            }

        }

        return extendedAttributesElement;

    }




}
