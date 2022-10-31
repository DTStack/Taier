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

package com.dtstack.taier.datasource.api.constant;

/**
 * config constants
 *
 * @author ：wangchuan
 * date：Created in 10:49 2022/9/23
 * company: www.dtstack.com
 */
public final class ConfigConstants {

    // ---------------------------- base -------------------------------
    private static final String CONFIG_PREFIX = "taier.datasource.";

    // ---------------------------- plugin -------------------------------
    public static final String PLUGIN_DIR = CONFIG_PREFIX + "plugin.dir";

    // ---------------------------- retry -------------------------------
    public static final String RETRY_TIMES = CONFIG_PREFIX + "retry.times";
    public static final String RETRY_INTERVAL_TIME = CONFIG_PREFIX + "retry.interval.time";

    // ---------------------------- execute -------------------------------
    public static final String EXECUTE_TIMEOUT = CONFIG_PREFIX + "execute.timeout";
    public static final String SQL_EXECUTE_TIMEOUT = CONFIG_PREFIX + "sql.execute.timeout";
    public static final String EXECUTE_POOL_CORE_SIZE = CONFIG_PREFIX + "execute.pool.core.size";
    public static final String EXECUTE_POOL_MAX_SIZE = CONFIG_PREFIX + "execute.pool.max.size";
    public static final String EXECUTE_POOL_KEEPALIVE_TIME = CONFIG_PREFIX + "execute.keepalive.time";
    public static final String EXECUTE_POOL_QUEUE_SIZE = CONFIG_PREFIX + "execute.queue.size";
}
