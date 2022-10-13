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
 *
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
