package org.fireflow.model.data.impl;

import org.fireflow.model.AbstractModelElement;
import org.fireflow.model.data.Input;

public class InputImpl extends AbsDataElement implements Input {
    
    /**
     * 初始值
     */
    private String defaultValue;
    
    /**
     * 数据格式
     */
    private String dataPattern;

	public String getDefaultValueAsString() {
		return defaultValue;
	}

	public void setDefaultValueAsString(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDataPattern() {
		return dataPattern;
	}

	public void setDataPattern(String dataPattern) {
		this.dataPattern = dataPattern;
	}
}
