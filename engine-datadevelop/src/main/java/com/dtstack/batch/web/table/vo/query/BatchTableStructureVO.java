package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("表基本信息")
public class BatchTableStructureVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "是否root用户", hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(value = "表数据源名称", example = "dev", required = true)
    private String originName;

    @ApiModelProperty(value = "表目标数据源名称", example = "dev1", required = true)
    private String targetName;

    @ApiModelProperty(value = "sql", example = "drop table user", required = true)
    private String sql;

    @ApiModelProperty(value = "sql", example = "drop table user", required = true)
    private Long sourceId;

    @ApiModelProperty(value = "如果存在是否删除", example = "false")
    private Boolean dropIfExist = false;

    @ApiModelProperty(value = "引擎类型", example = "1", required = true)
    private Integer engineType;

}
