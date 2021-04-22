package com.dtstack.engine.api.param;


import com.dtstack.engine.api.dto.SetAlarmUserDTO;

import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2019-05-20
 */
public class SetAlarmNotifyRecordParam extends NotifyRecordParam {
    private String title;
    private Long contentId;
    private Integer mailType;
    private String webhook;
    private List<Integer> senderTypes;
    private List<SetAlarmUserDTO> receivers;
    /**
     * 环境参数（jar告警时会传入）
     * @return
     */
    private Map<String,Object> env;

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

    public Integer getMailType() {
        return mailType;
    }

    public void setMailType(Integer mailType) {
        this.mailType = mailType;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public List<Integer> getSenderTypes() {
        return senderTypes;
    }

    public void setSenderTypes(List<Integer> senderTypes) {
        this.senderTypes = senderTypes;
    }

    public List<SetAlarmUserDTO> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<SetAlarmUserDTO> receivers) {
        this.receivers = receivers;
    }

    public Map<String, Object> getEnv() {
        return env;
    }

    public void setEnv(Map<String, Object> env) {
        this.env = env;
    }
}
