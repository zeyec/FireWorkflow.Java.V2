package org.fireflow.pdl.fpdl20.process.decorator.endnode;

import org.fireflow.pdl.fpdl20.process.decorator.Decorator;

/**
 * 当流程以该类型的节点结束的时候，表示流程将被中止，Token变成aborted状态；
 * 正在执行的节点变成aborted状态，已经完成的节点不做任何回滚。
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public interface ThrowTerminationDecorator extends Decorator {

}
