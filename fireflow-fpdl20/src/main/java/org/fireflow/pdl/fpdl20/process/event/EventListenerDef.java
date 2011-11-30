package org.fireflow.pdl.fpdl20.process.event;

import org.fireflow.model.ModelElement;

public interface EventListenerDef extends ModelElement{
	/**
	 * 业务类别。<br/>
	 * 例如："户政业务/户口迁入"
	 * @return
	 */
	public String getBizCategory();
	
	/**
	 * 获得所引用的EventListener的bean的Id或bean的class name。
	 * 如果该值以"#"开头，则表示bean 的id，否则表示bean的class name。
	 * @return
	 */
	public String getBeanName();
	
}
