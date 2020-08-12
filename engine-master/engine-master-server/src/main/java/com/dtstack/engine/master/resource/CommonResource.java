package com.dtstack.engine.master.resource;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.EngineDao;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
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
    protected ClusterDao clusterDao;

    @Autowired
    protected EngineDao engineDao;

    @Autowired
    protected ClusterService clusterService;

    @Autowired
    protected ComponentService componentService;


    public ComputeResourceType newInstance(JobClient jobClient) {
        EngineType engineType = EngineType.getEngineType(jobClient.getEngineType());
        CommonResource resource = resources.computeIfAbsent(engineType, k -> {
            CommonResource commonResource = null;
            switch (engineType) {
                case Flink:
                    commonResource = new FlinkResource();
                    commonResource.setClusterDao(clusterDao);
                    commonResource.setEngineDao(engineDao);
                    commonResource.setClusterService(clusterService);
                    commonResource.setComponentService(componentService);
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
                case GreenPlum:
                case Dummy:
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
            default:
                throw new RdosDefineException("engineType:" + engineType + " is not support.");
        }
    }

    public ClusterDao getClusterDao() {
        return clusterDao;
    }

    public void setClusterDao(ClusterDao clusterDao) {
        this.clusterDao = clusterDao;
    }

    public EngineDao getEngineDao() {
        return engineDao;
    }

    public void setEngineDao(EngineDao engineDao) {
        this.engineDao = engineDao;
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
}
