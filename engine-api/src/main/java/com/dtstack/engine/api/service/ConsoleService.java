package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Forbidden;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ConsoleService {
    @Forbidden
    public Boolean finishJob(String jobId, Integer status);

    public List<String> nodeAddress();

    public Map<String, Object> searchJob(@Param("jobName") String jobName);

    public List<String> listNames(@Param("jobName") String jobName);

    public List<String> jobResources();

    /**
     * 根据计算引擎类型显示任务
     */
    public Collection<Map<String, Object>> overview(@Param("nodeAddress") String nodeAddress, @Param("clusterName") String clusterName);

    public Map<String, Object> groupDetail(@Param("jobResource") String jobResource,
                                           @Param("nodeAddress") String nodeAddress,
                                           @Param("stage") Integer stage,
                                           @Param("pageSize") Integer pageSize,
                                           @Param("currentPage") Integer currentPage,@Param("dtToken") String dtToken);

    public Boolean jobStick(@Param("jobId") String jobId);

    public void stopJob(@Param("jobId") String jobId) throws Exception;

    /**
     * 概览，杀死全部
     */
    public void stopAll(@Param("jobResource") String jobResource,
                        @Param("nodeAddress") String nodeAddress) throws Exception;

    public void stopJobList(@Param("jobResource") String jobResource,
                            @Param("nodeAddress") String nodeAddress,
                            @Param("stage") Integer stage,
                            @Param("jobIdList") List<String> jobIdList) throws Exception;

    public Map<String, Object> clusterResources(@Param("clusterName") String clusterName);

    @Forbidden
    public Map<String, Object> getResources(Component yarnComponent, Cluster cluster);


}
