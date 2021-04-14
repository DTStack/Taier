package com.dtstack.engine.common.util;

import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.schedule.common.enums.EScheduleJobType;

import java.util.*;

/**
 * 组件版本信息
 * @author xinge
 */
public class ComponentVersionUtil {


    public static final Set<Integer> NO_VERSION_SET;
    public static final Map<Integer,EComponentType> TASK_COMPONENT;

    /**
     * 不允许修改对应关系
     */
    static {
        NO_VERSION_SET = Collections.unmodifiableSet(initUnVersionComponent());
        TASK_COMPONENT = Collections.unmodifiableMap(initComponent());
    }

    private static Set<Integer> initUnVersionComponent(){
        Set<Integer> set=new HashSet<>(4);
        set.add(EScheduleJobType.WORK_FLOW.getType());
        set.add(EScheduleJobType.VIRTUAL.getType());
        set.add(EScheduleJobType.KYLIN_CUBE.getType());
        set.add(EScheduleJobType.HADOOP_MR.getType());
        return set;
    }

    private static Map<Integer,EComponentType> initComponent(){
        Map<Integer,EComponentType> map=new HashMap<>(32);
        // Flink
        map.put(EScheduleJobType.SYNC.getType(),EComponentType.FLINK);
        // Spark
        map.put(EScheduleJobType.SPARK_SQL.getType(),EComponentType.SPARK);
        map.put(EScheduleJobType.SPARK.getType(),EComponentType.SPARK);
        map.put(EScheduleJobType.SPARK_PYTHON.getType(),EComponentType.SPARK);
        // DtScript
        map.put(EScheduleJobType.PYTHON.getType(),EComponentType.DT_SCRIPT);
        map.put(EScheduleJobType.SHELL.getType(),EComponentType.DT_SCRIPT);
        // CarbonData ThriftServer
        map.put(EScheduleJobType.CARBON_SQL.getType(),EComponentType.CARBON_DATA);
        // LibrA SQL
        map.put(EScheduleJobType.LIBRA_SQL.getType(),EComponentType.LIBRA_SQL);
        // TiDB SQL
        map.put(EScheduleJobType.TIDB_SQL.getType(),EComponentType.TIDB_SQL);
        // Oracle SQL
        map.put(EScheduleJobType.ORACLE_SQL.getType(),EComponentType.ORACLE_SQL);
        // HiveServer
        map.put(EScheduleJobType.HIVE_SQL.getType(),EComponentType.HIVE_SERVER);
        // Impala SQL
        map.put(EScheduleJobType.IMPALA_SQL.getType(),EComponentType.IMPALA_SQL);
        // Greenplum SQL
        map.put(EScheduleJobType.GREENPLUM_SQL.getType(),EComponentType.GREENPLUM_SQL);
        return map;
    }




    public static String getComponentVersion(Map<Integer,String > componentVersionMap, EComponentType componentType){
        return Objects.isNull(componentVersionMap)?null:componentVersionMap.get(componentType.getTypeCode());
    }

    public static String getComponentVersion(Map<Integer,String > componentVersionMap, Integer componentTypeCode){
        return Objects.isNull(componentVersionMap)?null:componentVersionMap.get(componentTypeCode);
    }

    public static EComponentType transformTaskType2ComponentType(Integer taskType){
        if (NO_VERSION_SET.contains(taskType)){
            return null;
        }
        EComponentType componentType = TASK_COMPONENT.get(taskType);
        if (Objects.nonNull(componentType)){
            return componentType;
        }
        throw new RdosDefineException("Task Type = "+ taskType +" UnSupport");
    }

    public static Integer transformTaskType2ComponentTypeCode(Integer taskType){
        EComponentType componentType = transformTaskType2ComponentType(taskType);
        if (Objects.nonNull(componentType)){
            return componentType.getTypeCode();
        }
        return null;
    }


    public static Map<Integer,String > singletonMap(EComponentType componentType,String componentVersion){
        return Collections.singletonMap(componentType.getTypeCode(),componentVersion);
    }


}
