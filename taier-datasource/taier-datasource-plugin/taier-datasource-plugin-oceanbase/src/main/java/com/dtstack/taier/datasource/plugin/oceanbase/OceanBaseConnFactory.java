package com.dtstack.taier.datasource.plugin.oceanbase;

import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.OceanBaseSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataBaseType;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.Statement;

/**
 * @company: www.dtstack.com
 * @Author ：qianyi
 * @Date ：Created in 14:18 2021/4/21
 */
public class OceanBaseConnFactory extends ConnFactory {
    public OceanBaseConnFactory() {
        driverName = DataBaseType.OceanBase.getDriverClassName();
        this.errorPattern = new OceanBaseErrorPattern();
    }

    @Override
    public Connection getConn(ISourceDTO sourceDTO) throws Exception {
        Connection connection = super.getConn(sourceDTO);
        OceanBaseSourceDTO source = (OceanBaseSourceDTO) sourceDTO;
        String schema = source.getSchema();
        if (StringUtils.isNotEmpty(schema)) {
            try (Statement statement = connection.createStatement()) {
                //选择schema
                String useSchema = String.format("USE %s", schema);
                statement.execute(useSchema);
            } catch (Exception e) {
                throw new SourceException(e.getMessage(), e);
            }
        }
        return connection;
    }

    @Override
    protected String getCreateProcHeader(String procName) {
        return String.format("create procedure %s() as \n", procName);
    }

    @Override
    public String getCallProc(String procName) {
        return String.format("EXEC SQL CALL %s()", procName);
    }

    @Override
    public String getDropProc(String procName) {
        return String.format("DROP PROCEDURE %s", procName);
    }
}
