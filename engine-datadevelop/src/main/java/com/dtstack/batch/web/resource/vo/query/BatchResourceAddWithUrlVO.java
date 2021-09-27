package com.dtstack.batch.web.resource.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("添加资源路径信息")
public class BatchResourceAddWithUrlVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "UIC 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "资源 ID", hidden = true)
    private Long id;

    @ApiModelProperty(value = "资源名称", example = "我是资源", required = true)
    private String resourceName;

    @ApiModelProperty(value = "源文件名称", example = "我是源文件名", required = true)
    private String originFileName;

    @ApiModelProperty(value = "资源路径", example = "hdfs://ns1/rdos/batch/***", required = true)
    private String url;

    @ApiModelProperty(value = "资源描述", example = "我是描述", required = true)
    private String resourceDesc;

    @ApiModelProperty(value = "资源类型", example = "1", required = true)
    private Integer resourceType;

    @ApiModelProperty(value = "目录ID", example = "1L", required = true)
    private Long nodePid;
}
