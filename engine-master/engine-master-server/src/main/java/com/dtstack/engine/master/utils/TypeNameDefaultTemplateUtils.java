package com.dtstack.engine.master.utils;

import com.dtstack.engine.api.vo.Pair;
import com.dtstack.engine.master.enums.EComponentType;

import java.util.HashMap;

/**
 * @author yuebai
 * @date 2021-02-25
 */
public class TypeNameDefaultTemplateUtils {

    public static final HashMap<String, Pair<Long, Integer>> typeNameMapping = new HashMap<>();

    static {
        typeNameMapping.put("yarn2-hdfs2-dtscript", new Pair<>(-100L, EComponentType.DT_SCRIPT.getTypeCode()));
        typeNameMapping.put("yarn3-hdfs3-dtscript", new Pair<>(-100L, EComponentType.DT_SCRIPT.getTypeCode()));

        typeNameMapping.put("sftp", new Pair<>(-101L, EComponentType.SFTP.getTypeCode()));
        typeNameMapping.put("dummy", new Pair<>(-101L, EComponentType.SFTP.getTypeCode()));

        typeNameMapping.put("k8s-hdfs2-flink110", new Pair<>(-102L, EComponentType.FLINK.getTypeCode()));


        typeNameMapping.put("yarn2-hdfs2-learning", new Pair<>(-103L, EComponentType.LEARNING.getTypeCode()));
        typeNameMapping.put("yarn3-hdfs3-learning", new Pair<>(-103L, EComponentType.LEARNING.getTypeCode()));

        typeNameMapping.put("tidb", new Pair<>(-104L, EComponentType.TIDB_SQL.getTypeCode()));
        typeNameMapping.put("oracle", new Pair<>(-104L, EComponentType.ORACLE_SQL.getTypeCode()));
        typeNameMapping.put("impala", new Pair<>(-104L, EComponentType.IMPALA_SQL.getTypeCode()));
        typeNameMapping.put("greenplumsql", new Pair<>(-104L, EComponentType.GREENPLUM_SQL.getTypeCode()));

        typeNameMapping.put("presto", new Pair<>(-105L, EComponentType.PRESTO_SQL.getTypeCode()));

        typeNameMapping.put("hive", new Pair<>(-106L, EComponentType.HIVE_SERVER.getTypeCode()));
        typeNameMapping.put("hive2", new Pair<>(-106L, EComponentType.HIVE_SERVER.getTypeCode()));

        typeNameMapping.put("k8s-hdfs2-spark240", new Pair<>(-107L, EComponentType.SPARK.getTypeCode()));

        typeNameMapping.put("yarn2-hdfs2-spark210", new Pair<>(-108L, EComponentType.SPARK.getTypeCode()));
        typeNameMapping.put("yarn3-hdfs3-spark240", new Pair<>(-108L, EComponentType.SPARK.getTypeCode()));
        typeNameMapping.put("yarn2-hdfs2-spark240", new Pair<>(-108L, EComponentType.SPARK.getTypeCode()));

        typeNameMapping.put("yarn2-hdfs2-flink110", new Pair<>(-109L, EComponentType.FLINK.getTypeCode()));

        typeNameMapping.put("yarn2-hdfs2-flink180", new Pair<>(-110L, EComponentType.FLINK.getTypeCode()));
        typeNameMapping.put("yarn2-hdfs2-flinkhw", new Pair<>(-110L, EComponentType.FLINK.getTypeCode()));
    }

    public static Pair<Long,Integer> getDefaultComponentIdByTypeName(String typeName){
        return typeNameMapping.get(typeName);
    }
}
