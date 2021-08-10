package com.dtstack.engine.master.router.login.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yuebai
 * @date 2021-08-04
 */
public class DTToken implements Serializable {
    /**
     * 用户id
     */
    public static final String USER_ID = "user_id";
    /**
     * 用户名
     */
    public static final String USER_NAME = "user_name";
    /**
     * 租户id
     */
    public static final String TENANT_ID = "tenant_id";
    /**
     * 登录用户id
     */
    private Long userId;
    /**
     * 登录用户名
     */
    private String userName;
    /**
     * 登录租户组id
     */
    private Long tenantId;
    /**
     * 过期时间
     */
    private Date expireAt;

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

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }
}