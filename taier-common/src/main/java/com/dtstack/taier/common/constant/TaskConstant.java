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

package com.dtstack.taier.common.constant;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/3/19
 */
public class TaskConstant {

    /**
     * -----extraInfo中json key------
     **/
    public static final String INFO = "info";


    /**
     * jobId 占位标识符
     */
    public static final String JOB_ID = "${jobId}";
    public static final String UPLOADPATH = "${uploadPath}";
    public static final String LAUNCH = "${launch}";
    public static final String LAUNCH_CMD = "launch-cmd";
    public static final String MODEL_PARAM = "${modelParam}";
    public static final String FILE_NAME = "${file}";
    public static final String CMD = "${cmd}";
    public static final String DQ_JOB_ID = "#{jobId}";
    public static final String DQ_FLOW_JOB_ID = "#{flowJobId}";

    public static final String CMD_OPTS = "--cmd-opts";
}
