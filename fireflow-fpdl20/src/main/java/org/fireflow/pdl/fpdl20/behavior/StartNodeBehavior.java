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
package org.fireflow.pdl.fpdl20.behavior;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ScheduleJob;
import org.fireflow.engine.entity.runtime.ScheduleJobState;
import org.fireflow.engine.entity.runtime.impl.ScheduleJobImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.misc.Utils;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.ScheduleJobPersister;
import org.fireflow.engine.modules.schedule.Scheduler;
import org.fireflow.engine.service.TimerOperationName;
import org.fireflow.model.ModelElement;
import org.fireflow.model.data.Expression;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.StartNode;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.decorator.Decorator;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.TimerStartDecorator;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.ExecuteResult;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;

/**
 * @author 非也
 * @version 2.0
 */
public class StartNodeBehavior extends AbsSynchronizerBehavior implements WorkflowBehavior {
	private static Log log = LogFactory.getLog(StartNodeBehavior.class);
	
	protected boolean hasAlivePreviousNode(WorkflowSession session,Token token,Node thisNode){
		return false;
	}

	@Override
	public ExecuteResult execute(WorkflowSession session, Token token,
			Object workflowElement) {
		ActivityInstance activityInstance = session.getCurrentActivityInstance();
		
		StartNode startNode = (StartNode)workflowElement ;
		Decorator dec = startNode.getDecorator();
		//只处理TimerStartDecorator
		if (dec == null || !(dec instanceof TimerStartDecorator)){
			ExecuteResult result = new ExecuteResult();
			result.setStatus(BusinessStatus.COMPLETED);
			return result;
		}
		
		//如果是流程入口，则需要不需在此处理
		WorkflowProcess process = (WorkflowProcess)((ModelElement)startNode).getParent();
		Node entry = process.getEntry();
		if (entry!=null && entry.getId().equals(startNode.getId())){
			ExecuteResult result = new ExecuteResult();
			result.setStatus(BusinessStatus.COMPLETED);
			return result;
		}else{
			//1、检验currentActivityInstance和currentProcessInstance的一致性
			ProcessInstance oldProcInst = session.getCurrentProcessInstance();
			ActivityInstance oldActInst = session.getCurrentActivityInstance();

			RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
			PersistenceService persistenceService = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE);
			ActivityInstancePersister actInstPersistenceService = persistenceService.getActivityInstancePersister();
			ProcessInstancePersister processInstancePersister = persistenceService.getProcessInstancePersister();
			
			if (oldProcInst==null || !oldProcInst.getId().equals(token.getProcessInstanceId())){
				ProcessInstance procInst = processInstancePersister.find(ProcessInstance.class, token.getProcessInstanceId());
				((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);
			}
			if (oldActInst==null || !oldActInst.getId().equals(token.getElementInstanceId())){
				ActivityInstance actInst = actInstPersistenceService.find(ActivityInstance.class, token.getElementInstanceId());
				((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
			}
			
			try{				
				//2、执行业务逻辑
				TimerStartDecorator timerDecorator = (TimerStartDecorator)dec;
				createScheduleJob(session,activityInstance,timerDecorator);
				
				ExecuteResult result = new ExecuteResult();
				result.setStatus(BusinessStatus.RUNNING);
				return result;
			}finally{
				((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(oldProcInst);
				((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(oldActInst);
			}
		}
		

	}
	
	private void createScheduleJob(WorkflowSession session,ActivityInstance activityInstance,TimerStartDecorator timerDecorator){
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		CalendarService calendarService = runtimeContext.getEngineModule(CalendarService.class, activityInstance.getProcessType());
		
		ProcessInstance processInstance = session.getCurrentProcessInstance();
		
		String operationName = timerDecorator.getTimerOperationName().name();
		String triggerType = null;
		String triggerExpression = null;
		
		//TODO 注册ScheduleJob
		if (TimerOperationName.TRIGGERED_ONLY_ONCE.name().equalsIgnoreCase(operationName.trim())){
			triggerType = ScheduleJob.STARTTIME_REPEATCOUNT_INTERVAL;
			triggerExpression = triggeredOnlyOnce(session,activityInstance,timerDecorator);
		}else if (TimerOperationName.TRIGGERED_BY_REPEAT_COUNT.name().equalsIgnoreCase(operationName.trim())){
			triggerType = ScheduleJob.STARTTIME_REPEATCOUNT_INTERVAL;
			triggerExpression = triggeredByRepeatCount(session,activityInstance,timerDecorator);
		}
		else if (TimerOperationName.TRIGGERED_BY_STARTTIME_ENDTIME.name().equalsIgnoreCase(operationName.trim())){
			triggerType = ScheduleJob.STARTTIME_ENDTIME_INTERVAL;
			triggerExpression = triggerdByStarttimeEndtime(session,activityInstance,timerDecorator);
		}
		else if (TimerOperationName.TRIGGERED_BY_CRON.name().equalsIgnoreCase(operationName.trim())){
			triggerType = ScheduleJob.CRON;
			triggerExpression = triggeredByCron(session,activityInstance,timerDecorator);
		}
		
		else{
			log.error("Unsupported timer operation '"+operationName+"'.");
			throw new EngineException(activityInstance,"Unsupported timer operation '"+operationName+"'.");
		}

		
		ScheduleJobImpl scheduleJob = new ScheduleJobImpl();
		scheduleJob.setName(activityInstance.getName());
		scheduleJob.setDisplayName(activityInstance.getDisplayName());
		scheduleJob.setState(ScheduleJobState.RUNNING);
		scheduleJob.setActivityInstance(activityInstance);
		if (timerDecorator.getAttachedToActivity()==null){//如果不是依附在Activity上，则是定时启动的StartNode
			scheduleJob.setCreateNewProcessInstance(true);
		}else{
			scheduleJob.setCreateNewProcessInstance(false);
		}
		
		scheduleJob.setCancelAttachedToActivity(timerDecorator.getCancelAttachedToActivity());
		
		scheduleJob.setProcessId(activityInstance.getProcessId());
		scheduleJob.setProcessType(activityInstance.getProcessType());
		scheduleJob.setVersion(activityInstance.getVersion());
		scheduleJob.setTriggerType(triggerType);
		scheduleJob.setTriggerExpression(triggerExpression);
		scheduleJob.setCreatedTime(calendarService.getSysDate());
		
		//保存

		PersistenceService persistenceService = runtimeContext.getEngineModule(PersistenceService.class, activityInstance.getProcessType());
		ScheduleJobPersister persister = persistenceService.getScheduleJobPersister();
		persister.saveOrUpdate(scheduleJob);
		
		//加入调度器
		Scheduler scheduler = runtimeContext.getEngineModule(Scheduler.class, activityInstance.getProcessType());
		scheduler.schedule(scheduleJob,runtimeContext);
	}
	
	private String triggeredOnlyOnce(WorkflowSession session,ActivityInstance activityInstance,TimerStartDecorator timerDecorator) throws EngineException{
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		ProcessInstance processInstance = session.getCurrentProcessInstance();
		
		Expression theStartTimeExpression = timerDecorator.getStartTimeExpression();
		Expression theRepeatCountExpression = null;
		Expression theRepeatIntervalExpression = null;
		
		if (theStartTimeExpression==null || theStartTimeExpression.getBody()==null ||
				theStartTimeExpression.getBody().trim().equals("")){
			log.error("The start time expression is null");
			throw new EngineException(activityInstance,"The start time expression is null");
		}

		Map<String,Object> varContext = Utils.fulfillScriptContext(session, processInstance, activityInstance);
					
		ScriptEngine scriptEngine = runtimeContext.getScriptEngine(runtimeContext.getDefaultScript());
		
		ScriptContext scriptContext = new SimpleScriptContext();
		Bindings engineScope = scriptContext
				.getBindings(ScriptContext.ENGINE_SCOPE);
		engineScope.putAll(varContext);
		
		Date startTime = null;
		Integer repeatCount = 0;
		Integer repeatInterval = 0;
		
		try {
			startTime = (Date)scriptEngine.eval(theStartTimeExpression.getBody(), scriptContext);
			

			StringBuffer buf = new StringBuffer();
			buf.append(startTime.getTime()).append("|")
				.append(repeatCount.toString()).append("|")
				.append(repeatInterval);
			return  buf.toString();
		} catch (ScriptException e) {
			log.error("Error when evaluate the timer script.",e);
			throw new EngineException(processInstance,activityInstance,"Error when evaluate the timer script."+e.getMessage());

		}
	}
	
	private String triggeredByRepeatCount(WorkflowSession session,ActivityInstance activityInstance,TimerStartDecorator timerDecorator) throws EngineException{
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		
		ProcessInstance processInstance = session.getCurrentProcessInstance();
		
		Expression theStartTimeExpression = timerDecorator.getStartTimeExpression();
		Expression theRepeatCountExpression = timerDecorator.getRepeatCountExpression();
		Expression theRepeatIntervalExpression = timerDecorator.getRepeatIntervalExpression();
		

		if (theStartTimeExpression==null || theStartTimeExpression.getBody()==null ||
				theStartTimeExpression.getBody().trim().equals("")){
			log.error("The start time expression is null");
			throw new EngineException(processInstance,activityInstance,"The start time expression is null");
		}

		Map<String,Object> varContext = Utils.fulfillScriptContext(session, processInstance, activityInstance);
					
		ScriptEngine scriptEngine = runtimeContext.getScriptEngine(runtimeContext.getDefaultScript());
		
		ScriptContext scriptContext = new SimpleScriptContext();
		Bindings engineScope = scriptContext
				.getBindings(ScriptContext.ENGINE_SCOPE);
		engineScope.putAll(varContext);
		
		Date startTime = null;
		Integer repeatCount = 0;
		Integer repeatInterval = 0;
		
		try {
			startTime = (Date)scriptEngine.eval(theStartTimeExpression.getBody(), scriptContext);

			if (theRepeatCountExpression!=null && theRepeatCountExpression.getBody()!=null
					&& !theRepeatCountExpression.getBody().trim().equals("")){
				repeatCount = (Integer)scriptEngine.eval(theRepeatCountExpression.getBody(), scriptContext);
			}
			
			if (theRepeatIntervalExpression!=null && theRepeatIntervalExpression.getBody()!=null 
					&& !theRepeatIntervalExpression.getBody().trim().equals("")){
				repeatInterval = (Integer)scriptEngine.eval(theRepeatIntervalExpression.getBody(), scriptContext);
			}
			

			StringBuffer buf = new StringBuffer();
			buf.append(startTime.getTime()).append("|")
				.append(repeatCount.toString()).append("|")
				.append(repeatInterval);
			return buf.toString();
		} catch (ScriptException e) {
			log.error("Error when evaluate the timer script.",e);
			throw new EngineException(processInstance,activityInstance,"Error when evaluate the timer script."+e.getMessage());

		}
	}
	
	public String triggerdByStarttimeEndtime(WorkflowSession session,ActivityInstance activityInstance,TimerStartDecorator timerDecorator) throws EngineException{
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		ProcessInstance processInstance = session.getCurrentProcessInstance();
		Expression theStartTimeExpression = timerDecorator.getStartTimeExpression();
		Expression theEndTimeExpression = timerDecorator.getEndTimeExpression();
		Expression theRepeatIntervalExpression = timerDecorator.getRepeatCountExpression();
		
		if (theStartTimeExpression==null || theStartTimeExpression.getBody()==null ||
				theStartTimeExpression.getBody().trim().equals("")){
			log.error("The start time expression is null");
			throw new EngineException(processInstance,activityInstance,"The start time expression is null");
		}

		Map<String,Object> varContext = Utils.fulfillScriptContext(session, processInstance, activityInstance);
					
		ScriptEngine scriptEngine = runtimeContext.getScriptEngine(runtimeContext.getDefaultScript());
		
		ScriptContext scriptContext = new SimpleScriptContext();
		Bindings engineScope = scriptContext
				.getBindings(ScriptContext.ENGINE_SCOPE);
		engineScope.putAll(varContext);
		
		Date startTime = null;
		Date endTime = null;
		Integer repeatInterval = 0;
		
		try {
			startTime = (Date)scriptEngine.eval(theStartTimeExpression.getBody(), scriptContext);

			if (theEndTimeExpression!=null && theEndTimeExpression.getBody()!=null
					&& !theEndTimeExpression.getBody().trim().equals("")){
				endTime = (Date)scriptEngine.eval(theEndTimeExpression.getBody(), scriptContext);
			}
			
			if (theRepeatIntervalExpression!=null && theRepeatIntervalExpression.getBody()!=null 
					&& !theRepeatIntervalExpression.getBody().trim().equals("")){
				repeatInterval = (Integer)scriptEngine.eval(theRepeatIntervalExpression.getBody(), scriptContext);
			}
			

			StringBuffer buf = new StringBuffer();
			buf.append(startTime.getTime()).append("|")
				.append(endTime==null?"null":endTime.getTime()).append("|")
				.append(repeatInterval);
			return buf.toString();
		} catch (ScriptException e) {
			log.error("Error when evaluate the timer script.",e);
			throw new EngineException(processInstance,activityInstance,"Error when evaluate the timer script."+e.getMessage());

		}		
	}
	
	private String triggeredByCron(WorkflowSession session,ActivityInstance activityInstance,TimerStartDecorator timerDecorator) throws EngineException{
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		
		ProcessInstance processInstance = session.getCurrentProcessInstance();
		Expression theCronExpression = timerDecorator.getCronExpression();

		
		if (theCronExpression==null){
			log.error("The cron expression is null!");
			throw new EngineException(processInstance,activityInstance,"The cron expression is null");
		}

		Map<String,Object> varContext = Utils.fulfillScriptContext(session, processInstance, activityInstance);
					
		ScriptEngine scriptEngine = runtimeContext.getScriptEngine(runtimeContext.getDefaultScript());
		
		ScriptContext scriptContext = new SimpleScriptContext();
		Bindings engineScope = scriptContext
				.getBindings(ScriptContext.ENGINE_SCOPE);
		engineScope.putAll(varContext);
		
		try {
			Object obj = scriptEngine.eval(theCronExpression.getBody(), scriptContext);

			return (String)obj;
		} catch (ScriptException e) {
			log.error("Error when evaluate the cron expression.",e);
			throw new EngineException(processInstance,activityInstance,"Error when evaluate the cron expression."+e.getMessage());
		}catch(ClassCastException e){
			log.error("The result of the cron expression is not a java String object.",e);
			throw new EngineException(processInstance,activityInstance,"The result of the cron expression is not a java String object."+e.getMessage());
		}
	}
	
	
	public ContinueDirection continueOn(WorkflowSession session,Token token, Object workflowElement){
		//启动后续节点，同时
		if (token.getState().getValue()!=TokenState.RUNNING.getValue()){
			return ContinueDirection.closeMe();
		}

		List<PObjectKey> nextPObjectKeys = determineNextPObjectKeys(session,token,workflowElement);
		ContinueDirection direction = null;
		
		//判断start节点的装饰器类型
		StartNode startNode = (StartNode)workflowElement ;
		Decorator dec = startNode.getDecorator();
		//只处理TimerStartDecorator
		if (dec == null || !(dec instanceof TimerStartDecorator)){
			direction = ContinueDirection.closeMe();
			direction.setNextProcessObjectKeys(nextPObjectKeys);
			return direction;
		}else{
			//只有依附在其他Activity上的timer才用ContinueDirection.startNextAndWaitingForClose();
			WorkflowProcess process = (WorkflowProcess)((ModelElement)startNode).getParent();
			Node entry = process.getEntry();
			if (entry!=null && entry.getId().equals(startNode.getId())){
				direction = ContinueDirection.closeMe();
				direction.setNextProcessObjectKeys(nextPObjectKeys);
				return direction;
			}else{
				direction = ContinueDirection.startNextAndWaitingForClose();
				direction.setNextProcessObjectKeys(nextPObjectKeys);
			}
		}

		return direction;

	}
}
