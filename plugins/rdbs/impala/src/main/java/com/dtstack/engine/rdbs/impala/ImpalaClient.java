package com.dtstack.engine.rdbs.impala;

import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

public class ImpalaClient extends AbstractRdbsClient {

    public ImpalaClient() {
        this.dbType = "impala";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new ImpalaConnFactory();
    }

}
