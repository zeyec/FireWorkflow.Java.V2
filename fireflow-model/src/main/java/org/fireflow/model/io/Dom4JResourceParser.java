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
import org.fireflow.model.resourcedef.Resource;
import org.fireflow.model.resourcedef.ResourceType;
import org.fireflow.model.resourcedef.impl.ResolverDefImpl;
import org.fireflow.model.resourcedef.impl.ResourceImpl;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class Dom4JResourceParser implements ModelElementNames{
	public List<Resource> parse(InputStream in) throws IOException,
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

			Element resourcesElement = document.getRootElement();
			
			List<Resource> resources = new ArrayList<Resource>();
			loadResources(resources,resourcesElement);
			
			return resources;
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new ParserException("Error parsing document.", e);
		} finally {
		}
	}
	
	public void loadResources(List<Resource> resources,Element resourcesElem){
		if(resourcesElem==null) return;
		resources.clear();
		List<Element> rscElms = Util4Parser.children(resourcesElem, RESOURCE);
		if (rscElms==null) return;
		for (Element rscElm : rscElms){
			ResourceImpl resource = new ResourceImpl();
			resource.setName(rscElm.attributeValue(NAME));
			resource.setDisplayName(rscElm.attributeValue(DISPLAY_NAME));
//			resource.setFileName(rscElm.attributeValue(FILE_NAME));
			
			resource.setSn(UUID.randomUUID().toString());
			String resourceType = rscElm.attributeValue(RESOURCE_TYPE);
			resource.setResourceType(ResourceType.fromValue(resourceType));
			
			resource.setDescription(Util4Parser.elementAsString(rscElm, DESCRIPTION));
			
			Element resolverElm = Util4Parser.child(rscElm, RESOLVER);
			if (resolverElm!=null){
				ResolverDefImpl resolver = new ResolverDefImpl();
				resolver.setBeanName(resolverElm.attributeValue(BEAN_NAME));
				
				Element paramsElem = Util4Parser.child(resolverElm,PARAMETERS);
				if (paramsElem!=null){
					List<Element> paramElems = Util4Parser.children(paramsElem, PARAMETER);
					if (paramElems!=null){
						for (Element paramElm : paramElems){
							InputImpl input = new InputImpl();
							input.setName(paramElm.attributeValue(NAME));
							input.setDisplayName(paramElm.attributeValue(DISPLAY_NAME));
							input.setDataType(paramElm.attributeValue(DATA_TYPE));
							input.setDataPattern(paramElm.attributeValue(DATA_PATTERN));
							input.setDefaultValueAsString(paramElm.attributeValue(DEFAULT_VALUE));
							
							resolver.getParameters().add(input);
						}
					}
				}
				
				resource.setResolver(resolver);
			}//if (resolverElm!=null)
			
			resources.add(resource);
		}//for (Element rscElm : rscElms)
	}
	
}
