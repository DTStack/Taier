package com.dtstack.engine.alert.factory.sms;//package com.dtstack.console.alert.service.client.sms;
//
//import com.dtstack.console.alert.service.client.AlertService;
//import com.dtstack.console.alert.service.client.param.AlertParam;
//import com.dtstack.console.alert.service.client.param.SmsAlertParam;
//import com.alibaba.fastjson.JSON;
//import com.aliyuncs.DefaultAcsClient;
//import com.aliyuncs.IAcsClient;
//import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
//import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
//import com.aliyuncs.exceptions.ClientException;
//import com.aliyuncs.profile.DefaultProfile;
//import com.aliyuncs.profile.IClientProfile;
//import com.dtstack.console.alert.constant.AlertGateCode;
//import com.dtstack.lang.data.R;
//import com.dtstack.console.alert.service.client.conf.DYSmsConf;
//import com.google.common.collect.Lists;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.List;
//
///**
// * Date: 2020/6/11
// * Company: www.dtstack.com
// *
// * @author xiaochen
// */
//@Slf4j
//@Component
//public class SmsDyService implements AlertService {
//
//    @PostConstruct
//    public void init() {
//        /**
//         * -Dsun.net.client.defaultConnectTimeout=<value in milliseconds>
//         * The default value set by the protocol handlers is -1, which means that no timeout is set.
//         * When a connection is made by an applet to a server and the server does not respond properly, the applet might seem to hang. The delay might also cause the browser to hang. The apparent hang occurs because there is no network connection timeout. To avoid this problem, the Java™ Plug-in has added a default value to the network timeout of 2 minutes for all HTTP connections. You can override the default by setting this property.
//         *
//         * https://www.ibm.com/support/knowledgecenter/en/SSB23S_1.1.0.15/com.ibm.java.vm.80.doc/docs/dsunnetclientdefaultconnecttimout.html
//         */
//        System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
//        System.setProperty("sun.net.client.defaultReadTimeout", "30000");
//    }
//
//
//    @Override
//    public R send(AlertParam param) {
//
//        DYSmsConf smsConf = JSON.parseObject(param.getAlertGatePO().getAlertGateJson(), DYSmsConf.class);
//        IClientProfile profile = DefaultProfile.getProfile(smsConf.getRegionId(), smsConf.getAccessKeyId(), smsConf.getAccessKeySecret());
//
//        try {
//            DefaultProfile.addEndpoint(smsConf.getEndpoint(), smsConf.getRegionId(), smsConf.getProduct(), smsConf.getDomain());
//        } catch (Exception e) {
//            throw new RuntimeException("初始化阿里大鱼配置失败", e);
//        }
//        IAcsClient acsClient = new DefaultAcsClient(profile);
//
//        SmsAlertParam smsAlertParam = (SmsAlertParam) param;
//        List<String> phones = smsAlertParam.getPhones();
//        String message = smsAlertParam.getMessage();
//
//        List<String> info = Lists.newArrayList();
//        boolean success = false;
//        for (String phone : phones) {
//            try {
//                SendSmsRequest request = new SendSmsRequest();
//                request.setSignName(smsConf.getSignName());
//                request.setTemplateCode(smsConf.getTemplateCode());
//                request.setPhoneNumbers(phone);
//                request.setTemplateParam(message);
//                SendSmsResponse response = acsClient.getAcsResponse(request);
//                success = isSuccess(response.getCode());
//                String msg = success ? "success" : "fail";
//
//                info.add(String.format("%s-%s-%s", phone, msg, response.getMessage()));
//                log.info("[sendSms] end, phone={},message={}, code={}, retMsg={}", phone, message, response.getCode(), response.getMessage());
//            } catch (ClientException e) {
//                log.error("[sendSms] error, phone={}, message={}", phone, message, e);
//                info.add(String.format("%s-%s-%s", phone, "fail", e.getMessage()));
//            }
//        }
//        return success ? R.ok(info.toString()) : R.fail(info.toString());
//    }
//
//
//    private boolean isSuccess(String code) {
//        return "OK".equals(code);
//    }
//
//
//    @Override
//    public AlertGateCode alertGateCode() {
//        return AlertGateCode.AG_GATE_SMS_DY;
//    }
//}
