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

package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * trino sourceDTO
 *
 * @author ：wangchuan
 * date：Created in 下午2:23 2021/9/7
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
public class TrinoSourceDTO extends RdbmsSourceDTO {

    /**
     * 位于schema上层，不为空时会在获取连接就切换到该catalog下
     */
    private String catalog;

    @Override
    public Integer getSourceType() {
        return DataSourceType.TRINO.getVal();
    }
}
