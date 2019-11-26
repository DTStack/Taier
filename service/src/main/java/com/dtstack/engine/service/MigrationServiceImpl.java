package com.dtstack.engine.service;

import com.dtstack.engine.service.node.MasterNode;

import java.util.Map;

/**
 * Created by sishu.yss on 2017/3/14.
 */
public class MigrationServiceImpl {

    private MasterNode masterNode = MasterNode.getInstance();

    public void migrate(Map<String,Object> params) throws Exception{
        String node = (String)params.get("node");
        masterNode.dataMigration(node);
    }
}