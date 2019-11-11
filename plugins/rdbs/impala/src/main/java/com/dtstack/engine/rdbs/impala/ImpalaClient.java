package com.dtstack.engine.rdbs.impala;

import com.dtstack.engine.rdbs.common.RdbsClient;
import com.dtstack.engine.rdbs.common.executor.ConnFactory;

public class ImpalaClient extends RdbsClient {

    public ImpalaClient() {
        this.dbType = "impala";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new ImpalaConnFactory();
    }

}
