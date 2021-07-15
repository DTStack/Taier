package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("表血缘信息")
public class BatchTableBloodInfoResultVO {

    @ApiModelProperty(value = "表 ID", example = "13")
    private Long tableId;

    @ApiModelProperty(value = "表 名称", example = "user")
    private String tableName;

    @ApiModelProperty(value = "目录", example = "开发")
    private String catalogue;

    @ApiModelProperty(value = "目录 ID", example = "1")
    private Long catalogueId = 0L;

    @ApiModelProperty(value = "创建用户", example = "admin")
    private String createUser;

    @ApiModelProperty(value = "创建 时间", example = "13")
    private Timestamp createTime;

    @ApiModelProperty(value = "备注", example = "test")
    private String comment;

    @ApiModelProperty(value = "上游表层数", example = "0")
    private Integer upstreamLevelNum = 0;

    @ApiModelProperty(value = "下游表层数", example = "1")
    private Integer downstreamLevelNum = 0;

    @ApiModelProperty(value = "上游表数量", example = "2")
    private Integer totalUpstreamNum = 0;

    @ApiModelProperty(value = "下游表数量", example = "1")
    private Integer totalDownstreamNum = 0;

    @ApiModelProperty(value = "直接上游表数量", example = "0")
    private Integer directUpstreamNum = 0;

    @ApiModelProperty(value = "直接下游表数量", example = "1")
    private Integer directDownstreamNum = 0;

    @ApiModelProperty(value = "数据源", example = "default")
    private String dataSource;

}
