package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Param;

import java.util.List;
import java.util.Map;

public interface ActionService {
    /**
     * 接受来自客户端的请求, 并判断节点队列长度。
     * 如在当前节点,则直接处理任务
     */
    public Boolean start(Map<String, Object> params);

    /**
     * 只允许发到master节点上
     * 1: 在master等待队列中查找
     * 2: 在worker-exe等待队列里面查找
     * 3：在worker-status监听队列里面查找（可以直接在master节点上直接发送消息到对应的引擎）
     * @param params
     * @throws Exception
     */
    public void stop(Map<String, Object> params) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    public Integer status(@Param("jobId") String jobId, @Param("computeType") Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    public Map<String, Integer> statusByJobIds(@Param("jobIds") List<String> jobIds, @Param("computeType") Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job开始运行的时间
     * return 毫秒级时间戳
     */
    public Long startTime(@Param("jobId") String jobId,@Param("computeType") Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的日志
     */
    public String log(@Param("jobId") String jobId,@Param("computeType") Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    public String retryLog(@Param("jobId") String jobId,@Param("computeType") Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    public String retryLogDetail(@Param("jobId") String jobId,@Param("computeType") Integer computeType, @Param("retryNum") Integer retryNum) throws Exception;

    /**
     * 根据jobids 和 计算类型，查询job
     */
    public List<Map<String,Object>> entitys(@Param("jobIds") List<String> jobIds,@Param("computeType") Integer computeType) throws Exception;

    /**
     * 根据jobid 和 计算类型，查询container 信息
     */
    public List<String> containerInfos(Map<String, Object> param) throws Exception;

    public String generateUniqueSign();

    /**
     * 重置任务状态为未提交
     * @return
     */
    public String resetTaskStatus(@Param("jobId") String jobId, @Param("computeType") Integer computeType);

    /**
     * task 工程使用
     */
    public List<Map<String, Object>> listJobStatus(@Param("time") Long time);

    public List<Map<String, Object>> listJobStatusByJobIds(@Param("jobIds") List<String> jobIds) throws Exception;


}
