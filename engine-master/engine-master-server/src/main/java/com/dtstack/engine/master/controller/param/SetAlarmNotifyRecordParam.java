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


import com.dtstack.engine.dto.SetAlarmUserDTO;

import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2019-05-20
 */
public class SetAlarmNotifyRecordParam extends NotifyRecordParam {
    private String title;
    private Long contentId;
    private Integer mailType;
    private String webhook;
    private List<Integer> senderTypes;
    private List<SetAlarmUserDTO> receivers;
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

    public Integer getMailType() {
        return mailType;
    }

    public void setMailType(Integer mailType) {
        this.mailType = mailType;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public List<Integer> getSenderTypes() {
        return senderTypes;
    }

    public void setSenderTypes(List<Integer> senderTypes) {
        this.senderTypes = senderTypes;
    }

    public List<SetAlarmUserDTO> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<SetAlarmUserDTO> receivers) {
        this.receivers = receivers;
    }

    public Map<String, Object> getEnv() {
        return env;
    }

    public void setEnv(Map<String, Object> env) {
        this.env = env;
    }
}
