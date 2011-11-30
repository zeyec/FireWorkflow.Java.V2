//$Id: Dom4JServiceSerializerTest.java 501 2011-03-15 15:35:42Z westerly.lzh $
package org.fireflow.model.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.fireflow.model.data.Input;
import org.fireflow.model.data.Output;
import org.fireflow.model.data.impl.InputImpl;
import org.fireflow.model.data.impl.OutputImpl;
import org.fireflow.model.servicedef.IOSpecification;
import org.fireflow.model.servicedef.Operation;
import org.fireflow.model.servicedef.Service;
import org.fireflow.model.servicedef.ServiceProp;
import org.fireflow.model.servicedef.ServicePropGroup;
import org.fireflow.model.servicedef.impl.IOSpecificationImpl;
import org.fireflow.model.servicedef.impl.OperationImpl;
import org.fireflow.model.servicedef.impl.ServiceImpl;
import org.fireflow.model.servicedef.impl.ServicePropGroupImpl;
import org.fireflow.model.servicedef.impl.ServicePropImpl;
import org.junit.Test;
/**
 * @author westerly
 * @version 2.0
 */
public class Dom4JServiceSerializerTest extends BaseTestEnv{

	@Test
	public void testSerialize() {
		serializer = new Dom4JServiceSerializer();
		OutputStream out;
		try {
			out = new FileOutputStream(new File("y:/serviceTest.xml"));
			serializer.serialize(assemblyService(), out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SerializerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private List<Service> assemblyService(){
		List<Service> services = new ArrayList<Service>();
		Service service = new ServiceImpl();
		service.setSn(UUID.randomUUID().toString());
		service.setName("service_name");
		service.setDisplayName("service_displayName");
		service.setDescription("service_description");
		service.setServiceType("service_serviceType");
		service.setBizCategory("service-category");
		
		Operation operation = new OperationImpl();
		operation.setOperationName("operation_name");
		
		IOSpecification io = new IOSpecificationImpl();
		Input in = new InputImpl();
		in.setName("in_name");
		in.setDisplayName("in-displayName");
		in.setDefaultValueAsString("in_value");
		in.setDataType("in_dataType");
		in.setDataPattern("in_dataPattern");
		io.addInput(in);
		Output out = new OutputImpl();
		out.setName("out_name");
		out.setDisplayName("out_displayName");
		out.setDataType("out_dataType");
		io.addOutput(out);
		
		operation.setIOSpecification(io);
		service.setOperation(operation);
		
		List<ServicePropGroup> listSPG = new ArrayList<ServicePropGroup>();
		ServicePropGroup spg = new ServicePropGroupImpl();
		spg.setName("servicepropgroup_name");
		spg.setDisplayName("servicepropgroup_displayname");
		
		List<ServiceProp> list = new ArrayList<ServiceProp>();
		ServiceProp prop = new ServicePropImpl();
		prop.setName("prop_name");
		prop.setDisplayName("prop_displayName");
		prop.setDescription("prop_description");
		prop.setValue("prop_value");
		list.add(prop);
		spg.setServiceProps(list);
		listSPG.add(spg);
		service.setServicePropGroups(listSPG);
		services.add(service);
		return services;
	}

}
