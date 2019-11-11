package com.dtstack.engine.dtscript.execution.mysql;
import com.dtstack.engine.dtscript.execution.rdbs.RdbsClient;
import com.dtstack.engine.dtscript.execution.rdbs.executor.ConnFactory;

public class MysqlClient extends RdbsClient {

    public MysqlClient() {
        this.dbType = "mysql";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new MysqlConnFactory();
    }
}
