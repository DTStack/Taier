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

package com.dtstack.engine.alert;


import com.alibaba.fastjson.annotation.JSONField;
import com.dtstack.engine.alert.enums.AlertGateCode;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Reason:
 * Date: 2017/5/15
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
public class AlterContext {

    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名： 一般用于替换
     */
    private String userName;

    /**
     * 内容
     */
    private String content;

    /**
     * 配置信息
     */
    private String alertGateJson;

    /**
     * 环境配置
     */
    private Map<String,Object> evn;

    /**
     * 类型
     */
    private AlertGateCode alertGateCode;

    /**
     * 模板
     */
    private String alertTemplate;

    /**
     * jar路径
     */
    @JSONField(serialize = false)
    private String jarPath;

    /**
     * 事件响应
     */
    @JSONField(serialize = false)
    private List<EventMonitor> eventMonitors;

    /**
     * 邮件必传
     *
     */
    private List<String> emails;

    /**
     * 只有邮件会用到，非必传
     */
    @JSONField(serialize = false)
    private List<File> attachFiles;

    /**
     * 钉钉必须
     */
    private String ding;

    /**
     * 手机号 短信和电话必传
     */
    private String phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 扩展参数: 传入事件响应,内部不会使用
     *
     */
    @JSONField(serialize = false)
    private Map<String,Object> extendedParam;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AlertGateCode getAlertGateCode() {
        return alertGateCode;
    }

    public void setAlertGateCode(AlertGateCode alertGateCode) {
        this.alertGateCode = alertGateCode;
    }

    public List<EventMonitor> getEventMonitors() {
        return eventMonitors;
    }

    public void setEventMonitors(List<EventMonitor> eventMonitors) {
        this.eventMonitors = eventMonitors;
    }

    public String getAlertGateJson() {
        return alertGateJson;
    }

    public void setAlertGateJson(String alertGateJson) {
        this.alertGateJson = alertGateJson;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<File> getAttachFiles() {
        return attachFiles;
    }

    public void setAttachFiles(List<File> attachFiles) {
        this.attachFiles = attachFiles;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getDing() {
        return ding;
    }

    public void setDing(String ding) {
        this.ding = ding;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Map<String, Object> getExtendedParam() {
        return extendedParam;
    }

    public void setExtendedParam(Map<String, Object> extendedParam) {
        this.extendedParam = extendedParam;
    }

    public String getAlertTemplate() {
        return alertTemplate;
    }

    public void setAlertTemplate(String alertTemplate) {
        this.alertTemplate = alertTemplate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, Object> getEvn() {
        return evn;
    }

    public void setEvn(Map<String, Object> evn) {
        this.evn = evn;
    }

    public String getMark(){
        String mark = "";

        if (id != null) {
            mark += id + "";
        }

        if (alertGateCode != null) {
            mark += "_type_"+alertGateCode.name();
        }

        return mark;
    }



}
