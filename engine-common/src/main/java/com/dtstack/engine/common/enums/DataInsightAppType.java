package com.dtstack.engine.common.enums;

/**
 * @Auther: dazhi
 * @Date: 2020/10/9 9:47 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum DataInsightAppType {

    BATCH("离线计算"),
    STREAM("流计算"),
    ANALYZE("分析引擎"),
    QUALITY("数据质量"),
    API("数据API"),
    SCIENCE("数据科学"),
    CONSOLE("控制台");

    private String comment;

    DataInsightAppType(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
}
