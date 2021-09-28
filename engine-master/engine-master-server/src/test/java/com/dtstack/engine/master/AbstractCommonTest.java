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

package com.dtstack.engine.master;

import com.dtstack.engine.master.config.CacheConfig;
import com.dtstack.engine.master.config.MybatisConfig;
import com.dtstack.engine.common.env.EnvironmentContext;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * 继承此类写单元测试会调用测试库，但是使用之前不会删除测试库的数据。
 * 如果你想要编写功能测试类的代码，请先将自己想要进行功能测试的数据传入数据库，然后使用此父类。
 */
@RunWith(DtCommonSpringRunner.class)
@ContextConfiguration(classes = {EnvironmentContext.class, CacheConfig.class, MybatisConfig.class})
@SpringBootTest
public abstract class AbstractCommonTest {


}
