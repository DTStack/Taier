package com.dtstack.batch.mapping;

import com.dtstack.dtcenter.common.enums.EScriptType;
import com.dtstack.dtcenter.common.enums.EngineType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 脚本类型和引擎类型直接的映射关系
 * Date: 2019/5/20
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class ScriptTypeEngineTypeMapping {

    private final static Map<Integer, MultiEngineType> refMap = Maps.newHashMap();
    private final static Map<Integer, EngineType> jobTypeMap = Maps.newHashMap();

    static {
        refMap.put(EScriptType.SparkSQL.getType(), MultiEngineType.HADOOP);
        refMap.put(EScriptType.Python_2x.getType(), MultiEngineType.HADOOP);
        refMap.put(EScriptType.Python_3x.getType(), MultiEngineType.HADOOP);
        refMap.put(EScriptType.Shell.getType(), MultiEngineType.HADOOP);
        refMap.put(EScriptType.LibrASQL.getType(), MultiEngineType.LIBRA);
        refMap.put(EScriptType.ImpalaSQL.getType(), MultiEngineType.HADOOP);

        jobTypeMap.put(EScriptType.SparkSQL.getType(), EngineType.Spark);
        jobTypeMap.put(EScriptType.Python_2x.getType(), EngineType.Python2);
        jobTypeMap.put(EScriptType.Python_3x.getType(), EngineType.Python3);
        jobTypeMap.put(EScriptType.Shell.getType(), EngineType.Shell);
        jobTypeMap.put(EScriptType.LibrASQL.getType(), EngineType.Libra);
    }

    public static MultiEngineType getEngineTypeByTaskType(Integer taskType) {
        return refMap.get(taskType);
    }

    public static EngineType getEngineTypeByScriptType(Integer scriptType) {
        return jobTypeMap.get(scriptType);
    }
}
