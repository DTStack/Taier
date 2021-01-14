package com.dtstack.engine.alert;

import com.dtstack.engine.alert.client.AlterClient;
import com.dtstack.engine.alert.enums.AlertGateTypeEnum;
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
    public R sendSyncAlter(AlterContext alterContext, EventMonitor eventMonitor) {
        AlterClient alterClient = alterSender.computeIfAbsent(alterContext.getAlertGateCode().name(), a -> {
            return null;
        });

        return null;
    }

    @Override
    public void sendAsyncAAlter(AlterContext alterContext, EventMonitor eventMonitor) {
        AlterClient alterClient = alterSender.computeIfAbsent(alterContext.getAlertGateCode().name(), a -> {
            return null;
        });
    }
}
