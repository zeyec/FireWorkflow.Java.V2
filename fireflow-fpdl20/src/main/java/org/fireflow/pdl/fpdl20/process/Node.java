package org.fireflow.pdl.fpdl20.process;

import java.util.List;

import org.fireflow.model.process.WorkflowElement;
import org.fireflow.pdl.fpdl20.process.decorator.Decorator;
/**
 * 流程图中的节点
 * @author 非也
 *
 */
public interface Node extends WorkflowElement{
	
	public List<Transition> getEnteringTransitions() ;


	public List<Transition> getLeavingTransitions() ;


	/**
	 * 获得装饰器，装饰器会影响节点的外观和行为
	 * @return
	 */
	public Decorator getDecorator();
	
	public void setDecorator(Decorator dec);
}
