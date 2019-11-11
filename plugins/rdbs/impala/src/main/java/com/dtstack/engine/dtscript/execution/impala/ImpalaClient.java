package com.dtstack.engine.dtscript.execution.impala;

import com.dtstack.engine.dtscript.execution.rdbs.RdbsClient;
import com.dtstack.engine.dtscript.execution.rdbs.executor.ConnFactory;

public class ImpalaClient extends RdbsClient {

    public ImpalaClient() {
        this.dbType = "impala";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new ImpalaConnFactory();
    }

}
