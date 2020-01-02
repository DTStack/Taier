package com.dtstack.engine.rdbs.oracle;


import com.dtstack.engine.rdbs.common.RdbsClient;
import com.dtstack.engine.rdbs.common.executor.ConnFactory;

public class OracleClient extends RdbsClient {

    public OracleClient() {
        this.dbType = "oracle";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new OracleConnFactory();
    }
}
