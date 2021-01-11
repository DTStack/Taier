package com.dtstack.engine.alert.send.mail;


import com.dtstack.engine.api.enums.MailType;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/4/28
 */
public class Mail {

    private MailType mailType;

    /**
     * accepter email address
     */
    private List<String> to;

    /**
     * carbon copy email address
     */
    private List<String> cc;

    /**
     * views.mail content
     */
    private String content;

    /**
     * views.mail title
     */
    private String title;

    /**
     * views.mail sender
     */
    private String sender;

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public MailType getMailType() {
        return mailType;
    }

    public void setMailType(MailType mailType) {
        this.mailType = mailType;
    }
}
