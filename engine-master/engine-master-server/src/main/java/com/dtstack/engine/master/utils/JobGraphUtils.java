package com.dtstack.engine.master.utils;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.scheduler.parser.*;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2021-01-19
 */
public class JobGraphUtils {

    public static String dtfFormatString = "yyyyMMddHHmmss";
    public static final String NORMAL_TASK_FLOW_ID = "0";
    private static final String CRON_TRIGGER_TYPE = "cronTrigger";

    private static final Logger logger = LoggerFactory.getLogger(JobGraphUtils.class);

    /**
     * 使用taskId cycTime appType 生成占位符Id
     * @param taskId
     * @param cycTime
     * @param appType
     * @return
     * @see  JobGraphUtils#doSetFlowJobIdForSubTasks(java.util.List, java.util.Map)
     */
    public static String buildFlowReplaceId(Long taskId, String cycTime, Integer appType) {
        return taskId + "_" + cycTime + "_" + appType ;
    }

    /**
     * <br>将工作流中的子任务flowJobId字段设置为所属工作流的实例id</br>
     * <br>用于BatchFlowWorkJobService中检查工作流子任务状态</br>
     *
     * @param jobList
     * @param flowJobId
     */
    public static void doSetFlowJobIdForSubTasks(List<ScheduleBatchJob> jobList, Map<String, String> flowJobId) {
        for (ScheduleBatchJob job : jobList) {
            String flowIdKey = job.getScheduleJob().getFlowJobId();
            job.getScheduleJob().setFlowJobId(flowJobId.getOrDefault(flowIdKey, NORMAL_TASK_FLOW_ID));
        }
    }

    public static void dealConcreteTime(List<String> triggerDayList, String triggerDay, String beginTime, String endTime) {
        DateTimeFormatter ddd = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        beginTime = triggerDay + " " + beginTime + ":00";
        endTime = triggerDay + " " + endTime + ":00";

        DateTime begin = DateTime.parse(beginTime, ddd);
        DateTime end = DateTime.parse(endTime, ddd);

        List<String> remove = Lists.newArrayList();
        for (String cur : triggerDayList) {
            if (DateTime.parse(cur, ddd).isBefore(begin)) {
                remove.add(cur);
            } else {
                break;
            }
        }

        Collections.reverse(triggerDayList);
        for (String cur : triggerDayList) {
            if (DateTime.parse(cur, ddd).isAfter(end)) {
                remove.add(cur);
            } else {
                break;
            }
        }
        triggerDayList.removeAll(remove);
    }

    /**
     * 根据cycTime计算bizTime
     *
     * @param cycTime cycTime格式必须是yyyyMMddHHmmss
     * @return
     */
    public static String generateBizDateFromCycTime(String cycTime) {
        DateTime cycDateTime = new DateTime(DateUtil.getTimestamp(cycTime, dtfFormatString));
        DateTime bizDate = cycDateTime.minusDays(1);
        return bizDate.toString(dtfFormatString);
    }

    //preKey_taskId_cyctime
    public static String parseCycTimeFromJobKey(String jobKey) {
        String[] strArr = jobKey.split("_");
        if (strArr.length < 1) {
            return null;
        }
        return strArr[strArr.length - 1];
    }

    public static EScheduleType parseScheduleTypeFromJobKey(String jobKey) {
        if (jobKey.startsWith(CRON_TRIGGER_TYPE)) {
            return EScheduleType.NORMAL_SCHEDULE;
        }

        return EScheduleType.FILL_DATA;
    }


    /**
     * 返回上一个执行周期的触发时间
     * 返回的时间格式： yyyyMMddHHmmss
     *
     * @param batchJobCycTime
     * @param cron
     * @return
     */
    public static String getPrePeriodJobTriggerDateStr(String batchJobCycTime, ScheduleCron cron) {
        DateTime triggerDate = new DateTime(DateUtil.getTimestamp(batchJobCycTime, dtfFormatString));
        Date preTriggerDate = getPreJob(triggerDate.toDate(), cron);
        if(null == preTriggerDate){
            return "";
        }
        return DateUtil.getFormattedDate(preTriggerDate.getTime(), dtfFormatString);
    }

