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

package com.dtstack.taier.develop.service.template.clickhouse;

import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.rdbms.RDBWriter;

import java.util.List;

/**
 * <a href="https://github.com/DTStack/chunjun/blob/master/chunjun-examples/json/clickhouse/clickhouse.json">...</a>
 * @author leon
 * @date 2022-10-12 14:54
 **/
public class ClickHouseWriter extends RDBWriter {

    private List<String> fullColumnName;
    private List<String> getFullColumnType;

    @Override
    public String pluginName() {
        return  PluginName.Clichhouse_W;
    }

    public List<String> getFullColumnName() {
        return fullColumnName;
    }

    public void setFullColumnName(List<String> fullColumnName) {
        this.fullColumnName = fullColumnName;
    }

    public List<String> getGetFullColumnType() {
        return getFullColumnType;
    }

    public void setGetFullColumnType(List<String> getFullColumnType) {
        this.getFullColumnType = getFullColumnType;
    }
}
