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

package com.dtstack.taier.pluginapi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 单位转换工具
 * Date: 2017/11/30
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class UnitConvertUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitConvertUtil.class);

    private static final Pattern capacityPattern = Pattern.compile("(\\d+)\\s*([a-zA-Z]{1,2})");

    /**
     * 暂时只做 kb, mb, gb转换
     * eg: 1g --> 1024
     * 1024k --> 1
     * 1mb --> 1
     *
     * @param memStr
     * @return
     */
    public static Integer convert2Mb(String memStr) {
        Matcher matcher = capacityPattern.matcher(memStr);
        if (matcher.find() && matcher.groupCount() == 2) {
            String num = matcher.group(1);
            String unit = matcher.group(2).toLowerCase();
            if (unit.contains("g")) {
                Double mbNum = MathUtil.getDoubleVal(num) * 1024;
                return mbNum.intValue();
            } else if (unit.contains("m")) {
                return MathUtil.getDoubleVal(num).intValue();
            } else if (unit.contains("k")) {
                Double mbNum = MathUtil.getDoubleVal(num) / 1024;
                return mbNum.intValue();
            } else {
                LOGGER.error("can not convert memStr:" + memStr + ", return default 512.");
            }
        } else {
            LOGGER.error("can not convert memStr:" + memStr + ", return default 512.");
        }

        return 512;
    }


    public static int getNormalizedMem(String rawMem) {
        if (rawMem.endsWith("G") || rawMem.endsWith("g")) {
            return Integer.parseInt(rawMem.trim().substring(0, rawMem.length() - 1)) * 1024;
        } else if (rawMem.endsWith("M") || rawMem.endsWith("m")) {
            return Integer.parseInt(rawMem.trim().substring(0, rawMem.length() - 1));
        } else {
            return Integer.parseInt(rawMem.trim());
        }
    }
}
