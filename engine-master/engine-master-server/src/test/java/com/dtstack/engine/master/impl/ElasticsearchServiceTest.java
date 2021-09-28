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

package com.dtstack.engine.master.impl;

import com.dtstack.engine.master.AbstractTest;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpHost;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 * @author basion
 * @Classname ElasticsearchServiceTest
 * @Description unit test for ElasticsearchService
 * @Date 2020-11-26 15:29:10
 * @Created basion
 */
public class ElasticsearchServiceTest extends AbstractTest {

    @Autowired
    private ElasticsearchService elasticsearchService;


    /**
     * do some mock before test
     */
    @Before
    public void setup() throws Exception {
        initMock();
    }

    private void initMock() throws IOException {
    }

//    @Test(expected = RdosDefineException.class)
//    public void testSearchWithJobId() {
//        try {
//            String searchWithJobId = elasticsearchService.searchWithJobId("asdf", "asdfasd");
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            Assert.assertNotNull(e);
//        }
//    }

    @Test
    public void testParseContent() {
        String sourceStr = "{\"component\":\"asd\",\"logInfo\":\"asd\",\"timestamp\":1606385340070}";
        Pair<String, String> parseContent = elasticsearchService.parseContent(sourceStr);
        Assert.assertNotNull(parseContent);
    }

    @Test
    public void testParseHostsString() {
        List<HttpHost> parseHostsString = elasticsearchService.parseHostsString("http://host_name:9092;http://host_name:9093");
        Assert.assertNotNull(parseHostsString);
    }

}
