package com.dtstack.engine.master.parser;

import com.dtstack.engine.common.util.MathUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 小时解析器
 * Date: 2017/5/4
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ScheduleCronHourParser extends ScheduleCron {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleCronHourParser.class);

    private static final String CRON_FORMAT = "0 ${beginMin} ${beginHour}-${endHour}/${gapNum} * * ?";

    private static final String BEGIN_HOUR_KEY = "beginHour";

    private static final String END_HOUR_KEY = "endHour";

    private static final String BEGIN_MIN_KEY = "beginMin";

    private static final String GAP_HOUR_KEY = "gapHour";

    private int beginHour = 0;

    private int endHour = 0;

    private int gapNum = 1;//间隔数最小为1

    private int beginMinute = 0;

    @Override
    public String parse(Map<String, Object> param) {
        Preconditions.checkState(param.containsKey(BEGIN_HOUR_KEY), BEGIN_HOUR_KEY + "not be empty!");
        Preconditions.checkState(param.containsKey(END_HOUR_KEY), END_HOUR_KEY + "not be empty!");
        Preconditions.checkState(param.containsKey(GAP_HOUR_KEY), GAP_HOUR_KEY + "not be empty!");

        beginHour = MathUtil.getIntegerVal(param.get(BEGIN_HOUR_KEY));
        endHour = MathUtil.getIntegerVal(param.get(END_HOUR_KEY));
        gapNum = MathUtil.getIntegerVal(param.get(GAP_HOUR_KEY));
        beginMinute = MathUtil.getIntegerVal(param.get(BEGIN_MIN_KEY));

        if(beginHour < 0 || endHour > 23){
            logger.error("illegal schedule cron for period hour :{}", param);
            return null;
        }

        String cronStr = CRON_FORMAT.replace("${beginHour}", beginHour + "").replace("${endHour}", endHour + "")
                .replace("${gapNum}", gapNum + "").replace("${beginMin}", beginMinute + "");
        setCronStr(cronStr);
        return cronStr;
    }

    @Override
    public List<String> getTriggerTime(String specifyDate) throws ParseException {
        if(!checkSpecifyDayCanExe(specifyDate)){
            return Lists.newArrayList();
        }

        List<String> resultList = Lists.newArrayList();
        for(int i = beginHour;i <= endHour;){
            String triggerTime = specifyDate + " " + getTimeStr(i) + ":" + getTimeStr(beginMinute) + ":00";
            resultList.add(triggerTime);
            i += gapNum;
        }

        return resultList;
    }

    @Override
    public boolean checkSpecifyDayCanExe(String specifyDate) {
        return true;
    }

    public int getFirstHour(){
        return beginHour;
    }

    public int getLastHour(){

        if(gapNum < 1){
            logger.error("err schedule parser of hour, gapNum:{} less then 1, cronStr:{}", gapNum, getCronStr());
            return beginHour;
        }

        int result = beginHour;
        for( ; ; ){

            if((result + gapNum) > endHour){
                break;
            }

            result += gapNum;
        }
        return result;
    }


    public int getBeginHour() {
        return beginHour;
    }

    public void setBeginHour(int beginHour) {
        this.beginHour = beginHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getGapNum() {
        return gapNum;
    }

    public void setGapNum(int gapNum) {
        this.gapNum = gapNum;
    }

    public int getBeginMinute() {
        return beginMinute;
    }

    public void setBeginMinute(int beginMinute) {
        this.beginMinute = beginMinute;
    }
}
