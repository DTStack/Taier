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

import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.enums.MatchType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 处理模糊查询和限制调试工具类
 *
 * @author ：wangchuan
 * date：Created in 下午1:57 2021/6/7
 * company: www.dtstack.com
 */
public class SearchUtil {

    /**
     * 处理结果集，进行模糊查询和条数限制
     *
     * @param result   结果集
     * @param queryDTO 查询条件
     */
    public static List<String> handleSearchAndLimit(List<String> result, SqlQueryDTO queryDTO) {
        if (CollectionUtils.isEmpty(result) || Objects.isNull(queryDTO)) {
            return result;
        }
        if (StringUtils.isNotBlank(queryDTO.getTableNamePattern())) {
            if (!ReflectUtil.fieldExists(SqlQueryDTO.class, "matchType")
                    || Objects.isNull(queryDTO.getMatchType())) {
                result = result.stream().filter(single -> StringUtils.containsIgnoreCase(single, queryDTO.getTableNamePattern().trim())).collect(Collectors.toList());
            } else if (MatchType.ALL.equals(queryDTO.getMatchType())) {
                result = result.stream().filter(single -> StringUtils.equalsIgnoreCase(single, queryDTO.getTableNamePattern().trim())).collect(Collectors.toList());
            } else if (MatchType.PREFIX.equals(queryDTO.getMatchType())) {
                result = result.stream().filter(single -> StringUtils.startsWithIgnoreCase(single, queryDTO.getTableNamePattern().trim())).collect(Collectors.toList());
            } else if (MatchType.SUFFIX.equals(queryDTO.getMatchType())) {
                result = result.stream().filter(single -> StringUtils.endsWithIgnoreCase(single, queryDTO.getTableNamePattern().trim())).collect(Collectors.toList());
            } else if (MatchType.CONTAINS.equals(queryDTO.getMatchType())) {
                result = result.stream().filter(single -> StringUtils.containsIgnoreCase(single, queryDTO.getTableNamePattern().trim())).collect(Collectors.toList());
            }
        }
        if (Objects.nonNull(queryDTO.getLimit())) {
            result = result.stream().limit(queryDTO.getLimit()).collect(Collectors.toList());
        }
        return result;
    }
}
