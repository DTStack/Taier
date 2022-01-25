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


package com.dtstack.taiga.develop.utils.develop.sync.job;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.taiga.common.exception.ErrorCode;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.common.util.Base64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jiangbo
 * @date 2019/6/10
 */
public class SyncJob {

    private static final Logger LOG = LoggerFactory.getLogger(SyncJob.class);

    public Plugin reader;
    public Plugin writer;

    public static class Plugin {
        public String name;
        public Map<String, Object> parameter;
    }

    public static SyncJob getSyncJob(String jobText) {
        if (StringUtils.isEmpty(jobText)) {
            throw new RdosDefineException("同步任务json不能为空", ErrorCode.CAN_NOT_PARSE_SYNC_TASK);
        }

        JSONObject jobJson;
        jobText = jobText.trim();
        if (jobText.startsWith("{") || jobText.startsWith("\"{")) {
            jobJson = JSONObject.parseObject(jobText);
        } else {
            jobText = Base64Util.baseDecode(jobText);
            jobJson = JSONObject.parseObject(jobText);
        }

        JSONObject contentJson = null;
        try {
            contentJson = (JSONObject) JSONPath.eval(jobJson, "$.job.content[0]");
            if (contentJson == null) {
                contentJson = (JSONObject) JSONPath.eval(jobJson.getJSONObject("job"), "$.job.content[0]");
            }
        } catch (Exception e) {
            LOG.error("", e);
            //会有多层job 嵌套
            contentJson = (JSONObject) JSONPath.eval(jobJson.getJSONObject("job"), "$.job.content[0]");
        }
        Plugin reader = contentJson.getObject("reader", Plugin.class);
        Plugin writer = contentJson.getObject("writer", Plugin.class);

        reader.parameter = reader.parameter == null ? new HashMap<>() : reader.parameter;
        writer.parameter = writer.parameter == null ? new HashMap<>() : writer.parameter;

        SyncJob syncJob = new SyncJob();
        syncJob.reader = reader;
        syncJob.writer = writer;
        return syncJob;
    }
}
