package com.dtstack.batch.web.datasource.vo.result;

import com.dtstack.batch.web.datasource.vo.query.BatchDataSourceMigrationTransformFieldVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel("整库同步结果信息")
public class BatchDataSourceMigrationResultVO {
    @ApiModelProperty(value = "转换field列表")
    private List<BatchDataSourceMigrationTransformFieldVO> transformFields;

    @ApiModelProperty(value = "表列表")
    private List<String> tables;

    @ApiModelProperty(value = "同步任务列表")
    private List<BatchDataSourceMigrationTaskVO> migrationTasks;

    @ApiModelProperty(value = "创建用户的名称", example = "admin")
    private String createUserName;

    @ApiModelProperty(value = "创建时间", example = "2020-11-25 17:25:05")
    private String gmtCreateFormat;

    @ApiModelProperty(value = "任务数量", example = "11")
    private Integer taskCount;

    @ApiModelProperty(value = "并发配置", example = "11")
    private ParallelConfig parallelConfig;

    @ApiModelProperty(value = "数据源id", example = "1")
    private Long dataSourceId;

    @ApiModelProperty(value = "调度配置", example = "json格式")
    private String scheduleConf;

    @ApiModelProperty(value = "目标字段", example = "1")
    private Integer syncType;

    @ApiModelProperty(value = "日期标识字段", example = "gmt_modified")
    private String timeFieldIdentifier;

    @ApiModelProperty(value = "并发配置类型 1分批上传 2整批上传", example = "1")
    private Integer parallelType;

    @ApiModelProperty(value = "分批上传配置")
    private String parallelConf;

    @ApiModelProperty(value = "字段转换规则 json array", example = "test")
    private String transformFieldConfig;

    @ApiModelProperty(value = "创建用户id")
    private Long createUserId;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "app类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "整库同步id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "1")
    private Integer isDeleted = 0;

    @Data
    public static class ParallelConfig {
        @ApiModelProperty(value = "间隔时间", example = "2")
        private Integer hourTime;

        @ApiModelProperty(value = "表个数", example = "10")
        private Integer tableNum;
    }
}