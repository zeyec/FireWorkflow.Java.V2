package org.fireflow.model.servicedef.impl;

import org.fireflow.model.servicedef.IOSpecification;
import org.fireflow.model.servicedef.Operation;


public class OperationImpl implements Operation{
	String operationName = null;
	IOSpecification iospecification = null;
	
	public String getOperationName(){
		return operationName;
	}
	
	public void setOperationName(String nm){
		this.operationName = nm;
	}
	
    /**
     * 获得Operation的输入输出定义
     * @return
     */
    public IOSpecification getIOSpecification(){
    	return iospecification;
    }
    
    /**
     * 设置Operation的输入输出定义
     * @param iospc
     */
    public void setIOSpecification(IOSpecification iospc){
    	this.iospecification = iospc;
    }
    
    
}
