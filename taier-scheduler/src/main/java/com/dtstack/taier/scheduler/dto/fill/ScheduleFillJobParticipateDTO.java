package com.dtstack.taier.scheduler.dto.fill;

/**
 * @Auther: dazhi
 * @Date: 2021/9/9 5:34 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleFillJobParticipateDTO {

    /**
     * 补数据名称
     * 必填
     */
    private String fillName;

    /**
     * 开始日期：精确到日
     * 时间格式： yyyy-MM-dd
     * 必填
     */
    private String startDay;

    /**
     * 结束时间：精确到日
     * 时间格式：yyyy-MM-dd
     * 必填
     */
    private String endDay;

    /**
     * 每天补数据的开始时间
     * 时间格式： HH:mm
     */
    private String beginTime;

    /**
     * 每天补数据的结束时间
     * 时间格式：HH:mm
     */
    private String endTime;

    /**
     * 触发补数据事件的用户Id
     * 必填
     */
    private Long userId;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 补数据运行信息
     */
    private ScheduleFillDataInfoDTO fillDataInfo;

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

    public ScheduleFillDataInfoDTO getFillDataInfo() {
        return fillDataInfo;
    }

    public void setFillDataInfo(ScheduleFillDataInfoDTO fillDataInfo) {
        this.fillDataInfo = fillDataInfo;
    }
}
