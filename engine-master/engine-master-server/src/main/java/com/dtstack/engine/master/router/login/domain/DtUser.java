package com.dtstack.engine.master.router.login.domain;

/**
 * @Auther: dazhi
 * @Date: 2021/5/7 11:46 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class DtUser {
    private Long id;
    private String userName;
    private String fullName;
    private String phone;
    private Boolean locked;
    private Boolean ldapUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getLdapUser() {
        return ldapUser;
    }

    public void setLdapUser(Boolean ldapUser) {
        this.ldapUser = ldapUser;
    }
}
