package com.dtstack.engine.domain;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/11/08
 */
public class NotifyRecord extends AppTenantEntity {
    /**
     * 通知id
     */
    private Long notifyId;
    /**
     * 内容文本
     */
    private Long contentId;
    /**
     * 任务状态
     */
    private Integer status;
    /**
     * 批处理调度的时间
     */
    private String cycTime;

    public Long getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(Long notifyId) {
        this.notifyId = notifyId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }
}
