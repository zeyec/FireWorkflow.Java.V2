package org.fireflow.model.data;

/**
 * 表达式
 * @author 非也
 *
 */
public interface Expression extends DataElement{
	/**
	 * 表达式的语言
	 * @return
	 */
	public String getLanguage();
	
	/**
	 * 表达式体
	 * @return
	 */
	public String getBody();
	
}
