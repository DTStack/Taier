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

package com.dtstack.engine.domain;

import com.dtstack.engine.common.annotation.Unique;
import io.swagger.annotations.ApiModel;

/**
 * RDOS 插件信息
 * Date: 2018/2/6
 * Company: www.dtstack.com
 * @author xuchao
 */
@ApiModel
public class PluginInfo extends DataObject{

    @Unique
    private String pluginKey;

    private String pluginInfo;

    private int type;

    public String getPluginKey() {
        return pluginKey;
    }

    public void setPluginKey(String pluginKey) {
        this.pluginKey = pluginKey;
    }

    public String getPluginInfo() {
        return pluginInfo;
    }

    public void setPluginInfo(String pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
