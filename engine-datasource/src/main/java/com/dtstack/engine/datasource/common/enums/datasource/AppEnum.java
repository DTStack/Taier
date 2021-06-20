package com.dtstack.engine.datasource.common.enums.datasource;

/**
 * 产品编码名称枚举类
 * @description:
 * @author: liuxx
 * @date: 2021/3/9
 */
public enum AppEnum {
    BATCH("batch", "离线"),
    STREAM("stream", "实时"),
    AI("ai", "算法"),
    VALID("valid", "数据质量"),
    TAG("tag", "智能标签"),
    ASSETS("assets", "数据资产"),
    API("api", "api");


    AppEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;

    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
