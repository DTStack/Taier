/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.common.constant;

import com.dtstack.taier.pluginapi.constrant.ConfigConstant;

/**
 * 常用常量
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-01-25 15:06
 */
public interface CommonConstant {

    String DOT = ".";
    String SYMBOL_COLON = ":";

    String XML_SUFFIX = ".xml";
    String ZIP_SUFFIX = ".zip";
    String JSON_SUFFIX = ".json";

    String RUN_JOB_NAME = "runJob";
    String RUN_DELIMITER = "_";

    String JOB_ID = "${jobId}";

    String LOGIN = "login";

    String QUERY_JOB_LOG = "queryJobLog";

    String DOWNLOAD_LOG = ConfigConstant.REQUEST_PREFIX + "/developDownload/downloadJobLog?jobId=%s&taskType=%s&tenantId=%s";

    String TASK_NAME_PREFIX = "run_%s_task_%s";

    String DATASOURCE_PREFIX = "taier.datasource.";
    String DATASOURCE_ID = "datasourceId";
    String DATASOURCE_TYPE = "datasourceType";

}
