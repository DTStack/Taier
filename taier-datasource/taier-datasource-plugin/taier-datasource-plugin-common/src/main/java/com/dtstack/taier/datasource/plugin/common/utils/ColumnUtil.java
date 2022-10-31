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

package com.dtstack.taier.datasource.plugin.common.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * column 工具类
 *
 * @author ：wangchuan
 * date：Created in 下午7:47 2022/3/3
 * company: www.dtstack.com
 */
public class ColumnUtil {

    /**
     * 将 column 转换成 id, name, age 这种形式的字符串, 如果 list 为空则返回 *
     *
     * @param list 字段集合
     * @return string column
     */
    public static String listToStr(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "*";
        }

        StringBuilder column = new StringBuilder();
        list.stream()
                .filter(str -> !StringUtils.isBlank(str))
                .forEach(str -> column.append(str).append(","));

        if (column.length() > 0) {
            column.deleteCharAt(column.length() - 1);
        }
        return column.toString();
    }
}
