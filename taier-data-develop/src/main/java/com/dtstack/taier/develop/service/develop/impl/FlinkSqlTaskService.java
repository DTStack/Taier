package com.dtstack.taier.develop.service.develop.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.dtcenter.loader.utils.AssertUtils;
import com.dtstack.taier.common.enums.ComputeType;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.enums.MultiEngineType;
import com.dtstack.taier.common.enums.TableType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.common.util.JobClientUtil;
import com.dtstack.taier.common.util.TaskParamsUtils;
import com.dtstack.taier.dao.domain.BatchFunction;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.dao.domain.ScheduleEngineJobCache;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.domain.Tenant;
import com.dtstack.taier.dao.mapper.DevelopTaskMapper;
import com.dtstack.taier.dao.mapper.ScheduleJobMapper;
import com.dtstack.taier.dao.mapper.UserMapper;
import com.dtstack.taier.dao.pager.PageQuery;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import com.dtstack.taier.develop.enums.develop.FlinkUDFType;
import com.dtstack.taier.develop.enums.develop.FlinkVersion;
import com.dtstack.taier.develop.enums.develop.TaskCreateModelType;
import com.dtstack.taier.develop.flink.sql.SqlGenerateFactory;
import com.dtstack.taier.develop.flink.sql.source.param.KafkaSourceParamEnum;
import com.dtstack.taier.develop.mapstruct.vo.TaskMapstructTransfer;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.schedule.JobService;
import com.dtstack.taier.develop.sql.formate.SqlFormatter;
import com.dtstack.taier.develop.utils.JsonUtils;
import com.dtstack.taier.develop.utils.TaskStatusCheckUtil;
import com.dtstack.taier.develop.utils.develop.service.impl.Engine2DTOService;
import com.dtstack.taier.develop.vo.develop.query.CheckResultVO;
import com.dtstack.taier.develop.vo.develop.query.TaskSearchVO;
import com.dtstack.taier.develop.vo.develop.query.TaskStatusSearchVO;
import com.dtstack.taier.develop.vo.develop.result.StartFlinkSqlResultVO;
import com.dtstack.taier.develop.vo.develop.result.TaskListResultVO;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.pojo.CheckResult;
import com.dtstack.taier.pluginapi.pojo.ParamAction;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.scheduler.WorkerOperator;
import com.dtstack.taier.scheduler.impl.pojo.ParamActionExt;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.EngineJobCacheService;
import com.dtstack.taier.scheduler.service.ScheduleActionService;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;


@Service
public class FlinkSqlTaskService {
    public static Logger LOGGER = LoggerFactory.getLogger(FlinkSqlTaskService.class);


    private static final String TIME_CHARACTERISTIC = "time.characteristic=EventTime";

    private static final String PIPELINE_TIME_CHARACTERISTIC = "pipeline.time-characteristic=EventTime";

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
    private ClusterService clusterService;

    @Autowired
    private BatchFunctionService batchFunctionService;

    @Autowired
    private JobService jobService;

    @Autowired
    private EnvironmentContext environmentContext;


    /**
     * 将前端接收的结果表维表转化
     */
    public void convertTableStr(TaskResourceParam taskResourceParam, TaskVO taskVO) {
        taskVO.setSourceStr(taskResourceParam.getSource() != null ? JSON.toJSONString(deal(taskResourceParam.getSource(), taskResourceParam.getComponentVersion())) : null);
        taskVO.setTargetStr(taskResourceParam.getSink() != null ? JSON.toJSONString(deal(taskResourceParam.getSink(), taskResourceParam.getComponentVersion())) : null);
        taskVO.setSideStr(taskResourceParam.getSide() != null ? JSON.toJSONString(deal(taskResourceParam.getSide(), taskResourceParam.getComponentVersion())) : null);
    }

    private List<JSONObject> deal(List<JSONObject> source, String componentVersion) {
        for (JSONObject obj : source) {
            //只有mysql oracle类型采用键值模式填写
            Integer type = obj.getInteger("type");
            if (DataSourceType.MySQL.getVal().equals(type)) {
                convertField(obj);
            } else {
                String columnsText = obj.getString("columnsText");
                obj.put("columns", parseColumnsFromText(columnsText, type, componentVersion));
            }
        }
        return source;
    }

