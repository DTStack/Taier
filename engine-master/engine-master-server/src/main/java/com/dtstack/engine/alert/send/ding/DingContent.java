package com.dtstack.engine.alert.send.ding;

import org.apache.http.entity.StringEntity;

import java.util.List;

/**
 * @explanation 钉钉消息实体类
 * @author jiangbo
 * @date 2018/11/17
 */
public class DingContent {

    private List<String> webHooks;

    private StringEntity entity;

    private String subject;

    private String message;

    private List<String> at;

    public List<String> getAt() {
        return at;
    }

    public void setAt(List<String> at) {
        this.at = at;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getWebHooks() {
        return webHooks;
    }

    public void setWebHooks(List<String> webHooks) {
        this.webHooks = webHooks;
    }

    public StringEntity getEntity() {
        return entity;
    }

    public void setEntity(StringEntity entity) {
        this.entity = entity;
    }
}
