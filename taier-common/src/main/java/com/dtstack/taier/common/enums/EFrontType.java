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

package com.dtstack.taier.common.enums;

/**
 * @author yuebai
 * @date 2020-05-11
 * input: 普通文件输入框
 * radio: 单选框 无子控件渲染
 * group: 类型切换tab框
 * select: 下拉选择控件
 * checkbox: 复选框
 * xml: xml格式的配置
 * other: typeName、md5等前端不展示的控件
 * radio_linkage: 单选框 选择单选框会渲染对应values的下的子控件
 * password: 隐藏显示的输入框
 * custom_control: 自定义控件 可手动添加 可删除
 */
public enum EFrontType {
    INPUT(0, "input"),
    RADIO(1, "radio"),
    GROUP(2, "group"),
    SELECT(3, "select"),
    CHECKBOX(4, "checkbox"),
    XML(5, "xml"),
    OTHER(6, "other"),
    RADIO_LINKAGE(7, "radio_linkage"),
    PASSWORD(8, "password"),
    CUSTOM_CONTROL(9, "custom_control");

    private int code;
    private String name;

    EFrontType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
