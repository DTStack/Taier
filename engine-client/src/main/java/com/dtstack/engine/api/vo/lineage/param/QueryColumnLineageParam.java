package com.dtstack.engine.api.vo.lineage.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname QueryColumnLineageParam
 * @Description
 * @Date 2020/11/3 10:45
 * @Created chener@dtstack.com
 */
@ApiModel("查询字段血缘参数")
public class QueryColumnLineageParam extends QueryTableLineageParam{

    @ApiModelProperty("字段名称")
    private String columnName;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
