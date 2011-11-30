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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.fireflow.model.data.impl.InputImpl;
import org.fireflow.model.data.impl.OutputImpl;
import org.fireflow.model.servicedef.Service;
import org.fireflow.model.servicedef.ServicePropGroup;
import org.fireflow.model.servicedef.impl.IOSpecificationImpl;
import org.fireflow.model.servicedef.impl.OperationImpl;
import org.fireflow.model.servicedef.impl.ServiceImpl;
import org.fireflow.model.servicedef.impl.ServicePropGroupImpl;
import org.fireflow.model.servicedef.impl.ServicePropImpl;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class Dom4JServiceParser implements ModelElementNames {
	public List<Service> parse(InputStream in) throws IOException,
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

			Element servicesElement = document.getRootElement();
			
			List<Service> services = new ArrayList<Service>();
			this.loadServices(services, servicesElement);
			
			return services;
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new ParserException("Error parsing document.", e);
		} finally {
		}
	}
	
	public void loadServices(List<Service> services,Element servicesElem){
		if (servicesElem==null) return;
		services.clear();
		List<Element> svcElms = Util4Parser.children(servicesElem,SERVICE);
		if (svcElms==null)return;
		for(Element svcElm : svcElms){
			ServiceImpl service = new ServiceImpl();
			service.setName(svcElm.attributeValue(NAME));
			service.setDisplayName(svcElm.attributeValue(DISPLAY_NAME));
//			service.setFileName(svcElm.attributeValue(FILE_NAME));
			service.setServiceType(svcElm.attributeValue(SERVICE_TYPE));
			service.setBizCategory(svcElm.attributeValue(BIZ_CATEGORY));
			service.setDescription(Util4Parser.elementAsString(svcElm, DESCRIPTION));
			service.setExecutorName(svcElm.attributeValue(EXECUTOR_NAME));
			service.setSn(UUID.randomUUID().toString());
			
			Element operationsElem = Util4Parser.child(svcElm, OPERATIONS);
			if (operationsElem!=null){
				List<Element> operationElements = Util4Parser.children(operationsElem, OPERATION);
				if (operationElements!=null){
					for (Element operationElm : operationElements){
						OperationImpl operation = new OperationImpl();
						operation.setOperationName(Util4Parser.elementAsString(operationElm, OPERATION_NAME));
						
						Element ioSpecElem = Util4Parser.child(operationElm,IO_SPECIFICATION);
						if (ioSpecElem!=null){
							IOSpecificationImpl iospec = new IOSpecificationImpl();
							
							Element inputsElm = Util4Parser.child(ioSpecElem, INPUTS);
							if (inputsElm!=null){
								List<Element> inputElms = Util4Parser.children(inputsElm, INPUT);
								if (inputElms!=null){
									for (Element inputElm : inputElms){
										InputImpl input = new InputImpl();
										input.setName(inputElm.attributeValue(NAME));
										input.setDisplayName(inputElm.attributeValue(DISPLAY_NAME));
										input.setDataType(inputElm.attributeValue(DATA_TYPE));
										input.setDataPattern(inputElm.attributeValue(DATA_PATTERN));
										input.setDefaultValueAsString(inputElm.attributeValue(DEFAULT_VALUE));
										
										iospec.getInputs().add(input);
									}
								}
							}
							
							Element outputsElm = Util4Parser.child(ioSpecElem, OUTPUTS);
							if (outputsElm!=null){
								List<Element> outputElms = Util4Parser.children(outputsElm, OUTPUT);
								if (outputElms!=null){
									for (Element outputElm : outputElms){
										OutputImpl output = new OutputImpl();
										output.setName(outputElm.attributeValue(NAME));
										output.setDisplayName(outputElm.attributeValue(DISPLAY_NAME));
										output.setDataType(outputElm.attributeValue(DATA_TYPE));
										
										iospec.getOutputs().add(output);
									}
								}
							}
							
							operation.setIOSpecification(iospec);
						}//if (ioSpecElem!=null)
						
						service.getOperations().add(operation);
						
					}//for (Element operationElm : operationElements){
				}
			}
			
			Element propGroupsElem = Util4Parser.child(svcElm, PROP_GROUPS);
			if (propGroupsElem!=null){
				List<Element> propGroupElems = Util4Parser.children(propGroupsElem, PROP_GROUP);
				if (propGroupElems!=null){
					for (Element propGroupElm : propGroupElems){
						ServicePropGroup servicePropGroup = new ServicePropGroupImpl();
						servicePropGroup.setName(propGroupElm.attributeValue(NAME));
						servicePropGroup.setDisplayName(propGroupElm.attributeValue(DISPLAY_NAME));
						
						List<Element> propElems = Util4Parser.children(propGroupElm, PROP);
						if (propElems!=null){
							for (Element propElm : propElems){
								ServicePropImpl prop = new ServicePropImpl();
								prop.setName(propElm.attributeValue(NAME));
								prop.setDisplayName(propElm.attributeValue(DISPLAY_NAME));
								prop.setValue(propElm.attributeValue(VALUE));
								prop.setDescription(Util4Parser.elementAsString(propElm, DESCRIPTION));
								
								servicePropGroup.getServiceProps().add(prop);
							}
						}
						service.getServicePropGroups().add(servicePropGroup);
						
					}//for (Element propGroupElm : propGroupElems)
				}
			}
			services.add(service);
		}//for(Element svcElm : svcElms)
	}
		
}
