package org.fireflow.model.binding;

import java.util.List;

import org.fireflow.model.servicedef.Operation;
import org.fireflow.model.servicedef.Service;


/**
 * 
 * 	<ServiceBinding>
 * 		<!--
 *      <service/>
 *      -->  
 * 		<ref service="Approve_XX" operation="">
 * 		  <IOMapping>
 * 			<InputAssignments>
 * 				<InputAssignment from="an_expression" to="the_input_name">
 * 			<InputAssignments>
 * 			<OutputAssignments>
 * 				<OutputAssignment from="an_expression" to="the_process_property_name">
 * 			<OutputAssignments> 
 * 		  </IOMapping>
 *      </ref>
 * 		<PropOverrides>
 * 			<PropOverride propGroupName="" propName="" value="">
 * 		</PropOverrides>
 * 	</ServiceBinding>
 * 
 * @author 非也
 * @version 2.0
 */
public interface ServiceBinding {	
	public String getServiceId();
	public void setServiceId(String serviceId);
	

	public String getOperationName();
	public void setOperationName(String opName);
	
	public Operation getOperation();
	public void setOperation(Operation op);
	
	public Service getService();

	public void setService(Service svc);

	public List<InputAssignment> getInputAssignments();
	public void setInputAssignments(List<InputAssignment> assignments);
	
	public List<OutputAssignment> getOutputAssignments();
	public void setOutputAssignments(List<OutputAssignment> assignments);
	
	public List<PropOverride> getPropOverrides();
	
	public void setPropOverrides(List<PropOverride> propOverrides);
}
