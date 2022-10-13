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

package com.dtstack.taier.common.util;

import com.dtstack.taier.common.enums.EComponentType;

import java.util.Map;
import java.util.Objects;

public class ComponentVersionUtil {


    public static String getComponentVersion(Map<Integer, String> componentVersionMap, EComponentType componentType) {
        return Objects.isNull(componentVersionMap) ? null : componentVersionMap.get(componentType.getTypeCode());
    }

    public static String getComponentVersion(Map<Integer, String> componentVersionMap, Integer componentTypeCode) {
        return Objects.isNull(componentVersionMap) ? null : componentVersionMap.get(componentTypeCode);
    }

    public static boolean isMultiVersionComponent(Integer componentTypeCode) {
        if (EComponentType.FLINK.getTypeCode().equals(componentTypeCode) || EComponentType.SPARK.getTypeCode().equals(componentTypeCode)) {
            return true;
        }
        return false;
    }


    public static String formatMultiVersion(Integer componentCode, String componentVersion) {
        return isMultiVersionComponent(componentCode) ? componentVersion : null;
    }


}
