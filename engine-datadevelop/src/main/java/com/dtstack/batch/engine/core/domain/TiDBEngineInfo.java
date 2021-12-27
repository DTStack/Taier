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

package com.dtstack.batch.engine.core.domain;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.service.multiengine.EngineInfo;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.common.util.MathUtil;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * tidb 引擎
 * Date: 2019/5/29
 * Company: www.dtstack.com
 * @author xuchao
 */

public class TiDBEngineInfo extends EngineInfo {

    private static final Logger LOG = LoggerFactory.getLogger(TiDBEngineInfo.class);

    //TODO
    private static final String JDBC_URL_KEY = "jdbcUrl";

    private static final String USER_NAME_KEY = "username";

    private String jdbcURL;

    private String userName;

    @Override
    public void init(Map<String, String> conf){
        String jsonString = conf.get(EComponentType.TIDB_SQL.getTypeCode() + "");
        Map<String, Object> tidbConf = JSONObject.parseObject(jsonString, HashMap.class);
        String jdbcUrlTmp = MathUtil.getString(tidbConf.get(JDBC_URL_KEY));
        Preconditions.checkNotNull(jdbcUrlTmp, String.format("Libra引擎类型必须配置: %s", JDBC_URL_KEY));
        this.jdbcURL = jdbcUrlTmp;

        String userNameTmp = MathUtil.getString(tidbConf.get(USER_NAME_KEY));
        this.userName = userNameTmp;
    }

    public TiDBEngineInfo() {
        super(MultiEngineType.TIDB);
    }

    public String getJdbcURL() {
        return jdbcURL;
    }

    public void setJdbcURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
