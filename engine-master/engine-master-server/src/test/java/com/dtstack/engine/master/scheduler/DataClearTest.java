/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.domain.ScheduleDict;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.dao.TestScheduleDictDao;
import com.dtstack.engine.dao.TestScheduleJobDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.server.scheduler.JobDataClear;
import com.dtstack.engine.master.utils.Template;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * @author yuebai
 * @date 2021-07-02
 */
public class DataClearTest extends AbstractTest {

    @Autowired
    private TestScheduleJobDao scheduleJobDao;

    @Autowired
    private JobDataClear jobDataClear;

    @Autowired
    private TestScheduleDictDao scheduleDictDao;

    @Autowired
    private ScheduleDictDao dictDao;

    @Test
    public void testData() {
        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        scheduleJobTemplate.setGmtCreate(new Timestamp(DateTime.now().plusMonths(-4).getMillis()));
        scheduleJobTemplate.setGmtModified(new Timestamp(DateTime.now().plusMonths(-4).getMillis()));
        scheduleJobDao.insertWithCustomGmt(scheduleJobTemplate);

        ScheduleJob scheduleJobTemplate2 = Template.getScheduleJobTemplate();
        scheduleJobTemplate2.setJobId(UUID.randomUUID().toString());
        scheduleJobTemplate2.setGmtCreate(new Timestamp(DateTime.now().plusMonths(-2).getMillis()));
        scheduleJobTemplate2.setGmtModified(new Timestamp(DateTime.now().plusMonths(-2).getMillis()));
        scheduleJobTemplate2.setJobKey(UUID.randomUUID().toString());
        scheduleJobDao.insertWithCustomGmt(scheduleJobTemplate2);

        ScheduleDict scheduleDict = dictDao.getByNameValue(DictType.DATA_CLEAR_NAME.type, "schedule_job", null, null);
        if (null == scheduleDict) {
            scheduleDict = new ScheduleDict();
            scheduleDict.setDataType("STRING");
            scheduleDict.setDictCode(DictType.DATA_CLEAR_NAME.name());
            scheduleDict.setDictName("schedule_job");
            scheduleDict.setType(DictType.DATA_CLEAR_NAME.type);
            JSONObject config = new JSONObject();
            config.put("clearDateConfig",60);
            config.put("deleteDateConfig",60);
            scheduleDict.setDictValue(config.toJSONString());
            scheduleDict.setSort(1);
            scheduleDictDao.insert(scheduleDict);
        }
        List<ScheduleDict> scheduleDicts = dictDao.listDictByType(DictType.DATA_CLEAR_NAME.type);
        ReflectionTestUtils.invokeMethod(jobDataClear,"dataClear",scheduleDicts);
    }
}
