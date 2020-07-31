package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;
import com.dtstack.engine.api.vo.action.ActionJobStatusVO;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.api.vo.action.ActionRetryLogVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.*;

import java.util.List;
import java.util.Map;

public interface ActionService extends DtInsightServer {
    /**
     * 接受来自客户端的请求, 并判断节点队列长度。
     * 如在当前节点,则直接处理任务
     */
    @RequestLine("POST /node/action/start")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Boolean> start(ParamActionExt paramActionExt);

    /**
     * 只允许发到master节点上
     * 1: 在master等待队列中查找
     * 2: 在worker-exe等待队列里面查找
     * 3：在worker-status监听队列里面查找（可以直接在master节点上直接发送消息到对应的引擎）
     * @param jobIds 任务id
     * @throws Exception
     */
    @RequestLine("POST /node/action/stop")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Boolean> stop(@Param("jobIds") List<String> jobIds) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    @RequestLine("POST /node/action/status")
    ApiResponse<Integer> status(@Param("jobId") String jobId, @Param("computeType") Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    @RequestLine("POST /node/action/statusByJobIds")
    ApiResponse<Map<String, Integer>> statusByJobIds(@Param("jobIds") List<String> jobIds, @Param("computeType") Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job开始运行的时间
     * return 毫秒级时间戳
     */
    @RequestLine("POST /node/action/startTime")
    ApiResponse<Long> startTime(@Param("jobId") String jobId, @Param("computeType") Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的日志
     */
    @RequestLine("POST /node/action/log")
    ApiResponse<ActionLogVO> log(@Param("jobId") String jobId, @Param("computeType") Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    @RequestLine("POST /node/action/retryLog")
    ApiResponse<List<ActionRetryLogVO>> retryLog(@Param("jobId") String jobId, @Param("computeType") Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    @RequestLine("POST /node/action/retryLogDetail")
    ApiResponse<ActionRetryLogVO> retryLogDetail(@Param("jobId") String jobId, @Param("computeType") Integer computeType, @Param("retryNum") Integer retryNum) throws Exception;

    /**
     * 根据jobids 和 计算类型，查询job
     */
    @RequestLine("POST /node/action/entitys")
    ApiResponse<List<ActionJobEntityVO>> entitys(@Param("jobIds") List<String> jobIds, @Param("computeType") Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询container 信息
     */
    @RequestLine("POST /node/action/containerInfos")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<List<String>> containerInfos(ParamAction paramAction) throws Exception;


    /**
     * 重置任务状态为未提交
     * @return
     */
    @RequestLine("POST /node/action/resetTaskStatus")
    ApiResponse<String> resetTaskStatus(@Param("jobId") String jobId, @Param("computeType") Integer computeType);

    /**
     * task 工程使用
     */
    @RequestLine("POST /node/action/listJobStatus")
    ApiResponse<List<ActionJobStatusVO>> listJobStatus(@Param("time") Long time);

    @RequestLine("POST /node/action/listJobStatusByJobIds")
    ApiResponse<List<ActionJobStatusVO>> listJobStatusByJobIds(@Param("jobIds") List<String> jobIds) throws Exception;

    @RequestLine("POST /node/action/generateUniqueSign")
    ApiResponse<String> generateUniqueSign();
}