package com.dtstack.engine.alert;

import com.dtstack.engine.alert.client.AlterClient;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.enums.AlertGateTypeEnum;
import com.dtstack.engine.alert.exception.AlterException;
import com.dtstack.lang.data.R;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: dazhi
 * @Date: 2021/1/11 4:02 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class DefaultAlterSender implements AlterSender {

    private final Map<String, AlterClient> alterSender;
    private AlterConfig alterConfig;

    public DefaultAlterSender(AlterConfig alterConfig){
        if (alterConfig == null) {
            // 使用默认的配置
            alterConfig = new AlterConfig();
        }
        alterSender = new ConcurrentHashMap<>(AlertGateTypeEnum.values().length);
    }

    @Override
    public R sendSyncAlter(AlterContext alterContext, EventMonitor eventMonitor) throws Exception {
        AlterClient alterClient = getAlterClient(alterContext);
        return alterClient.sendSyncAlter(alterContext,eventMonitor);
    }

    @Override
    public void sendAsyncAAlter(AlterContext alterContext, EventMonitor eventMonitor) throws Exception {
        AlterClient alterClient = getAlterClient(alterContext);
        alterClient.sendAsyncAAlter(alterContext,eventMonitor);
    }

    private AlterClient getAlterClient(AlterContext alterContext) throws Exception {
        AlertGateCode alertGateCode = alterContext.getAlertGateCode();

        if (alertGateCode == null) {
            throw new AlterException("上下文对象必须有告警类型");
        }

        AlterClient alterClient = alterSender.computeIfAbsent(alertGateCode.name(), a -> getClient(alertGateCode, alterConfig));

        if (alterClient == null) {
            throw new AlterException(alertGateCode.name() + "类型通道不存在，请联系技术人员");
        }
        return alterClient;
    }

    private AlterClient getClient(AlertGateCode alertGateCode, AlterConfig alterConfig) {

        return null;
    }
}
