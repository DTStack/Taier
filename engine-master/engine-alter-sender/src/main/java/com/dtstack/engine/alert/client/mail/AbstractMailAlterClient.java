package com.dtstack.engine.alert.client.mail;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.client.AbstractAlterClient;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.exception.AlterException;
import com.dtstack.lang.data.R;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/1/18 11:44 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractMailAlterClient extends AbstractAlterClient {


    @Override
    protected R send(AlterContext alterContext) throws AlterException {
        AlterSendMailBean alterSendMailBean = buildAlterSendMailBean(alterContext);
        return sendMail(alterSendMailBean);
    }

    public AlterSendMailBean buildAlterSendMailBean(AlterContext alterContext) throws AlterException {
        List<String> emails = alterContext.getEmails();
        if (CollectionUtils.isEmpty(emails)) {
            throw new AlterException("发送人数必须大于或者等于1");
        }

        String title = alterContext.getTitle();
        if (StringUtils.isBlank(title)) {
            throw new AlterException("发送邮件必须有发送标题");
        }

        String content = alterContext.getContent();
        if (StringUtils.isBlank(content)) {
            throw new AlterException("发送邮件必须有发送内容");
        }

        AlterSendMailBean alterSendMailBean = new AlterSendMailBean();
        alterSendMailBean.setContent(content);
        alterSendMailBean.setSubject(title);
        alterSendMailBean.setEmails(emails);
        alterSendMailBean.setAttachFiles(alterContext.getAttachFiles());

        // 检查其他配置
        String alertGateJson = alterContext.getAlertGateJson();
        JSONObject jsonObject = JSONObject.parseObject(alertGateJson);

        if (jsonObject == null) {
            throw new AlterException("发送邮件时，必须要传入相关配置，例如className等");
        }
        alterSendMailBean.setAlertGateJson(alertGateJson);
        buildBean(alterSendMailBean, jsonObject,alterContext);
        return alterSendMailBean;
    }



    protected static class ConstMailAlter {
        // 邮件通道
        public static String MAIL_HOST = "mail.smtp.host";

        public static String MAIL_PORT = "mail.smtp.port";

        public static String MAIL_SSL = "mail.smtp.ssl.enable";

        public static String MAIL_USERNAME = "mail.smtp.username";

        public static String MAIL_PASSWORD = "mail.smtp.password";

        public static String MAIL_FROM = "mail.smtp.from";

        public static String MAIL_CLASS = "className";

        public static String PATH_CUT = "&sftp:";

    }

    /**
     * 发送邮件
     *
     * @param alterSendMailBean
     * @return
     */
    protected abstract R sendMail(AlterSendMailBean alterSendMailBean);

    /**
     * 构建bean
     *
     * @param alterSendMailBean
     * @param jsonObject
     * @throws AlterException
     */
    public abstract void buildBean(AlterSendMailBean alterSendMailBean, JSONObject jsonObject,AlterContext alterContext) throws AlterException;

}
