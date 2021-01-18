package com.dtstack.engine.alert.client.mail;

import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.client.AbstractAlterClient;
import com.dtstack.lang.data.R;

/**
 * @Auther: dazhi
 * @Date: 2021/1/18 11:44 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractMailAlterClient extends AbstractAlterClient {


    @Override
    protected R send(AlterContext alterContext) {
        AlterSendMailBean alterSendMailBean = buildAlterSendMailBean(alterContext);
        return sendMail(alterSendMailBean);
    }

    public AlterSendMailBean buildAlterSendMailBean(AlterContext alterContext) {


        return null;
    }

    /**
     * 发送邮件
     *
     * @param alterSendMailBean
     * @return
     */
    protected abstract R sendMail(AlterSendMailBean alterSendMailBean);


}
