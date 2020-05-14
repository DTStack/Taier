package com.dtstack.engine.rdbs.mysql;
import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

public class MysqlClient extends AbstractRdbsClient {

    public MysqlClient() {
        super();
        this.dbType = "mysql";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new MysqlConnFactory();
    }
}
