package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.pojo.ParamTaskAction;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;
import com.dtstack.engine.api.vo.action.ActionJobStatusVO;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.api.vo.action.ActionRetryLogVO;

import java.util.List;
import java.util.Map;

public interface ActionService {
    /**
     * 接受来自客户端的请求, 并判断节点队列长度。
     * 如在当前节点,则直接处理任务
     */
    Boolean start(ParamActionExt paramActionExt);

    /**
     * 如在当前节点,则直接处理任务(包括预处理)
     *
     * @param paramTaskAction
     * @return
     */
    Boolean startJob(ParamTaskAction paramTaskAction) throws Exception;

    /**
     * 执行前预处理，逻辑
     *
     * @param paramActionExt
     * @return
     */
    ParamActionExt paramActionExt(ParamTaskAction paramActionExt) throws Exception;


    /**
     * @param jobIds 任务id
     * @
     */
    Boolean stop(List<String> jobIds) throws Exception;

    /**
     * @param jobIds  任务id
     * @param isForce 是否强制
     * @return
     */
    Boolean stop(List<String> jobIds, Integer isForce) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    Integer status(String jobId) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    Map<String, Integer> statusByJobIds(List<String> jobIds) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job开始运行的时间
     * return 毫秒级时间戳
     */
    Long startTime(String jobId) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的日志
     */
    ActionLogVO log(String jobId, Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询k8s调度下job的日志
     */
    String logFromEs(String jobId) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    List<ActionRetryLogVO> retryLog(String jobId) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    ActionRetryLogVO retryLogDetail(String jobId, Integer retryNum) throws Exception;

    /**
     * 根据jobids 和 计算类型，查询job
     */
    List<ActionJobEntityVO> entitys(List<String> jobIds) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询container 信息
     */
    List<String> containerInfos(ParamAction paramAction) throws Exception;


    /**
     * 重置任务状态为未提交
     *
     * @return
     */
    String resetTaskStatus(String jobId);

    /**
     * task 工程使用
     */
    List<ActionJobStatusVO> listJobStatus(Long time,Integer appType);

    List<ScheduleJob> listJobStatusScheduleJob(Long time,Integer appType);

    List<ActionJobStatusVO> listJobStatusByJobIds(List<String> jobIds) throws Exception;

    String generateUniqueSign();
}