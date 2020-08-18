package com.dtstack.schedule.common.enums;

import com.dtstack.engine.common.exception.RdosDefineException;

/**
 *
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public enum EScheduleJobType {

    VIRTUAL(-1, "虚节点", -1,14),
    SPARK_SQL(0, "SparkSQL", 0,1),
    SPARK(1, "Spark", 1,2),
    SYNC(2, "数据同步", 2,8),
    SPARK_PYTHON(3, "PySpark", 3,3),
    //R 任务--暂时未支持
    R(4, "R", 3,19),
    DEEP_LEARNING(5,"深度学习", 3,11),
    PYTHON(6,"Python", 3,12),
    SHELL(7,"Shell", 3,13),
    ML_LIb(8, "机器学习", 1,10),
    HADOOP_MR(9, "HadoopMR", 1,5),
    WORK_FLOW(10, "工作流", -1,9),
    CARBON_SQL(12, "CarbonSQL", -1,15),
    NOTEBOOK(13, "Notebook", 3,17),
    ALGORITHM_LAB(14, "算法实验", -1,18),
    KYLIN_CUBE(16,"Kylin",4,16),
    LIBRA_SQL(15, "LibrA SQL", 0,7),
    HIVE_SQL(17,"HiveSQL",0,4),
    IMPALA_SQL(18, "ImpalaSQL", 0, 6),
    TIDB_SQL(19,"TiDBSQL",0,7),
    ORACLE_SQL(20,"Oracle SQL",0,8),
    GREENPLUM_SQL(21,"greenplum SQL",0,21),
    TENSORFLOW_1_X(22, "TensorFlow 1.x", 0, 5),
    KERAS(23, "Keras", 3, 6),
    PRESTO_SQL(24, "Presto", 0, 30);


    private Integer type;

    private String name;

    /**
     * 引擎能够接受的jobType
     * SQL              0
     * MR               1
     * SYNC             2
     * PYTHON           3
     * 不接受的任务类型    -1
     */
    private Integer engineJobType;

    private Integer sort;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEngineJobType(Integer engineJobType) {
        this.engineJobType = engineJobType;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    EScheduleJobType(Integer type, String name, Integer engineJobType, Integer sort){
        this.type = type;
        this.name = name;
        this.engineJobType = engineJobType;
        this.sort = sort;
    }

    public Integer getVal(){
        return this.type;
    }

    public String getName() {
        return name;
    }

    public Integer getEngineJobType() {
        return engineJobType;
    }

    public static EScheduleJobType getEJobType(int type){
        EScheduleJobType[] eJobTypes = EScheduleJobType.values();
        for(EScheduleJobType eJobType:eJobTypes){
            if(eJobType.type == type){
                return eJobType;
            }
        }
        return null;
    }

    public static Integer getEngineJobType(int type){
        EScheduleJobType[] eJobTypes = EScheduleJobType.values();
        for(EScheduleJobType eJobType:eJobTypes){
            if(eJobType.type == type){
                if (eJobType.getVal() != -1){
                    return eJobType.getEngineJobType();
                }
                break;
            }

        }
        throw new RdosDefineException("不支持的任务类型");
    }
}