package org.fireflow.pdl.fpdl20.process;

import org.fireflow.model.data.Expression;

public interface Transition extends Arc{
	
	/**
	 * 转移条件
	 * @return
	 */
    public Expression getCondition() ;

    /**
     * 设置转移条件
     * @param condition
     */
    public void setCondition(Expression condition) ;
    
//    /**
//     * 返回一个规则，如果conditions的值为空，则检查是否存在一个规则，如果规则不为空，则应用该规则。
//     * @return
//     */
//    public RuleDef getRuleDef();
//    
//    public void setRuleDef(RuleDef rule);
    
    /**
     * 是否是循环。
     * @return
     */
    public boolean isLoop();
    
    /**
     * 是否是缺省路由
     * @return
     */
    public boolean isDefault();
}
