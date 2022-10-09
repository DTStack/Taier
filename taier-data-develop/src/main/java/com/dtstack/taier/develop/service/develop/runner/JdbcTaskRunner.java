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

package com.dtstack.taier.develop.service.develop.runner;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.util.RegexUtils;
import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.develop.datasource.convert.load.SourceLoaderService;
import com.dtstack.taier.develop.dto.devlop.BuildSqlVO;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.develop.IJdbcService;
import com.dtstack.taier.develop.service.develop.ITaskRunner;
import com.dtstack.taier.develop.service.develop.impl.DevelopTaskService;
import com.dtstack.taier.develop.service.schedule.JobExpandService;
import com.dtstack.taier.develop.service.schedule.JobService;
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.develop.utils.develop.common.IDownload;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.ComponentService;
import com.dtstack.taier.scheduler.service.ScheduleActionService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class JdbcTaskRunner implements ITaskRunner {

    @Autowired
    private IJdbcService jdbcService;

    @Autowired
    protected EnvironmentContext environmentContext;

    @Autowired
    protected ClusterService clusterService;

    @Autowired
    protected ComponentService componentService;

    @Autowired
    protected ScheduleActionService actionService;

    @Autowired
    protected JobService jobService;

    @Autowired
    protected JobExpandService jobExpandService;

    @Autowired
    protected DevelopTaskService developTaskService;

    @Autowired
    protected SourceLoaderService sourceLoaderService;

    @Autowired
    protected DatasourceService datasourceService;

    @Override
    public abstract List<EScheduleJobType> support();

    @Override
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, String sql, Task task, List<Map<String, Object>> taskVariableList) {
        ExecuteResultVO<List<Object>> result = new ExecuteResultVO<>();
        result.setContinue(false);
        EScheduleJobType taskType = EScheduleJobType.getByTaskType(task.getTaskType());
        ISourceDTO sourceDTO = getSourceDTO(tenantId, userId, taskType.getType(), true, task.getDatasourceId());
        if (RegexUtils.isQuery(sql)) {
            List<List<Object>> executeResult = jdbcService.executeQuery(sourceDTO, Lists.newArrayList(sql), task.getTaskParams(), environmentContext.getSelectLimit());
            result.setResult(executeResult);
        } else {
            jdbcService.executeQueryWithoutResult(sourceDTO, sql);
        }
        result.setStatus(TaskStatus.FINISHED.getStatus());
        result.setSqlText(sql);
        return result;
    }

    @Override
    public ExecuteResultVO selectData(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception {
        return null;
    }

    @Override
    public ExecuteResultVO selectStatus(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) {
        ExecuteResultVO executeResultVO = new ExecuteResultVO(selectSql.getJobId());
        executeResultVO.setStatus(getSchedulerStatus(selectSql));
        return executeResultVO;
    }

    private Integer getSchedulerStatus(DevelopSelectSql selectSql) {
        ScheduleJob scheduleJob = jobService.getScheduleJob(selectSql.getJobId());
        if (Objects.isNull(scheduleJob)) {
            return TaskStatus.NOTFOUND.getStatus();
        }
        return TaskStatus.getShowStatus(scheduleJob.getStatus());
    }


    @Override
    public ExecuteResultVO runLog(String jobId, Integer taskType, Long tenantId, Integer limitNum) {
        return null;
    }


    @Override
    public IDownload logDownLoad(Long tenantId, String jobId, Integer limitNum) {
        return null;
    }

    @Override
    public List<String> getAllSchema(Long tenantId, Integer taskType) {
        ISourceDTO sourceDTO = getSourceDTO(tenantId, null, taskType, false, null);
        return jdbcService.getAllDataBases(sourceDTO);
    }

    @Override
    public ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType, boolean useSchema, Long datasourceId) {
        return sourceLoaderService.buildSourceDTO(datasourceId);
    }

    @Override
    public BuildSqlVO buildSql(ParseResult parseResult, Long userId, Task task) {
        return null;
    }

    @Override
    public Map<String, Object> readyForSyncImmediatelyJob(Task task, Long tenantId, Boolean isRoot) {
        return null;
    }
}
