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

package com.dtstack.engine.master.schedule;

import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.server.scheduler.JobRichOperator;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: newman
 * Date: 2021/1/11 3:53 下午
 * Description: 单测
 * @since 1.0.0
 */
public class TestJobRichOperator extends AbstractTest {

    @Autowired
    private JobRichOperator operator;

    @Test
    public void testGetCycTimeLimitEndNow(){
        Pair<String, String> cycTime = operator.getCycTimeLimitEndNow(false);
        Pair<String, String> minCycTime = operator.getCycTimeLimitEndNow(true);

        Assert.assertEquals(cycTime.getRight(),minCycTime.getRight());
        Assert.assertNotEquals(cycTime.getLeft(),minCycTime.getLeft());
    }

    @Test
    public void testGetCycTimeLimit(){
        Pair<String, String> cycTimeLimit = operator.getCycTimeLimit();
        System.out.println("周期实例,startTime:"+ cycTimeLimit.getLeft()+":"+
                cycTimeLimit.getRight());
    }

}
