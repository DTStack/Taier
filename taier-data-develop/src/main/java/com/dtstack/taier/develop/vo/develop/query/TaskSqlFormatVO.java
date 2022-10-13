package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModelProperty;


/**
 * @author qianyi
 * @version 1.0
 * @date 2021/1/3 6:54 下午
 */
public class TaskSqlFormatVO extends DtInsightAuthParam {

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @ApiModelProperty(value = "任务SQL", example = "create database", required = true)
    private String sql;
}
