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

package com.dtstack.engine.dtscript.am;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import com.dtstack.engine.dtscript.common.AppEnvConstant;
import com.dtstack.engine.dtscript.common.SecurityUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Build container env
 *
 * @author huyifan.zju@163.com
 */
public class ContainerEnvBuilder {

    private static final Log LOG = LogFactory.getLog(ContainerEnvBuilder.class);

    private String role;

    private ApplicationMaster applicationMaster;

    /**
     * env parameters of notebook job
     */
    private static final String MODEL_ENV = "modelEnv";
    /**
     * reg pattern of key-value parse
     */
    private static final Pattern p = Pattern.compile("([\\w.]+)=\"*((?<=\")[^\"]+(?=\")|([^\\s]+))\"*");

    public ContainerEnvBuilder(String role, ApplicationMaster applicationMaster) {
        this.role = role;
        this.applicationMaster = applicationMaster;
    }

    public Map<String,String> build() {
        final String learningAppType = applicationMaster.appArguments.learningAppType;
        final Configuration conf = applicationMaster.conf;
        final String cmd = applicationMaster.appArguments.cmd;
        final String appEnv = applicationMaster.appArguments.appEnv;
        final int workerNum = applicationMaster.appArguments.workerNum;
        final ApplicationAttemptId applicationAttemptId = applicationMaster.appArguments.applicationAttemptID;
        final ApplicationContainerListener containerListener = applicationMaster.containerListener;


        LOG.info("Setting environments for the Container");
        Map<String, String> containerEnv = new HashMap<>();

        containerEnv.put(DtYarnConstants.Environment.XLEARNING_CONTAIENR_GPU_NUM.toString(), String.valueOf(applicationMaster.appArguments.workerGCores));

        containerEnv.put(DtYarnConstants.Environment.HADOOP_USER_NAME.toString(), conf.get("hadoop.job.ugi").split(",")[0]);
        containerEnv.put("CLASSPATH", System.getenv("CLASSPATH"));
        containerEnv.put(DtYarnConstants.Environment.APP_ATTEMPTID.toString(), applicationAttemptId.toString());
        containerEnv.put(DtYarnConstants.Environment.APP_ID.toString(), applicationAttemptId.getApplicationId().toString());
        containerEnv.put(DtYarnConstants.Environment.APPMASTER_HOST.toString(),
                System.getenv(ApplicationConstants.Environment.NM_HOST.toString()));
        containerEnv.put(DtYarnConstants.Environment.APPMASTER_PORT.toString(),
                String.valueOf(containerListener.getServerPort()));

        StringBuilder pathStr = new StringBuilder();
        pathStr.append(System.getenv("PATH")).append(":");
        if (StringUtils.isNotBlank(System.getenv(DtYarnConstants.Environment.USER_PATH.toString()))) {
            pathStr.append(System.getenv(DtYarnConstants.Environment.USER_PATH.toString())).append(":");
        }
        if (StringUtils.isNotBlank(conf.get(DtYarnConstants.Environment.USER_PATH.toString()))) {
            pathStr.append(conf.get(DtYarnConstants.Environment.USER_PATH.toString()));
        }
        containerEnv.put("PATH", pathStr.toString());

        containerEnv.put(DtYarnConstants.Environment.APP_TYPE.toString(),learningAppType);
        SecurityUtil.setupUserEnv(containerEnv);

        // set cmd into container envs
        containerEnv.put(DtYarnConstants.Environment.DT_EXEC_CMD.toString(), cmd);
        parseAppEnv(appEnv, containerEnv);

        LOG.info("container env:" + containerEnv.toString());
        Set<String> envStr = containerEnv.keySet();
        for (String anEnvStr : envStr) {
            LOG.debug("env:" + anEnvStr);
        }

        return containerEnv;

    }


    /**
     * 1.parse cmd.
     * 2.add key-value into container envs.
     * @param appEnv
     */
    private void parseAppEnv(String appEnv, Map<String,String> containerEnv) {
        if (StringUtils.isBlank(appEnv)) {
            return;
        }

        try {
            // envJson is encode, we need decode it.
            appEnv = URLDecoder.decode(appEnv, "UTF-8");
            LOG.info("cmdStr decoded is : " + appEnv);

            Map<String,Object> envMap = JSON.parseObject(appEnv.trim());
            Iterator entries = envMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                String key = (String) entry.getKey();
                String value;
                if (AppEnvConstant.MODEL_PARAM.equals(key)) {
                    value = URLEncoder.encode((String) entry.getValue(), "UTF-8");
                } else {
                    value = (String) entry.getValue();
                }
                //add prefix for app env, make it easier to recognize.
                containerEnv.put(AppEnvConstant.SUB_PROCESS_ENV.concat(key) , value);
            }
        } catch (Exception e) {
            String message = String.format("Could't parse {%s} to json format. Reason : {%s}", appEnv , e.getMessage());
            LOG.error(message);
            throw new RuntimeException(message, e);
        }
    }
}
