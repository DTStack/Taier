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

package com.dtstack.taier.datasource.plugin.trino;

import com.dtstack.taier.datasource.plugin.common.DtClassConsistent;
import com.dtstack.taier.datasource.plugin.common.exception.ErrorCode;
import com.dtstack.taier.datasource.plugin.common.utils.PropertiesUtil;
import com.dtstack.taier.datasource.plugin.common.utils.PropertyUtil;
import com.dtstack.taier.datasource.plugin.common.utils.ReflectUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SSLUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosConfigUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosUtil;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.TrinoSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.constant.KerberosConstant;
import com.dtstack.taier.datasource.api.source.DataBaseType;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import sun.security.krb5.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * trino 数据源连接工厂类
 *
 * @author ：wangchuan
 * date：Created in 下午2:21 2021/9/9
 * company: www.dtstack.com
 */
@Slf4j
public class TrinoConnFactory extends ConnFactory {

    /**
     * Kerberos 服务名称
     */
    public static final String KERBEROS_REMOTE_SERVICE_NAME = "KerberosRemoteServiceName";

    /**
     * principal
     */
    public static final String KERBEROS_PRINCIPAL = "KerberosPrincipal";

    /**
     * keytab 路径
     */
    public static final String KERBEROS_KEYTAB_PATH = "KerberosKeytabPath";

    public TrinoConnFactory() {
        driverName = DataBaseType.TRINO.getDriverClassName();
        this.errorPattern = new TrinoErrorPattern();
        testSql = DataBaseType.TRINO.getTestSql();
    }

    @Override
    public Connection getConn(ISourceDTO sourceDTO) throws Exception {
        init();
        TrinoSourceDTO trinoSourceDTO = (TrinoSourceDTO) sourceDTO;
        try {
            Properties properties = new Properties();
            //catalog
            String catalog = null;
            if (ReflectUtil.fieldExists(TrinoSourceDTO.class, "catalog")) {
                catalog = trinoSourceDTO.getCatalog();
            }
            // 处理 ssl
            SSLUtil.SSLConfiguration sslConfiguration = SSLUtil.getSSLConfiguration(sourceDTO);
            buildSSLConfig(properties, sslConfiguration);
            DriverManager.setLoginTimeout(30);
            // kerberos
            Map<String, Object> kerberosConfig = trinoSourceDTO.getKerberosConfig();
            PropertyUtil.putIfNotNull(properties, DtClassConsistent.PublicConsistent.USER, trinoSourceDTO.getUsername());
            PropertyUtil.putIfNotNull(properties, DtClassConsistent.PublicConsistent.PASSWORD, trinoSourceDTO.getPassword());
            if (MapUtils.isEmpty(kerberosConfig)) {
                // 未开启 kerberos 直接返回
                return getConnAndSetSchema(trinoSourceDTO.getUrl(), catalog, trinoSourceDTO.getSchema(), properties);
            }
            // 处理 kerberos
            buildKerberosConfig(properties, kerberosConfig);
            PropertiesUtil.convertToProp(trinoSourceDTO, properties);
            // 加锁，防止其他线程正在进行 kerberos 认证
            synchronized (DataSourceType.class) {
                // 不在 properties 设置，不一致会 SpnegoHandler 报错
                System.setProperty(KerberosConstant.KEY_JAVA_SECURITY_KRB5_CONF, MapUtils.getString(kerberosConfig, KerberosConstant.KEY_JAVA_SECURITY_KRB5_CONF));
                Config.refresh();
                return getConnAndSetSchema(trinoSourceDTO.getUrl(), catalog, trinoSourceDTO.getSchema(), properties);
            }
        } catch (SQLException e) {
            // 对异常进行统一处理
            throw new SourceException(errorAdapter.connAdapter(e.getMessage(), errorPattern), e);
        }
    }

    /**
     * 处理 SSL 认证信息
     *
     * @param properties       prop 配置
     * @param sslConfiguration ssl 配置
     */
    private void buildSSLConfig(Properties properties, SSLUtil.SSLConfiguration sslConfiguration) {
        if (Objects.nonNull(sslConfiguration)) {
            // 设置 ssl 参数
            PropertyUtil.putIfNotNull(properties, DtClassConsistent.SSLConsistent.SSL, "true");
            PropertyUtil.putIfNotNull(properties, DtClassConsistent.SSLConsistent.SSL_KEYSTORE_PATH, sslConfiguration.getKeyStorePath());
            PropertyUtil.putIfNotNull(properties, DtClassConsistent.SSLConsistent.SSL_KEYSTORE_PASSWORD, sslConfiguration.getKeyStorePassword());
            PropertyUtil.putIfNotNull(properties, DtClassConsistent.SSLConsistent.SSL_KEYSTORE_TYPE, sslConfiguration.getKeyStoreType());
            PropertyUtil.putIfNotNull(properties, DtClassConsistent.SSLConsistent.SSL_TRUST_STORE_PATH, sslConfiguration.getTrustStorePath());
            PropertyUtil.putIfNotNull(properties, DtClassConsistent.SSLConsistent.SSL_TRUSTSTORE_PASSWORD, sslConfiguration.getTrustStorePassword());
            PropertyUtil.putIfNotNull(properties, DtClassConsistent.SSLConsistent.SSL_TRUSTSTORE_TYPE, sslConfiguration.getTrustStoreType());
        }
    }

