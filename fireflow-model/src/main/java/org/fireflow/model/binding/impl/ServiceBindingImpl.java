package org.fireflow.model.binding.impl;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.binding.InputAssignment;
import org.fireflow.model.binding.OutputAssignment;
import org.fireflow.model.binding.PropOverride;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.servicedef.Operation;
import org.fireflow.model.servicedef.Service;

public class ServiceBindingImpl implements ServiceBinding{
	protected String serviceId = null;
	protected Service service = null;
	protected String operationName = null;
	protected Operation operation = null;
	protected List<InputAssignment> inputAssignments = new ArrayList<InputAssignment>();
	protected List<OutputAssignment> outputAssignments = new ArrayList<OutputAssignment>();
	protected List<PropOverride> propOverrides = new ArrayList<PropOverride>();


	/* (non-Javadoc)
	 * @see org.fireflow.model.process.binding.ServiceRef#getService()
	 */
	public Service getService() {
		return service;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.process.binding.ServiceRef#setService(org.fireflow.model.service.impl.ServiceImpl)
	 */
	public void setService(Service svc) {
		service = svc;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.process.binding.ServiceBinding#getInputAssignments()
	 */
	public List<InputAssignment> getInputAssignments() {
		return inputAssignments;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.process.binding.ServiceBinding#getOutputAssignments()
	 */
	public List<OutputAssignment> getOutputAssignments() {
		return outputAssignments;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.process.binding.ServiceBinding#getPropOverrides()
	 */
	public List<PropOverride> getPropOverrides() {
		return propOverrides;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.process.binding.ServiceBinding#getServiceId()
	 */
	public String getServiceId() {
		return serviceId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.process.binding.ServiceBinding#setInputAssignments(java.util.List)
	 */
	public void setInputAssignments(List<InputAssignment> assignments) {
		inputAssignments = assignments;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.process.binding.ServiceBinding#setOutputAssignments(java.util.List)
	 */
	public void setOutputAssignments(List<OutputAssignment> assignments) {
		outputAssignments = assignments;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.process.binding.ServiceBinding#setPropOverrides(java.util.List)
	 */
	public void setPropOverrides(List<PropOverride> propOverrides) {
		this.propOverrides = propOverrides;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.process.binding.ServiceBinding#setServiceId(java.lang.String)
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;		
	}
	
	public String getOperationName(){
		return this.operationName;
	}
	public void setOperationName(String opName){
		this.operationName = opName;
	}
	
	public Operation getOperation(){
		return this.operation;
	}
	public void setOperation(Operation op){
		this.operation = op;
	}
}
