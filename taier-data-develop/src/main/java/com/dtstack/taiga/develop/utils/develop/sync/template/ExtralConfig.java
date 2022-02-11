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
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 1.数据同步高级配置,自定义参数
 * 2.json格式
 * 3.各类输入输出插件都需要
 *
 * @author sanyue
 * @date 2019/3/13
 */
public abstract class ExtralConfig {
    protected String extralConfig;

    public String getExtralConfig() {
        return extralConfig;
    }

    public void setExtralConfig(String extralConfig) {
        this.extralConfig = extralConfig;
    }

    protected Map<String, Object> getExtralConfigMap() {
        try {
            Map<String, Object> map = new HashMap<>();
            if (StringUtils.isNotBlank(getExtralConfig())) {
                JSONObject config = JSONObject.parseObject(getExtralConfig());
                if (config != null) {
                    for (String key : config.keySet()) {
                        map.put(key, config.get(key));
                    }
                }
            }
            return map;
        } catch (Exception e) {
            throw new RdosDefineException("数据同步高级配置JSON格式错误");
        }
    }

    protected void checkExtralConfigIsJSON() {
        try {
            JSONObject.parseObject(getExtralConfig());
        } catch (Exception e) {
            throw new RdosDefineException("数据同步高级配置JSON格式错误");
        }
    }
}
