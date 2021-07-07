package com.dtstack.batch.web.task.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel("任务信息")
public class BatchScheduleTaskShadeVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "修改开始时间", required = true)
    private Timestamp startGmtModified;

    @ApiModelProperty(value = "修改结束时间", required = true)
    private Timestamp endGmtModified;

    @ApiModelProperty(value = "")
    private List<Integer> periodTypeList;

    @ApiModelProperty(value = "任务类别list")
    private List<Integer> taskTypeList;

    @ApiModelProperty(value = "模糊查询名称")
    private String fuzzName;

    @ApiModelProperty(value = "查询类别", example = "1")
    private Integer searchType;

    @ApiModelProperty(value = "开始时间")
    private Timestamp startTime;

    @ApiModelProperty(value = "截止时间")
    private Timestamp endTime;

    @ApiModelProperty(value = "任务名称", example = "test")
    private String taskName;

    @ApiModelProperty(value = "每页展示条数", example = "10", required = true)
    private Integer pageSize = 10;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageIndex = 1;

    @ApiModelProperty(value = "排序字段排序方式", example = "desc", required = true)
    private String sort = "desc";

    @ApiModelProperty(value = "版本", example = "5")
    private Integer version;

    @ApiModelProperty(value = "是否发布到了生产环境", example = "1")
    private Long isPublishToProduce;

    @ApiModelProperty(value = "扩展信息", example = "test")
    private String extraInfo;

    @ApiModelProperty(value = "任务 ID", example = "1")
    private Long taskId;

    @ApiModelProperty(value = "batchJob执行的时候的vesion版本", example = "23")
    private Integer versionId;

    @ApiModelProperty(value = "任务名称(name)必填", example = "dev_test")
    private String name;

    @ApiModelProperty(value = "任务类型(taskType)必填", example = "0")
    private Integer taskType;

    @ApiModelProperty(value = "计算类型 0实时，1 离线", example = "1")
    private Integer computeType;

    @ApiModelProperty(value = "执行引擎类型 0 flink, 1 spark", example = "1")
    private Integer engineType;

    @ApiModelProperty(value = "sql 文本", example = "select * from test")
    private String sqlText;

    @ApiModelProperty(value = "任务参数", example = "job.executor:1")
    private String taskParams;

    @ApiModelProperty(value = "调度配置")
    private String scheduleConf;

    @ApiModelProperty(value = "周期类型", example = "1")
    private Integer periodType;

    @ApiModelProperty(value = "调度状态", example = "2")
    private Integer scheduleStatus;

    @ApiModelProperty(value = "启动:0 停止:1", example = "0")
    private Integer projectScheduleStatus;

    @ApiModelProperty(value = "提交状态", example = "0")
    private Integer submitStatus;

    @ApiModelProperty(value = "最后修改task的用户", example = "5")
    private Long modifyUserId;

    @ApiModelProperty(value = "新建task的用户", example = "3")
    private Long createUserId;

    @ApiModelProperty(value = "负责人id", example = "3")
    private Long ownerUserId;

    @ApiModelProperty(value = "节点 id", example = "13")
    private Long nodePid;

    @ApiModelProperty(value = "任务描述", example = "测试任务")
    private String taskDesc;

    @ApiModelProperty(value = "入口类")
    private String mainClass;

    @ApiModelProperty(value = "启动参数")
    private String exeArgs;

    @ApiModelProperty(value = "所属工作流id", example = "32")
    private Long flowId;

    @ApiModelProperty(value = "是否过期", example = "1")
    private Integer isExpire;

    @ApiModelProperty(value = "uic 租户 ID", example = "11")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "平台类型", example = "11")
    private Integer appType;

    @ApiModelProperty(value = "租户 ID", example = "3")
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", example = "5")
    private Long projectId;

    @ApiModelProperty(value = "主键 ID", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;

}
