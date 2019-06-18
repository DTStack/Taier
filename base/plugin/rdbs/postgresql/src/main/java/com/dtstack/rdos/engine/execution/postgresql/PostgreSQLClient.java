package com.dtstack.rdos.engine.execution.postgresql;

import com.dtstack.rdos.engine.execution.rdbs.RdbsClient;
import com.dtstack.rdos.engine.execution.rdbs.executor.ConnFactory;

public class PostgreSQLClient extends RdbsClient {

    public PostgreSQLClient() {
        this.dbType = "postgresql";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new PostgreSQLConnFactory();
    }
}
