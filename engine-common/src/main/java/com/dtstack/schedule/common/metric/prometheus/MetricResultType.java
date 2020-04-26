package com.dtstack.schedule.common.metric.prometheus;

/**
 * 描述各个返回类型的数据格式
 * Date: 2018/10/10
 * Company: www.dtstack.com
 * @author xuchao
 */
public enum MetricResultType {

    /**
     [
     {
     "metric": { "<label_name>": "<label_value>", ... },
     "values": [ [ <unix_time>, "<sample_value>" ], ... ]
     },
     ...
     ]
     */
    MATRIX("matrix"),


    /**
     [
     {
     "metric": { "<label_name>": "<label_value>", ... },
     "value": [ <unix_time>, "<sample_value>" ]
     },
     ...
     ]
     */
    VERTOR("vector"),


    /**
     * [ <unix_time>, "<scalar_value>" ]
     */
    SCALAR("scalar"),


    /**
     * [ <unix_time>, "<string_value>" ]
     */
    TRING("string");

    private String typeInfo;

    MetricResultType(String typeInfo){
        this.typeInfo = typeInfo;
    }

    public String getTypeInfo(){
        return typeInfo;
    }

}
