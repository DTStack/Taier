package com.dtstack.engine.master.enums;

import com.dtstack.engine.common.exception.EngineDefineException;
import com.dtstack.engine.common.exception.ErrorCode;

public enum ComponentTempFile {

    FLINK(0, "flink-kerberos.xml"),
    SPARK(1, "spark-kerberos.xml"),
    LEARNING(2, "learning-kerberos.xml"),
    DT_YARN_SHELL(3, "dtyarnshell-kerberos.xml"),
    HDFS(4, "hdfs-kerberos.xml"),
    YARN(5, "yarn-kerberos.xml"),
    SPARK_THRIFT(6,"hive-kerberos.xml"),
    CARBON_DATA(7, "carbon-kerberos.xml"),
    HIVE_SERVER(9,"hive-kerberos.xml")
    ;

    private int typeCode;

    private String name;

    ComponentTempFile(int typeCode, String name) {
        this.typeCode = typeCode;
        this.name = name;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public String getName() {
        return name;
    }

    public static String getFileName(Integer code) {
        for (ComponentTempFile componentTempFile : ComponentTempFile.values()) {
            if(componentTempFile.getTypeCode() == code) {
                return componentTempFile.getName();
            }
        }
        throw new EngineDefineException(ErrorCode.INVALID_PARAMETERS.getDescription());
    }
}

