package com.dtstack.batch.web.resource.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("添加资源信息")
public class BatchResourceAddVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "资源名称")
    private String resourceName;

    @ApiModelProperty(value = "项目ID", hidden = true)
    private Long projectId = 1L;

    @ApiModelProperty(value = "租户ID", hidden = true)
    private Long tenantId =1L;

    @ApiModelProperty(value = "用户ID", hidden = true)
    private Long userId = 1L;

    @ApiModelProperty(value = "资源ID")
    private Long id = 0L;

    @ApiModelProperty(value = "资源描述")
    private String resourceDesc;

    @ApiModelProperty(value = "资源存放的目录ID")
    private Long nodePid;

    @ApiModelProperty(value = "资源类型", required = true)
    private Integer resourceType;

    @ApiModelProperty(value = "资源原始名称", hidden = true)
    private String originalFilename;

    @ApiModelProperty(value = "资源临时存放地址", hidden = true)
    private String tmpPath;

    @ApiModelProperty(value = "UIC租户ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(hidden = true)
    private String dtToken;

    @ApiModelProperty(value = "默认项目ID")
    private Long defaultProjectId;

    @ApiModelProperty(value = "新建资源的用户ID", required = true)
    private Long createUserId;

    @ApiModelProperty(value = "修改资源的用户ID", required = true)
    private Long modifyUserId;

    @ApiModelProperty(value = "项目代号")
    private String productCode;

    @ApiModelProperty(value = "计算类型 0实时，1 离线")
    private Integer computeType;

}
