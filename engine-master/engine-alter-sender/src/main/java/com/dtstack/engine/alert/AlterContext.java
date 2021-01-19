package com.dtstack.engine.alert;


import com.dtstack.engine.alert.enums.AlertGateCode;

import java.io.File;
import java.util.List;

/**
 * Reason:
 * Date: 2017/5/15
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
public class AlterContext {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 配置信息
     */
    private String alertGateJson;

    /**
     * 类型
     */
    private AlertGateCode alertGateCode;

    /**
     * jar路径
     */
    private String jarPath;

    /**
     * 事件响应
     */
    private EventMonitor eventMonitor;

    /**
     * 邮件必传
     *
     */
    private List<String> emails;

    /**
     * 只有邮件会用到，非必传
     */
    private List<File> attachFiles;

    /**
     * 钉钉必须
     */
    private String ding;

    /**
     * 手机号 短信和电话必传
     */
    private String phone;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AlertGateCode getAlertGateCode() {
        return alertGateCode;
    }

    public void setAlertGateCode(AlertGateCode alertGateCode) {
        this.alertGateCode = alertGateCode;
    }

    public EventMonitor getEventMonitor() {
        return eventMonitor;
    }

    public void setEventMonitor(EventMonitor eventMonitor) {
        this.eventMonitor = eventMonitor;
    }

    public String getAlertGateJson() {
        return alertGateJson;
    }

    public void setAlertGateJson(String alertGateJson) {
        this.alertGateJson = alertGateJson;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<File> getAttachFiles() {
        return attachFiles;
    }

    public void setAttachFiles(List<File> attachFiles) {
        this.attachFiles = attachFiles;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getDing() {
        return ding;
    }

    public void setDing(String ding) {
        this.ding = ding;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMark(){
        return "";
    }


}
