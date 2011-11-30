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
import org.fireflow.model.resourcedef.ResolverDef;
import org.fireflow.model.resourcedef.Resource;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class Dom4JResourceSerializer implements ModelElementNames {
	private static DocumentFactory df = new DocumentFactory();

	public void serialize(List<Resource> resources, OutputStream out)
			throws IOException, SerializerException {
		Element resourcesElement = df.createElement(new QName(RESOURCE,
				RESOURCE_NS));

		resourcesElement.addNamespace(RESOURCE_NS_PREFIX, RESOURCE_NS_URI);
		resourcesElement.addNamespace(XSD_NS_PREFIX, XSD_URI);
		resourcesElement.addNamespace(XSI_NS_PREFIX, XSI_URI);

		QName qname = df.createQName("schemaLocation", "xsi", XSI_URI);
		resourcesElement.addAttribute(qname, RESOURCE_SCHEMA_LOCATION);

		this.writeResources(resources, resourcesElement);

		Document document = df.createDocument(resourcesElement);

		// write the document to the output stream
		OutputFormat format = new OutputFormat("    ", true);
		format.setEncoding("UTF-8");

		XMLWriter writer = new XMLWriter(out, format);

		writer.write(document);
		out.flush();
	}

	public void writeResources(List<Resource> resources, Element parent)
			throws SerializerException {
		if (resources == null || resources.size() == 0) {
			return;
		}
		QName qname = df.createQName(RESOURCES, RESOURCE_NS_PREFIX,
				RESOURCE_NS_URI);
		Element resourcesElement = Util4Serializer.addElement(parent, qname);
		Iterator<Resource> iter = resources.iterator();

		while (iter.hasNext()) {
			writeResource((Resource) iter.next(), resourcesElement);
		}
	}

	protected void writeResource(Resource r, Element resourcesElement) {
		Element resourceElem = Util4Serializer.addElement(resourcesElement,
				RESOURCE);
		resourceElem.addAttribute(ID, r.getId());
		resourceElem.addAttribute(NAME, r.getName());
		if (r.getDisplayName() != null && !r.getDisplayName().trim().equals("")) {
			resourceElem.addAttribute(DISPLAY_NAME, r.getDisplayName());
		}

		resourceElem
				.addAttribute(RESOURCE_TYPE, r.getResourceType().getValue());

		ResolverDef resolverDef = r.getResolver();
		if (resolverDef != null) {
			Element resolverElem = Util4Serializer.addElement(resourceElem,
					RESOLVER);
			resolverElem.addAttribute(BEAN_NAME, resolverDef.getBeanName());

			List<Input> parameters = resolverDef.getParameters();
			if (parameters != null && parameters.size() > 0) {
				Element parametersElem = Util4Serializer.addElement(
						resolverElem, PARAMETERS);
				for (Input input : parameters) {
					Element inputElem = Util4Serializer.addElement(
							parametersElem, PARAMETER);
					inputElem.addAttribute(NAME, input.getName());
					if (input.getDisplayName() != null
							&& !input.getDisplayName().trim().equals("")) {
						inputElem.addAttribute(DISPLAY_NAME, input
								.getDisplayName());
					}
					inputElem.addAttribute(DATA_TYPE, input.getDataType());
					if (input.getDefaultValueAsString() != null
							&& !input.getDefaultValueAsString().equals("")) {
						inputElem.addAttribute(DEFAULT_VALUE, input
								.getDefaultValueAsString());
					}

					if (input.getDataPattern() != null
							&& !input.getDataPattern().trim().equals("")) {
						inputElem.addAttribute(DATA_PATTERN, input
								.getDataPattern());
					}
				}
			}
		}
	}

}
