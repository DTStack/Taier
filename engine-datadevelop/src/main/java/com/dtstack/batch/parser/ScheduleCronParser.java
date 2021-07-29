package com.dtstack.batch.parser;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 分钟时间解析,默认开始分钟是0, 不允许修改
 * Date: 2017/5/4
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ScheduleCronParser extends ScheduleCron {

    private static final String CRON = "cron";

    @Override
    public String parse(Map<String, Object> param) {
        Preconditions.checkState(param.containsKey(CRON), CRON + "not be empty!");
        setCronStr(MapUtils.getString(param, CRON));
        return getCronStr();
    }

    @Override
    public List<String> getTriggerTime(String specifyDate) throws ParseException {
        return Lists.newArrayList();
    }

    @Override
    public boolean checkSpecifyDayCanExe(String specifyDate) {
        return true;
    }

}
