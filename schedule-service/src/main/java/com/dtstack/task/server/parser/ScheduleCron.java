package com.dtstack.task.server.parser;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * [秒] [分] [时] [天] [月] [周]
 * Date: 2017/5/4
 * Company: www.dtstack.com
 * @author xuchao
 */

public abstract class ScheduleCron implements IScheduleCronParser{

    private static final Logger logger = LoggerFactory.getLogger(ScheduleCron.class);

    public static final DateTimeFormatter dayFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    private Date beginDate;

    private Date endDate;

    private String cronStr;

    private int periodType;

    /**FIXME 由于历史原因该字段含义已经不是字段表面的意思，当前标识依赖的类型*/
    private Integer selfReliance = 0;//默认不为自依赖

    private int maxRetryNum;

    /**
     * 获得指定日期的触发时间
     * @param specifyDate 格式: yyyy-MM-dd
     * @return yyyy-MM-dd HH:mm:ss
     */
    public abstract List<String> getTriggerTime(String specifyDate) throws ParseException;

    /**
     * 判断指定的天是否有可以执行
     * @param specifyDate
     * @return
     * @throws ParseException
     */
    public abstract boolean checkSpecifyDayCanExe(String specifyDate) throws ParseException;

    public String getTimeStr(int timeNum){
        String timeStr = timeNum >= 10 ? timeNum + "" : "0" + timeNum;
        return timeStr;
    }

    public String getCronStr() {
        return cronStr;
    }

    public void setCronStr(String cronStr) {
        this.cronStr = cronStr;
    }

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

    public int getPeriodType() {
        return periodType;
    }

    public void setPeriodType(int periodType) {
        this.periodType = periodType;
    }

    public Integer getSelfReliance() {
        return selfReliance;
    }

    public void setSelfReliance(Integer selfReliance) {
        this.selfReliance = selfReliance;
    }

    public int getMaxRetryNum() {
        return maxRetryNum;
    }

    public void setMaxRetryNum(int maxRetryNum) {
        this.maxRetryNum = maxRetryNum;
    }
}
