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

package com.dtstack.engine.datasource.common.utils.datakit;

import com.google.common.base.Joiner;

/**
 * Created by 袋鼠云-数栈产研部-应用研发中心.
 *
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a>
 * @date 2021/3/18
 * @desc 字符串操作工具类
 */
public class Strs {

    public static final char BLANK = ' ';
    public static final char DOUBLE_QUOTES = '"';
    public static final char SINGLE_QUOTES = '\'';
    public static final String POINT = ".";
    public static final String OBLIQUE_QUOTES = "`";

    private Strs() {
    }

    /**
     * 拼接"."
     *
     * @param vars 多个待拼接参数
     * @return <e.g.>schema1.t_student.name</e.g.>
     */
    public static String joinPoint(Object... vars) {
        return Joiner.on(POINT).skipNulls().join(vars);
    }

    /**
     * 拼接"."
     *
     * @param vars 多个待拼接参数
     * @return <e.g.>schema1.t_student.name</e.g.>
     */
    public static StringBuilder joinPoint(StringBuilder sb, Object... vars) {
        if (sb == null) {
            sb = new StringBuilder();
        }
        for (int i = 0; i < vars.length; i++) {
            if (i > 0) {
                sb.append(POINT);
            }
            sb.append(vars[i]);
        }
        return sb;
    }

}
