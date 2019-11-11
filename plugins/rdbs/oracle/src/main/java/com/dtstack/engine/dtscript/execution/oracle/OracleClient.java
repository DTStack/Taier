package com.dtstack.engine.dtscript.execution.oracle;


import com.dtstack.engine.dtscript.execution.rdbs.RdbsClient;
import com.dtstack.engine.dtscript.execution.rdbs.executor.ConnFactory;

public class OracleClient extends RdbsClient {

    public OracleClient() {
        this.dbType = "oracle";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new OracleConnFactory();
    }
}
