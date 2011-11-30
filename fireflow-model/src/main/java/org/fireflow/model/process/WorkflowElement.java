package org.fireflow.model.process;

import java.util.Map;

import org.fireflow.model.ModelElement;
import org.fireflow.model.process.lifecycle.InstanceCreatorDef;
import org.fireflow.model.process.lifecycle.InstanceExecutorDef;
import org.fireflow.model.process.lifecycle.InstanceTerminatorDef;


/**
 * 流程元素
 * @author 非也
 *
 */
public interface WorkflowElement extends ModelElement{
//	public InstanceCreatorDef getInstanceCreatorDef();
//	public InstanceExecutorDef getInstanceExecutorDef();
//	public InstanceTerminatorDef getInstanceTerminatorDef();
//	
//	public void setInstanceCreatorDef(InstanceCreatorDef instanceCreator);
//	public void setInstanceExecutorDef(InstanceExecutorDef instanceExecutor);
//	public void setInstanceTerminatorDef(InstanceTerminatorDef instanceTerminator);	
	
	public Map<String,String> getExtendedAttributes();
}
