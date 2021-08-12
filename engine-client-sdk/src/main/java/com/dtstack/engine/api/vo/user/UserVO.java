package com.dtstack.engine.api.vo.user;

/**
 * @Auther: dazhi
 * @Date: 2021/5/11 7:54 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class UserVO {

    private String userName;

    private String phoneNumber;

    private Long dtuicUserId;

    private String email;

    private Integer status;

    private Long defaultProjectId;

    private Integer rootUser;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getDtuicUserId() {
        return dtuicUserId;
    }

    public void setDtuicUserId(Long dtuicUserId) {
        this.dtuicUserId = dtuicUserId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getDefaultProjectId() {
        return defaultProjectId;
    }

    public void setDefaultProjectId(Long defaultProjectId) {
        this.defaultProjectId = defaultProjectId;
    }

    public Integer getRootUser() {
        return rootUser;
    }

    public void setRootUser(Integer rootUser) {
        this.rootUser = rootUser;
    }
}
