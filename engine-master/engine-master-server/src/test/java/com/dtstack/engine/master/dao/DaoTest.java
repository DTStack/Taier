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

package com.dtstack.engine.master.dao;

import com.dtstack.engine.domain.*;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.AbstractTest;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/2/19 2:10 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */

public class DaoTest extends AbstractTest {

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ScheduleSqlTextTempDao sqlTextTempDao;

    @Autowired
    private LineageTableTableUniqueKeyRefDao tableTableUniqueKeyRefDao;

    @Autowired
    private LineageColumnColumnUniqueKeyRefDao columnUniqueKeyRefDao;

    @Test
    public void testTenantDao(){
        Tenant byDtUicTenantId = tenantDao.getByDtUicTenantId(1L);
        List<Tenant> tenantList = tenantDao.listAllTenantByDtUicTenantIds(Lists.newArrayList(1L, 2L));
        System.out.println(byDtUicTenantId);
        System.out.println(tenantList);
    }

    @Test
    public void testEngineJobCacheDao() {
        EngineJobCache testJobId = engineJobCacheDao.getOne("jobId");

        System.out.println(testJobId);

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testSqlTextInsert(){

        ScheduleSqlTextTemp sqlTextTemp = new ScheduleSqlTextTemp();
        sqlTextTemp.setSqlText("select * from chener");
        sqlTextTemp.setJobId("falfalfjl");
        sqlTextTemp.setEngineType("hive");
        sqlTextTempDao.insert(sqlTextTemp);
        ScheduleSqlTextTemp temp = sqlTextTempDao.selectByJobId(sqlTextTemp.getJobId());
        System.out.println(temp);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testLineageTableTableRefInsert(){

        LineageTableTableUniqueKeyRef uniqueKeyRef = new LineageTableTableUniqueKeyRef();
        uniqueKeyRef.setVersionId(1);
        uniqueKeyRef.setLineageTableTableId(100L);
        uniqueKeyRef.setAppType(1);
        uniqueKeyRef.setUniqueKey("100");
        List<LineageTableTableUniqueKeyRef> keyRefs = Collections.singletonList(uniqueKeyRef);
        Integer integer = tableTableUniqueKeyRefDao.batchInsert(keyRefs);
        Assert.assertEquals("1",integer.toString());
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testLineageColumnColumnRefInsert(){

        LineageColumnColumnUniqueKeyRef uniqueKeyRef = new LineageColumnColumnUniqueKeyRef();
        uniqueKeyRef.setLineageColumnColumnId(100L);
        uniqueKeyRef.setUniqueKey("200");
        uniqueKeyRef.setAppType(1);
        uniqueKeyRef.setVersionId(0);
        List<LineageColumnColumnUniqueKeyRef> keyRefs = Collections.singletonList(uniqueKeyRef);
        Integer integer = columnUniqueKeyRefDao.batchInsert(keyRefs);
        Assert.assertEquals("1",integer.toString());
    }



}
