package com.dtstack.engine.master.router.login.domain;

/**
 * @author toutian
 */
public class DtUicUser {
	
	private Long userId;
	
	private String userName;
	
	private String email;
	
	private String phone;
	
	private Long tenantId;

	/**
	 * 租户所有者用户id
	 */
	private Long tenantOwnerId;

	private String tenantName;
	
	private Boolean tenantOwner;

	/**
	 * 仅标注是否是管理员
	 */
	private Boolean isRootOnly;

	/**
	 * 仅标注是否是tenant owner
	 */
	private Boolean isOwnerOnly;

	public Boolean getTenantOwner() {
		return tenantOwner;
	}

	public void setTenantOwner(Boolean tenantOwner) {
		this.tenantOwner = tenantOwner;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Long getTenantOwnerId() {
		return tenantOwnerId;
	}

	public void setTenantOwnerId(Long tenantOwnerId) {
		this.tenantOwnerId = tenantOwnerId;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public Boolean getRootOnly() {
		return isRootOnly;
	}

	public void setRootOnly(Boolean rootOnly) {
		isRootOnly = rootOnly;
	}

	public Boolean getOwnerOnly() {
		return isOwnerOnly;
	}

	public void setOwnerOnly(Boolean ownerOnly) {
		isOwnerOnly = ownerOnly;
	}
}
