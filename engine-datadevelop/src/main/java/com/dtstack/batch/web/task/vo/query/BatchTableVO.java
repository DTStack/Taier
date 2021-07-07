package com.dtstack.batch.web.task.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("表基本信息")
public class BatchTableVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "项目 ID", example = "1", required = true)
    private Long projectId;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "表名", example = "user", required = true)
    private String tableName;

    @ApiModelProperty(value = "表注释", example = "用户信息表")
    private String tableDesc;

    @ApiModelProperty(value = "表字段")
    private List<BatchColumnVO> columns;

    @ApiModelProperty(value = "表添加的字段", required = true)
    private List<BatchColumnVO> addColumns;

    @ApiModelProperty(value = "分区表字段")
    private List<BatchColumnVO> partitionKeys;

    @ApiModelProperty(value = "分隔符", example = ",", required = true)
    private String delim;

    @ApiModelProperty(value = "路径", example = "/tmp/a/b", required = true)
    private String location;

    @ApiModelProperty(value = "生命周期  单位：天", example = "99", required = true)
    private Integer lifeDay;

    @ApiModelProperty(value = "类目 ID", example = "1", required = true)
    private Long catalogueId;

    @ApiModelProperty(value = "存储类别", example = "textfile", required = true)
    private String storedType;

    @ApiModelProperty(value = "表 ID", example = "3", required = true)
    private Long tableId;

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "表类别", example = "1", required = true)
    private Integer tableType;

}
