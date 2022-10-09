package com.dtstack.taier.develop.datasource.convert.enums;

import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.datasource.api.source.DataSourceType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author ：nanqi
 * date：Created in 下午5:15 2021/7/29
 * company: www.dtstack.com
 */
public enum EComponent2Others {

    /**
     * HDFS 组件
     */
    HDFS(EComponentType.HDFS.getTypeCode(), null, DataSourceType.HDFS, null),

    /**
     * ThriftServer 组件
     */
    SPARK_THRIFT_1x(EComponentType.SPARK_THRIFT.getTypeCode(), HiveVersion.HIVE_1x.getVersion(), DataSourceType.SparkThrift2_1, EScheduleJobType.SPARK_SQL),
    SPARK_THRIFT_2x(EComponentType.SPARK_THRIFT.getTypeCode(), HiveVersion.HIVE_2x.getVersion(), DataSourceType.SparkThrift2_1, EScheduleJobType.SPARK_SQL),
    SPARK_THRIFT_3x(EComponentType.SPARK_THRIFT.getTypeCode(), HiveVersion.HIVE_3x.getVersion(), DataSourceType.SparkThrift2_1, EScheduleJobType.SPARK_SQL),
    SPARK_THRIFT_3x_APACHE(EComponentType.SPARK_THRIFT.getTypeCode(), HiveVersion.HIVE_3x_APACHE.getVersion(), DataSourceType.SparkThrift2_1, EScheduleJobType.SPARK_SQL),
    SPARK_THRIFT_3x_CDP(EComponentType.SPARK_THRIFT.getTypeCode(), HiveVersion.HIVE_3x_CDP.getVersion(), DataSourceType.SparkThrift2_1, EScheduleJobType.SPARK_SQL),

    /**
     * HiveServer 组件
     */
    HIVE_SERVER_1x(EComponentType.HIVE_SERVER.getTypeCode(), HiveVersion.HIVE_1x.getVersion(), DataSourceType.HIVE1X, EScheduleJobType.HIVE_SQL),
    HIVE_SERVER_2x(EComponentType.HIVE_SERVER.getTypeCode(), HiveVersion.HIVE_2x.getVersion(), DataSourceType.HIVE, EScheduleJobType.HIVE_SQL),
    HIVE_SERVER_3x(EComponentType.HIVE_SERVER.getTypeCode(), HiveVersion.HIVE_3x.getVersion(), DataSourceType.HIVE3X, EScheduleJobType.HIVE_SQL),
    HIVE_SERVER_3x_APACHE(EComponentType.HIVE_SERVER.getTypeCode(), HiveVersion.HIVE_3x_APACHE.getVersion(), DataSourceType.HIVE3X, EScheduleJobType.HIVE_SQL),
    HIVE_SERVER_3x_CDP(EComponentType.HIVE_SERVER.getTypeCode(), HiveVersion.HIVE_3x_CDP.getVersion(), DataSourceType.HIVE3_CDP, EScheduleJobType.HIVE_SQL),
    ;

    EComponent2Others(Integer typeCode, String version, DataSourceType sourceType, EScheduleJobType scheduleJobType) {
        this.typeCode = typeCode;
        this.version = version;
        this.sourceType = sourceType;
        this.scheduleJobType = scheduleJobType;
    }

    /**
     * 组件编码
     */
    private final Integer typeCode;

    /**
     * 版本
     */
    private final String version;

    /**
     * 数据源类型
     */
    private final DataSourceType sourceType;

    /**
     * 任务类型
     */
    private final EScheduleJobType scheduleJobType;

    public Integer getTypeCode() {
        return typeCode;
    }

    public String getVersion() {
        return version;
    }

    public DataSourceType getSourceType() {
        return sourceType;
    }

    public EScheduleJobType getJobType() {
        return scheduleJobType;
    }

    private static final Map<String, EComponent2Others> COMPONENT_MAP;

    private static final Map<Integer, EComponentType> DATASOURCE_MAP;

    private static final Map<Integer, EComponentType> SCHEDULE_JOB_MAP;

    static {
        COMPONENT_MAP = new HashMap<>();
        DATASOURCE_MAP = new HashMap<>();
        SCHEDULE_JOB_MAP = new HashMap<>();
        for (EComponent2Others component2DataSource : EComponent2Others.values()) {
            COMPONENT_MAP.put(getTypeCodeMapKey(component2DataSource.getTypeCode(), component2DataSource.getVersion()), component2DataSource);
            DATASOURCE_MAP.put(component2DataSource.getSourceType().getVal(), EComponentType.getByCode(component2DataSource.getTypeCode()));
            // HDFS没有Job类型
            if (Objects.equals(EComponentType.HDFS.getTypeCode(), component2DataSource.getTypeCode())) {
                continue;
            }
            SCHEDULE_JOB_MAP.put(component2DataSource.getJobType().getType(), EComponentType.getByCode(component2DataSource.getTypeCode()));
        }
    }

    /**
     * 根据 EComponent2DataSource 枚举获取 typeCodes 的Map key
     *
     * @param componentCode 组件编码
     * @param version       版本
     * @return type code key
     */
    private static String getTypeCodeMapKey(Integer componentCode, String version) {
        if (componentCode == null) {
            throw new DtCenterDefException("组件编码不能为空");
        }

        if (!componentCode.equals(EComponentType.HIVE_SERVER.getTypeCode())) {
            return componentCode.toString();
        }

        return String.format("%s_%s", componentCode, version);
    }

    /**
     * 根据组件获取数据源类型
     *
     * @param componentType 组件编码
     * @return 数据源类型
     */
    public static DataSourceType getDataSourceType(Integer componentType, String version) {
        if (componentType == null) {
            throw new DtCenterDefException("组件不能为空");
        }
        EComponent2Others component2DataSource = COMPONENT_MAP.get(getTypeCodeMapKey(componentType, version));
        if (component2DataSource == null) {
            throw new DtCenterDefException(String.format("根据组件%s获取数据源对应关系为空", EComponentType.getByCode(componentType).getName()));
        }
        return component2DataSource.getSourceType();
    }

    /**
     * 根据 EScheduleJobType 获取组件类型
     *
     * @param scheduleJobType 调度任务类型
     * @return 组件枚举
     */
    public static EComponentType getComponentTypeByEJob(Integer scheduleJobType) {
        if (scheduleJobType == null || !SCHEDULE_JOB_MAP.containsKey(scheduleJobType)) {
            throw new DtCenterDefException(String.format("任务类型 %s 查找对应组件类型异常", scheduleJobType));
        }

        return SCHEDULE_JOB_MAP.get(scheduleJobType);
    }

    /**
     * 根据 DataSourceType 获取组件类型
     *
     * @param datasourceType 数据源类型
     * @return 组件类型
     */
    public static EComponentType getComponentTypeBySourceType(Integer datasourceType) {
        if (datasourceType == null || !DATASOURCE_MAP.containsKey(datasourceType)) {
            throw new DtCenterDefException(String.format("数据源类型%s查找对应组件类型异常", datasourceType));
        }

        return DATASOURCE_MAP.get(datasourceType);
    }
}
