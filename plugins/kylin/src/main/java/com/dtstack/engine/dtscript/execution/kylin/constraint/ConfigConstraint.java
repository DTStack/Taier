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


package com.dtstack.engine.dtscript.execution.kylin.constraint;

/**
 * @author jiangbo
 * @date 2019/7/2
 */
public class ConfigConstraint {
    public static final String KEY_HOST_PORT = "hostPort";
    public static final String KEY_CONNECT_PARAMS = "connectParams";
    public static final String KEY_SOCKET_TIMEOUT = "socketTimeout";
    public static final String KEY_CONNECT_TIMEOUT = "connectTimeout";
    public static final String KEY_CONNECTION_REQUEST_TIMEOUT = "connectionRequestTimeout";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_BUILD_TYPE = "buildType";
    public static final String KEY_CUBE_NAME = "cubeName";
    public static final String KEY_START_TIME = "startTime";
    public static final String KEY_END_TIME = "endTime";

    public static final int DEFAULT_SOCKET_TIMEOUT = 10000;
    public static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 10000;
}
