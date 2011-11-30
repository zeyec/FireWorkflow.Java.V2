package org.fireflow.model.data;

import org.fireflow.model.ModelElement;

/**
 * 数据元素
 * @author 非也
 *
 */
public interface DataElement {

	public String getName();
	public void setName(String name);
	
	public String getDisplayName();
	public void setDisplayName(String displayName);
	
	/**
	 * 数据类型，数据类型必须是一个合法的java类名，如 java.lang.String，java.lang.Integer等
	 * @return
	 */
    public String getDataType();
    
    public void setDataType(String dataType);
}
