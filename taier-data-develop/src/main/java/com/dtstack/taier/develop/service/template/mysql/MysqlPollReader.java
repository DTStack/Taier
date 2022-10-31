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

package com.dtstack.taier.develop.service.template.mysql;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.dto.devlop.ConnectionDTO;
import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.rdbms.RdbmsPollReader;
import org.apache.commons.collections.CollectionUtils;

/**
 * Date: 2020/2/19
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class MysqlPollReader extends RdbmsPollReader {

    @Override
    public String pluginName() {
        boolean isMultiTable = (CollectionUtils.isNotEmpty(getConnection()) && getConnection().size() > 1);

        if (isMultiTable) {
            return PluginName.MySQLD_R;
        } else {
            if(getConnection().size() == 1){
                ConnectionDTO connectionDTO = getConnection().get(0);
                if (connectionDTO.getTable().size()>1) {
                    return PluginName.MySQLD_R;
                }
            }
            return PluginName.MySQL_R;
        }
    }

    @Override
    public void checkFormat(JSONObject jsonObject) {

    }
}
