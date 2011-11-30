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

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.pdl.fpdl20.process.EndNode;
import org.fireflow.pdl.fpdl20.process.decorator.Decorator;
import org.fireflow.pdl.fpdl20.process.decorator.endnode.NormalEndDecorator;
import org.fireflow.pdl.fpdl20.process.decorator.endnode.ThrowTerminationDecorator;
import org.fireflow.pdl.fpdl20.process.decorator.endnode.ThrowCompensationDecorator;
import org.fireflow.pdl.fpdl20.process.decorator.endnode.ThrowFaultDecorator;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.pdllogic.ContinueDirection;



/**
 * @author 非也
 * @version 2.0
 */
public class EndNodeBehavior extends AbsSynchronizerBehavior {

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#continueOn(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	@Override
	public ContinueDirection continueOn(WorkflowSession session, Token token,
			Object workflowElement) {
		
		EndNode endNode = (EndNode)workflowElement;
		Decorator decorator = endNode.getDecorator();
		
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		
		if (decorator!=null && decorator instanceof ThrowTerminationDecorator){
			Token token4ProcessInstance = kernelManager.getParentToken(token);
			kernelManager.fireTerminationEvent(session, token4ProcessInstance,token);
		}else if (decorator!=null && decorator instanceof ThrowFaultDecorator){
			ThrowFaultDecorator throwExceptionDecorator = (ThrowFaultDecorator)decorator;
			Token token4ProcessInstance = kernelManager.getParentToken(token);
			kernelManager.fireFaultEvent(session, token4ProcessInstance,token, throwExceptionDecorator.getErrorCode());
			
		}else if (decorator!=null && decorator instanceof ThrowCompensationDecorator){
			ThrowCompensationDecorator throwCompensationDecorator = (ThrowCompensationDecorator)decorator;
			Token token4ProcessInstance = kernelManager.getParentToken(token);
			kernelManager.fireCompensationEvent(session, token4ProcessInstance,token,throwCompensationDecorator.getCompensationCodes());
		}

		return ContinueDirection.closeMe();
	}

}
