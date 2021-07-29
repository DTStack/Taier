package com.dtstack.batch.web.apply.vo.result;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;


@Data
@ApiModel("申请资源/权限-申请表结果信息")
public class BatchApplyTableResultVO {
    @ApiModelProperty(value = "表权限", example = "0")
    private Long tablePermission;

    @ApiModelProperty(value = "字段名称列表")
    private List<String> columnNames;

    @ApiModelProperty(value = "是否全部字段", example = "false")
    private Boolean fullColumn;

    @ApiModelProperty(value = "表的全部字段列表")
    private List<JSONObject> fullColumnList;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    @ApiModelProperty(value = "申请用户id")
    private Long userId;

    @ApiModelProperty(value = "申请的资源类型 0-hive表，1-函数,2-资源", example = "0")
    private Integer applyResourceType;

    @ApiModelProperty(value = "资源所属项目")
    private Long projectId;

    @ApiModelProperty(value = "资源id", example = "1")
    private Long resourceId;

    @ApiModelProperty(value = "资源名称", example = "test")
    private String resourceName;

    @ApiModelProperty(value = "资源申请期限", example = "3")
    private Integer day;

    @ApiModelProperty(value = "申请理由", example = "reason")
    private String applyReason;

    @ApiModelProperty(value = "申请状态 0-待审批，1-通过，2-不通过", example = "0")
    private Integer status;

    @ApiModelProperty(value = "处理人id", example = "0")
    private Long dealUserId;

    @ApiModelProperty(value = "回复内容", example = "reply")
    private String reply;

    @ApiModelProperty(value = "是否取消", example = "0")
    private Integer isCancel;

    @ApiModelProperty(value = "是否回收", example = "0")
    private Integer isRevoke;

    @ApiModelProperty(value = "申请id")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;
}
