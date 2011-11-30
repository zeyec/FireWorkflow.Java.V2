/**
 * Copyright 2004-2008 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
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
package org.fireflow.pdl.fpdl20.process.impl;

import org.fireflow.model.data.Expression;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.Transition;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;



/**
 * 流程转移
 * @author 非也,nychen2000@163.com
 */
@SuppressWarnings("serial")
public class TransitionImpl extends ArcImpl implements Transition{
	private boolean isLoop = false;
	private boolean isDefault = false;
	private Expression condition = null;
	
    public TransitionImpl() {
    }

    public TransitionImpl(WorkflowProcess workflowProcess, String name) {
        super(workflowProcess, name);
    }

    public TransitionImpl(WorkflowProcess workflowProcess, String name, Node fromNode, Node toNode) {
        super(workflowProcess, name);
        this.fromNode = fromNode;
        this.toNode = toNode;
    }


//	@Override
//	public void setRuleDef(RuleDef rule) {
//		// TODO Auto-generated method stub
//		
//	}
//	@Override
//	public RuleDef getRuleDef() {
//		// TODO Auto-generated method stub
//		return null;
//	}


	public boolean isLoop() {
		return isLoop;
	}
	
	public void setIsLoop(boolean isLoop){
		this.isLoop = isLoop;
	}


	public void setCondition(Expression condition) {
		this.condition = condition;
		
	}

	public Expression getCondition() {
		return this.condition;
	}
	
	public boolean isDefault(){
		return this.isDefault;
	}
	
	public void setDefault(boolean isDefault){
		this.isDefault = isDefault;
	}
}