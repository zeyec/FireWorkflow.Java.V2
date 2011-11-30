package org.fireflow.model.data.impl;

import org.fireflow.model.AbstractModelElement;
import org.fireflow.model.data.Output;

public class OutputImpl extends AbsDataElement implements Output{

    
    /**
     * 初始值
     */
    private String avlueAsString;
    
    /**
     * 数据格式
     */
    private String dataPattern;

	public String getAvlueAsString() {
		return avlueAsString;
	}

	public void setAvlueAsString(String avlueAsString) {
		this.avlueAsString = avlueAsString;
	}

	public String getDataPattern() {
		return dataPattern;
	}

	public void setDataPattern(String dataPattern) {
		this.dataPattern = dataPattern;
	}
    
}
