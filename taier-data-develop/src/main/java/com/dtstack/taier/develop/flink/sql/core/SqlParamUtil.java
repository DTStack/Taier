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

package com.dtstack.taier.develop.flink.sql.core;

import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.util.MapUtil;
import com.dtstack.taier.develop.enums.develop.FlinkVersion;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * sql 参数工具
 *
 * @author ：qianyi
 * company: www.dtstack.com
 */
public class SqlParamUtil {

    /**
     * 获得对应版本的key
     *
     * @param flinkVersion      flink 版本
     * @param sqlParamEnumBases 参数枚举集合
     * @return 前端 key 对应的 flinkX 需要的 key
     */
    public static Map<String, String> getFrontFlinkXKeyMap(FlinkVersion flinkVersion, ISqlParamEnum[] sqlParamEnumBases) {
        Map<String, String> frontFlinkXKeyMap = Maps.newHashMap();
        if (ArrayUtils.isEmpty(sqlParamEnumBases)) {
            return frontFlinkXKeyMap;
        }
        switch (flinkVersion) {
            case FLINK_112:
                for (ISqlParamEnum paramEnumBase : sqlParamEnumBases) {
                    MapUtil.putIfValueNotBlank(frontFlinkXKeyMap, paramEnumBase.getFront(), paramEnumBase.getFlink112());
                }
                break;
            default:
                throw new DtCenterDefException(String.format("不支持flink版本: %s", flinkVersion.getType()));
        }
        return frontFlinkXKeyMap;
    }

    /**
     * 转换参数 value 信息
     *
     * @param allParam          所有参数信息
     * @param frontKey          前端入参 key
     * @param flinkVersion      flink 版本
     * @param sqlParamEnumBases 参数枚举
     * @param isDelete          找不到对应 value 时是否清除
     */
    public static void convertParamValue(Map<String, Object> allParam, String frontKey, FlinkVersion flinkVersion,
                                         ISqlParamEnum[] sqlParamEnumBases, boolean isDelete) {
        String value = MapUtils.getString(allParam, frontKey);
        if (StringUtils.isEmpty(value)) {
            return;
        }
        for (ISqlParamEnum sqlParamEnumBase : sqlParamEnumBases) {
            if (sqlParamEnumBase.getFront().equals(value)) {
                String defaultValue;
                switch (flinkVersion) {
                    default:
                        defaultValue = sqlParamEnumBase.getFlink112();
                        break;
                }
                allParam.put(frontKey, defaultValue);
                return;
            }
        }
        // value 不符合条件时清除
        if (isDelete) {
            allParam.remove(frontKey);
        }
    }
}
