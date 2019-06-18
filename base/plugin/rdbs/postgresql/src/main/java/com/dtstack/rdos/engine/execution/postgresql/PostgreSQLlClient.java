package com.dtstack.rdos.engine.execution.postgresql;

import com.dtstack.rdos.engine.execution.rdbs.RdbsClient;
import com.dtstack.rdos.engine.execution.rdbs.executor.ConnFactory;

public class PostgreSQLlClient extends RdbsClient {

    public PostgreSQLlClient() {
        this.dbType = "postgresql";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new PostgreSQLConnFactory();
    }
}
