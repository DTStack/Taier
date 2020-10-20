package com.dtstack.engine.alert.client;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.alert.domian.AlertEvent;
import com.dtstack.engine.alert.enums.AGgateType;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.exception.AlertGateException;
import com.dtstack.engine.alert.factory.AlertService;
import com.dtstack.engine.alert.factory.AlertServiceFactory;
import com.dtstack.engine.alert.param.AlertParam;
import com.dtstack.engine.alert.param.DingAlertParam;
import com.dtstack.engine.alert.param.MailAlertParam;
import com.dtstack.engine.alert.param.SmsAlertParam;
import com.dtstack.engine.alert.serivce.AlertGateService;
import com.dtstack.engine.api.domain.po.AlertGatePO;
import com.dtstack.engine.api.domain.po.AlertTemplatePO;
import com.dtstack.engine.common.util.RenderUtil;
import com.dtstack.engine.dao.AlertTemplateDao;
import com.dtstack.lang.base.Strings;
import com.dtstack.lang.data.R;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @Auther: dazhi
 * @Date: 2020/10/12 9:59 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class AlertServiceProvider {

    private final Logger log = LoggerFactory.getLogger(AlertServiceProvider.class);

    @Autowired
    private AlertGateService alertGateService;

    @Autowired
    private AlertTemplateDao alertTemplateDao;

    @Autowired
    private AlertServiceFactory alertServiceFactory;

    public AlertParam convertToAlertParam(AlertEvent alertEvent, AGgateType aGgateType) {
        alertEvent.setaGgateType(aGgateType);
        AlertParam alertParam = createAlertParam(aGgateType);
        BeanUtils.copyProperties(alertEvent, alertParam);
        return alertParam;
    }

    public static AlertParam createAlertParam(AGgateType aGgateType) {
        switch (aGgateType) {
            case AG_GATE_TYPE_SMS:
            case AG_GATE_TYPE_PHONE:
                return new SmsAlertParam();
            case AG_GATE_TYPE_DING:
                return new DingAlertParam();
            case AG_GATE_TYPE_MAIL:
                return new MailAlertParam();
            default:
                throw new RuntimeException("un support AGgateType");
        }
    }

    public R send(AlertEvent alertEvent, AGgateType aGgateType) {
        AlertParam alertParam = convertToAlertParam(alertEvent, aGgateType);
        return send(alertParam);
    }

    public R send(AlertParam sendParam) {
        try {
            AlertGatePO alertGatePO = sendParam.getAlertGatePO();
            if (alertGatePO == null) {
                alertGatePO = alertGateService.locateAlertGatePO(sendParam.getAGgateType(), sendParam.getSource());
                sendParam.setAlertGatePO(alertGatePO);
            }

            if (sendParam.getDynamicParams() == null) {
                sendParam.setDynamicParams(new HashMap<>());
            }
            if (sendParam.getExtCfg() == null) {
                sendParam.setExtCfg(new HashMap<>());
            }

            String message = handleAlertMessage(sendParam);
            sendParam.setMessage(message);
            AlertService alertService = alertServiceFactory.getAlertService(alertGatePO.getAlertGateCode());
            log.info("sendParam :{}", JSON.toJSONString(sendParam));
            return alertService.send(sendParam);
        } catch (Exception e) {
            log.error("[send alert ] error,{}", JSON.toJSONString(sendParam), e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * <p>
     * 处理告警内容:
     * <p>
     * 如果{alertEvent#message}为空，则根据告警模板获取告警内容，并使用{alertEvent#dynamicParams}动态组装告警内容，
     * 否则直接获取{alertEvent#message}做为告警内容
     * </p>
     * @return
     */
    private String handleAlertMessage(AlertParam sendParam) throws AlertGateException {
        if (Strings.isNotBlank(sendParam.getMessage())) {
            return sendParam.getMessage();
        }
        String alertGateCode = sendParam.getAlertGatePO().getAlertGateCode();

        if (AlertGateCode.AG_GATE_MAIL_DT.code().equals(alertGateCode)) {
            if (StringUtils.isBlank(sendParam.getAlertTemplate())) {
                AlertTemplatePO alertTemplatePO = alertTemplateDao.getByTemplateTypeAndSource(sendParam.getAGgateType().type(), sendParam.getSource());
                if (alertTemplatePO == null) {
                    throw new AlertGateException("没有配置告警模板");
                }
                sendParam.setAlertTemplate(alertTemplatePO.getAlertTemplate());
            }
            return RenderUtil.renderTemplate(sendParam.getAlertTemplate(), sendParam.getDynamicParams());
        }
        String msg = "";
        if (AlertGateCode.AG_GATE_MAIL_JAR.code().equals(alertGateCode)) {
            msg = sendParam.getDynamicParams().get("message");
        }
        return msg;
    }



}
