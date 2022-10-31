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

package com.dtstack.taier.datasource.api.enums;

import com.dtstack.taier.datasource.api.exception.SourceException;

import java.util.Objects;

/**
 * 导入数据的匹配类型
 * Date: 2017/9/12
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
public enum ImportDataMatchType {

    /**
     * 根据位置
     */
    BY_POS(0),

    /**
     * 根据名称
     */
    BY_NAME(1);

    Integer type;

    ImportDataMatchType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public static ImportDataMatchType getMatchType(Integer type) {
        for (ImportDataMatchType value : ImportDataMatchType.values()) {
            if (Objects.equals(value.getType(), type)) {
                return value;
            }
        }

        throw new SourceException("can't find valid match type");
    }
}
