package com.dtstack.engine.common.restart;

import com.dtstack.engine.common.IClient;

/**
 * 各个插件对失败作业的重启策略
 * Date: 2018/1/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public abstract class ARestartService {

    public IJobRestartStrategy getAndParseErrorLog(String jobId, String engineJobId, String appId, IClient client) {
        return null;
    }

}
