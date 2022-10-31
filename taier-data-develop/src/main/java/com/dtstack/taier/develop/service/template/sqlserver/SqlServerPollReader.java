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

package com.dtstack.taier.develop.service.template.sqlserver;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.rdbms.RdbmsPollReader;

/**
 * sqlServer 间隔轮询 reader
 *
 * @author ：wangchuan
 * date：Created in 上午10:40 2021/7/7
 * company: www.dtstack.com
 */
public class SqlServerPollReader extends RdbmsPollReader {

    @Override
    public String pluginName() {
        return PluginName.SQLSERVER_POLL_R;
    }

    @Override
    public void checkFormat(JSONObject jsonObject) {

    }
}
