package com.dtstack.engine.master.resource;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.exception.RdosDefineException;
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

    public ComputeResourceType newInstance(JobClient jobClient) {
        EngineType engineType = EngineType.getEngineType(jobClient.getEngineType());
        CommonResource resource = resources.computeIfAbsent(engineType, k -> {
            CommonResource commonResource = null;
            switch (engineType) {
                case Flink:
                    commonResource = new FlinkResource();
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
}
