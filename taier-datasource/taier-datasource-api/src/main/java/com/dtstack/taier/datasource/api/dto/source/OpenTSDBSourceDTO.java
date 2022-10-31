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

import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * open TSDB 数据源连接信息
 *
 * @author ：wangchuan
 * date：Created in 上午10:43 2021/6/17
 * company: www.dtstack.com
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OpenTSDBSourceDTO extends AbstractSourceDTO {

    /**
     * url
     */
    private String url;

    @Override
    public Integer getSourceType() {
        return DataSourceType.OPENTSDB.getVal();
    }

    @Override
    public String getUsername() {
        throw new SourceException("The method is not supported");
    }

    @Override
    public String getPassword() {
        throw new SourceException("The method is not supported");
    }
}
