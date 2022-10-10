package com.dtstack.taier.datasource.plugin.impala;

import com.dtstack.taier.datasource.plugin.common.exception.ErrorCode;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ImpalaSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;

import java.security.PrivilegedAction;
import java.sql.Connection;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 20:16 2020/1/7
 * @Description：Impala 工厂连接
 */
@Slf4j
public class ImpalaConnFactory extends ConnFactory {
    public ImpalaConnFactory() {
        this.driverName = "com.cloudera.impala.jdbc.Driver";
        this.errorPattern = new ImpalaErrorPattern();
    }

    @Override
    public Connection getConn(ISourceDTO sourceDTO) throws Exception {
        init();
        ImpalaSourceDTO impalaSourceDTO = (ImpalaSourceDTO) sourceDTO;
        Connection connection = KerberosLoginUtil.loginWithUGI(impalaSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<Connection>) () -> {
                    try {
                        return super.getConn(impalaSourceDTO);
                    } catch (Exception e) {
                        throw new SourceException(e.getMessage(), e);
                    }
                }
        );

        return ImpalaDriverUtil.setSchema(connection, impalaSourceDTO.getSchema());
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
