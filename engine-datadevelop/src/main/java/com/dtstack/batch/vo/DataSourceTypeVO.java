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

package com.dtstack.batch.vo;

import com.dtstack.dtcenter.loader.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 数据源类型前端展示vo层
 *
 * @author ：wangchuan
 * date：Created in 上午10:05 2020/10/26
 * company: www.dtstack.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceTypeVO {

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据源类型
     */
    private Integer value;

    /**
     * 排序标示
     */
    private Integer order;

    /**
     * dto -> vo
     * @param dataSourceType
     * @return
     */
    public static DataSourceTypeVO toVO (DataSourceType dataSourceType) {
        if (Objects.isNull(dataSourceType)) {
            return new DataSourceTypeVO();
        }
        return DataSourceTypeVO.builder()
                .name(dataSourceType.getName())
                .value(dataSourceType.getVal())
                .order(dataSourceType.getOrder()).build();
    }
}
