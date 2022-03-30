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

package com.dtstack.taier.develop.utils.develop.common.util;

import com.dtstack.taier.common.util.Base64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chener
 * @Classname SqlFormatterUtil
 * @Description TODO
 * @Date 2020/8/11 17:39
 * @Created chener@dtstack.com
 */
public class SqlFormatterUtil {

    private static final Logger logger = LoggerFactory.getLogger(SqlFormatterUtil.class);
    private static final Pattern note_pattern = Pattern.compile("--.*\n|/\\*\\*[\\s\\S]*\\*/");
    private static final Pattern note_pattern_new = Pattern.compile("##.*\n");

    public static String dealAnnotationBefore(String sql) {
        sql = sql + "\n";
        for (Matcher matcher = note_pattern.matcher(sql);
             matcher.find(); matcher = note_pattern.matcher(sql)) {
            sql = matcher.replaceFirst("##" +
                    Base64Util.baseEncode(matcher.group()) + "\n");
        }
        return sql;
    }

    public static String dealAnnotationAfter(String sql) {
        for (Matcher matcher = note_pattern_new.matcher(sql);
             matcher.find(); matcher = note_pattern_new.matcher(sql)) {
            String group = matcher.group();
            if (group.endsWith("\n")) {
                group = group.substring(2, group.length() - 1);
            }
            String s = group;
            try {
                s = group;
            } catch (IllegalArgumentException var5) {
                logger.warn("baseEncode failed, sql={}, e={}", sql, var5);
            }
            s = s.replaceAll("\\$", "RDS_CHAR_DOLLAR");
            sql = matcher.replaceFirst(s);
            sql = sql.replaceAll("RDS_CHAR_DOLLAR", "\\$");
        }
        return sql;
    }

}
