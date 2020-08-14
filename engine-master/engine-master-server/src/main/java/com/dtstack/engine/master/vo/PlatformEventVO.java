package com.dtstack.engine.master.vo;

/**
 * @author yuebai
 * @date 2020-08-13
 */
public class PlatformEventVO {

    /**
     * 由于接口不需要登陆，添加加密验证
     */
    private String sign;

    private String eventCode;

    private Long userId;
    private String token;
    private String phone;

    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "PlatformEventVO{" +
                "sign='" + sign + '\'' +
                ", eventCode='" + eventCode + '\'' +
                ", userId=" + userId +
                ", token='" + token + '\'' +
                ", phone='" + phone + '\'' +
                ", tenantId=" + tenantId +
                '}';
    }
}
