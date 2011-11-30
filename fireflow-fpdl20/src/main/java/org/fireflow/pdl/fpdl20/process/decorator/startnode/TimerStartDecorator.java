package org.fireflow.pdl.fpdl20.process.decorator.startnode;

import org.fireflow.engine.service.TimerOperationName;
import org.fireflow.model.data.Expression;
import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.decorator.Decorator;

public interface TimerStartDecorator extends Decorator {

	public Activity getAttachedToActivity();
	
	public void setAttachedToActivity(Activity act);
	
	/**
	 * 该字段表示事件触发时，是否将所依附的Activity取消。默认为不取消(false)
	 * @return
	 */
	public boolean getCancelAttachedToActivity();
	
	public TimerOperationName getTimerOperationName();
	public Expression getStartTimeExpression();
	public Expression getEndTimeExpression();
	public Expression getRepeatCountExpression();
	public Expression getRepeatIntervalExpression();
	public Expression getCronExpression();
}
