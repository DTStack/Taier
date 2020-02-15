package com.dtstack.engine.rdbs.tidb;
import com.dtstack.engine.rdbs.common.RdbsClient;
import com.dtstack.engine.rdbs.common.executor.ConnFactory;

public class MysqlClient extends RdbsClient {

    public MysqlClient() {
        this.dbType = "mysql";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new MysqlConnFactory();
    }
}
