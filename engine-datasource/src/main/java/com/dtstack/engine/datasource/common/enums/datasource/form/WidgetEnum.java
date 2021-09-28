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

package com.dtstack.engine.datasource.common.enums.datasource.form;

/**
 * 表单属性展现形式枚举类
 * @description:
 * @author: liuxx
 * @date: 2021/3/9
 */
public enum WidgetEnum {

    /**
     * 输入框
     */
    INPUT("Input", "输入框"),
    /**
     * 带复制按钮的文本行
     */
    INPUT_WITH_COPY("InputWithCopy", "带复制按钮的输出框"),
    /**
     * 下拉列表
     */
    SELECT("Select", "下拉列表"),
    /**
     * 多行文本输入框
     */
    TEXT_AREA( "TextArea", "多行文本输入框"),
    /**
     * 带复制按钮的多行文本输入框
     */
    TEXT_AREA_WITH_COPY("TextAreaWithCopy", "带复制按钮的多行文本输入框"),
    /**
     * 富文本输入框
     */
    RICH_TEXT( "RichText", "富文本输入框"),
    /**
     * 密码输入框
     */
    PASSWORD("Password", "密码输入框"),
    /**
     * 文件上传
     */
    UPLOAD("Upload", "文件上传"),
    /**
     * 选择框
     */
    RADIO("Radio", "选择框"),
    /**
     * 数字输入框
     */
    INTEGER("Integer", "数字输入框"),
    /**
     * 按钮开关
     */
    SWITCH("Switch", "按钮开关"),
    /**
     * Kerberos组件, 自定义
     */
    KERBEROS("Kerberos", "Kerberos组件"),
    /**
     * HbaseKerberos组件, 自定义
     */
    HBASE_KERBEROS("HbaseKerberos", "HbaseKerberos组件"),
    /**
     * FTP联动相关组件 自定义
     */
    FTP_REACT("FtpReact", "FTP联动相关组件"),
    /**
     * CarbonData联动相关组件 自定义
     */
    CARBON_REACT("CarbonReact", "CarbonData联动相关组件"),
    /**
     * redis联动相关组件 自定义
     */
    REDIS_REACT("RedisReact", "Redis联动相关组件"),
    /**
     * WebSocket特殊化组件，自定义
     */
    WEBSOCKET_SUB("WebSocketSub", "WebSocket特殊化组件"),
    ;


    WidgetEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;

    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
