package com.dtstack.taier.datasource.plugin.tidb;

import com.dtstack.taier.datasource.plugin.mysql5.MysqlConnFactory;

/**
 * @author luming
 * @date 2022/2/23
 */
public class TidbConnFactory extends MysqlConnFactory {
    @Override
    protected boolean supportProcedure(String sql) {
        return false;
    }
}
