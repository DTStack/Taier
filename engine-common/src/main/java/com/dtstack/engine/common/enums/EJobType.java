package com.dtstack.engine.common.enums;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;

/**
 *
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public enum EJobType {
    //
    VIRTUAL(-1, "虚节点", -1,14),
    SPARK_SQL(0, "Spark SQL", 0,1),
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
    GaussDB_SQL(15, "GaussDB SQL", 0,7),
    HIVE_SQL(17,"Hive SQL",0,4),
    IMPALA_SQL(18, "Impala SQL", 0, 6),
    TIDB_SQL(19,"TiDB SQL",0,7),
    ORACLE_SQL(20,"Oracle SQL",0,8),
    GREENPLUM_SQL(21,"Greenplum SQL",0,21),
    TENSORFLOW_1_X(22, "TensorFlow 1.x", 3, 5),
    KERAS(23, "Keras", 3, 6),
    PRESTO(24,"Presto",0,30),
    PYTORCH(25, "PyTorch", 3, 40),
    KINGBASE(26,"kingbase",0,41),
    NOT_DO_TASK(27,"空任务",-1,0),
    INCEPTOR_SQL(28,"Inceptor SQL",0,4),
    SHELL_ON_AGENT(29,"Shell on Agent",3,4),
    ANALYTICDB_FOR_PG(30, "AnalyticDB PostgreSQL",0,4),
    FLINK_SQL(31,"Flink SQL",0,4),
    MYSQL(32, "MySQL", 0, 7),
    SQL_SERVER(33, "SQL Server", 0, 8),
    DB2(34, "DB2", 0, 9),
    OCEANBASE(35, "OceanBase", 0, 10),
    TRINO(36, "Trino", 0, 42)
    ;

    private final Integer type;

    private final String name;

    /**
     * 引擎能够接受的jobType
     * SQL              0
     * MR               1
     * SYNC             2
     * PYTHON           3
     * 不接受的任务类型    -1
     */
    private final Integer engineJobType;

    private final Integer sort;

    public final Integer getType() {
        return type;
    }

    public Integer getSort() {
        return sort;
    }

    EJobType(Integer type, String name, Integer engineJobType, Integer sort){
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

    public static EJobType getEJobType(int type){
        EJobType[] eJobTypes = EJobType.values();
        for(EJobType eJobType:eJobTypes){
            if(eJobType.type == type){
                return eJobType;
            }
        }
        return null;
    }

    public static Integer getEngineJobType(int type){
        EJobType[] eJobTypes = EJobType.values();
        for(EJobType eJobType:eJobTypes){
            if(eJobType.type == type){
                    return eJobType.getEngineJobType();
            }

        }
        throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
    }
}
