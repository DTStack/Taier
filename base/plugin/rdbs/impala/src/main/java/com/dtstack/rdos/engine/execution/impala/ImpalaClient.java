package com.dtstack.rdos.engine.execution.impala;

import com.dtstack.rdos.engine.execution.rdbs.RdbsClient;
import com.dtstack.rdos.engine.execution.rdbs.executor.ConnFactory;

public class ImpalaClient extends RdbsClient {

    public ImpalaClient() {
        this.dbType = "impala";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new ImpalaConnFactory();
    }

}
