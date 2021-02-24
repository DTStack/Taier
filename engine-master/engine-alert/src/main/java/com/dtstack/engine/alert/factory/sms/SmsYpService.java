package com.dtstack.engine.alert.factory.sms;//package com.dtstack.console.alert.service.client.sms;
//
//import com.dtstack.console.alert.service.client.AlertService;
//import com.dtstack.console.alert.service.client.param.AlertParam;
//import com.dtstack.console.alert.service.client.param.SmsAlertParam;
//import com.alibaba.fastjson.JSON;
//import com.dtstack.console.alert.constant.AlertGateCode;
//import com.dtstack.lang.data.R;
//import com.dtstack.console.alert.util.GateJsonUtils;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.javalite.http.Http;
//import org.javalite.http.Post;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.StringJoiner;
//
///**
// * Date: 2020/5/22
// * Company: www.dtstack.com
// * 云片短信服务
// * @author xiaochen
// */
//@Slf4j
//@Service
//public class SmsYpService implements AlertService {
//    private static final String YP_URI = "https://sms.yunpian.com/v2/sms/batch_send.json";
//
//    @Override
//    public R send(AlertParam param) {
//        SmsAlertParam smsAlertParam = (SmsAlertParam) param;
//        StringJoiner mobile = new StringJoiner(",");
//        smsAlertParam.getPhones().forEach(mobile::add);
//        String ypApiKey = GateJsonUtils.getYpApiKey(smsAlertParam.getAlertGatePO().getAlertGateJson());
//        boolean success = batchSend(ypApiKey, mobile.toString(), smsAlertParam.getMessage());
//        return success ? R.ok() : R.fail();
//    }
//
//    @Override
//    public AlertGateCode alertGateCode() {
//        return AlertGateCode.AG_GATE_SMS_YP;
//    }
//
//    public boolean batchSend(String apiKey, String mobile, String text) {
//        Post post = Http.post(YP_URI)
//                .header("Accept", "application/json;charset=utf-8;")
//                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8;")
//                .param("apikey", apiKey)
//                .param("mobile", mobile)
//                .param("text", text);
//        String jsonResult = post.text();
//        log.info("batchSend result  :{}", jsonResult);
//        SmsBatchSend smsBatchSend = JSON.parseObject(jsonResult, SmsBatchSend.class);
//        List<SmsSingleSend> dataList = smsBatchSend.getData();
//        //有一条成功 即视为成功
//        for (SmsSingleSend smsSingleSend : dataList) {
//            if (smsSingleSend.getCode() == 0) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//    @Data
//    public static class SmsBatchSend {
//        private Integer total_count;
//        private Double total_fee;
//        private String unit;
//        private List<SmsSingleSend> data;
//    }
//
//    @Data
//    public static class SmsSingleSend {
//        private Integer code;
//        private String msg;
//        private Integer count;
//        private Double fee;
//        private String unit;
//        private String mobile;
//        private Long sid;
//    }
//}
