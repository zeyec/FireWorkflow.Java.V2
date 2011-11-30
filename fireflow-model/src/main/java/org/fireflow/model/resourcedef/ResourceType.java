package org.fireflow.model.resourcedef;


public enum ResourceType {
	PROCESS_INSTANCE_CREATOR("org.fireflow.constants.ProcessInstanceCreator"),//,"流程创建者"
	ACTIVITY_INSTANCE_PERFORMER("org.fireflow.constants.ActivityInstancePerformer"),//,"活动实例执行者"
	VARIABLE_IMPLICATION("org.fireflow.constants.VariableImplication"),//,"流程变量所指用户"
	USER("org.fireflow.constants.User"),//,"用户"
	ROLE("org.fireflow.constants.Role"),//,"角色"
	GROUP("org.fireflow.constants.Group"),//,"用户组"
	DEPARTMENT("org.fireflow.constants.Department"),//,"部门"
	CUSTOM("org.fireflow.constants.Custom"),//,"用户自定义"
	SYSTEM("org.fireflow.constants.System");//,"系统"
	
	private String value = null;
	private ResourceType(String v){
		this.value = v;
	}
	
	public String getValue(){
		return value;
	}
	public static ResourceType fromValue(String v){
		ResourceType[] values =  ResourceType.values();
		for (ResourceType tmp : values){
			if (tmp.getValue().equals(v)){
				return tmp;
			}
		}
		return null;
	}	
}
