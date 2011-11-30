package org.fireflow.pdl.bpel;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.WorkflowStatement;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.impl.InternalSessionAttributeKeys;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.ProcessInstanceManager;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.CancellationHandler;
import org.fireflow.pvm.pdllogic.CompensationHandler;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.ExecuteResult;
import org.fireflow.pvm.pdllogic.FaultHandler;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;

public class BpelProcess implements WorkflowBehavior{
	private BpelActivity startActivity = null;
	

	private String name = null;
	
	public BpelProcess(String name){
		this.name = name;
	}
	
	public String getId() {
		return name;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BpelActivity getStartActivity() {
		return startActivity;
	}
	public BpelProcess setStartActivity(BpelActivity activity) {
		this.startActivity = activity;
		return this;
	}

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
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#canBeFired(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public Boolean prepare(WorkflowSession session, Token token,
			Object workflowElement) {
		WorkflowSessionLocalImpl sessionLocal = (WorkflowSessionLocalImpl)session;
		RuntimeContext context = sessionLocal.getRuntimeContext();
		ProcessInstanceManager processInstanceManager = context.getEngineModule(ProcessInstanceManager.class,BpelConstants.PROCESS_TYPE);
//		ProcessRepositoryService processRepositoryService = context.getEngineModule(ProcessRepositoryService.class, BpelConstants.PROCESS_TYPE);
		PersistenceService persistenceStrategy = context.getEngineModule(PersistenceService.class, BpelConstants.PROCESS_TYPE);
		ProcessPersister processRepositoryPersister = persistenceStrategy.getProcessPersister();
//		VariableService variableService = context.getEngineModule(VariableService.class, BpelConstants.PROCESS_TYPE);
		ProcessInstancePersister procInstPersistSvc = persistenceStrategy.getProcessInstancePersister();

		
		ProcessKey pk = ProcessKey.valueOf(token);
		ProcessDescriptor processDescriptor = processRepositoryPersister.findProcessDescriptorByProcessKey(pk);

		//创建流程实例，设置初始化参数
		String bizId = (String)session.getAttribute(InternalSessionAttributeKeys.BIZ_ID);
		Map<String, Object> variables = (Map<String, Object>) session
				.getAttribute(InternalSessionAttributeKeys.VARIABLES);
		ActivityInstance parentActivityInstance = session
				.getCurrentActivityInstance();
		ProcessInstance parentProcessInstance = session
				.getCurrentProcessInstance();

		ProcessInstance newProcessInstance = processInstanceManager
				.createProcessInstance(sessionLocal, workflowElement, bizId, processDescriptor,parentActivityInstance);

		procInstPersistSvc.saveOrUpdate(newProcessInstance);
		
		//初始化流程变量
		if (variables != null && variables.size() > 0) {
			Iterator<Entry<String, Object>> it = variables.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<String, Object> entry = it.next();
				WorkflowStatement stmt = sessionLocal.createWorkflowStatement(BpelConstants.PROCESS_TYPE);
				stmt.setVariableValue(newProcessInstance, entry.getKey(), entry.getValue());
			}
		}
		
		token.setProcessInstanceId(newProcessInstance.getId());
		token.setElementInstanceId(newProcessInstance.getId());
		
		sessionLocal.setCurrentProcessInstance(newProcessInstance);

		return true;//true表示告诉虚拟机，“我”已经准备妥当了。
	}
	

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#continueOn(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ContinueDirection continueOn(WorkflowSession session, Token token,
			Object workflowElement) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		List<Token> childTokenList = kernelManager.getChildren(token);
		if (childTokenList==null || childTokenList.size()==0){
			return ContinueDirection.closeMe();
		}else{
			for (Token tk : childTokenList){
				if (tk.getState().getValue()<TokenState.DELIMITER.getValue()){
					return ContinueDirection.waitingForClose();
				}
			}
		}
		return ContinueDirection.closeMe();
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#execute(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ExecuteResult execute(WorkflowSession session, Token parentToken,
			Object workflowElement) {
		BpelActivity activity = this.getStartActivity();
		
		PObjectKey pobjectKey = new PObjectKey(parentToken.getProcessId(),parentToken.getVersion(),parentToken.getProcessType(),activity.getId());
		
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		kernelManager.fireChildPObject(session, pobjectKey, parentToken);
		
		ExecuteResult result = new ExecuteResult();
		result.setStatus(BusinessStatus.RUNNING);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#onTokenStateChanged(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public void onTokenStateChanged(WorkflowSession session, Token token,
			Object workflowElement) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		
		PersistenceService persistenceStrategy = ctx.getEngineModule(PersistenceService.class, BpelConstants.PROCESS_TYPE);
		ProcessInstancePersister procInstPersistenceService = persistenceStrategy.getProcessInstancePersister();
		
		CalendarService calendarService = ctx.getEngineModule(CalendarService.class,BpelConstants.PROCESS_TYPE);
		ProcessInstance procInst = procInstPersistenceService.find(ProcessInstance.class, token.getElementInstanceId());
		
		ProcessInstanceState state = ProcessInstanceState.valueOf(token.getState().name());
		((ProcessInstanceImpl)procInst).setState(state);
		if (state.getValue()>ProcessInstanceState.DELIMITER.getValue()){
			((ProcessInstanceImpl)procInst).setEndTime(calendarService.getSysDate());
			
		}
		procInstPersistenceService.saveOrUpdate(procInst);
		
	}
	
	public void abort(WorkflowSession session,Token thisToken,Object workflowElement){
		
	}
}
