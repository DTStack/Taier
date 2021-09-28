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

package com.dtstack.engine.common.client.config;

import com.dtstack.engine.master.impl.pojo.ClientTemplate;
import com.dtstack.engine.config.YamlConfigParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.List;

/**
 * @author yuebai
 * @date 2021-02-05
 */
public class TestConfigParse extends YamlConfigParser {

    @Test
    public void testPassword() {
        try {
            URL resource = this.getClass().getResource("/config/default-config.yaml");
            List<ClientTemplate> config = parse(new FileInputStream(new File(resource.getFile())));
            Assert.assertNotNull(config);
            Assert.assertTrue(config.stream().anyMatch(c -> c.getKey().equalsIgnoreCase("password") &&
                    c.getType().equalsIgnoreCase("password")));
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
