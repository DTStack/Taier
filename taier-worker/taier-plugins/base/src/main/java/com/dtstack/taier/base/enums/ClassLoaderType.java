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

package com.dtstack.taier.base.enums;

import com.dtstack.taier.pluginapi.enums.EJobType;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/17
 */
public enum ClassLoaderType {
    //
    NONE,
    //
    CHILD_FIRST,
    //
    PARENT_FIRST,
    //
    CHILD_FIRST_CACHE,
    //
    PARENT_FIRST_CACHE;

    public static ClassLoaderType getClassLoaderType(EJobType jobType) {
        if (EJobType.SYNC == jobType || EJobType.SQL == jobType) {
            return PARENT_FIRST_CACHE;
        } else {
            return PARENT_FIRST;
        }
    }

    public static final String CLASSLOADER_DTSTACK_CACHE = "classloader.dtstack-cache";
    public static final String CLASSLOADER_DTSTACK_CACHE_TRUE = "true";
    public static final String CLASSLOADER_DTSTACK_CACHE_FALSE = "false";
}
