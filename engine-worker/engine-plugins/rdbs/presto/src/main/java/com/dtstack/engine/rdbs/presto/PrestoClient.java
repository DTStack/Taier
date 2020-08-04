package com.dtstack.engine.rdbs.presto;

import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

/**
 * 在服务器上部署presto 0.100版本能成功，其余版本都有404问题。
 */
public class PrestoClient extends AbstractRdbsClient {

    public PrestoClient() {
        this.dbType = "presto";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new PrestoConnFactory();
    }
}
