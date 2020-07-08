package com.dtstack.engine.master.controller;

import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActionControllerTest extends AbstractTest {

    @Autowired
    ActionController actionController;

    @Test
    public void testGetListJobStatusByJobIds() {
        List<String> jobIds = Lists.newArrayList(DataCollection.getData().getScheduleJobFirst().getJobId());
        Map<String, Object> map = new HashMap<>();
        map.put("jobIds", jobIds);
        String result = actionController.getListJobStatusByJobIds(map);
        System.out.println(result);
    }
}
