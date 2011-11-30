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
package org.fireflow.engine.modules.persistence.hibernate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.entity.repository.ServiceDescriptorProperty;
import org.fireflow.engine.entity.repository.ServiceRepository;
import org.fireflow.engine.entity.repository.impl.ServiceDescriptorImpl;
import org.fireflow.engine.entity.repository.impl.ServiceRepositoryImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.misc.Utils;
import org.fireflow.engine.modules.persistence.ServicePersister;
import org.fireflow.model.io.Dom4JServiceParser;
import org.fireflow.model.io.ParserException;
import org.fireflow.model.servicedef.Service;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ServicePersisterHibernateImpl extends AbsPersisterHibernateImpl implements ServicePersister {
	private static Log log = LogFactory.getLog(ServicePersisterHibernateImpl.class);

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ServicePersister#findServiceRepositoryByFileName(java.lang.String)
	 */
	public ServiceRepository findServiceRepositoryByFileName(
			final String serviceFileName) {
		ServiceRepository result = (ServiceRepository)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria c = session.createCriteria(ServiceRepositoryImpl.class);
				c.add(Restrictions.eq("fileName", serviceFileName));
				
				return c.uniqueResult();
			}
			
		});

		if (result!=null && result.getServiceContent()!=null){
			try {
				ByteArrayInputStream byteIn = new ByteArrayInputStream(result.getServiceContent().getBytes("UTF-8"));
				
				Dom4JServiceParser parser = new Dom4JServiceParser();
				List<Service> services = parser.parse(byteIn);
				
				((ServiceRepositoryImpl)result).setServices(services);
			} catch (UnsupportedEncodingException e) {
				log.error(e);
			}catch(ParserException e){
				log.error(e);
			}
			catch(IOException e){
				log.error(e);
			}
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ServicePersister#persistServiceFileToRepository(java.io.InputStream, java.util.Map)
	 */
	public ServiceRepository persistServiceFileToRepository(
			InputStream serviceFileInput,
			Map<ServiceDescriptorProperty, Object> properties) {
		if (properties==null) throw new EngineException("The service descriptor properties can NOT be emtpy!");
		final String fileName = (String)properties.get(ServiceDescriptorProperty.FILE_NAME);
		String lastEditor = (String)properties.get(ServiceDescriptorProperty.LATEST_EDITOR);
		Date lastEditTime = (Date)properties.get(ServiceDescriptorProperty.LATEST_EDIT_TIME);
		
		if (fileName==null || fileName.trim().equals("")){
			throw new EngineException("The FILE_NAME property can NOT be emtpy!");
		}
		ServiceRepository repository = repositoryFromInputStream(fileName,serviceFileInput);
		
		
		
		this.saveOrUpdate(repository);
		
		//将Service Descriptor先删后插……
		this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String delete = "Delete From ServiceDescriptorImpl m Where m.fileName=:fileName";
				
				Query q4Delete = session.createQuery(delete);
				q4Delete.setString("fileName", fileName);
				q4Delete.executeUpdate();
				return null;
			}
			
		});
		
		
		List<Service> services = repository.getServices();
		if (services!=null){
			for (Service svc : services){
				ServiceDescriptorImpl desc = new ServiceDescriptorImpl();
				desc.setServiceId(svc.getId());
				desc.setBizCategory(svc.getBizCategory());
				desc.setName(svc.getName());
				desc.setDisplayName(svc.getDisplayName());
				desc.setDescription(svc.getDescription());
				
				desc.setFileName(fileName);
				desc.setLatestEditor(lastEditor);
				if (lastEditTime!=null){
					desc.setLatestEditTime(lastEditTime);
				}else{
					desc.setLatestEditTime(new Date());
				}
				
				this.saveOrUpdate(desc);
			}
		}
		return repository;
	}

	private ServiceRepository repositoryFromInputStream(String serviceFileName,InputStream inStream){
		ServiceRepositoryImpl repository = (ServiceRepositoryImpl)this.findServiceRepositoryByFileName(serviceFileName);
		if (repository==null){
			repository = new ServiceRepositoryImpl();
		}
		
		Dom4JServiceParser parser = new Dom4JServiceParser();
		try {
			byte[] bytes = Utils.getBytes(inStream);
			ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
			List<Service> services = parser.parse(bytesIn);
			
			
			repository.setServiceContent(new String(bytes,"UTF-8"));
			repository.setFileName(serviceFileName);
			repository.setServices(services);
			return repository;
		} catch (ParserException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.hibernate.AbsPersisterHibernateImpl#getEntityClass4Runtime(java.lang.Class)
	 */
	@Override
	public Class getEntityClass4Runtime(Class interfaceClz) {
		return ServiceDescriptorImpl.class;
	}

}
