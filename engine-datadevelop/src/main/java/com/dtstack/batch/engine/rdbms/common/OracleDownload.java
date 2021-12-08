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

package com.dtstack.batch.engine.rdbms.common;

import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.dtcenter.common.engine.JdbcInfo;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.loader.IDownloader;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.exception.DtLoaderException;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * shixi
 */
public class OracleDownload implements IDownload {

    private static Logger logger = LoggerFactory.getLogger(OracleDownload.class);

    private IDownloader pluginDownloader;

    public OracleDownload(String sql, Long dtuicTenantId, String schema) throws Exception {
        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtuicTenantId, null, EJobType.ORACLE_SQL);
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtuicTenantId, null, DataSourceType.Oracle.getVal(), schema, jdbcInfo);
        IClient client = ClientCache.getClient(DataSourceType.Oracle.getVal());
        SqlQueryDTO queryDTO = SqlQueryDTO.builder()
                .sql(sql)
                .limit(jdbcInfo.getMaxRows())
                .queryTimeout(jdbcInfo.getQueryTimeout())
                .build();
        pluginDownloader = client.getDownloader(iSourceDTO, queryDTO);
    }

    @Override
    public void configure() {
        try {
            pluginDownloader.configure();
        } catch (Exception e) {
            if (e instanceof DtLoaderException) {
                throw (DtLoaderException) e;
            }
            throw new DtCenterDefException(String.format("下载器configure失败，原因是：%s", e.getMessage()), e);
        }
    }

    @Override
    public List<String> getMetaInfo() {
        try {
            return pluginDownloader.getMetaInfo();
        } catch (Exception e) {
            logger.error("", e);
            if (e instanceof DtLoaderException) {
                throw (DtLoaderException) e;
            }
            throw new DtCenterDefException(String.format("下载器getMetaInfo失败，原因是：%s", e.getMessage()), e);
        }
    }

    @Override
    public Object readNext() {
        try {
            return pluginDownloader.readNext();
        } catch (Exception e) {
            if (e instanceof DtLoaderException) {
                throw (DtLoaderException) e;
            }
            throw new DtCenterDefException(String.format("下载器readNext失败，原因是：%s", e.getMessage()), e);
        }
    }

    @Override
    public boolean reachedEnd() {
        try {
            return pluginDownloader.reachedEnd();
        } catch (Exception e) {
            if (e instanceof DtLoaderException) {
                throw (DtLoaderException) e;
            }
            throw new RdosDefineException(String.format("下载器reachedEnd失败，原因是：%s", e.getMessage()), e);
        }
    }

    @Override
    public void close() {
        try {
            pluginDownloader.close();
        } catch (Exception e) {
            if (e instanceof DtLoaderException) {
                throw (DtLoaderException) e;
            }
            throw new RdosDefineException(String.format("下载器close失败，原因是：%s", e.getMessage()), e);
        }
    }

    @Override
    public String getFileName() {
        try {
            return pluginDownloader.getFileName();
        } catch (Exception e) {
            if (e instanceof DtLoaderException) {
                throw (DtLoaderException) e;
            }
            logger.error(String.format("获取getFileName失败,原因是%s", e.getMessage()), e);
        }
        return "";
    }

}
