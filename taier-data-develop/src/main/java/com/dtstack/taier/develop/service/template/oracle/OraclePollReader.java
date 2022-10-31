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

package com.dtstack.taier.develop.service.template.oracle;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.rdbms.RdbmsPollReader;


/**
 * Date: 2020/1/7
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class OraclePollReader extends RdbmsPollReader {

//    @Override
//    public JSONObject toReaderJson() {
//        JSONObject param = JSON.parseObject(JSON.toJSONString(this));
//        dealExtralConfig(param);
//        JSONObject res = new JSONObject();
//        res.put("name", PluginName.ORACLE_POLL_R);
//        res.put("type", DataSourceType.Oracle.getVal());
//        res.put("parameter", param);
//        return res;
//    }

    @Override
    public String pluginName() {
        return PluginName.ORACLE_POLL_R;
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject jsonObject) {
        //todo  联调的时候再准备增加检查
    }

}
