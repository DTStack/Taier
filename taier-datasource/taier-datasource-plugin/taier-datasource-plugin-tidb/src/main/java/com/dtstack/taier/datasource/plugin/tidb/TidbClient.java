package com.dtstack.taier.datasource.plugin.tidb;

import com.dtstack.taier.datasource.plugin.mysql5.MysqlClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.source.DataSourceType;

/**
 * @author luming
 * @date 2022/2/23
 */
public class TidbClient extends MysqlClient{
    @Override
    protected ConnFactory getConnFactory() {
        return new TidbConnFactory();
    }

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.TiDB;
    }
}
