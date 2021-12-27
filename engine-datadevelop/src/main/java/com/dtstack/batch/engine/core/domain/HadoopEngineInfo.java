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
 * Hadoop引擎
 * Date: 2019/4/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public class HadoopEngineInfo extends EngineInfo {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopEngineInfo.class);

    private static final String JDBC_URL_KEY = "jdbcUrl";

    private static final String DEFAULT_FS_KEY = "fs.defaultFS";

    private String jdbcURL;

    private String defaultFS;

    public HadoopEngineInfo(){
        super(MultiEngineType.HADOOP);
    }

    @Override
    public void init(Map<String, String> conf){
        String jdbcJsonString = conf.get("metePluginInfo");
        Map<String, Object> sparkThriftConf = JSONObject.parseObject(jdbcJsonString, HashMap.class);
        String jdbcURL = MathUtil.getString(sparkThriftConf.get(JDBC_URL_KEY));
        Preconditions.checkNotNull(jdbcURL, "HADOOP 引擎类型必须配置" + JDBC_URL_KEY);
        this.jdbcURL = jdbcURL;

        String hdfsJsonString = conf.get(EComponentType.HDFS.getTypeCode() + "");
        Map<String, Object> hdfsConf = JSONObject.parseObject(hdfsJsonString, HashMap.class);
        String defaultFS = MathUtil.getString(hdfsConf.get(DEFAULT_FS_KEY));
        Preconditions.checkNotNull(jdbcURL, "HADOOP 引擎类型必须配置" + DEFAULT_FS_KEY);
        this.defaultFS = defaultFS;
    }

    public String getJdbcURL() {
        return jdbcURL;
    }

    public void setJdbcURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    public String getDefaultFS() {
        return defaultFS;
    }

    public void setDefaultFS(String defaultFS) {
        this.defaultFS = defaultFS;
    }
}
