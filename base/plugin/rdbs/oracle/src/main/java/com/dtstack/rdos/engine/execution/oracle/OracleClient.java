package com.dtstack.rdos.engine.execution.oracle;


import com.dtstack.rdos.engine.execution.rdbs.RdbsClient;
import com.dtstack.rdos.engine.execution.rdbs.executor.ConnFactory;

public class OracleClient extends RdbsClient {

    public OracleClient() {
        this.dbType = "oracle";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new OracleConnFactory();
    }
}
