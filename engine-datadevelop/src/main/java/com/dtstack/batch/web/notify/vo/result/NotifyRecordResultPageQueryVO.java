package com.dtstack.batch.web.notify.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("通知记录分页返回信息")
public class NotifyRecordResultPageQueryVO {

    @ApiModelProperty(value = "通知记录ID", example = "1")
    private Long notifyRecordId;

    @ApiModelProperty(value = "用户ID", example = "1")
    private Long userId;

    @ApiModelProperty(value = "读取状态", example = "1")
    private Integer readStatus;

    @ApiModelProperty(value = "内容", example = "内容")
    private String content;

    @ApiModelProperty(value = "创建格式", example = "yyyyMMdd")
    private String gmtCreateFormat;

    @ApiModelProperty(value = "内容文本",example = "1")
    private Long contentId;

    @ApiModelProperty(value = "任务状态",example = "1")
    private Integer status;

    @ApiModelProperty(value = "租户ID",example = "0")
    private Long tenantId;

    @ApiModelProperty(value = "项目ID",example = "0")
    private Long projectId;

    @ApiModelProperty(value = "应用类型",example = "1")
    private Integer appType;

    @ApiModelProperty(value = "项目 ID", example = "0")
    private Long id;
}
