package com.dtstack.batch.web.job.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel("实例运行sql信息")
public class BatchJobStartSqlVO extends DtInsightAuthParam {
    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(hidden = true)
    private String dtToken;

    @ApiModelProperty(hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(value = "任务Id", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "唯一标识", example = "标识", required = true)
    private String uniqueKey;

    @ApiModelProperty(value = "sql语句", example = "show tables;", required = true)
    private String sql;

    @ApiModelProperty(value = "任务前置执行语句", required = true)
    private List<Map> taskVariables;

    @ApiModelProperty(value = "是否是DDL语句", example = "false", required = true)
    private Integer isCheckDDL;

    @ApiModelProperty(value = "是否终止", example = "false", required = true)
    private Boolean isEnd;

    @ApiModelProperty(value = "任务参数", example = "1", required = true)
    private String taskParams;
}
