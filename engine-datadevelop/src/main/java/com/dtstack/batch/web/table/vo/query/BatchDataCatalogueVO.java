package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("目录信息")
public class BatchDataCatalogueVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "节点名称", example = "a", required = true)
    private String nodeName;

    @ApiModelProperty(value = "节点父id", example = "3", required = true)
    private Long nodePid;

    @ApiModelProperty(value = "节点值", example = "1", required = true)
    private Integer orderVal;

    @ApiModelProperty(value = "节点路径", example = "2/6/9", required = true)
    private String path;

    @ApiModelProperty(value = "目录层级 0:一级 1:二级 n:n+1级", example = "1", required = true)
    private Integer level;

    @ApiModelProperty(value = "创建用户", hidden = true)
    private Long createUserId;

    @ApiModelProperty(value = "id", hidden = true)
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", hidden = true)
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", hidden = true)
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", hidden = true)
    private Integer isDeleted = 0;


}
