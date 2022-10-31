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

package com.dtstack.taier.datasource.plugin.common.convert;

/**
 * 用于字段类型转换
 *
 * @author ：wangchuan
 * date：Created in 上午10:56 2021/10/11
 * company: www.dtstack.com
 */
@FunctionalInterface
public interface ColumnTypeConverter {

    /**
     * @param type 字段类型，如 "SHORT", "INT", "TIMESTAMP","INT8"
     * @return 转换后的字段类型 TINYINT
     */
    String convert(String type);
}
