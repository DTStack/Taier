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

package com.dtstack.engine.datasource.common.utils;

import java.util.regex.Pattern;

/**
 * Created by 袋鼠云-数栈产研部-应用研发中心.
 *
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a>
 * @date 2021/3/3
 * @desc 正则工具类
 */
public class RegexUtil {

    /**
     * 模型英文名称
     * 只能包含 字母|数字|下划线
     */
    public static final String MODEL_EN_NAME_REGEX = "[a-zA-Z0-9_]*$";
    /**
     * 模型名称
     * 只能包含 汉字|字母|数字|下划线
     */
    public static final String MODEL_NAME_REGEX = "^[a-zA-Z0-9_\u4e00-\u9fa5]+$";
    /**
     * 表别名
     * 只能包含 字母|数字|下划线 同时必须包含字母
     */
    public static final String TABLE_ALIAS_REGEX = "^(?=.*?[a-zA-Z])[a-zA-Z0-9_]*$";


    /**
     * 匹配函数
     *
     * @param regex 正则表达式
     * @param input 输入参数
     * @return true符合 false不符合
     */
    public static boolean match(String regex, CharSequence input) {
        return Pattern
                .compile(regex)
                .matcher(input)
                .matches();
    }
}
