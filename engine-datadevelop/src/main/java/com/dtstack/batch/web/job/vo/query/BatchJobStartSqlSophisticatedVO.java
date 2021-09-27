package com.dtstack.batch.web.job.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel("高级运行sparkSql从引擎执行逻辑信息")
public class BatchJobStartSqlSophisticatedVO extends DtInsightAuthParam {

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

    @ApiModelProperty(value = "任务前置执行语句", required = true)
    private List<Map> taskVariables;

    @ApiModelProperty(value = "sql列表", required = true)
    private List<String> sqlList;

    @ApiModelProperty(value = "是否是DDL语句", example = "false", required = true)
    private Integer isCheckDDL;

    @ApiModelProperty(value = "是否终止", example = "false", required = true)
    private Boolean isEnd;
}
