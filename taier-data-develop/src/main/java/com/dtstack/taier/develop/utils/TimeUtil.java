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

package com.dtstack.taier.develop.utils;

import com.dtstack.taier.common.enums.ETimeCarry;
import com.dtstack.taier.develop.dto.devlop.TimespanVO;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

    // 时间跨度匹配正则
    private static final String TIMESPAN_STR_REGEX = "((?<num>\\d+)(?<type>[smhdwy]))+";

    // 时间跨度切割正则
    private static final String TIMESPAN_STR_SPLIT = "(?<num>\\d+)(?<type>[smhdwy])";

    private static final Pattern TIMESPAN_STR_REGEX_PATTERN = Pattern.compile(TIMESPAN_STR_REGEX);

    private static final Pattern TIMESPAN_STR_SPLIT_PATTERN = Pattern.compile(TIMESPAN_STR_SPLIT);

    private static final Integer YEAR_TO_DAY = 365;


    /**
     * 获取开始时间
     *
     * @param endTime  结束时间
     * @param timespan 时间跨度
     * @return 开始时间
     */
    public static Long getStartTime(Long endTime, Long timespan) {
        return endTime - timespan;
    }

    /**
     * 格式化 时间跨度
     * @param timespan 未格式化的时间跨度
     * @return 格式化后的时间跨度
     */
    public static TimespanVO formatTimespan(String timespan) {
        if (StringUtils.isBlank(timespan)) {
            TimespanVO timespanVO = new TimespanVO();
            timespanVO.setCorrect(false);
            timespanVO.setMsg("timespan is not null...");
            return timespanVO;
        }
        // 格式不匹配
        if (!TIMESPAN_STR_REGEX_PATTERN.matcher(timespan.trim()).matches()) {
            TimespanVO timespanVO = new TimespanVO();
            timespanVO.setCorrect(false);
            timespanVO.setMsg("timespan format error. examples: 15m、15d、1d1m、1d1m1s...");
            return timespanVO;
        }
        Matcher timespanMatcher = TIMESPAN_STR_SPLIT_PATTERN.matcher(timespan.trim());
        // timeFlag 从开始往下递减，如 1d1s,1d1h1m1s 如果是 1s1d 则不符合规则
        int timeFlag = Integer.MAX_VALUE;
        Map<String, Integer> typeNum = Maps.newHashMap();
        while (timespanMatcher.find()) {
            String type = timespanMatcher.group("type");
            if (typeNum.containsKey(type)) {
                TimespanVO timespanVO = new TimespanVO();
                timespanVO.setCorrect(false);
                timespanVO.setMsg("type '%s' in timespan repeat...");
                return timespanVO;
            }
            ETimeCarry timeCarry = ETimeCarry.getTimeCarryByType(type);
            if (timeCarry.getFlag() > timeFlag) {
                TimespanVO timespanVO = new TimespanVO();
                timespanVO.setCorrect(false);
                timespanVO.setMsg("press y->w->d->h->m->s sequential input. examples: 1d1m1s, example as '1s1m' is not true");
                return timespanVO;
            }
            timeFlag = timeCarry.getFlag();
            Integer num = Integer.valueOf(timespanMatcher.group("num"));
            typeNum.put(type, num);
        }
        // 处理时间进位，按照 flag 从小到到进行进位处理
        for (ETimeCarry timeCarry : ETimeCarry.getSortETimeCarryList()) {
            Integer num = typeNum.get(timeCarry.getType());
            // 不存在则跳过
            if (Objects.isNull(num)) {
                continue;
            }
            if (num == 0) {
                typeNum.remove(timeCarry.getType());
            }
            // 时间进位小于0: 如达到最大进位 'y' 则跳过
            if (timeCarry.getCarry() < 0 || StringUtils.isBlank(timeCarry.getCarryType())) {
                continue;
            }
            // 进位后的类型原来的值
            Integer carryNumBefore = Objects.isNull(typeNum.get(timeCarry.getCarryType())) ? 0 : typeNum.get(timeCarry.getCarryType());
            // 处理 w -> y 因为52w 不足一年，按52w1d (365) 进一年
            if (ETimeCarry.WEEK.equals(timeCarry)) {
                // 获取原本的天数值
                Integer dayNum = Objects.isNull(typeNum.get(ETimeCarry.DAY.getType())) ? 0 : typeNum.get(ETimeCarry.DAY.getType());
                // 周 + 天 转换为天的和值
                int wAndDNum = dayNum + num * ETimeCarry.DAY.getCarry();
                if (wAndDNum < YEAR_TO_DAY) {
                    // 不足一年直接返回
                    continue;
                }
                int yearNum = wAndDNum / YEAR_TO_DAY;
                int dayCarryAfter = wAndDNum % YEAR_TO_DAY;
                typeNum.put(ETimeCarry.YEAR.getType(), yearNum + carryNumBefore);
                int weekNum = dayCarryAfter / ETimeCarry.DAY.getCarry();
                if (weekNum < 1) {
                    typeNum.remove(ETimeCarry.WEEK.getType());
                    if (dayCarryAfter < 1) {
                        typeNum.remove(ETimeCarry.DAY.getType());
                    } else {
                        typeNum.put(ETimeCarry.DAY.getType(), dayCarryAfter);
                    }
                } else {
                    typeNum.put(ETimeCarry.WEEK.getType(), weekNum);
                    int dayNumAfter = dayCarryAfter % ETimeCarry.DAY.getCarry();
                    if (dayNumAfter < 1) {
                        typeNum.remove(ETimeCarry.DAY.getType());
                    } else {
                        typeNum.put(ETimeCarry.DAY.getType(), dayNumAfter);
                    }
                }
                continue;
            }
            // 往前进的位数
            int carryNum = num / timeCarry.getCarry();
            // 进位后的值
            int carryAfter = num % timeCarry.getCarry();
            typeNum.put(timeCarry.getCarryType(), carryNum + carryNumBefore);
            if (carryAfter == 0) {
                typeNum.remove(timeCarry.getType());
            } else {
                typeNum.put(timeCarry.getType(), carryAfter);
            }
        }
        StringBuilder formatTimespan = new StringBuilder();
        long span = 0;
        List<ETimeCarry> sortETimeCarryList = ETimeCarry.getSortETimeCarryList();
        // 反转集合
        Collections.reverse(sortETimeCarryList);
        for (ETimeCarry timeCarry : sortETimeCarryList) {
            Integer num = typeNum.get(timeCarry.getType());
            if (Objects.isNull(num)) {
                continue;
            }
            // 计算时间跨度 单位秒
            span = span + num * timeCarry.getConvertToSecond();
            formatTimespan.append(num).append(timeCarry.getType());
        }
        TimespanVO timespanVO = new TimespanVO();
        timespanVO.setCorrect(true);
        timespanVO.setSpan(span * 1000);
        timespanVO.setFormatResult(formatTimespan.toString());
        return timespanVO;
    }

    /**
     * 到天日期转化
     *
     * @param var
     * @return
     */
    public static String getDate(Date var, int num) {
        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.setTime(var); //设置时间为当前时间
        ca.add(Calendar.YEAR, num); //年份减1
        Date lastMonth = ca.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return null != var ? simpleDateFormat.format(lastMonth) : null;
    }
}
