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

package com.dtstack.taier.develop.flink.sql.sink;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.util.MapUtil;
import com.dtstack.taier.develop.flink.sql.core.ISqlParamEnum;
import com.dtstack.taier.develop.flink.sql.core.SqlConstant;
import com.dtstack.taier.develop.flink.sql.sink.param.HbaseSinkParamEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.List;
import java.util.Map;

public class HbaseSinkTable extends AbstractSinkTable {

    @Override
    public ISqlParamEnum[] getSqlParamEnums() {
        return HbaseSinkParamEnum.values();
    }

    @Override
    protected void addSelfParam(Map<String, Object> tableParam) {
        super.addSelfParam(tableParam);
        String batchWaitInterval = getAllParam().getString(HbaseSinkParamEnum.batchWaitInterval.getFront());
        if (NumberUtils.isNumber(batchWaitInterval)) {
            Long intervalLong = NumberUtils.createLong(batchWaitInterval);
            // 处理 ms 成 s , 不足 1 s 按 1 s 计算
            String intervalStr = (intervalLong % 1000) > 0 ? ((intervalLong / 1000) + 1) + "s" : (intervalLong / 1000) + "s";
            MapUtil.putIfValueNotNull(tableParam, HbaseSinkParamEnum.batchWaitInterval.getFront(), intervalStr);
        }
    }

    @Override
    protected void addColumnStr(List<String> tableStructure) {
        // 暂时只处理 flink1.12，暂时不解析类型，后续解析
        // 处理 rowKey 和 rowKey 类型
        String rowKey = getAllParam().getString(HbaseSinkParamEnum.rowKey.getFront());
        String rowKeyType = getAllParam().getString(HbaseSinkParamEnum.rowKeyType.getFront());
        tableStructure.add(rowKey + SqlConstant.COLUMN_SEPARATOR + rowKeyType);
        JSONArray columns = getAllParam().getJSONArray(SqlConstant.COLUMNS_KEY);
        if (CollectionUtils.isEmpty(columns)) {
            return;
        }
        for (int i = 0; i < columns.size(); i++) {
            JSONObject column = columns.getJSONObject(i);
            tableStructure.add(column.getString(SqlConstant.COLUMN_KEY));
        }
    }

    /**
     * 添加主健信息，主键，rowkey 当成主键
     *
     * @param tableStructure 表结构信息
     */
    @Override
    protected void addPrimaryKey(List<String> tableStructure) {
        String primaryKey = getAllParam().getString(HbaseSinkParamEnum.rowKey.getFront());
        if (StringUtils.isBlank(primaryKey)) {
            return;
        }
        tableStructure.add(String.format(PRIMARY_KEY_TEMPLATE, primaryKey));
    }

    @Override
    protected String getTypeBeforeVersion112() {
        return "hbase";
    }

    @Override
    protected String getTypeVersion112() {
        return "hbase14-x";
    }
}
