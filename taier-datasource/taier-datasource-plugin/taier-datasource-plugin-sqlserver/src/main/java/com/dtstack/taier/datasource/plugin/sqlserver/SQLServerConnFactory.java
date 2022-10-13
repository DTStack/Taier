package com.dtstack.taier.datasource.plugin.sqlserver;

import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataBaseType;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 15:30 2020/1/7
 * @Description：连接器工厂类
 */
@Slf4j
public class SQLServerConnFactory extends ConnFactory {
    public SQLServerConnFactory() {
        // 兼容 JTDS 逻辑
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        testSql = DataBaseType.SQLServer.getTestSql();
        errorPattern = new SqlServerErrorPattern();
    }

    @Override
    public String getCreateProcHeader(String procName) {
        return String.format("create procedure \"%s\" as\n", procName);
    }

    @Override
    public String getCallProc(String procName) {
        return String.format("execute \"%s\"", procName);
    }

    @Override
    protected String getDriverClassName(ISourceDTO source) {
        RdbmsSourceDTO sqlServerSourceDTO = (RdbmsSourceDTO) source;
        String url = sqlServerSourceDTO.getUrl();
        if (StringUtils.isEmpty(url)) {
            throw new SourceException("url can't be null.");
        }
        if (StringUtils.startsWithIgnoreCase(url.trim(), "jdbc:sqlserver")) {
            return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        } else {
            return "net.sourceforge.jtds.jdbc.Driver";
        }
    }
}
