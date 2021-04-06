package com.dtstack.engine.master.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.AdapterEventMonitor;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/4/1 2:36 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class AlterEnvHandlerEvent extends AdapterEventMonitor {

    @Autowired
    private EnvironmentContext environmentContext;

    private AlterEnvironment alterEnvironment;

    @Override
    public Boolean startEvent(AlterContext alterContext) {
        Map<String, Object> evn = alterContext.getEvn();

        if (MapUtils.isEmpty(evn)) {
            evn = Maps.newHashMap();
        }

        evn.putAll(alterEnvironment.env);

        String alertGateJson = alterContext.getAlertGateJson();
        if (StringUtils.isNotBlank(alertGateJson)) {
            JSONObject jsonObject = JSON.parseObject(alertGateJson);
            evn.putAll(jsonObject);
        }

        alterContext.setEvn(evn);
        return super.startEvent(alterContext);
    }

    @PostConstruct
    public void init(){
        this.alterEnvironment = new AlterEnvironment();
    }

    class AlterEnvironment{
        private final static String SEND_IP = "CONSOLE_IP";

        private final Map<String,Object> env = Maps.newHashMap();

        AlterEnvironment(){
            env.put(SEND_IP,environmentContext.getHttpAddress());
        }

        public Map<String, Object> getEnv() {
            return env;
        }

    }
}
