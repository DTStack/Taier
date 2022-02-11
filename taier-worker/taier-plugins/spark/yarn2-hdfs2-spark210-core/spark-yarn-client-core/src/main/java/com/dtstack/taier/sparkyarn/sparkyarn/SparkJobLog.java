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

package com.dtstack.taier.sparkyarn.sparkyarn;

import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 从spark上获取的任务日志
 * Date: 2017/11/24
 * Company: www.dtstack.com
 * @author xuchao
 */

public class SparkJobLog {

    private static final Logger logger = LoggerFactory.getLogger(SparkJobLog.class);

    private static final String TO_STRING_FORMAT = "{\"driverLog\": %s, \"appLog\": %s}";

    private List<SparkDetailLog> appLogList = Lists.newArrayList();

    private List<SparkDetailLog> driverLogList = Lists.newArrayList();

    public List<SparkDetailLog> getAppLogList() {
        return appLogList;
    }

    public void setAppLogList(List<SparkDetailLog> appLogList) {
        this.appLogList = appLogList;
    }

    public List<SparkDetailLog> getDriverLogList() {
        return driverLogList;
    }

    public void setDriverLogList(List<SparkDetailLog> driverLogList) {
        this.driverLogList = driverLogList;
    }

    public void addAppLog(String key, String value){
        SparkDetailLog detailLog = new SparkDetailLog(key, value);
        appLogList.add(detailLog);
    }

    public void addDriverLog(String key, String value){
        SparkDetailLog detailLog = new SparkDetailLog(key, value);
        driverLogList.add(0, detailLog);
    }

    class SparkDetailLog{

        private String id;

        private String value;

        SparkDetailLog(String id, String value){
            this.id = id;
            this.value = value;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Override
    public String toString() {

        try{
            String driverLogStr = PublicUtil.objToString(driverLogList);
            String appLogStr = PublicUtil.objToString(appLogList);
            return String.format(TO_STRING_FORMAT, driverLogStr, appLogStr);

        }catch (Exception e){
            logger.error("", e);
        }

        return "";
    }
}
