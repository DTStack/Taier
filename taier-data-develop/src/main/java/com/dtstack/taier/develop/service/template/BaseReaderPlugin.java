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


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.common.template.Reader;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Date: 2020/1/9
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public abstract class BaseReaderPlugin implements Reader {

    //todo  这个字段考虑放在插件里好  还是放在builder里面好
    protected static final String EXTRAL_CONFIG = "extralConfig";

    protected String extralConfig;

    public String getExtralConfig() {
        return extralConfig;
    }

    public void setExtralConfig(String extralConfig) {
        this.extralConfig = extralConfig;
    }


    public static void dealExtralConfig(JSONObject inParam) {
        if (MapUtils.isEmpty(inParam) || !inParam.containsKey(EXTRAL_CONFIG)) {
            return;
        }

        String exJson = inParam.getString(EXTRAL_CONFIG);
        if (StringUtils.isEmpty(exJson)) {
            return;
        }

        JSONObject exJsonTemp = JSONObject.parseObject(exJson);
        if (MapUtils.isEmpty(exJsonTemp)) {
            return;
        }
        inParam.putAll(exJsonTemp);
    }

    @Override
    public JSONObject toReaderJson() {
        JSONObject param = JSON.parseObject(JSON.toJSONString(this));
        dealExtralConfig(param);
        param.remove(EXTRAL_CONFIG);
        JSONObject res = new JSONObject();
        res.put("name", pluginName());
        res.put("parameter", param);
        return res;
    }

    @Override
    public String toReaderJsonString(){
        return toReaderJson().toJSONString();
    }

    public abstract String pluginName();
}
