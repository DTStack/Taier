package com.dtstack.engine.api.dto;


import com.dtstack.engine.api.domain.NotifyRecordRead;

public class NotifyRecordReadDTO extends NotifyRecordRead {
    private String content;

    private Integer status;

    private String gmtCreateFormat;

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

    public String getGmtCreateFormat() {
        return gmtCreateFormat;
    }

    public void setGmtCreateFormat(String gmtCreateFormat) {
        this.gmtCreateFormat = gmtCreateFormat;
    }
}