package com.dtstack.batch.web.apply.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;


@Data
@ApiModel("申请资源/权限-查询结果信息")
public class BatchApplyQueryResultVO {
    @ApiModelProperty(value = "申请id", example = "1")
    private Long applyId;

    @ApiModelProperty(value = "资源id", example = "1")
    private Long resourceId;

    @ApiModelProperty(value = "资源名称", example = "name")
    private String resourceName;

    @ApiModelProperty(value = "项目名称", example = "project_name")
    private String projectName;

    @ApiModelProperty(value = "项目别名", example = "project_alia")
    private String projectAlias;

    @ApiModelProperty(value = "资源类型", example = "1")
    private Integer resourceType;

    @ApiModelProperty(value = "申请时间", example = "2020-12-23 11:42:14")
    private Timestamp applyTime;

    @ApiModelProperty(value = "申请用户")
    private String applyUser;

    @ApiModelProperty(value = "申请状态", example = "1")
    private Integer applyStatus;

    @ApiModelProperty(value = "生命周期", example = "999")
    private Integer day;

    @ApiModelProperty(value = "申请原因", example = "原因")
    private String applyReason;

    @ApiModelProperty(value = "回复", example = "1")
    private String reply;

    @ApiModelProperty(value = "处理人", example = "aka")
    private String dealUser;

    @ApiModelProperty(value = "是否取消", example = "0")
    private Integer isCancel;

    @ApiModelProperty(value = "是否撤回", example = "0")
    private Integer isRevoke;

    @ApiModelProperty(value = "资源是否被删除", example = "0")
    private Integer resourceIsDeleted;
}
