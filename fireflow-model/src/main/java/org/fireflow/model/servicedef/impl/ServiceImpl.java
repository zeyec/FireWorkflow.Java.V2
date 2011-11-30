package org.fireflow.model.servicedef.impl;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.AbstractModelElement;
import org.fireflow.model.servicedef.Operation;
import org.fireflow.model.servicedef.Service;
import org.fireflow.model.servicedef.ServicePropGroup;

public class ServiceImpl extends AbstractModelElement implements Service{
	String executorName = null;
	String bizCategory = null;
//	String fileName = null;
	String serviceType = null;
	List<Operation> operations = new ArrayList<Operation>();
	List<ServicePropGroup> servicePropGroups= new ArrayList<ServicePropGroup>();
	
	public ServiceImpl(){
		init();
	}
	private void init(){
		ServicePropGroupImpl servicePropGroup = new ServicePropGroupImpl();
		servicePropGroup.setName(ServicePropGroup.COMMON_PROPERTIES_GROUP);
		servicePropGroup.setDisplayName(ServicePropGroup.COMMON_PROPERTIES_GROUP);
		servicePropGroups.add(servicePropGroup);

	}
	
    /**
     * Service的业务类别，便于分类管理。格式如下：<br>
     * 受理科业务/车管受理/新车登记
     * @return
     */
    public String getBizCategory(){
    	return bizCategory;
    }
    
    /**
     * 设置服务的业务类别
     * @param category
     */
    public void setBizCategory(String category){
    	this.bizCategory = category;
    }
    
    
    /**
     * 服务类型，如Form,Java,Subflow等等
     * @return
     */
    public String getServiceType(){
    	return this.serviceType;
    }
    
    
    public void setServiceType(String svcType){
    	this.serviceType = svcType;
    }




	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedesc.Service#getOperation()
	 */
	public Operation getOperation(String opName) {
		for (Operation op : operations){
			if (op.getOperationName().equals(opName)){
				return op;
			}
		}
		return null;
	}
	
	public void setOperation(Operation op){
		this.operations.add(op);
	}
	
	public List<Operation> getOperations(){
		return this.operations;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedesc.Service#getServicePropGroups()
	 */
	public List<ServicePropGroup> getServicePropGroups() {
		return servicePropGroups;
	}


	/* (non-Javadoc)
	 * @see org.fireflow.model.servicedesc.Service#setServicePropGroups(java.util.List)
	 */
	public void setServicePropGroups(List<ServicePropGroup> propGroups) {
		this.servicePropGroups = propGroups;
		
	}
	
	public ServicePropGroup getServicePropGroup(String propGroupName){
		if (servicePropGroups==null || servicePropGroups.size()==0){
			return null;
		}
		for (ServicePropGroup propGroup : servicePropGroups){
			if (propGroup.getName()!=null && propGroup.getName().equals(propGroupName)){
				return propGroup;
			}
		}
		return null;
	}
	/**
	 * @return the executorName
	 */
	public String getExecutorName() {
		return executorName;
	}
	/**
	 * @param executorName the executorName to set
	 */
	public void setExecutorName(String executorName) {
		this.executorName = executorName;
	}
//	/**
//	 * @return the fileName
//	 */
//	public String getFileName() {
//		return fileName;
//	}
//	/**
//	 * @param fileName the fileName to set
//	 */
//	public void setFileName(String fileName) {
//		this.fileName = fileName;
//	}
	
	
}