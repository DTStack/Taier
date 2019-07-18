package com.dtstack.rdos.engine.execution.base.restart;

import com.dtstack.rdos.engine.execution.base.IClient;

/**
 * @description:    解析日志信息返回相关的重启策略
 * @author: maqi
 * @create: 2019/07/17 16:16
 */
public interface IRestartService {
    // 根据配置拿到任务日志，解析并返回重启策略
    IExtractStrategy parseErrorLog(String engineJobId, IClient client);

    //  未达到重试次数
    public boolean checkCanRestart(String jobId, String engineJobId, IClient client,
                                   int alreadyRetryNum, int maxRetryNum);

}