package com.dtstack.taier.datasource.plugin.phoenix5;

import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Phoenix5SourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataBaseType;
import com.dtstack.taier.datasource.plugin.common.constant.KerberosConstant;
import com.dtstack.taier.datasource.plugin.common.utils.PropertiesUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosUtil;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * @author ：nanqi
 * company: www.dtstack.com
 * date ：Created in 10:32 2020/7/8
 * description：默认 Phoenix5 连接工厂
 */
@Slf4j
public class PhoenixConnFactory extends ConnFactory {

    public PhoenixConnFactory() {
        this.driverName = DataBaseType.Phoenix.getDriverClassName();
        this.testSql = DataBaseType.Phoenix.getTestSql();
        this.errorPattern = new PhoenixErrorPattern();
    }

    @Override
    public Connection getConn(ISourceDTO sourceDTO) throws Exception {
        init();
        Phoenix5SourceDTO phoenix5SourceDTO = (Phoenix5SourceDTO) sourceDTO;
        Map<String, Object> kerberosConfig = phoenix5SourceDTO.getKerberosConfig();
        return KerberosLoginUtil.loginWithUGI(phoenix5SourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<Connection>) () -> {
                    Properties props = new Properties();
                    if (StringUtils.isNotBlank(phoenix5SourceDTO.getUsername())) {
                        props.setProperty("username", phoenix5SourceDTO.getUsername());
                    }
                    if (StringUtils.isNotBlank(phoenix5SourceDTO.getPassword())) {
                        props.setProperty("password", phoenix5SourceDTO.getPassword());
                    }
                    // 设置kerberos认证参数
                    if (MapUtils.isNotEmpty(kerberosConfig)) {
                        // principal
                        String principal = MapUtils.getString(kerberosConfig, KerberosConstant.PRINCIPAL);
                        if (StringUtils.isNotBlank(principal)) {
                            String masterPrincipal = "hbase/_HOST@" + KerberosUtil.getDomainRealm(principal);
                            props.setProperty(KerberosConstant.HBASE_MASTER_PRINCIPAL, masterPrincipal);
                            props.setProperty(KerberosConstant.HBASE_REGION_PRINCIPAL, masterPrincipal);
                            props.setProperty(KerberosConstant.PHOENIX_QUERYSERVER_KERBEROS_PRINCIPAL, principal);
                            props.setProperty(KerberosConstant.HADOOP_SECURITY_AUTHORIZATION, "kerberos");
                            props.setProperty(KerberosConstant.HBASE_SECURITY_AUTHORIZATION, "kerberos");
                        }
                    }
                    PropertiesUtil.convertToProp(phoenix5SourceDTO, props);
                    try {
                        return DriverManager.getConnection(phoenix5SourceDTO.getUrl(), props);
                    } catch (Exception e) {
                        if (e instanceof SQLException) {
                            props.setProperty("phoenix.schema.isNamespaceMappingEnabled", "true");
                            try {
                                return DriverManager.getConnection(phoenix5SourceDTO.getUrl(), props);
                            } catch (SQLException throwables) {
                                throw new SourceException("getPhoenix5Connection error : " + e.getMessage(), e);
                            }
                        }
                        throw new SourceException("getPhoenix5Connection error : " + e.getMessage(), e);
                    }
                }
        );
    }
}
