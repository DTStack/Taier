package com.dtstack.batch.web.task.vo.result;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel("任务版本详细信息")
public class BatchTaskVersionDetailResultVO {

    @ApiModelProperty(value = "用户名", example = "测试用户")
    private String userName;

    @ApiModelProperty(value = "依赖任务名称")
    private List<String> dependencyTaskNames;

    @ApiModelProperty(value = "依赖任务信息")
    private JSONObject dependencyTasks;

    @ApiModelProperty(value = "任务 ID", example = "3")
    private Long taskId;

    @ApiModelProperty(value = "sql")
    private String originSql;

    @ApiModelProperty(value = "sql 文本", example = "use dev")
    private String sqlText;

    @ApiModelProperty(value = "发布备注", example = "test")
    private String publishDesc;

    @ApiModelProperty(value = "新建task的用户", example = "35")
    private Long createUserId;

    @ApiModelProperty(value = "task版本", example = "23")
    private Integer version;

    @ApiModelProperty(value = "环境参数", example = "")
    private String taskParams;

    @ApiModelProperty(value = "调度信息", example = "")
    private String scheduleConf;

    @ApiModelProperty(value = "调度状态", example = "1")
    private Integer scheduleStatus;

    @ApiModelProperty(value = "依赖的任务id", example = "23")
    private String dependencyTaskIds;

    @ApiModelProperty(value = "数栈租户 ID", example = "3")
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", example = "3")
    private Long projectId;

    @ApiModelProperty(value = "UIC 租户 ID", example = "111")
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
