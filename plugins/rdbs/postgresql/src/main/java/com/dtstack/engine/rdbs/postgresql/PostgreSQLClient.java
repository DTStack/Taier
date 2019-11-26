package com.dtstack.engine.rdbs.postgresql;

import com.dtstack.engine.rdbs.common.RdbsClient;
import com.dtstack.engine.rdbs.common.executor.ConnFactory;

public class PostgreSQLClient extends RdbsClient {

    public PostgreSQLClient() {
        this.dbType = "postgresql";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new PostgreSQLConnFactory();
    }
}
