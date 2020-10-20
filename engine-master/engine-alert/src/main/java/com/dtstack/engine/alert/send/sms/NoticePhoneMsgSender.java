package com.dtstack.engine.alert.send.sms;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.domian.Notice;
import com.dtstack.engine.alert.send.AbstractSender;
import com.dtstack.engine.api.domain.po.ClusterAlertPO;
import org.springframework.stereotype.Component;

import java.util.Collections;


/**
 * 短信发送
 * Date: 2017/5/17
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
@Component
public class NoticePhoneMsgSender extends AbstractSender {

    @Override
    protected String buildBody(Notice notice, ClusterAlertPO clusterAlertPO) {
        Sms sms = buildPhoneMsg(notice);
        String smsBody = buildSmsBody(sms, clusterAlertPO.getAlertGateSource());
        return smsBody;
    }

    private Sms buildPhoneMsg(Notice notice) {
        Sms sms = new Sms();
        sms.setUsername(notice.getUserDTO().getUsername());
        sms.setRecNum(notice.getUserDTO().getTelephone());
        sms.setContent(notice.getContent());

        return sms;
    }

    private static String buildSmsBody(Sms sms, String source ) {
        JSONObject body = new JSONObject();
        body.put("phones", Collections.singletonList(sms.getRecNum()));
        JSONObject json = new JSONObject();
        json.put("user", sms.getUsername());
        json.put("content", sms.getContent());
        body.put("dynamicParams",json);
        body.put("source",source);
        return body.toJSONString();
    }
}
