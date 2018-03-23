package com.dtstack.rdos.engine.execution.sqlserver;


import com.dtstack.rdos.engine.execution.rdbs.RdbsClient;
import com.dtstack.rdos.engine.execution.rdbs.executor.ConnFactory;

public class SqlserverClient extends RdbsClient {
    public SqlserverClient() {
        this.dbType = "sqlserver";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new SqlserverConnFactory();
    }
}
