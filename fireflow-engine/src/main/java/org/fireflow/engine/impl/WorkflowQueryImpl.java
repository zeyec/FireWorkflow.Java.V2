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
package org.fireflow.engine.impl;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.engine.Criterion;
import org.fireflow.engine.Order;
import org.fireflow.engine.WorkflowQuery;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.WorkflowStatement;
import org.fireflow.engine.entity.WorkflowEntity;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class WorkflowQueryImpl<T extends WorkflowEntity> implements WorkflowQuery<T> {
	List<Criterion> criterions = new ArrayList<Criterion>();
	
	List<Order> orders = new ArrayList<Order>();
	
	int firstResult = 0;
	int maxResults = -1;//表示查询所有
	
	boolean queryFromHistory = false;
	
	WorkflowSession session = null;
	Class<T> interfaceClass = null;
	WorkflowStatement statement = null;
	
	public WorkflowQueryImpl(WorkflowSession session,Class<T> interfaceClass,String processType){
		this.session = session;
		this.interfaceClass = interfaceClass;
		statement = this.session.createWorkflowStatement(processType);
		
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#add(org.fireflow.engine.Criterion)
	 */
	public WorkflowQuery<T> add(Criterion criterion) {
		
		criterions.add(criterion);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#addOrder(org.fireflow.engine.Order)
	 */
	public WorkflowQuery<T> addOrder(Order order) {
		orders.add(order);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#count()
	 */
	public int count() {
		return this.statement.executeQueryCount(this);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#get(java.lang.String)
	 */
	public T get(String entityId) {
		return (T)this.statement.getEntity(entityId, this.interfaceClass);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#getFirstResult()
	 */
	public int getFirstResult() {
		return this.firstResult;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#getMaxResults()
	 */
	public int getMaxResults() {
		return this.maxResults;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#isQueryFromHistory()
	 */
	public boolean isQueryFromHistory() {
		
		return this.queryFromHistory;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#list()
	 */
	public List<T> list() {
		return this.statement.executeQueryList(this);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#reset()
	 */
	public WorkflowQuery<T> reset() {
		this.criterions.clear();
		this.orders.clear();
		this.firstResult=0;
		this.maxResults=-1;
		this.queryFromHistory = false;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#setFirstResult(int)
	 */
	public WorkflowQuery<T> setFirstResult(int rowNum) {
		this.firstResult = rowNum;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#setMaxResults(int)
	 */
	public WorkflowQuery<T> setMaxResults(int size) {
		this.maxResults = size;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#setQueryFromHistory(boolean)
	 */
	public WorkflowQuery<T> setQueryFromHistory(boolean b) {
		this.queryFromHistory = b;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#unique()
	 */
	public T unique() {
		List<T> entities = this.statement.executeQueryList(this);
		if (entities!=null && entities.size()>0){
			return entities.get(0);
		}
		return null;
	}

	public Class<T> getEntityClass(){
		return this.interfaceClass;
	}
	
	public List<Criterion> getAllCriterions(){
		return this.criterions;
	}
	public List<Order> getAllOrders(){
		return this.orders;
	}
}
