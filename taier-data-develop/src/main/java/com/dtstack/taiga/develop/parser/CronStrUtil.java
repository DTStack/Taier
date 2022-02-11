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

package com.dtstack.taiga.develop.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取输入的cronStr的各个field的值
 * 格式 * * * * * ?
 * Date: 2017/5/29
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class CronStrUtil {

    private static final Logger logger = LoggerFactory.getLogger(CronStrUtil.class);

    public static String getDayStr(String str){
        String[] arr = str.split("\\s+");
        if(arr.length < 6){
            logger.error("it is an illegal cron string");
            return null;
        }

        return arr[3];
    }

    public static String getDayOfWeekStr(String str){
        String[] arr = str.split("\\s+");
        if(arr.length < 6){
            logger.error("it is an illegal cron string");
            return null;
        }

        return arr[5];
    }
}
