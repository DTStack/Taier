package com.dtstack.engine.rdbs.hive;

import com.dtstack.engine.rdbs.common.RdbsClient;
import com.dtstack.engine.rdbs.common.executor.ConnFactory;


public class HiveClient extends RdbsClient {

    public HiveClient() {
        this.dbType = "hive";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new HiveConnFactory();
    }

}
