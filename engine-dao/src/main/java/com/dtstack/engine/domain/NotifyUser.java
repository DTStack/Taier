package com.dtstack.engine.domain;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/11/08
 */
public class NotifyUser extends AppTenantEntity {
    /**
     * 通知id
     */
    private Long notifyId;
    /**
     * 接收人id
     */
    private Long userId;

    public Long getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(Long notifyId) {
        this.notifyId = notifyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
