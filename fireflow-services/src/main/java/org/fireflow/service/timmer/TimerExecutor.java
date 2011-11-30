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
package org.fireflow.service.timmer;

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
import org.fireflow.engine.exception.ServiceExecutionException;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.misc.ScriptContextVariableNames;
import org.fireflow.engine.misc.Utils;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ScheduleJobPersister;
import org.fireflow.engine.modules.schedule.Scheduler;
import org.fireflow.engine.service.AbsServiceExecutor;
import org.fireflow.engine.service.ServiceExecutor;
import org.fireflow.engine.service.TimerArgs;
import org.fireflow.engine.service.TimerOperationName;
import org.fireflow.model.binding.InputAssignment;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Expression;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class TimerExecutor extends AbsServiceExecutor implements
		ServiceExecutor {
	private static Log log = LogFactory.getLog(TimerExecutor.class);
	
	public static final String SERVICE_TYPE = "TIMER";

	

	/* 
	 * 在流程中的定时器服务节点只允许被触发一次。
	 * @see org.fireflow.engine.service.ServiceExecutor#execute(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance, org.fireflow.model.binding.ServiceBinding, org.fireflow.model.binding.ResourceBinding)
	 */
	public boolean executeService(WorkflowSession session,
			ActivityInstance activityInstance, ServiceBinding serviceBinding,
			ResourceBinding resourceBinding) throws ServiceExecutionException {
		WorkflowSessionLocalImpl sessionLocal = (WorkflowSessionLocalImpl)session;
		RuntimeContext runtimeContext = sessionLocal.getRuntimeContext();
		CalendarService calendarService = runtimeContext.getEngineModule(CalendarService.class, activityInstance.getProcessType());
		
		String operationName = serviceBinding.getOperationName();
		String triggerType = null;
		String triggerExpression = null;
		
		if (TimerOperationName.TRIGGERED_ONLY_ONCE.name().equalsIgnoreCase(operationName.trim())){
			triggerType = ScheduleJob.STARTTIME_REPEATCOUNT_INTERVAL;
			triggerExpression = triggeredOnlyOnce(session,activityInstance,serviceBinding);
		}else if (TimerOperationName.TRIGGERED_BY_REPEAT_COUNT.name().equalsIgnoreCase(operationName.trim())){
			triggerType = ScheduleJob.STARTTIME_REPEATCOUNT_INTERVAL;
			triggerExpression = triggeredByRepeatCount(session,activityInstance,serviceBinding);
		}
		else if (TimerOperationName.TRIGGERED_BY_STARTTIME_ENDTIME.name().equalsIgnoreCase(operationName.trim())){
			triggerType = ScheduleJob.STARTTIME_ENDTIME_INTERVAL;
			triggerExpression = triggerdByStarttimeEndtime(session,activityInstance,serviceBinding);
		}
		else if (TimerOperationName.TRIGGERED_BY_CRON.name().equalsIgnoreCase(operationName.trim())){
			triggerType = ScheduleJob.CRON;
			triggerExpression = triggeredByCron(session,activityInstance,serviceBinding);
		}
		
		else{
			log.error("Unsupported timer operation '"+operationName+"'.");
			throw new ServiceExecutionException("Unsupported timer operation '"+operationName+"'.");
		}

		
		ScheduleJobImpl scheduleJob = new ScheduleJobImpl();
		scheduleJob.setName(activityInstance.getName());
		scheduleJob.setDisplayName(activityInstance.getDisplayName());
		scheduleJob.setState(ScheduleJobState.RUNNING);
		scheduleJob.setActivityInstance(activityInstance);
		scheduleJob.setCreateNewProcessInstance(false);
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
		
		return false;
	}
	
	private String triggeredOnlyOnce(WorkflowSession session,ActivityInstance activityInstance,ServiceBinding serviceBinding) throws ServiceExecutionException{
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		
		List<InputAssignment> inputAssignments = serviceBinding.getInputAssignments();
		Expression theStartTimeExpression = null;
		Expression theRepeatCountExpression = null;
		Expression theRepeatIntervalExpression = null;
		
		for (InputAssignment assignment:inputAssignments){
			if ((ScriptContextVariableNames.INPUTS+"."+TimerArgs.START_TIME.name()).equalsIgnoreCase(assignment.getTo())){
				theStartTimeExpression = assignment.getFrom();
				break;
			}			
		}

		if (theStartTimeExpression==null || theStartTimeExpression.getBody()==null ||
				theStartTimeExpression.getBody().trim().equals("")){
			log.error("The start time expression is null");
			throw new ServiceExecutionException("The start time expression is null");
		}
		ProcessInstance processInstance = session.getCurrentProcessInstance();

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
			throw new ServiceExecutionException("Error when evaluate the timer script.",e);

		}
	}
	
	private String triggeredByRepeatCount(WorkflowSession session,ActivityInstance activityInstance,ServiceBinding serviceBinding) throws ServiceExecutionException{
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		
		List<InputAssignment> inputAssignments = serviceBinding.getInputAssignments();
		Expression theStartTimeExpression = null;
		Expression theRepeatCountExpression = null;
		Expression theRepeatIntervalExpression = null;
		
		for (InputAssignment assignment:inputAssignments){
			if ((ScriptContextVariableNames.INPUTS+"."+TimerArgs.START_TIME.name()).equalsIgnoreCase(assignment.getTo())){
				theStartTimeExpression = assignment.getFrom();
			}
			if ((ScriptContextVariableNames.INPUTS+"."+TimerArgs.REPEAT_COUNT.name()).equals(assignment.getTo())){
				theRepeatCountExpression = assignment.getFrom();
			}
			if ((ScriptContextVariableNames.INPUTS+"."+TimerArgs.REPEAT_INTERVAL.name()).equals(assignment.getTo())){
				theRepeatIntervalExpression = assignment.getFrom();
			}				
		}

		if (theStartTimeExpression==null || theStartTimeExpression.getBody()==null ||
				theStartTimeExpression.getBody().trim().equals("")){
			log.error("The start time expression is null");
			throw new ServiceExecutionException("The start time expression is null");
		}
		ProcessInstance processInstance = session.getCurrentProcessInstance();
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
			throw new ServiceExecutionException("Error when evaluate the timer script.",e);

		}
	}
	
	public String triggerdByStarttimeEndtime(WorkflowSession session,ActivityInstance activityInstance,ServiceBinding serviceBinding) throws ServiceExecutionException{
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		List<InputAssignment> inputAssignments = serviceBinding.getInputAssignments();
		Expression theStartTimeExpression = null;
		Expression theEndTimeExpression = null;
		Expression theRepeatIntervalExpression = null;
		
		for (InputAssignment assignment:inputAssignments){
			if ((ScriptContextVariableNames.INPUTS+"."+TimerArgs.START_TIME.name()).equalsIgnoreCase(assignment.getTo())){
				theStartTimeExpression = assignment.getFrom();
			}
			if ((ScriptContextVariableNames.INPUTS+"."+TimerArgs.END_TIME.name()).equals(assignment.getTo())){
				theEndTimeExpression = assignment.getFrom();
			}
			if ((ScriptContextVariableNames.INPUTS+"."+TimerArgs.REPEAT_INTERVAL.name()).equals(assignment.getTo())){
				theRepeatIntervalExpression = assignment.getFrom();
			}				
		}

		if (theStartTimeExpression==null || theStartTimeExpression.getBody()==null ||
				theStartTimeExpression.getBody().trim().equals("")){
			log.error("The start time expression is null");
			throw new ServiceExecutionException("The start time expression is null");
		}
		ProcessInstance processInstance = session.getCurrentProcessInstance();
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
			throw new ServiceExecutionException("Error when evaluate the timer script.",e);

		}		
	}
	
	private String triggeredByCron(WorkflowSession session,ActivityInstance activityInstance,ServiceBinding serviceBinding) throws ServiceExecutionException{
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		
		List<InputAssignment> inputAssignments = serviceBinding.getInputAssignments();
		Expression theCronExpression = null;
		for (InputAssignment assignment:inputAssignments){
			if (TimerArgs.CRON_EXPRESSION.name().equalsIgnoreCase(assignment.getTo())){
				theCronExpression = assignment.getFrom();
				break;
			}
		}
		
		if (theCronExpression==null){
			log.error("The cron expression is null!");
			throw new ServiceExecutionException("The cron expression is null");
		}
		ProcessInstance processInstance = session.getCurrentProcessInstance();
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
			throw new ServiceExecutionException("Error when evaluate the cron expression.",e);
		}catch(ClassCastException e){
			log.error("The result of the cron expression is not a java String object.",e);
			throw new ServiceExecutionException("The result of the cron expression is not a java String object.",e);
		}
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.ServiceExecutor#getServiceType()
	 */
	public String getServiceType() {
		return SERVICE_TYPE;
	}

	public int determineActivityCloseStrategy(WorkflowSession session,ActivityInstance activityInstance){
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceService = ctx.getEngineModule(PersistenceService.class, activityInstance.getProcessType());
		ScheduleJobPersister scheduleJobPersister = persistenceService.getScheduleJobPersister();
		List<ScheduleJob> jobs = scheduleJobPersister.findScheduleJob4ActivityInstance(activityInstance.getId());
		
		Scheduler scheduler = ctx.getEngineModule(Scheduler.class, activityInstance.getProcessType());
		
		CalendarService calendarService = ctx.getEngineModule(CalendarService.class, activityInstance.getProcessType());
		
		if (jobs==null || jobs.size()==0){
			return ServiceExecutor.CLOSE_ACTIVITY;
		}
		
		ScheduleJob job = jobs.get(0);//只允许有一个job
		if (job.getState().getValue()>ScheduleJobState.DELIMITER.getValue()){
			return ServiceExecutor.CLOSE_ACTIVITY;
		}
		
		if (scheduler.ifJobCanBeFiredAgain(job, ctx)) {
			return ServiceExecutor.START_NEXT_AND_WAITING_FOR_CLOSE;
		} else {
			((ScheduleJobImpl) job).setState(ScheduleJobState.COMPLETED);
			((ScheduleJobImpl) job).setEndTime(calendarService.getSysDate());
			scheduleJobPersister.saveOrUpdate(job);
			scheduler.unSchedule(job, ctx);
		}
		
		return ServiceExecutor.CLOSE_ACTIVITY;
	}

}
