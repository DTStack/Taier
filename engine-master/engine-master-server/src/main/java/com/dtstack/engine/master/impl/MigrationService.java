package com.dtstack.engine.master.impl;

import com.dtstack.engine.master.failover.FailoverStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by sishu.yss on 2017/3/14.
 */
@Service
public class MigrationService implements com.dtstack.engine.api.service.MigrationService {

    @Autowired
    private FailoverStrategy failoverStrategy;

    public void migrate(Map<String,Object> params) throws Exception{
        String node = (String)params.get("node");
        failoverStrategy.dataMigration(node);
    }
}