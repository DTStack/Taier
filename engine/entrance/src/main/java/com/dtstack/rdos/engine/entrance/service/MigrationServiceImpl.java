package com.dtstack.rdos.engine.entrance.service;

import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;

import java.util.Map;

/**
 * Created by sishu.yss on 2017/3/14.
 */
public class MigrationServiceImpl {

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    public void migrate(Map<String,Object> params) throws Exception{
        String node = (String)params.get("node");
        zkDistributed.dataMigration(node);
    }
}
