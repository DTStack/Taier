package com.dtstack.taier.datasource.plugin.inceptor;

import com.dtstack.taier.datasource.plugin.common.utils.PropertiesUtil;
import com.dtstack.taier.datasource.plugin.common.exception.ErrorCode;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.InceptorSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataBaseType;
import lombok.extern.slf4j.Slf4j;

import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * inceptor 连接工厂
 *
 * @author ：wangchuan
 * date：Created in 下午2:19 2021/5/6
 * company: www.dtstack.com
 */
@Slf4j
public class InceptorConnFactory extends ConnFactory {

    public InceptorConnFactory() {
        this.driverName = DataBaseType.INCEPTOR.getDriverClassName();
        this.errorPattern = new InceptorErrorPattern();
    }

    @Override
    public Connection getConn(ISourceDTO sourceDTO) throws Exception {
        init();
        InceptorSourceDTO inceptorSourceDTO = (InceptorSourceDTO) sourceDTO;
        Connection connection = KerberosLoginUtil.loginWithUGI(inceptorSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<Connection>) () -> {
                    try {
                        DriverManager.setLoginTimeout(30);
                        String urlWithoutSchema = InceptorDriverUtil.removeSchema(inceptorSourceDTO.getUrl());

                        Properties properties = PropertiesUtil.convertToProp(inceptorSourceDTO);
                        return DriverManager.getConnection(urlWithoutSchema, properties);
                    } catch (SQLException e) {
                        // 对异常进行统一处理
                        throw new SourceException(errorAdapter.connAdapter(e.getMessage(), errorPattern), e);
                    }
                }
        );
        return InceptorDriverUtil.setSchema(connection, inceptorSourceDTO.getUrl(), inceptorSourceDTO.getSchema());
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
}
