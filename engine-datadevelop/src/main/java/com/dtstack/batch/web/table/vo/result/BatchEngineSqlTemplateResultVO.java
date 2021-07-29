package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;


@Data
@ApiModel("sql模版信息")
public class BatchEngineSqlTemplateResultVO {

    @ApiModelProperty(value = "执行引擎类型", example = "1")
    private Integer engineType;

    @ApiModelProperty(value = "sql 文本")
    private String params;

    @ApiModelProperty(value = "表类型 1 hive表 2 libra表", example = "1")
    private Integer tableType;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "主键id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtModified;

}
