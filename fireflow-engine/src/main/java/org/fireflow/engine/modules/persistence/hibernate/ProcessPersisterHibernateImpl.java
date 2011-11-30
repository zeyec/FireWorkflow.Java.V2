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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessDescriptorProperty;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.engine.entity.repository.impl.ProcessRepositoryImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.engine.modules.process.ProcessUtil;
import org.fireflow.model.InvalidModelException;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;


/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ProcessPersisterHibernateImpl extends
		AbsPersisterHibernateImpl implements ProcessPersister {
	private static final Log log = LogFactory.getLog(ProcessPersisterHibernateImpl.class);
	
	//TODO 下面这个Cache是否需要，待研究……
	Map<ProcessKey,ProcessRepository> cache = new HashMap<ProcessKey,ProcessRepository>();
	private boolean useProcessCache = false;
	
	public boolean isUseProcessCache(){
		return useProcessCache;
	}
	
	public void setUseProcessCache(boolean b){
		this.useProcessCache = b;
	}


	@SuppressWarnings("unchecked")
	public Class getEntityClass4Runtime(Class interfaceClz){
		if (interfaceClz.isAssignableFrom(ProcessDescriptor.class)){
			return ProcessDescriptorImpl.class;
		}else if (interfaceClz.isAssignableFrom(ProcessRepository.class)){
			return ProcessRepositoryImpl.class;
		}
		return null;		
	}

	public ProcessDescriptor findProcessDescriptorByProcessKey(
			final ProcessKey processKey){
		ProcessRepository repository = this.getFromCache(processKey);
		if (repository!=null) {
			return repository;
		}
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query q = session.createQuery("from ProcessDescriptorImpl c where c.processId=:processId and c.processType=:processType and c.version=:version");
				q.setString("processId", processKey.getProcessId());
				q.setString("processType", processKey.getProcessType());
				q.setInteger("version", processKey.getVersion());
				return q.uniqueResult();
			}
		});
		return (ProcessDescriptor)result;
	}


	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.ProcessRepositoryPersister#findProcessRepositoryByProcessKey(org.fireflow.engine.entity.repository.ProcessKey)
	 */
	public ProcessRepository findProcessRepositoryByProcessKey(
			final ProcessKey processKey) throws InvalidModelException{		
		ProcessRepository repository = this.getFromCache(processKey);
		if (repository!=null) {
			return repository;
		}
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query q = session.createQuery("from ProcessRepositoryImpl c where c.processId=:processId and c.processType=:processType and c.version=:version");
				q.setString("processId", processKey.getProcessId());
				q.setString("processType", processKey.getProcessType());
				q.setInteger("version", processKey.getVersion());
				return q.uniqueResult();
			}
			
		});
		
		repository = (ProcessRepository)result;
		if (repository != null) {
			try{
				ProcessUtil processUtil = persistenceService.getProcessUtil(processKey.getProcessType());
				String xml = repository.getProcessAsXml();
				ByteArrayInputStream inStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
				Object obj = processUtil.deserializeXml2Process(inStream);
				((ProcessRepositoryImpl)repository).setProcess(obj);
			}catch(UnsupportedEncodingException e){
				log.error(e);
			}

		}
		
		return repository;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.ProcessRepositoryPersister#findTheLatestVersion(java.lang.String, java.lang.String)
	 */
	public int findTheLatestVersion(final String processId, final String processType) {
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query q = session.createQuery("select max(c.version) from ProcessDescriptorImpl c where c.processId=:processId and c.processType=:processType");
				q.setString("processId", processId);
				q.setString("processType", processType);
				return q.uniqueResult();
			}
			
		});
		if (result==null){
			return 0;
		}else{
			if (result instanceof Integer){
				return (Integer)result;
			}
			else if (result instanceof Long){
				return ((Long)result).intValue();
			}else{
				return 0;
			}
		}

	}
	public int findTheLatestPublishedVersion(final String processId,final String processType){
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query q = session.createQuery("select max(c.version) from ProcessDescriptorImpl c where c.processId=:processId and c.processType=:processType and c.publishState=:publishState");
				q.setString("processId", processId);
				q.setString("processType", processType);
				q.setBoolean("publishState", Boolean.TRUE);
				return q.uniqueResult();
			}
			
		});
		if (result==null){
			return 0;
		}else{
			if (result instanceof Integer){
				return (Integer)result;
			}
			else if (result instanceof Long){
				return ((Long)result).intValue();
			}else{
				return 0;
			}
		}
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.ProcessRepositoryPersister#findTheLatestVersionOfProcessDescriptor(java.lang.String, java.lang.String)
	 */
	public ProcessDescriptor findTheLatestVersionOfProcessDescriptor(
			String processId, String processType) {
		int v = this.findTheLatestVersion(processId,processType) ;
		if (v==0){
			return null;
		}else{
			ProcessKey processKey = new ProcessKey(processId,v,processType);
			ProcessRepository repository = this.getFromCache(processKey);
			if (repository!=null) {
				return repository;
			}else{
				return this.findProcessDescriptorByProcessKey(new ProcessKey(processId,v,processType));
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.ProcessRepositoryPersister#findTheLatestVersionOfProcessRepository(java.lang.String, java.lang.String)
	 */
	public ProcessRepository findTheLatestVersionOfProcessRepository (
			String processId, String processType) throws InvalidModelException{
		int v = this.findTheLatestVersion(processId,processType) ;
		if (v==0){
			return null;
		}else{
			ProcessKey processKey = new ProcessKey(processId,v,processType);
			ProcessRepository repository = this.getFromCache(processKey);
			if (repository!=null) {
				return repository;
			}else{
				return this.findProcessRepositoryByProcessKey(new ProcessKey(processId,v,processType));
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ProcessPersister#persistProcessToRepository(java.lang.Object, java.util.Map)
	 */
	public ProcessRepository persistProcessToRepository(Object process,
			Map<ProcessDescriptorProperty, Object> descriptorKeyValues)throws InvalidModelException {	
		
		if (descriptorKeyValues == null){
			throw new EngineException("The process metadata can NOT be null");
		}
		String processType = (String)descriptorKeyValues.get(ProcessDescriptorProperty.PROCESS_TYPE);
		if (processType==null || processType.trim().equals("")){
			throw new EngineException("The processType can NOT be emtpy!");
		}
		
		ProcessUtil processUtil = persistenceService.getProcessUtil(processType);
		
		ProcessRepositoryImpl processRepository = (ProcessRepositoryImpl)processUtil.serializeProcess2ProcessRepository(process);
		
		//1.processType
		processRepository.setProcessType(processType);
		
		//2.processId
		String processId = (String)descriptorKeyValues.get(ProcessDescriptorProperty.PROCESS_ID);
		if (processId == null || processId.trim().equals("")){
			processId = processRepository.getProcessId();
			if (processId == null || processId.trim().equals("")){
				throw new EngineException("The processId can NOT be emtpy!");
			}
		}else{
			processRepository.setProcessId(processId);
		}
		//3.version
		Integer version = (Integer)descriptorKeyValues.get(ProcessDescriptorProperty.VERSION);
		ProcessDescriptor existProcess = null;//同一版本的流程是否已经存在。
		if (version!=null && version>0){
			processRepository.setVersion(version);
			existProcess = this.findProcessDescriptorByProcessKey(new ProcessKey(processId,version,processType));
		}else{
			existProcess = this.findTheLatestVersionOfProcessDescriptor(processId, processType);
			if (existProcess==null){
				version = 1;
			}else{
				version = existProcess.getVersion()+1;
			}
			processRepository.setVersion(version); 
		}
		
		//4.processName
		String processName = (String)descriptorKeyValues.get(ProcessDescriptorProperty.NAME);
		if (processName!=null && !processName.trim().equals("")){
			processRepository.setName(processName);		
		}
		
		//5.displayName
		String displayName = (String)descriptorKeyValues.get(ProcessDescriptorProperty.DISPLAY_NAME);
		if (displayName!=null && !displayName.trim().equals("")){
			processRepository.setDisplayName(displayName);	
		}
		
		//6.description
		String description = (String)descriptorKeyValues.get(ProcessDescriptorProperty.DESCRIPTION);
		if (description!=null && !description.trim().equals("")){
			processRepository.setDescription(description);
		}

		//7.process
		processRepository.setProcess(process);
		

		//8.latestEditTime
		Date d = (Date)descriptorKeyValues.get(ProcessDescriptorProperty.LATEST_EDIT_TIME);
		if (d==null){
			processRepository.setLatestEditTime(new Date());
		}else{
			processRepository.setLatestEditTime(d);
		}


		//9.publishState
		Boolean publishState = null;	
		if (existProcess!=null){
			publishState = existProcess.getPublishState();
		}
		Boolean stateFromArgs = null;
		if (descriptorKeyValues!=null && descriptorKeyValues
					.get(ProcessDescriptorProperty.PUBLISH_STATE)!=null){
			if (descriptorKeyValues
					.get(ProcessDescriptorProperty.PUBLISH_STATE) instanceof Boolean){
				stateFromArgs = (Boolean) descriptorKeyValues
				.get(ProcessDescriptorProperty.PUBLISH_STATE);
			}
		}	
		publishState = stateFromArgs;
		if (publishState==null){
			publishState = false;
		}		
		processRepository.setPublishState(publishState);	
		
		//10.latestOperation
		boolean publishStateChanged = false;//发布状态是否改变
		if (existProcess!=null && stateFromArgs!=null){
			boolean oldState = existProcess.getPublishState();
			if (oldState!=stateFromArgs){
				publishStateChanged = true;
			}
		}
		String latestOperation = ProcessDescriptor.OPERATION_UPLOAD;
		if (existProcess==null){
			if (publishState){
				latestOperation = ProcessDescriptor.OPERATION_UPLOAD_PUBLISH;
			}
		}else{
			if (!publishStateChanged){
				latestOperation = ProcessDescriptor.OPERATION_UPDATE;
			}else{
				if (publishState){
					latestOperation = ProcessDescriptor.OPERATION_UPDATE_PUBLISH;
				}else{
					latestOperation = ProcessDescriptor.OPERATION_UPDATE_UNPUBLISH;
				}
			}
		}
		processRepository.setLatestOperation(latestOperation);
		
		//11.latestEditor
		processRepository.setLatestEditor((String) descriptorKeyValues
				.get(ProcessDescriptorProperty.LATEST_EDITOR));
		
		//12,filename
		String fileName = (String) descriptorKeyValues
		.get(ProcessDescriptorProperty.FILE_NAME);
		if (fileName==null || fileName.trim().equals("")){
			fileName=processId+".fpdl20.xml";
		}
		processRepository.setFileName(fileName);

		//12.other properties
		processRepository.setOwnerDeptId((String) descriptorKeyValues
				.get(ProcessDescriptorProperty.OWNER_DEPT_ID));
		processRepository.setOwnerDeptName((String) descriptorKeyValues
				.get(ProcessDescriptorProperty.OWNER_DEPT_NAME));

		processRepository.setApprover((String) descriptorKeyValues
				.get(ProcessDescriptorProperty.APPROVER));
		processRepository.setApprovedTime((Date) descriptorKeyValues
				.get(ProcessDescriptorProperty.APPROVED_TIME));


		this.saveOrUpdate(processRepository);
		
		this.cache(processRepository);
		
		return processRepository;

	}

	
//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.persistence.ProcessRepositoryPersister#updateProcessDescriptor(org.fireflow.engine.entity.repository.ProcessDescriptor)
//	 */
//	@Override
//	public void updateProcessDescriptor(ProcessDescriptor processDescriptor) {
//		this.getHibernateTemplate().update(processDescriptor);
//
//	}
//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.persistence.ProcessRepositoryPersister#findAllTheLatestVersionsOfProcessRepository(java.lang.String)
//	 */
//	@Override
//	public java.util.List<ProcessRepository> findAllTheLatestVersionsOfProcessRepository(
//			String processType) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	protected void cache(ProcessRepository processRepository){
		if (this.isUseProcessCache()) {
			ProcessKey pk = new ProcessKey(processRepository.getProcessId(),
					processRepository.getVersion(), processRepository
							.getProcessType());
			this.cache.put(pk, processRepository);
		}
	}
	
	protected ProcessRepository getFromCache(ProcessKey key){
		if (this.isUseProcessCache()){
			return this.cache.get(key);
		}
		return null;
	}
}
