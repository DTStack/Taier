package com.dtstack.batch.web.notify.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel("告警记录返回结果信息")
public class NotifyRecordResultVO {

    @ApiModelProperty(value = "读取状态",example = "1")
    private Integer readStatus;

    @ApiModelProperty(value = "接收用户")
    private List<Long> receivers;

    @ApiModelProperty(value = "发送类型")
    private List<Integer> sendTypes;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "创建格式", example = "yyyyMMdd")
    private String gmtCreateFormat;

    @ApiModelProperty(value = "通知id",example = "1")
    private Long notifyId;

    @ApiModelProperty(value = "内容文本",example = "1")
    private Long contentId;

    @ApiModelProperty(value = "任务状态",example = "1")
    private Integer status;

    @ApiModelProperty(value = "批处理调度的时间",example = "2020-10-10")
    private String cycTime;

    @ApiModelProperty(value = "租户ID",example = "0")
    private Long tenantId;

    @ApiModelProperty(value = "项目ID",example = "0")
    private Long projectId;

    @ApiModelProperty(value = "应用类型",example = "1")
    private Integer appType;

    @ApiModelProperty(value = "dtuic用户id",example = "0")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "项目 ID", example = "0")
    private Long id;

    @ApiModelProperty(value = "创建时间", example = "2020-12-23 11:42:14")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-23 11:42:14")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "1")
    private Integer isDeleted = 0;
}
