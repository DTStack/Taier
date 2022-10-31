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

package com.dtstack.taier.datasource.plugin.common.exception;

import java.util.regex.Pattern;

/**
 * 错误信息正则匹配
 *
 * @author ：wangchuan
 * date：Created in 下午1:34 2020/11/6
 * company: www.dtstack.com
 */
public interface IErrorPattern {

    /**
     * 获取连接时错误匹配
     * @param errorCode 错误代码
     * @return 对应匹配正则规则
     */
    Pattern getConnErrorPattern(Integer errorCode);
}
