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

import org.dom4j.Namespace;
import org.fireflow.model.io.ModelElementNames;
/**
 * @author 非也
 * @version 2.0
 */
public interface FPDLNames extends ModelElementNames{

    /** Namespace prefix to use for FPDL elements. */
    String FPDL_NS_PREFIX = "f20";
    
    /** The FPDL20 namespace URI. */
    String FPDL_NS_URI = "http://www.fireflow.org/schema/workflowprocess";

    /** The FPDL schema URI. */
    String FPDL_SCHEMA_LOCATION = FPDL_NS_URI+" "+FPDL_NS_URI+"/WorkflowProcessSchema-2.0.xsd";//FPDL_NS_URI+" "+"WorkflowProcessSchema-2.0.xsd"; //
    

    String EXTENDED_ATTRIBUTES = "extended-attributes";
    String EXTENDED_ATTRIBUTE = "extended-attribute";


    String VERSION = "version";

    String WORKFLOW_PROCESS = "workflow-process";
    
    String IMPORT = "import";
    
    String IMPORT_TYPE = "type";    
    String LOCATION = "location";
    
    String PRIORITY = "priority";
    String ENTRY = "entry";

    String DURATION = "duration";    
    String UNIT = "unit";
    String IS_BUSINESS_TIME = "is-business-time";
    
    String START_NODE = "start-node";
    String START_NODES = "start-nodes";
    String END_NODE = "end-node";
    String END_NODES = "end-nodes";

    String ACTIVITIES = "activities";
    String ACTIVITY = "activity";
    String LOOP_STRATEGY = "loop-strategy";

    String ROUTERS = "routers";
    String ROUTER = "router";


    String PROPERTIES = "properties";
    String PROPERTY = "property";
    
    String REFERENCE = "reference";

    String INIT_VALUE = "init-value";


    String TRANSITIONS = "transitions";
    String TRANSITION = "transition";
    String FROM = "from";
    String TO = "to";
    String IS_LOOP = "is-loop";
    String IS_DEFAULT = "is-default";

    String CONDITION = "condition";

    String TYPE = "type";


    String NAMESPACE = "namespace";

    

    String DECORATOR = "decorator";
    
    String NORMAL_START_DECORATOR = "normal-start-decorator";
    String NORMAL_END_DECORATOR = "normal-end-decorator";
    String THROW_TERMINATION_DECORATOR = "throw-termination-decorator";
    String TIMER_START_DECORATOR = "timer-start-decorator";
    String ATTACHED_TO_ACTIVITY = "attached-to-activity";
    String IS_CANCEL_ATTACHED_TO_ACTIVITY = "is-cancel-attached-to-activity";
    String TIMER_OPERATION_NAME = "timer-operation-name";
    String CRON_EXPRESSION = "cron";
    String START_TIME_EXPRESSION = "start-time";
    String END_TIME_EXPRESSION = "end-time";
    String REPEAT_INTERVAL_EXPRESSION = "repeat-interval";
    String REPEAT_COUNT_EXPRESSION = "repeat-count";
    
    String CATCH_COMPENSATION_DECORATOR = "catch-compensation-decorator";
    String COMPENSATION_CODE = "compensation-code";
    String COMPENSATION_CODES = "compensation-codes";
    String ERROR_CODE = "error-code";
    String CATCH_FAULT_DECORATOR = "catch-fault-decorator";
    
    String THROW_COMPENSATION_DECORATOR = "throw-compensation-decorator";
    String THROW_FAULT_DECORATOR = "throw-fault-decorator";
    
    String SERVICE_BINDING = "service-binding";
    String SERVICE_ID = "service-id";
    
    String INPUT_ASSIGNMENTS = "input-assignments";
    String INPUT_ASSIGNMENT = "input-assignment";
    
    String OUTPUT_ASSIGNMENTS = "output-assignments";
    String OUTPUT_ASSIGNMENT = "output-assignment";
    
    String PROP_OVERRIDES = "prop-overrides";
    String PROP_OVERRIDE = "prop-override";
    
    String PROP_GROUP_NAME = "prop-group-name";
    String PROP_NAME = "prop-name";
    
    String RESOURCE_BINDING = "resource-binding";
    
    String ASSIGNMENT_STRATEGY = "assignment-strategy";
    
    String ADMINISTRATORS = "administrators";

    
    String RESOURCE_REF = "resource-ref";
    String RESOURCE_ID = "resource-id";
    
    String PARAMETER_ASSIGNMENTS = "parameter-assignments";
    String PARAMETER_ASSIGNMENT = "parameter-assignment";
    
    String POTENTIAL_OWNERS = "potential-owners";
    String READERS = "readers";
    


    
    String EVENT_LISTENERS = "event-listeners";
    String EVENT_LISTENER = "event-listener";


    Namespace XSD_NS = new Namespace(XSD_NS_PREFIX, XSD_URI);
    Namespace XSI_NS = new Namespace(XSI_NS_PREFIX, XSI_URI);
    Namespace FPDL_NS = new Namespace(FPDL_NS_PREFIX, FPDL_NS_URI);

}
