package com.dtstack.engine.kylin.enums;

/**
 * @author jiangbo
 * @date 2019/7/2
 */
public enum EBuildType {

    /**
     * 全量构建
     */
    BUILD,

    /**
     * 合并指定范围的数据
     */
    MERGE,

    /**
     * 只刷新指定时间范围内的数据
     */
    REFRESH;

    public static EBuildType getType(String type){
        for (EBuildType value : EBuildType.values()) {
            if(value.name().equalsIgnoreCase(type)){
                return value;
            }
        }

        return BUILD;
    }
}
