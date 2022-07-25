package com.dtstack.taier.develop.service.develop.runner;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.taier.common.engine.JdbcInfo;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.util.RegexUtils;
import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.domain.DevelopTaskParamShade;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.domain.TenantComponent;
import com.dtstack.taier.develop.dto.devlop.BuildSqlVO;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.service.develop.IJdbcService;
import com.dtstack.taier.develop.service.develop.ITaskRunner;
import com.dtstack.taier.develop.service.develop.impl.DevelopTaskService;
import com.dtstack.taier.develop.service.develop.impl.DevelopTenantComponentService;
import com.dtstack.taier.develop.service.schedule.JobExpandService;
import com.dtstack.taier.develop.service.schedule.JobService;
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.develop.utils.develop.common.IDownload;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.ComponentService;
import com.dtstack.taier.scheduler.service.ScheduleActionService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
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
    private DevelopTenantComponentService developTenantComponentService;

    @Autowired
    protected ScheduleActionService actionService;

    @Autowired
    protected JobService jobService;

    @Autowired
    protected JobExpandService jobExpandService;

    @Autowired
    protected DevelopTaskService developTaskService;

    @Override
    public abstract List<EScheduleJobType> support();

    @Override
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, String sql, Task task, List<Map<String, Object>> taskVariableList) {
        ExecuteResultVO<List<Object>> result = new ExecuteResultVO<>();
        result.setContinue(false);
        EScheduleJobType taskType = EScheduleJobType.getByTaskType(task.getTaskType());
        ISourceDTO sourceDTO = getSourceDTO(tenantId, userId, taskType.getType(), true);
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
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, Long tenantId, Task task, List<DevelopTaskParamShade> taskParamsToReplace) throws Exception {

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
    public String scheduleRunLog(String jobId) {
        return null;
    }

    @Override
    public IDownload logDownLoad(Long tenantId, String jobId, Integer limitNum) {
        return null;
    }

    @Override
    public List<String> getAllSchema(Long tenantId, Integer taskType) {
        ISourceDTO sourceDTO = getSourceDTO(tenantId, null, taskType, false);
        return jdbcService.getAllDataBases(sourceDTO);
    }

    @Override
    public abstract ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType, boolean useSchema);


    /**
     * 根据db构建url
     *
     * @param jdbcUrl url
     * @param dbName  数据库
     * @return 构建后的url
     */
    protected String buildUrlWithDb(String jdbcUrl, String dbName) {
        dbName = StringUtils.isNotBlank(dbName) ? dbName.trim() : "";
        if (StringUtils.isNotBlank(jdbcUrl) && jdbcUrl.trim().contains("%s")) {
            return String.format(jdbcUrl, dbName);
        }
        return jdbcUrl;
    }


    /**
     * 获取集群组件 JDBC 信息
     *
     * @param tenantId      集群ID
     * @param componentType 组件类型
     * @return
     */
    protected JdbcInfo getJdbcInCluster(Long tenantId, EComponentType componentType, String componentVersion) {
        JSONObject componentConfig = clusterService.getConfigByKey(tenantId, componentType.getConfName(), componentVersion);
        if (Objects.isNull(componentConfig)) {
            throw new DtCenterDefException(String.format("please config component %s", componentType.getName()));
        }
        return componentConfig.toJavaObject(JdbcInfo.class);
    }

    @Override
    public String getCurrentDb(Long tenantId, Integer taskType) {
        TenantComponent tenantEngine = developTenantComponentService.getByTenantAndTaskType(tenantId, taskType);
        return tenantEngine.getComponentIdentity();
    }

    @Override
    public BuildSqlVO buildSql(ParseResult parseResult, Long tenantId, Long userId, String database, Long taskId) {
        return null;
    }

    @Override
    public Map<String, Object> readyForSyncImmediatelyJob(Task task, Long tenantId, Boolean isRoot) {
        return null;
    }
}
