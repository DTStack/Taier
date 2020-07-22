package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.pojo.ParamActionExt;

import java.util.List;
import java.util.Map;

public interface ActionService {
    /**
     * 接受来自客户端的请求, 并判断节点队列长度。
     * 如在当前节点,则直接处理任务
     */
    public Boolean start(ParamActionExt paramActionExt);

    /**
     * 只允许发到master节点上
     * 1: 在master等待队列中查找
     * 2: 在worker-exe等待队列里面查找
     * 3：在worker-status监听队列里面查找（可以直接在master节点上直接发送消息到对应的引擎）
     * @param params
     * @throws Exception
     */
    public Boolean stop(Map<String, Object> params) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    public Integer status( String jobId,  Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    public Map<String, Integer> statusByJobIds( List<String> jobIds,  Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job开始运行的时间
     * return 毫秒级时间戳
     */
    public Long startTime( String jobId, Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的日志
     */
    public String log( String jobId, Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    public String retryLog( String jobId, Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    public String retryLogDetail( String jobId, Integer computeType,  Integer retryNum) throws Exception;

    /**
     * 根据jobids 和 计算类型，查询job
     */
    public List<Map<String,Object>> entitys( List<String> jobIds, Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询container 信息
     */
    public List<String> containerInfos(ParamAction paramAction) throws Exception;


    /**
     * 重置任务状态为未提交
     * @return
     */
    public String resetTaskStatus( String jobId,  Integer computeType);

    /**
     * task 工程使用
     */
    public List<Map<String, Object>> listJobStatus( Long time);

    public List<Map<String, Object>> listJobStatusByJobIds( List<String> jobIds) throws Exception;


}