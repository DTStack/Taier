package com.dtstack.engine.api.domain;

import com.dtstack.engine.api.annotation.Unique;
import io.swagger.annotations.ApiModel;

/**
 * @author: toutian
 */
@ApiModel
public class User extends BaseEntity {

    private String userName;

    private String phoneNumber;

    @Unique
    private Long dtuicUserId;

    private String email;

    private Integer status;

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

}
