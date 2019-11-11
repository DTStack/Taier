package com.dtstack.engine.dtscript.execution.sqlserver;


import com.dtstack.engine.dtscript.execution.rdbs.RdbsClient;
import com.dtstack.engine.dtscript.execution.rdbs.executor.ConnFactory;

public class SqlserverClient extends RdbsClient {
    public SqlserverClient() {
        this.dbType = "sqlserver";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new SqlserverConnFactory();
    }
}
