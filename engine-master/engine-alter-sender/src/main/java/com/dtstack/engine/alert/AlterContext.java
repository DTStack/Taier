package com.dtstack.engine.alert;


import com.dtstack.engine.alert.enums.AlertGateCode;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Reason:
 * Date: 2017/5/15
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
public class AlterContext {

    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名： 一般用于替换
     */
    private String userName;

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
     * 模板
     */
    private String alertTemplate;

    /**
     * jar路径
     */
    private String jarPath;

    /**
     * 事件响应
     */
    private List<EventMonitor> eventMonitors;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 扩展参数: 传入事件响应,内部不会使用
     *
     */
    private Map<String,Object> extendedPara;

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

    public List<EventMonitor> getEventMonitors() {
        return eventMonitors;
    }

    public void setEventMonitors(List<EventMonitor> eventMonitors) {
        this.eventMonitors = eventMonitors;
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

    public Map<String, Object> getExtendedPara() {
        return extendedPara;
    }

    public void setExtendedPara(Map<String, Object> extendedPara) {
        this.extendedPara = extendedPara;
    }

    public String getAlertTemplate() {
        return alertTemplate;
    }

    public void setAlertTemplate(String alertTemplate) {
        this.alertTemplate = alertTemplate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMark(){
        String mark = "";

        if (id != null) {
            mark += id + "";
        }

        if (alertGateCode != null) {
            mark += "_type_"+alertGateCode.name();
        }

        return mark;
    }



}
