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

import org.fireflow.pdl.fpdl20.process.EndNode;
import org.fireflow.pdl.fpdl20.process.Transition;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.decorator.endnode.impl.NormalEndDecoratorImpl;



/**
 * 结束节点
 * @author 非也,nychen2000@163.com
 */
@SuppressWarnings("serial")
public class EndNodeImpl extends SynchronizerImpl implements EndNode{

    public EndNodeImpl() {
    	this.setDecorator(new NormalEndDecoratorImpl());
    }

    public EndNodeImpl(WorkflowProcess workflowProcess, String name) {
        super(workflowProcess, name);
    	this.setDecorator(new NormalEndDecoratorImpl());
    }

    /**
     * 返回null。表示无输出弧。
     */
    @Override
    public List<Transition> getLeavingTransitions() {
        return null;
    }
}
