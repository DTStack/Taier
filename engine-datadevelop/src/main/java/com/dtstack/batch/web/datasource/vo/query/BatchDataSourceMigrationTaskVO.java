package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel("整库同步-同步信息")
public class BatchDataSourceMigrationTaskVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "转换field列表", required = true)
    private List<BatchDataSourceMigrationTransformFieldVO> transformFields;

    @ApiModelProperty(value = "表列表", required = true)
    private List<String> tables;

    @ApiModelProperty(value = "同步任务列表")
    private List<BatchDataSourceMigrationTaskBaseVO> migrationTasks;

    @ApiModelProperty(value = "创建用户的名称", example = "admin")
    private String createUserName;

    @ApiModelProperty(value = "创建时间", example = "2020-11-25 17:25:05")
    private String gmtCreateFormat;

    @ApiModelProperty(value = "任务数量", example = "11")
    private Integer taskCount;

    @ApiModelProperty(value = "并发配置", example = "11", required = true)
    private BatchDataSourceMigrationVO.ParallelConfig parallelConfig;

    @ApiModelProperty(value = "数据源id", example = "1", required = true)
    private Long dataSourceId;

    @ApiModelProperty(value = "调度配置", example = "json格式", required = true)
    private String scheduleConf;

    @ApiModelProperty(value = "同步类别 1：增量 2：全量", example = "1", required = true)
    private Integer syncType;

    @ApiModelProperty(value = "日期标识字段", example = "gmt_modified", required = true)
    private String timeFieldIdentifier;

    @ApiModelProperty(value = "并发配置类型 1分批上传 2整批上传", example = "1", required = true)
    private Integer parallelType;

    @ApiModelProperty(value = "分批上传配置")
    private String parallelConf;

    @ApiModelProperty(value = "字段转换规则 json array", example = "test")
    private String transformFieldConfig;

    @ApiModelProperty(value = "创建用户id", hidden = true)
    private Long createUserId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "app类型 RDOS(1) DQ(2), API(3) TAG(4) MAP(5) CONSOLE(6) STREAM(7) DATASCIENCE(8)", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "整库同步id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "1")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "是否位root", example = "false")
    private Boolean isRoot;

    @ApiModelProperty(value = "engine类型", example = "1", required = true)
    private Integer engineType;

    @ApiModelProperty(value = "dtToken信息", hidden = true)
    private String dtToken;

    @Data
    public static class ParallelConfig {
        @ApiModelProperty(value = "间隔时间", example = "2", required = true)
        private Integer hourTime;

        @ApiModelProperty(value = "表个数", example = "10", required = true)
        private Integer tableNum;
    }
}