    /**
     * @param triggerType
     * @param taskId
     * @param triggerTime 格式要求:yyyyMMddHHmmss
     * @return
     */
    public static String generateJobKey(String triggerType, long taskId, String triggerTime) {
        triggerTime = triggerTime.replace("-", "").replace(":", "").replace(" ", "");
        return triggerType + "_" + taskId + "_" + triggerTime;
    }


    /**
     * 获取上一个任务周期执行的时间
     *
     * @param currTriggerDate
     * @param cron
     * @return
     */
    public static Date getPreJob(Date currTriggerDate, ScheduleCron cron) {

        String[] timeFields = cron.getCronStr().split("\\s+");
        DateTime dateTime = new DateTime(currTriggerDate);

        if (timeFields.length != 6) {
            throw new RdosDefineException("illegal param of cron str:" + cron);
        }

        if (cron.getPeriodType() == ESchedulePeriodType.MONTH.getVal()) {
            //当前是当月执行的第一天的话则取上个周期的最后一天,其他的取当前的执行的前一个日期
            List<Integer> dayArr = getSortDayList(timeFields[3]);
            int currDay = dateTime.get(DateTimeFieldType.dayOfMonth());
            int index = dayArr.indexOf(currDay);

            if (index == -1) {//找不到该运行时间,不应该出现
                logger.error("can't find dayOfMonth:{} in cronStr:{}!", currDay, cron);
                return null;
            }

            if (index == 0) {//上个月的最后一天
                dateTime = dateTime.minusMonths(1);
                dateTime = dateTime.withDayOfMonth(dayArr.get(dayArr.size() - 1));
            } else {//上一天
                dateTime = dateTime.withDayOfMonth(dayArr.get(index - 1));
            }
        } else if (cron.getPeriodType() == ESchedulePeriodType.WEEK.getVal()) {
            //当前是当周执行的第一天的话则取上个周期的最后一天,其他的取当前的执行的前一个日期
            List<Integer> dayArr = getSortDayList(timeFields[5]);
            int currDay = dateTime.get(DateTimeFieldType.dayOfWeek());
            int index = dayArr.indexOf(currDay);
            if (index == -1) {//找不到该运行时间,不应该出现
                logger.error("can't find dayOfWeek:{} in cronStr:{}!", currDay, cron);
                return null;
            }

            if (index == 0) {//上周的最后一个执行天
                dateTime = dateTime.minusWeeks(1);
                dateTime = dateTime.withDayOfWeek(dayArr.get(dayArr.size() - 1));
            } else {//上一天
                dateTime = dateTime.withDayOfWeek(dayArr.get(index - 1));
            }

        } else if (cron.getPeriodType() == ESchedulePeriodType.DAY.getVal()) {
            //获取前一天的执行时间
            dateTime = dateTime.minusDays(1);
        } else if (cron.getPeriodType() == ESchedulePeriodType.HOUR.getVal()) {
            //如果是第一个小时--返回上一天的最后一个小时
            int firstHour = ((ScheduleCronHourParser) cron).getFirstHour();
            if (dateTime.getHourOfDay() == firstHour) {
                int lastHour = ((ScheduleCronHourParser) cron).getLastHour();
                dateTime = dateTime.minusDays(1);
                dateTime = dateTime.withHourOfDay(lastHour);
            } else {
                dateTime = dateTime.minusHours(((ScheduleCronHourParser) cron).getGapNum());
            }

        } else if (cron.getPeriodType() == ESchedulePeriodType.MIN.getVal()) {
            boolean isFirstOfDay = ((ScheduleCronMinParser) cron).isDayFirstTrigger(dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
            if (isFirstOfDay) {
                int last = ((ScheduleCronMinParser) cron).getLastTriggerMinutes();
                int hour = last / 60;
                int minute = last % 60;

                dateTime = dateTime.minusDays(1);
                dateTime = dateTime.withHourOfDay(hour).withMinuteOfHour(minute);
            } else {
                dateTime = dateTime.minusMinutes(((ScheduleCronMinParser) cron).getGapNum());
            }

        } else {
            throw new RdosDefineException("not support of ESchedulePeriodType:" + cron.getPeriodType());
        }

        return dateTime.toDate();
    }


    /**
     * 返回有序的队列
     *
     * @param dayListStr
     * @return
     */
    public static List<Integer> getSortDayList(String dayListStr) {

        String[] dayArr = dayListStr.split(",");
        List<Integer> sortList = Lists.newArrayList();
        for (String dayStr : dayArr) {
            sortList.add(MathUtil.getIntegerVal(dayStr.trim()));
        }

        Collections.sort(sortList);
        return sortList;
    }

    /**
     * 返回有序的队列
     *
     * @param dayListStr
     * @return
     */
    public static List<Integer> getSortTimeList(String dayListStr, String hourStr, String minuteStr, String secondStr) {

        int hour = Integer.parseInt(hourStr.trim());
        int minute = Integer.parseInt(minuteStr.trim());
        int second = Integer.parseInt(secondStr);

        int suffix = hour * 10000 + minute * 100 + second;
        String[] dayArr = dayListStr.split(",");
        List<Integer> sortList = Lists.newArrayList();
        for (String dayStr : dayArr) {
            int dayInteger = MathUtil.getIntegerVal(dayStr.trim()) * 1000000 + suffix;
            sortList.add(dayInteger);
        }

        Collections.sort(sortList);
        return sortList;
    }


    /**
     * 返回父任务执行时间最靠近当前执行时间的
     * 如果父子任务都是天则返回父任务当天的key
     */
    public static String getFatherLastJobBusinessDate(ScheduleJob childScheduleJob, ScheduleCron fatherCron, ScheduleCron childCron) {
        DateTime dateTime = new DateTime(DateUtil.getTimestamp(childScheduleJob.getCycTime(), dtfFormatString));
        String pCronstr = fatherCron.getCronStr();


        String[] timeFields = pCronstr.split("\\s+");
        if (timeFields.length != 6) {
            throw new RdosDefineException("illegal param of cron str:" + pCronstr);
        }

        if (fatherCron.getPeriodType() == ESchedulePeriodType.MONTH.getVal()) {
            dateTime = getCloseInDateTimeOfMonth(timeFields, dateTime);
        } else if (fatherCron.getPeriodType() == ESchedulePeriodType.WEEK.getVal()) {
            dateTime = getCloseInDateTimeOfWeek(timeFields, dateTime);
        } else if (fatherCron.getPeriodType() == ESchedulePeriodType.DAY.getVal() && childCron.getPeriodType() != ESchedulePeriodType.DAY.getVal()) {
            dateTime = getCloseInDateTimeOfDay(dateTime, (ScheduleCronDayParser) fatherCron, false);
        } else if (fatherCron.getPeriodType() == ESchedulePeriodType.DAY.getVal() && childCron.getPeriodType() == ESchedulePeriodType.DAY.getVal()) {
            dateTime = getCloseInDateTimeOfDay(dateTime, (ScheduleCronDayParser) fatherCron, true);
        } else if (fatherCron.getPeriodType() == ESchedulePeriodType.HOUR.getVal()) {
            dateTime = getCloseInDateTimeOfHour(dateTime, (ScheduleCronHourParser) fatherCron);
        } else if (fatherCron.getPeriodType() == ESchedulePeriodType.MIN.getVal()) {
            dateTime = getCloseInDateTimeOfMin(dateTime, (ScheduleCronMinParser) fatherCron);
        } else {
            throw new RuntimeException("not support period type of " + fatherCron.getPeriodType());
        }

        return DateUtil.getFormattedDate(dateTime.getMillis(), dtfFormatString);
    }

    public static DateTime getCloseInDateTimeOfMonth(String[] timeFields, DateTime dateTime) {
        String dayStr = timeFields[3];
        String hourStr = timeFields[2];
        String minStr = timeFields[1];
        String secStr = timeFields[0];
        List<Integer> timeList = getSortTimeList(dayStr, hourStr, minStr, secStr);
        int targetTime = dateTime.getDayOfMonth() * 1000000 + dateTime.getHourOfDay() * 10000 +
                dateTime.getMinuteOfHour() * 100 + dateTime.getSecondOfMinute();

        Integer dependencyTime = -1;
        for (int time : timeList) {
            if (targetTime < time) {
                break;
            }

            dependencyTime = time;
        }

        if (dependencyTime == -1) {//说明应该是上一个月的最后一个执行时间
            dependencyTime = timeList.get(timeList.size() - 1);
            dateTime = dateTime.minusMonths(1);
        }

        int day = dependencyTime / 1000000;
        dependencyTime = dependencyTime - day * 1000000;
        int hour = dependencyTime / 10000;
        dependencyTime = dependencyTime - hour * 10000;
        int min = dependencyTime / 100;
        int sec = dependencyTime % 100;

        dateTime = dateTime.withDayOfMonth(day).withTime(hour, min, sec, 0);
        return dateTime;
    }

    public static DateTime getCloseInDateTimeOfWeek(String[] timeFields, DateTime dateTime) {
        String dayStr = timeFields[5];
        String hourStr = timeFields[2];
        String minStr = timeFields[1];
        String secStr = timeFields[0];
        List<Integer> timeList = getSortTimeList(dayStr, hourStr, minStr, secStr);
        int targetTime = dateTime.dayOfWeek().get() * 1000000 + dateTime.getHourOfDay() * 10000 +
                dateTime.getMinuteOfHour() * 100 + dateTime.getSecondOfMinute();

        Integer dependencyTime = -1;
        for (int time : timeList) {
            if (targetTime < time) {
                break;
            }

            dependencyTime = time;
        }

        if (dependencyTime == -1) {//说明应该是上周的最后一个执行时间
            dependencyTime = timeList.get(timeList.size() - 1);
            dateTime = dateTime.minusWeeks(1);
        }

        int day = dependencyTime / 1000000;
        dependencyTime = dependencyTime - day * 1000000;
        int hour = dependencyTime / 10000;
        dependencyTime = dependencyTime - hour * 10000;
        int min = dependencyTime / 100;
        int sec = dependencyTime % 100;

        dateTime = dateTime.withDayOfWeek(day).withTime(hour, min, sec, 0);
        return dateTime;
    }


    public static DateTime getCloseInDateTimeOfDay(DateTime dateTime, ScheduleCronDayParser fatherCron, boolean isSamePeriod) {
        DateTime fatherCurrDayTime = dateTime.withTime(fatherCron.getHour(), fatherCron.getMinute(), 0, 0);
        if (fatherCurrDayTime.isAfter(dateTime) && !isSamePeriod) {//依赖昨天的
            fatherCurrDayTime = fatherCurrDayTime.minusDays(1);
        }

        return fatherCurrDayTime;
    }

    public static DateTime getCloseInDateTimeOfHour(DateTime dateTime, ScheduleCronHourParser fatherCron) {
        int childTime = dateTime.getHourOfDay() * 100 + dateTime.getMinuteOfHour();
        int triggerTime = -1;

        for (int i = fatherCron.getBeginHour(); i <= fatherCron.getEndHour(); ) {
            int fatherTime = i * 100 + fatherCron.getBeginMinute();

            if (fatherTime > childTime) {
                break;
            }

            triggerTime = fatherTime;
            i += fatherCron.getGapNum();
        }

        if (triggerTime == -1) {//获取昨天最后一个执行时间
            dateTime = dateTime.minusDays(1);
            int i = fatherCron.getBeginHour();
            for (; i <= fatherCron.getEndHour(); ) {
                i += fatherCron.getGapNum();
            }
            triggerTime = i * 100 + fatherCron.getBeginMinute();
        }

        int hour = triggerTime / 100;
        int min = triggerTime % 100;
        dateTime = dateTime.withTime(hour, min, 0, 0);

        return dateTime;
    }

    public static DateTime getCloseInDateTimeOfMin(DateTime dateTime, ScheduleCronMinParser fatherCron) {
        int childTime = dateTime.getHourOfDay() * 60 + dateTime.getMinuteOfHour();
        int triggerTime = -1;
        int begin = fatherCron.getBeginHour() * 60 + fatherCron.getBeginMin();
        int end = fatherCron.getEndHour() * 60 + fatherCron.getEndMin();

        if (end - begin < 0) {
            throw new RdosDefineException("illegal cron str :" + fatherCron.getCronStr());
        }

        for (int i = begin; i <= end; ) {
            if (i > childTime) {
                break;
            }
            triggerTime = i;
            i += fatherCron.getGapNum();
        }

        int hour = 0;
        int minute = 0;
        if (triggerTime == -1) {//获取昨天最后一个执行时间
            dateTime = dateTime.minusDays(1);
            int remainder = (end - begin) % fatherCron.getGapNum();
            //余数肯定不会超过59,所以直接减
            minute = fatherCron.getEndMin() - remainder;
            hour = fatherCron.getEndHour();

        } else {
            hour = triggerTime / 60;
            minute = triggerTime % 60;
        }
        dateTime = dateTime.withTime(hour, minute, 0, 0);

        return dateTime;
    }
}
