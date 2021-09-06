package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel("表数据基本信息")
public class BatchTableSearchInfoVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "是否root用户", hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "token", hidden = true)
    private String dtToken;

    @ApiModelProperty(value = "是否admin用户", hidden = true)
    private Boolean isAdmin;

    @ApiModelProperty(value = "删除标识 0正常 1逻辑删除", hidden = true)
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "项目id", example = "3", required = true)
    @JsonProperty("pId")
    private Long pId;

    @ApiModelProperty(value = "表名", example = "user", required = true)
    private String tableName;

    @ApiModelProperty(value = "表类型")
    private Integer tableType;

    @ApiModelProperty(value = "字段名", example = "id")
    private String columnName;

    @ApiModelProperty(value = "类目id", example = "1", required = true)
    private Long catalogueId;

    @ApiModelProperty(value = "排序字段", example = "gmt_modified", required = true)
    private String sortColumn = "gmt_modified";

    @ApiModelProperty(value = "排序方式", example = "desc", required = true)
    private String sort = "desc";

    @ApiModelProperty(value = "前端页面tab类别", example = "1", required = true)
    private Integer listType = 0;

    @ApiModelProperty(value = "表授权状态", example = "1", required = true)
    private Integer permissionStatus;

    @ApiModelProperty(value = "分页 展示条数", example = "10", required = true)
    private Integer pageSize = 10;

    @ApiModelProperty(value = "分页 当前页", example = "1", required = true)
    private Integer pageIndex = 1;

    @ApiModelProperty(value = "任务id", example = "3", required = true)
    private Long taskId;

    @ApiModelProperty(value = "是否脏数据（表）", example = "0")
    private Integer isDirtyDataTable = 0;

    @ApiModelProperty(value = "模型层级")
    private String grade;

    @ApiModelProperty(value = "主题域")
    private String subject;

    @ApiModelProperty(value = "刷新频率")
    private String refreshRate;

    @ApiModelProperty(value = "增量类型")
    private String increType;

    @ApiModelProperty(value = "是否忽略 0:不忽略, 1:忽略", example = "1", required = true)
    private Integer ignore;

    @ApiModelProperty(value = "检测中心tab页类型", example = "1", required = true)
    private Integer type;

    @ApiModelProperty(value = "检测结果")
    private List<Integer> triggerType;

    @ApiModelProperty(value = "指定查询项目projectId", example = "1", required = true)
    private Long appointProjectId;

    @ApiModelProperty(value = "修改开始时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp mdfBeginTime;

    @ApiModelProperty(value = "修改结束时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp mdfEndTime;

    @ApiModelProperty(value = "表修改用户id", example = "1")
    private Long tableModifyUserId;

    @ApiModelProperty(value = "开始时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp endTime;

    @ApiModelProperty(value = "是否展示被删除数据 true 展示  false 不展示", example = "true", required = true)
    private Boolean showDeleted = false;

    @ApiModelProperty(value = "生命周期排序方式  1 升序 2 降序", example = "1")
    private Integer lifeDayOrder;

    @ApiModelProperty(value = "文件数量排序方式 0为升序 1为倒序", example = "1")
    private Integer fileCountOrder;

    @ApiModelProperty(value = "占用存储排序方式 0为升序 1为倒序", example = "1")
    private Integer tableSizeOrder;

    @ApiModelProperty(value = "责任人id", example = "1")
    private Long chargeUserId;

}
