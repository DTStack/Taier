package com.dtstack.engine.master.component;

import com.dtstack.schedule.common.enums.DataBaseType;
import com.dtstack.engine.master.enums.EComponentType;

import java.util.Map;

public class ComponentFactory {

    public static ComponentImpl getComponent(Map<String, Object> config, EComponentType type) {
        BaseComponent component;
        switch (type) {
            case HDFS:
                component = new HDFSComponent(config);
                break;
            case YARN:
                component = new YARNComponent(config);
                break;
            case FLINK:
                component = new FlinkComponent(config);
                break;
            case SPARK:
                component = new SparkComponent(config);
                break;
            case LEARNING:
                component = new LearningComponent(config);
                break;
            case DT_SCRIPT:
                component = new DtScriptComponent(config);
                break;
            case SPARK_THRIFT:
                component = new JDBCComponent(config, DataBaseType.HIVE);
                break;
            case CARBON_DATA:
                component = new JDBCComponent(config, DataBaseType.CarbonData);
                break;
            case LIBRA_SQL:
                component = new JDBCComponent(config, DataBaseType.PostgreSQL);
                break;
            case HIVE_SERVER:
                component = new JDBCComponent(config, DataBaseType.HIVE);
                break;
            case SFTP:
                component = new SFTPComponet(config);
                break;
            case IMPALA_SQL:
                component = new JDBCComponent(config, DataBaseType.Impala);
                break;
            case TIDB_SQL:
                component = new JDBCComponent(config,DataBaseType.TiDB);
                break;
            case ORACLE_SQL:
                component = new JDBCComponent(config,DataBaseType.Oracle);
                break;
            default:
                throw new IllegalArgumentException("未知组件类型");
        }

        component.setComponentType(type);
        return component;
    }
}
