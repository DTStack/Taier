package com.dtstack.engine.master.router.login.domain;

/**
 * @author toutian
 */
public class TenantAdmin {

    private Long userId;

    private String userName;

    private String fullName;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
