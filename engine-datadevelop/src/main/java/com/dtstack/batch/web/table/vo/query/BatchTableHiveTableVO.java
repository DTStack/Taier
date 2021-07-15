package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("表基本信息")
public class BatchTableHiveTableVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "项目 ID", example = "1", required = true)
    private Long projectId;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "表名", example = "user", required = true)
    private String tableName;

    @ApiModelProperty(value = "表注释", example = "用户信息表")
    private String tableDesc;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "类目 ID", example = "3", required = true)
    private Long catalogueId;

    @ApiModelProperty(value = "项目名称", example = "dev", required = true)
    private String projectName;

    @ApiModelProperty(value = "表类别", example = "1", required = true)
    private Integer tableType;

    @ApiModelProperty(value = "是否脏数据表", example = "1", required = true)
    private Integer isDirtyTable;

    @ApiModelProperty(value = "生命周期  单位：天", example = "99", required = true)
    private Integer lifecycle;

    @ApiModelProperty(value = "路径", example = "/tmp/a/b", required = true)
    private String location;

    @ApiModelProperty(value = "分隔符", example = ",", required = true)
    private String delim;

    @ApiModelProperty(value = "存储类别", example = "textfile", required = true)
    private String storeType;

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "是否使用项目所有者")
    private Boolean useProjectOwner;
}
