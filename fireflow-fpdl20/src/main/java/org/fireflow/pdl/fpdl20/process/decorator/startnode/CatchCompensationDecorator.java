package org.fireflow.pdl.fpdl20.process.decorator.startnode;

import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.decorator.Decorator;

public interface CatchCompensationDecorator  extends Decorator{
	/**
	 * 被catch的Activity
	 * @return
	 */
	public Activity getAttachedToActivity();
	
	public void setAttachedToActivity(Activity act);

	public void setCompensationCode(String compensationCode);
	public String getCompensationCode();
}
