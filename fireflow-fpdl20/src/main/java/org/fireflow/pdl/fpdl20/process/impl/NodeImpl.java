/**
 * Copyright 2004-2008 非也
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
package org.fireflow.pdl.fpdl20.process.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.model.AbstractModelElement;
import org.fireflow.model.process.lifecycle.InstanceCreatorDef;
import org.fireflow.model.process.lifecycle.InstanceExecutorDef;
import org.fireflow.model.process.lifecycle.InstanceTerminatorDef;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.Transition;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.decorator.Decorator;

/**
 * 流程图的节点。
 * @author 非也,nychen2000@163.com
 *
 */
@SuppressWarnings("serial")
public abstract class NodeImpl extends AbstractModelElement implements Node{
    /**
     * 输入转移
     */
    protected List<Transition> enteringTransitions = new ArrayList<Transition>();//输入弧
    
    /**
     * 输出转移
     */
    protected List<Transition> leavingTransitions = new ArrayList<Transition>();//输出弧
    
    protected Decorator decorator = null;
    
    protected Map<String,String> extendAttributes = new HashMap<String,String>();
	
	
    public NodeImpl() {
    }

    public NodeImpl(WorkflowProcess workflowProcess, String name) {
        super(workflowProcess, name);
    }

	public List<Transition> getEnteringTransitions() {
		return enteringTransitions;
	}


	public List<Transition> getLeavingTransitions() {
		return leavingTransitions;
	}

	public Decorator getDecorator() {
		return decorator;
	}

	public void setDecorator(Decorator dec) {
		this.decorator = dec;
	}

	public Map<String, String> getExtendedAttributes() {
		return extendAttributes;
	}
}
