package com.dtstack.engine.api.vo.tenant;

/**
 * @Auther: dazhi
 * @Date: 2020/7/29 4:28 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class TenantAdminVO {

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
