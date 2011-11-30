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

import java.util.ArrayList;
import java.util.List;

import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pdl.fpdl20.behavior.ActivityBehavior;
import org.fireflow.pdl.fpdl20.behavior.EndNodeBehavior;
import org.fireflow.pdl.fpdl20.behavior.RouterBehavior;
import org.fireflow.pdl.fpdl20.behavior.StartNodeBehavior;
import org.fireflow.pdl.fpdl20.behavior.TransitionBehavior;
import org.fireflow.pdl.fpdl20.behavior.WorkflowProcessBehavior;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.EndNode;
import org.fireflow.pdl.fpdl20.process.Router;
import org.fireflow.pdl.fpdl20.process.StartNode;
import org.fireflow.pdl.fpdl20.process.Transition;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.decorator.Decorator;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.CatchCompensationDecorator;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.CatchFaultDecorator;
import org.fireflow.pvm.kernel.PObject;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.impl.ArcInstanceImpl;
import org.fireflow.pvm.kernel.impl.NetInstanceImpl;
import org.fireflow.pvm.kernel.impl.NodeInstanceImpl;
import org.fireflow.pvm.translate.Process2PObjectTranslator;

/**
 * @author 非也
 * @version 2.0
 */
public class Process2PObjectTranslatorFpdl20Impl implements
		Process2PObjectTranslator {

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.translate.PDL2ProcessObjectTranslator#translatePDL2ProcessObjects(org.fireflow.engine.entity.repository.ProcessRepository)
	 */
	public List<PObject> translateProcess(ProcessKey processKey ,Object process) {
		WorkflowProcess fpdl20Process = (WorkflowProcess)process;
		WorkflowProcessBehavior workflowProcessBehavior = new WorkflowProcessBehavior();
		
		PObjectKey key = new PObjectKey(processKey.getProcessId(),
				processKey.getVersion(), processKey.getProcessType(), fpdl20Process.getId());
		
		PObject pObject = new NetInstanceImpl(key);
		pObject.setWorkflowBehavior(workflowProcessBehavior);
		pObject.setWorkflowElement(fpdl20Process);
		
		List<PObject> pobjectList = new ArrayList<PObject>();
		pobjectList.add(pObject);
		
		ProcessKey pk = ProcessKey.valueOf(key);
		
		List<StartNode> startNodes = fpdl20Process.getStartNodes();
		if (startNodes!=null && startNodes.size()>0){
			List<PObject> pos = this.translateStartNodes(startNodes, pk);
			pobjectList.addAll(pos);
		}
		
		List<Router> routers = fpdl20Process.getRouters();
		if (routers!=null && routers.size()>0){
			List<PObject> pos = this.translateRouters(routers, pk);
			pobjectList.addAll(pos);
		}
		
		List<EndNode> endNodes = fpdl20Process.getEndNodes();
		if (endNodes!=null && endNodes.size()>0){
			List<PObject> pos = this.translateEndNodes(endNodes, pk);
			pobjectList.addAll(pos);
		}
		
		List<Activity> activities = fpdl20Process.getActivities();
		if (activities!=null && activities.size()>0){
			List<PObject> pos = this.translateActivities(activities, pk);
			pobjectList.addAll(pos);
		}
		
		List<Transition> transitions = fpdl20Process.getTransitions();
		if (transitions!=null && transitions.size()>0){
			List<PObject> pos = this.translateTransitions(transitions, pk);
			pobjectList.addAll(pos);
		}

		assemblePObject(fpdl20Process,pobjectList,pk);
		
		return pobjectList;
	}
	
	private List<PObject> translateStartNodes(List<StartNode> startNodes,ProcessKey pk){
		List<PObject> result = new ArrayList<PObject>();
		
		for (StartNode startNode:startNodes){
			
			PObjectKey key = new PObjectKey(pk.getProcessId(),
					pk.getVersion(), pk
							.getProcessType(), startNode.getId());
			
			PObject pObject = new NodeInstanceImpl(key);
			pObject.setCancellable(true);
			pObject.setCompensable(false);
			pObject.setWorkflowBehavior(new StartNodeBehavior());
			pObject.setWorkflowElement(startNode);
			
			result.add(pObject);
		}
		
		return result;
	}
	
	private List<PObject> translateRouters(List<Router> routers,ProcessKey pk){
		List<PObject> result = new ArrayList<PObject>();
		
		for (Router router:routers){
			
			PObjectKey key = new PObjectKey(pk.getProcessId(),
					pk.getVersion(), pk
							.getProcessType(), router.getId());
			
			PObject pObject = new NodeInstanceImpl(key);
			pObject.setCancellable(true);
			pObject.setCompensable(false);
			pObject.setWorkflowBehavior(new RouterBehavior());
			pObject.setWorkflowElement(router);
			
			result.add(pObject);
		}
		
		return result;
	}

	private List<PObject> translateEndNodes(List<EndNode> endNodes,ProcessKey pk){
		List<PObject> result = new ArrayList<PObject>();
		
		for (EndNode endNode:endNodes){
			
			PObjectKey key = new PObjectKey(pk.getProcessId(),
					pk.getVersion(), pk
							.getProcessType(), endNode.getId());
			
			PObject pObject = new NodeInstanceImpl(key);
			pObject.setCancellable(true);
			pObject.setCompensable(false);
			pObject.setWorkflowBehavior(new EndNodeBehavior());
			pObject.setWorkflowElement(endNode);
			
			result.add(pObject);
		}
		
		return result;
	}
	
	private List<PObject> translateActivities(List<Activity> activities,ProcessKey pk){
		List<PObject> result = new ArrayList<PObject>();
		
		for (Activity activity:activities){
			
			PObjectKey key = new PObjectKey(pk.getProcessId(),
					pk.getVersion(), pk
							.getProcessType(), activity.getId());
			
			PObject pObject = new NodeInstanceImpl(key);
			pObject.setCancellable(true);
			pObject.setCompensable(true);
			pObject.setWorkflowBehavior(new ActivityBehavior());
			pObject.setWorkflowElement(activity);
			
			result.add(pObject);
		}
		
		return result;
	}
	
	private List<PObject> translateTransitions(List<Transition> transitions,ProcessKey pk){
		List<PObject> result = new ArrayList<PObject>();
		
		for (Transition transition:transitions){
			
			PObjectKey key = new PObjectKey(pk.getProcessId(),
					pk.getVersion(), pk
							.getProcessType(), transition.getId());
			
			PObject pObject = new ArcInstanceImpl(key);
			pObject.setCancellable(false);
			pObject.setCompensable(false);
			pObject.setWorkflowBehavior(new TransitionBehavior());
			pObject.setWorkflowElement(transition);
			
			result.add(pObject);
		}
		
		return result;
	}
	
	/**
	 * 组装异常处理器，补偿处理器等
	 * @param fpdl20Process
	 * @param pobjectList
	 */
	private void assemblePObject(WorkflowProcess fpdl20Process ,List<PObject> pobjectList,ProcessKey pk){
		List<Activity> activities = fpdl20Process.getActivities();
		if (activities==null || activities.size()==0){
			return;
		}
		for (Activity activity : activities){

			PObjectKey pkey4Activity = new PObjectKey(pk.getProcessId(),pk.getVersion(),pk.getProcessType(),activity.getId());
			PObject pobject4Activity = this.findPObject(pobjectList, pkey4Activity);
			
			List<StartNode> attachedStartNodes = activity.getAttachedStartNodes();
			if (attachedStartNodes!=null && attachedStartNodes.size()>0){
				for (StartNode startNode : attachedStartNodes){
					Decorator decorator = startNode.getDecorator();
					if (decorator!=null && decorator instanceof CatchCompensationDecorator){
						CatchCompensationDecorator compensationDecorator = (CatchCompensationDecorator)decorator;
						PObjectKey pkey = new PObjectKey(pk.getProcessId(),pk.getVersion(),pk.getProcessType(),startNode.getId());
						PObject po = this.findPObject(pobjectList, pkey);
						if (po!=null){
							String compensationCode = compensationDecorator.getCompensationCode();
							if (compensationCode==null || compensationCode.trim().equals("")){
								compensationCode = FpdlConstants.DEFAULT_COMPENSATION_CODE;
							}
							if (compensationCode.equals(FpdlConstants.DEFAULT_COMPENSATION_CODE)){
								((NodeInstanceImpl)pobject4Activity).setCompensationHandler(compensationCode,  po,true);
							}else{
								((NodeInstanceImpl)pobject4Activity).setCompensationHandler(compensationCode,  po);
							}
							
						}
					}else if (decorator!=null && decorator instanceof CatchFaultDecorator){
						CatchFaultDecorator exceptionDecorator = (CatchFaultDecorator)decorator;
						PObjectKey pkey = new PObjectKey(pk.getProcessId(),pk.getVersion(),pk.getProcessType(),startNode.getId());
						PObject po = this.findPObject(pobjectList, pkey);
						if (po!=null){
							String errorCode = exceptionDecorator.getErrorCode();
							if (errorCode==null || errorCode.trim().equals("")){
								((NodeInstanceImpl)pobject4Activity).setFaultHandler("", po,true);
							}else{
								((NodeInstanceImpl)pobject4Activity).setFaultHandler(errorCode, po);
							}
							
						}
					}
					//TODO 需要catch cancellation decorator吗？
//					else if (decorator!=null && decorator instanceof CatchCancellationDecorator){
//						
//					}
//					else if ( is catchTimerDecorator){
//						
//					}
				}
			}
		}			
		//装配流程级别的handler
		List<StartNode> startNodes = fpdl20Process.getStartNodes();
		PObject pobject4Process = this.findPObject(pobjectList, new PObjectKey(pk.getProcessId(),pk.getVersion(),pk.getProcessType(),pk.getProcessId()));
		if (startNodes!=null && startNodes.size()>0){
			for (StartNode start : startNodes) {
				PObjectKey pkey4Start = new PObjectKey(pk.getProcessId(), pk
						.getVersion(), pk.getProcessType(), start.getId());
				PObject pobject4Start = this.findPObject(pobjectList,
						pkey4Start);

				Decorator decorator = start.getDecorator();
				if (decorator != null
						&& (decorator instanceof CatchFaultDecorator)) {					
					CatchFaultDecorator faultDecorator = (CatchFaultDecorator) decorator;
					
					if (faultDecorator.getAttachedToActivity()==null){
						String errorCode = faultDecorator.getErrorCode();
						if (errorCode == null || errorCode.trim().equals("")) {
							pobject4Process
									.setFaultHandler("", pobject4Start, true);
						} else {
							pobject4Process.setFaultHandler(errorCode,
									pobject4Start);
						}
					}
				} else if (decorator != null
						&& (decorator instanceof CatchCompensationDecorator)) {
					CatchCompensationDecorator compensationDecorator = (CatchCompensationDecorator) decorator;
					if (compensationDecorator.getAttachedToActivity() == null) {
						String compensationCode = compensationDecorator
								.getCompensationCode();
						if (compensationCode == null
								|| compensationCode.trim().equals("")) {
							compensationCode = FpdlConstants.DEFAULT_COMPENSATION_CODE;
						}
						if (compensationCode
								.equals(FpdlConstants.DEFAULT_COMPENSATION_CODE)) {
							pobject4Process.setCompensationHandler(
									compensationCode, pobject4Start, true);
						} else {
							pobject4Process.setCompensationHandler(
									compensationCode, pobject4Start);
						}
					}
				}
			}
		}
	}
	
	private PObject findPObject(List<PObject> pobjectList,PObjectKey pkey){
		if (pobjectList==null || pobjectList.size()==0){
			return null;
		}
		for (PObject pobject : pobjectList){
			if (pobject.getKey().equals(pkey)){
				return pobject;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.translate.Process2PObjectTranslator#translateProcess(org.fireflow.engine.entity.repository.ProcessKey)
	 */
	public List<PObject> translateProcess(ProcessKey processKey) throws InvalidModelException,WorkflowProcessNotFoundException{
		PersistenceService persistenceService = runtimeContext.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE);
		ProcessPersister processPersister = persistenceService.getProcessPersister();
		ProcessRepository repository = processPersister.findProcessRepositoryByProcessKey(processKey);
		if (repository==null){
			throw new WorkflowProcessNotFoundException("The process is not found, id="+processKey.getProcessId()+", version="+processKey.getVersion()+", processType="+processKey.getProcessType());
		}else{
			return this.translateProcess(processKey, repository.getProcess());
		}
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#getRuntimeContext()
	 */
	public RuntimeContext getRuntimeContext() {
		return runtimeContext;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#setRuntimeContext(org.fireflow.engine.context.RuntimeContext)
	 */
	public void setRuntimeContext(RuntimeContext ctx) {
		this.runtimeContext = ctx;
		
	}
	
	private RuntimeContext runtimeContext = null;
}
