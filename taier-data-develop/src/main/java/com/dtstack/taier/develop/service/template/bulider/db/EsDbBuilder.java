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

package com.dtstack.taier.develop.service.template.bulider.db;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author daojin
 * @program: dt-centet-datasync
 * @description: es
 * @date 2021-11-11 15:39:03
 */
@Component
public class EsDbBuilder implements DbBuilder{

    @Override
    public IClient getClient() {
        return ClientCache.getClient(getDataSourceType().getVal());
    }


    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.ES7;
    }

    @Override
    public JSONObject pollPreview(String tableName, ISourceDTO sourceDTO) {
        return null;
    }

    @Override
    public List<JSONObject> listPollTableColumn(ISourceDTO sourceDTO, String tableName) {
        List<JSONObject> columns = new ArrayList<>();
        SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().tableName(tableName).build();
        List<ColumnMetaDTO> columnMetaDTOList = getClient().getColumnMetaData(sourceDTO, sqlQueryDTO);
        if (CollectionUtils.isNotEmpty(columnMetaDTOList)) {
            for (ColumnMetaDTO columnMetaDTO : columnMetaDTOList) {
                columns.add(JSON.parseObject(JSON.toJSONString(columnMetaDTO)));
            }
        }
        return columns;
    }

    @Override
    public List<String> listSchemas(ISourceDTO sourceDTO, String db) {
        return null;
    }

    @Override
    public List<String> listTablesBySchema(String schema, String tableNamePattern, ISourceDTO sourceDTO, String db) {
        return null;
    }

    @Override
    public String buildConnMsgForSA(JSONObject dataJson) {
        return null;
    }
}
