package com.dtstack.engine.api.service;

import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ConsoleService extends DtInsightServer {

    @RequestLine("POST /node/console/nodeAddress")
    List<String> nodeAddress();

    @RequestLine("POST /node/console/searchJob")
    Map<String, Object> searchJob( String jobName);

    @RequestLine("POST /node/console/listNames")
    List<String> listNames( String jobName);

    @RequestLine("POST /node/console/jobResources")
    List<String> jobResources();

    /**
     * 根据计算引擎类型显示任务
     */
    @RequestLine("POST /node/console/overview")
    Collection<Map<String, Object>> overview( String nodeAddress,  String clusterName);

    @RequestLine("POST /node/console/groupDetail")
    Map<String, Object> groupDetail( String jobResource,
                                     String nodeAddress,
                                     Integer stage,
                                     Integer pageSize,
                                     Integer currentPage, String dtToken);

    @RequestLine("POST /node/console/jobStick")
    Boolean jobStick( String jobId);

    @RequestLine("POST /node/console/stopJob")
    void stopJob( String jobId) throws Exception;

    /**
     * 概览，杀死全部
     */
    @RequestLine("POST /node/console/stopAll")
    void stopAll( String jobResource,
                  String nodeAddress) throws Exception;

    @RequestLine("POST /node/console/stopJobList")
    void stopJobList( String jobResource,
                      String nodeAddress,
                      Integer stage,
                      List<String> jobIdList) throws Exception;

    @RequestLine("POST /node/console/clusterResources")
    Map<String, Object> clusterResources( String clusterName);


}
