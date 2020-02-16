package com.dtstack.engine.master.impl;

import com.dtstack.engine.master.MasterNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by sishu.yss on 2017/3/14.
 */
@Service
public class MigrationService {

    @Autowired
    private MasterNode masterNode;

    public void migrate(Map<String,Object> params) throws Exception{
        String node = (String)params.get("node");
        masterNode.dataMigration(node);
    }
}