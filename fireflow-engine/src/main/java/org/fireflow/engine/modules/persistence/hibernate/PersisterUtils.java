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

import org.hibernate.criterion.Restrictions;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class PersisterUtils {
	public static org.hibernate.criterion.Criterion fireCriterion2HibernateCriterion(
			org.fireflow.engine.Criterion fireCriterion) {
		String operation = fireCriterion.getOperation().trim();
		
		if (operation.equals("=")){
			return Restrictions.eq(fireCriterion.getEntityProperty().getPropertyName(), fireCriterion.getValues()[0]);
		}
		else if (operation.equals("<>")){
			return Restrictions.ne(fireCriterion.getEntityProperty().getPropertyName(), fireCriterion.getValues()[0]);
		}
		else if (operation.equals("like")){
			return Restrictions.like(fireCriterion.getEntityProperty().getPropertyName(), fireCriterion.getValues()[0]);
		}
		else if (operation.equals(">")){
			return Restrictions.gt(fireCriterion.getEntityProperty().getPropertyName(), fireCriterion.getValues()[0]);
		}
		else if (operation.equals("<")){
			return Restrictions.lt(fireCriterion.getEntityProperty().getPropertyName(), fireCriterion.getValues()[0]);
		}	
		else if (operation.equals(">=")){
			return Restrictions.ge(fireCriterion.getEntityProperty().getPropertyName(), fireCriterion.getValues()[0]);
		}		
		else if (operation.equals("<=")){
			return Restrictions.le(fireCriterion.getEntityProperty().getPropertyName(), fireCriterion.getValues()[0]);
		}
		else if (operation.equals("is null")){
			return Restrictions.isNull(fireCriterion.getEntityProperty().getPropertyName());
		}	
		else if (operation.equals("is not null")){
			return Restrictions.isNotNull(fireCriterion.getEntityProperty().getPropertyName());
		}	
		else if (operation.equals("in")){
			return Restrictions.in(fireCriterion.getEntityProperty().getPropertyName(),fireCriterion.getValues());
		}	
		else if (operation.equals("between")){
			return Restrictions.between(fireCriterion.getEntityProperty().getPropertyName(),fireCriterion.getValues()[0],fireCriterion.getValues()[1]);
		}		
		else if (operation.equals("and")){
			org.fireflow.engine.Criterion left = (org.fireflow.engine.Criterion)fireCriterion.getValues()[0];
			org.fireflow.engine.Criterion right = (org.fireflow.engine.Criterion)fireCriterion.getValues()[1];
			org.hibernate.criterion.Criterion hLeft = fireCriterion2HibernateCriterion(left);
			org.hibernate.criterion.Criterion hRight = fireCriterion2HibernateCriterion(right);
			return Restrictions.and(hLeft, hRight);
			
		}		
		else if (operation.equals("or")){
			org.fireflow.engine.Criterion left = (org.fireflow.engine.Criterion)fireCriterion.getValues()[0];
			org.fireflow.engine.Criterion right = (org.fireflow.engine.Criterion)fireCriterion.getValues()[1];
			org.hibernate.criterion.Criterion hLeft = fireCriterion2HibernateCriterion(left);
			org.hibernate.criterion.Criterion hRight = fireCriterion2HibernateCriterion(right);
			return Restrictions.or(hLeft, hRight);
			
		}	
		return null;
	}
	
	public static org.hibernate.criterion.Order fireOrder2HibernateOrder(
			org.fireflow.engine.Order fireOrder) {
		if (fireOrder.isAscending()){
			return org.hibernate.criterion.Order.asc(fireOrder.getEntityProperty().getPropertyName());
		}else{
			return org.hibernate.criterion.Order.desc(fireOrder.getEntityProperty().getPropertyName());
		}
	}
}
