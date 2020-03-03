//package com.dtstack.engine.master.impl;
//
//import com.alibaba.fastjson.JSONObject;
//import com.dtstack.dtcenter.common.annotation.Forbidden;
//import com.dtstack.dtcenter.common.enums.EComponentType;
//import com.dtstack.dtcenter.common.enums.MultiEngineType;
//import com.dtstack.dtcenter.common.pager.PageResult;
//import com.dtstack.dtcenter.common.pager.PageUtil;
//import com.dtstack.engine.common.exception.ErrorCode;
//import com.dtstack.engine.common.exception.RdosDefineException;
//import com.dtstack.engine.dao.ClusterDao;
//import com.dtstack.engine.dao.EngineDao;
//import com.dtstack.engine.dao.TenantDao;
//import com.dtstack.engine.domain.Cluster;
//import com.dtstack.engine.domain.Component;
//import com.dtstack.engine.domain.Engine;
//import com.dtstack.engine.master.component.ComponentFactory;
//import com.dtstack.engine.master.component.FlinkComponent;
//import com.dtstack.engine.master.component.YARNComponent;
//import com.google.common.base.Preconditions;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.collections.MapUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.ibatis.annotations.Param;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * company: www.dtstack.com
// * author: toutian
// * create: 2018/9/19
// */
//@Service
//public class GroupService {
//
//    private static Logger LOG = LoggerFactory.getLogger(GroupService.class);
//
//    @Autowired
//    private ClusterDao clusterDao;
//
//    @Autowired
//    private ConsoleService consoleService;
//
//    @Autowired
//    private ActionService actionService;
//
//    @Autowired
//    private EngineDao engineDao;
//
//    @Autowired
//    private ComponentService componentService;
//
//    private static final Integer GROUP_TOTAL_KILL_MODEL = 0;
//    private static final Integer NAME_TOTAL_KILL_MODEL = 1;
//
//    public List<String> nodes() {
//        List<String> nodes = new ArrayList<>();
//        try {
//            nodes.addAll(consoleService.nodes());
//        } catch (Exception e) {
//            LOG.warn("get nodes from engine error:{}", e);
//        }
//        return nodes;
//    }
//
//    //操作到ConsoleService中
//    public PageResult overview(@Param("pageSize") int pageSize,
//                               @Param("currentPage") int currentPage) {
//        List<Map<String, Object>> overview = consoleService.overview();
//        return PageUtil.getPageResult(overview, currentPage, pageSize);
//    }
//
//    //操作到ConsoleService中
//    public List<String> listNames(@Param("jobName") String jobName) {
//        Preconditions.checkNotNull(jobName, "parameters of jobName is required");
//        List<String> jobNames = consoleService.listNames(jobName);
//        return jobNames;
//    }
//
//    //操作到ConsoleService中
//    public Map<String, Object> searchJob(@Param("jobName") String jobName,
//                                         @Param("pageSize") Integer pageSize,
//                                         @Param("currentPage") Integer currentPage) {
//        Preconditions.checkNotNull(jobName, "parameters of jobName is required");
//        Preconditions.checkNotNull(pageSize, "parameters of pageSize is required");
//        Preconditions.checkNotNull(currentPage, "parameters of currentPage is required");
//
//        Map<String, Object> result = consoleService.searchJob(jobName);
//        return result;
//    }
//
//
//    //停止可以操作到ConsoleService中，并考虑对停止去掉没有用处的参数
//    public void stopJob(@Param("computeTypeInt") String computeTypeInt,
//                        @Param("engineType") String engineType,
//                        @Param("queueName") String queueName,
//                        @Param("clusterName") String clusterName,
//                        @Param("jobId") String jobId) throws Exception {
//        Preconditions.checkNotNull(computeTypeInt, "parameters of computeTypeInt is required");
//        Preconditions.checkNotNull(engineType, "parameters of engineType is required");
//        Preconditions.checkNotNull(queueName, "parameters of queueName is required");
//        Preconditions.checkNotNull(clusterName, "parameters of clusterName is required");
//        Preconditions.checkNotNull(jobId, "parameters of jobId is required");
//
//        Map<String, Object> params = new HashMap<>(4);
//        params.put("taskId", jobId);
//        params.put("engineType", engineType);
//        params.put("computeType", computeTypeInt);
//        params.put("groupName", clusterName + '_' + queueName);
//
//        List<Map<String, Object>> jobs = new ArrayList<>(1);
//        jobs.add(params);
//
//
//        Map<String, Object> stopJobs = new HashMap<>(1);
//        stopJobs.put("jobs", jobs);
//
//        actionService.stop(stopJobs);
//    }
//
//    //停止可以操作到ConsoleService中
//    public void stopJobList(@Param("jobResource") String jobResource,
//                            @Param("jobIdList") List<Map<String, Object>> jobIdList,
//                            @Param("jobName") String jobName,
//                            @Param("totalModel") Integer totalModel,
//                            @Param("totalSize") Integer totalSize) throws Exception {
//        List<Map<String, Object>> jobs = null;
//        if (totalModel != null) {
//            List<Map<String, Object>> topN = null;
//            if (GROUP_TOTAL_KILL_MODEL.equals(totalModel)) {
//                Map<String, Object> groupDetail = groupDetail(jobResource, totalSize, 1);
//                if (groupDetail == null) {
//                    return;
//                }
//                topN = (List<Map<String, Object>>) groupDetail.get("topN");
//            } else if (NAME_TOTAL_KILL_MODEL.equals(totalModel)) {
//                Map<String, Object> searchJob = searchJob(jobName, totalSize, 1);
//                if (searchJob == null) {
//                    return;
//                }
//                topN = (List<Map<String, Object>>) searchJob.get("theJob");
//            }
//            jobs = topN;
//        } else {
//            jobs = jobIdList;
//        }
//        if (jobs == null || jobs.isEmpty()) {
//            return;
//        }
//
//        Map<String, Object> stopJobs = new HashMap<>(1);
//        stopJobs.put("jobs", jobs);
//
//        actionService.stop(stopJobs);
//    }
//
//    //engineTypes -> jobResources
//    public List<String> engineTypes() {
//        List<String> engineTypes = consoleService.jobResources();
//        return engineTypes;
//    }
//
//    //groups 直接去掉
////    public List<String> groups(@Param("node") String node,
////                               @Param("engineType") String engineType,
////                               @Param("clusterName") String clusterName) {
////        Preconditions.checkNotNull(node, "parameters of node is required");
////        Preconditions.checkNotNull(engineType, "parameters of engineType is required");
////
////        List<Map<String, Object>> engineTypes = consoleService.groups(node, engineType, clusterName);
////        List<String> groupNames = engineTypes.stream().map(t -> MapUtils.getString(t, "groupName"))
////                .filter(t -> {
////                    if (StringUtils.isNotBlank(clusterName)) {
////                        return t.startsWith(clusterName);
////                    }
////                    return true;
////                }).collect(Collectors.toList());
////        return groupNames;
////    }
//
//    //groupDetail 直接对接 consoleService
//    public Map<String, Object> groupDetail(@Param("jobResource") String jobResource,
//                                           @Param("pageSize") Integer pageSize,
//                                           @Param("currentPage") Integer currentPage) {
//
//        Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");
//        Preconditions.checkNotNull(pageSize, "parameters of pageSize is required");
//        Preconditions.checkNotNull(currentPage, "parameters of currentPage is required");
//        Map<String, Object> result = consoleService.groupDetail(jobResource, pageSize, currentPage);
//        return result;
//    }
//
//    //优先级 直接对接 consoleService
//    public boolean jobPriority(@Param("jobId") String jobId,
//                               @Param("jobResource") String jobResource) {
//        Preconditions.checkArgument(StringUtils.isNotBlank(jobId), "parameters of jobId is required");
//        Preconditions.checkArgument(StringUtils.isNotBlank(jobResource), "parameters of jobResource is required");
//        return consoleService.jobStick(jobId, jobResource);
//    }
//
//    public Map<String, Object> clusterResources(@Param("clusterName") String clusterName) {
//        if (StringUtils.isEmpty(clusterName)) {
//            return MapUtils.EMPTY_MAP;
//        }
//
//        Cluster cluster = clusterDao.getByClusterName(clusterName);
//        if (cluster == null) {
//            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
//        }
//
//        Component yarnComponent = getYarnComponent(cluster.getId());
//        if (yarnComponent == null) {
//            return null;
//        }
//
//        Map<String, Object> yarnConfig = JSONObject.parseObject(yarnComponent.getComponentConfig(), Map.class);
//
//        return getResources(yarnConfig, cluster.getId());
//    }
//
//    @Forbidden
//    public Map<String, Object> getResources(Map<String, Object> yarnConfig, Long clusterId) {
//        YARNComponent yarnComponent = null;
//        try {
//            Map<String, Object> kerberosConfig = componentService.fillKerberosConfig(JSONObject.toJSONString(yarnConfig), clusterId);
//            yarnComponent = (YARNComponent) ComponentFactory.getComponent(kerberosConfig, EComponentType.YARN);
//            yarnComponent.initClusterResource(false);
//
//            FlinkComponent flinkComponent = (FlinkComponent) ComponentFactory.getComponent(null, EComponentType.FLINK);
//            flinkComponent.initTaskManagerResource(yarnComponent.getYarnClient());
//
//            Map<String, Object> clusterResources = new HashMap<>(2);
//            clusterResources.put("yarn", yarnComponent.getClusterNodes());
//            clusterResources.put("flink", flinkComponent.getTaskManagerDescriptions());
//            return clusterResources;
//        } catch (Exception e) {
//            LOG.error(" ", e);
//            throw new RdosDefineException("flink资源获取异常");
//        } finally {
//            if (yarnComponent != null) {
//                yarnComponent.closeYarnClient();
//            }
//        }
//    }
//
//    private Component getYarnComponent(Long clusterId) {
//        List<Engine> engines = engineDao.listByClusterId(clusterId);
//        if (CollectionUtils.isEmpty(engines)) {
//            return null;
//        }
//
//        Engine hadoopEngine = null;
//        for (Engine e : engines) {
//            if (e.getEngineType() == MultiEngineType.HADOOP.getType()) {
//                hadoopEngine = e;
//                break;
//            }
//        }
//
//        if (hadoopEngine == null) {
//            return null;
//        }
//
//        List<Component> componentList = componentService.listComponent(hadoopEngine.getId());
//        if (CollectionUtils.isEmpty(componentList)) {
//            return null;
//        }
//
//        for (Component component : componentList) {
//            if (EComponentType.YARN.getTypeCode() == component.getComponentTypeCode()) {
//                return component;
//            }
//        }
//
//        return null;
//    }
//}
