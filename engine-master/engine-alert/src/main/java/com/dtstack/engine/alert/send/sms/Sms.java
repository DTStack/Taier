package com.dtstack.engine.alert.send.sms;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/7/7
 */
public class Sms {

    private String username = "用户";

    private String recNum;

    private String content;

    private String from = "【袋鼠云 - RD-OS平台】";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRecNum() {
        return recNum;
    }

    public void setRecNum(String recNum) {
        this.recNum = recNum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
