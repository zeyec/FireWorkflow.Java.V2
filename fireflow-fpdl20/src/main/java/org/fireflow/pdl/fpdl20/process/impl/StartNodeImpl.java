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

import java.util.List;

import org.fireflow.pdl.fpdl20.process.StartNode;
import org.fireflow.pdl.fpdl20.process.Transition;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.decorator.startnode.impl.NormalStartDecoratorImpl;



/**
 * 开始节点
 * @author 非也,nychen2000@163.com
 */
@SuppressWarnings("serial")
public class StartNodeImpl extends SynchronizerImpl implements StartNode{

    private StartNodeImpl() {
    	this.setDecorator(new NormalStartDecoratorImpl());
    }

    public StartNodeImpl(WorkflowProcess workflowProcess, String name) {
        super(workflowProcess, name);
    	this.setDecorator(new NormalStartDecoratorImpl());
    }

    /**
     * 返回null值，表示无输入弧
     */
    @Override
    public List<Transition> getEnteringTransitions() {
        return null;
    }
}
