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

package com.dtstack.taier.develop.utils.develop.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author sishu.yss
 */
public class UnitUtil {

    public static final BigDecimal BASE_VALUE = new BigDecimal("1024");
    public static final BigDecimal KB_VALUE = BASE_VALUE.pow(1);
    public static final BigDecimal MB_VALUE = BASE_VALUE.pow(2);
    public static final BigDecimal GB_VALUE = BASE_VALUE.pow(3);
    public static final BigDecimal TB_VALUE = BASE_VALUE.pow(4);
    public static final BigDecimal PB_VALUE = BASE_VALUE.pow(5);
    public static final BigDecimal EB_VALUE = BASE_VALUE.pow(6);

    /**
     * @param size 字节大小
     * @return format size
     */
    public static String unitConverter(long size) {
        BigDecimal sizeBig = BigDecimal.valueOf(size);
        double convertSize;
        String unit;
        if (size == 0) {
            return "0";
        } else if (sizeBig.compareTo(MB_VALUE) < 0) {
            convertSize = sizeBig.divide(KB_VALUE).doubleValue();
            unit = "KB";
        } else if (sizeBig.compareTo(GB_VALUE) < 0) {
            convertSize = sizeBig.divide(MB_VALUE).doubleValue();
            unit = "MB";
        } else if (sizeBig.compareTo(TB_VALUE) < 0) {
            convertSize = sizeBig.divide(GB_VALUE).doubleValue();
            unit = "GB";
        } else if (sizeBig.compareTo(PB_VALUE) < 0) {
            convertSize = sizeBig.divide(TB_VALUE).doubleValue();
            unit = "TB";
        } else if (sizeBig.compareTo(EB_VALUE) < 0) {
            convertSize = sizeBig.divide(PB_VALUE).doubleValue();
            unit = "PB";
        } else {
            convertSize = sizeBig.divide(EB_VALUE).doubleValue();
            unit = "EB";
        }
        DecimalFormat df = new DecimalFormat("0.00");
        if (df.format(convertSize).equals("0.00")) {
            return "0";
        } else {
            return df.format(convertSize) + unit;
        }
    }
}
