package com.dtstack.taier.datasource.plugin.hive3;

import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.Hive3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataBaseType;
import com.dtstack.taier.datasource.plugin.common.DtClassConsistent;
import com.dtstack.taier.datasource.plugin.common.constant.CommonConstant;
import com.dtstack.taier.datasource.plugin.common.exception.ErrorCode;
import com.dtstack.taier.datasource.plugin.common.utils.PropertiesUtil;
import com.dtstack.taier.datasource.plugin.common.utils.ReflectUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SSLUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SqlFormatUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @company: www.dtstack.com
 * @Author ：qianyi
 * @Date ：Created in 14:03 2021/05/13
 * @Description：Hive 连接池工厂
 */
@Slf4j
public class HiveConnFactory extends ConnFactory {

    private static final String SSL_FLAG = "ssl";

    private static final String SSL_TRUST_STORE = "sslTrustStore";

    private static final String SSL_STORE_PASSWORD = "trustStorePassword";

    private static final String SUB_TYPE_INCEPTOR = "INCEPTOR";

    public HiveConnFactory() {
        this.driverName = DataBaseType.HIVE3.getDriverClassName();
        this.errorPattern = new HiveErrorPattern();
    }

    @Override
    public Connection getConn(ISourceDTO sourceDTO) throws Exception {
        init();
        Hive3SourceDTO hiveSourceDTO = (Hive3SourceDTO) sourceDTO;

        Connection connection = KerberosLoginUtil.loginWithUGI(hiveSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<Connection>) () -> {
                    try {
                        DriverManager.setLoginTimeout(30);
                        Properties properties = new Properties();
                        SSLUtil.SSLConfiguration sslConfiguration = SSLUtil.getSSLConfiguration(hiveSourceDTO);
                        dealSsl(properties, sslConfiguration);
                        properties.put(DtClassConsistent.PublicConsistent.USER, hiveSourceDTO.getUsername() == null ? "" : hiveSourceDTO.getUsername());
                        properties.put(DtClassConsistent.PublicConsistent.PASSWORD, hiveSourceDTO.getPassword() == null ? "" : hiveSourceDTO.getPassword());
                        PropertiesUtil.convertToProp(hiveSourceDTO, properties);
                        setQueue(properties, hiveSourceDTO);
                        String urlWithoutSchema = HiveDriverUtil.removeSchema(hiveSourceDTO.getUrl());
                        return DriverManager.getConnection(urlWithoutSchema, properties);
                    } catch (Exception e) {
                        // 对异常进行统一处理
                        throw new SourceException(errorAdapter.connAdapter(e.getMessage(), errorPattern), e);
                    }
                }
        );

        return HiveDriverUtil.setSchema(connection, hiveSourceDTO.getUrl(), hiveSourceDTO.getSchema());
    }

    /**
     * 设置 hive 使用的 yarn queue
     *
     * @param properties    配置信息
     * @param hiveSourceDTO 数据源配置
     */
    private void setQueue(Properties properties, Hive3SourceDTO hiveSourceDTO) {
        String queue = ReflectUtil.getFieldValueNotThrow(String.class, hiveSourceDTO, "queue", null);
        if (StringUtils.isNotBlank(queue)) {
            properties.setProperty(CommonConstant.MAPREDUCE_JOB_QUEUENAME, queue);
            properties.setProperty(CommonConstant.TEZ_QUEUE_NAME, queue);
        }
    }

    /**
     * 处理hive ssl认证信息
     *
     * @param properties       jdbc properties
     * @param sslConfiguration ssl 认证信息
     */
    private void dealSsl(Properties properties, SSLUtil.SSLConfiguration sslConfiguration) {
        if (sslConfiguration == null) {
            return;
        }

        // 兼容一下 keystore 和 truststore
        String storePath = StringUtils.isNotBlank(sslConfiguration.getTrustStorePath()) ?
                sslConfiguration.getTrustStorePath() : sslConfiguration.getKeyStorePath();

        if (StringUtils.isBlank(storePath)) {
            throw new SourceException("Hive SSL certification lack certificate path");
        }

        properties.put(SSL_FLAG, BooleanUtils.toStringTrueFalse(true));
        properties.put(SSL_TRUST_STORE, storePath);
        if (StringUtils.isNotBlank(sslConfiguration.getKeyStorePassword())) {
            properties.put(SSL_STORE_PASSWORD, sslConfiguration.getKeyStorePassword());
        }
    }

    @Override
    protected List<String> buildSqlList(SqlQueryDTO queryDTO) {
        if (queryDTO.getHiveSubType() != null
                && SUB_TYPE_INCEPTOR.equalsIgnoreCase(queryDTO.getHiveSubType())) {
            String sql = "BEGIN\n" + queryDTO.getSql() + "\nEND;\n";
            return Collections.singletonList(sql);
        } else {
            return SqlFormatUtil.splitIgnoreQuota(queryDTO.getSql(), ';');
        }
    }

    protected boolean supportTransaction() {
        return false;
    }

    protected boolean supportSelectSql() {
        return true;
    }

    protected boolean supportProcedure(String sql) {
        return false;
    }

    @Override
    protected String getCallProc(String procName) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    protected String getDropProc(String procName) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }
}
