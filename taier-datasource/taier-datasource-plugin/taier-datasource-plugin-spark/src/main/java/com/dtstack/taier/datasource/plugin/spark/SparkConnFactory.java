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

package com.dtstack.taier.datasource.plugin.spark;

import com.dtstack.taier.datasource.plugin.common.utils.PropertiesUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SparkSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataBaseType;
import lombok.extern.slf4j.Slf4j;

import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 17:07 2020/1/7
 * @Description：Spark 连接池工厂
 */
@Slf4j
public class SparkConnFactory extends ConnFactory {

    public SparkConnFactory() {
        this.driverName = DataBaseType.Spark.getDriverClassName();
        this.errorPattern = new SparkErrorPattern();
    }

    @Override
    public Connection getConn(ISourceDTO sourceDTO) throws Exception {
        init();
        SparkSourceDTO sparkSourceDTO = (SparkSourceDTO) sourceDTO;

        Connection connection = KerberosLoginUtil.loginWithUGI(sparkSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<Connection>) () -> {
                    try {
                        DriverManager.setLoginTimeout(30);
                        String urlWithoutSchema = SparkThriftDriverUtil.removeSchema(sparkSourceDTO.getUrl());
                        Properties properties = PropertiesUtil.convertToProp(sparkSourceDTO);
                        return DriverManager.getConnection(urlWithoutSchema, properties);
                    } catch (SQLException e) {
                        // 对异常进行统一处理
                        throw new SourceException(errorAdapter.connAdapter(e.getMessage(), errorPattern), e);
                    }
                }
        );
        return SparkThriftDriverUtil.setSchema(connection, sparkSourceDTO.getUrl(), sparkSourceDTO.getSchema());
    }
}
