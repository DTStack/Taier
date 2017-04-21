package com.dtstack.rdos.engine.entrance.db.dataobject;


/**
 * 
 * @author sishu.yss
 *
 */
public class TenantProjectObject extends DataObject{
	
	private long tenantId;
	
	private long projectId;

	public long getTenantId() {
		return tenantId;
	}

	public void setTenantId(long tenantId) {
		this.tenantId = tenantId;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	
}
