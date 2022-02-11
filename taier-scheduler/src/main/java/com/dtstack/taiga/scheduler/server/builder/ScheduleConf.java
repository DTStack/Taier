package com.dtstack.taiga.scheduler.server.builder;

import java.util.Date;

/**
 * @Auther: dazhi
 * @Date: 2022/1/4 11:31 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleConf {

    /**
     * 任务调度的开始时间
     */
    private Date beginDate;

    /**
     * 任务调度的结束时间
     */
    private Date endDate;

    /**
     * 调度类型 MIN(0), HOUR(1), DAY(2), WEEK(3), MONTH(4), CUSTOM(5);
     */
    private Integer periodType;

    /**
     * 任务重试次数
     */
    private Integer maxRetryNum;

    /**
     * 自依赖类型
     */
    private Integer selfReliance;

    /**
     * 开始小时 periodType = 0,1 必填
     */
    private Integer beginHour;

    /**
     * 结束小时  periodType = 0,1 必填
     */
    private Integer endHour;

    /**
     * 开始分钟  periodType = 0,1 必填
     */
    private Integer beginMin;

    /**
     * 结束分钟  periodType = 0 必填
     */
    private Integer endMin;

    /**
     * 间隔多少分分钟执行一次  periodType = 0 必填
     */
    private Integer gapMin;

    /**
     * 间隔多少小时执行一次  periodType = 1 必填
     */
    private Integer gapHour;

    /**
     * 具体执行时间 分钟数 periodType = 2,3,4 必填
     */
    private Integer min;

    /**
     * 具体执行时间 小时数 periodType = 2,3,4 必填
     */
    private Integer hour;

    /**
     * 每周执行时间 periodType = 3 必填
     */
    private String weekDay;

    /**
     * 每月执行天数 periodType = 4 必填
     */
    private String day;

    /**
     * corn表达式 periodType = 5时必填
     */
    private String corn;

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    public Integer getMaxRetryNum() {
        return maxRetryNum;
    }

    public void setMaxRetryNum(Integer maxRetryNum) {
        this.maxRetryNum = maxRetryNum;
    }

    public Integer getSelfReliance() {
        return selfReliance;
    }

    public void setSelfReliance(Integer selfReliance) {
        this.selfReliance = selfReliance;
    }

    public Integer getBeginHour() {
        return beginHour;
    }

    public void setBeginHour(Integer beginHour) {
        this.beginHour = beginHour;
    }

    public Integer getEndHour() {
        return endHour;
    }

    public void setEndHour(Integer endHour) {
        this.endHour = endHour;
    }

    public Integer getBeginMin() {
        return beginMin;
    }

    public void setBeginMin(Integer beginMin) {
        this.beginMin = beginMin;
    }

    public Integer getEndMin() {
        return endMin;
    }

    public void setEndMin(Integer endMin) {
        this.endMin = endMin;
    }

    public Integer getGapMin() {
        return gapMin;
    }

    public void setGapMin(Integer gapMin) {
        this.gapMin = gapMin;
    }

    public Integer getGapHour() {
        return gapHour;
    }

    public void setGapHour(Integer gapHour) {
        this.gapHour = gapHour;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getCorn() {
        return corn;
    }

    public void setCorn(String corn) {
        this.corn = corn;
    }
}
