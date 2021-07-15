package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("表所属数据源信息")
public class BatchTableDataSourceVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "平台类别 RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)",
            example = "1", required = true)
    private Integer appType;

    @ApiModelProperty(value = "数据源名称", example = "default", required = true)
    private String dataName;

    @ApiModelProperty(value = "数据源描述", example = "默认数据源", required = true)
    private String dataDesc;

    @ApiModelProperty(value = "加密的数据源信息", example = "{}", required = true)
    private String dataJson;

    @ApiModelProperty(value = "数据源类型", example = "1", required = true)
    private Integer type;

    @ApiModelProperty(value = "新建用户id", example = "1", required = true)
    private Long createUserId;

    @ApiModelProperty(value = "修改用户id", example = "3", required = true)
    private Long modifyUserId;

    @ApiModelProperty(value = "是否启用", example = "0", required = true)
    private Integer active;

    @ApiModelProperty(value = "连接是否可用", example = "1", required = true)
    private Integer linkState;

    @ApiModelProperty(value = "是不是项目下的默认数据库", example = "1", required = true)
    private Integer isDefault;
}
