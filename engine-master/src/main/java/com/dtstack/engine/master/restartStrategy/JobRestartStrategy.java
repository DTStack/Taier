package com.dtstack.engine.master.restartStrategy;

/**
 *
 * 根据日志获取重启策略，重启策略就是调整任务绑定的执行参数
 *
 * @description:
 * @author: maqi
 * @create: 2019/07/16 19:50
 */
public interface JobRestartStrategy {


    String setRestartInfo(String taskParam, int retryNum, String lastRetryParams);

}