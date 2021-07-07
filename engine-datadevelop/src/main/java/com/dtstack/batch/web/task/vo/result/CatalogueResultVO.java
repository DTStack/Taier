package com.dtstack.batch.web.task.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("目录信息")
public class CatalogueResultVO {

    @ApiModelProperty(value = "文件夹名", example = "数据开发")
    private String nodeName;

    @ApiModelProperty(value = "父文件夹 ID", example = "23")
    private Long nodePid;

    @ApiModelProperty(value = "创建用户", example = "3")
    private Long createUserId;

    @ApiModelProperty(value = "目录层级", example = "3")
    private Integer level;

    @ApiModelProperty(value = "引擎类型", example = "1")
    private Integer engineType;

    @ApiModelProperty(value = "序号", example = "2")
    private Integer orderVal;

    @ApiModelProperty(value = "类目类别", example = "1")
    private Integer catalogueType;

    @ApiModelProperty(value = "ID", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "租户 ID", example = "")
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", example = "")
    private Long projectId;

    @ApiModelProperty(value = "uic 租户 ID", example = "3")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "平台类别", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "创建时间", example = "")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;

}
