package com.dtstack.engine.alert.factory.conf;

import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *     告警通道配置
 * </p>
 * @author 青涯
 */
@Configuration
public class AlertGateConf {

    /**
     * <p>
     *    告警通道以rest提供服务的rest地址
     * </p>
     */
    public String alertGateUrl;

    public String getAlertGateUrl() {
        return alertGateUrl;
    }

    public void setAlertGateUrl(String alertGateUrl) {
        this.alertGateUrl = alertGateUrl;
    }
}
