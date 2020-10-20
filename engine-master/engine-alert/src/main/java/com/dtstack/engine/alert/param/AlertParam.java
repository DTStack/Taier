package com.dtstack.engine.alert.param;


import com.dtstack.engine.alert.enums.AGgateType;
import com.dtstack.engine.api.domain.po.AlertGatePO;

import java.util.Map;

/**
 * Date: 2020/5/19
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */

public class AlertParam {
    private AGgateType aGgateType;
    private AlertGatePO alertGatePO;
    private String source;
    private String message;
    private String alertTemplate;
    /**
     * 扩展配置
     */
    private Map<String, Object> extCfg;

    /**
     * <p>
     *     动态占位符参数
     * </p>
     */
    private Map<String, String> dynamicParams;

    public AGgateType getAGgateType() {
        return aGgateType;
    }

    public void setAGgateType(AGgateType aGgateType) {
        this.aGgateType = aGgateType;
    }

    public AlertGatePO getAlertGatePO() {
        return alertGatePO;
    }

    public void setAlertGatePO(AlertGatePO alertGatePO) {
        this.alertGatePO = alertGatePO;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAlertTemplate() {
        return alertTemplate;
    }

    public void setAlertTemplate(String alertTemplate) {
        this.alertTemplate = alertTemplate;
    }

    public Map<String, Object> getExtCfg() {
        return extCfg;
    }

    public void setExtCfg(Map<String, Object> extCfg) {
        this.extCfg = extCfg;
    }

    public Map<String, String> getDynamicParams() {
        return dynamicParams;
    }

    public void setDynamicParams(Map<String, String> dynamicParams) {
        this.dynamicParams = dynamicParams;
    }
}
