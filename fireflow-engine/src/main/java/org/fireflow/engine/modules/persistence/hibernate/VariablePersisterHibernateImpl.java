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
package org.fireflow.engine.modules.persistence.hibernate;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.engine.WorkflowQuery;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.entity.runtime.Scope;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.impl.AbsVariable;
import org.fireflow.engine.entity.runtime.impl.VariableHistory;
import org.fireflow.engine.entity.runtime.impl.VariableImpl;
import org.fireflow.engine.modules.persistence.VariablePersister;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class VariablePersisterHibernateImpl extends AbsPersisterHibernateImpl
		implements VariablePersister {


	public Class getEntityClass4Runtime(Class interfaceClz){
		return VariableImpl.class;
	}

	public Class getEntityClass4History(Class interfaceClz){
		return VariableHistory.class;
	}
	@Override
	public <T extends WorkflowEntity> java.util.List<T> list(final WorkflowQuery<T> q) {
		List<T> vars = super.list(q);
		if (vars!=null && vars.size()>0){
			for (T v:vars){
				if (v!=null){
					((AbsVariable)v).setValue(this.deserializeString2Object(((AbsVariable)v).getValueAsString()));
				}
			}
		}
		return vars;
	}
	@Override
	public <T extends WorkflowEntity> T find(Class<T> entityClz, String entityId) {
		AbsVariable var = (AbsVariable)super.find(entityClz, entityId);
		var.setValue(this.deserializeString2Object(var.getValueAsString()));
		return (T)var;
	}
	
	@Override
	public void saveOrUpdate(Object entity) {
		AbsVariable v = ((AbsVariable)entity);
		v.setValueAsString(this.serializeObject2String(v.getValue()));
		super.saveOrUpdate(v);
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.VariablePersister#findVariable(java.lang.String, java.lang.String)
	 */
	public Variable findVariable(final String scopeId, final String name) {
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(VariableImpl.class);
				criteria.add(Restrictions.eq("scopeId", scopeId));
				criteria.add(Restrictions.eq("name", name));
				return criteria.uniqueResult();
			}
			
		});
		Variable v = (Variable)result;
		if (v!=null){
			((AbsVariable)v).setValue(this.deserializeString2Object(v.getValueAsString()));
		}
		return v;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.VariablePersister#findVariables(java.lang.String)
	 */
	public java.util.List<Variable> findVariables(final String scopeId) {
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(VariableImpl.class);
				criteria.add(Restrictions.eq("scopeId", scopeId));
				return criteria.list();
			}
			
		});
		List<Variable> vars = (java.util.List<Variable>)result;
		if (vars!=null && vars.size()>0){
			for(Variable v : vars){
				if (v!=null){
					((AbsVariable)v).setValue(this.deserializeString2Object(v.getValueAsString()));
				}
			}
		}
		return vars;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.VariablePersister#findVariableValues(java.lang.String, java.lang.String)
	 */
	public Map<String, Object> findVariableValues(String scopeId) {
		List<Variable> vars = this.findVariables(scopeId);
		Map<String,Object> varValues = new HashMap<String,Object>();
		if (vars!=null && vars.size()>0){
			for (Variable var : vars){
				varValues.put(var.getName(), var.getValue());
			}
		}
		return varValues;
	}

	public Object findVariableValue(String scopeId,String name){
		Variable v = this.findVariable(scopeId, name);
		if (v==null)return null;
		return v.getValue();
	}
	public Variable setVariable(Scope scope,String name,Object value){
		Variable v = this.findVariable(scope.getScopeId(), name);
		if (v!=null){
			((AbsVariable)v).setValue(value);
			//TODO 是否检验类型一致性？
			if (value!=null){
				((AbsVariable)v).setDataType(value.getClass().getName());
			}
			this.saveOrUpdate(v);
			return v;
		}else{
			v = new VariableImpl();
			((AbsVariable)v).setScopeId(scope.getScopeId());
			((AbsVariable)v).setName(name);
			((AbsVariable)v).setValue(value);
			if (value!=null){
				((AbsVariable)v).setDataType(value.getClass().getName());
			}
			((AbsVariable)v).setProcessId(scope.getProcessId());
			((AbsVariable)v).setVersion(scope.getVersion());
			((AbsVariable)v).setProcessType(scope.getProcessType());
			
			this.saveOrUpdate(v);
			return v;
		}

	}
	

	public Object deserializeString2Object(String strValue) {
		XStream xstream = new XStream();
		Object obj = xstream.fromXML(strValue);
		return obj;
	}


	public String serializeObject2String(Object object) {
		XStream xstream = new XStream();
		String s = xstream.toXML(object);
		return s;
	}
}
