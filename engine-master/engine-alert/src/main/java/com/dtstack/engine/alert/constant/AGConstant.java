package com.dtstack.engine.alert.constant;

/**
 * <p>
 * 告警通道常量
 * </p>
 *
 * @author 青涯
 */
public interface AGConstant {

    /**
     * <p>
     * 告警通道可用
     * </p>
     */
    Integer AG_STATUS_ENABLE = 1;

    /**
     * <p>
     * 告警通道禁用
     * </p>
     */
    Integer AG_STATUS_DISABLE = 2;

    String DEFAULT_SOURCE = "dtlog";

    // 邮件通道
    String MAIL_HOST = "mail.smtp.host";

    String MAIL_PORT = "mail.smtp.port";

    String MAIL_SSL = "mail.smtp.ssl.enable";

    String MAIL_USERNAME = "mail.smtp.username";

    String MAIL_PASSWORD = "mail.smtp.password";

    String MAIL_FROM = "mail.smtp.from";
}
