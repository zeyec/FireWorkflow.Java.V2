/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.model.servicedef;

import java.util.List;

import org.fireflow.model.ModelElement;

/**
 * Service定义xml格式:<br>
 * <services><br>
 * 	<service type="human" id="" name="" displayName="ABC业务审批" category="/xyz业务/审批岗"><br>
 * 		<operations><br/>
 * 			<operation><br>
 * 				<name>/zyz/shenpi/ABCShenpi.jsp</name><br>
 * 				<iospecification><br>
 * 					<inputs><br>
 *						<input name="" dataType=""/><br>
 *					</inputs><br>
 *					<outputs><br>
 *						<output name="" dataType=""/><br>
 *					</outputs><br>
 * 				</iospecification><br>
 * 			</operation><br>
 *      </operations>
 * 		<propGroups><br>
 * 			<propGroup name="" displayName="表单字段权限配置"><br>
 * 				<prop name="" displayName="" defaultValue="" description=""></prop><br>
 * 			</propGroup><br>
 * 		</propGroups><br>
 * 	</service><br>
 * 
 * </services><br>
 * 
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public interface Service extends ModelElement{
	/**
	 * 服务类别
	 * 
	 * @return
	 */
	public String getServiceType();
	public void setServiceType(String serviceType);
	
	/**
	 * 业务类别
	 * @return
	 */
	public String getBizCategory();
	
	public void setBizCategory(String category);
	
	/**
	 * 服务中的操作，每种服务类型的操作的格式各不相同，由服务的开发者定义。
	 * @return
	 */
	public Operation getOperation(String opName);
	public void setOperation(Operation operation);
	public List<Operation> getOperations();

	
	public List<ServicePropGroup> getServicePropGroups();
	
	public void setServicePropGroups(List<ServicePropGroup> propGroups);
	
	public ServicePropGroup getServicePropGroup(String propGroupName);
	
	/**
	 * 返回executorName,系统将利用org.fireflow.engine.modules.beanfactory.BeanFactory.getBean(executorName)获得service实例。
	 * 如果executorName以"#"号开始，则表示容器中一个bean的Name，否则表示bean的classname
	 * @return
	 */
	public String getExecutorName();
	
//	/**
//	 * 服务xml定义文件的classpath全路径名
//	 * (file name 在这里不合理，2011-02-17)
//	 * @return
//	 */
//	public String getFileName();
//	/**
//	 * Service的实现方式
//	 * @return
//	 */
//	public Implementation getImplementation();
//	public void setImplementation(Implementation svcImpl);
//	
//	/**
//	 * 服务的操作定义
//	 * @return
//	 */
//	public List<Operation> getOperations();
	
}
