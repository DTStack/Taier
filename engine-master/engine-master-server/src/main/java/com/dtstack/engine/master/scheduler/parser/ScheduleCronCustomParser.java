package com.dtstack.engine.master.scheduler.parser;

import com.dtstack.engine.common.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.support.CronSequenceGenerator;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

/**
 * @author xinge
 * cron表达式解析
 */
public class ScheduleCronCustomParser extends ScheduleCron{



    @Override
    public String parse(Map<String, Object> param) {
        String cron =(String) param.get("cron");
        if (StringUtils.isNotBlank(cron)){
            setCronStr(cron);
            return null;
        }
        return null;
    }

    @Override
    public List<String> getTriggerTime(String specifyDate) throws ParseException {
        // Spring解析cron
        CronSequenceGenerator generator = new CronSequenceGenerator(getCronStr());
        LocalDate startDate = DateUtil.parseDate(specifyDate, DateUtil.DATE_FORMAT).toInstant()
                .atZone(DateUtil.DEFAULT_ZONE).toLocalDate();
        // 开始日期
        Date startDateTime= new Date(startDate.atStartOfDay()
                .toInstant(DateUtil.DEFAULT_ZONE).toEpochMilli());
        // 结束日期
        Date endDateTime = new Date(startDate.plusDays(2).atStartOfDay()
                .toInstant(DateUtil.DEFAULT_ZONE).toEpochMilli());
        List<String> triggerTimeList = new ArrayList<>();
        Date curDateTime = generator.next(startDateTime);
        //  构建每个触发时间
        while (curDateTime.before(endDateTime) && curDateTime.after(startDateTime)){
            String curDateStr = DateUtil.getDate(curDateTime, DateUtil.STANDARD_DATETIME_FORMAT);
            triggerTimeList.add(curDateStr);
            curDateTime = generator.next(curDateTime);
        }
        return triggerTimeList;
    }

    @Override
    public boolean checkSpecifyDayCanExe(String specifyDate) throws ParseException {
        return true;
    }


}
