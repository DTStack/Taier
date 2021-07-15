package com.dtstack.batch.web.job.vo.result;

import com.dtstack.batch.web.task.vo.query.BatchScheduleTaskResultVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel("调度任务返回结果")
public class BatchScheduleJobResultVO {

    @ApiModelProperty(value = "批任务")
    protected BatchScheduleTaskResultVO batchTask;

    @ApiModelProperty(value = "租户名称", example = "DTStack租户")
    private String tenantName;

    @ApiModelProperty(value = "项目名称", example = "dev")
    private String projectName;

    @ApiModelProperty(value = "任务实例ID", example = "1")
    private String jobId;

    @ApiModelProperty(value = "任务实例主键", example = "1")
    private String jobKey;

    @ApiModelProperty(value = "任务实例名称", example = "name")
    private String jobName;

    @ApiModelProperty(value = "状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "任务ID", example = "name")
    private Long taskId;

    @ApiModelProperty(value = "创建用户 ID", example = "5")
    private Long createUserId;

    @ApiModelProperty(value = "所有者用户 ID", example = "5")
    private Long ownerUserId;

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

    @ApiModelProperty(value = "开始时间", example = "2020-12-24 17:33:26")
    private String execStartDate;

    @ApiModelProperty(value = "结束时间", example = "2020-12-24 17:33:26")
    private String execEndDate;

    @ApiModelProperty(value = "任务周期ID", example = "1")
    private Integer taskPeriodId;

    @ApiModelProperty(value = "任务周期类型", example = "1")
    protected String taskPeriodType;

    @ApiModelProperty(value = "调度任务")
    private List<BatchScheduleJobResultVO> jobVOS;

    @ApiModelProperty(value = "批引擎任务")
    protected BatchScheduleEngineJobResultVO batchEngineJob;

    @ApiModelProperty(value = "调度任务节点")
    private BatchScheduleJobResultVO subNodes;

    @ApiModelProperty(value = "工作流ID", example = "3")
    private String flowJobId;

    @ApiModelProperty(value = "重试次数", example = "3")
    private Integer retryNum;

    @ApiModelProperty(value = "关联任务")
    private List<BatchScheduleJobResultVO> relatedJobs;

    @ApiModelProperty(value = "是否是脏数据", example = "1")
    private Integer isDirty;

    @ApiModelProperty(value = "是否重启", example = "1")
    private Integer isRestart;

    @ApiModelProperty(value = "是否是group任务", example = "true")
    protected Boolean isGroupTask = false;

    @ApiModelProperty(value = "版本", example = "3")
    private Integer version;

    @ApiModelProperty(value = "任务类型", example = "3")
    private Integer taskType;

    @ApiModelProperty(value = "租户ID", example = "3")
    private Long tenantId;

    @ApiModelProperty(value = "项目ID", example = "1")
    private Long projectId;

    @ApiModelProperty(value = "是否删除", example = "dtstack")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "主键id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-12-24 17:33:25")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-24 17:33:25")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "产品类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "是否存在开启的质量任务")
    private Boolean existsOnRule;
}
