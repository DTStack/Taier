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

package com.dtstack.taier.rdbs.hive;

import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.pojo.ClusterResource;
import com.dtstack.taier.pluginapi.pojo.ParamAction;
import com.dtstack.taier.pluginapi.util.MD5Util;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.rdbs.common.AbstractRdbsClient;
import com.dtstack.taier.rdbs.common.executor.AbstractConnFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/** @author dtstack tiezhu 2021/4/20 星期二 */
public class HiveClient extends AbstractRdbsClient {
    public HiveClient() {
        this.dbType = "hive";
    }

    private static final Logger LOG = LoggerFactory.getLogger(HiveClient.class);

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new HiveConnFactory();
    }
}
