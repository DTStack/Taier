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

package com.dtstack.engine.alert.client.ding;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/1/18 4:52 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlterSendDingBean {

    private String ding;

    private String title;

    private String content;

    private String className;

    private String jarPath;

    private String alertGateJson;

    private List<String> atMobiles;

    private Boolean isAtAll;

    private String dingMsgType;

    private JSONObject alertGateJsonObject;

    private Map<String,Object> env;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDing() {
        return ding;
    }

    public void setDing(String ding) {
        this.ding = ding;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getAlertGateJson() {
        return alertGateJson;
    }

    public void setAlertGateJson(String alertGateJson) {
        this.alertGateJson = alertGateJson;
    }

    public List<String> getAtMobiles() {
        return atMobiles;
    }

    public void setAtMobiles(List<String> atMobiles) {
        this.atMobiles = atMobiles;
    }

    public Boolean getAtAll() {
        return isAtAll;
    }

    public void setAtAll(Boolean atAll) {
        isAtAll = atAll;
    }

    public String getDingMsgType() {
        return dingMsgType;
    }

    public void setDingMsgType(String dingMsgType) {
        this.dingMsgType = dingMsgType;
    }

    public JSONObject getAlertGateJsonObject() {
        return alertGateJsonObject;
    }

    public void setAlertGateJsonObject(JSONObject alertGateJsonObject) {
        this.alertGateJsonObject = alertGateJsonObject;
    }

    public Map<String, Object> getEnv() {
        return env;
    }

    public void setEnv(Map<String, Object> env) {
        this.env = env;
    }
}
