package com.dtstack.batch.web.catalogue.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("目录更新信息")
public class BatchCatalogueUpdateVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "文件类型 folder file", example = "file", required = true)
    private String type;

    @ApiModelProperty(value = "父目录")
    private BatchCatalogueUpdateVO parentCatalogue;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "节点名称", example = "a", required = true)
    private String nodeName;

    @ApiModelProperty(value = "节点父id", example = "3", required = true)
    private Long nodePid;

    @ApiModelProperty(value = "目录层级 0:一级 1:二级 n:n+1级", example = "1")
    private Integer level;

    @ApiModelProperty(value = "创建用户", example = "5")
    private Long createUserId;

    @ApiModelProperty(value = "engine类型", example = "1")
    private Integer engineType;

    @ApiModelProperty(value = "节点值", example = "1")
    private Integer orderVal;

    @ApiModelProperty(value = "目录类型", example = "1")
    private Integer catalogueType;

    @ApiModelProperty(value = "dtuic租户id", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "app类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "id")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除")
    private Integer isDeleted = 0;
}
