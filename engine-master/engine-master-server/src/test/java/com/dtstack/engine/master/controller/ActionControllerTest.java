package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.vo.action.ActionJobStatusVO;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.fail;


public class ActionControllerTest extends AbstractTest {

    @Autowired
    ActionController actionController;

    @Test
    public void testGetListJobStatusByJobIds() {
        List<String> jobIds = Lists.newArrayList(DataCollection.getData().getScheduleJobFirst().getJobId());
        try {
            List<ActionJobStatusVO> actionJobStatusVOS = actionController.listJobStatusByJobIds(jobIds);
            System.out.println(actionJobStatusVOS);
        } catch (Exception e) {
            fail();
        }
    }
}
