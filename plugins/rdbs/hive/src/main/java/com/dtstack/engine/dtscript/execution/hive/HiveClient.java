package com.dtstack.engine.dtscript.execution.hive;

import com.dtstack.engine.dtscript.execution.rdbs.RdbsClient;
import com.dtstack.engine.dtscript.execution.rdbs.executor.ConnFactory;


public class HiveClient extends RdbsClient {

    public HiveClient() {
        this.dbType = "hive";
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new HiveConnFactory();
    }

}
