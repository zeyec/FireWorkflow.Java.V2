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

import java.util.ArrayList;
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
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.misc.Utils;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;
import org.fireflow.engine.modules.instancemanager.WorkItemManager;
import org.fireflow.engine.modules.instancemanager.event.EventType;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.model.data.Expression;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.StartNode;
import org.fireflow.pdl.fpdl20.process.Transition;
import org.fireflow.pdl.fpdl20.process.decorator.Decorator;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.TimerStartDecorator;
import org.fireflow.pvm.kernel.BookMark;
import org.fireflow.pvm.kernel.ExecutionEntrance;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.pdllogic.CancellationHandler;
import org.fireflow.pvm.pdllogic.CompensationHandler;
import org.fireflow.pvm.pdllogic.FaultHandler;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;

/**
 * @author 非也 
 * @version 2.0
 */
public abstract class AbsNodeBehavior implements WorkflowBehavior {
	

	public CompensationHandler getCompensationHandler(String compensationCode){
		return null;
	}
	
	public CancellationHandler getCancellationHandler(){
		return null;
	}
	
	public FaultHandler getFaultHandler(String errorCode){
		return null;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#onTokenStateChanged(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public void onTokenStateChanged(WorkflowSession session, Token token,
			Object workflowElement) {
		
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceStrategy = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE);
		ActivityInstancePersister actInstPersistenceService = persistenceStrategy.getActivityInstancePersister();
		TokenPersister tokenPersister = persistenceStrategy.getTokenPersister();
		
		CalendarService calendarService = ctx.getEngineModule(CalendarService.class,FpdlConstants.PROCESS_TYPE);		
		
		ActivityInstance oldActInst = session.getCurrentActivityInstance();
		ActivityInstance activityInstance = oldActInst;
		if (oldActInst==null || !oldActInst.getId().equals(token.getElementInstanceId())){
			activityInstance = actInstPersistenceService.find(ActivityInstance.class, token.getElementInstanceId());
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(activityInstance);
		}
		
		try{
			ActivityInstanceState state = ActivityInstanceState.valueOf(token.getState().name());
			((ActivityInstanceImpl)activityInstance).setState(state);
			if (state.getValue()>ActivityInstanceState.DELIMITER.getValue()){
				((ActivityInstanceImpl)activityInstance).setEndTime(calendarService.getSysDate());
				
				// 停止边上的定时器对应的activityInstance
				List<Token> attachedTokens = tokenPersister.findAttachedTokens(token);
				if (attachedTokens!=null && attachedTokens.size()>0){
					KernelManager kernelManager = ctx.getEngineModule(KernelManager.class, token.getProcessType());
					for (Token attachedToken : attachedTokens){
						BookMark bookMark = new BookMark();
						bookMark.setToken(attachedToken);
						bookMark.setExecutionEntrance(ExecutionEntrance.HANDLE_CANCELLATION);
						bookMark.setExtraArg(BookMark.SOURCE_TOKEN, token);
						
						kernelManager.addBookMark(bookMark);
					}
				}
			}else if (state.equals(ActivityInstanceState.RUNNING)){
				((ActivityInstanceImpl)activityInstance).setStartedTime(calendarService.getSysDate());
			}
			actInstPersistenceService.saveOrUpdate(activityInstance);

			if (state.getValue()>ActivityInstanceState.DELIMITER.getValue()){
				//发布AFTER_ACTIVITY_INSTANCE_END
				ActivityInstanceManager activityInstanceMgr = ctx.getEngineModule(ActivityInstanceManager.class, token.getProcessType());
				activityInstanceMgr.fireActivityInstanceEvent(session, activityInstance, workflowElement, EventType.AFTER_ACTIVITY_INSTANCE_END);
			}
		}finally{
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(oldActInst);
		}
	}
	

	
	protected List<PObjectKey> determineNextPObjectKeys(WorkflowSession session, Token token,
			Object workflowElement){
		String targetActivityId = (String)session.removeAttribute(WorkItemManager.TARGET_ACTIVITY_ID);
		if (targetActivityId!=null && !targetActivityId.trim().equals("")){
			List<PObjectKey> nextPObjectKeys = new ArrayList<PObjectKey>();
			PObjectKey pobjKey = new PObjectKey(token.getProcessId(),
					token.getVersion(), token.getProcessType(),
					targetActivityId);
			nextPObjectKeys.add(pobjKey);
			return nextPObjectKeys;
		}
		
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		
		Map<String,Object> fireflowVariableContext = Utils.fulfillScriptContext(session, 
				session.getCurrentProcessInstance(),
				session.getCurrentActivityInstance());
		ScriptContext scriptContext = new SimpleScriptContext();
        Bindings engineScope = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
        engineScope.putAll(fireflowVariableContext);
		
		Node node = (Node)workflowElement;
		List<PObjectKey> nextPObjectKeys = new ArrayList<PObjectKey>();
		
		List<Transition> leavingTransitions = node.getLeavingTransitions();
		Transition defaultTransition = null;
		if (leavingTransitions!=null){	        
			for (Transition transition : leavingTransitions){
				if (transition.isDefault()){
					defaultTransition = transition;
					continue;
				}
				boolean b = true;
				
				Expression expression = transition.getCondition();
				if (expression!=null && expression.getBody()!=null && !expression.getBody().trim().equals("")){
					ScriptEngine scriptEngine = ctx.getScriptEngine(expression.getLanguage());
					if (scriptEngine!=null){
						Object obj;
						try {
							obj = scriptEngine.eval(expression.getBody(), scriptContext);
							if (obj instanceof Boolean){
								b = ((Boolean)obj).booleanValue();
							}
						} catch (ScriptException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				if (b) {
					PObjectKey pobjKey = new PObjectKey(token.getProcessId(),
							token.getVersion(), token.getProcessType(),
							transition.getId());
					nextPObjectKeys.add(pobjKey);
				}
			}
		}
		
		if (nextPObjectKeys.size()==0){
			if(defaultTransition!=null){
				PObjectKey pobjKey = new PObjectKey(token.getProcessId(),
						token.getVersion(), token.getProcessType(),
						defaultTransition.getId());
				
				nextPObjectKeys.add(pobjKey);
			}
		}
		return nextPObjectKeys;
	}
	

}
