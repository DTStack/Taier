package com.dtstack.engine.alert.client.sms;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.client.AbstractAlterClient;
import com.dtstack.engine.alert.client.mail.AlterSendMailBean;
import com.dtstack.engine.alert.exception.AlterException;
import com.dtstack.lang.data.R;
import org.apache.commons.lang3.StringUtils;

/**
 * @Auther: dazhi
 * @Date: 2021/1/18 11:44 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractSmsAlterClient extends AbstractAlterClient {


    @Override
    protected R send(AlterContext alterContext) throws AlterException {
        AlterSendSmsBean alterSendMailBean = buildSmsBean(alterContext);
        return sendSms(alterSendMailBean);
    }

    private AlterSendSmsBean buildSmsBean(AlterContext alterContext) throws AlterException {
        String content = alterContext.getContent();
        if (StringUtils.isBlank(content)) {
            throw new AlterException("发送短信必须有发送内容");
        }

        String phone = alterContext.getPhone();
        if (StringUtils.isBlank(phone)) {
            throw new AlterException("发送短信必须有手机号");
        }

        AlterSendSmsBean alterSendSmsBean = new AlterSendSmsBean();
        alterSendSmsBean.setTitle(alterContext.getTitle());
        alterSendSmsBean.setMessage(content);
        alterSendSmsBean.setPhone(phone);
        alterSendSmsBean.setAlertGateJson(alterContext.getAlertGateJson());
        buildBean(alterSendSmsBean,alterContext);

        return alterSendSmsBean;
    }

    protected static class ConstSmsAlter {
        public static String SMS_CLASS = "className";

        public static String PATH_CUT = "&sftp:";
    }

    /**
     * 发送短信
     *
     * @param alterSendSmsBean
     * @return
     */
    protected abstract R sendSms(AlterSendSmsBean alterSendSmsBean);

    /**
     * 构建bean
     *
     * @param alterSendSmsBean
     * @throws AlterException
     */
    public abstract void buildBean(AlterSendSmsBean alterSendSmsBean, AlterContext alterContext) throws AlterException;

}
