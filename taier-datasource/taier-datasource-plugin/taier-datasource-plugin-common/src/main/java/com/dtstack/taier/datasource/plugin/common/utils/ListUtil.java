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

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * list 工具类
 *
 * @author ：wangchuan
 * date：Created in 下午3:01 2022/4/8
 * company: www.dtstack.com
 */
public class ListUtil {

    /**
     * 判断字符串 List 集合中是否包含指定字符串
     *
     * @param targetList 目标集合
     * @param searchStr  搜索的字符串
     * @return 是否包含
     */
    public static boolean containsIgnoreCase(List<String> targetList, String searchStr) {
        String targetStr = targetList
                .stream()
                .filter(x -> StringUtils.equalsIgnoreCase(x, searchStr))
                .findAny()
                .orElse(null);
        return StringUtils.isNotBlank(targetStr);
    }
}