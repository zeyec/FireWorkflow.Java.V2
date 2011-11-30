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
package org.fireflow.service.java;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.beanutils.MethodUtils;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.ServiceExecutionException;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.misc.ScriptContextVariableNames;
import org.fireflow.engine.misc.Utils;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.service.AbsServiceExecutor;
import org.fireflow.engine.service.ServiceExecutor;
import org.fireflow.model.binding.OutputAssignment;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Input;
import org.fireflow.model.data.Output;
import org.fireflow.model.servicedef.IOSpecification;
import org.fireflow.model.servicedef.Operation;
import org.fireflow.model.servicedef.Service;
import org.fireflow.model.servicedef.ServiceProp;
import org.fireflow.model.servicedef.ServicePropGroup;
/**
 * java bean服务，负责调用java bean；
 * 
 * @author 非也
 * @version 2.0
 */
public class JavaExecutor extends AbsServiceExecutor implements ServiceExecutor {
	public static final String SERVICE_TYPE = "JAVA";
	
	private static  Map<String,Class> basicDataTypeClassMap = new HashMap<String,Class>();
	{
		basicDataTypeClassMap.put("int", int.class);
		basicDataTypeClassMap.put("float", float.class);
		basicDataTypeClassMap.put("boolean", boolean.class);
		basicDataTypeClassMap.put("long", long.class);
		basicDataTypeClassMap.put("double", double.class);
		basicDataTypeClassMap.put("short", short.class);
		basicDataTypeClassMap.put("byte",byte.class);
		basicDataTypeClassMap.put("char", char.class);
	}

	public String getServiceType(){
		return SERVICE_TYPE;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.service.ServiceExecutor#execute(org.fireflow.engine
	 * .WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance,
	 * java.lang.Object)
	 */
	public boolean executeService(WorkflowSession session,
			ActivityInstance activityInstance, ServiceBinding serviceBinding,
			ResourceBinding resourceBinding)
			throws ServiceExecutionException {
		WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;
		BeanFactory beanFactory = sessionLocalImpl.getRuntimeContext().getEngineModule(BeanFactory.class, activityInstance.getProcessType());
		
		Service service = serviceBinding.getService();		
		
		Operation operation = serviceBinding.getOperation();
		IOSpecification ioSpec = operation.getIOSpecification();
		List<Input> inputs = ioSpec.getInputs();

		try {
			ServicePropGroup propGroup = service
					.getServicePropGroup(ServicePropGroup.COMMON_PROPERTIES_GROUP);
			
			
			ServiceProp serviceProp = propGroup.getServiceProp("JavaBeanName");
			if (serviceProp==null){
				serviceProp = propGroup.getServiceProp("JavaClassName");
			}
			
			if (serviceProp==null || serviceProp.getValue()==null || serviceProp.getValue().trim().equals("")){
				throw new EngineException(activityInstance,"Service config error,need 'JavaBeanName' or 'JavaClassName'.");
			}
				
			String javaClassName = serviceProp.getValue();
			
			
			Object bean = beanFactory.getBean(javaClassName);
			
			Map<String,Object> scriptContext = Utils.fulfillScriptContext(session, session.getCurrentProcessInstance(), activityInstance);
			Map<String,Object> inputParamValues = Utils.resolveInputParameters(session, serviceBinding.getInputAssignments(),scriptContext);
			List<Object> args = new ArrayList<Object>();
			for (Input _input:inputs){
				Object paramValue = inputParamValues.get(_input.getName());
				args.add(paramValue);
			}
			
//			Class[] methodsArgTypes = null;			
//			if (inputs != null && inputs.size() > 0) {
//				methodsArgTypes = new Class[inputs.size()];
//				int i = 0;
//				for (Input input : inputs) {
//					String inputDataType = input.getDataType();
//					if (inputDataType==null || inputDataType.trim().equals("")){
//						inputDataType="java.lang.String";//默认为String
//					}
//					Class argclz = basicDataTypeClassMap.get(inputDataType);
//					if (argclz==null){
//						argclz = Class.forName(inputDataType);
//					}					
//						
//					methodsArgTypes[i] = argclz;
//					i++;
//				}
//			}
//			Class clz = Class.forName(javaClassName);
//			Method m = clz.getMethod(operation.getName(), methodsArgTypes);
//			Object result = m.invoke(bean, args.toArray());
			
			Object result = MethodUtils.invokeMethod(bean, operation.getOperationName(), args.toArray());


			List<OutputAssignment> outputAssignments = serviceBinding.getOutputAssignments();

			if (outputAssignments!=null && outputAssignments.size()>0){
				Map<String,Object> outputsResults = new HashMap<String,Object>();
				List<Output> outputs = ioSpec.getOutputs();
				if (outputs!=null && outputs.size()>0){
					Output output = outputs.get(0);
					outputsResults.put(output.getName(), result);
					scriptContext.put(ScriptContextVariableNames.OUTPUTS, outputsResults);
				}
				
				OutputAssignment assignment = outputAssignments.get(0);				
				Utils.assignOutputToVariable(session, session.getCurrentProcessInstance(), assignment, scriptContext);
			}

		} 
//		catch (ClassNotFoundException e) {
//			throw new ServiceExecutionException(e);
//		} 
		catch(ScriptException e){
			throw new ServiceExecutionException(e);
		}
		catch (SecurityException e) {
			throw new ServiceExecutionException(e);
		} catch (NoSuchMethodException e) {
			throw new ServiceExecutionException(e);
		} catch (IllegalArgumentException e) {
			throw new ServiceExecutionException(e);
		} catch (IllegalAccessException e) {
			throw new ServiceExecutionException(e);
		} catch (InvocationTargetException e) {
			throw new ServiceExecutionException(e);
		}catch(Exception e){
			throw new ServiceExecutionException(e);
		}

		return true;
	}

}
