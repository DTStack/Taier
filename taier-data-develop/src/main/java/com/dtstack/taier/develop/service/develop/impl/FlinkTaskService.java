package com.dtstack.taier.develop.service.develop.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.enums.ResourceType;
import com.dtstack.taier.common.enums.TableType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.GenerateErrorMsgUtil;
import com.dtstack.taier.common.util.JobClientUtil;
import com.dtstack.taier.dao.domain.DevelopResource;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.mapper.DevelopTaskMapper;
import com.dtstack.taier.dao.mapper.ScheduleJobMapper;
import com.dtstack.taier.dao.mapper.UserMapper;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import com.dtstack.taier.develop.enums.develop.FlinkVersion;
import com.dtstack.taier.develop.enums.develop.TaskCreateModelType;
import com.dtstack.taier.develop.flink.sql.GuideTableParamsUtil;
import com.dtstack.taier.develop.flink.sql.SqlGenerateFactory;
import com.dtstack.taier.develop.flink.sql.source.param.KafkaSourceParamEnum;
import com.dtstack.taier.develop.mapstruct.vo.TaskMapstructTransfer;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.schedule.JobService;
import com.dtstack.taier.develop.sql.formate.SqlFormatter;
import com.dtstack.taier.develop.utils.EncoderUtil;
import com.dtstack.taier.develop.utils.JsonUtils;
import com.dtstack.taier.develop.vo.develop.query.CheckResultVO;
import com.dtstack.taier.develop.vo.develop.query.TaskSearchVO;
import com.dtstack.taier.develop.vo.develop.query.TaskStatusSearchVO;
import com.dtstack.taier.develop.vo.develop.result.StartFlinkResultVO;
import com.dtstack.taier.develop.vo.develop.result.TaskListResultVO;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.pojo.CheckResult;
import com.dtstack.taier.scheduler.WorkerOperator;
import com.dtstack.taier.scheduler.impl.pojo.ParamActionExt;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.ScheduleActionService;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;


@Service
public class FlinkTaskService {
    public static Logger LOGGER = LoggerFactory.getLogger(FlinkTaskService.class);


    private static final String TIME_CHARACTERISTIC = "time.characteristic=EventTime";

    private static final String PIPELINE_TIME_CHARACTERISTIC = "pipeline.time-characteristic=EventTime";
    public static final String KEY_CHECKPOINT_INTERVAL = "flink.checkpoint.interval";
    private static final String DEFAULT_VAL_CHECKPOINT_INTERVAL = "300000";
    private static final String KEY_CHECKPOINT_STATE_BACKEND = "flink.checkpoint.stateBackend";
    private static final String KEY_OPEN_CHECKPOINT = "openCheckpoint";
    private static final String JOB_SAVEPOINT_ARGS_TEMPLATE = " -confProp %s";
    private static final String JOB_NAME_ARGS_TEMPLATE = "-jobName %s -job %s";


    @Autowired
    private DevelopTaskMapper developTaskMapper;

    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkerOperator workerOperator;

    @Autowired
    private DsInfoService dsInfoService;

    @Autowired
    private ScheduleActionService actionService;

    @Autowired
    private TaskDirtyDataManageService taskDirtyDataManageService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private DevelopFunctionService developFunctionService;

    @Autowired
    private JobService jobService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private DatasourceService dataSourceService;

    @Autowired
    private StreamTaskCheckpointService streamTaskCheckpointService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private DevelopTaskResourceService developTaskResourceService;

    @Autowired
    private StreamSqlFormatService streamSqlFormatService;

    public void convertTableStr(TaskResourceParam taskResourceParam) {
        taskResourceParam.setSourceStr(taskResourceParam.getSource() != null ? JSON.toJSONString(GuideTableParamsUtil.deal(taskResourceParam.getSource(), taskResourceParam.getComponentVersion())) : null);
        taskResourceParam.setTargetStr(taskResourceParam.getSink() != null ? JSON.toJSONString(GuideTableParamsUtil.deal(taskResourceParam.getSink(), taskResourceParam.getComponentVersion())) : null);
        taskResourceParam.setSideStr(taskResourceParam.getSide() != null ? JSON.toJSONString(GuideTableParamsUtil.deal(taskResourceParam.getSide(), taskResourceParam.getComponentVersion())) : null);
    }



