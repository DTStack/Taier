package com.dtstack.engine.datasource.common.enums.datasource;

/**
 * 数据源类目枚举类
 * @description:
 * @author: liuxx
 * @date: 2021/3/9
 */
public enum DsClassifyEnum {

    TOTAL(1L, "total", "全部", 10),
    MOST_USE(2L, "mostUse", "常用", 9),
    RELATIONAL(3L, "relational", "关系型", 8),
    BIG_DATA(4L, "bigData", "大数据存储", 7),
    MPP(5L, "mpp", "MPP", 6),
    SEMI_STRUCT(6L, "semiStruct", "半结构化",5),
    ANALYTIC(7L, "analytic", "分析型", 4),
    NO_SQL(8L, "NoSQL", "NoSQL", 3),
    ACTUAL_TIME(9L, "actualTime", "实时", 2),
    API(10L, "api", "接口", 1);


    DsClassifyEnum(Long classifyId, String classifyCode, String classifyName, Integer sorted) {
        this.classifyId = classifyId;
        this.classifyCode = classifyCode;
        this.classifyName = classifyName;
        this.sorted = sorted;
    }

    private Long classifyId;

    private String classifyCode;

    private String classifyName;

    private Integer sorted;

    public Long getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(Long classifyId) {
        this.classifyId = classifyId;
    }

    public String getClassifyCode() {
        return classifyCode;
    }

    public void setClassifyCode(String classifyCode) {
        this.classifyCode = classifyCode;
    }

    public String getClassifyName() {
        return classifyName;
    }

    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }

    public Integer getSorted() {
        return sorted;
    }

    public void setSorted(Integer sorted) {
        this.sorted = sorted;
    }
}
