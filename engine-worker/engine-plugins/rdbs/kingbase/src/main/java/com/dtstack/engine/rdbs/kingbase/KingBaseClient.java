package com.dtstack.engine.rdbs.kingbase;

import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

public class KingBaseClient extends AbstractRdbsClient {

    public KingBaseClient() {
        this.dbType = "kingbase";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new KingBaseConnFactory();
    }
}
