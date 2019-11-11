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

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author jiangbo
 * @date 2019/7/3
 */
public class KylinConfigTest {

    @Test
    public void getFormatUrlTest(){
        List<String> testData = Arrays.asList(
                "http://127.0.0.1:7070"
                ,"http://127.0.0.1:7070/kylin"
                ,"127.0.0.1:7070"
                ,"127.0.0.1:7070/kylin");

        KylinConfig kylinConfig = new KylinConfig();

        for (String testDatum : testData) {
            String formatUrl = kylinConfig.getFormatUrl(testDatum);
            Assert.assertEquals("http://127.0.0.1:7070", formatUrl);
        }
    }
}
