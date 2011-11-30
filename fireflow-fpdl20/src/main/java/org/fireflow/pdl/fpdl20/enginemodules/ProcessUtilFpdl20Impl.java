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
package org.fireflow.pdl.fpdl20.enginemodules;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.entity.repository.ResourceRepository;
import org.fireflow.engine.entity.repository.ServiceRepository;
import org.fireflow.engine.entity.repository.impl.ProcessRepositoryImpl;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.engine.modules.persistence.ResourcePersister;
import org.fireflow.engine.modules.persistence.ServicePersister;
import org.fireflow.engine.modules.process.ProcessUtil;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.io.ParserException;
import org.fireflow.model.resourcedef.Resource;
import org.fireflow.model.servicedef.Service;
import org.fireflow.pdl.fpdl20.io.Dom4JFPDLParser;
import org.fireflow.pdl.fpdl20.io.Dom4JFPDLSerializer;
import org.fireflow.pdl.fpdl20.io.ImportLoader;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;

/**
 * TODO 等fire2.0 流程xsd出来后，此处需要用FPDLSerializer和FPDLDeserializer改造。
 * @author 非也
 * @version 2.0
 */
public class ProcessUtilFpdl20Impl implements
		ProcessUtil {
	RuntimeContext ctx = null;

    public ServiceBinding getServiceBinding(ProcessKey processKey,String activityId)throws InvalidModelException{
    	WorkflowProcess process = (WorkflowProcess)this.getWorkflowProcess(processKey);
    	if (process==null) return null;
    	Activity activity = process.getActivity(activityId);
    	if (activity==null){
    		return null;
    	}else{
    		return activity.getServiceBinding();
    	}
    }
    
    public ResourceBinding getResourceBinding(ProcessKey processKey,String activityId)throws InvalidModelException{
    	WorkflowProcess process = (WorkflowProcess)this.getWorkflowProcess(processKey);
    	if (process==null) return null;
    	Activity activity = process.getActivity(activityId);
    	if (activity==null){
    		return null;
    	}else{
    		return activity.getResourceBinding();
    	}
    }
    
    public Object getActivity(ProcessKey processKey,String activityId)throws InvalidModelException{
    	WorkflowProcess process = (WorkflowProcess)this.getWorkflowProcess(processKey);
    	if (process==null) return null;
    	Activity activity = process.getActivity(activityId);
    	return activity;
    }
	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#getRuntimeContext()
	 */
	public RuntimeContext getRuntimeContext() {
		
		return ctx;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#setRuntimeContext(org.fireflow.engine.context.RuntimeContext)
	 */
	public void setRuntimeContext(RuntimeContext ctx) {
		this.ctx = ctx;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.repository.ProcessRepositoryService#getWorkflowProcess(org.fireflow.engine.entity.repository.ProcessKey)
	 */
	protected Object getWorkflowProcess(ProcessKey processKey) throws InvalidModelException{
		PersistenceService persistenceService = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE);
		ProcessPersister processPersister = persistenceService.getProcessPersister();
		ProcessRepository repository = processPersister.findProcessRepositoryByProcessKey(processKey);
		return repository.getProcess();
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.process.ProcessUtil#deserializeXml2Process(java.lang.String)
	 */
	public Object deserializeXml2Process(InputStream inStream) throws InvalidModelException{
		if (inStream==null) return null;
		Dom4JFPDLParser parser = new Dom4JFPDLParser();
		
		InnerImportLoader importLoader = new InnerImportLoader();
		parser.setImportLoader(importLoader);
		
		try {
			WorkflowProcess process;
			process = parser.parse(inStream);
			
			return process;
		} catch(IOException e){
			throw new InvalidModelException(e);
		}


	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.process.ProcessUtil#serializeProcess2Xml(java.lang.Object)
	 */
	public String serializeProcess2Xml(Object process) throws InvalidModelException{
		WorkflowProcess wfProcess = (WorkflowProcess)process;
		Dom4JFPDLSerializer ser = new Dom4JFPDLSerializer();
		try {
			return ser.workflowProcessToXMLString(wfProcess);
		} catch (IOException e) {
			
			throw new InvalidModelException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.process.ProcessUtil#serializeProcess2ProcessRepository(java.lang.Object)
	 */
	public ProcessRepository serializeProcess2ProcessRepository(Object process) throws InvalidModelException{
		WorkflowProcess wfProcess = (WorkflowProcess)process;
		ProcessRepositoryImpl repository = new ProcessRepositoryImpl();

		repository.setProcessId(wfProcess.getId());
		repository.setProcessType(FpdlConstants.PROCESS_TYPE);
		repository.setName(wfProcess.getName());
		String displayName = wfProcess.getDisplayName();
		repository.setDisplayName((displayName==null || displayName.trim().equals(""))?wfProcess.getName():displayName);
		repository.setDescription(wfProcess.getDescription());
		
		repository.setProcess(process);
		repository.setProcessAsXml(this.serializeProcess2Xml(process));	

		return repository;
	}
	

	private class InnerImportLoader implements ImportLoader{

		/* (non-Javadoc)
		 * @see org.fireflow.pdl.fpdl20.io.ImportLoader#loadResources(java.lang.String)
		 */
		public List<Resource> loadResources(String resourceLocation)
				throws ParserException, IOException {
			PersistenceService persistenceService = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE);
			ResourcePersister resourcePersister = persistenceService.getResourcePersister();
			
			ResourceRepository repository = resourcePersister.findResourceRepositoryByFileName(resourceLocation);
			return repository.getResources();
		}

		/* (non-Javadoc)
		 * @see org.fireflow.pdl.fpdl20.io.ImportLoader#loadServices(java.lang.String)
		 */
		public List<Service> loadServices(String serviceLocation)
				throws ParserException, IOException {
			PersistenceService persistenceService = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE);
			ServicePersister servicePersister = persistenceService.getServicePersister();
			
			ServiceRepository repository = servicePersister.findServiceRepositoryByFileName(serviceLocation);
			return repository.getServices();
		}
		
	}
}
