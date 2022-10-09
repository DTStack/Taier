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

package com.dtstack.taier.develop.utils.develop.service.impl;

import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.utils.DBUtil;
import com.dtstack.taier.develop.service.develop.IJdbcService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chener
 * @Classname JdbcServiceImpl
 * @Description Jdbc 实现类
 * @Date 2020/5/20 14:31
 * @Created chener@dtstack.com
 */
@Service
public class JdbcServiceImpl implements IJdbcService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(JdbcServiceImpl.class);

    @Override
    public List<List<Object>> executeQuery(ISourceDTO sourceDTO, List<String> sqls, String taskParam, Integer limit) {
        return executeQueryWithVariables(sourceDTO, sqls, limit, taskParam);
    }

    @Override
    public Boolean executeQueryWithoutResult(ISourceDTO sourceDTO, String sql) {
        IClient client = ClientCache.getClient(sourceDTO.getSourceType());
        client.executeSqlWithoutResultSet(sourceDTO, SqlQueryDTO.builder().sql(sql).build());
        return Boolean.TRUE;
    }

    @Override
    public List<String> getAllDataBases(ISourceDTO sourceDTO) {
        IClient client = ClientCache.getClient(sourceDTO.getSourceType());
        return client.getAllDatabases(sourceDTO, SqlQueryDTO.builder().build());
    }


    @SuppressWarnings("all")
    public List<List<Object>> executeQueryWithVariables(ISourceDTO sourceDTO, List<String> sqls, Integer limit, String taskParam) {
        List<List<Object>> returnList = new ArrayList<>();
        IClient client = ClientCache.getClient(sourceDTO.getSourceType());
        // to json properties
        String properties = DBUtil.propToJson(taskParam);
        ((RdbmsSourceDTO) sourceDTO).setProperties(properties);
        Connection con = client.getCon(sourceDTO);
        // 处理 variables SQL
        try {
            sourceDTO.setConnection(con);
            List<Map<String, Object>> list = new ArrayList<>();
            for (int i = 0; i < sqls.size(); i++) {
                LOGGER.info("jdbc run sql:{}", sqls.get(i));
                client.executeSqlWithoutResultSet(sourceDTO, SqlQueryDTO.builder().sql(sqls.get(i)).build());
                if (i == sqls.size() - 1) {
                    list = client.executeQuery(sourceDTO, SqlQueryDTO.builder().sql(sqls.get(i)).limit(limit).build());
                }
            }

            List<ColumnMetaDTO> columnMetaDataWithSql = client.getColumnMetaDataWithSql(sourceDTO, SqlQueryDTO.builder().sql(sqls.get(sqls.size() - 1)).limit(0).build());
            if (CollectionUtils.isNotEmpty(columnMetaDataWithSql)) {
                List<Object> column = new ArrayList<>();
                columnMetaDataWithSql.forEach(bean -> column.add(bean.getKey()));
                returnList.add(column);
            }
            //数据源插件化 查询出值不符合要求  进行转化
            if (CollectionUtils.isNotEmpty(list)) {
                for (Map<String, Object> result : list) {
                    List<Object> value = new ArrayList<>(result.values());
                    returnList.add(value);
                }
            }
        } finally {
            sourceDTO.setConnection(null);
            DBUtil.closeDBResources(null, null, con);
        }
        return returnList;
    }

}
