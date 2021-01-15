package com.dtstack.engine.alert.client;

import com.dtstack.engine.alert.AlterConfig;
import com.dtstack.engine.alert.enums.AlertGateCode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: dazhi
 * @Date: 2021/1/15 2:40 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlterClientFactory {

    private Map<String,Class<? extends AlterClient>> clients;

    {
        clients = new ConcurrentHashMap<>();

        
    }


    public AlterClient getClient(AlertGateCode alertGateCode, AlterConfig alterConfig) {
        try {
            if (alertGateCode == null) {
                throw new RuntimeException("参数错误!");
            }
            Class<? extends AlterClient> aClass = clients.get(alertGateCode.name());
            AlterClient alterClient = aClass.newInstance();
            alterClient.setConfig(alterConfig);

            return alterClient;
        } catch (Exception e) {
            return null;
        }
    }
}
