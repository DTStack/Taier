package com.dtstack.engine.master.jobdealer.resource;

import com.dtstack.engine.mapper.ClusterMapper;
import com.dtstack.engine.mapper.ClusterTenantMapper;
import com.dtstack.engine.master.service.ClusterService;
import com.dtstack.engine.master.service.ComponentService;
import com.dtstack.engine.pluginapi.JobClient;
import com.dtstack.engine.pluginapi.enums.EngineType;
import com.dtstack.engine.common.exception.RdosDefineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/11
 */
@Component
public class CommonResource {

    private static Map<EngineType, CommonResource> resources = new ConcurrentHashMap<>();

    @Autowired
    protected ClusterMapper clusterMapper;

    @Autowired
    protected ClusterService clusterService;

    @Autowired
    protected ComponentService componentService;

    @Autowired
    protected ClusterTenantMapper clusterTenantMapper;


    public ComputeResourceType newInstance(JobClient jobClient) {
        EngineType engineType = EngineType.getEngineType(jobClient.getEngineType());
        CommonResource resource = resources.computeIfAbsent(engineType, k -> {
            CommonResource commonResource = null;
            switch (engineType) {
                case Flink:
                    commonResource = new FlinkResource();
                    commonResource.setClusterMapper(clusterMapper);
                    commonResource.setClusterService(clusterService);
                    commonResource.setComponentService(componentService);
                    commonResource.setClusterTenantMapper(clusterTenantMapper);
                    break;
                case Spark:
                case Learning:
                case DtScript:
                case Hadoop:
                case Hive:
                case Mysql:
                case Oracle:
                case Sqlserver:
                case Maxcompute:
                case PostgreSQL:
                case Kylin:
                case Impala:
                case TiDB:
                case Presto:
                case GreenPlum:
                case Dummy:
                case KingBase:
                case InceptorSQL:
                case DtScriptAgent:
                case AnalyticdbForPg:
                    commonResource = this;
                    break;
                default:
                    throw new RdosDefineException("engineType:" + engineType + " is not support.");
            }
            return commonResource;
        });

        return resource.getComputeResourceType(jobClient);
    }

    public ComputeResourceType getComputeResourceType(JobClient jobClient) {

        EngineType engineType = EngineType.getEngineType(jobClient.getEngineType());
        switch (engineType) {
            case Spark:
            case Learning:
            case DtScript:
            case Hadoop:
                return ComputeResourceType.Yarn;
            case Hive:
                return ComputeResourceType.Hive;
            case Mysql:
                return ComputeResourceType.Mysql;
            case Oracle:
                return ComputeResourceType.Oracle;
            case Sqlserver:
                return ComputeResourceType.Sqlserver;
            case Maxcompute:
                return ComputeResourceType.Maxcompute;
            case PostgreSQL:
                return ComputeResourceType.PostgreSQL;
            case Kylin:
                return ComputeResourceType.Kylin;
            case Impala:
                return ComputeResourceType.Impala;
            case TiDB:
                return ComputeResourceType.TiDB;
            case GreenPlum:
                return ComputeResourceType.GreenPlum;
            case Dummy:
                return ComputeResourceType.Dummy;
            case Presto:
                return ComputeResourceType.Presto;
            case KingBase:
                return ComputeResourceType.KingBase;
            case InceptorSQL:
                return ComputeResourceType.InceptorSQL;
            case DtScriptAgent:
                return ComputeResourceType.DtScriptAgent;
            case AnalyticdbForPg:
                return ComputeResourceType.AnalyticdbForPg;
            default:
                throw new RdosDefineException("engineType:" + engineType + " is not support.");
        }
    }

    public static Map<EngineType, CommonResource> getResources() {
        return resources;
    }

    public static void setResources(Map<EngineType, CommonResource> resources) {
        CommonResource.resources = resources;
    }

    public ClusterMapper getClusterMapper() {
        return clusterMapper;
    }

    public void setClusterMapper(ClusterMapper clusterMapper) {
        this.clusterMapper = clusterMapper;
    }

    public ClusterService getClusterService() {
        return clusterService;
    }

    public void setClusterService(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    public ComponentService getComponentService() {
        return componentService;
    }

    public void setComponentService(ComponentService componentService) {
        this.componentService = componentService;
    }

    public ClusterTenantMapper getClusterTenantMapper() {
        return clusterTenantMapper;
    }

    public void setClusterTenantMapper(ClusterTenantMapper clusterTenantMapper) {
        this.clusterTenantMapper = clusterTenantMapper;
    }
}
