package com.dtstack.engine.master.enums;

import com.dtstack.dtcenter.common.enums.EComponentType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ComponentTypeNameNeedVersion{
    dtscript(EComponentType.DT_SCRIPT.getTypeCode(), "dtscript"),
    flink150(EComponentType.FLINK.getTypeCode(), "flink150"),
    flink180(EComponentType.FLINK.getTypeCode(), "flink180"),
    learning(EComponentType.LEARNING.getTypeCode(), "learning"),
    //spark(EComponentType.SPARK.getTypeCode(), "spark"),
    sparkYarn(EComponentType.SPARK.getTypeCode(), "spark-yarn");

    private Integer code;
    private String typeName;

    ComponentTypeNameNeedVersion(Integer code, String typeName) {
        this.code = code;
        this.typeName = typeName;
    }

    public Integer getCode() {
        return code;
    }

    public String getTypeName() {
        return typeName;
    }

    public static List<ComponentTypeNameNeedVersion> listByCode(Integer code) {
        return Arrays.stream(ComponentTypeNameNeedVersion.values()).filter(c -> c.getCode().equals(code)).collect(Collectors.toList());
    }
}


