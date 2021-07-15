package com.dtstack.batch.web.project.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("项目分页查询返回信息")
public class BatchGetProjectListResultVO {

    @ApiModelProperty(value = "任务总数", example = "1")
    private Integer jobSum = 0;

    @ApiModelProperty(value = "表总数", example = "1")
    private Integer tableCount;

    @ApiModelProperty(value = "总大小", example = "1")
    private String totalSize;

    @ApiModelProperty(value = "任务数")
    private Map<String, Integer> taskCountMap;

    @ApiModelProperty(value = "置顶字段", example = "2020-12-23 11:42:14")
    private Timestamp stick;

    @ApiModelProperty(value = "置顶状态", example = "1")
    private Integer stickStatus;

    @ApiModelProperty(value = "支持引擎类型")
    private List<Integer> supportEngineType;

    @ApiModelProperty(value = "目录路径", example = "user/**")
    private String cataloguePath;
    
    @ApiModelProperty(value = "租户 ID", example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "项目描述", example = "这是描述")
    private String projectDesc;

    @ApiModelProperty(value = "项目别名", example = "这是别名")
    private String projectAlias;

    @ApiModelProperty(value = "项目标识", example = "标识")
    private String projectIdentifier;

    @ApiModelProperty(value = "项目名称", example = "若木的项目")
    private String projectName;

    @ApiModelProperty(value = "项目状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "创建项目用户 ID", example = "1L")
    private Long createUserId;

    @ApiModelProperty(value = "项目类型", example = "1")
    private Integer projectType;

    @ApiModelProperty(value = "生产项目 ID", example = "1L")
    private Long produceProjectId;

    @ApiModelProperty(value = "调度状态:0开, 1关", example = "0")
    private Integer scheduleStatus;

    @ApiModelProperty(value = "是否允许下载查询结果", example = "1-正常 0-禁用")
    private Integer isAllowDownload;

    @ApiModelProperty(value = "项目创建人", example = "admin")
    private String createUserName;

    @ApiModelProperty(value = "目录 ID", example = "1L")
    private Long catalogueId;

    @ApiModelProperty(value = "告警状态", example = "1")
    private Integer alarmStatus;

    @ApiModelProperty(value = "项目 ID", example = "0")
    private Long id;

    @ApiModelProperty(value = "创建时间", example = "2020-12-23 11:42:14")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-23 11:42:14")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "1")
    private Integer isDeleted = 0;
}
