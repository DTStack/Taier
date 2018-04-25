package com.dtstack.rdos.engine.execution.mysql;
import com.dtstack.rdos.engine.execution.rdbs.RdbsClient;
import com.dtstack.rdos.engine.execution.rdbs.executor.ConnFactory;

public class MysqlClient extends RdbsClient {

    public MysqlClient() {
        this.dbType = "mysql";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new MysqlConnFactory();
    }
}
