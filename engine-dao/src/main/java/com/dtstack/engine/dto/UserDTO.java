package com.dtstack.engine.dto;


import com.dtstack.engine.domain.User;

/**
 * @author sishu.yss
 */
public class UserDTO extends User {

    private Long defaultProjectId;

    private long tenantId;

    private int roleValue;

    private String roleName;

    private Integer rootUser;

    public Long getDefaultProjectId() {
        return defaultProjectId;
    }

    public void setDefaultProjectId(Long defaultProjectId) {
        this.defaultProjectId = defaultProjectId;
    }

    public long getTenantId() {
        return tenantId;
    }

    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }

    public int getRoleValue() {
        return roleValue;
    }

    public void setRoleValue(int roleValue) {
        this.roleValue = roleValue;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getRootUser() {
        return rootUser;
    }

    public void setRootUser(Integer rootUser) {
        this.rootUser = rootUser;
    }
}
