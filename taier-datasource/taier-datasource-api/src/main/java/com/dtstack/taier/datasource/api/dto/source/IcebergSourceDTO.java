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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Iceberg 数据源连接信息
 *
 * @author ：wangchuan
 * date：Created in 下午9:37 2021/11/9
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IcebergSourceDTO extends RdbmsSourceDTO {

    /**
     * iceberg hive warehouse
     */
    private String warehouse;

    /**
     * iceberg hive uri
     */
    private String uri;

    /**
     * hdfs、hive 等配置的路径
     */
    private String confDir;

    /**
     * hive metaStore 客户端池大小
     */
    private Integer clients;

    @Override
    public Integer getSourceType() {
        return DataSourceType.ICEBERG.getVal();
    }
}
