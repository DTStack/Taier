package com.dtstack.engine.api.dto;

/**
 * @Auther: dazhi
 * @Date: 2020/10/10 11:01 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class UserMessageDTO {
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
}
