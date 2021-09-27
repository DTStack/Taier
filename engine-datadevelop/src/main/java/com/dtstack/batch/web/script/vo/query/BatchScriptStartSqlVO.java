package com.dtstack.batch.web.script.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("运行sql脚本信息")
public class BatchScriptStartSqlVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "脚本ID", example = "3", required = true)
    private Long scriptId;

    @ApiModelProperty(value = "唯一键", example = "3_1609330883193", required = true)
    private String uniqueKey;

    @ApiModelProperty(value = "sql脚本", example = "show tables;", required = true)
    private String sql;

    @ApiModelProperty(value = "是否是DDL", example = "0", required = true)
    private Integer isCheckDDL;

    @ApiModelProperty(hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private String dtToken;
}
