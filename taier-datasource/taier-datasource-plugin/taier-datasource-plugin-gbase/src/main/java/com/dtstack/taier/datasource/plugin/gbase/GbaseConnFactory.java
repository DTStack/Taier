package com.dtstack.taier.datasource.plugin.gbase;

import com.dtstack.taier.datasource.api.dto.source.GBaseSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.source.DataBaseType;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 17:58 2020/1/7
 * @Description：GBase8a 连接工厂
 */
public class GbaseConnFactory extends ConnFactory {
    public GbaseConnFactory() {
        this.driverName = DataBaseType.GBase8a.getDriverClassName();
        this.errorPattern = new GbaselErrorPattern();
    }

    @Override
    public Connection getConn(ISourceDTO source) throws Exception {
        Connection conn = super.getConn(source);
        GBaseSourceDTO gBaseSourceDTO = (GBaseSourceDTO) source;
        if (StringUtils.isBlank(gBaseSourceDTO.getSchema())) {
            return conn;
        }

        // 选择 Schema
        String useSchema = String.format("USE %s", gBaseSourceDTO.getSchema());
        DBUtil.executeSql(conn, useSchema);
        return conn;
    }
}