    private void convertField(JSONObject sourceMeta) {
        if (!sourceMeta.containsKey("columns")) {
            return;
        }
        JSONArray columns = sourceMeta.getJSONArray("columns");
        if (Objects.isNull(columns) || columns.size() <= 0) {
            return;
        }
        JSONArray formatColumns = new JSONArray();
        for (int i = 0; i < columns.size(); i++) {
            JSONObject columnJson = columns.getJSONObject(i);
            String type = columnJson.getString("type");
            columnJson.put("type", StringUtils.replace(type, " ", ""));
            formatColumns.add(columnJson);
        }
        sourceMeta.put("columns", formatColumns);
    }

    private List<JSONObject> parseColumnsFromText(String columnsText, Integer dataSourceType, String componentVersion) {
        List<JSONObject> list = Lists.newArrayList();
        List<String> check = Lists.newArrayList();
        if (StringUtils.isBlank(columnsText)) {
            return list;
        }
        String[] columns = columnsText.split("\n");
        for (String column : columns) {
            if (StringUtils.isNotBlank(column)) {
                if (FlinkVersion.FLINK_112.getType().equals(componentVersion) && DataSourceType.HBASE.getVal().equals(dataSourceType)) {
                    // 1. flink 1.12 2. 数据源为 hbase
                    JSONObject obj = new JSONObject();
                    obj.put("column", column);
                    list.add(obj);
                } else {
                    //根据空格分隔字段名和类型
                    String[] s = column.trim().split("\\s+", 2);
                    if (s.length == 2) {
                        if (check.contains(s[0].trim())) {
                            throw new DtCenterDefException(String.format("field name:[%s] fill in the repeat", column));
                        } else {
                            check.add(s[0].trim());
                        }
                        JSONObject obj = new JSONObject();
                        obj.put("column", s[0].trim());
                        obj.put("type", s[1].trim());
                        list.add(obj);
                    } else {
                        throw new DtCenterDefException(String.format("field information:[%s] fill in the wrong", column));
                    }
                }
            }
        }
        return list;
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
        if (TaskCreateModelType.GUIDE.getType().equals(task.getCreateModel())) {
            sql.append(generateCreateFlinkSql(task.getSourceStr(), task.getComponentVersion(), TableType.SOURCE));
            sql.append(generateCreateFlinkSql(task.getTargetStr(), task.getComponentVersion(), TableType.SINK));
            sql.append(generateCreateFlinkSql(task.getSideStr(), task.getComponentVersion(), TableType.SIDE));
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
        Set<String> funcSet = batchFunctionService.getFuncSet(task, true);
        List<String> addJarSqlList = batchFunctionService.generateFuncSql(funcSet, task.getTenantId());
        StringBuilder sb = new StringBuilder();
        addJarSqlList.forEach(sql -> sb.append(sql).append(";"));
        return sb.toString();
    }


    /**
     * ----------------------------------------------------------
     */


    /**
     * 开启任务
     *
     * @param task
     * @param externalPath
     * @return
     */
    public String startFlinkSqlTask(Task task, String externalPath) {
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

    private String sendTaskStartTrigger(Task task, String externalPath) {
        //重置任务状态
        resetTaskStatus(task.getJobId());
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
            if (!TaskStatusCheckUtil.CAN_RESET_STATUS.contains(status)) {
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
        String sql = generateSqlToScheduler(task).toString();
        task.setSqlText(sql);
        task.setTaskType(EScheduleJobType.SQL.getType());
        return generateParamActionExt(task, externalPath, taskParams);
    }


    private ParamActionExt generateParamActionExt(Task task, String externalPath, String taskParams) {
        Map<String, Object> actionParam = JsonUtils.objectToMap(task);
        // 去除默认生成的字段
        actionParam.remove("mainClass");
        actionParam.remove("class");
        // 补充其他的字段
        actionParam.put("type", EScheduleType.NORMAL_SCHEDULE.getType());
        actionParam.put("tenantId", task.getTenantId());
        actionParam.put("taskParams", formatTaskParams(taskParams, task.getSourceStr(), task.getComponentVersion()));
        actionParam.put("name", task.getName());
        actionParam.put("deployMode", EDeployMode.PERJOB.getType());

        if (!Strings.isNullOrEmpty(externalPath)) {
            actionParam.put("externalPath", externalPath);
        }
        return JsonUtils.objectToObject(actionParam, ParamActionExt.class);
    }

    private String formatTaskParams(String taskParams, String sourceParam, String componentVersion) {
        List<String> params = new ArrayList<>();
        String[] tempParams = taskParams.split("\r|\n");
        for (String param : tempParams) {
            if (StringUtils.isNotEmpty(param.trim()) && !param.trim().startsWith("#") && param.contains("=")) {
                int special = param.indexOf("=");
                params.add(String.format("%s=%s", param.substring(0, special).trim(), param.substring(special + 1).trim()));
            }
        }
        if (StringUtils.isNotEmpty(sourceParam)) {
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
    public StartFlinkSqlResultVO startFlinkSql(Long taskId, String externalPath) {
        StartFlinkSqlResultVO startFlinkSqlResultVO = new StartFlinkSqlResultVO();
        Task task = developTaskMapper.selectById(taskId);
        try {
            startFlinkSqlTask(task, externalPath);
            startFlinkSqlResultVO.setMsg(String.format("任务提交成功,名称为: %s", task.getName()));
            startFlinkSqlResultVO.setJobId(task.getJobId());
            startFlinkSqlResultVO.setStatus(TaskStatus.SUBMITTING.getStatus());
        } catch (Exception e) {
            LOGGER.warn("startFlinkSQL-->", e);
            startFlinkSqlResultVO.setMsg(e.getMessage());
            startFlinkSqlResultVO.setStatus(TaskStatus.SUBMITFAILD.getStatus());
        }
        return startFlinkSqlResultVO;
    }


    /**
     * 停止任务
     *
     * @param taskId
     * @return
     */
    public Boolean stopStreamTask(Long taskId) {
        Task task = developTaskMapper.selectById(taskId);
        return actionService.stop(Arrays.asList(task.getJobId()));
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
        TaskVO taskVO = TaskMapstructTransfer.INSTANCE.TaskResourceParamToTaskVO(taskResourceParam);
        convertTableStr(taskResourceParam, taskVO);
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

    public CheckResult grammarCheck(Long taskId) {
        Task task = developTaskMapper.selectById(taskId);
        return grammarCheck(task);
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
        List<Integer> type = CollectionUtils.isEmpty(taskSearchVO.getType()) ? EScheduleJobType.getStreamJobTypes() : taskSearchVO.getType().stream().filter(EScheduleJobType.getStreamJobTypes()::contains).collect(Collectors.toList());
        List<Task> taskList = developTaskMapper.selectList(Wrappers.lambdaQuery(Task.class).in(Task::getTaskType, type).eq(Task::getTenantId, taskSearchVO.getTenantId()).eq(Task::getIsDeleted, Deleted.NORMAL.getStatus()).like(Task::getName, taskSearchVO.getTaskName()));
        if (CollectionUtils.isEmpty(taskList)) {
            return PageResult.EMPTY_PAGE_RESULT;
        }
        List<String> jobIds = taskList.stream().map(Task::getJobId).collect(Collectors.toList());
        List<ScheduleJob> scheduleJobs = scheduleJobMapper.getRdosJobByJobIds(jobIds);
        Map<String, ScheduleJob> engineJobMap = new HashMap<>();
        for (ScheduleJob scheduleJob : scheduleJobs) {
            engineJobMap.put(scheduleJob.getJobId(), scheduleJob);
        }

        PageQuery<TaskListResultVO> pageQuery = new PageQuery<>(taskSearchVO.getCurrentPage(), taskSearchVO.getPageSize());
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
                vo.setStatus(status != null ? status : TaskStatus.UNSUBMIT.getStatus());

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
        return new PageResult<>(streamTaskVOS, streamTaskVOS.size(), pageQuery);
    }

    public Map<String, Integer> getStatusCountByCondition(TaskStatusSearchVO taskSearchDTO) {

        Map<String, Integer> statusCount = Maps.newHashMap();
        statusCount.put("ALL", 0);
        statusCount.put(TaskStatus.FAILED.name(), 0);
        statusCount.put(TaskStatus.RUNNING.name(), 0);
        statusCount.put(TaskStatus.CANCELED.name(), 0);
        statusCount.put("UNRUNNING", 0);
        List<Integer> type = CollectionUtils.isEmpty(taskSearchDTO.getType()) ? EScheduleJobType.getStreamJobTypes() : taskSearchDTO.getType().stream().filter(EScheduleJobType.getStreamJobTypes()::contains).collect(Collectors.toList());
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
}