    /**
     * 处理 kerberos 配置
     *
     * @param properties     prop 配置
     * @param kerberosConfig kerberos 配置
     */
    private void buildKerberosConfig(Properties properties, Map<String, Object> kerberosConfig) {
        KerberosUtil.downloadAndReplace(kerberosConfig);
        String keytabPath = MapUtils.getString(kerberosConfig, KerberosConstant.PRINCIPAL_FILE);
        AssertUtils.notBlank(keytabPath, "keytab path can't be null");
        String principal = MapUtils.getString(kerberosConfig, KerberosConstant.PRINCIPAL);
        if (StringUtils.isBlank(principal)) {
            List<String> principals = KerberosConfigUtil.getPrincipals(keytabPath);
            if (CollectionUtils.isEmpty(principals)) {
                throw new SourceException(String.format("The principal parsed from keytab [%s] is empty", keytabPath));
            }
            // 取第一个 principal 账号
            principal = principals.get(0);
        }
        // 设置 kerberos 参数
        PropertyUtil.putIfNotNull(properties, KERBEROS_PRINCIPAL, principal);
        PropertyUtil.putIfNotNull(properties, KERBEROS_KEYTAB_PATH, keytabPath);
        PropertyUtil.putIfNotNull(properties, DtClassConsistent.PublicConsistent.USER, principal);
        // 默认 kerberos 服务名为 trino，不设置会报错
        String KerberosRemoteServiceName = MapUtils.getString(kerberosConfig, KERBEROS_REMOTE_SERVICE_NAME, "trino");
        PropertyUtil.putIfNotNull(properties, KERBEROS_REMOTE_SERVICE_NAME, KerberosRemoteServiceName);
        log.info("kerberos properties info:\nprincipal:{}\nkeytabPath:{}\nKerberosRemoteServiceName:{}\n", principal, keytabPath, KERBEROS_REMOTE_SERVICE_NAME);
    }

    /**
     * 获取 connection 并设置 schema
     *
     * @param url        url 信息
     * @param catalog    catalog
     * @param schema     schema
     * @param properties prop 配置
     * @return 设置 schema 后的 connection
     */
    private Connection getConnAndSetSchema(String url, String catalog, String schema, Properties properties) throws SQLException {
        AssertUtils.notBlank(url, "jdbc url can't be empty");
        // 未开启 kerberos 直接返回
        Connection connection = DriverManager.getConnection(url, properties);
        // 切换 catalog 和 schema
        if (StringUtils.isNotBlank(catalog)) {
            connection.setCatalog(catalog);
        }
        // 是否校验 schema，默认为 true
        boolean needCheckSchema = true;
        if (StringUtils.isNotBlank(schema)) {
            connection.setSchema(schema);
            // 当用户传入 schema 时，不予校验，因为用户有可能是要将一个不存在的 schema 创建出来
            needCheckSchema = false;
        }
        String realCatalog = StringUtils.isBlank(catalog) ? connection.getCatalog() : catalog;
        String realSchema = StringUtils.isBlank(schema) ? connection.getSchema() : schema;
        // 校验 url 中 catalog 和 schema 是否存在
        checkCatalogSchemaExists(realCatalog, realSchema, connection, needCheckSchema);
        return connection;
    }

    /**
     * 校验 catalog 和 schema 是否存在
     *  @param catalog    catalog 信息
     * @param schema     schema 信息
     * @param connection 链接信息
     * @param needCheckSchema 是否校验 schema
     */
    private void checkCatalogSchemaExists(String catalog, String schema, Connection connection, boolean needCheckSchema) {
        // catalog 不存在直接跳过校验
        if (StringUtils.isBlank(catalog)) {
            log.warn("catalog is empty.");
            return;
        }
        // 校验 catalog 是否存在
        boolean catalogExists = false;
        try (ResultSet catalogs = connection.getMetaData().getCatalogs()){
            while (catalogs.next()) {
                if (catalog.equalsIgnoreCase(catalogs.getString(1))) {
                    catalogExists = true;
                    break;
                }
            }
        } catch (Exception e) {
            // 如果权限不足则跳过校验
            if (checkAccessDenied(e)) {
                catalogExists = true;
                log.warn("check catalog [{}] exists access denied.", catalog, e);
            } else {
                throw new SourceException(String.format("check catalog [%s] exists error: %s", catalog, e.getMessage()), e);
            }
        }
        if (!catalogExists) {
            throw new SourceException(String.format("catalog %s does not exist.", catalog));
        }

        if (StringUtils.isBlank(schema)) {
            log.warn("schema is empty.");
            return;
        }
        if (!needCheckSchema) {
            return;
        }
        // 校验 schema 是否存在
        boolean schemaExists = false;
        try (ResultSet schemas = connection.getMetaData().getSchemas(catalog, null)){
            while (schemas.next()) {
                if (schema.equalsIgnoreCase(schemas.getString(1))) {
                    schemaExists = true;
                    break;
                }
            }
        } catch (Exception e) {
            // 如果权限不足则跳过校验
            if (checkAccessDenied(e)) {
                schemaExists = true;
                log.warn("check schema [{}] exists access denied.", schema, e);
            } else {
                throw new SourceException(String.format("check schema [%s] exists error: %s", schema, e.getMessage()), e);
            }
        }
        if (!schemaExists) {
            throw new SourceException(String.format("schema %s does not exist.", schema));
        }
    }

    @Override
    protected String getCallProc(String procName) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    protected boolean supportTransaction() {
        return false;
    }

    protected boolean supportProcedure(String sql) {
        return false;
    }

    protected String getDropProc(String procName) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    private boolean checkAccessDenied(Exception e) {
        return e != null && StringUtils.isNotBlank(e.getMessage()) && StringUtils.containsIgnoreCase(e.getMessage(), "Access Denied");
    }
}
