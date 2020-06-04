/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.master.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * Date: 2020/6/4
 * Company: www.dtstack.com
 * @author maqi
 */
public class ScheduleJobServiceTest extends BaseTest {
    @Autowired
    ScheduleJobService sheduleJobService;

    @Test
    public void testGetStatusById() {
        long notExistId = 5993333L;
        Integer statusById = sheduleJobService.getStatusById(notExistId);
        if (!Objects.isNull(statusById)) {
            Assert.assertTrue(statusById.intValue() >= 0);
        }
    }


    @Test
    public void testGetStatusJobList() {
        PageResult emptyResult = sheduleJobService.getStatusJobList(115L, 7L, 1, 9L, 0, 1, 1);
        if (!Objects.isNull(emptyResult)) {
            Assert.assertTrue(emptyResult.getTotalCount() >= 0);
        }
    }

    @Test
    public void testGetStatusCount(){
        JSONObject statusCount = sheduleJobService.getStatusCount(115L, 7L, 1, 9L);
        if (!Objects.isNull(statusCount)) {
            String all = statusCount.getString("ALL");
            Assert.assertTrue(Integer.valueOf(all) >= 0);
        }
    }


}
