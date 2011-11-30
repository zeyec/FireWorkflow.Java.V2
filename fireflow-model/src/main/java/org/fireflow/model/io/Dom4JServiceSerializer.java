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
package org.fireflow.model.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.fireflow.model.data.Input;
import org.fireflow.model.data.Output;
import org.fireflow.model.servicedef.IOSpecification;
import org.fireflow.model.servicedef.Operation;
import org.fireflow.model.servicedef.Service;
import org.fireflow.model.servicedef.ServiceProp;
import org.fireflow.model.servicedef.ServicePropGroup;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class Dom4JServiceSerializer implements ModelElementNames {
	private static DocumentFactory df = new DocumentFactory();

	public void serialize(List<Service> services, OutputStream out) throws IOException, SerializerException {
		Element servicesElement = df.createElement(new QName(SERVICES, SERVICE_NS));

		servicesElement.addNamespace(SERVICE_NS_PREFIX, SERVICE_NS_URI);
		servicesElement.addNamespace(XSD_NS_PREFIX, XSD_URI);
		servicesElement.addNamespace(XSI_NS_PREFIX, XSI_URI);

		QName qname = df.createQName("schemaLocation", "xsi", XSI_URI);
		servicesElement.addAttribute(qname, SERVICE_SCHEMA_LOCATION);

		this.writeServices(services, servicesElement);

		Document document = df.createDocument(servicesElement);

		// write the document to the output stream
		OutputFormat format = new OutputFormat("    ", true);
		format.setEncoding("UTF-8");

		XMLWriter writer = new XMLWriter(out, format);

		writer.write(document);
		out.flush();
	}

	public void writeServices(List<Service> services, Element parent) throws SerializerException {
		if (services == null || services.size() == 0) {
			return;
		}
//		QName qname = df.createQName(SERVICES, SERVICE_NS_PREFIX, SERVICE_NS_URI);
		// 这里把tasksElement修改为servicesTask有利于强化fireFlow面向服务的概念，但是这里的parent应当
		// 就是services元素了，怎么又通过qname又给parent添加了个services子元素？ --westerly
		// Element tasksElement = Util4Serializer.addElement(parent, qname);
//		Element servicesElement = Util4Serializer.addElement(parent, qname);
		Iterator<Service> iter = services.iterator();

		while (iter.hasNext()) {
			// writeService((Service) iter.next(), tasksElement);
//			writeService((Service) iter.next(), servicesElement);
			writeService((Service) iter.next(), parent);
		}
	}

	protected void writeService(Service service, Element parent) throws SerializerException {
		QName qname = df.createQName(SERVICE, SERVICE_NS_PREFIX, SERVICE_NS_URI);
		Element serviceElement = Util4Serializer.addElement(parent, qname);

		serviceElement.addAttribute(ID, service.getId());
		serviceElement.addAttribute(NAME, service.getName());
		serviceElement.addAttribute(SERVICE_TYPE, service.getServiceType());
		if (service.getDisplayName() != null && !service.getDisplayName().trim().equals("")) {
			serviceElement.addAttribute(DISPLAY_NAME, service.getDisplayName());
		}

		if (service.getBizCategory() != null && !service.getBizCategory().trim().equals("")) {
			serviceElement.addAttribute(BIZ_CATEGORY, service.getBizCategory());
		}

		if (service.getExecutorName() != null && !service.getExecutorName().trim().equals("")) {
			serviceElement.addAttribute(EXECUTOR_NAME, service.getExecutorName());
		}

		if (service.getDescription() != null && !service.getDescription().trim().equals("")) {
			Util4Serializer.addElement(serviceElement, DESCRIPTION, service.getDescription());
		}

		writeOperations(service.getOperations(), serviceElement);
		writeServicePropGroups(service.getServicePropGroups(), serviceElement);
	}

	protected void writeOperations(List<Operation> operations, Element parent) {
		if (operations == null || operations.size() == 0) {
			return;
		}
		Element operationsElem = Util4Serializer.addElement(parent, OPERATIONS);

		for (Operation operation : operations) {
			Element operationElem = Util4Serializer.addElement(operationsElem, OPERATION);
			Util4Serializer.addElement(operationElem, OPERATION_NAME, operation.getOperationName());
			IOSpecification iospec = operation.getIOSpecification();
			if (iospec == null) {
				continue;
			}
			if ((iospec.getInputs() != null && iospec.getInputs().size() > 0)
					|| (iospec.getOutputs() != null && iospec.getOutputs().size() > 0)) {
				Element iospecElem = Util4Serializer.addElement(operationElem, IO_SPECIFICATION);
				if (iospec.getInputs() != null && iospec.getInputs().size() > 0) {
					Element inputsElem = Util4Serializer.addElement(iospecElem, INPUTS);
					List<Input> inputs = iospec.getInputs();
					for (Input input : inputs) {
						Element inputElem = Util4Serializer.addElement(inputsElem, INPUT);
						inputElem.addAttribute(NAME, input.getName());
						if (input.getDisplayName() != null && !input.getDisplayName().trim().equals("")) {
							inputElem.addAttribute(DISPLAY_NAME, input.getDisplayName());
						}
						inputElem.addAttribute(DATA_TYPE, input.getDataType());
						if (input.getDefaultValueAsString() != null && !input.getDefaultValueAsString().equals("")) {
							inputElem.addAttribute(DEFAULT_VALUE, input.getDefaultValueAsString());
						}

						if (input.getDataPattern() != null && !input.getDataPattern().trim().equals("")) {
							inputElem.addAttribute(DATA_PATTERN, input.getDataPattern());
						}
					}
				}
				if (iospec.getOutputs() != null && iospec.getOutputs().size() > 0) {
					Element outputsElem = Util4Serializer.addElement(iospecElem, OUTPUTS);
					List<Output> outputs = iospec.getOutputs();
					for (Output output : outputs) {
						Element outputElem = Util4Serializer.addElement(outputsElem, OUTPUT);
						outputElem.addAttribute(NAME, output.getName());
						if (output.getDisplayName() != null && !output.getDisplayName().trim().equals("")) {
							outputElem.addAttribute(DISPLAY_NAME, output.getDisplayName());
						}
						outputElem.addAttribute(DATA_TYPE, output.getDataType());
					}
				}
			}
		}

	}

	protected void writeServicePropGroups(List<ServicePropGroup> propGroups, Element parent) {
		if (propGroups == null || propGroups.size() == 0) {
			return;
		}
		Element propGroupsElem = Util4Serializer.addElement(parent, PROP_GROUPS);
		for (ServicePropGroup propGroup : propGroups) {
			Element propGroupElem = Util4Serializer.addElement(propGroupsElem, PROP_GROUP);
			propGroupElem.addAttribute(NAME, propGroup.getName());
			propGroupElem.addAttribute(DISPLAY_NAME, propGroup.getDisplayName());

			List<ServiceProp> propList = propGroup.getServiceProps();
			if (propList != null) {
				for (ServiceProp serviceProp : propList) {
					Element propElem = Util4Serializer.addElement(propGroupElem, PROP);
					propElem.addAttribute(NAME, serviceProp.getName());
					propElem.addAttribute(DISPLAY_NAME, serviceProp.getDisplayName());
					propElem.addAttribute(VALUE, serviceProp.getValue());
					Util4Serializer.addElement(propElem, DESCRIPTION, serviceProp.getDescription());
				}
			}
		}
	}
}
