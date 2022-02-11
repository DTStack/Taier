package com.dtstack.taiga.dao.domain;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author: toutian
 */
@TableName("console_user")
public class User extends BaseEntity {

    private String userName;

    private String phoneNumber;

    private String password;

    private String email;

    private Integer status;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

}
