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
package org.fireflow.engine.entity.repository;

import java.util.Locale;
import java.util.ResourceBundle;

import org.fireflow.engine.entity.EntityProperty;

/**
 * @author 非也
 * @version 2.0
 */
public enum ProcessDescriptorProperty 	implements EntityProperty{
		ID("id"),
		NAME("name"),
		DISPLAY_NAME("displayName"),
		DESCRIPTION("description"),
		FILE_NAME("fileName"),

		PROCESS_ID("processId"),
		VERSION("version"),
		PROCESS_TYPE("processType"),
		BIZ_CATEGORY("bizCategory"),
		
		PUBLISH_STATE("publishState"),
		LATEST_EDITOR("latestEditor"),
		LATEST_EDIT_TIME("latestEditTime"),
		LATEST_OPERATION("latestOperation"),
		
		OWNER_DEPT_ID("ownerDeptId"),
		OWNER_DEPT_NAME("ownerDeptName"),
		APPROVER("approver"),
		APPROVED_TIME("approvedTime"),

		
		;
		
		
		private String propertyName = null;
		private ProcessDescriptorProperty(String propertyName){
			this.propertyName = propertyName;
		}
		
		public String getPropertyName(){
			return this.propertyName;
		}
		
		public String getColumnName(){
			return this.name();
		}
		
		public String getDisplayName(Locale locale){
			ResourceBundle resb = ResourceBundle.getBundle("myres", locale);
			return resb.getString(this.name());
		}
		
		public String getDisplayName(){
			return this.getDisplayName(Locale.getDefault());
		}
		
}
