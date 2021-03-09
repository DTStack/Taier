package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.fail;

/**
 * @author haier
 * @Description
 * @date 2021/3/5 10:36 上午
 */
public class StreamTaskControllerTest extends AbstractTest {

    @Autowired
    private StreamTaskController streamTaskController;

    @Autowired
    private EngineJobCheckpointDao engineJobCheckpointDao;


    @Test
    public void testGetFailedCheckPoint() {
        try {
            long time1 = new Date().getTime();
            EngineJobCheckpoint ejc = DataCollection.getData().getFailedEngineJobCheckpoint();
            long time2 = new Date().getTime();
            List<EngineJobCheckpoint> list = streamTaskController.getFailedCheckPoint(ejc.getTaskId(), time1, time2);
            System.out.println(CollectionUtils.isEmpty(list) ? list : list.get(0));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGrammarCheck(){

    }
}
