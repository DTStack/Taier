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

package com.dtstack.taier.datasource.plugin.hbase;

import com.dtstack.taier.datasource.plugin.common.exception.IErrorPattern;
import com.dtstack.taier.datasource.plugin.common.service.ErrorAdapterImpl;
import com.dtstack.taier.datasource.plugin.common.service.IErrorAdapter;
import com.dtstack.taier.datasource.plugin.hbase.pool.HbasePoolManager;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.HbaseSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.hadoop.hbase.client.Connection;

import java.io.IOException;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 20:00 2020/2/27
 * @Description：Hbase 连接工厂
 */
@Slf4j
public class HbaseConnFactory {

    // 连接超时时间 单位：秒。 默认60秒
    private static final Integer TIMEOUT = 60;

    private static final IErrorPattern ERROR_PATTERN = new HbaseErrorPattern();

    // 异常适配器
    private static final IErrorAdapter ERROR_ADAPTER = new ErrorAdapterImpl();

    public Boolean testConn(ISourceDTO iSource) {
        HbaseSourceDTO hbaseSourceDTO = (HbaseSourceDTO) iSource;
        boolean check = false;
        Connection hConn = null;
        try {
            hConn = getHbaseConn(hbaseSourceDTO, SqlQueryDTO.builder().build());
            hConn.getAdmin().getClusterStatus();
            check = true;
        } catch (Exception e) {
            throw new SourceException(ERROR_ADAPTER.connAdapter(e.getMessage(), ERROR_PATTERN), e);
        } finally {
            if ((hbaseSourceDTO.getPoolConfig() == null || MapUtils.isNotEmpty(hbaseSourceDTO.getKerberosConfig())) && hConn != null) {
                try {
                    hConn.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            HbaseClient.destroyProperty();
        }
        return check;
    }

    public static Connection getHbaseConn(HbaseSourceDTO source, SqlQueryDTO queryDTO) {
        if (source.getPoolConfig() == null || MapUtils.isNotEmpty(source.getKerberosConfig())) {
            return HbasePoolManager.initHbaseConn(source, queryDTO);
        }
        return HbasePoolManager.getConnection(source, queryDTO);
    }

    public static Connection getHbaseConn(HbaseSourceDTO source, Integer queryTimeout) {
        SqlQueryDTO queryDTO = SqlQueryDTO.builder().queryTimeout(queryTimeout).build();
        return getHbaseConn(source, queryDTO);
    }

    public static Connection getHbaseConn(HbaseSourceDTO source) {
        SqlQueryDTO queryDTO = SqlQueryDTO.builder().queryTimeout(TIMEOUT).build();
        return getHbaseConn(source, queryDTO);
    }
}
