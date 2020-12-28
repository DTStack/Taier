package com.dtstack.engine.rdbs.common;

import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

public class TestRdbsClient extends AbstractRdbsClient {

    public TestRdbsClient() {
        this.dbType = "mysql";
    }

    @Override
    public AbstractConnFactory getConnFactory() {
        return new TestConnFactory();
    }
}
