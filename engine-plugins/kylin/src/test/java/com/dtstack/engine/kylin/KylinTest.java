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


package com.dtstack.engine.kylin;

import org.junit.Test;


/**
 * @author jiangbo
 * @date 2019/7/3
 */
public class KylinTest {

    @Test
    public void submitJobTest(){
        KylinConfig config = getConfig();
        KylinHttpClient client = new KylinHttpClient();
        client.init(config);

        KylinHttpClient.RequestResult requestResult = client.buildCube(config);
        System.out.println(requestResult.getBody());
    }

    private KylinConfig getConfig(){
        return new KylinConfig().setHostPort("http://172.16.8.84:7070")
                .setUsername("ADMIN")
                .setPassword("KYLIN")
                .setConnectParams(null)
                .setBuildType("MERGE")
                .setCubeName("student_cube")
                .setStartTime(null)
                .setEndTime(null);
    }
}
