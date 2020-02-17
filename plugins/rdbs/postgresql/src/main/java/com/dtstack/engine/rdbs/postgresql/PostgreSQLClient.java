package com.dtstack.engine.rdbs.postgresql;

import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

public class PostgreSQLClient extends AbstractRdbsClient {

    public PostgreSQLClient() {
        this.dbType = "postgresql";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new PostgreSQLConnFactory();
    }
}
