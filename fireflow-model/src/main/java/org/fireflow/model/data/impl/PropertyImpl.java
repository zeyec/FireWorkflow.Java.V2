package org.fireflow.model.data.impl;

import org.fireflow.model.AbstractModelElement;
import org.fireflow.model.ModelElement;
import org.fireflow.model.data.Property;


public class PropertyImpl extends AbstractModelElement implements Property{

    
    /**
     * 数据类型，数据类型必须是一个合法的java类名，如 java.lang.String，java.lang.Integer等。
     */
    private String dataType;
    
    /**
     * 初始值
     */
    private String initialValue;
    
    /**
     * 数据格式
     */
    private String dataPattern;
    
    public PropertyImpl(ModelElement parentElement,String name){
    	super(parentElement,name);
    }

    public PropertyImpl() {
        this.setDataType("java.lang.String");
        
    }

//    public PropertyImpl(WorkflowProcessImpl workflowProcess, String name, String dataType) {
//        super(workflowProcess, name);
//        setDataType(dataType);
//    }

    /**
     * 返回流程变量的数据类型
     * @return 数据类型
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * 设置数据类型，
     * @param dataType
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }


    public String getInitialValueAsString() {
        return initialValue;
    }


    public void setInitialValueAsString(String initialValue) {
        this.initialValue = initialValue;
    }


    public String getDataPattern() {
        return dataPattern;
    }


    public void setDataPattern(String dataPattern) {
        this.dataPattern = dataPattern;
    }
}
