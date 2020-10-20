package com.dtstack.engine.alert.send.phone;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.domian.Notice;
import com.dtstack.engine.alert.send.AbstractSender;
import com.dtstack.engine.api.domain.po.ClusterAlertPO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TencentCloudPhoneSender extends AbstractSender {

    private Phone buildPhone(Notice notice) {
        Phone phone = new Phone();
        phone.setContent(notice.getContent());
        List<String> list = new ArrayList<>();
        String p = notice.getUserDTO().getTelephone();
        if (StringUtils.isNotEmpty(p)) {
            list.add(p);
        }
        phone.setPhone(list);
        return phone;
    }

    private static String buildPhoneBody(Phone phone, String source) {
        JSONObject body = new JSONObject();
        body.put("phones", phone.getPhone());
        body.put("template", phone.getTemplateId());
        body.put("message", phone.getContent());
        body.put("dynamicParams", null);
        body.put("source", source);
        return body.toJSONString();
    }

    @Override
    protected String buildBody(Notice notice, ClusterAlertPO clusterAlertPO) {
        Phone phone = buildPhone(notice);
        String body = buildPhoneBody(phone, clusterAlertPO.getAlertGateSource());
        return body;
    }
}
