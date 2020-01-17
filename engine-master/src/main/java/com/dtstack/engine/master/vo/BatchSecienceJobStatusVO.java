package com.dtstack.engine.master.vo;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/25
 */
public class BatchSecienceJobStatusVO {
	
	private Long totalLabCount;
	
	private Long totalNotebookCount;
	
	private Long deployLabCount;
	
	private Long deployNotebookCount;
	
	private Long failLabCount;
	
	private Long failNotebookCount;
	
	private Long successLabCount;
	
	private Long successNotebookCount;

	public Long getTotalLabCount() {
		return totalLabCount;
	}

	public void setTotalLabCount(Long totalLabCount) {
		this.totalLabCount = totalLabCount;
	}

	public Long getTotalNotebookCount() {
		return totalNotebookCount;
	}

	public void setTotalNotebookCount(Long totalNotebookCount) {
		this.totalNotebookCount = totalNotebookCount;
	}

	public Long getDeployLabCount() {
		return deployLabCount;
	}

	public void setDeployLabCount(Long deployLabCount) {
		this.deployLabCount = deployLabCount;
	}

	public Long getDeployNotebookCount() {
		return deployNotebookCount;
	}

	public void setDeployNotebookCount(Long deployNotebookCount) {
		this.deployNotebookCount = deployNotebookCount;
	}

	public Long getFailLabCount() {
		return failLabCount;
	}

	public void setFailLabCount(Long failLabCount) {
		this.failLabCount = failLabCount;
	}

	public Long getFailNotebookCount() {
		return failNotebookCount;
	}

	public void setFailNotebookCount(Long failNotebookCount) {
		this.failNotebookCount = failNotebookCount;
	}

	public Long getSuccessLabCount() {
		return successLabCount;
	}

	public void setSuccessLabCount(Long successLabCount) {
		this.successLabCount = successLabCount;
	}

	public Long getSuccessNotebookCount() {
		return successNotebookCount;
	}

	public void setSuccessNotebookCount(Long successNotebookCount) {
		this.successNotebookCount = successNotebookCount;
	}

}
