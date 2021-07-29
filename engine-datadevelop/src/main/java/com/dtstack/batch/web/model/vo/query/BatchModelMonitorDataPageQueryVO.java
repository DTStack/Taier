package com.dtstack.batch.web.model.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel("数据监视模型分页查询信息")
public class BatchModelMonitorDataPageQueryVO extends DtInsightAuthParam {

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden =  true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @JsonProperty("pId")
    @ApiModelProperty(value = "pId", example = "1")
    private Long pId;

    @ApiModelProperty(value = "表名", example = "表名")
    private String tableName;

    @ApiModelProperty(value = "表类型", example = "1")
    private Integer tableType;

    @ApiModelProperty(value = "字段名", example = "id")
    private String columnName;

    @ApiModelProperty(value = "目录ID", example = "1")
    private Long catalogueId;

    @ApiModelProperty(value = "列排序", example = "gmt_modified")
    private String sortColumn = "gmt_modified";

    @ApiModelProperty(value = "排序", example = "desc")
    private String sort = "desc";

    @ApiModelProperty(value = "列表类型: 0-全部,1-最近操作的，2-个人账户的，3-我管理的表，4-被授权的表，5-我收藏的表", example = "1")
    private Integer listType = 0;

    @ApiModelProperty(value = "授权状态 0-未授权，1-已授权,2-待审批,null-全部", example = "1")
    private Integer permissionStatus;

    @ApiModelProperty(value = "总页数", example = "10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer pageIndex = 1;

    @ApiModelProperty(value = "任务ID", example = "1")
    private Long taskId;

    @ApiModelProperty(value = "脏数据表", example = "0")
    private Integer isDirtyDataTable = 0;

    @ApiModelProperty(value = "等级", example = "等级")
    private String grade;

    @ApiModelProperty(value = "科目", example = "科目")
    private String subject;

    @ApiModelProperty(value = "刷新速率", example = "1")
    private String refreshRate;

    @ApiModelProperty(value = "incre类型", example = "1")
    private String increType;

    @ApiModelProperty(value = "忽略", example = "1")
    private Integer ignore;

    @ApiModelProperty(value = "类型", example = "1", required = true)
    private Integer type;

    @ApiModelProperty(value = "触发器类型")
    private List<Integer> triggerType;

    @ApiModelProperty(value = "指定项目ID", example = "1")
    private Long appointProjectId;

    @ApiModelProperty(value = "mdf开始时间", example = "2020-11-04 14:48:05")
    private Timestamp mdfBeginTime;

    @ApiModelProperty(value = "mdf结束时间", example = "2020-11-04 14:48:05")
    private Timestamp mdfEndTime;

    @ApiModelProperty(value = "操作表用户ID", example = "1")
    private Long tableModifyUserId;

    @ApiModelProperty(value = "开始时间", example = "2020-11-04 14:48:05")
    private Timestamp startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-11-04 14:48:05")
    private Timestamp endTime;

    @ApiModelProperty(value = "展示被删除的", example = "false")
    private Boolean showDeleted = false;

    @ApiModelProperty(value = "0为升序 1为倒序", example = "1")
    private Integer lifeDayOrder;

    @ApiModelProperty(value = "大小排序", example = "1")
    private Integer sizeOrder;
}
