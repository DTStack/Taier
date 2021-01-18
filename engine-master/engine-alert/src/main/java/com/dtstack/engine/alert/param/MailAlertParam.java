package com.dtstack.engine.alert.param;

import java.io.File;
import java.util.List;

/**
 * Date: 2020/5/19
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class MailAlertParam extends AlertParam {
    private List<String> emails;
    private String subject;
    private List<File> attachFiles;

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<File> getAttachFiles() {
        return attachFiles;
    }

    public void setAttachFiles(List<File> attachFiles) {
        this.attachFiles = attachFiles;
    }
}
