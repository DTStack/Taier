package com.dtstack.engine.alert.client.ding;

import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.client.AbstractAlterClient;
import com.dtstack.engine.alert.exception.AlterException;
import com.dtstack.lang.data.R;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: dazhi
 * @Date: 2021/1/18 4:50 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractDingAlterClient extends AbstractAlterClient {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected R send(AlterContext alterContext) throws AlterException {
        AlterSendDingBean alterSendMailBean = buildAlterSendDingBean(alterContext);
        return sendDing(alterSendMailBean);
    }

    public AlterSendDingBean buildAlterSendDingBean(AlterContext alterContext) throws AlterException {
        String content = alterContext.getContent();
        if (StringUtils.isBlank(content)) {
            throw new AlterException("发送钉钉必须有发送内容");
        }

        String title = alterContext.getTitle();
        if (StringUtils.isBlank(title)) {
            title = ConstDingAlter.DING_DEFAULT_TITLE;
        }

        AlterSendDingBean alterSendDingBean = new AlterSendDingBean();
        alterSendDingBean.setContent(content);
        alterSendDingBean.setTitle(title);
        buildBean(alterContext,alterSendDingBean);
        return alterSendDingBean;
    }


    protected static class ConstDingAlter {
        // 钉钉类型
        public static String DING_MSG_TYPE_KEY = "msgtype";

        public static String DING_DEFAULT_TITLE = "您收到一条钉钉消息";

        public static String DING_CLASS = "className";
    }

    /**
     * 构建bean
     *
     * @param alterContext
     * @param alterSendDingBean
     */
    protected abstract void buildBean(AlterContext alterContext, AlterSendDingBean alterSendDingBean) throws AlterException;

    /**
     * 发送钉钉
     *
     * @param alterSendDingBean
     * @return
     */
    protected abstract R sendDing(AlterSendDingBean alterSendDingBean);


}
