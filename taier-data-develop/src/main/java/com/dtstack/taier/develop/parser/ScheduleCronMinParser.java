/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.develop.parser;

import com.dtstack.taier.common.util.MathUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 分钟时间解析,默认开始分钟是0, 不允许修改
 * Date: 2017/5/4
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ScheduleCronMinParser extends ScheduleCron {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleCronMinParser.class);

    private static final String cronFormat = "0 0/${gapMin} ${beginHour}-${endHour} * * ?";

    private static final String BEGIN_HOUR_KEY = "beginHour";

    private static final String END_HOUR_KEY = "endHour";

    private static final String BEGIN_MIN_KEY = "beginMin";

    private static final String END_MIN_KEY = "endMin";

    private static final String GAP_NUM_KEY = "gapMin";

    private int beginHour = 0;

    private int endHour = 0;

    private int gapNum = 0;

    private int beginMin = 0; //默认开始的分钟

    private int endMin = 59;//默认结束的分钟

    @Override
    public String parse(Map<String, Object> param) {

        Preconditions.checkState(param.containsKey(BEGIN_HOUR_KEY), BEGIN_HOUR_KEY + "not be empty!");
        Preconditions.checkState(param.containsKey(END_HOUR_KEY), END_HOUR_KEY + "not be empty!");
        Preconditions.checkState(param.containsKey(GAP_NUM_KEY), GAP_NUM_KEY + "not be empty!");
        Preconditions.checkState(param.containsKey(BEGIN_MIN_KEY), BEGIN_MIN_KEY + "not be empty!");
        Preconditions.checkState(param.containsKey(END_MIN_KEY), END_MIN_KEY + "not be empty!");

        beginHour = MathUtil.getIntegerVal(param.get(BEGIN_HOUR_KEY));
        endHour = MathUtil.getIntegerVal(param.get(END_HOUR_KEY));
        beginMin = MathUtil.getIntegerVal(param.get(BEGIN_MIN_KEY));
        endMin = MathUtil.getIntegerVal(param.get(END_MIN_KEY));
        gapNum = MathUtil.getIntegerVal(param.get(GAP_NUM_KEY));

        String cronStr = cronFormat.replace("${gapMin}", gapNum + "")
                .replace("${beginHour}", beginHour + "").replace("${endHour}", endHour + "");
        setCronStr(cronStr);
        return cronStr;
    }

    @Override
    public List<String> getTriggerTime(String specifyDate) throws ParseException {
        if(!checkSpecifyDayCanExe(specifyDate)){
            return Lists.newArrayList();
        }

        List<String> resultList = Lists.newArrayList();
        int bMin = beginHour * 60 + beginMin;
        int eMin = endHour * 60 + endMin;
        for(int i=bMin; i<=eMin;){
            int currHour = i/60;
            int currMin = i%60;
            String triggerTime = specifyDate + " " + getTimeStr(currHour) + ":" + getTimeStr(currMin) + ":00";
            resultList.add(triggerTime);
            i += gapNum;
        }

        return resultList;
    }

    @Override
    public boolean checkSpecifyDayCanExe(String specifyDate) {
        return true;
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

    public int getBeginMin() {
        return beginMin;
    }

    public int getEndMin() {
        return endMin;
    }

    public void setBeginMin(int beginMin) {
        this.beginMin = beginMin;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }
}
