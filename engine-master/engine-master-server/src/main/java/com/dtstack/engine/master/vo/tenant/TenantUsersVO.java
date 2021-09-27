package com.dtstack.engine.master.vo.tenant;

import java.util.Date;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/7/15
 */
public class TenantUsersVO {

    private Long id;
    private Long creator;
    private Date gmtCreate;
    private String username;
    private String fullName;
    private String phone;
    private Boolean active;
    private String email;
    private String company;
    private Long ownTelantId;
    private String password;
    private String externalId;
    private Boolean root;
    private Boolean admin;
    private Boolean newUser;
    private Boolean firstLogin;
    private Date lastLoginDate;
    private Date userTime;
    private Date gmtJoin;

    public TenantUsersVO() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreator() {
        return this.creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public Date getGmtCreate() {
        return this.gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getActive() {
        return this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return this.company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Long getOwnTelantId() {
        return this.ownTelantId;
    }

    public void setOwnTelantId(Long ownTelantId) {
        this.ownTelantId = ownTelantId;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Boolean getRoot() {
        return this.root;
    }

    public void setRoot(Boolean root) {
        this.root = root;
    }

    public Boolean getAdmin() {
        return this.admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getNewUser() {
        return this.newUser;
    }

    public void setNewUser(Boolean newUser) {
        this.newUser = newUser;
    }

    public Boolean getFirstLogin() {
        return this.firstLogin;
    }

    public void setFirstLogin(Boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    public Date getLastLoginDate() {
        return this.lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getUserTime() {
        return this.userTime;
    }

    public void setUserTime(Date userTime) {
        this.userTime = userTime;
    }

    public Date getGmtJoin() {
        return this.gmtJoin;
    }

    public void setGmtJoin(Date gmtJoin) {
        this.gmtJoin = gmtJoin;
    }
}
