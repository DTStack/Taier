package com.dtstack.engine.rdbs.presto;

import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

public class PrestoClient extends AbstractRdbsClient {

    public PrestoClient() {
        this.dbType = "presto";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new PrestoConnFactory();
    }
}
