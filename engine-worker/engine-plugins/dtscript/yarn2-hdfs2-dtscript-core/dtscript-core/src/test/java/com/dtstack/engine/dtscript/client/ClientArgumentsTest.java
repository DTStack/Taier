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

package com.dtstack.engine.dtscript.client;

import com.dtstack.engine.pluginapi.JobClient;
import com.dtstack.engine.pluginapi.util.RetryUtil;
import com.dtstack.engine.dtscript.DataUtil;
import com.dtstack.engine.dtscript.DtscriptUtils;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import com.google.gson.Gson;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.nio.file.FileSystem;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/27 13:01
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JobConf.class, ClientTest.class, RetryUtil.class,
        ConverterUtils.class,
        Gson.class, UserGroupInformation.class, YarnClient.class,
        FileSystem.class, DtYarnConstants.class, ApplicationConstants.class})
@PowerMockIgnore("javax.net.ssl.*")
public class ClientArgumentsTest {


    @Test
    public void testInit() throws Exception{

        JobClient jobClient = DataUtil.getJobClient();
        String[] args = DtscriptUtils.buildPythonArgs(jobClient);
        ClientArguments clientArguments = new ClientArguments(args);
    }

}
