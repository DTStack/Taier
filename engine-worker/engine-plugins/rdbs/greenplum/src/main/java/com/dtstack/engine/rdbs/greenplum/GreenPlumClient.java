package com.dtstack.engine.rdbs.greenplum;

import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

public class GreenPlumClient extends AbstractRdbsClient {

    public GreenPlumClient() {
        this.dbType = "greenplum";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new GreenPlumConnFactory();
    }
}
