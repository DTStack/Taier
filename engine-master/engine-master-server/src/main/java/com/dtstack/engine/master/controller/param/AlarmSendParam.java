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

package com.dtstack.engine.master.controller.param;


import com.dtstack.engine.dto.UserMessageDTO;

import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2020/12/17 10:10 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlarmSendParam extends NotifyRecordParam {

    private String title;

    private Long contentId;

    /**
     * 发送钉钉时必传
     */
    private String webhook;

    /**
     * 通道标识
     */
    private List<String> alertGateSources;

    /**
     * 用户信息
     */
    private List<UserMessageDTO> receivers;

    /**
     * 环境参数（jar告警时会传入）
     * @return
     */
    private Map<String,Object> env;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public List<String> getAlertGateSources() {
        return alertGateSources;
    }

    public void setAlertGateSources(List<String> alertGateSources) {
        this.alertGateSources = alertGateSources;
    }

    public List<UserMessageDTO> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<UserMessageDTO> receivers) {
        this.receivers = receivers;
    }

    public Map<String, Object> getEnv() {
        return env;
    }

    public void setEnv(Map<String, Object> env) {
        this.env = env;
    }
}
