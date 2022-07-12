package com.dtstack.taier.develop.service.develop;

import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.domain.DevelopTaskParamShade;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.utils.develop.common.IDownload;

import java.util.List;
import java.util.Map;

public interface ITaskService {

    List<EScheduleJobType> support();

    /**
     * 运行任务
     *
     * @param userId
     * @param tenantId
     * @param taskId
     * @param sql
     * @param task
     * @return
     * @throws Exception
     */
    ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, Long taskId, String sql, Task task, String jobId) throws Exception;

    /**
     * 提交至调度
     * <p>
     * eg:
     * 任务sql中参数(包括系统参数和自定义参数)的替换
     *
     * @param actionParam
     * @param tenantId
     * @throws Exception
     */
    void readyForTaskStartTrigger(Map<String, Object> actionParam, Long tenantId, Task task, List<DevelopTaskParamShade> taskParamsToReplace) throws Exception;


    /**
     * 根据jobId 获取任务执行结果
     *
     * @param task
     * @param selectSql
     * @param tenantId
     * @param userId
     * @param isRoot
     * @param taskType
     * @return
     * @throws Exception
     */
    ExecuteResultVO selectData(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception;

    /**
     * 根据jobId 获取任务执行状态
     *
     * @param task
     * @param selectSql
     * @param tenantId
     * @param userId
     * @param isRoot
     * @param taskType
     * @return
     */
    ExecuteResultVO selectStatus(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType);


    /**
     * 运行日志查看
     *
     * @param jobId
     * @param taskType
     * @param tenantId
     * @param limitNum
     * @return
     */
    IDownload runLogShow(String jobId, Integer taskType, Long tenantId, Integer limitNum);

    /**
     * 完整日志下载
     *
     * @param tenantId
     * @param jobId
     * @param limitNum
     * @param logType
     * @return
     */
    IDownload logDownLoad(Long tenantId, String jobId, Integer limitNum, String logType);

    /**
     * 获取schema
     */
    List<String> getAllSchema(Long tenantId, Integer taskType);


    /**
     * 获取datasourcex对象信息
     *
     * @param tenantId
     * @param userId
     * @param taskType
     * @return
     */
    ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType);
}
