package org.fireflow.engine.service;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.exception.ServiceExecutionException;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.servicedef.Service;
import org.fireflow.pvm.pdllogic.ContinueDirection;

/**
 * 相当于TaskInstanceRunner
 * @author 陈乜云
 *
 */
public interface ServiceExecutor {
	public static final int CLOSE_ACTIVITY = ContinueDirection.CLOSE_ME;
	public static final int WAITING_FOR_CLOSE = ContinueDirection.WAITING_FOR_CLOSE;
	public static final int START_NEXT_AND_WAITING_FOR_CLOSE = ContinueDirection.START_NEXT_AND_WAITING_FOR_CLOSE;
	
	/**
	 * 初始化Service运行环境，如建立数据库连接等。
	 * 建立的环境信息存放在Map中
	 * @return
	 */
//	public Map before();
	
	/**
	 * execute或者onComplete方法执行完毕后，引擎调用该方法执行收尾工作。
	 * @param serviceContext
	 */
//	public void after(Map serviceContext);
	
	/**
	 * 执行Service，如果是同步调用，则返回true；
	 * 如果是异步调用（需要长时间执行的service）,则返回false，service结束后回调complete(...)方法
	 * @param params
	 * @return
	 */
	public boolean executeService(WorkflowSession session,ActivityInstance activityInstance, ServiceBinding serviceBinding,
			ResourceBinding resourceBinding)throws ServiceExecutionException;
	

	public void onServiceCompleted(WorkflowSession session,ActivityInstance activityInstance);
	
	/**
	 * ActivityInstanceManager调用该方法决定activityInstance是否可以结束。返回值是：<br/>
	 * ServiceExecutor.CLOSE_ACTIVITY：该值表示activityInstance可以被关闭，并启动后续活动；<br/>
	 * ServiceExecutor.WAITING_FOR_CLOSE：该值表示activityInstance继续保持Runing状态<br/>
	 * ServiceExecutor.START_NEXT_AND_WAITING_FOR_CLOSE：该值表示启动后续活动，但是当前activityInstance继续保持Running状态。
	 * 
	 * @param session
	 * @param activityInstance
	 * @return
	 */
	public int determineActivityCloseStrategy(WorkflowSession session,ActivityInstance activityInstance);
	
	public String getServiceType();
    //***********************这些property的作用是什么？*********************/
//    public Properties getProperties();
//    
//    public void setProperties(Properties props);
//    
//    public void setProperty(String key,String value);	
}
