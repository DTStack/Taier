package com.dtstack.rdos.engine.execution.hive;

import com.dtstack.rdos.engine.execution.rdbs.RdbsClient;
import com.dtstack.rdos.engine.execution.rdbs.executor.ConnFactory;


public class HiveClient extends RdbsClient {

    public HiveClient() {
        this.dbType = "hive";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new HiveConnFactory();
    }

}
