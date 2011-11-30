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
package org.fireflow.engine.modules.persistence.classpath;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.WorkflowQuery;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.entity.repository.ResourceDescriptor;
import org.fireflow.engine.entity.repository.ResourceDescriptorProperty;
import org.fireflow.engine.entity.repository.ResourceRepository;
import org.fireflow.engine.entity.repository.impl.ResourceDescriptorImpl;
import org.fireflow.engine.entity.repository.impl.ResourceRepositoryImpl;
import org.fireflow.engine.misc.Utils;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ResourcePersister;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.io.Dom4JResourceParser;
import org.fireflow.model.io.ParserException;
import org.fireflow.model.resourcedef.Resource;


/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ResourcePersisterClassPathImpl implements ResourcePersister {
	private static Log log = LogFactory.getLog(ResourcePersisterClassPathImpl.class);

	PersistenceService persistenceService = null;
	public ResourceRepository persistResourceFileToRepository(
			InputStream resourceFileInput,
			Map<ResourceDescriptorProperty, Object> properties){
		throw new UnsupportedOperationException("This method is unsupported");
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ResourcePersister#findServiceRepositoryByFileName(java.lang.String)
	 */
	public ResourceRepository findResourceRepositoryByFileName(
			String resourceFileName) throws ParserException{
		if (resourceFileName==null || resourceFileName.trim().equals("")){
			throw new EngineException("The resource file name can NOT be empty!");
		}
		String fileName = resourceFileName;
		if (resourceFileName.startsWith("/") || resourceFileName.startsWith("\\")){
			fileName = resourceFileName.substring(1);
		}
		
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(fileName);				
		return repositoryFromInputStream(resourceFileName,inStream);
	}
	
	private ResourceRepository repositoryFromInputStream(
			String resourceFileName, InputStream inStream)
			throws ParserException {
		Dom4JResourceParser parser = new Dom4JResourceParser();
		try {
			byte[] bytes = Utils.getBytes(inStream);
			ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
			List<Resource> resources = parser.parse(bytesIn);
			
			ResourceRepositoryImpl repository = new ResourceRepositoryImpl();
			repository.setResourceContent(new String(bytes,"UTF-8"));
			repository.setResources(resources);
			repository.setFileName(resourceFileName);
			
			if (resources!=null){
				List<ResourceDescriptor> resourceDescriptors = new ArrayList<ResourceDescriptor>();
				for (Resource rsc : resources){
					ResourceDescriptorImpl desc = new ResourceDescriptorImpl();
					desc.setResourceId(rsc.getId());
					desc.setResourceType(rsc.getResourceType().getValue());
					desc.setName(rsc.getName());
					desc.setDisplayName(rsc.getDisplayName());
					desc.setDescription(rsc.getDescription());
					desc.setFileName(resourceFileName);
					
					resourceDescriptors.add(desc);
				}
			}
			
			return repository;
		} catch (ParserException e) {
			log.error(e);
			throw e;
		} catch (IOException e) {
			log.error(e);
			throw new ParserException(e);
		}

	}
	

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.Persister#count(org.fireflow.engine.WorkflowQuery)
	 */
	public <T extends WorkflowEntity> int count(WorkflowQuery<T> q) {
		throw new UnsupportedOperationException("This method is unsupported");
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.Persister#find(java.lang.Class, java.lang.String)
	 */
	public <T extends WorkflowEntity> T find(Class<T> entityClz, String entityId) {
		throw new UnsupportedOperationException("This method is unsupported");
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.Persister#list(org.fireflow.engine.WorkflowQuery)
	 */
	public <T extends WorkflowEntity> List<T> list(WorkflowQuery<T> q) {
		throw new UnsupportedOperationException("This method is unsupported");
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.Persister#saveOrUpdate(java.lang.Object)
	 */
	public void saveOrUpdate(Object entity) {
		throw new UnsupportedOperationException("This method is unsupported");
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.Persister#getPersistenceService()
	 */
	public PersistenceService getPersistenceService() {
		return persistenceService;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.Persister#setPersistenceService(org.fireflow.engine.modules.persistence.PersistenceService)
	 */
	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
		
	}
}
