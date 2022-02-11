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

package com.dtstack.taier.develop.utils.develop.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.utils.develop.sync.job.PluginName;

public class StreamWriter extends ExtralConfig implements Writer {

    @Override
    public JSONObject toWriterJson() {
        JSONObject writer = new JSONObject();
        writer.put("name", PluginName.Stream_W);
        JSONObject parameter = new JSONObject(true);
        parameter.putAll(super.getExtralConfigMap());

        writer.put("parameter", parameter);
        return writer;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }
}
