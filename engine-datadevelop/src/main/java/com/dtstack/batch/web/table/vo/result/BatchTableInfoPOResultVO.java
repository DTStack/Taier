package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("表信息")
public class BatchTableInfoPOResultVO  {

    @ApiModelProperty(value = "表名称", example = "user")
    private String tableName;

    @ApiModelProperty(value = "表类别", example = "1")
    private Integer tableType;

    @ApiModelProperty(value = "创建表的用户 ID", example = "3")
    private Long userId;

    @ApiModelProperty(value = "表负责人", example = "5")
    private Long chargeUserId;

    @ApiModelProperty(value = "修改用户 ID", example = "15")
    private Long modifyUserId;

    @ApiModelProperty(value = "表大小", example = "341")
    private Long tableSize;

    @ApiModelProperty(value = "表大小更新时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp sizeUpdateTime;

    @ApiModelProperty(value = "类目 ID", example = "5")
    private Long catalogueId;

    @ApiModelProperty(value = "类目路径", example = "/数据开发/数据同步")
    private String path;

    @ApiModelProperty(value = "hdfs路径", example = "/usr/tmp")
    private String location;

    @ApiModelProperty(value = "列分隔符", example = ",")
    private String delim;

    @ApiModelProperty(value = "存储格式", example = "xls")
    private String storeType;

    @ApiModelProperty(value = "生命周期，单位：day", example = "99")
    private Integer lifeDay;

    @ApiModelProperty(value = "生命周期状态，0：未开始，1：存活，2：销毁，3：执行过程出现异常", example = "0")
    private Integer lifeStatus;

    @ApiModelProperty(value = "是否是脏数据表 0-否，1-是", example = "0")
    private Integer isDirtyDataTable = 0;

    @ApiModelProperty(value = "表结构最近修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp lastDdlTime;

    @ApiModelProperty(value = "表数据最后修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp lastDmlTime;

    @ApiModelProperty(value = "表描述", example = "基本信息表")
    private String tableDesc;

    @ApiModelProperty(value = "层级", example = "1")
    private String grade;

    @ApiModelProperty(value = "主题域", example = "")
    private String subject;

    @ApiModelProperty(value = "刷新频率", example = "")
    private String refreshRate;

    @ApiModelProperty(value = "增量类型", example = "")
    private String increType;

    @ApiModelProperty(value = "是否忽略", example = "0")
    private Integer isIgnore;

    @ApiModelProperty(value = "审核结果", example = "0")
    private String checkResult;

    @ApiModelProperty(value = "数栈租户ID")
    private Long tenantId;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "UIC租户ID")
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
