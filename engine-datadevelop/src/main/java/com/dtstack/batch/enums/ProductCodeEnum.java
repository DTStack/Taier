package com.dtstack.batch.enums;

/**
 * date: 2021/4/12 5:37 下午
 * author: zhaiyue
 */
public enum  ProductCodeEnum {

    /**
     * 离线开发
     */
    RDOS(1,"RDOS", "rdos", "离线开发"),

    /**
     * 算法开发
     */
    SCIENCE(8, "RDOS", "science", "算法开发"),

    /**
     * 智能标签
     */
    TAG(4, "RDOS", "tagEngine", "智能标签");

    private Integer type;

    private String productCode;

    private String subProductCode;

    private String subProductName;

    ProductCodeEnum(Integer type, String productCode, String subProductCode, String subProductName){
        this.type = type;
        this.productCode = productCode;
        this.subProductCode = subProductCode;
        this.subProductName = subProductName;
    }

    public Integer getType(){
        return type;
    }

    public String getProjectCode(){
        return productCode;
    }

    public String getSubProductCode(){
        return subProductCode;
    }

    public String getSubProductName(){
        return subProductName;
    }


}
