package com.dtstack.engine.rdbs.oracle;


import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

public class OracleClient extends AbstractRdbsClient {

    public OracleClient() {
        super();
        this.dbType = "oracle";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new OracleConnFactory();
    }
}
