package com.dtstack.taiga.develop.vo.fill;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @Auther: dazhi
 * @Date: 2021/9/9 5:34 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleFillJobParticipateVO {

    /**
     * 补数据名称
     * 必填
     */
    @NotBlank(message = "fillName is not null")
    @Size(max = 256, message = "The length of the fill name (fillName) field ranges from 0 to 256, please control the length of the fill name field")
    @ApiModelProperty(value = "补数据名称",required = true)
    private String fillName;

    /**
     * 开始日期：精确到日
     * 时间格式： yyyy-MM-dd
     * 必填
     */
    @NotBlank(message = "startDay is not null")
    @Pattern(regexp = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$",
            message = "startDay need format yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期：精确到日 时间格式： yyyy-MM-dd",required = true)
    private String startDay;

    /**
     * 结束时间：精确到日
     * 时间格式：yyyy-MM-dd
     * 必填
     */
    @NotBlank(message = "endDay is not null")
    @Pattern(regexp = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$",
            message = "endDay need format yyyy-MM-dd")
    @ApiModelProperty(value = "结束时间：精确到日 时间格式： yyyy-MM-dd",required = true)
    private String endDay;

    /**
     * 每天补数据的开始时间
     * 时间格式： HH:mm
     */
    @Pattern(regexp = "^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$",
            message = "beginTime need format HH:mm")
    @ApiModelProperty(value = "结束时间：精确到日 时间格式： yyyy-MM-dd")
    private String beginTime;

    /**
     * 每天补数据的结束时间
     * 时间格式：HH:mm
     */
    @Pattern(regexp = "^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$",
            message = "beginTime need format HH:mm")
    @ApiModelProperty(value = "每天补数据的结束时间 时间格式：HH:mm")
    private String endTime;

    /**
     * 触发补数据事件的用户Id
     * 必填
     */
    @NotNull(message = "userId is not null")
    @ApiModelProperty(value = "触发补数据事件的用户Id" ,required = true)
    private Long userId;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id" ,required = true)
    private Long tenantId;

    /**
     * 补数据运行信息
     */
    @Valid
    private ScheduleFillDataInfoVO fillDataInfo;

    public String getFillName() {
        return fillName;
    }

    public void setFillName(String fillName) {
        this.fillName = fillName;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public ScheduleFillDataInfoVO getFillDataInfo() {
        return fillDataInfo;
    }

    public void setFillDataInfo(ScheduleFillDataInfoVO fillDataInfo) {
        this.fillDataInfo = fillDataInfo;
    }
}
