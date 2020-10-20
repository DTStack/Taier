package com.dtstack.engine.alert.domian;

import com.dtstack.engine.api.dto.UserMessageDTO;
import com.dtstack.engine.api.enums.MailType;

/**
 * Reason:
 * Date: 2017/5/15
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
public class Notice {

    private Integer senderType;

    private UserMessageDTO userDTO;

    private String title;

    private Long contentId;

    private String content;

    private String webhook;

    private Long notifyRecordId;

    private Integer appType;

    private Long tenantId;

    private Long projectId;

    private MailType mailType;

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public Integer getSenderType() {
        return senderType;
    }

    public void setSenderType(Integer senderType) {
        this.senderType = senderType;
    }

    public UserMessageDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserMessageDTO userDTO) {
        this.userDTO = userDTO;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getNotifyRecordId() {
        return notifyRecordId;
    }

    public void setNotifyRecordId(Long notifyRecordId) {
        this.notifyRecordId = notifyRecordId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public MailType getMailType() {
        return mailType;
    }

    public void setMailType(MailType mailType) {
        this.mailType = mailType;
    }
}
