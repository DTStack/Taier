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

package com.dtstack.taier.datasource.plugin.rdbms;

import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.pool.PoolConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Assert;
import org.junit.Test;

public class ConnFactoryTest {

    @Test
    public void testGetConnRejectDangerousJdbcUrlOnSimplePath() {
        TestConnFactory connFactory = new TestConnFactory();
        RdbmsSourceDTO sourceDTO = buildSourceDTO("jdbc:postgresql://127.0.0.1:5432/test?socketFactory=evil");

        try {
            connFactory.getConn(sourceDTO);
            Assert.fail("Dangerous JDBC parameter should be rejected");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof SourceException);
            Assert.assertTrue(e.getMessage().contains("Dangerous JDBC parameter detected"));
        }
    }

    @Test
    public void testTransHikariRejectDangerousJdbcUrlOnPooledPath() {
        TestConnFactory connFactory = new TestConnFactory();
        RdbmsSourceDTO sourceDTO = buildSourceDTO("jdbc:postgresql://127.0.0.1:5432/test?socketFactory=evil");
        sourceDTO.setPoolConfig(PoolConfig.builder().build());

        try {
            connFactory.exposeTransHikari(sourceDTO);
            Assert.fail("Dangerous JDBC parameter should be rejected before HikariDataSource is created");
        } catch (SecurityException e) {
            Assert.assertTrue(e.getMessage().contains("socketFactory"));
        }
    }

    @Test
    public void testValidateJdbcSecurityRejectEncodedDangerousJdbcUrl() {
        RdbmsSourceDTO sourceDTO = buildSourceDTO("jdbc:postgresql://127.0.0.1:5432/test?socketFactory%3Devil");

        try {
            ConnFactory.validateJdbcSecurity(sourceDTO);
            Assert.fail("Encoded dangerous JDBC parameter should be rejected");
        } catch (SecurityException e) {
            Assert.assertTrue(e.getMessage().contains("socketFactory"));
        }
    }

    @Test
    public void testValidateJdbcSecurityRejectDangerousJdbcProperties() {
        RdbmsSourceDTO sourceDTO = buildSourceDTO("jdbc:postgresql://127.0.0.1:5432/test");
        sourceDTO.setProperties("{\"socketFactory\":\"evil\"}");

        try {
            ConnFactory.validateJdbcSecurity(sourceDTO);
            Assert.fail("Dangerous JDBC property should be rejected");
        } catch (SecurityException e) {
            Assert.assertTrue(e.getMessage().contains("socketFactory"));
        }
    }

    @Test
    public void testValidateJdbcSecurityAllowNormalJdbcUrl() {
        RdbmsSourceDTO sourceDTO = buildSourceDTO("jdbc:postgresql://127.0.0.1:5432/test?ssl=true&connectTimeout=10");

        ConnFactory.validateJdbcSecurity(sourceDTO);
    }

    private static RdbmsSourceDTO buildSourceDTO(String jdbcUrl) {
        RdbmsSourceDTO sourceDTO = new RdbmsSourceDTO();
        sourceDTO.setUrl(jdbcUrl);
        sourceDTO.setUsername("user");
        sourceDTO.setPassword("password");
        return sourceDTO;
    }

    private static class TestConnFactory extends ConnFactory {

        private TestConnFactory() {
            this.driverName = "org.postgresql.Driver";
        }

        private HikariDataSource exposeTransHikari(RdbmsSourceDTO sourceDTO) {
            return transHikari(sourceDTO);
        }
    }
}
