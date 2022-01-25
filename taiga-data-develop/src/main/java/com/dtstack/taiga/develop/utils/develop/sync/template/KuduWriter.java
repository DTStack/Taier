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
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.develop.common.template.Writer;
import com.dtstack.taiga.develop.utils.develop.sync.job.PluginName;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:28 2019-08-09
 */
public class KuduWriter extends KuduBase implements Writer {

    private Map<String,Object> hadoopConfig;

    public Map<String, Object> getHadoopConfig() {
        return hadoopConfig;
    }

    public void setHadoopConfig(Map<String, Object> hadoopConfig) {
        this.hadoopConfig = hadoopConfig;
    }

    @Override
    public JSONObject toWriterJson() {
        JSONObject parameter = new JSONObject(true);
        parameter.put("table", this.getTable());
        parameter.put("column", this.convertColumn());
        parameter.put("masterAddresses", this.getMasterAddresses());
        parameter.put("writeMode", this.getWriteMode());
        parameter.put("sourceIds",getSourceIds());
        if (MapUtils.isNotEmpty(this.getHadoopConfig())) {
            parameter.put("hadoopConfig", this.getHadoopConfig());
            parameter.put("authentication", "Kerberos");
        }
        parameter.putAll(super.getExtralConfigMap());
        JSONObject reader = new JSONObject(true);
        reader.put("name", PluginName.Kudu_W);
        reader.put("parameter", parameter);
        return reader;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {
        data = data.getJSONObject("parameter");

        if (StringUtils.isBlank(data.getString("table"))) {
            throw new RdosDefineException("table 不能为空");
        }

        if (!data.containsKey("writeMode")) {
            throw new RdosDefineException("writeMode 不能为空");
        }
    }

    /**
     * kudu 需要有name参数
     *
     * @return
     */
    private List<Map<String, Object>> convertColumn() {
        if (CollectionUtils.isNotEmpty(this.getColumn())) {
            for (Map<String, Object> map : this.getColumn()) {
                if (!map.containsKey("name") && Objects.nonNull(map.get("key"))) {
                    map.put("name", map.get("key"));
                }
            }
            return this.getColumn();
        }
        return this.getColumn();
    }
}
