package org.fireflow.pdl.fpdl20.process.decorator.startnode;

import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.decorator.Decorator;

public interface CatchFaultDecorator  extends Decorator{
	/**
	 * 被catch的Activity
	 * @return
	 */
	public Activity getAttachedToActivity();
	
	public void setAttachedToActivity(Activity act);
	
	/**
	 * 被监听的异常类的名称
	 * @return
	 */
	public String getErrorCode();
	
	public void setErrorCode(String errorCode);
}
