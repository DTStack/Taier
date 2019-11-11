package com.dtstack.engine.dtscript.execution.postgresql;

import com.dtstack.engine.dtscript.execution.rdbs.RdbsClient;
import com.dtstack.engine.dtscript.execution.rdbs.executor.ConnFactory;

public class PostgreSQLClient extends RdbsClient {

    public PostgreSQLClient() {
        this.dbType = "postgresql";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new PostgreSQLConnFactory();
    }
}
