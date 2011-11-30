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
import org.fireflow.engine.entity.repository.ServiceDescriptor;
import org.fireflow.engine.entity.repository.ServiceDescriptorProperty;
import org.fireflow.engine.entity.repository.ServiceRepository;
import org.fireflow.engine.entity.repository.impl.ServiceDescriptorImpl;
import org.fireflow.engine.entity.repository.impl.ServiceRepositoryImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.misc.Utils;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ServicePersister;
import org.fireflow.model.io.Dom4JServiceParser;
import org.fireflow.model.io.ParserException;
import org.fireflow.model.servicedef.Service;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ServicePersisterClassPathImpl implements ServicePersister {
	private static Log log = LogFactory.getLog(ServicePersisterClassPathImpl.class);

	PersistenceService persistenceService = null;
	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ServicePersister#findServiceRepositoryByFileName(java.lang.String)
	 */
	public ServiceRepository findServiceRepositoryByFileName(
			String serviceFileName) throws ParserException{
		if (serviceFileName==null || serviceFileName.trim().equals("")){
			throw new EngineException("The resource file name can NOT be empty!");
		}
		String fileName = serviceFileName;
		if (serviceFileName.startsWith("/") || serviceFileName.startsWith("\\")){
			fileName = serviceFileName.substring(1);
		}
		
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(fileName);				
		return repositoryFromInputStream(serviceFileName,inStream);
	}
	private ServiceRepository repositoryFromInputStream(String serviceFileName,InputStream inStream)throws ParserException{
		Dom4JServiceParser parser = new Dom4JServiceParser();
		try {
			
			byte[] bytes = Utils.getBytes(inStream);
			ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
			List<Service> services = parser.parse(bytesIn);
			
			ServiceRepositoryImpl repository = new ServiceRepositoryImpl();
			repository.setServiceContent(new String(bytes,"UTF-8"));
			repository.setFileName(serviceFileName);
			repository.setServices(services);
			
			
			if (services!=null){
				List<ServiceDescriptor> serviceDescriptors = new ArrayList<ServiceDescriptor>();
				for (Service svc : services){
					ServiceDescriptorImpl desc = new ServiceDescriptorImpl();
					desc.setServiceId(svc.getId());
					desc.setBizCategory(svc.getBizCategory());
					desc.setName(svc.getName());
					desc.setDisplayName(svc.getDisplayName());
					desc.setDescription(svc.getDescription());
					
					desc.setFileName(serviceFileName);
					
					serviceDescriptors.add(desc);
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
	 * @see org.fireflow.engine.modules.persistence.ServicePersister#persistServiceFileToRepository(java.io.InputStream, java.util.Map)
	 */
	public ServiceRepository persistServiceFileToRepository(
			InputStream serviceFileInput,
			Map<ServiceDescriptorProperty, Object> properties) {
		throw new UnsupportedOperationException("This method is unsupported");
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
