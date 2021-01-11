package com.dtstack.engine.api.param;


import java.util.List;

/**
 * @author yuebai
 * @date 2019-05-17
 */
public class NotifyRecordParam extends NotifyParam {

    private Long readId;

    private List<Long> notifyRecordIds;

    private Long notifyRecordId;

    private String content;

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

    public List<Long> getNotifyRecordIds() {
        return notifyRecordIds;
    }

    public void setNotifyRecordIds(List<Long> notifyRecordIds) {
        this.notifyRecordIds = notifyRecordIds;
    }

    public Long getReadId() {
        return readId;
    }

    public void setReadId(Long readId) {
        this.readId = readId;
    }
}
