package com.dtstack.batch.web.task.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;


@Data
@ApiModel("资源信息")
public class BatchResourceResultVO  {

    @ApiModelProperty(value = "资源路径", example = "/usr/tmp")
    private String url;

    @ApiModelProperty(value = "资源类型 1,jar 2 sql", example = "1")
    private Integer resourceType;

    @ApiModelProperty(value = "资源名称", example = "开发测试")
    private String resourceName;

    @ApiModelProperty(value = "源文件名", example = "测试")
    private String originFileName;

    @ApiModelProperty(value = "创建人 ID", example = "5")
    private Long createUserId;

    @ApiModelProperty(value = "修改人 ID", example = "7")
    private Long modifyUserId;

    @ApiModelProperty(value = "父节点 ID", example = "3")
    private Long nodePid;

    @ApiModelProperty(value = "资源备注", example = "test")
    private String resourceDesc;

    @ApiModelProperty(value = "数栈租户ID")
    private Long tenantId;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "UIC租户ID")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "平台类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "主键id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtModified;

}
