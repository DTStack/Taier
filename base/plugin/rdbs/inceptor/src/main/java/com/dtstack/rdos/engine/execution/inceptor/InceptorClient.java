package com.dtstack.rdos.engine.execution.inceptor;


import com.dtstack.rdos.engine.execution.rdbs.RdbsClient;
import com.dtstack.rdos.engine.execution.rdbs.executor.ConnFactory;

public class InceptorClient extends RdbsClient {
    public InceptorClient() {
        this.dbType = "inceptor";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new InceptorConnFactory();
    }
}
