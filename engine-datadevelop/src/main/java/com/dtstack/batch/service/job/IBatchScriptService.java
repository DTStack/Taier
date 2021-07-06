package com.dtstack.batch.service.job;

import com.alibaba.fastjson.JSONObject;

/**
 * 脚本执行相关
 * Date: 2019/5/20
 * Company: www.dtstack.com
 * @author xuchao
 */
public interface IBatchScriptService {

    /***
     * 创建python,shell执行参数
     * @param scriptType
     * @param scriptName
     * @param tenantId
     * @param projectId
     * @param content
     * @param jobId
     * @param userId
     * @return
     */
    JSONObject buildPythonShellArgs(Integer scriptType, String scriptName, Long tenantId, Long projectId, String content, String jobId, Long userId);


    /**
     * 创建python,shell执行参数
     * @param taskType
     * @param exeArgs
     * @param name
     * @param content
     * @param taskParams
     * @param tenantId
     * @param projectId
     * @param jobId
     * @return
     */
    JSONObject buildPythonShellArgs(Integer taskType, String exeArgs, String name,
                                    String content, String taskParams, Long tenantId,
                                    Long projectId, String jobId);


    /**
     * 构建脚本运行信息,用于运行信息记录
     * @param scriptType
     * @param scriptName
     * @return
     */
    JSONObject buildSaveData(Integer scriptType, String scriptName);

    /***
     * 根据任务类型创建任务名称
     * @param scriptType
     * @return
     */
    String createTaskName(Integer scriptType);
}
