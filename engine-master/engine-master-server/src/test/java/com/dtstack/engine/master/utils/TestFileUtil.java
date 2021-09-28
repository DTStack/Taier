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

package com.dtstack.engine.master.utils;

import com.dtstack.engine.master.AbstractTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;

/**
 * @Author: newman
 * Date: 2020/12/31 4:06 下午
 * Description: 测试
 * @since 1.0.0
 */
public class TestFileUtil extends AbstractTest {

    @Test
    public void test() throws FileNotFoundException {

        String contentFromFile = FileUtil.getContentFromFile(getClass().getClassLoader().getResource("json/b.json").getFile());
        Assert.assertNotNull(contentFromFile);
    }

}
