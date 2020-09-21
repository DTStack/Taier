package com.dtstack.engine.api.vo.tenant;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/7/29 4:25 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class UserTenantVO {

    private Long tenantId;

    private String tenantName;

    private String tenantDesc;

    private Boolean current;

    private Boolean lastLogin;

    private Boolean admin;

    private List<TenantAdminVO> adminList;

    private String createTime;

    private Integer otherUserCount;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantDesc() {
        return tenantDesc;
    }

    public void setTenantDesc(String tenantDesc) {
        this.tenantDesc = tenantDesc;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public Boolean getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Boolean lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public List<TenantAdminVO> getAdminList() {
        return adminList;
    }

    public void setAdminList(List<TenantAdminVO> adminList) {
        this.adminList = adminList;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getOtherUserCount() {
        return otherUserCount;
    }

    public void setOtherUserCount(Integer otherUserCount) {
        this.otherUserCount = otherUserCount;
    }
}
