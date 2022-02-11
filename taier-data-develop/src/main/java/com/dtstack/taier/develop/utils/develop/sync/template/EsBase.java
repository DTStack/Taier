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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public abstract class EsBase extends BaseSource{

    protected String address;

    protected List column;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List getColumn() {
        return column;
    }

    public void setColumn(List column) {
        this.column = column;
    }

    public void checkArray(JSONObject parameter, String param){
        if (StringUtils.isBlank(parameter.getString(param))){
            throw new RdosDefineException(param + "不能为空");
        }

        if(!(parameter.get(param) instanceof JSONArray)){
            throw new RdosDefineException(param + "必须为数组格式");
        }

        JSONArray column = parameter.getJSONArray(param);
        if(column.isEmpty()){
            throw new RdosDefineException(param + "不能为空");
        }

        for (Object o : column) {
            if(!(o instanceof JSONObject)){
                throw new RdosDefineException(param + "必须为对象数组格式");
            }
        }
    }
}
