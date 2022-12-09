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

package com.dtstack.taier.develop.flink.sql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.TableType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.util.Base64Util;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.develop.enums.develop.FlinkVersion;
import com.dtstack.taier.develop.flink.sql.core.TableFactory;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

public class SqlGenerateFactory {

    /**
     * 生成 flinkSql 建表 sql，112 版本
     *
     * @param dataSource   数据源信息
     * @param paramJson    前端入参信息
     * @param versionValue 组建版本
     * @param tableType    表类型
     * @return 建表 sql
     */
    public static String generateSql(DsInfo dataSource, JSONObject paramJson, String versionValue, TableType tableType) {
        if (Objects.isNull(tableType)) {
            throw new DtCenterDefException("表类型不能为空");
        }
        JSONObject dataJson = JSON.parseObject(Base64Util.baseDecode(dataSource.getDataJson()));

        switch (tableType) {
            case SIDE:
                if (StringUtils.isNotBlank(versionValue) && FlinkVersion.FLINK_112.getVersion().equals(versionValue)) {
                    return TableFactory.getSideTable(dataSource.getDataTypeCode(), dataJson, paramJson, FlinkVersion.FLINK_112).getCreateSql();
                }
            case SINK:
                if (StringUtils.isNotBlank(versionValue) && FlinkVersion.FLINK_112.getVersion().equals(versionValue)) {
                    return TableFactory.getSinkTable(dataSource.getDataTypeCode(), dataJson, paramJson, FlinkVersion.FLINK_112).getCreateSql();
                }
            case SOURCE:
                if (StringUtils.isNotBlank(versionValue) && FlinkVersion.FLINK_112.getVersion().equals(versionValue)) {
                    return TableFactory.getSourceTable(dataSource.getDataTypeCode(), dataJson, paramJson, FlinkVersion.FLINK_112).getCreateSql();
                }
            default:
                throw new DtCenterDefException(String.format("不支持的表类型:%s", tableType.getTableType()));
        }
    }
}
