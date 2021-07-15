package com.dtstack.batch.web.dirtydata.vo.result;

import com.dtstack.batch.web.task.vo.query.BatchScheduleTaskResultVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel("脏数据表结果信息")
public class BatchDirtyDataTableResultVO {
    @ApiModelProperty(value = "表id", example = "1")
    private Long id;

    @ApiModelProperty(value = "表名称", example = "table_name")
    private String tableName;

    @ApiModelProperty(value = "表类别", example = "1")
    private Integer tableType;

    @ApiModelProperty(value = "所属项目 ID", example = "3")
    private Long belongProjectId;

    @ApiModelProperty(value = "数据源 ID", example = "13")
    private Long dataSourceId;

    @ApiModelProperty(value = "项目名称", example = "dev开发")
    private String project;

    @ApiModelProperty(value = "项目别名", example = "开发环境")
    private String projectAlias;

    @ApiModelProperty(value = "所属db or schema 名称", example = "dev")
    private String dbName;

    @ApiModelProperty(value = "负责人", example = "admin")
    private String chargeUser;

    @ApiModelProperty(value = "创建时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "类目", example = "数据开发测试")
    private String catalogue;

    @ApiModelProperty(value = "类目 ID", example = "5")
    private Long catalogueId;

    @ApiModelProperty(value = "表描述", example = "测试table")
    private String tableDesc;

    @ApiModelProperty(value = "授权状态 0-未授权，1-已授权,2-待审批,null-全部", example = "1")
    private Integer permissionStatus;

    @ApiModelProperty(value = "生命周期，单位：day", example = "99")
    private Integer lifeDay;

    @ApiModelProperty(value = "表大小", example = "354")
    private String tableSize;

    @ApiModelProperty(value = "是否分区", example = "true")
    private Boolean isPartition = false;

    @ApiModelProperty(value = "表结构最近修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp lastDdlTime;

    @ApiModelProperty(value = "表数据最后修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp lastDmlTime;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "任务信息")
    private List<BatchScheduleTaskResultVO> tasks;

    @ApiModelProperty(value = "审核结果", example = "审核通过")
    private String checkResult;

    @ApiModelProperty(value = "列表类别")
    private Integer listType;

    @ApiModelProperty(value = "是否收藏", example = "1")
    private Integer isCollect;

    @ApiModelProperty(value = "层级", example = "1")
    private String grade;

    @ApiModelProperty(value = "主题域")
    private String subject;

    @ApiModelProperty(value = "刷新频率")
    private String refreshRate;

    @ApiModelProperty(value = "增量类型")
    private String increType;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted;

    @ApiModelProperty(value = "是否忽略", example = "0")
    private Integer isIgnore;

    @ApiModelProperty(value = "剩余时间")
    private Integer timeTeft;

    @ApiModelProperty(value = "审核通过时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp passTime;

    @ApiModelProperty(value = "存储位置")
    private String location;

    @ApiModelProperty(value = "存储类别", example = "1")
    private String storedType;

    @ApiModelProperty(value = "分隔符", example = ",")
    private String delim;
}