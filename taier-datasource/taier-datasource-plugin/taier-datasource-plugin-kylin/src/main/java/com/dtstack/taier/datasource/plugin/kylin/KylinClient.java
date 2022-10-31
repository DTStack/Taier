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

package com.dtstack.taier.datasource.plugin.kylin;

import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SearchUtil;
import com.dtstack.taier.datasource.plugin.rdbms.AbsRdbmsClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.KylinSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 19:14 2020/1/7
 * @Description：Kylin 客户端
 */
public class KylinClient extends AbsRdbmsClient {
    private static final String TABLE_SHOW = "\"%s\".\"%s\"";

    // 获取当前版本号
    private static final String SHOW_VERSION = "select version()";

    @Override
    protected ConnFactory getConnFactory() {
        return new KylinConnFactory();
    }

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.Kylin;
    }

    @Override
    public IDownloader getDownloader(ISourceDTO source, SqlQueryDTO queryDTO) throws Exception {
        KylinSourceDTO kylinSourceDTO = (KylinSourceDTO) source;
        KylinDownloader kylinDownloader = new KylinDownloader(getCon(kylinSourceDTO), queryDTO.getSql(), kylinSourceDTO.getSchema());
        kylinDownloader.configure();
        return kylinDownloader;
    }

    @Override
    public List<String> getTableList(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        RdbmsSourceDTO rdbmsSourceDTO = (RdbmsSourceDTO) sourceDTO;
        ResultSet rs = null;
        List<String> tableList = new ArrayList<>();
        try {
            DatabaseMetaData meta = connection.getMetaData();
            if (null == queryDTO) {
                rs = meta.getTables(null, null, null, null);
            } else {
                rs = meta.getTables(null, rdbmsSourceDTO.getSchema(), null, DBUtil.getTableTypes(queryDTO));
            }
            while (rs.next()) {
                tableList.add(String.format(TABLE_SHOW, rs.getString(2), rs.getString(3)));
            }
        } catch (Exception e) {
            throw new SourceException(String.format("Get database table exception,%s", e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(rs, null, connection);
        }
        return SearchUtil.handleSearchAndLimit(tableList, queryDTO);
    }

    @Override
    protected String getVersionSql() {
        return SHOW_VERSION;
    }

}
