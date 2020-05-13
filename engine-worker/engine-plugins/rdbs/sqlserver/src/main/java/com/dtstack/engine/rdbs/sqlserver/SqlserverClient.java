package com.dtstack.engine.rdbs.sqlserver;


import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

public class SqlserverClient extends AbstractRdbsClient {
    public SqlserverClient() {
        super();
        this.dbType = "sqlserver";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new SqlserverConnFactory();
    }
}
