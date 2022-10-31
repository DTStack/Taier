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

package com.dtstack.taier.develop.service.template;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.google.common.collect.Lists;

/**
 * @author sanyue
 * @date 2018/9/12
 */
public abstract class BaseJobTemplate {

    public abstract Reader newReader();

    public abstract Writer newWrite();

    public String toJobJsonString(TaskResourceParam param) {
        return refresh(param);
    }

    public String refresh(TaskResourceParam param) {
        Reader reader = newReader();
        Writer writer = newWrite();

        JSONObject content = new JSONObject();
        if (reader != null) {
            content.put("inputs", new JSONArray(Lists.newArrayList(reader.toReaderJson())));
        }
        if (writer != null) {
            content.put("outputs", new JSONArray(Lists.newArrayList(writer.toWriterJson())));
        }
        return JSONObject.toJSONString(content, SerializerFeature.MapSortField);
    }
}
