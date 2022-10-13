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

package com.dtstack.taier.pluginapi.logstore;


import com.dtstack.taier.pluginapi.logstore.mysql.MysqlLogStore;

import java.util.Map;

/**
 * Created by sishu.yss on 2018/4/17.
 */
public class LogStoreFactory {

    private static AbstractLogStore logStore;

    public static AbstractLogStore getLogStore() {
        return getLogStore(null);
    }

    public static synchronized AbstractLogStore getLogStore(Map<String, String> dbConfig) {
        if (logStore == null) {
            if (dbConfig == null) {
                return null;
            }
            logStore = MysqlLogStore.getInstance(dbConfig);
        }
        return logStore;
    }

}
