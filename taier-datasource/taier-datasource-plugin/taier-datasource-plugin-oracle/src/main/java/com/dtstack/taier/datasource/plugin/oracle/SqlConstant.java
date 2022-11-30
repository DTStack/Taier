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

package com.dtstack.taier.datasource.plugin.oracle;

/**
 * sql 常量
 *
 * @author ：wangchuan
 * date：Created in 下午7:47 2022/3/3
 * company: www.dtstack.com
 */
public interface SqlConstant {

    /**
     * 获取 oracle PDB 列表前 设置 session
     */
    String ALTER_PDB_SESSION = "ALTER SESSION SET CONTAINER=%s";

    /**
     * alter session
     */
    String ALTER_SESSION = "ALTER SESSION";

}
