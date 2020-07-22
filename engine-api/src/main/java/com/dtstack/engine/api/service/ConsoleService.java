package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Forbidden;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ConsoleService {

    public List<String> nodeAddress();

    public Map<String, Object> searchJob( String jobName);

    public List<String> listNames( String jobName);

    public List<String> jobResources();

    /**
     * 根据计算引擎类型显示任务
     */
    public Collection<Map<String, Object>> overview( String nodeAddress,  String clusterName);

    public Map<String, Object> groupDetail( String jobResource,
                                            String nodeAddress,
                                            Integer stage,
                                            Integer pageSize,
                                            Integer currentPage, String dtToken);

    public Boolean jobStick( String jobId);

    public void stopJob( String jobId) throws Exception;

    /**
     * 概览，杀死全部
     */
    public void stopAll( String jobResource,
                         String nodeAddress) throws Exception;

    public void stopJobList( String jobResource,
                             String nodeAddress,
                             Integer stage,
                             List<String> jobIdList) throws Exception;

    public Map<String, Object> clusterResources( String clusterName);


}
