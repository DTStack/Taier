package com.dtstack.engine.rdbs.sqlserver;


import com.dtstack.engine.rdbs.common.RdbsClient;
import com.dtstack.engine.rdbs.common.executor.ConnFactory;

public class SqlserverClient extends RdbsClient {
    public SqlserverClient() {
        this.dbType = "sqlserver";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new SqlserverConnFactory();
    }
}
