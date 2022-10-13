package com.dtstack.taier.datasource.plugin.oracle;

import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.OracleSourceDTO;
import com.dtstack.taier.datasource.api.source.DataBaseType;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

/**
 * Oracle 连接工厂
 *
 * @author ：nanqi
 * date：Created in 下午7:47 2022/3/3
 * company: www.dtstack.com
 */
@Slf4j
public class OracleConnFactory extends ConnFactory {
    public OracleConnFactory() {
        driverName = DataBaseType.Oracle.getDriverClassName();
        errorPattern = new OracleErrorPattern();
    }

    @Override
    public String getCreateProcHeader(String procName) {
        return String.format("create  procedure \"%s\" Authid Current_User as\n", procName);
    }

    @Override
    public Connection getConn(ISourceDTO sourceDTO) throws Exception {
        Connection conn = super.getConn(sourceDTO);
        OracleSourceDTO oracleSourceDTO = (OracleSourceDTO) sourceDTO;
        // 为空直接返回
        if (StringUtils.isBlank(oracleSourceDTO.getPdb())) {
            return conn;
        }
        // 切换 container session
        alterSession(conn, oracleSourceDTO.getPdb());
        return conn;
    }

    /**
     * oracle 切换数据库
     *
     * @param connection 数据库链接
     * @param pdb        pdb
     */
    private void alterSession(Connection connection, String pdb) {
        if (StringUtils.isBlank(pdb)) {
            log.warn("pdb is null, No switching...");
            return;
        }
        try {
            // 切换 pdb session，相当于 mysql 的use db ，此处执行后不关闭 connection
            DBUtil.executeSql(connection, String.format(SqlConstant.ALTER_PDB_SESSION, pdb));
        } catch (Exception e) {
            log.error("alter oracle container session error... {}", e.getMessage(), e);
        }
    }
}
