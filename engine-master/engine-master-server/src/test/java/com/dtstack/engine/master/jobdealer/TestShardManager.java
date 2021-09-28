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

package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.jobdealer.cache.ShardManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 10:07 上午 2020/11/28
 */
public class TestShardManager extends AbstractTest {



    @Test
    public void testGetResource(){

        ShardManager shardManager = new ShardManager("aa");
        String jobResource = shardManager.getJobResource();
        Assert.assertEquals("aa",jobResource);
    }

}
