package org.fireflow.engine.entity.repository.impl;

import java.util.Date;

import org.fireflow.engine.entity.repository.ProcessDescriptor;

/**
 * 流程定义相关信息对象
 * @author 非也
 *
 */
public class ProcessDescriptorImpl implements ProcessDescriptor{
    protected String id; //主键
    protected String processId;//流程id
    protected String processType = null;//定义文件的语言类型，fpdl,xpdl,bepl...
    protected Integer version;//版本号
    
    protected String name; //流程英文名称
    protected String displayName;//流程显示名称
    protected String description;//流程业务说明
    protected String bizCategory = null;//业务类别
    protected String fileName = null;//流程文件在classpath中的全路径名

    protected Boolean publishState;//是否发布，1=已经发布,0未发布
    protected String latestOperation = null;
    protected Date latestEditTime = null;
    protected String latestEditor = null;//最后编辑流程的操作者姓名

    protected String ownerDeptName = null;//流程所属的部门名称
    protected String ownerDeptId = null;//流程所属的部门Id    
    protected String approver = null;//批准发布人
    protected Date approvedTime = null;//批准发布时间


    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }    


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ProcessRepositoryDescriptor#getApprovedTime()
	 */
	public Date getApprovedTime() {
		
		return this.approvedTime;
	}
	
	public void setApprovedTime(Date time){
		this.approvedTime = time;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ProcessRepositoryDescriptor#getApprover()
	 */
	public String getApprover() {
		return this.approver;
	}
	
	public void setApprover(String approver){
		this.approver = approver;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ProcessRepositoryDescriptor#getLatestEditTime()
	 */
	public Date getLatestEditTime() {
		return this.latestEditTime;
	}
	
	public void setLatestEditTime(Date time){
		this.latestEditTime = time;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ProcessRepositoryDescriptor#getLatestEditor()
	 */
	public String getLatestEditor() {
		return this.latestEditor;
	}
	
	public void setLatestEditor(String editorName){
		this.latestEditor = editorName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ProcessRepositoryDescriptor#getLatestOperation()
	 */
	public String getLatestOperation() {
		// TODO Auto-generated method stub
		return this.latestOperation;
	}
	
	public void setLatestOperation(String latestOperation){
		this.latestOperation = latestOperation;
	}
	
	

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ProcessRepositoryDescriptor#getOrgId()
	 */
	public String getOwnerDeptId() {
		return this.ownerDeptId;
	}
	
	public void setOwnerDeptId(String orgId){
		this.ownerDeptId = orgId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ProcessRepositoryDescriptor#getOrgName()
	 */
	public String getOwnerDeptName() {
		return this.ownerDeptName;
	}
	
	public void setOwnerDeptName(String orgName){
		this.ownerDeptName = orgName;
	}



	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ProcessRepositoryDescriptor#getProcessType()
	 */
	public String getProcessType() {
		return processType;
	}
	
	public void setProcessType(String processType){
		this.processType = processType;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ProcessRepositoryDescriptor#getPublishState()
	 */
	public Boolean getPublishState() {
		return publishState;
	}

	public void setPublishState(Boolean state){
		this.publishState = state;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
    
	public String getBizCategory(){
		return this.bizCategory;
	}
	
	public void setBizCategory(String bizCategory){
		this.bizCategory = bizCategory;
	}
}
