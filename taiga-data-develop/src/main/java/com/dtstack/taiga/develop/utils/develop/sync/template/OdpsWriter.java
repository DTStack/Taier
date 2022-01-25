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

package com.dtstack.taiga.develop.utils.develop.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taiga.develop.common.template.Writer;
import com.dtstack.taiga.develop.utils.develop.sync.job.PluginName;
import com.dtstack.taiga.develop.utils.develop.sync.util.ColumnUtil;

public class OdpsWriter extends OdpsBase implements Writer {

    private String writeMode;

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    @Override
    public JSONObject toWriterJson() {
        JSONObject odpsConfig = new JSONObject(true);
        odpsConfig.put("accessId", accessId);
        odpsConfig.put("accessKey", accessKey);
        odpsConfig.put("project", project);
        odpsConfig.put("odpsServer", endPoint);

        JSONObject parameter = new JSONObject(true);
        parameter.put("odpsConfig", odpsConfig);
        parameter.put("table", table);
        parameter.put("partition", partition);
        parameter.put("writeMode", writeMode);
        parameter.put("column", ColumnUtil.getColumns(this.getColumn(), PluginName.ODPS_W));
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());


        JSONObject writer = new JSONObject(true);
        writer.put("name", PluginName.ODPS_W);
        parameter.put("sourceId",getSourceId());
        writer.put("parameter", parameter);

        return writer;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {
        super.checkFormat(data);
    }
}
