package com.dtstack.engine.alert.client.sms;

import com.dtstack.engine.alert.enums.AlertGateCode;

import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/1/19 2:22 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlterSendSmsBean {

    private String title;

    private String message;

    private String phone;

    private String className;

    private String jarPath;

    private String alertGateJson;

    private Map<String,Object> env;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getAlertGateJson() {
        return alertGateJson;
    }

    public void setAlertGateJson(String alertGateJson) {
        this.alertGateJson = alertGateJson;
    }

    public Map<String, Object> getEnv() {
        return env;
    }

    public void setEnv(Map<String, Object> env) {
        this.env = env;
    }
}
