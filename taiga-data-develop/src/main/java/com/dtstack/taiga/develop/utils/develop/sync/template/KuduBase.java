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
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:25 2019-08-09
 */
@Data
public class KuduBase extends BaseSource {
    private String masterAddresses;

    private String otherParams;

    /**
     * 表名
     */
    protected String table;

    /**
     * 字段名
     */
    protected List<Map<String, Object>> column;

    /**
     * 写入模式
     */
    protected String writeMode;

    public void checkFormat(JSONObject data){
        data = data.getJSONObject("parameter");

        if(StringUtils.isEmpty(data.getString("hostPorts"))){
            throw new RdosDefineException("hostPorts 不能为空");
        }
    }
}
