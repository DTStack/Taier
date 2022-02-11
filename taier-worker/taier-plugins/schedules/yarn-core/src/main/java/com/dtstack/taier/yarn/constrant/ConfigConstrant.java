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

package com.dtstack.taier.yarn.constrant;

import java.io.File;

/**
 * @program: engine-plugins
 * @author: xiuzhu
 * @create: 2021/05/18
 */
public class ConfigConstrant {

    // ------------------------------------------------------------------------
    // General Configs
    // ------------------------------------------------------------------------

    public static final String SP = File.separator;
    public static final String USER_DIR = System.getProperty("user.dir");

    public static final String FAIRSCHEDULER_TPYE = "FAIRSCHEDULER";
    public static final String CAPACITYSCHEDULER_TPYE = "CAPACITYSCHEDULER";
    public static final String FIFOSCHEDULER_TPYE = "FIFOSCHEDULER";

    public static final int HTTP_MAX_RETRY = 3;
    public static final String HTTP_AUTHENTICATION_TOKEN_KEY = "http.authentication.token";
    public static final String IS_FULL_PATH_KEY = "yarn.resourcemanager.scheduler.queue.is-full-path";

}
