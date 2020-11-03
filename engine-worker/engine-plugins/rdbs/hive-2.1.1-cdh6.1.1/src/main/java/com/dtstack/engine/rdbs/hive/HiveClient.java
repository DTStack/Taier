package com.dtstack.engine.rdbs.hive;

import com.dtstack.engine.rdbs.common.AbstractHiveClient;
import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;


public class HiveClient extends AbstractHiveClient {

    public HiveClient() {
        this.dbType = "hive";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new HiveConnFactory();
    }

}
