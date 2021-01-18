package com.dtstack.engine.alert;


import com.dtstack.engine.alert.enums.AlertGateCode;

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
     * 类型
     */
    private AlertGateCode alertGateCode;

    /**
     * 事件响应
     */
    private EventMonitor eventMonitor;


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

    public String getMark(){
        return "";
    }
}
