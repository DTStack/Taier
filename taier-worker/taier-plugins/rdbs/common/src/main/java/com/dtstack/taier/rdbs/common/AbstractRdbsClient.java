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

package com.dtstack.taier.rdbs.common;

import com.dtstack.taier.pluginapi.pojo.ComponentTestResult;
import com.dtstack.taier.base.resource.EngineResourceInfo;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.client.AbstractClient;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.pluginapi.pojo.JobResult;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.dtstack.taier.pluginapi.util.MathUtil;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.rdbs.common.constant.ConfigConstant;
import com.dtstack.taier.rdbs.common.executor.AbstractConnFactory;
import com.dtstack.taier.rdbs.common.executor.RdbsExeQueue;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

/**
 * 和其他类型的client不同--需要等待sql执行完成。
 * Date: 2018/2/27
 * Company: www.dtstack.com
 * @author jingzhen
 */

public abstract class AbstractRdbsClient extends AbstractClient {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRdbsClient.class);

    private RdbsExeQueue exeQueue;

    private EngineResourceInfo resourceInfo;

    protected AbstractConnFactory connFactory;

    protected String dbType = "rdbs";

    protected abstract AbstractConnFactory getConnFactory();

    @Override
    public void init(Properties prop) throws Exception {

        connFactory = getConnFactory();
        connFactory.init(prop);

        exeQueue = new RdbsExeQueue(connFactory, MathUtil.getIntegerVal(prop.get(ConfigConstant.MAX_JOB_POOL_KEY)),
                MathUtil.getIntegerVal(prop.get(ConfigConstant.MIN_JOB_POOL_KEY)));
        exeQueue.init();
        resourceInfo = new RdbsResourceInfo(exeQueue);
        LOG.warn("-------init {} plugin success-----, properties={}", dbType, prop.toString());
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        EJobType jobType = jobClient.getJobType();
        JobResult jobResult = null;
        if(EJobType.MR.equals(jobType)){
            jobResult = submitJobWithJar(jobClient);
        }else if(EJobType.SQL.equals(jobType)){
            jobResult = submitSqlJob(jobClient);
        }
        return jobResult;
    }

    private JobResult submitSqlJob(JobClient jobClient) {
        String submitId = exeQueue.submit(jobClient);
        return JobResult.createSuccessResult(jobClient.getJobId(),submitId);
    }

    private JobResult submitJobWithJar(JobClient jobClient) {
        throw new PluginDefineException(dbType + "client not support MR job");
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getEngineJobId();
        boolean cancelResult = exeQueue.cancelJob(jobId);
        if(cancelResult){
            return JobResult.createSuccessResult(jobId);
        }

        return JobResult.createErrorResult("can't not find the job");
    }

    @Override
    public TaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        String jobId = jobIdentifier.getEngineJobId();
        return exeQueue.getJobStatus(jobId);
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        throw new PluginDefineException(dbType + " client not support method 'getJobMaster'");
    }

    @Override
    public String getMessageByHttp(String path) {
        throw new PluginDefineException(dbType + "client not support method 'getMessageByHttp'");
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getEngineJobId();
        return exeQueue.getJobLog(jobId);
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        try {
            return resourceInfo.judgeSlots(jobClient);
        } catch (Exception e) {
            LOG.error("jobId:{} judgeSlots error:", jobClient.getJobId(), e);
            return JudgeResult.exception("judgeSlots error:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    @Override
    public ComponentTestResult testConnect(String pluginInfo) {
        ComponentTestResult componentTestResult = new ComponentTestResult();
        try {
            Properties properties = PublicUtil.jsonStrToObject(pluginInfo, Properties.class);
            if(null == connFactory){
                synchronized (AbstractRdbsClient.class){
                    if(null == connFactory){
                        connFactory = getConnFactory();
                    }
                }
            }
            connFactory.init(properties);
            componentTestResult.setResult(true);
        } catch (Exception e) {
            componentTestResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
            componentTestResult.setResult(false);
        }
        return componentTestResult;
    }

    @Override
    public List<List<Object>> executeQuery(String sql, String database) {
        Statement statement = null;
        ResultSet res = null;
        Connection conn = null;
        List<List<Object>> result = Lists.newArrayList();
        try {
            if (StringUtils.isBlank(sql)) {
                return null;
            }
            conn = connFactory.getConn();
            statement = conn.createStatement();
            if (StringUtils.isNotBlank(database)) {
                statement.execute("use " + database);
            }

            if (statement.execute(sql)) {
                res = statement.getResultSet();
                int columns = res.getMetaData().getColumnCount();
                List<Object> cloumnName = Lists.newArrayList();
                int timeStamp = 0;
                SimpleDateFormat dateFormat = null;

                for (int i = 1; i <= columns; ++i) {
                    String name = res.getMetaData().getColumnName(i);
                    if (name.contains(".")) {
                        name = name.split("\\.")[1];
                    }
                    cloumnName.add(name);
                }

                result.add(cloumnName);

                while (res.next()) {
                    List<Object> objects = Lists.newArrayList();

                    for (int i = 1; i <= columns; ++i) {
                        if (i == timeStamp && null != dateFormat) {
                            objects.add(dateFormat.format(res.getObject(i)));
                        } else {
                            objects.add(res.getObject(i));
                        }
                    }

                    result.add(objects);
                }
            }
        } catch (Exception e) {
            LOG.error("execue sql {} error",sql,e);
            throw new PluginDefineException(e);
        } finally {
            try {
                if (res != null) {
                    res.close();
                }

                if (statement != null) {
                    statement.close();
                }

                if (null != conn){
                    conn.close();;
                }
            } catch (Throwable var18) {
                LOG.error("", var18);
            }
        }
        return result;
    }
}
