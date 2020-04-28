/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.enums.MultiEngineType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EngineUtil {

    public static Logger logger = LoggerFactory.getLogger(EngineUtil.class);

    public static final Map<Integer, List<Integer>> ENGINE_SUPPORTED_COMPONENTS = new HashMap<>();

    static {
        ENGINE_SUPPORTED_COMPONENTS.put(MultiEngineType.LIBRA.getType(), Collections.singletonList(EComponentType.LIBRA_SQL.getTypeCode()));

        List<Integer> hadoopEngineSupportedComponents = new ArrayList<>();
        for (EComponentType value : EComponentType.values()) {
            hadoopEngineSupportedComponents.add(value.getTypeCode());
        }
        hadoopEngineSupportedComponents.remove(EComponentType.LIBRA_SQL.getTypeCode());
        ENGINE_SUPPORTED_COMPONENTS.put(MultiEngineType.HADOOP.getType(), hadoopEngineSupportedComponents);
        ENGINE_SUPPORTED_COMPONENTS.put(MultiEngineType.TIDB.getType(), Collections.singletonList(EComponentType.TIDB_SQL.getTypeCode()));
    }

    public static final List<Integer> REQUIRED_COMPONENT_TYPES = Arrays.asList(EComponentType.HDFS.getTypeCode(),
            EComponentType.YARN.getTypeCode(), EComponentType.SFTP.getTypeCode());

    public static boolean isRequiredComponent(Integer componentType) {
        return REQUIRED_COMPONENT_TYPES.contains(componentType);
    }

    public static void checkComponent(MultiEngineType engineType, List<Integer> componentTypeCodeList) {
        if (CollectionUtils.isEmpty(componentTypeCodeList)) {
            return;
        }

        List<Integer> supportedComponents = ENGINE_SUPPORTED_COMPONENTS.get(engineType.getType());
        for (Integer componentType : componentTypeCodeList) {
            if (!supportedComponents.contains(componentType)) {
                throw new RdosDefineException("引擎:" + engineType.name() + " 不支持组件类型:" + componentType);
            }
        }
    }

    public static Map<Integer, List<String>> classifyComponent(Set<String> confNames) {
        Map<Integer, List<String>> result = new HashMap<>();

        for (String confName : confNames) {
            try {
                EComponentType componentType = EComponentType.getByConfName(confName);
                for (Integer integer : ENGINE_SUPPORTED_COMPONENTS.keySet()) {
                    if (ENGINE_SUPPORTED_COMPONENTS.get(integer).contains(componentType.getTypeCode())) {
                        List<String> components = result.computeIfAbsent(integer, k -> new ArrayList<>());
                        components.add(confName);
                    }
                }
            } catch (Exception e){
                logger.warn(" ", e);
            }
        }

        return result;
    }

    public static MultiEngineType getByType(Integer type) {
        for (MultiEngineType value : MultiEngineType.values()) {
            if (value.getType() == type) {
                return value;
            }
        }

        throw new UnsupportedOperationException("未知引擎类型：" + type);
    }
}
