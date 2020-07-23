package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;
import java.util.Map;

public interface ActionService extends DtInsightServer {
    /**
     * 接受来自客户端的请求, 并判断节点队列长度。
     * 如在当前节点,则直接处理任务
     */
    @RequestLine("POST /node/action/start")
    Boolean start(ParamActionExt paramActionExt);

    /**
     * 只允许发到master节点上
     * 1: 在master等待队列中查找
     * 2: 在worker-exe等待队列里面查找
     * 3：在worker-status监听队列里面查找（可以直接在master节点上直接发送消息到对应的引擎）
     * @param params
     * @throws Exception
     */
    @RequestLine("POST /node/action/stop")
    Boolean stop(Map<String, Object> params) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    @RequestLine("POST /node/action/status")
    Integer status( String jobId,  Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    @RequestLine("POST /node/action/statusByJobIds")
    Map<String, Integer> statusByJobIds( List<String> jobIds,  Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job开始运行的时间
     * return 毫秒级时间戳
     */
    @RequestLine("POST /node/action/startTime")
    Long startTime( String jobId, Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的日志
     */
    @RequestLine("POST /node/action/log")
    String log( String jobId, Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    @RequestLine("POST /node/action/retryLog")
    String retryLog( String jobId, Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    @RequestLine("POST /node/action/retryLogDetail")
    String retryLogDetail( String jobId, Integer computeType,  Integer retryNum) throws Exception;

    /**
     * 根据jobids 和 计算类型，查询job
     */
    @RequestLine("POST /node/action/entitys")
    List<Map<String,Object>> entitys( List<String> jobIds, Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询container 信息
     */
    @RequestLine("POST /node/action/containerInfos")
    List<String> containerInfos(ParamAction paramAction) throws Exception;


    /**
     * 重置任务状态为未提交
     * @return
     */
    @RequestLine("POST /node/action/resetTaskStatus")
    String resetTaskStatus( String jobId,  Integer computeType);

    /**
     * task 工程使用
     */
    @RequestLine("POST /node/action/listJobStatus")
    List<Map<String, Object>> listJobStatus( Long time);

    @RequestLine("POST /node/action/listJobStatusByJobIds")
    List<Map<String, Object>> listJobStatusByJobIds( List<String> jobIds) throws Exception;

    @RequestLine("POST /node/action/generateUniqueSign")
    String generateUniqueSign();
}