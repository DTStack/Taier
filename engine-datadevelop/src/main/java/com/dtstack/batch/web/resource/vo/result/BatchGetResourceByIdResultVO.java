package com.dtstack.batch.web.resource.vo.result;

import com.dtstack.batch.web.user.vo.result.BatchUserBaseResultVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("获取资源返回信息")
public class BatchGetResourceByIdResultVO {

    @ApiModelProperty(value = "创建用户")
    private BatchUserBaseResultVO createUser;

    @ApiModelProperty(value = "修改用户")
    private BatchUserBaseResultVO modifyUser;

    @ApiModelProperty(value = "资源路径", example = "hdfs://ns1/rdos/batch/***")
    private String url;

    @ApiModelProperty(value = "资源描述", example = "我是描述")
    private String resourceDesc;

    @ApiModelProperty(value = "资源类型", example = "1")
    private Integer resourceType;

    @ApiModelProperty(value = "资源名称", example = "我是资源")
    private String resourceName;

    @ApiModelProperty(value = "源文件名称", example = "我是源文件名")
    private String originFileName;

    @ApiModelProperty(value = "创建用户 ID", example = "1")
    private Long createUserId;

    @ApiModelProperty(value = "修改用户的ID", example = "1")
    private Long modifyUserId;

    @ApiModelProperty(value = "父文件夹id", example = "1")
    private Long nodePid;

    @ApiModelProperty(value = "租户id", example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "项目id", example = "1")
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id", example = "1")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "app类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "ID", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;
}
