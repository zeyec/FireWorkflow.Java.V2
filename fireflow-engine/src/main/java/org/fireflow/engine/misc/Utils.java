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
package org.fireflow.engine.misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.model.binding.InputAssignment;
import org.fireflow.model.binding.OutputAssignment;
import org.fireflow.model.data.Expression;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class Utils {
	/**
	 * 构建脚本引擎的Context,
	 * 
	 * @param session
	 * @param processInstance
	 * @param activityInstance
	 * @return
	 */
	public static Map<String, Object> fulfillScriptContext(
			WorkflowSession session, ProcessInstance processInstance,
			ActivityInstance activityInstance) {

		Map<String, Object> engineScope = new HashMap<String, Object>();

		engineScope.put(ScriptContextVariableNames.CURRENT_PROCESS_INSTANCE, processInstance);
		engineScope.put(ScriptContextVariableNames.CURRENT_ACTIVITY_INSTANCE, activityInstance);
		Map<String, Object> varValues = processInstance
				.getVariableValues(session);
		engineScope.put(ScriptContextVariableNames.PROCESS_VARIABLES,varValues);
		
		if (activityInstance != null) {
			Map<String, Object> varValues2 = activityInstance
					.getVariableValues(session);
			engineScope.put(ScriptContextVariableNames.ACTIVITY_VARIABLES,
					varValues2);
		}
		//fire flow提供的工具类
		engineScope.put(ScriptContextVariableNames.DATE_TIME_UTIL, new DateTimeUtil());
		return engineScope;
	}

	/**
	 * 解析输入参数的值
	 * 
	 * @return
	 */
	public static Map<String, Object> resolveInputParameters(
			WorkflowSession session, List<InputAssignment> inputs,
			Map<String, Object> context) throws ScriptException {
		if (inputs == null || inputs.size() == 0) {
			return null;
		}

		WorkflowSessionLocalImpl localSession = (WorkflowSessionLocalImpl) session;
		RuntimeContext ctx = localSession.getRuntimeContext();
		Map<String, Object> result = new HashMap<String, Object>();

		for (InputAssignment input : inputs) {
			Expression expression = input.getFrom();

			ScriptEngine scriptEngine = ctx.getScriptEngine(expression
					.getLanguage());
			ScriptContext scriptContext = new SimpleScriptContext();
			Bindings engineScope = scriptContext
					.getBindings(ScriptContext.ENGINE_SCOPE);
			engineScope.putAll(context);

			Object obj;

			obj = scriptEngine.eval(expression.getBody(), scriptContext);
			
			String inputName = input.getTo();
			
			int index = inputName.indexOf(ScriptContextVariableNames.INPUTS);
			if (index==0){
				inputName = inputName.substring(index+ScriptContextVariableNames.INPUTS.length()+1);
			}

			
			result.put(inputName, obj);

		}
		return result;
	}

	public static void assignOutputToVariable(WorkflowSession session,
			ProcessInstance processInstance, OutputAssignment assignment,
			Map<String, Object> context) throws ScriptException {
		WorkflowSessionLocalImpl localSession = (WorkflowSessionLocalImpl) session;
		RuntimeContext ctx = localSession.getRuntimeContext();

		Expression expression = assignment.getFrom();
		ScriptEngine scriptEngine = ctx.getScriptEngine(expression
				.getLanguage());
		ScriptContext scriptContext = new SimpleScriptContext();
		Bindings engineScope = scriptContext
				.getBindings(ScriptContext.ENGINE_SCOPE);
		engineScope.putAll(context);

		Object obj = scriptEngine.eval(expression.getBody(), scriptContext);

		try {
			String processVarName = assignment.getTo();
			int index = processVarName.indexOf(ScriptContextVariableNames.PROCESS_VARIABLES);
			if (index==0){
				processVarName = processVarName.substring(index+ScriptContextVariableNames.PROCESS_VARIABLES.length()+1);
			}
			processInstance.setVariableValue(session, processVarName, obj);
		} catch (InvalidOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 将string 转换成相应的java对象，例如Integer,Float等
	 * 
	 * @param valueAsStr
	 * @param dataType
	 * @param pattern
	 * @return
	 */
	public static Object string2Object(String valueAsStr, String dataType,
			String pattern) {
		if (dataType == null || dataType.trim().equals("")
				|| dataType.trim().equals("java.lang.String")) {
			return valueAsStr;
		}
		try {
			if (dataType.trim().equals("int")
					|| dataType.trim().equals("java.lang.Integer")) {
				return new Integer(valueAsStr);
			} else if (dataType.trim().equals("long")
					|| dataType.trim().equals("java.lang.Long")) {
				return new Long(valueAsStr);
			} else if (dataType.trim().equals("float")
					|| dataType.trim().equals("java.lang.Float")) {
				return new Float(valueAsStr);
			} else if (dataType.trim().equals("double")
					|| dataType.trim().equals("java.lang.Double")) {
				return new Double(valueAsStr);
			} else if (dataType.trim().equals("short")
					|| dataType.trim().equals("java.lang.Short")) {
				return new Short(valueAsStr);
			} else if (dataType.trim().equals("byte")
					|| dataType.trim().equals("java.lang.Byte")) {
				return new Byte(valueAsStr);
			} else if (dataType.trim().equals("boolean")
					|| dataType.trim().equals("java.lang.Boolean")) {
				return new Boolean(valueAsStr);
			}
			// else if
			// (dataType.trim().equals("char")||dataType.trim().equals("java.lang.Char")){
			// return new java.lang.Character(valueAsStr);
			// }
			else if (dataType.trim().equals("java.util.Date")) {
				SimpleDateFormat format = new SimpleDateFormat(pattern);
				Date d = format.parse(valueAsStr);
				return d;
			}

			else {
				return valueAsStr;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	//InputStream 转换成byte[]
	private static final int BUFFER_SIZE = 1024;

	public static byte[] getBytes(InputStream is) throws IOException {

	   ByteArrayOutputStream baos = new ByteArrayOutputStream();
	   byte[] b = new byte[BUFFER_SIZE];
	   int len = 0;

	   while ((len = is.read(b, 0, BUFFER_SIZE)) != -1) {
	    baos.write(b, 0, len);
	   }

	   baos.flush();

	   byte[] bytes = baos.toByteArray();

	   return bytes;
	}
}
