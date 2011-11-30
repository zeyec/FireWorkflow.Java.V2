package org.fireflow.pdl.bpel.basic;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.VariablePersister;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.ExecuteResult;

public class XYZActivity extends BasicActivity {
	public XYZActivity(String name){
		super(name);
	}
	
	
	@Override
	public ExecuteResult execute(WorkflowSession session, Token token,
			Object workflowElement) {
		
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		ProcessInstance processInstance = session.getCurrentProcessInstance();
		PersistenceService persistenceService = ctx.getEngineModule(PersistenceService.class, token.getProcessType());
		VariablePersister variablePersister = persistenceService.getVariablePersister();
		Object _x = variablePersister.findVariableValue(processInstance.getScopeId(), "x");
		
		if (_x != null) {
			Integer x = (Integer) _x;
			
			int level = getLevel();
			for (int i = 0; i < (level + 1); i++) {
				System.out.print("    ");// 打印空格
			}
			System.out.println(this.getName() + " executed!(x=" + x + ")");

			x = x + 1;

			variablePersister.setVariable(processInstance, "x", x);
		}else{
			System.out.println(this.getName() + " executed!(x is null)");
		}
		ExecuteResult result = new ExecuteResult();
		result.setStatus(BusinessStatus.COMPLETED);
		return result;
	}
		
}
