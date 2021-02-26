package com.dtstack.engine.master.impl;

import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.master.AbstractTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yuebai
 * @date 2021-02-26
 */
public class StreamCheckPointTest extends AbstractTest {

    @Autowired
    private EngineJobCheckpointDao engineJobCheckpointDao;


    @Test
    public void testSql(){
        engineJobCheckpointDao.getByTaskEngineIdAndCheckpointIndexAndCount("","",1,2);
    }
}
