package org.fireflow.pdl.fpdl20.process.decorator.activity;

import org.fireflow.model.misc.Duration;
import org.fireflow.model.servicedef.ServiceType;
import org.fireflow.pdl.fpdl20.process.decorator.Decorator;

public interface ActivityDecorator extends Decorator {
	/**
	 * 获得目标Service的类型
	 * @return
	 */
	public ServiceType getTargetServiceType();
	
	public void setTargetServiceType(ServiceType serviceType);
	

	//下面两个属性应该放在资源定义或者服务定义里面
//	public String getAssignmentStrategy();
//	public void setAssignmentStrategy(String s);
//	
//	public String getCompletionStrategy();
//	public void setCompletionStrategy(String s);
}
