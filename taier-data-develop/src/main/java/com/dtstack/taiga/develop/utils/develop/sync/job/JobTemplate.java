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

package com.dtstack.taiga.develop.utils.develop.sync.job;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taiga.develop.common.template.Reader;
import com.dtstack.taiga.develop.common.template.Setting;
import com.dtstack.taiga.develop.common.template.Writer;
import com.google.common.collect.Lists;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/15
 */
public abstract class JobTemplate {

    public abstract Reader newReader();

    public abstract Writer newWrite();

    public abstract Setting newSetting();

    public String toJobJsonString() {

        Reader reader = newReader();
        Writer writer = newWrite();
        Setting setting = newSetting();

        JSONObject content = new JSONObject(2);
        content.put("reader", reader.toReaderJson());
        content.put("writer", writer.toWriterJson());

        JSONObject jobJson = new JSONObject(2);
        jobJson.put("content", Lists.newArrayList(content));
        jobJson.put("setting", setting.toSettingJson());

        StringBuilder job = new StringBuilder();
        job.append("{ \"job\":");
        job.append(jobJson.toJSONString());
        job.append(" }");
        return job.toString();
    }

}
