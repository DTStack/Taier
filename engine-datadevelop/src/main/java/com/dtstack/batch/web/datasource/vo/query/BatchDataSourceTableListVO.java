package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("数据源表列表信息")
public class BatchDataSourceTableListVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "数据源id", example = "1", required = true)
    private Long sourceId;

    @ApiModelProperty(value = "查询的schema", example = "test")
    private String schema;

    @ApiModelProperty(value = "是否为系统用户", example = "false", required = true)
    private Boolean isSys = false;

    @ApiModelProperty(value = "模糊查询表名", example = "table_name")
    private String name;

    @ApiModelProperty(value = "是否获取所有表", example = "false")
    private Boolean isAll;

    @ApiModelProperty(value = "是否读取类型", example = "true")
    private Boolean isRead;
}
