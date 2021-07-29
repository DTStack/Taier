package com.dtstack.batch.web.job.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * 返回这些jobId对应的父节点的jobMap返回信息
 *
 * @author Ruomu[ruomu@dtstack.com]
 * @Data 2021/1/8 10:10
 */
@Data
@ApiModel("返回这些jobId对应的父节点的jobMap返回信息")
public class BatchGetLabTaskRelationMapResultVO {

    @ApiModelProperty(value = "工作任务ID", example = "1")
    private String jobId;

    @ApiModelProperty(value = "任务 ID", example = "1")
    private Long taskId;

    @ApiModelProperty(value = "依赖类型", example = "1")
    private Integer dependencyType;

    @ApiModelProperty(value = "版本 ID", example = "1")
    private Integer versionId;

    @ApiModelProperty(value = "周期类型", example = "1")
    private Integer periodType;

    @ApiModelProperty(value = "fill ID", example = "1")
    private Long fillId;

    @ApiModelProperty(value = "提交日期", example = "2020-12-24 17:33:25")
    private Date submitTime;

    @ApiModelProperty(value = "最大重试次数", example = "3")
    private Integer maxRetryNum;

    @ApiModelProperty(value = "节点地址", example = "3")
    private String nodeAddress;

    @ApiModelProperty(value = "下一次时间", example = "2020-12-24 17:33:25")
    private String nextCycTime;

    @ApiModelProperty(value = "日志详情", example = "1")
    private String logInfo;

    @ApiModelProperty(value = "引擎日志", example = "1")
    private String engineLog;

    @ApiModelProperty(value = "任务类型")
    private List<Integer> taskTypes;

    @ApiModelProperty(value = "引擎任务ID", example = "1")
    private String engineJobId;

    @ApiModelProperty(value = "执行引擎任务id", example = "2")
    private String applicationId;

    @ApiModelProperty(value = "组件详情ID", example = "1")
    private Long pluginInfoId;

    @ApiModelProperty(value = "计算类型", example = "1")
    private Integer computeType;

    @ApiModelProperty(value = "源类型", example = "1")
    private Integer sourceType;

    @ApiModelProperty(value = "重试任务参数", example = "1")
    private String retryTaskParams;

    @ApiModelProperty(value = "相位状态", example = "1")
    private Integer phaseStatus;

    @ApiModelProperty(value = "是否强制", example = "true")
    private Boolean isForce;

    @ApiModelProperty(value = "任务提交用户", example = "1")
    private String submitUserName;

    @ApiModelProperty(value = "任务实例主键", example = "1")
    private String jobKey;

    @ApiModelProperty(value = "任务实例名称", example = "name")
    private String jobName;

    @ApiModelProperty(value = "状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "创建用户 ID", example = "5")
    private Long createUserId;

    @ApiModelProperty(value = "0正常调度 1补数据 2临时运行", example = "1")
    private Integer type;

    @ApiModelProperty(value = "业务日期", example = "yyyymmddhhmmss")
    private String businessDate;

    @ApiModelProperty(value = "任务调度时间", example = "yyyymmddhhmmss")
    private String cycTime;

    @ApiModelProperty(value = "开始时间", example = "1525942614000")
    private Timestamp execStartTime;

    @ApiModelProperty(value = "结束时间", example = "1525942614000")
    private Timestamp execEndTime;

    @ApiModelProperty(value = "当前执行时间单位为s", example = "s")
    private String execTime;

    @ApiModelProperty(value = "工作流ID", example = "3")
    private String flowJobId;

    @ApiModelProperty(value = "重试次数", example = "3")
    private Integer retryNum;

    @ApiModelProperty(value = "是否重启", example = "1")
    private Integer isRestart;

    @ApiModelProperty(value = "任务类型", example = "3")
    private Integer taskType;

    @ApiModelProperty(value = "租户ID", example = "3")
    private Long tenantId;

    @ApiModelProperty(value = "项目ID", example = "1")
    private Long projectId;

    @ApiModelProperty(value = "uic 租户 ID", example = "13")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "平台类别", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "是否删除", example = "dtstack")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "主键id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-12-24 17:33:25")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-24 17:33:25")
    private Timestamp gmtModified;

}
