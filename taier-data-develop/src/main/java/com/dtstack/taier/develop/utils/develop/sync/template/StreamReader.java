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
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.utils.develop.sync.job.PluginName;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class StreamReader extends ExtralConfig implements Reader {

    private long sliceRecordCount;

    private List<JSONObject> column;

    @Override
    public JSONObject toReaderJson() {
        JSONObject parameter = new JSONObject(true);
        parameter.put("sliceRecordCount",sliceRecordCount);
        parameter.put("column", CollectionUtils.isNotEmpty(this.getColumn()));
        parameter.putAll(super.getExtralConfigMap());

        JSONObject reader = new JSONObject(true);
        reader.put("name", PluginName.Stream_R);
        reader.put("parameter", parameter);
        return reader;
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }

    public long getSliceRecordCount() {
        return sliceRecordCount;
    }

    public void setSliceRecordCount(long sliceRecordCount) {
        this.sliceRecordCount = sliceRecordCount;
    }

    public List<JSONObject> getColumn() {
        return column;
    }

    public void setColumn(List<JSONObject> column) {
        this.column = column;
    }
}
