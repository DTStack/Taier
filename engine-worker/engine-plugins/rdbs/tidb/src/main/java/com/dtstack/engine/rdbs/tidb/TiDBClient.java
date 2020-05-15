package com.dtstack.engine.rdbs.tidb;
import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

public class TiDBClient extends AbstractRdbsClient {

    public TiDBClient() {
        this.dbType = "tidb";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new TiDBConnFactory();
    }
}
