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

package com.dtstack.taier.flink.constant;

/**
 * @program: engine-all
 * @author: wuren
 * @create: 2021/02/18
 **/
// TODO 这个代码未来要抽到统一的Flink base模块中
public class ErrorMessageConstant {

    public static String WAIT_SESSION_RECOVER = "Flink session cluster is unhealthy, waiting to reboot cluster.";

    public final static String FLINK_GET_LOG_ERROR_UNDO_RESTART_EXCEPTION = "Failed to get the task log";

    public final static String FLINK_UNALE_TO_GET_CLUSTERCLIENT_STATUS_EXCEPTION = "Unable to get ClusterClient status from Application Client";

}
