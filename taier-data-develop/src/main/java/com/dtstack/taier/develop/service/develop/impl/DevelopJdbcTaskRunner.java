package com.dtstack.taier.develop.service.develop.impl;

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
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.service.develop.IJdbcService;
import com.dtstack.taier.develop.service.develop.ITaskRunner;
import com.dtstack.taier.develop.utils.develop.common.IDownload;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public abstract class DevelopJdbcTaskRunner implements ITaskRunner {

    @Autowired
    private IJdbcService jdbcService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ClusterService clusterService;

    @Override
    public abstract List<EScheduleJobType> support();

    @Override
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, Long taskId, String sql, Task task, String jobId) throws Exception {
        ExecuteResultVO<List<Object>> result = new ExecuteResultVO<>();
        result.setJobId(jobId);
        result.setContinue(false);
        EScheduleJobType taskType = EScheduleJobType.getByTaskType(task.getTaskType());
        ISourceDTO sourceDTO = getSourceDTO(tenantId, userId, taskType.getType());
        if (RegexUtils.isQuery(sql)) {
            List<List<Object>> executeResult = jdbcService.executeQuery(sourceDTO, Lists.newArrayList(sql), task.getTaskParams(), environmentContext.getSelectLimit());
            result.setResult(executeResult);
        } else {
            jdbcService.executeQueryWithoutResult(sourceDTO, sql);
        }
        result.setStatus(TaskStatus.FINISHED.getStatus());
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
        return null;
    }

    @Override
    public IDownload runLogShow(String jobId, Integer taskType, Long tenantId, Integer limitNum) {
        return null;
    }

    @Override
    public IDownload logDownLoad(Long tenantId, String jobId, Integer limitNum, String logType) {
        return null;
    }

    @Override
    public List<String> getAllSchema(Long tenantId, Integer taskType) {
        return null;
    }

    @Override
    public abstract ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType);


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
        if (componentConfig == null) {
            throw new DtCenterDefException(String.format("please config component %s", componentType.getName()));
        }
        return componentConfig.toJavaObject(JdbcInfo.class);
    }

}
