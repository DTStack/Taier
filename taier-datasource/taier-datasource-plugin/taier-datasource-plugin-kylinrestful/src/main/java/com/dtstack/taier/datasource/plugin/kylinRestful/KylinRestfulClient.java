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

package com.dtstack.taier.datasource.plugin.kylinRestful;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.kylinRestful.request.RestfulClient;
import com.dtstack.taier.datasource.plugin.kylinRestful.request.RestfulClientFactory;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.JobParam;
import com.dtstack.taier.datasource.api.dto.JobResult;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.KylinRestfulSourceDTO;
import com.dtstack.taier.datasource.api.enums.JobStatus;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * kylin 客户端
 *
 * @author ：qianyi
 * date：Created in 上午10:33 2021/7/14
 * company: www.dtstack.com
 */
@Slf4j
public class KylinRestfulClient extends AbsNoSqlClient{

    private static final String KEY_JOB_STATUS = "job_status";
    private static final String KEY_JOB_ID = "uuid";
    private static final String KEY_STEPS = "steps";
    private static final String KEY_STEP_ID = "id";
    private static final String KEY_STEP_STATUS = "step_status";
    private static final String KEY_ERROR_INFO = "error_info";

    @Override
    public Boolean testCon(ISourceDTO source) {
        KylinRestfulSourceDTO kylinRestfulSourceDTO = (KylinRestfulSourceDTO) source;
        RestfulClient restfulClient = RestfulClientFactory.getRestfulClient();
        return restfulClient.auth(kylinRestfulSourceDTO);
    }


    @Override
    public List<String> getAllDatabases(ISourceDTO source, SqlQueryDTO queryDTO) {
        KylinRestfulSourceDTO kylinRestfulSourceDTO = (KylinRestfulSourceDTO) source;
        RestfulClient restfulClient = RestfulClientFactory.getRestfulClient();
        return restfulClient.getAllHiveDbList(kylinRestfulSourceDTO);
    }

    @Override
    public List<String> getTableListBySchema(ISourceDTO source, SqlQueryDTO queryDTO) {
        KylinRestfulSourceDTO kylinRestfulSourceDTO = (KylinRestfulSourceDTO) source;
        RestfulClient restfulClient = RestfulClientFactory.getRestfulClient();
        return restfulClient.getAllHiveTableListBySchema(kylinRestfulSourceDTO, queryDTO);
    }

    @Override
    public List<ColumnMetaDTO> getColumnMetaData(ISourceDTO source, SqlQueryDTO queryDTO) {
        KylinRestfulSourceDTO kylinRestfulSourceDTO = (KylinRestfulSourceDTO) source;
        RestfulClient restfulClient = RestfulClientFactory.getRestfulClient();
        return restfulClient.getHiveColumnMetaData(kylinRestfulSourceDTO, queryDTO);
    }

    private JobResult resumeJob(KylinRestfulSourceDTO source, JobParam jobParam) {
        JSONObject lastJob = getLastJob(source, jobParam);
        if (lastJob == null) {
            return createNewJobInstance(source, jobParam);
        }

        String jobId = lastJob.getString(KEY_JOB_ID);
        try {
            if (RestfulClientFactory.getRestfulClient().resumeJob(source, jobId, jobParam)) {
                return JobResult.createSuccessResult(jobId, jobId);
            }
        } catch (Exception e) {
            return JobResult.createErrorResult("resume job error : " + e.getMessage());
        }

        return null;
    }

    private JobResult createNewJobInstance(KylinRestfulSourceDTO source, JobParam jobParam) {
        checkParamOfNewJob(jobParam);
        discardErrorJob(source, jobParam);
        try {
            String jobId = RestfulClientFactory.getRestfulClient().buildCube(source, jobParam);
            return JobResult.createSuccessResult(jobId, jobId);
        } catch (Exception e) {
            return JobResult.createErrorResult("create new job error " + e.getMessage());
        }
    }

    private void checkParamOfNewJob(JobParam jobParam) {
        if (StringUtils.isBlank(jobParam.getCubeName())) {
            throw new SourceException("when create new job, cubeName param must need");
        }
        if (jobParam.getStartTime() == null || jobParam.getEndTime() == null) {
            throw new SourceException("when create new job, time param must need");
        }
    }

    private void discardErrorJob(KylinRestfulSourceDTO source, JobParam jobParam) {
        JSONObject lastJob = getLastJob(source, jobParam);
        if (lastJob == null) {
            return;
        }

        if (JobStatus.ERROR.name().equals(lastJob.getString(KEY_JOB_STATUS))) {
            String jobId = lastJob.getString(KEY_JOB_ID);
            jobParam.setJobId(jobId);
            RestfulClientFactory.getRestfulClient().discardJob(source, jobParam);
        }
    }

    private JSONObject getLastJob(KylinRestfulSourceDTO source, JobParam jobParam) {
        List<JSONObject> jobList = RestfulClientFactory.getRestfulClient().getJobList(source, jobParam, 1);
        if (CollectionUtils.isEmpty(jobList)) {
            return null;
        }
        return jobList.get(0);
    }
}
