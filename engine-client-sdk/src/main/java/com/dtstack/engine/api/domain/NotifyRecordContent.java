package com.dtstack.engine.api.domain;

/**
 * @Auther: dazhi
 * @Date: 2020/10/10 11:04 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class NotifyRecordContent extends TenantProjectEntity {
    private Integer appType;
    private String content;
    private Integer status;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
