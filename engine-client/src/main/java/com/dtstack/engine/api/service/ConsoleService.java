package com.dtstack.engine.api.service;

import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ConsoleService extends DtInsightServer {

    @RequestLine("POST /node/console/nodeAddress")
    ApiResponse<List<String>> nodeAddress();

    @RequestLine("POST /node/console/searchJob")
    ApiResponse<Map<String, Object>> searchJob(@Param("jobName") String jobName);

    @RequestLine("POST /node/console/listNames")
    ApiResponse<List<String>> listNames(@Param("jobName") String jobName);

    @RequestLine("POST /node/console/jobResources")
    ApiResponse<List<String>> jobResources();

    /**
     * 根据计算引擎类型显示任务
     */
    @RequestLine("POST /node/console/overview")
    ApiResponse<Collection<Map<String, Object>>> overview(@Param("nodeAddress") String nodeAddress, @Param("clusterName") String clusterName);

    @RequestLine("POST /node/console/groupDetail")
    ApiResponse<Map<String, Object>> groupDetail(@Param("jobResource") String jobResource,
                                                 @Param("nodeAddress") String nodeAddress,
                                                 @Param("stage") Integer stage,
                                                 @Param("pageSize") Integer pageSize,
                                                 @Param("currentPage") Integer currentPage, @Param("dtToken") String dtToken);

    @RequestLine("POST /node/console/jobStick")
    ApiResponse<Boolean> jobStick(@Param("jobId") String jobId);

    @RequestLine("POST /node/console/stopJob")
    ApiResponse stopJob(@Param("jobId") String jobId) throws Exception;

    /**
     * 概览，杀死全部
     */
    @RequestLine("POST /node/console/stopAll")
    ApiResponse stopAll(@Param("jobResource") String jobResource,
                        @Param("nodeAddress") String nodeAddress) throws Exception;

    @RequestLine("POST /node/console/stopJobList")
    ApiResponse stopJobList(@Param("jobResource") String jobResource,
                            @Param("nodeAddress") String nodeAddress,
                            @Param("stage") Integer stage,
                            @Param("jobIdList") List<String> jobIdList) throws Exception;

    @RequestLine("POST /node/console/clusterResources")
    ApiResponse<Map<String, Object>> clusterResources(@Param("clusterName") String clusterName);


}