    private String generateCreateFlinkSql(String sourceParams, String componentVersion, TableType type) {
        StringBuilder createSql = new StringBuilder();
        if (StringUtils.isNotBlank(sourceParams)) {
            JSONArray array = JSON.parseArray(sourceParams);
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                //从源表维表结果表的信息获取到 dataSource
                Long srcId = Long.parseLong(obj.getString("sourceId"));
                DsInfo dataSource = dsInfoService.dsInfoDetail(srcId);
                if (dataSource != null) {
                    String sql = SqlGenerateFactory.generateSql(dataSource, obj, componentVersion, type);
                    if (StringUtils.isNotBlank(sql)) {
                        createSql.append(sql);
                    }
                }
            }
        }
        return createSql.toString();
    }


    public String generateCreateFlinkSql(Task task) {
        StringBuilder sql = new StringBuilder(generateAddJarSQL(task));
        sql.append(generateCreateFlinkSql(task.getSourceStr(), task.getComponentVersion(), TableType.SOURCE));
        sql.append(generateCreateFlinkSql(task.getTargetStr(), task.getComponentVersion(), TableType.SINK));
        sql.append(generateCreateFlinkSql(task.getSideStr(), task.getComponentVersion(), TableType.SIDE));
        // 用户填写的sql
        sql.append(task.getSqlText());
        String sqlString = SqlFormatter.removeAnnotation(sql.toString());
        sqlString = SqlFormatter.sqlFormat(sqlString);
        return sqlString;
    }

    public StringBuilder generateSqlToScheduler(Task task) {
        StringBuilder sql = new StringBuilder();
        //将资源数据拼接到sql
        sql.append(generateAddJarSQL(task));
        EScheduleJobType eJobType = EScheduleJobType.getByTaskType(task.getTaskType());
        switch (Objects.requireNonNull(eJobType)) {
            case FLINK_SQL:
                if (TaskCreateModelType.GUIDE.getType().equals(task.getCreateModel())) {
                    sql.append(generateCreateFlinkSql(task.getSourceStr(), task.getComponentVersion(), TableType.SOURCE));
                    sql.append(generateCreateFlinkSql(task.getTargetStr(), task.getComponentVersion(), TableType.SINK));
                    sql.append(generateCreateFlinkSql(task.getSideStr(), task.getComponentVersion(), TableType.SIDE));
                }
            case FLINK_MR:
                sql.append(generateAddFlinkJar(task));
                break;
        }
        //用户填写的sql
        sql.append(task.getSqlText());
        return sql;
    }


    /**
     * 函数资源
     *
     * @param task
     * @return
     */
    private String generateAddJarSQL(Task task) {
        //sql 任务需要解析出关联的资源(eg:自定义function)
        Set<String> funcSet = developFunctionService.getFuncSet(task, true);
        List<String> addJarSqlList = developFunctionService.generateFuncSql(funcSet, task.getTenantId());
        StringBuilder sb = new StringBuilder();
        addJarSqlList.forEach(sql -> sb.append(sql).append(";"));
        return sb.toString();
    }


    private String generateAddFlinkJar(Task task) {
        if (task.getTaskType() == EScheduleJobType.FLINK_MR.getVal().intValue()) {
            List<DevelopResource> developResourceList = developTaskResourceService.getResources(task.getId(), ResourceType.JAR.getType());
            if (CollectionUtils.isEmpty(developResourceList)) {
                return "";
            }
            // MR任务关联主资源只能有一个
            DevelopResource developResource = developResourceList.get(0);
            if (Objects.nonNull(developResource)) {
                return streamSqlFormatService.generateAddJarSQL(developResource.getId(), task.getMainClass());
            }
        }
        return "";
    }


    /**
     * 开启任务
     *
     * @param task
     * @param externalPath
     * @return
     */
    public String startFlinkTask(Task task, String externalPath) {
        //检查是否配置flink 集群
        checkFlinkConfig(task.getTenantId());
        //检查任务状态
        checkTaskStatus(task);
        //提交任务
        return sendTaskStartTrigger(task, externalPath);
    }

    private void checkFlinkConfig(Long tenantId) {
        JSONObject configByKey = clusterService.getConfigByKey(tenantId, EComponentType.FLINK.getConfName(), null);
        AssertUtils.notNull(configByKey, "当前租户未配置flink集群信息");

    }


    private void checkTaskStatus(Task task) {
        ScheduleJob scheduleJob = getByJobId(task.getJobId());
        boolean canStart = scheduleJob == null || scheduleJob.getStatus() == null || TaskStatus.CAN_RUN_STATUS.contains(scheduleJob.getStatus());
        AssertUtils.isTrue(canStart, "任务状态不能运行");
    }


    public ScheduleJob getByJobId(String jobId) {
        List<ScheduleJob> scheduleJobs = scheduleJobMapper.getRdosJobByJobIds(Arrays.asList(jobId));
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(scheduleJobs)) {
            return scheduleJobs.get(0);
        } else {
            return null;
        }
    }

    private boolean isRestore(String job) {
        JSONObject jobJson = JSONObject.parseObject(job);
        Object isRestore = JSONPath.eval(jobJson, "$.job.setting.restore.isRestore");
        return BooleanUtils.toBoolean(String.valueOf(null == isRestore ? "true" : isRestore));
    }


    private void buildSyncTaskExecArgs(Long tenantId, String taskParams, JSONObject confProp) {
        String savepointPath = streamTaskCheckpointService.getSavepointPath(tenantId);
        Properties properties = new Properties();
        try {
            properties.load(new ByteArrayInputStream(taskParams.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new RdosDefineException(String.format("task parameter resolution exception:%s", e.getMessage()), e);
        }
        String interval = properties.getProperty(KEY_CHECKPOINT_INTERVAL, DEFAULT_VAL_CHECKPOINT_INTERVAL);
        confProp.put(KEY_CHECKPOINT_STATE_BACKEND, savepointPath);
        confProp.put(KEY_CHECKPOINT_INTERVAL, interval);
    }


    private String sendTaskStartTrigger(Task task, String externalPath) {
        //重置任务状态
        resetTaskStatus(task.getJobId());
        if (Objects.equals(task.getTaskType(), EScheduleJobType.DATA_ACQUISITION.getType())) {
            // 构造savepoint参数
            String taskParams = task.getTaskParams();
            JSONObject confProp = new JSONObject();
            String job = JSONObject.parseObject(task.getSqlText()).getString("job");
            dataSourceService.setJobDataSourceInfo(job, task.getTenantId(), task.getCreateModel());
            boolean isRestore = isRestore(job);
            if (isRestore) {
                buildSyncTaskExecArgs(task.getTenantId(), taskParams, confProp);
                taskParams += String.format(" \n %s=%s", KEY_OPEN_CHECKPOINT, Boolean.TRUE);
                task.setTaskParams(taskParams);
            }
            task.setExeArgs(String.format(JOB_NAME_ARGS_TEMPLATE, task.getName(), EncoderUtil.encoderURL(job, Charsets.UTF_8.name())));
            taskDirtyDataManageService.buildTaskDirtyDataManageArgs(task.getTaskType(), task.getId(), confProp);
            if (confProp.size() > 0) {
                String confPropArgs = String.format(JOB_SAVEPOINT_ARGS_TEMPLATE, EncoderUtil.encoderURL(confProp.toJSONString(), Charsets.UTF_8.name()));
                if (StringUtils.isNotBlank(confPropArgs)) {
                    task.setExeArgs(task.getExeArgs() == null ? confPropArgs : task.getExeArgs() + confPropArgs);
                }
            }
        }
        actionService.start(generateParamActionExt(task, externalPath));
        return "";
    }


    /**
     * 重置任务的状态为unSubmit
     *
     * @param jobId
     * @throws Exception
     */
    private void resetTaskStatus(String jobId) {
        ScheduleJob scheduleJob = getByJobId(jobId);
        //engineJob==null 说明任务为首次提交，由engine自行初始化job
        if (scheduleJob == null) {
            return;
        }
        Integer status = scheduleJob.getStatus();
        if (status != null) {
            //续跑或重跑
            if (!TaskStatus.isStopped(status)) {
                throw new RdosDefineException("(任务状态不匹配)");
            }
            boolean reset = jobService.resetTaskStatus(scheduleJob.getJobId(), status, environmentContext.getLocalAddress());
            if (!reset) {
                throw new RdosDefineException("fail to reset task status");
            }
        }
    }

    public ParamActionExt generateParamActionExt(Task task, String externalPath) {
        // 构造savepoint参数
        String taskParams = task.getTaskParams();
        //生成最终拼接的sql
        if (Objects.equals(task.getTaskType(), EScheduleJobType.FLINK_SQL.getType()) || Objects.equals(task.getTaskType(), EScheduleJobType.FLINK_MR.getType())) {
            String sql = generateSqlToScheduler(task).toString();
            task.setSqlText(sql);
        }
        return generateParamActionExt(task, externalPath, taskParams);
    }

    public String getJobName(String taskName, String jobId) {
        return taskName + "_" + jobId;
    }

    private ParamActionExt generateParamActionExt(Task task, String externalPath, String taskParams) {
        Map<String, Object> actionParam = JsonUtils.objectToMap(task);
        // 去除默认生成的字段
        actionParam.remove("mainClass");
        actionParam.remove("class");
        // 补充其他的字段
        actionParam.put("type", EScheduleType.NORMAL_SCHEDULE.getType());
        actionParam.put("tenantId", task.getTenantId());
        actionParam.put("taskParams", formatTaskParams(taskParams, task.getSourceStr(), task.getComponentVersion(), task.getTaskType()));
        actionParam.put("name", getJobName(task.getName(), task.getJobId()));
        Integer deployMode = EDeployMode.PERJOB.getType();
        if (clusterService.hasStandalone(task.getTenantId(), EComponentType.FLINK.getTypeCode())) {
            deployMode = EDeployMode.STANDALONE.getType();
        }
        actionParam.put("deployMode", deployMode);
        actionParam.put("queueName", task.getQueueName());

        if (!Strings.isNullOrEmpty(externalPath)) {
            actionParam.put("externalPath", externalPath);
        }
        return JsonUtils.objectToObject(actionParam, ParamActionExt.class);
    }

    private String formatTaskParams(String taskParams, String sourceParam, String componentVersion, Integer taskType) {
        List<String> params = new ArrayList<>();
        String[] tempParams = taskParams.split("\r|\n");
        for (String param : tempParams) {
            if (StringUtils.isNotEmpty(param.trim()) && !param.trim().startsWith("#") && param.contains("=")) {
                int special = param.indexOf("=");
                params.add(String.format("%s=%s", param.substring(0, special).trim(), param.substring(special + 1).trim()));
            }
        }
        if (StringUtils.isNotEmpty(sourceParam) && Objects.equals(taskType, EScheduleJobType.FLINK_SQL.getType())) {
            //为时间特征为eventTime的的任务添加参数
            JSONArray array = JSON.parseArray(sourceParam);
            boolean timeCharacteristic = true;
            for (int i = 0; i < array.size(); i++) {
                JSONObject sourceJson = array.getJSONObject(i);
                String timeColumnFront = sourceJson.getString(KafkaSourceParamEnum.TIME_COLUMN.getFront());
                if (StringUtils.isNotBlank(timeColumnFront)) {
                    // flink1.12 默认时间语义是事件时间，之前是机器时间
                    String timeCharKey = FlinkVersion.FLINK_112.getType().equals(componentVersion) ?
                            PIPELINE_TIME_CHARACTERISTIC : TIME_CHARACTERISTIC;
                    if (!params.contains(timeCharKey)) {
                        if (timeCharacteristic) {
                            params.add(timeCharKey);
                            timeCharacteristic = false;
                        }
                    }
                }
            }
        }

        return StringUtils.join(params, "\n");
    }


    public List<JSONObject> dealWithSourceName(String jsonParams) {
        List<JSONObject> list = Lists.newArrayList();
        if (StringUtils.isNotBlank(jsonParams)) {
            JSONArray array = JSON.parseArray(jsonParams);
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                //设置名称
                setSourceName(obj);
                list.add(obj);
            }
        }
        return list;
    }

    private void setSourceName(JSONObject param) {
        if (param.containsKey("sourceId")) {
            Long srcId = Long.parseLong(param.getString("sourceId"));
            DsInfo dsInfo = dsInfoService.dsInfoDetail(srcId);
            if (dsInfo != null) {
                param.put("sourceName", dsInfo.getDataName());
            }
        }
    }


    /**
     * 运行FlinkSql 任务
     *
     * @return
     */
    public StartFlinkResultVO startFlinkTask(Long taskId, String externalPath) {
        StartFlinkResultVO startFlinkResultVO = new StartFlinkResultVO();
        Task task = developTaskMapper.selectById(taskId);
        try {
            startFlinkTask(task, externalPath);
            startFlinkResultVO.setMsg(String.format("任务提交成功,名称为: %s", task.getName()));
            startFlinkResultVO.setJobId(task.getJobId());
            startFlinkResultVO.setStatus(TaskStatus.SUBMITTING.getStatus());
        } catch (Exception e) {
            LOGGER.warn("startFlinkSQL-->", e);
            startFlinkResultVO.setMsg(e.getMessage());
            startFlinkResultVO.setStatus(TaskStatus.SUBMITFAILD.getStatus());
            scheduleJobService.jobFail(task.getJobId(), TaskStatus.SUBMITFAILD.getStatus(), GenerateErrorMsgUtil.generateErrorMsg(e.getMessage()));
        }
        return startFlinkResultVO;
    }


    /**
     * 停止任务
     *
     * @param taskId
     * @return
     */
    public Boolean stopStreamTask(Long taskId, Integer isForce) {
        Task task = developTaskMapper.selectById(taskId);
        return actionService.stop(Collections.singletonList(task.getJobId()), isForce);
    }

    /**
     * 返回所有时区
     *
     * @return
     */
    public List<String> getAllTimeZone() {
        return Lists.newArrayList(TimeZone.getAvailableIDs());
    }


    public CheckResultVO grammarCheck(TaskResourceParam taskResourceParam) {
        convertTableStr(taskResourceParam);
        TaskVO taskVO = TaskMapstructTransfer.INSTANCE.TaskResourceParamToTaskVO(taskResourceParam);
        CheckResult data = grammarCheck(taskVO);
        if (data == null) {
            throw new DtCenterDefException("engine service exception");
        }
        CheckResultVO checkResultVO = new CheckResultVO();
        if (data.isResult()) {
            checkResultVO.setCode(1);
            checkResultVO.setData("语法检查正常");
        } else {
            checkResultVO.setCode(999);
            checkResultVO.setErrorMsg(data.getErrorMsg());
        }
        return checkResultVO;
    }

    public CheckResult grammarCheck(Task task) {
        return grammarCheck(generateParamActionExt(task, null));
    }


    public CheckResult grammarCheck(ParamActionExt paramActionExt) {
        CheckResult checkResult = null;
        try {
            JobClient jobClient = JobClientUtil.conversionJobClient(paramActionExt);
            checkResult = workerOperator.grammarCheck(jobClient);
        } catch (Exception e) {
            checkResult = CheckResult.exception(ExceptionUtil.getErrorMessage(e));
        }
        return checkResult;
    }


    public PageResult<List<TaskListResultVO>> getTaskList(TaskSearchVO taskSearchVO) {
        List<Integer> type = CollectionUtils.isEmpty(taskSearchVO.getType()) ? EScheduleJobType.STREAM_JOB_TYPES : taskSearchVO.getType().stream().filter(EScheduleJobType.STREAM_JOB_TYPES::contains).collect(Collectors.toList());
        Page<Task> resultPage = new Page<>(taskSearchVO.getCurrentPage(), taskSearchVO.getPageSize());
        Page<Task> result = developTaskMapper.selectPage(resultPage, Wrappers.lambdaQuery(Task.class).in(Task::getTaskType, type).eq(Task::getTenantId, taskSearchVO.getTenantId()).eq(Task::getIsDeleted, Deleted.NORMAL.getStatus()).like(Task::getName, taskSearchVO.getTaskName()));
        List<Task> taskList = result.getRecords();
        if (CollectionUtils.isEmpty(taskList)) {
            return PageResult.EMPTY_PAGE_RESULT;
        }
        List<String> jobIds = taskList.stream().map(Task::getJobId).collect(Collectors.toList());
        List<ScheduleJob> scheduleJobs = scheduleJobMapper.getRdosJobByJobIds(jobIds);
        Map<String, ScheduleJob> engineJobMap = new HashMap<>();
        for (ScheduleJob scheduleJob : scheduleJobs) {
            engineJobMap.put(scheduleJob.getJobId(), scheduleJob);
        }

        List<TaskListResultVO> streamTaskVOS = new ArrayList<>(taskList.size());
        for (Task task : taskList) {
            try {
                TaskListResultVO vo = TaskMapstructTransfer.INSTANCE.taskVToTaskListResult(task);
                Integer status = null;
                ScheduleJob scheduleJob = engineJobMap.get(vo.getJobId());
                if (scheduleJob != null) {
                    status = scheduleJob.getStatus();
                    if (TaskStatus.RUNNING.getStatus().equals(status)) {
                        vo.setExecStartTime(scheduleJob.getExecStartTime());
                    }
                }
                //任务状态为null 设置为未提交状态
                vo.setStatus(TaskStatus.getShowStatus(status != null ? status : TaskStatus.UNSUBMIT.getStatus()));

                if (CollectionUtils.isEmpty(taskSearchVO.getStatusList()) || taskSearchVO.getStatusList().contains(vo.getStatus())) {
                    vo.setSubmitModified(task.getGmtModified());
                    vo.setCreateUserName(userMapper.selectById(task.getCreateUserId()).getUserName());
                    vo.setModifyUserName(userMapper.selectById(task.getModifyUserId()).getUserName());
                    streamTaskVOS.add(vo);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return new PageResult<>((int) result.getCurrent(), (int) result.getSize(), result.getTotal(), streamTaskVOS);
    }

    public Map<String, Integer> getStatusCountByCondition(TaskStatusSearchVO taskSearchDTO) {

        Map<String, Integer> statusCount = Maps.newHashMap();
        statusCount.put("ALL", 0);
        statusCount.put(TaskStatus.FAILED.name(), 0);
        statusCount.put(TaskStatus.RUNNING.name(), 0);
        statusCount.put(TaskStatus.CANCELED.name(), 0);
        statusCount.put("UNRUNNING", 0);
        List<Integer> type = CollectionUtils.isEmpty(taskSearchDTO.getType()) ? EScheduleJobType.STREAM_JOB_TYPES : taskSearchDTO.getType().stream().filter(EScheduleJobType.STREAM_JOB_TYPES::contains).collect(Collectors.toList());
        List<Task> taskList = developTaskMapper.selectList(Wrappers.lambdaQuery(Task.class).in(Task::getTaskType, type).eq(Task::getTenantId, taskSearchDTO.getTenantId()).eq(Task::getIsDeleted, Deleted.NORMAL.getStatus()).like(Task::getName, taskSearchDTO.getTaskName()));
        List<String> jobIds = taskList.stream().map(Task::getJobId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(jobIds)) {
            return statusCount;
        }
        List<ScheduleJob> scheduleJobs = scheduleJobMapper.getRdosJobByJobIds(jobIds);

        Map<String, Integer> jobIdStatusMap = new HashMap<>();
        for (ScheduleJob scheduleJob : scheduleJobs) {
            jobIdStatusMap.put(scheduleJob.getJobId(), scheduleJob.getStatus());
        }

        int totalCount = 0;
        for (String jobId : jobIds) {
            Integer status = jobIdStatusMap.getOrDefault(jobId, TaskStatus.UNSUBMIT.getStatus());
            if (CollectionUtils.isEmpty(taskSearchDTO.getStatusList()) || taskSearchDTO.getStatusList().contains(status)) {
                if (TaskStatus.RUNNING_STATUS.contains(status)
                        || TaskStatus.FAILED_STATUS.contains(status)
                        || TaskStatus.STOP_STATUS.contains(status)) {
                    String key = TaskStatus.getCode(status);
                    int count = statusCount.getOrDefault(key, 0);
                    statusCount.put(key, count + 1);
                } else {
                    String key = "UNRUNNING";
                    int count = statusCount.getOrDefault(key, 0);
                    statusCount.put(key, count + 1);
                }
                totalCount++;
            }
        }
        statusCount.put("ALL", totalCount);
        return statusCount;
    }


    /**
     * 格式化sql
     */
    public String sqlFormat(String sql) {
        if (!Strings.isNullOrEmpty(sql)) {
            boolean isJSON;
            JSONObject jsonObject = null;
            try {
                jsonObject = (JSONObject) JSONObject.parse(sql);
                isJSON = true;
            } catch (Exception e) {
                isJSON = false;
            }
            try {
                if (isJSON) {
                    sql = JSON.toJSONString(jsonObject, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                            SerializerFeature.WriteDateUseDateFormat);
                    return sql;

                } else {
                    return SqlFormatter.format(sql);
                }

            } catch (Exception e) {
                LOGGER.error("failure to format sql, e:{}", e.getMessage(), e);
            }
        }

        return sql;
    }


    public Task getTaskSqlText(Long taskId) {
        Task task = developTaskMapper.selectById(taskId);
        if (EScheduleJobType.FLINK_SQL.getVal().equals(task.getTaskType()) && TaskCreateModelType.GUIDE.getType().equals(task.getCreateModel())) {

            String source = generateCreateFlinkSql(task.getSourceStr(), task.getComponentVersion(), TableType.SOURCE);
            source = sqlFormat(source);
            task.setSourceStr(source);

            String sink = generateCreateFlinkSql(task.getTargetStr(), task.getComponentVersion(), TableType.SINK);
            sink = sqlFormat(sink);
            task.setTargetStr(sink);

            String side = generateCreateFlinkSql(task.getSideStr(), task.getComponentVersion(), TableType.SIDE);
            side = sqlFormat(side);
            task.setSideStr(side);
        }

        return task;
    }
}
