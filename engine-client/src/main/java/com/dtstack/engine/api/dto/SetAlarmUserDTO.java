package com.dtstack.engine.api.dto;

/**
 * @author yuebai
 * @date 2019-05-20
 */
public class SetAlarmUserDTO {
    private Long userId;
    private String username;
    private String email;
    private String telephone;
    private String dingding;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getDingding() {
        return dingding;
    }

    public void setDingding(String dingding) {
        this.dingding = dingding;
    }

    @Override
    public String toString() {
        return "SetAlarmUserDTO{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", dingding='" + dingding + '\'' +
                '}';
    }
}
