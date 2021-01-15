package com.dtstack.engine.alert.client;

import com.dtstack.engine.alert.AlterConfig;
import com.dtstack.engine.alert.AlterSender;

/**
 * @Auther: dazhi
 * @Date: 2021/1/14 4:13 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface AlterClient extends AlterSender {

    /**
     * 设置配置
     *
     * @param config
     */
    void setConfig(AlterConfig config);

}
