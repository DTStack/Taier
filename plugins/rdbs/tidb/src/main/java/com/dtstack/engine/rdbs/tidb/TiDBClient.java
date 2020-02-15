package com.dtstack.engine.rdbs.tidb;
import com.dtstack.engine.rdbs.common.RdbsClient;
import com.dtstack.engine.rdbs.common.executor.ConnFactory;

public class TiDBClient extends RdbsClient {

    public TiDBClient() {
        this.dbType = "tidb";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new TiDBConnFactory();
    }
}
