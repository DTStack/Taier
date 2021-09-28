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

package com.dtstack.batch.service.task.impl;


import com.alibaba.fastjson.*;
import com.dtstack.batch.common.enums.EDeployType;
import com.dtstack.batch.common.enums.PublishTaskStatusEnum;
import com.dtstack.engine.domain.BaseEntity;
import com.dtstack.engine.domain.BatchDataSource;
import com.dtstack.engine.domain.BatchTask;
import com.dtstack.engine.domain.User;
import com.dtstack.engine.domain.*;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.*;
import com.dtstack.batch.dao.po.TaskOwnerAndProjectPO;
import com.dtstack.batch.domain.*;
import com.dtstack.batch.dto.BatchTaskDTO;
import com.dtstack.batch.engine.rdbms.common.enums.Constant;
import com.dtstack.batch.enums.*;
import com.dtstack.batch.mapping.TaskTypeEngineTypeMapping;
import com.dtstack.batch.parser.ESchedulePeriodType;
import com.dtstack.batch.parser.ScheduleCron;
import com.dtstack.batch.parser.ScheduleFactory;
import com.dtstack.batch.schedule.JobParamReplace;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceService;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceTaskRefService;
import com.dtstack.batch.service.datasource.impl.IMultiEngineService;
import com.dtstack.batch.service.impl.*;
import com.dtstack.batch.service.job.ITaskService;
import com.dtstack.batch.service.job.impl.BatchJobService;
import com.dtstack.batch.service.table.ISqlExeService;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.job.SyncJobCheck;
import com.dtstack.batch.vo.*;
import com.dtstack.batch.web.pager.PageQuery;
import com.dtstack.batch.web.task.vo.query.AllProductGlobalSearchVO;
import com.dtstack.batch.web.task.vo.result.BatchTaskGetComponentVersionResultVO;
import com.dtstack.batch.web.task.vo.result.BatchTaskGetSupportJobTypesResultVO;
import com.dtstack.batch.web.task.vo.result.BatchTaskRecentlyRunTimeResultVO;
import com.dtstack.dtcenter.common.constant.PatternConstant;
import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.dtcenter.common.enums.ESubmitStatus;
import com.dtstack.dtcenter.common.thread.RdosThreadFactory;
import com.dtstack.dtcenter.common.util.*;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.engine.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.dto.UserDTO;
import com.dtstack.engine.common.pager.PageResult;
import com.dtstack.engine.master.vo.ScheduleTaskShadeVO;
import com.dtstack.engine.master.vo.ScheduleTaskVO;
import com.dtstack.engine.master.vo.schedule.task.shade.ScheduleTaskShadePageVO;
import com.dtstack.engine.master.vo.schedule.task.shade.ScheduleTaskShadeTypeVO;
import com.dtstack.engine.master.vo.task.NotDeleteTaskVO;
import com.dtstack.engine.master.vo.task.SaveTaskTaskVO;
import com.dtstack.engine.master.vo.template.TaskTemplateResultVO;
import com.dtstack.engine.master.impl.*;
import com.dtstack.engine.master.impl.ProjectService;
import com.dtstack.engine.master.impl.TenantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/4
 */
@Service
public class BatchTaskService {

    public static Logger logger = LoggerFactory.getLogger(BatchTaskService.class);

    @Resource(name = "batchJobParamReplace")
    private JobParamReplace jobParamReplace;

    @Autowired
    private BatchTaskParamShadeService batchTaskParamShadeService;

    @Autowired
    private BatchTaskRecordService batchTaskRecordService;

    @Autowired
    private BatchTaskDao batchTaskDao;

    @Autowired
    private TaskParamTemplateService taskParamTemplateService;

    @Autowired
    private BatchTaskResourceService batchTaskResourceService;

    @Autowired
    private BatchTaskResourceDao batchTaskResourceDao;

    @Autowired
    private BatchTaskParamService batchTaskParamService;

    @Autowired
    private BatchTaskTaskService batchTaskTaskService;

    @Autowired
    private BatchDataSourceService dataSourceService;

    @Autowired
    private BatchCatalogueDao batchCatalogueDao;

    @Autowired
    private UserService userService;

    @Autowired
    private BatchDataSourceTaskRefService dataSourceTaskRefService;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private BatchFunctionService batchFunctionService;

    @Autowired
    private BatchTaskVersionDao batchTaskVersionDao;

    @Autowired
    private BatchSysParamService batchSysParamService;

    @Autowired
    private BatchResourceService batchResourceService;

    @Autowired
    private BatchResourceDao batchResourceDao;

    @Autowired
    private BatchFunctionDao batchFunctionDao;

    @Autowired
    private RoleUserService roleUserService;

    @Autowired
    private DictService dictService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private BatchDataSourceTaskRefService batchDataSourceTaskRefService;

    @Autowired
    private IMultiEngineService multiEngineService;

    @Autowired
    private MultiEngineServiceFactory multiEngineServiceFactory;

    @Autowired
    private BatchSqlExeService batchSqlExeService;

    @Autowired
    private BatchTaskResourceShadeService batchTaskResourceShadeService;

    @Autowired
    private ScheduleTaskTaskShadeService scheduleTaskTaskShadeService;

    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private BatchJobService batchJobService;

    @Autowired
    private ReadWriteLockDao readWriteLockDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ComponentService componentService;

    private static Map<Integer, List<Pair<Integer, String>>> jobSupportTypeMap = Maps.newHashMap();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String DEFAULT_SCHEDULE_CONF = "{\"selfReliance\":false, \"min\":0,\"hour\":0,\"periodType\":\"2\",\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"isFailRetry\":true,\"maxRetryNum\":\"3\"}";

    private static final Integer DEFAULT_SCHEDULE_PERIOD = ESchedulePeriodType.DAY.getVal();

    private static final String CMD_OPTS = "--cmd-opts";

    private static final String OPERATE_MODEL = "operateModel";

    private static final Integer IS_FILE = 1;

    private static final Integer IS_SUBMIT = 1;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final Integer INIT_LOCK_VERSION = 0;

    private static Boolean YES_IS_ROOT = true;

    private static Boolean IGNORE_CHECK = false;

    private static final String LOGGER_AGAIN_PUSH_TASK = "[id: %s , name : %s]";

    private static final Long MIN_PERIOD = 300L;

    /**
     * 任务自动提交异常信息模板
     */
    private static final String LOGGER_TENANT_PROJECT_TASK_ERROR = "租户名称：%s，项目名称：%s，任务名称：%s，异常提交，需要手动处理；";

    private static final ExecutorService TASK_PUBLISH_JOB = new ThreadPoolExecutor(8, 8, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(5000), new RdosThreadFactory("BatchPublishTaskFunction"));


    @Autowired
    private ReadWriteLockService readWriteLockService;

    private static final String TASK_PATTERN = "[\\u4e00-\\u9fa5_a-z0-9A-Z-]+";

    @PostConstruct
    public void init() {

        //初始化可以支持的任务类型
        final List<Dict> yarn = this.dictService.getDictByType(DictType.BATCH_TASK_TYPE_YARN.getValue());
        final List<Pair<Integer, String>> yarnSupportType = yarn.stream().map(dict -> Pair.of(dict.getDictValue(), dict.getDictNameZH())).collect(Collectors.toList());

        final List<Dict> libraDict = this.dictService.getDictByType(DictType.BATCH_TASK_TYPE_LIBRA.getValue());
        final List<Pair<Integer, String>> libraSupportType = libraDict.stream().map(dict -> Pair.of(dict.getDictValue(), dict.getDictNameZH())).collect(Collectors.toList());

        jobSupportTypeMap.put(EDeployType.YARN.getType(), yarnSupportType);
        jobSupportTypeMap.put(EDeployType.LIBRA.getType(), libraSupportType);
    }

    /**
     * 任务克隆
     */
    @Transactional
    public BatchTask cloneTask(Long projectId, Long userId, Long taskId, String taskName, String taskDesc, Long nodePid) {

        final BatchTask distTask = this.copyTask(projectId, userId, taskId, taskName, taskDesc, nodePid, 0L);

        // 工作流任务拷贝子节点
        if (distTask.getTaskType().equals(EJobType.WORK_FLOW.getVal())) {
            // 子节点之间的依赖关系
            final List<BatchTask> subTasks = this.batchTaskDao.listByFlowId(taskId);
            if (CollectionUtils.isNotEmpty(subTasks)) {

                final Map<Long, BatchTask> srcTaskIdDistTaskMap = new HashMap<>();
                for (final BatchTask subTask : subTasks) {
                    final BatchTask subDistTask = this.copyTask(projectId, userId, subTask.getId(), subTask.getName(), subTask.getTaskDesc(), subTask.getNodePid(), distTask.getId());
                    srcTaskIdDistTaskMap.put(subTask.getId(), subDistTask);
                }

                // 更新sqlText
                this.updateWorkFlowSqlText(srcTaskIdDistTaskMap, distTask);
                // 解析出子任务间的依赖关系
                final Map<Long, List<Long>> dependency = this.parseTaskRelationsFromSqlText(distTask.getSqlText());
                // 判断任务依赖是否成环
                if (MapUtils.isNotEmpty(dependency)) {
                    checkIsLoopByList(dependency);
                }
                // 添加任务依赖关系
                for (final Map.Entry<Long, List<Long>> entry : dependency.entrySet()) {
                    List<BatchTask> dependencyTasks = getTaskByIds(entry.getValue());
                    dependencyTasks.stream().forEach(task -> {
                        task.setProjectId(distTask.getProjectId());
                        task.setTenantId(distTask.getTenantId());
                        task.setAppType(AppType.RDOS.getType());
                    });
                    batchTaskTaskService.addOrUpdateTaskTask(entry.getKey(), dependencyTasks);
                }
            }
        }
        return distTask;
    }

    /**
     * 更新工作流里的任务依赖关系
     *
     * @param srcTaskIdDistTaskMap
     * @param batchTask
     */
    public void updateWorkFlowSqlText(final Map<Long, BatchTask> srcTaskIdDistTaskMap, final BatchTask batchTask) {
        final JSONArray jsonArray = JSON.parseArray(batchTask.getSqlText());
        BatchTask subTask;
        for (int i = 0; i < jsonArray.size(); i++) {
            final JSONObject node = jsonArray.getJSONObject(i);
            if (node.containsKey("source") && node.containsKey("target")) {
                final JSONObject sourceNode = node.getJSONObject("source");
                subTask = srcTaskIdDistTaskMap.get(sourceNode.getJSONObject("data").getLong("id"));
                this.updateDataNode(sourceNode, subTask);

                final JSONObject targetNode = node.getJSONObject("target");
                subTask = srcTaskIdDistTaskMap.get(targetNode.getJSONObject("data").getLong("id"));
                this.updateDataNode(targetNode, subTask);
            } else {
                subTask = srcTaskIdDistTaskMap.get(node.getJSONObject("data").getLong("id"));
                this.updateDataNode(node, subTask);
            }
        }

        batchTask.setSqlText(jsonArray.toJSONString());
        this.batchTaskDao.update(batchTask);
    }

    private void updateDataNode(final JSONObject node, final BatchTask subTask) {
        if (subTask == null) {
            return;
        }

        node.put("id", subTask.getId());
        final JSONObject data = node.getJSONObject("data");
        data.put("id", subTask.getId());
        data.put("name", subTask.getName());
        data.put("parentId", subTask.getNodePid());
        data.put("submitStatus", "0");
        data.put("version", "0");
    }

    private BatchTask copyTask(final Long projectId, final Long userId, final Long srcTaskId, String taskName, final String taskDesc, final Long nodePid, final Long flowId) {
        final BatchTask srcTask = this.batchTaskDao.getOne(srcTaskId);
        if (srcTask == null) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }

        BatchTask taskGetByName = this.batchTaskDao.getByName(taskName, projectId);
        if (flowId == 0) {
            if (taskGetByName != null) {
                throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
            }
        } else {
            while (taskGetByName != null) {
                taskName = NameUtil.getCopyName(taskName);
                taskGetByName = this.batchTaskDao.getByName(taskName, projectId);
            }
        }

        final BatchTask distTask = new BatchTask();
        PublicUtil.copyPropertiesIgnoreNull(srcTask, distTask);
        distTask.setId(null);
        distTask.setProjectId(projectId);
        distTask.setName(taskName);
        distTask.setCreateUserId(userId);
        distTask.setModifyUserId(userId);
        distTask.setTaskDesc(taskDesc);
        distTask.setOwnerUserId(userId);
        distTask.setNodePid(nodePid);
        distTask.setFlowId(flowId);
        distTask.setVersion(0);
        distTask.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
        distTask.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        this.batchTaskDao.insert(distTask);
        final BatchTaskRecord record = new BatchTaskRecord();
        record.setTaskId(this.batchTaskDao.getByName(distTask.getName(), distTask.getProjectId()).getId());
        record.setProjectId(distTask.getProjectId());
        record.setTenantId(distTask.getTenantId());
        record.setRecordType(TaskOperateType.CREATE.getType());
        record.setOperatorId(userId);
        record.setOperateTime(new Timestamp(System.currentTimeMillis()));
        this.batchTaskRecordService.saveTaskRecord(record);

        // 任务数据源关系拷贝
        this.batchDataSourceTaskRefService.copyTaskDataSource(srcTaskId, distTask);

        // 任务主资源关系拷贝
        this.batchTaskResourceService.copyTaskResource(srcTaskId, distTask, ResourceRefType.MAIN_RES.getType());

        //任务依赖资源拷贝
        this.batchTaskResourceService.copyTaskResource(srcTaskId, distTask, ResourceRefType.DEPENDENCY_RES.getType());

        // 任务参数拷贝
        this.batchTaskParamService.copyTaskParam(srcTaskId, distTask.getId());

        // 新增锁
        this.readWriteLockService.getLock(srcTask.getTenantId(), userId, ReadWriteLockType.BATCH_TASK.name(),
                distTask.getId(), projectId, null, null);

        return distTask;
    }

    /**
     * 数据开发-任务全局搜索
     *
     * @param taskName
     */
    public List<Map<String, Object>> globalSearch(String taskName, long projectId) {
        final List<Map<String, Object>> result = Lists.newArrayList();
        if (StringUtils.isEmpty(taskName)) {
            return result;
        }

        final BatchTaskDTO batchTaskDTO = new BatchTaskDTO();
        batchTaskDTO.setProjectId(projectId);
        batchTaskDTO.setFlowId(null);
        batchTaskDTO.setIsDeleted(Deleted.NORMAL.getStatus());
        if (StringUtils.isNotBlank(taskName)) {
            batchTaskDTO.setFuzzName(taskName);
        }

        final PageQuery<BatchTaskDTO> pageQuery = new PageQuery<BatchTaskDTO>(batchTaskDTO);

        final List<BatchTask> batchTasks = this.batchTaskDao.generalQuery(pageQuery);

        Map<String, Object> elem = null;
        for (final BatchTask task : batchTasks) {
            elem = new HashMap<>();
            elem.put("name", task.getName());
            elem.put("id", task.getId());
            elem.put("createUser", userService.getUserName(task.getCreateUserId()));
            elem.put("taskType", task.getTaskType());
            result.add(elem);
        }
        return result;
    }

    /**
     * 数据开发-根据任务id，查询详情
     *
     * @return
     * @author toutian
     */
    public BatchTaskBatchVO getTaskById(final ScheduleTaskVO ScheduleTaskVO) {
        // 如果非离线的任务，则调用engine获取任务信息
        if (Objects.nonNull(ScheduleTaskVO.getAppType()) && AppType.RDOS.getType() != ScheduleTaskVO.getAppType()) {
            ScheduleTaskShade taskShade = scheduleTaskShadeService.findTaskId(ScheduleTaskVO.getId(), Deleted.NORMAL.getStatus(),ScheduleTaskVO.getAppType());
            BatchTaskBatchVO taskBatchVO = new BatchTaskBatchVO();
            BeanUtils.copyProperties(taskShade, taskBatchVO);
            return taskBatchVO;
        }

        final BatchTask task = this.batchTaskDao.getOne(ScheduleTaskVO.getId());
        if (task == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }

        final List<BatchResource> resources = this.batchTaskResourceService.getResources(ScheduleTaskVO.getId(), task.getProjectId(), ResourceRefType.MAIN_RES.getType());
        final List<BatchResource> refResourceIdList = this.batchTaskResourceService.getResources(ScheduleTaskVO.getId(), task.getProjectId(), ResourceRefType.DEPENDENCY_RES.getType());

        final BatchTaskBatchVO taskVO = new BatchTaskBatchVO(this.batchTaskTaskService.getForefathers(task));
        taskVO.setVersion(task.getVersion());
        if (task.getTaskType().intValue() == EJobType.SYNC.getVal().intValue()) {  //同步任务类型
            final String taskJson = Base64Util.baseDecode(task.getSqlText());
            if (StringUtils.isBlank(taskJson)) {
                taskVO.setCreateModel(Constant.CREATE_MODEL_GUIDE);  //向导模式存在为空的情况
                taskVO.setSqlText("");
            } else {
                final JSONObject obj = JSON.parseObject(taskJson);
                taskVO.setCreateModel(obj.get("createModel") == null ? Constant.CREATE_MODEL_GUIDE : Integer.parseInt(String.valueOf(obj.get("createModel"))));
                formatSqlText(taskVO, obj);
            }
        }

        this.setTaskOperatorModelAndOptions(taskVO, task);
        if (task.getFlowId() != null && task.getFlowId() > 0) {
            taskVO.setFlowId(task.getFlowId());
            final BatchTask flow = this.batchTaskDao.getOne(task.getFlowId());
            if (flow != null) {
                taskVO.setFlowName(flow.getName());
            }
        }

        final BatchCatalogue catalogue = this.batchCatalogueDao.getOne(task.getNodePid());
        if (catalogue != null) {
            taskVO.setNodePName(catalogue.getNodeName());
        }

        taskVO.setResourceList(resources);
        taskVO.setRefResourceList(refResourceIdList);

        final PageQuery pageQuery = new PageQuery(1, 5, "gmt_create", Sort.DESC.name());
        final List<BatchTaskVersionDetail> taskVersions = this.batchTaskVersionDao.listByTaskId(ScheduleTaskVO.getId(), pageQuery).stream()
                .map(ver -> {
                    if (StringUtils.isNotBlank(ver.getOriginSql())) {
                        if (task.getTaskType().intValue() == EJobType.SYNC.getVal().intValue()) {
                            ver.setSqlText(ver.getSqlText());
                        } else {
                            ver.setSqlText(ver.getOriginSql());
                        }

                    }
                    // 填充用户名称
                    ver.setUserName(userService.getUserName(ver.getCreateUserId()));
                    return ver;
                }).collect(Collectors.toList());
        taskVO.setTaskVersions(taskVersions);

        // 密码脱敏 --2019/10/25 茂茂-- 同步任务 密码脱敏 仅 向导模式 修改成 全部模式
        if (task.getTaskType().intValue() == EJobType.SYNC.getVal().intValue()) {
            try {
                taskVO.setSqlText(JsonUtils.formatJSON(DataFilter.passwordFilter(taskVO.getSqlText())));
            }catch (final Exception e){
                logger.error(String.format("同步任务json解析失败 taskId = {%s},错误= {%s}",task.getId(),e.getMessage()));
                taskVO.setSqlText(DataFilter.passwordFilter(taskVO.getSqlText()));
            }

            for (final BatchTaskVersionDetail taskVersion : taskVO.getTaskVersions()) {
                try {
                    taskVersion.setSqlText(JsonUtils.formatJSON(DataFilter.passwordFilter(taskVersion.getSqlText())));
                }catch (final Exception e){
                    logger.error(String.format("同步任务json解析失败 taskVersionId = {%s},错误= {%s}",taskVersion.getId(),e.getMessage()));
                    taskVersion.setSqlText(DataFilter.passwordFilter(taskVersion.getSqlText()));
                }
            }
        }

        final ReadWriteLockVO readWriteLockVO = this.readWriteLockService.getDetail(
                task.getProjectId(), task.getId(),
                ReadWriteLockType.BATCH_TASK, ScheduleTaskVO.getUserId(),
                task.getModifyUserId(),
                task.getGmtModified());
        taskVO.setReadWriteLockVO(readWriteLockVO);
        taskVO.setUserId(ScheduleTaskVO.getUserId());
        setTaskVariables(taskVO, ScheduleTaskVO.getId());
        final Set<Long> userIds = new HashSet<>();
        userIds.add(task.getCreateUserId());
        userIds.add(task.getOwnerUserId());
        final Map<Long, User> userMap = userService.getUserMap(userIds);
        buildUserDTOInfo(userMap,taskVO);
        return taskVO;
    }

    private void setTaskOperatorModelAndOptions(final ScheduleTaskVO taskVO, final BatchTask task) {
        if (task.getTaskType().equals(EJobType.PYTHON.getVal())
                || task.getTaskType().equals(EJobType.SPARK_PYTHON.getVal())
                || task.getTaskType().equals(EJobType.ML_LIb.getVal())
                || task.getTaskType().equals(EJobType.SPARK.getVal())
                || task.getTaskType().equals(EJobType.HADOOP_MR.getVal())) {
            if (StringUtils.isBlank(task.getExeArgs())) {
                //  兼容之前v3.3及以前生成的task
                taskVO.setOperateModel(TaskOperateType.RESOURCE.getType());
            } else {
                JSONObject exeArgsJson;
                try {
                    exeArgsJson = JSON.parseObject(task.getExeArgs());
                } catch (final Exception e) {
                    // 兼容v3.3之前
                    exeArgsJson = new JSONObject();
                    exeArgsJson.put(CMD_OPTS, task.getExeArgs());
                    exeArgsJson.put(OPERATE_MODEL, TaskOperateType.RESOURCE.getType());
                }
                if (task.getTaskType().equals(EJobType.PYTHON.getVal())) {
                    taskVO.setPythonVersion(exeArgsJson.getInteger("--python-version"));
                    taskVO.setLearningType(0);
                    taskVO.setInput(exeArgsJson.getString("--input"));
                    taskVO.setOutput(exeArgsJson.getString("--output"));
                }
                taskVO.setOptions(exeArgsJson.getString(CMD_OPTS));
                taskVO.setOperateModel(exeArgsJson.getIntValue(OPERATE_MODEL));
            }
        }
    }

    private void setTaskVariables(final ScheduleTaskVO taskVO, final Long taskId) {
        final List<BatchTaskParam> taskParams = this.batchTaskParamService.getTaskParam(taskId);
        final List<Map> mapParams = new ArrayList<>();
        if (taskParams != null) {
            for (final BatchTaskParam taskParam : taskParams) {
                final Map map = new HashMap();
                map.put("type", taskParam.getType());
                map.put("paramName", taskParam.getParamName());
                map.put("paramCommand", taskParam.getParamCommand());
                mapParams.add(map);
            }
        }
        taskVO.setTaskVariables(mapParams);
    }

    /**
     * 数据开发-根据项目id获取任务列表
     *
     * @param projectId
     * @return
     * @author toutian
     */
    public List<BatchTask> getTasksByProjectId(Long tenantId, Long projectId, String taskName) {
        //构建查询条件
        ScheduleTaskShadeDTO dto = new ScheduleTaskShadeDTO();
        dto.setTenantId(tenantId);
        dto.setProjectId(projectId);
        dto.setTaskName(taskName);
        dto.setAppType(AppType.RDOS.getType());
        dto.setPageIndex(1);
        dto.setPageSize(200);

        List<BatchTask> returnList = getTaskIdAndNameBySchedule(dto);
        return returnList;
    }

    /**
     * 根据信息 去dagScheduleX获取 任务信息 只返回taskId 和TaskName
     *
     * @param dto
     * @return
     */
    public List<BatchTask> getTaskIdAndNameBySchedule(ScheduleTaskShadeDTO dto) {
        PageResult<List<ScheduleTaskShadeVO>> submitTaskList = scheduleTaskShadeService.pageQuery(dto);
        List<ScheduleTaskShadeVO> scheduleTaskVOS = submitTaskList.getData();
        List<BatchTask> returnList=  new ArrayList<>();
        scheduleTaskVOS.forEach(bean ->{
            BatchTask task = new BatchTask();
            task.setId(bean.getTaskId());
            task.setName(bean.getName());
            returnList.add(task);
        });
        return returnList;
    }

    /**
     * 查询工作流任务
     */
    public List<BatchTask> queryTaskByType(Long projectId,
                                           String taskName,
                                           Integer taskType) {
        final EJobType eJobType = EJobType.getEJobType(taskType);
        if (eJobType == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        return this.batchTaskDao.listTaskByType(projectId, eJobType.getVal(), taskName);
    }

    /**
     * 克隆任务到工作流
     *
     * @param userId
     * @param taskId
     * @param taskName
     * @param taskDesc
     * @param flowId      克隆到哪个工作流
     * @param coordsExtra 图形坐标信息
     * @return
     */
    @Transactional
    public BatchTask cloneTaskToFlow(Long userId,
                                     Long taskId,
                                     String taskName,
                                     String taskDesc,
                                     Long flowId,
                                     Map<String, Object> coordsExtra) {
        //step 1 检查原始task合理性，检查目的的工作流合理性
        final BatchTask srcTask = this.batchTaskDao.getOne(taskId);
        if (srcTask == null) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }
        //原始task是工作流任务不能被克隆至工作流
        if (srcTask.getTaskType().equals(EJobType.WORK_FLOW.getVal())) {
            throw new RdosDefineException("工作流任务不能被克隆至工作流", ErrorCode.TASK_CAN_NOT_BE_CLONED);
        }
        // 增量同步不能被克隆至工作流
        if (srcTask.getTaskType().equals(EJobType.SYNC.getVal())) {
            if (StringUtils.isNotEmpty(srcTask.getSqlText())) {
                final JSONObject json = JSON.parseObject(Base64Util.baseDecode(srcTask.getSqlText()));
                final int syncModel = json.getIntValue("syncModel");
                if (syncModel == 1) {
                    throw new RdosDefineException(ErrorCode.TASK_CAN_NOT_BE_CLONED);
                }
            }
        }
        //step 2 检查目的task合理性
        final BatchTask flowTask = this.batchTaskDao.getOne(flowId);
        if (flowTask == null) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }
        if (!flowTask.getTaskType().equals(EJobType.WORK_FLOW.getVal())) {
            throw new RdosDefineException("只能克隆至工作流任务", ErrorCode.TASK_CAN_NOT_BE_CLONED);
        }
        //step 3 检查名称是否存在
        final BatchTask taskGetByName = this.batchTaskDao.getByName(taskName, flowTask.getProjectId());
        if (taskGetByName != null) {
            throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
        }

        final BatchTask distTask = new BatchTask();
        PublicUtil.copyPropertiesIgnoreNull(srcTask, distTask);
        distTask.setId(null);
        distTask.setProjectId(flowTask.getProjectId());
        distTask.setName(taskName);
        distTask.setCreateUserId(userId);
        distTask.setModifyUserId(userId);
        if (StringUtils.isEmpty(taskDesc)) {
            taskDesc = srcTask.getTaskDesc();
        }
        distTask.setTaskDesc(taskDesc);
        distTask.setOwnerUserId(userId);
        distTask.setNodePid(flowTask.getNodePid());
        distTask.setFlowId(flowId);
        distTask.setVersion(0);
        distTask.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
        distTask.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        this.batchTaskDao.insert(distTask);
        final BatchTaskRecord record = new BatchTaskRecord();
        record.setTaskId(distTask.getId());
        record.setProjectId(distTask.getProjectId());
        record.setTenantId(distTask.getTenantId());
        record.setRecordType(TaskOperateType.CREATE.getType());
        record.setOperatorId(userId);
        record.setOperateTime(new Timestamp(System.currentTimeMillis()));
        this.batchTaskRecordService.saveTaskRecord(record);

        // 任务数据源关系拷贝
        this.batchDataSourceTaskRefService.copyTaskDataSource(taskId, distTask);

        // 任务主资源关系拷贝
        this.batchTaskResourceService.copyTaskResource(taskId, distTask, ResourceRefType.MAIN_RES.getType());

        //任务依赖资源拷贝
        this.batchTaskResourceService.copyTaskResource(taskId, distTask, ResourceRefType.DEPENDENCY_RES.getType());

        // 任务参数拷贝
        this.batchTaskParamService.copyTaskParam(taskId, distTask.getId());

        // 新增锁
        final ReadWriteLockVO lock = this.readWriteLockService.getLock(distTask.getTenantId(), userId, ReadWriteLockType.BATCH_TASK.name(),
                distTask.getId(), distTask.getProjectId(), null, null);
        //更新工作流task sqlText字段
        final String sqlText = this.buildSqlText(flowTask, distTask, lock, coordsExtra);
        flowTask.setSqlText(sqlText);
        this.batchTaskDao.update(flowTask);

        return distTask;
    }

    /**
     * 克隆task到工作流后重新生成sqlText
     *
     * @param flowTask
     * @param coordsExtra
     * @return
     */
    private String buildSqlText(final BatchTask flowTask, final BatchTask clonedTask, final ReadWriteLockVO lock, final Map<String, Object> coordsExtra) {
        final Map<String, Object> taskGraphMap = new HashMap<>();
        if (coordsExtra != null) {
            taskGraphMap.putAll(coordsExtra);
        } else {
            taskGraphMap.put("vertex", true);
            taskGraphMap.put("edge", false);
            taskGraphMap.put("x", 100);
            taskGraphMap.put("y", -100);
            taskGraphMap.put("value", null);
        }
        taskGraphMap.put("id", clonedTask.getId());
        final Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("id", clonedTask.getId());
        taskMap.put("name", clonedTask.getName());
        taskMap.put("type", "file");
        taskMap.put("taskType", clonedTask.getTaskType());
        taskMap.put("parentId", clonedTask.getNodePid());
        taskMap.put("catalogueType", CatalogueType.TASK_DEVELOP.getType());
        taskMap.put("nodePid", clonedTask.getNodePid());
        taskMap.put("submitStatus", clonedTask.getSubmitStatus());
        taskMap.put("version", clonedTask.getVersion());
        taskGraphMap.put("data", taskMap);
        taskGraphMap.put("readWriteLockVO", lock);

        final String sqlText = flowTask.getSqlText();
        JSONArray graphArray = null;
        if (StringUtils.isNotEmpty(sqlText)) {
            graphArray = JSON.parseArray(sqlText);
        } else {
            graphArray = new JSONArray();
        }
        graphArray.add(taskGraphMap);
        return graphArray.toJSONString();
    }

    /**
     * 数据开发-根据项目id,任务名 获取任务列表
     *
     * @param projectId
     * @return
     * @author toutian
     */
    public List<BatchTask> getTasksByName(long projectId,
                                          String name) {
        return this.batchTaskDao.listByNameFuzzy(projectId, name);
    }

    /**
     * 推荐依赖任务
     *
     * @param projectId
     * @param taskId
     * @return
     */
    public List<Map<String, Object>> recommendDependencyTask(long projectId, long taskId) {
        return Collections.emptyList();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByProjectId(Long projectId, Long userId) {
        batchTaskResourceService.deleteByProjectId(projectId);
        batchTaskResourceShadeService.deleteByProjectId(projectId);
        batchTaskTaskService.deleteByProjectId(projectId);
        batchTaskVersionDao.deleteByProjectId(projectId);
        batchTaskRecordService.deleteByProjectId(projectId);
        batchTaskDao.deleteByProjectId(projectId, userId);
    }

    /**
     * 数据开发-获取依赖任务
     * 可以被依赖的任务必须是已经发布的
     *
     * @param projectId
     * @param taskId
     * @return
     * @author toutian
     */
    public List<Map<String, Object>> getDependencyTask(long projectId,
                                                       long taskId,
                                                       String name, Long searchProjectId) {

        /**
         * 虚节点不支持查询依赖（前段隐藏该功能，后端验证判断）
         */
        final BatchTask task = this.batchTaskDao.getOne(taskId);
        if (task == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        if (task.getTaskType().intValue() == EJobType.VIRTUAL.getVal().intValue()) {
            throw new RdosDefineException(ErrorCode.VIRTUAL_TASK_UNSUPPORTED_OPERATION);
        }
        final List<BatchTaskTask> taskTasks = this.batchTaskTaskService.getByParentTaskId(taskId);
        final List<Long> excludeIds = new ArrayList<>(taskTasks.size());
        excludeIds.add(taskId);
        taskTasks.forEach(taskTask -> {
            excludeIds.add(taskTask.getTaskId());
        });
        if (searchProjectId == null) {
            searchProjectId = projectId;
        }

        final List<Map<String, Object>> result = this.scheduleTaskShadeService.listDependencyTask(excludeIds, name, searchProjectId);
        if (CollectionUtils.isNotEmpty(result)) {

            List<Long> taskIds = Lists.newArrayList();
            for (Map map : result) {
                taskIds.add(MapUtils.getLong(map, "task_id"));
            }
            //根据taskIdList查询本地已经提交的task
            List<BatchTask> localSubmitTaskList = batchTaskDao.listSubmitTaskByIds(taskIds, searchProjectId);
            List<Long> localSubmitTaskIdList = Lists.newArrayList();
            if(CollectionUtils.isNotEmpty(localSubmitTaskList)){
                localSubmitTaskIdList = localSubmitTaskList.stream().map(BatchTask::getId).collect(Collectors.toList());
            }
            //移除本地不存在的task
            Iterator<Map<String, Object>> resutlIterator = result.iterator();
            while (resutlIterator.hasNext()){
                Map<String, Object> next = resutlIterator.next();
                if(!localSubmitTaskIdList.contains(MapUtils.getLong(next, "task_id"))){
                    resutlIterator.remove();
                }
            }

            final List<Long> userIds = new ArrayList<>(result.size());
            for (final Map map : result) {
                final Object createUserId = map.get("createUserId");
                if (Objects.nonNull(createUserId) && createUserId instanceof Integer) {
                    userIds.add(Long.valueOf((int) createUserId));
                }
            }
            final Map<Long, User> userMap = userService.getUserMap(userIds);
            for (final Map<String, Object> r : result) {
                ScheduleEngineProject project = this.projectService.getProjectById(searchProjectId);
                Long userId = null;
                if (Objects.nonNull(r.get("createUserId")) && r.get("createUserId") instanceof Integer) {
                    userId = Long.valueOf((int) r.get("createUserId"));
                }
                r.put("createUser", userMap.get(userId));
                r.put("tenantName", tenantService.getByDtUicTenantId(project.getUicTenantId()).getTenantName());
                r.put("projectName", project.getProjectName());
            }
        }
        return result;
    }

    /**
     * 数据开发-检查task与依赖的task是否有构成有向环
     *
     * @author toutian
     */
    public BatchTask checkIsLoop(Long taskId,
                                 Long dependencyTaskId) {

        final HashSet<Long> node = new HashSet<>();
        node.add(taskId);

        final Long loopTaskId = this.isHasLoop(dependencyTaskId, node);
        if (loopTaskId == 0L) {
            return null;
        }
        return this.batchTaskDao.getOne(loopTaskId);
    }

    public Long isHasLoop(final Long parentTaskId, final HashSet<Long> node) {
        final HashSet<Long> loopNode = new HashSet<>(node.size() + 1);
        loopNode.addAll(node);
        loopNode.add(parentTaskId);
        //出现闭环则返回
        if (loopNode.size() == node.size()) {
            return parentTaskId;
        }

        final List<BatchTaskTask> taskTasks = this.batchTaskTaskService.getAllParentTask(parentTaskId);
        if (CollectionUtils.isEmpty(taskTasks)) {
            return 0L;
        }
        for (final BatchTaskTask subTask : taskTasks) {
            final Long loopTaskId = this.isHasLoop(subTask.getParentTaskId(), loopNode);
            if (loopTaskId != 0L) {
                return loopTaskId;
            }
        }

        return 0L;
    }

    public void checkIsLoopByList(final Long taskId, final List<Long> parentTaskIds) {
        if (CollectionUtils.isNotEmpty(parentTaskIds)) {
            for (final Long parentTaskId : parentTaskIds) {
                final BatchTask task = this.checkIsLoop(taskId, parentTaskId);
                if (task != null) {
                    throw new RdosDefineException(task.getName() + "任务发生依赖闭环");
                }
            }
        }
    }

    /**
     * 判断是否成环
     *
     * @param nodeMap 任务完整依赖关系  key：节点  value 节点的所有父节点
     * @return
     */
    public void checkIsLoopByList(Map<Long, List<Long>> nodeMap) {
        if (MapUtils.isEmpty(nodeMap)) {
            return;
        }
        for (Map.Entry<Long, List<Long>> entry : nodeMap.entrySet()) {
            mapDfs(entry.getKey(), new HashSet(), nodeMap);
        }
    }

    /**
     * 图深度遍历
     *
     * @param taskId  任务ID
     * @param set     已经遍历过的节点
     * @param nodeMap 任务完整依赖关系  key：节点  value 节点的所有父节点
     */
    private void mapDfs(Long taskId, HashSet<Long> set, Map<Long, List<Long>> nodeMap) {
        HashSet<Long> node = new HashSet<>(set);
        // 判断该节点是否以及存在，如果存在，则证明成环了
        if (set.contains(taskId)) {
            BatchTask task = batchTaskDao.getOne(taskId);
            if (Objects.nonNull(task)) {
                throw new RdosDefineException(String.format("%s任务发生依赖闭环", task.getName()));
            }
        }
        node.add(taskId);
        for (Long j : nodeMap.get(taskId)){
            mapDfs(j, node, nodeMap);
        }
    }

    /**
     * 数据开发-关键字搜索
     * 1. 模糊查询文件夹，点击可展开目录得到文件列表
     * 2. 模糊查询文件
     *
     * @param projectId 项目id
     * @param name      名字
     * @return
     * @author toutian
     */
    public TaskCatalogueVO queryCatalogueTasks(Long projectId,
                                               String name) {

        final List<BatchCatalogue> catalogues = this.batchCatalogueDao.listByNameFuzzy(projectId, name);

        final List<BatchTask> tasks = this.batchTaskDao.listByNameFuzzy(projectId, name);

        final TaskCatalogueVO taskCatalogueVO = new TaskCatalogueVO();
        taskCatalogueVO.setCatalogues(catalogues);
        taskCatalogueVO.setTasks(tasks);

        return taskCatalogueVO;
    }

    /**
     * 运维中心 - 任务管理 - 搜索
     *
     * @param tenantId    租户id
     * @param projectId   项目id
     * @param name        名字
     * @param ownerId     责任人id
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @param currentPage 当前页
     * @param pageSize    分页大小
     * @return
     * @author toutian
     */
    public Map<String, Object> queryTasks(Long tenantId,
                                          Long projectId,
                                          String name,
                                          Long ownerId,
                                          Long startTime,
                                          Long endTime,
                                          Integer scheduleStatus,
                                          String taskTypeList,
                                          String periodTypeList,
                                          Integer currentPage,
                                          Integer pageSize, String searchType) {

        Long dtUicTenantId = tenantService.getDtuicTenantId(tenantId);

        //需要处理用户信息
        ScheduleTaskShadePageVO data = scheduleTaskShadeService.queryTasks(tenantId, dtUicTenantId, projectId, name, ownerId, startTime, endTime, scheduleStatus, taskTypeList, periodTypeList, currentPage, pageSize, searchType, AppType.RDOS.getType());
        Map<String,Object> resMap = new HashMap<>(8);
        PageResult<List<ScheduleTaskVO>> pageResult = data.getPageResult();
        List<ScheduleTaskVO> vos = data.getPageResult().getData();
        if (CollectionUtils.isNotEmpty(vos)) {
            Set<Long> userIds = vos.stream().map(ScheduleTaskVO::getCreateUserId).collect(Collectors.toSet());
            userIds.addAll(vos.stream().map(ScheduleTaskVO::getOwnerUserId).collect(Collectors.toSet()));
            final Map<Long, User> userMap = userService.getUserMap(userIds);
            for (final ScheduleTaskVO vo : vos) {
                final Long taskId = vo.getTaskId();
                vo.setTaskId(taskId);
                vo.setId(taskId);
                //维持原来逻辑
                vo.setId(taskId);
                vo.setName(vo.getName());
                buildUserDTOInfo(userMap, vo);
                if(CollectionUtils.isNotEmpty(vo.getRelatedTasks())){
                    //补充子任务用户信息
                    dealFlowWorkSubTasks(vo.getRelatedTasks());
                }
            }
        }
        resMap.put("pageSize", pageResult.getPageSize());
        resMap.put("totalCount", pageResult.getTotalCount());
        resMap.put("totalPage", pageResult.getTotalPage());
        resMap.put("data", vos);
        return resMap;
    }

    public void buildUserDTOInfo(final Map<Long, User> userMap, final ScheduleTaskVO vo) {
        if (Objects.nonNull(vo.getCreateUserId())) {
            final User createUser = userMap.get(vo.getCreateUserId());
            final UserDTO dto = new UserDTO();
            BeanUtils.copyProperties(createUser, dto);
            vo.setCreateUser(dto);
            if (vo.getCreateUserId().equals(vo.getModifyUserId())) {
                vo.setModifyUser(dto);
            } else {
                final UserDTO modifyDto = new UserDTO();
                BeanUtils.copyProperties(userMap.getOrDefault(vo.getModifyUserId(),new User()),modifyDto);
                vo.setModifyUser(modifyDto);
            }

            if (vo.getCreateUserId().equals(vo.getOwnerUserId())) {
                vo.setOwnerUser(dto);
            } else {
                final UserDTO ownerDto = new UserDTO();
                BeanUtils.copyProperties(userMap.getOrDefault(vo.getOwnerUserId(),new User()), ownerDto);
                vo.setOwnerUser(ownerDto);
            }
        }
    }

    private List<ScheduleTaskVO> dealFlowWorkSubTasks(final List<ScheduleTaskVO> vos) {
        final Set<Long> userIds = vos.stream().map(ScheduleTaskVO::getCreateUserId).collect(Collectors.toSet());
        userIds.addAll(vos.stream().map(ScheduleTaskVO::getOwnerUserId).collect(Collectors.toSet()));
        final Map<Long, User> userMap = userService.getUserMap(userIds);
        for (final ScheduleTaskVO vo : vos) {
            buildUserDTOInfo(userMap, vo);
        }
        return vos;
    }

    /**
     * 查询工作流下的子节点
     *
     * @param taskId
     * @return
     */
    public BatchTaskBatchVO dealFlowWorkTask(Long taskId, String taskTypes, Long ownerId) {
        List<Integer> types = null;
        if (StringUtils.isNotBlank(taskTypes)) {
            types = Arrays.stream(taskTypes.split(",")).map(Integer::valueOf).collect(Collectors.toList());
        }
        return buildUserInfo(this.scheduleTaskShadeService.dealFlowWorkTask(taskId, AppType.RDOS.getType(), types, ownerId));
    }

    private BatchTaskBatchVO buildUserInfo(final ScheduleTaskVO batchTask) {
        if (Objects.isNull(batchTask)) {
            return null;
        }

        final long taskId = batchTask.getTaskId();
        final BatchTask task = this.batchTaskDao.getOne(taskId);
        if (Objects.isNull(task)) {
            return null;
        }
        final Set<Long> userIds = new HashSet<>();
        final List<BatchTaskBatchVO> tasks = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(batchTask.getRelatedTasks())) {
            for (final ScheduleTaskVO relatedTask : batchTask.getRelatedTasks()) {
                tasks.add(new BatchTaskBatchVO(relatedTask));
            }
        }
        tasks.add(new BatchTaskBatchVO(batchTask));
        userIds.addAll(tasks.stream().map(ScheduleTaskVO::getCreateUserId).collect(Collectors.toSet()));
        userIds.addAll(tasks.stream().map(ScheduleTaskVO::getOwnerUserId).collect(Collectors.toSet()));
        userIds.addAll(tasks.stream().map(ScheduleTaskVO::getModifyUserId).collect(Collectors.toSet()));

        final Map<Long, User> userMap = userService.getUserMap(userIds);
        if (MapUtils.isEmpty(userMap)) {
            return new BatchTaskBatchVO(batchTask);
        }
        final BatchTaskBatchVO batchTaskBatchVO = this.getBatchTaskUserDTO(batchTask, userMap);

        if (CollectionUtils.isEmpty(batchTask.getRelatedTasks())) {
            return batchTaskBatchVO;
        }
        final List<ScheduleTaskVO> taskBatchVOS = new ArrayList<>();
        if(CollectionUtils.isEmpty(batchTask.getRelatedTasks())){
            return batchTaskBatchVO;
        }
        for (final ScheduleTaskVO relate : batchTask.getRelatedTasks()) {
            taskBatchVOS.add(getBatchTaskUserDTO(relate,userMap));
        }
        batchTaskBatchVO.setRelatedTasks(taskBatchVOS);
        return batchTaskBatchVO;
    }

    private BatchTaskBatchVO getBatchTaskUserDTO(final ScheduleTaskVO batchTask, final Map<Long, User> userMap) {
        final BatchTaskBatchVO batchTaskBatchVO = new BatchTaskBatchVO(batchTask);
        final User createUser = userMap.get(batchTask.getCreateUserId());
        final UserDTO createDto = new UserDTO();
        BeanUtils.copyProperties(createUser, createDto);
        batchTaskBatchVO.setCreateUser(createDto);
        if (batchTask.getCreateUserId().equals(batchTask.getModifyUserId())) {
            batchTaskBatchVO.setModifyUser(createDto);
        } else {
            final User modifyUser = userMap.get(batchTask.getModifyUserId());
            final UserDTO modifiyDto = new UserDTO();
            BeanUtils.copyProperties(modifyUser, modifiyDto);
            batchTaskBatchVO.setModifyUser(modifiyDto);
        }
        if (batchTask.getOwnerUserId().equals(batchTask.getOwnerUserId())) {
            batchTaskBatchVO.setOwnerUser(createDto);
        } else {
            final User ownerUser = userMap.get(batchTask.getOwnerUserId());
            final UserDTO ownerUserDTO = new UserDTO();
            BeanUtils.copyProperties(ownerUser, ownerUserDTO);
            batchTaskBatchVO.setOwnerUser(ownerUserDTO);
        }
        return batchTaskBatchVO;
    }

    /**
     * 任务发布权限判断、发布
     * @param projectId
     * @param id
     * @param userId
     * @param publishDesc
     * @param isRoot
     * @param dtuicTenantId
     * @throws Exception
     */
    @Transactional
    public void checkAndPublishTask(Long projectId, Long id, Long userId,
                                    String publishDesc, Boolean isRoot,
                                    Long dtuicTenantId, String commitId) {
        TaskCheckResultVO checkPermissionVO = publishTask(projectId, id, userId, publishDesc, isRoot,false, dtuicTenantId, commitId);
        if (!PublishTaskStatusEnum.NOMAL.getType().equals(checkPermissionVO.getErrorSign())) {
            throw new RdosDefineException(checkPermissionVO.getErrorMessage());
        }
    }
    /**
     * 任务发布
     * @param projectId
     * @param id
     * @param userId
     * @param publishDesc
     * @param isRoot
     * @param ignoreCheck 是否忽略语法校验
     * @return
     * @throws Exception
     */
    @Transactional
    public TaskCheckResultVO publishTask(Long projectId, Long id, Long userId,
                                         String publishDesc, Boolean isRoot, Boolean ignoreCheck, Long dtuicTenantId, String commitId) {
        TaskCheckResultVO vo ;
        BatchTask task = batchTaskDao.getOne(id);
        if (task == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        List<String> resourceLimitErrors = scheduleTaskShadeService.checkResourceLimit(dtuicTenantId, task.getTaskType(), task.getTaskParams(), null);
        if (CollectionUtils.isNotEmpty(resourceLimitErrors)) {
            vo = new TaskCheckResultVO();
            vo.setErrorMessage(StringUtils.join(resourceLimitErrors, ","));
            vo.setErrorSign(PublishTaskStatusEnum.PERMISSIONERROR.getType());
            return vo;
        }
        JSONObject scheduleConf = JSON.parseObject(task.getScheduleConf());
        //判断自定义调度是否合法
        checkCronValid(scheduleConf);
        // 需要发布的任务集合
        List<BatchTask> tasks = Lists.newArrayList();
        // 工作流下所有子任务置为发布状态
        if (task.getTaskType().equals(EJobType.WORK_FLOW.getVal())) {
            final List<BatchTask> subTasks = this.getFlowWorkSubTasks(id);
            tasks.addAll(subTasks);
            final Map<Long, List<Long>> relations = this.parseTaskRelationsFromSqlText(task.getSqlText());
            final List<String> noParents = Lists.newArrayList();
            for (final BatchTask t : subTasks) {
                //没有父节点
                if (CollectionUtils.isEmpty(relations.get(t.getId()))) {
                    noParents.add(t.getName());
                }
            }
            if (noParents.size() >= 2) {
                throw new RdosDefineException("工作流中包含多个根节点:" + StringUtils.join(noParents, ","));
            }
            // 判断工作流任务是否成环
            if (MapUtils.isNotEmpty(relations)) {
                checkIsLoopByList(relations);
            }
        }

        tasks.add(task);
        return publishBatchTaskInfo(tasks, projectId, userId, publishDesc, isRoot, ignoreCheck, commitId);
    }

    /**
     * 批量发布任务至engine
     * @param subTasks 要发布的task集合
     * @param projectId 项目id
     * @param userId 用户id
     * @param publishDesc 发布描述
     * @param isRoot 是否是管理员
     * @param ignoreCheck 忽略检查
     * @return 发布结果
     */
    public TaskCheckResultVO publishBatchTaskInfo(List<BatchTask> subTasks, Long projectId, Long userId, String publishDesc, Boolean isRoot, Boolean ignoreCheck, String commitId) {
        //判断任务责任人是否存在 如果任务责任人不存在或无权限 不允许提交
        subTasks.forEach(task -> {
            User user = userService.getById(task.getOwnerUserId());
            if (user == null){
                throw new RdosDefineException(String.format("%s任务责任人在数栈中不存在", task.getName()));
            }
        });

        TaskCheckResultVO checkResultVO = new TaskCheckResultVO();
        checkResultVO.setErrorSign(PublishTaskStatusEnum.NOMAL.getType());

        // 检查任务是否可以发布并记录版本信息
        for (BatchTask task : subTasks) {
            TaskCheckResultVO resultVO = checkTaskAndSaveVersion(task, projectId, userId, publishDesc, isRoot, ignoreCheck);
            if (!PublishTaskStatusEnum.NOMAL.getType().equals(resultVO.getErrorSign())){
                //做一下优化 如果是工作流任务的话 把任务名称打印出来
                if (task.getFlowId()>0){
                    resultVO.setErrorMessage(String.format("任务:%s提交失败，原因是:%s", task.getName(), resultVO.getErrorMessage()));
                }
                return resultVO;
            }
        }
        // 发布任务中所有的依赖关系
        List<BatchTaskTask> allTaskTaskList = new ArrayList<>();
        // 构建要发布的任务列表
        List<ScheduleTaskShadeDTO> scheduleTasks = subTasks.stream().map(task -> buildScheduleTaskShadeDTO(task, allTaskTaskList)).collect(Collectors.toList());
        logger.info("待发布任务检查完毕，{}个任务准备处于待提交状态，taskId：{}", scheduleTasks.size(), scheduleTasks.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList()));
        // 批量发布任务
        String taskCommitId = this.scheduleTaskShadeService.addOrUpdateBatchTask(scheduleTasks, commitId);
        logger.info("待发布任务提交完毕，commitId：{}", taskCommitId);
        if (StringUtils.isBlank(taskCommitId)) {
            throw new RdosDefineException("engine返回commitId为空");
        }

        // 判断任务依赖关系
        if (CollectionUtils.isNotEmpty(allTaskTaskList)) {
            SaveTaskTaskVO saveTaskTaskVO = scheduleTaskTaskShadeService.saveTaskTaskList(JSON.toJSONString(allTaskTaskList), taskCommitId);
            // 判断任务关系是否正常
            // 失败情况：1、任务成环  2、依赖的任务已经被删除
            if (BooleanUtils.isFalse(saveTaskTaskVO.getSave())) {
                throw new RdosDefineException(saveTaskTaskVO.getMsg());
            }
        }

        // 提交任务参数信息并保存任务记录和更新任务状态
        for (BatchTask task : subTasks) {
            try {
                this.batchJobService.sendTaskStartTrigger(task.getId(), userId, taskCommitId);
                // 无异常保存一条任务记录并更新任务状态
                saveRecordAndUpdateSubmitStatus(task, projectId, userId, TaskOperateType.COMMIT.getType(), ESubmitStatus.SUBMIT.getStatus());
            } catch (Exception e) {
                logger.error("send task error {} ", task.getId(), e);
                throw new RdosDefineException(String.format("任务提交异常：%s", e.getMessage()), e);
            }
        }
        if (StringUtils.isBlank(commitId)) {
            // 无异常表示任务提交全部提交成功，调用engine接口提交
            this.scheduleTaskShadeService.taskCommit(taskCommitId);
        }
        logger.info("待发布任务参数提交完毕");
        return checkResultVO;
    }

    /**
     * 保存一条任务记录并更新任务状态
     * @param task 任务信息
     * @param projectId 项目id
     * @param userId 用户id
     * @param taskOperateType 任务操作类型 @{@link TaskOperateType}
     * @param submitStatus 发布状态 {@link ESubmitStatus}
     */
    private void saveRecordAndUpdateSubmitStatus(BatchTask task, Long projectId, Long userId, Integer taskOperateType, Integer submitStatus) {
        final BatchTaskRecord record = new BatchTaskRecord();
        record.setTaskId(this.batchTaskDao.getByName(task.getName(), task.getProjectId()).getId());
        record.setProjectId(task.getProjectId());
        record.setTenantId(task.getTenantId());
        record.setRecordType(taskOperateType);
        record.setOperatorId(userId);
        record.setOperateTime(new Timestamp(System.currentTimeMillis()));
        this.batchTaskRecordService.saveTaskRecord(record);
        this.updateSubmitStatus(projectId, task.getId(), submitStatus);
    }

    /**
     * 检查要发布的任务并保存版本信息
     *
     * @param task 任务信息
     * @param projectId 项目id
     * @param userId 用户id
     * @param publishDesc 发布描述
     * @param isRoot 是否是管理员
     * @param ignoreCheck 是否忽略检查
     * @return 检查结果
     */
    private TaskCheckResultVO checkTaskAndSaveVersion(BatchTask task, Long projectId, Long userId, String publishDesc, Boolean isRoot, Boolean ignoreCheck) {
        TaskCheckResultVO checkVo  = new TaskCheckResultVO();
        checkVo.setErrorSign(PublishTaskStatusEnum.NOMAL.getType());
        checkTaskCanSubmit(task);

        task.setSubmitStatus(ESubmitStatus.SUBMIT.getStatus());
        final Long dtuicTenantId = tenantService.getDtuicTenantId(task.getTenantId());

        Integer engineType = null;
        if (!EJobType.WORK_FLOW.getVal().equals(task.getTaskType())
                && !EJobType.ALGORITHM_LAB.getVal().equals(task.getTaskType())
                && !EJobType.VIRTUAL.getVal().equals(task.getTaskType())) {
            final MultiEngineType multiEngineType = TaskTypeEngineTypeMapping.getEngineTypeByTaskType(task.getTaskType());
            engineType = multiEngineType.getType();
            final ITaskService taskService = this.multiEngineServiceFactory.getTaskService(multiEngineType.getType());
            if (Objects.nonNull(taskService)) {
                taskService.readyForPublishTaskInfo(task, dtuicTenantId, projectId);
            }
        }

        task.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));

        final BatchTaskVersion version = new BatchTaskVersion();
        version.setCreateUserId(userId);

        String versionSqlText = StringUtils.EMPTY;

        if (EJobType.SPARK_SQL.getVal().intValue() == task.getTaskType().intValue()
                || EJobType.LIBRA_SQL.getVal().intValue() == task.getTaskType().intValue()
                || EJobType.HIVE_SQL.getVal().intValue() == task.getTaskType().intValue()) {
            // 语法检测
            List<BatchTaskParam> taskParamsToReplace = batchTaskParamService.getTaskParam(task.getId());
            versionSqlText = this.jobParamReplace.paramReplace(task.getSqlText(), taskParamsToReplace, this.sdf.format(new Date()));
            //避免重复校验
            ScheduleTaskShade taskShade = this.scheduleTaskShadeService.findTaskId(task.getId(), Deleted.NORMAL.getStatus(), AppType.RDOS.getType());
            String sqlTextShade = null == taskShade ? "" : taskShade.getSqlText();
            boolean checkSyntax = !((sqlTextShade != null && sqlTextShade.equals(task.getSqlText()))) && ignoreCheck;

            CheckSyntaxResult syntaxResult = batchSqlExeService.processSqlText(dtuicTenantId,task.getTaskType(), versionSqlText, userId, task.getTenantId(),
                    task.getProjectId(), checkSyntax, isRoot, engineType, task.getTaskParams());
            if (!syntaxResult.getCheckResult()){
                checkVo.setErrorSign(PublishTaskStatusEnum.CHECKSYNTAXERROR.getType());
                checkVo.setErrorMessage(syntaxResult.getMessage());
                return checkVo;
            }

        } else if (EJobType.TIDB_SQL.getVal().intValue() == task.getTaskType().intValue()) {
            // 语法检测
            List<BatchTaskParam> taskParamsToReplace = batchTaskParamService.getTaskParam(task.getId());
            versionSqlText = jobParamReplace.paramReplace(task.getSqlText(), taskParamsToReplace, sdf.format(new Date()));
            ProjectEngine projectEngine = projectEngineService.getProjectDb(projectId, engineType);
            ISqlExeService sqlExeService = multiEngineServiceFactory.getSqlExeService(engineType, null, projectId);
            String sql = sqlExeService.process(versionSqlText, projectEngine.getEngineIdentity());

        } else if (EJobType.SYNC.getVal().intValue() == task.getTaskType().intValue()) {
            if (StringUtils.isNotEmpty(task.getSqlText())) {
                final JSONObject jsonTask = JSON.parseObject(Base64Util.baseDecode(task.getSqlText()));

                Integer createModelType = Integer.valueOf(jsonTask.getString("createModel"));
                JSONObject job = jsonTask.getJSONObject("job");
                if (Objects.isNull(job)) {
                    throw new RdosDefineException(String.format("数据同步任务：%s 未配置", task.getName()));
                }
                // 检测job格式
                SyncJobCheck.checkJobFormat(job.toJSONString(), createModelType);
                versionSqlText = jsonTask.getString("job");
            }
        } else if (EJobType.PYTHON.getVal().equals(task.getTaskType())
                || EJobType.SPARK_PYTHON.getVal().equals(task.getTaskType())) {
            final JSONObject exeArgsJson = JSON.parseObject(task.getExeArgs());
            if (exeArgsJson != null) {
                final Integer operateModel = exeArgsJson.getInteger("operateModel");
                if (TaskOperateType.EDIT.getType() == operateModel) {
                    versionSqlText = task.getSqlText();
                }
            }
        }

        version.setSqlText(versionSqlText);
        version.setOriginSql(task.getSqlText());
        version.setProjectId(task.getProjectId());
        version.setTenantId(task.getTenantId());
        version.setTaskId(task.getId());
        //任务的版本号
        version.setVersion(task.getVersion());
        version.setTaskParams(task.getTaskParams());
        version.setScheduleConf(task.getScheduleConf());
        version.setScheduleStatus(task.getScheduleStatus());
        version.setGmtModified(task.getGmtModified());

        String dependencyTaskIds = StringUtils.EMPTY;
        final List<BatchTaskTask> taskTasks = this.batchTaskTaskService.getAllParentTask(task.getId());
        if (CollectionUtils.isNotEmpty(taskTasks)) {
            List<Map<String, Object>> parentTasks = taskTasks.stream().map(taskTask -> {
                Map<String, Object> map = Maps.newHashMap();
                map.put("parentAppType", taskTask.getParentAppType());
                map.put("parentTaskId", taskTask.getParentTaskId());
                return map;
            }).collect(Collectors.toList());
            dependencyTaskIds = JSON.toJSONString(parentTasks);
        }

        version.setDependencyTaskIds(dependencyTaskIds);
        version.setPublishDesc(null == publishDesc ? "" : publishDesc);
        // 插入一条记录信息
        this.batchTaskVersionDao.insert(version);
        task.setDtuicTenantId(dtuicTenantId);
        task.setVersion(version.getId().intValue());
        return checkVo;
    }

    /**
     * 构建一个要发布到engine的任务DTO {@link ScheduleTaskShadeDTO}
     * @param batchTask 要发布的任务集合
     * @param allTaskTaskList 任务之间的依赖关系
     * @return 调度任务DTO
     */
    private ScheduleTaskShadeDTO buildScheduleTaskShadeDTO(final BatchTask batchTask, List<BatchTaskTask> allTaskTaskList) {
        if (batchTask.getId() <= 0) {
            //只有异常情况才会走到该逻辑
            throw new RdosDefineException("batchTask id can't be 0", ErrorCode.SERVER_EXCEPTION);
        }

        final long taskId = batchTask.getId();
        //清空任务关联的batch_task_param, task_resource, task_task 表信息
        this.batchTaskParamShadeService.clearDataByTaskId(taskId);
        this.batchTaskResourceShadeService.clearDataByTaskId(taskId);
        this.scheduleTaskTaskShadeService.clearDataByTaskId(taskId, AppType.RDOS.getType());

        final List<BatchTaskParam> batchTaskParamList = this.batchTaskParamService.getTaskParam(batchTask.getId());
        //查询出任务所有的关联的资源(运行主体资源和依赖引用资源)
        final List<BatchTaskResource> batchTaskResourceList = this.batchTaskResourceService.getTaskResources(batchTask.getId(), null, batchTask.getProjectId());
        final List<BatchTaskTask> batchTaskTaskList = this.batchTaskTaskService.getAllParentTask(batchTask.getId());

        if (!CollectionUtils.isEmpty(batchTaskParamList)) {
            this.batchTaskParamShadeService.saveTaskParam(batchTaskParamList);
        }

        // 处理任务之间的依赖关系
        if (CollectionUtils.isNotEmpty(batchTaskTaskList)) {
            for (BatchTaskTask batchTaskTask : batchTaskTaskList) {
                batchTaskTask.setDtuicTenantId(batchTask.getDtuicTenantId());
                batchTaskTask.setAppType(AppType.RDOS.getType());
            }
            allTaskTaskList.addAll(batchTaskTaskList);
        }

        if (!CollectionUtils.isEmpty(batchTaskResourceList)) {
            this.batchTaskResourceShadeService.saveTaskResource(batchTaskResourceList);
        }
        //保存batch_task_shade
        final ScheduleTaskShadeDTO scheduleTaskShadeDTO = new ScheduleTaskShadeDTO();
        BeanUtils.copyProperties(batchTask, scheduleTaskShadeDTO);
        scheduleTaskShadeDTO.setTaskId(batchTask.getId());
        scheduleTaskShadeDTO.setAppType(AppType.RDOS.getType());
        scheduleTaskShadeDTO.setScheduleStatus(EScheduleStatus.NORMAL.getVal());
        if (StringUtils.isNotEmpty(batchTask.getScheduleConf())) {
            JSONObject scheduleConfig = JSONObject.parseObject(batchTask.getScheduleConf());
            if (scheduleConfig != null) {
                scheduleTaskShadeDTO.setIsExpire(scheduleConfig.getBooleanValue("isExpire") ? 1 : 0);
            } else {
                scheduleTaskShadeDTO.setIsExpire(0);
            }
        }
        return scheduleTaskShadeDTO;
    }

    public List<BatchTaskVersionDetail> getTaskVersionRecord(Long taskId,
                                                             Integer pageSize,
                                                             Integer pageNo) {
        if (pageNo == null) {
            pageNo = 0;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        final PageQuery pageQuery = new PageQuery(pageNo, pageSize, "gmt_create", Sort.DESC.name());
        List<BatchTaskVersionDetail> res = this.batchTaskVersionDao.listByTaskId(taskId, pageQuery);
        for (BatchTaskVersionDetail detail : res) {
            detail.setUserName(userService.getUserName(detail.getCreateUserId()));
        }
        return res;
    }

    public BatchTaskVersionDetail taskVersionScheduleConf(Long versionId) {
        final BatchTaskVersionDetail taskVersion = this.batchTaskVersionDao.getByVersionId(versionId);
        if (taskVersion == null) {
            return null;
        }
        taskVersion.setUserName(userService.getUserName(taskVersion.getCreateUserId()));
        if (StringUtils.isNotBlank(taskVersion.getDependencyTaskIds())) {
            List<Map<String, Object>> dependencyTasks = getDependencyTasks(taskVersion.getDependencyTaskIds());
            JSONObject taskParams = new JSONObject();
            int i = 1;
            for (Map<String, Object> dependencyTask : dependencyTasks) {
                ScheduleTaskShade taskShade = scheduleTaskShadeService.findTaskId(MathUtil.getLongVal(dependencyTask.get("parentTaskId")),
                        Deleted.NORMAL.getStatus(), MathUtil.getIntegerVal(dependencyTask.get("parentAppType")));
                if (taskShade != null) {
                    JSONObject taskParam = new JSONObject();
                    taskParam.put("taskName", taskShade.getName());
                    taskParam.put("tenantName", tenantService.getTenantById(taskShade.getTenantId()).getTenantName());
                    ScheduleEngineProject project = projectService.getProjectById(taskShade.getProjectId());
                    if (project != null) {
                        taskParam.put("projectName", project.getProjectName());
                    } else {
                        taskParam.put("projectName", "");
                    }
                    taskParams.put("task" + i++, taskParam);
                }
            }
            taskVersion.setDependencyTasks(taskParams);
        }
        return taskVersion;
    }

    /**
     * 获取任务json模板，过滤账号密码
     *
     * @param param
     * @return
     */
    public String getJsonTemplate(final TaskResourceParam param) {
        param.setCreateModel(Constant.CREATE_MODEL_TEMPLATE);  //脚本模式
        param.getSourceMap().put("column", Lists.newArrayList());
        param.getSourceMap().put("table", "");
        param.getTargetMap().put("column", Lists.newArrayList());
        param.getTargetMap().put("table", "");
        param.setSettingMap(new HashMap<>());
        final String syncSql = this.dataSourceService.getSyncSql(param,true);
        return DataFilter.passwordFilter(syncSql);
    }

    /**
     * 数据开发-新建/更新 任务
     *
     * @param param 任务
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @author toutian
     */
    @Transactional(rollbackFor = Exception.class)
    public TaskCatalogueVO addOrUpdateTask(final TaskResourceParam param) {
        //检查密码回填操作
        this.checkFillPassword(param);
        //数据预处理 主要是数据同步任务 生成sqlText
        final Integer engineType = this.checkBeforeUpdateTask(param);
        if (StringUtils.isNotBlank(param.getScheduleConf())) {
            //处理调度配置
            JSONObject schduleConf = JSON.parseObject(param.getScheduleConf());
            //判断自定义调度是否合法
            checkCronValid(schduleConf);
            if (schduleConf.get("isExpire") != null && "false".equals(schduleConf.get("isExpire").toString())) {
                schduleConf.replace("isLastInstance", true);
                param.setScheduleConf(schduleConf.toString());
            }
            param.setPeriodType(schduleConf.getInteger("periodType"));
        }
        if (param.getId() > 0 && param.getTaskType().equals(EJobType.WORK_FLOW.getVal())) {
            //更新子任务间的依赖关系
            final String sqlText = param.getSqlText();
            if (StringUtils.isNotBlank(sqlText)) {
                final Map<Long, List<Long>> relations = this.parseTaskRelationsFromSqlText(sqlText);
                // 判断任务依赖是否成环
                if (MapUtils.isNotEmpty(relations)) {
                    checkIsLoopByList(relations);
                }
                for (final Map.Entry<Long, List<Long>> entry : relations.entrySet()) {
                    List<BatchTask> dependencyTasks = getTaskByIds(entry.getValue());
                    dependencyTasks.stream().forEach(task -> {
                        task.setProjectId(param.getProjectId());
                        task.setTenantId(param.getTenantId());
                        task.setAppType(AppType.RDOS.getType());
                    });
                    batchTaskTaskService.addOrUpdateTaskTask(entry.getKey(), dependencyTasks);
                }
            }
        }

        BatchTaskBatchVO task = null;
        try {
            task = PublicUtil.objectToObject(param, BatchTaskBatchVO.class);
        } catch (IOException e) {
            throw new RdosDefineException(e.getMessage(), e);
        }
        task.setModifyUserId(param.getUserId());
        task.setVersion(Objects.isNull(param.getVersion()) ? 0 : param.getVersion());
        task.setEngineType(engineType);
        task.parsePeriodType();
        task = this.updateTask(task, param.getIsEditBaseInfo());
        TaskCatalogueVO taskCatalogueVO = new TaskCatalogueVO(task, task.getNodePid());
        if (task.getReadWriteLockVO().getResult() != TaskLockStatus.TO_UPDATE.getVal()) {
            return taskCatalogueVO;
        }

        //更新 关联资源
        if (param.getResourceIdList() != null) {
            final Map<String, Object> params = Maps.newHashMap();
            params.put("id", task.getId());
            params.put("projectId", task.getProjectId());
            params.put("resources", param.getResourceIdList());
            params.put("createUserId", task.getCreateUserId());
            this.updateTaskResource(params);
        }

        if (param.getRefResourceIdList() != null) {
            final Map<String, Object> params = Maps.newHashMap();
            params.put("id", task.getId());
            params.put("projectId", task.getProjectId());
            params.put("refResource", param.getRefResourceIdList());
            params.put("createUserId", task.getCreateUserId());
            this.updateTaskRefResource(params);
        }

        final User user = userService.getById(task.getModifyUserId());
        if (user != null) {
            taskCatalogueVO.setCreateUser(user.getUserName());
        }
        final List<BatchTask> dependencyTasks = param.getDependencyTasks();
        if (dependencyTasks != null) {
            this.batchTaskTaskService.addOrUpdateTaskTask(task.getId(), dependencyTasks);
            taskCatalogueVO.setDependencyTasks(dependencyTasks);
        }

        String createUserName = userService.getUserName(task.getCreateUserId());
        taskCatalogueVO.setCreateUser(createUserName);
        taskCatalogueVO.setCatalogueType(CatalogueType.TASK_DEVELOP.getType());

        return taskCatalogueVO;
    }

    /**
     * 密码回填检查方法
     **/
    private void checkFillPassword(final TaskResourceParam param) {
        // 单独对同步任务中密码进行补全处理 将未变更的 ****** 填充为原密码信息 --2019/10/25 茂茂--
        if (param.getId() > 0 && EJobType.SYNC.getVal().equals(param.getTaskType())) {
            final String context = param.getSqlText();
            if (null == context) {
                return;
            }
            //1、检查上送字段是否存在需要处理的密码，不存在直接跳过
            final Pattern pattern = Pattern.compile(PatternConstant.PASSWORD_FIELD_REGEX, Pattern.CASE_INSENSITIVE);
            final Matcher matcher = pattern.matcher(context);
            if (matcher.find()) {
                logger.debug("当前上送信息存在隐藏密码字段，准备执行旧密码回填操作");
                //2、查询旧数据信息，保存成结构数据，待数据解析补充
                final BatchTask task = this.batchTaskDao.getOne(param.getId());
                if (Objects.nonNull(task)) {
                    final String sqlText = task.getSqlText();
                    if (StringUtils.isNotEmpty(sqlText)) {
                        final JSONObject oldData = JSON.parseObject(Base64Util.baseDecode(sqlText));
                        //3、处理新上送的数据，替换未变更的密码信息
                        final JSONObject newData = JSON.parseObject(context);
                        //值并行处理 -- 固定接口直接写死job的值处理密码问题
                        this.fillPassword(newData, oldData.getJSONObject("job"));
                        param.setSqlText(newData.toJSONString());
                    }
                }
            }
        }
    }

    /**
     * 填充密文密码信息
     */
    private void fillPassword(final Object newData, final Object oldData) {
        if (null == newData || null == oldData) {
            return;
        }
        if (newData instanceof JSONObject && oldData instanceof JSONObject) {
            final Set<Map.Entry<String, Object>> entrySet = ((JSONObject) newData).entrySet();
            for (final Map.Entry<String, Object> entry : entrySet) {
                final String key = entry.getKey();
                final Object value = entry.getValue();
                final Object oldValue = ((JSONObject) oldData).get(key);
                if (StringUtils.isBlank(key) || null == value || null == oldValue) {
                    continue;
                }
                if (DataFilter.PASSWORD_KEYS.contains(key.toLowerCase())
                        && "******".equals(value)) {
                    entry.setValue(oldValue);
                } else {

                    this.fillPassword(value, oldValue);
                }
            }
        } else if (newData instanceof JSONArray && oldData instanceof JSONArray) {
            final JSONArray newArr = (JSONArray) newData;
            final JSONArray oldArr = (JSONArray) oldData;
            for (int i = 0; i < newArr.size(); i++) {
                if (oldArr.size() > i) {
                    this.fillPassword(newArr.get(i), oldArr.get(i));
                }
            }
        }
    }

    /**
     * 任务保存之前的一些参数校验并返回engineType
     *
     * @param param
     * @return
     */
    private Integer checkBeforeUpdateTask(TaskResourceParam param) {
        Integer engineType = EngineType.Spark.getVal();
        if (EJobType.SPARK.getVal().equals(param.getTaskType()) ||
                EJobType.HADOOP_MR.getVal().equals(param.getTaskType())) {
            if (param.getId() <= 0 && checkResourceType(param.getResourceIdList())) {
                throw new RdosDefineException("MR 任务必须添加资源.", ErrorCode.INVALID_PARAMETERS);
            }
            if (EJobType.SPARK.getVal().equals(param.getTaskType())) {
                engineType = EngineType.Spark.getVal();
            } else {
                engineType = EngineType.Hadoop.getVal();
            }

            final JSONObject exeArgs = new JSONObject(1);
            exeArgs.put(CMD_OPTS, param.getOptions());
            param.setExeArgs(exeArgs.toJSONString());
        } else if (EJobType.SYNC.getVal().equals(param.getTaskType())) {
            engineType = operateSyncTask(param);
        } else if (EJobType.SPARK_PYTHON.getVal().equals(param.getTaskType()) ||
                EJobType.PYTHON.getVal().equals(param.getTaskType())
                || EJobType.SHELL.getVal().equals(param.getTaskType())) {
            engineType = operateShellOrPython(param);
        } else if (EJobType.CARBON_SQL.getVal().equals(param.getTaskType())) {
            if (param.getDataSourceId() == null) {
                throw new RdosDefineException("Carbon SQL任务必须关联数据源.", ErrorCode.INVALID_PARAMETERS);
            }
        } else if (EJobType.LIBRA_SQL.getVal().equals(param.getTaskType())) {
            engineType = EngineType.Libra.getVal();
        } else if (EJobType.HIVE_SQL.getVal().equals(param.getTaskType())) {
            //HiveSql任务匹配
            engineType = EngineType.HIVE.getVal();
        } else if (EJobType.IMPALA_SQL.getVal().equals(param.getTaskType())) {
            engineType = EngineType.IMPALA.getVal();
        } else if (EJobType.TIDB_SQL.getVal().equals(param.getTaskType())) {
            engineType = EngineType.TIDB.getVal();
        } else if (EJobType.ORACLE_SQL.getVal().equals(param.getTaskType())) {
            engineType = EngineType.ORACLE.getVal();
        } else if (EJobType.GREENPLUM_SQL.getVal().equals(param.getTaskType())) {
            engineType = EngineType.GREENPLUM.getVal();
        } else {
            if (CollectionUtils.isNotEmpty(param.getResourceIdList())) {
                throw new RdosDefineException("该任务不能添加资源.", ErrorCode.INVALID_PARAMETERS);
            }
        }

        return engineType;
    }

    /**
     * 处理数据同步任务
     * @param param
     * @return
     */
    private Integer operateSyncTask(TaskResourceParam param) {
        Integer engineType;
        Map<String, Object> sourceMap = param.getSourceMap();
        Map<String, Object> settingMap = param.getSettingMap();
        //下面代码 是为了 拿到断点续传在字段列表的第几位
        if (sourceMap != null && settingMap != null) {
            Object column = sourceMap.get("column");
            Integer restoreColumnIndex = 0;
            if (column != null) {
                JSONArray colums = JSONArray.parseArray(JSONObject.toJSONString(sourceMap.get("column")));
                for (int i = 0; i < colums.size(); i++) {
                    if (Objects.equals(colums.getJSONObject(i).getString("key"), settingMap.get("restoreColumnName"))) {
                        restoreColumnIndex = i;
                        break;
                    }
                }
            }
            settingMap.put("restoreColumnIndex", restoreColumnIndex);
            param.setSettingMap(settingMap);
        }
        engineType = EngineType.Flink.getVal();
        logger.info("addOrUpdateTask with createModel {}", param.getCreateModel());

        if (param.getIsEditBaseInfo()) {
            // 右键编辑 处理增量标识
            operateIncreCol(param);
        } else {
            JSONObject sql = new JSONObject();
            if (param.getCreateModel() == TaskCreateModelType.TEMPLATE.getType()) {
                sql.put("job", param.getSqlText());
                this.batchTaskParamService.checkParams(sql.toJSONString(), param.getTaskVariables());
            } else if ((param.isPreSave() || param.getId() == 0) && param.getCreateModel() == TaskCreateModelType.GUIDE.getType()) {
                if (param.getId() != 0) {
                    String sqlText = this.dataSourceService.getSyncSql(param, false);
                    sql = JSON.parseObject(sqlText);
                }
            }
            sql.put("createModel", param.getCreateModel());
            sql.put("syncModel", param.getSyncModel());
            param.setSqlText(sql.toJSONString());
        }
        if (param.getSqlText() != null) {
            this.checkIncreSyncTask(param);
            param.setSqlText(Base64Util.baseEncode(param.getSqlText()));
        }
        // 再做一次权限校验，因为表名可以填写
        if (param.getId() != 0 && !param.getIsEditBaseInfo()) {
            Long ownerUserId = param.getOwnerUserId();
            if (ownerUserId == null) {
                ownerUserId = param.getUserId();
            }
        }
        return engineType;
    }

    /**
     * 处理shell 、python任务 校验资源 设置默认值
     * @param param
     * @return
     */
    private Integer operateShellOrPython(TaskResourceParam param) {
        Integer engineType;
        if (param.getId() <= 0) {
            if (param.getOperateModel() == TaskOperateType.RESOURCE.getType() && !EJobType.SHELL.getVal().equals(param.getTaskType())) {
                if (checkResourceType(param.getResourceIdList())) {
                    throw new RdosDefineException("python 任务必须添加资源.", ErrorCode.INVALID_PARAMETERS);
                }
            } else if (param.getOperateModel() == TaskOperateType.EDIT.getType() && !EJobType.SHELL.getVal().equals(param.getTaskType())) {
                if (StringUtils.isBlank(param.getSqlText())) {
                    param.setSqlText("#coding=utf-8\n");
                }
            }
        }
        JSONObject exeArgs = new JSONObject();
        engineType = EngineType.Spark.getVal();
        exeArgs.put("operateModel", param.getOperateModel());
        exeArgs.put(CMD_OPTS, param.getOptions());
        if (EJobType.PYTHON.getVal().equals(param.getTaskType())
                || EJobType.SHELL.getVal().equals(param.getTaskType())){
            exeArgs.put("--app-name", param.getName());
            exeArgs.put("--input", param.getInput());
            exeArgs.put("--output", param.getOutput());
            exeArgs.put("--files", param.getResourceIdList());
            exeArgs.put("--python-version", param.getPythonVersion());

            String appType = EngineType.Shell.getEngineName() ;
            if (EJobType.SHELL.getVal().equals(param.getTaskType())) {
                exeArgs.put("operateModel", TaskOperateType.EDIT.getType());
                engineType = EngineType.DtScript.getVal();
            } else if (EJobType.PYTHON.getVal().equals(param.getTaskType())) {
                final EngineType pyEnginType = EngineType.getByPythonVersion(param.getPythonVersion());
                appType = pyEnginType.getEngineName();
                engineType = pyEnginType.getVal();
            }
            exeArgs.put("--app-type", appType);

            final String exeArgsParam = param.getExeArgs();
            if (StringUtils.isNotEmpty(exeArgsParam)) {
                final JSONObject paramObj = JSON.parseObject(exeArgsParam);
                paramObj.putAll(exeArgs);
                exeArgs = paramObj;
            }
        }
        param.setExeArgs(exeArgs.toJSONString());
        return engineType;
    }

    /**
     * 处理增量标识  主要处理两部分 1 处理增量标识字段  2.处理调度依赖
     * @param param
     */
    private void operateIncreCol(TaskResourceParam param) {
        final BatchTask task = this.batchTaskDao.getOne(param.getId());
        if (StringUtils.isNotEmpty(task.getSqlText())) {
            final JSONObject json = JSON.parseObject(Base64Util.baseDecode(task.getSqlText()));
            json.put("syncModel", param.getSyncModel());
            //处理增量标示
            operateIncreamColumn(json,param.getSyncModel());
            param.setSqlText(json.toJSONString());
        }

        JSONObject scheduleConf = JSON.parseObject(task.getScheduleConf());
        Integer selfReliance = scheduleConf.getInteger("selfReliance");
        if (param.getSyncModel() == SyncModel.HAS_INCRE_COL.getModel() &&
                !DependencyType.SELF_DEPENDENCY_SUCCESS.getType().equals(selfReliance)
                && !DependencyType.SELF_DEPENDENCY_END.getType().equals(selfReliance)) {
            scheduleConf.put("selfReliance", DependencyType.SELF_DEPENDENCY_END.getType());
            param.setScheduleConf(scheduleConf.toJSONString());
        }
    }

    /**
     * 向导模式转模版
     * @param param
     * @return
     * @throws Exception
     */
    @Transactional
    public TaskCatalogueVO guideToTemplate(final TaskResourceParam param) {
        final BatchTask task = this.batchTaskDao.getOne(param.getId());
        BatchTaskBatchVO taskVO = new BatchTaskBatchVO();
        taskVO.setId(param.getId());
        taskVO.setProjectId(param.getProjectId());
        taskVO.setName(task.getName());
        taskVO.setVersion(param.getVersion());
        taskVO.setUserId(param.getUserId());
        taskVO.setNodePid(task.getNodePid());
        taskVO.setReadWriteLockVO(param.getReadWriteLockVO());
        taskVO.setLockVersion(param.getLockVersion());
        final JSONObject sqlJson = JSON.parseObject(Base64Util.baseDecode(task.getSqlText()));
        sqlJson.put("createModel", TaskCreateModelType.TEMPLATE.getType());

        taskVO.setSqlText(Base64Util.baseEncode(sqlJson.toJSONString()));
        taskVO = this.updateTask(taskVO, true);
        final TaskCatalogueVO taskCatalogueVO = new TaskCatalogueVO(taskVO, taskVO.getNodePid());
        return taskCatalogueVO;
    }

    /**
     * 判断任务是否可以配置增量标识
     */
    public boolean canSetIncreConf(Long taskId) {
        final BatchTask task = this.getBatchTaskById(taskId);
        if (task == null) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }

        if (!EJobType.SYNC.getVal().equals(task.getTaskType())) {
            return false;
        }

        // 增量同步任务不能在工作流中运行
        if (task.getFlowId() != 0) {
            return false;
        }

        if (StringUtils.isEmpty(task.getSqlText())) {
            throw new RdosDefineException("同步任务未配置数据源");
        }

        try {
            final JSONObject json = JSON.parseObject(Base64Util.baseDecode(task.getSqlText()));
            this.checkSyncJobContent(json.getJSONObject("job"), false);
        } catch (final RdosDefineException e) {
            return false;
        }

        return true;
    }

    /**
     * 检查增量同步任务配置
     *
     * @param param
     */
    private void checkIncreSyncTask(final TaskResourceParam param) {
        final JSONObject taskJson = JSON.parseObject(param.getSqlText());
        if (!taskJson.containsKey("syncModel") || SyncModel.NO_INCRE_COL.getModel() == taskJson.getInteger("syncModel")) {
            return;
        }

        if (param.getFlowId() != 0) {
            throw new RdosDefineException("增量同步任务不能在工作流中运行", ErrorCode.INVALID_PARAMETERS);
        }

        this.checkSyncJobContent(taskJson.getJSONObject("job"), true);
    }

    public void checkSyncJobContent(final JSONObject jobJson, final boolean checkIncreCol) {
        if (jobJson == null) {
            return;
        }

        final String readerPlugin = JSONPath.eval(jobJson, "$.job.content[0].reader.name").toString();
        final String writerPlugin = JSONPath.eval(jobJson, "$.job.content[0].writer.name").toString();

        if (!PluginName.RDB_READER.contains(readerPlugin)) {
            throw new RdosDefineException("增量同步任务只支持从关系型数据库读取", ErrorCode.INVALID_PARAMETERS);
        }

        if (!PluginName.HDFS_W.equals(writerPlugin) && !PluginName.Hive_W.equals(writerPlugin)) {
            throw new RdosDefineException("增量同步任务只支持写入hive和hdfs", ErrorCode.INVALID_PARAMETERS);
        }

        if (!checkIncreCol) {
            return;
        }

        final String increColumn = (String) JSONPath.eval(jobJson, "$.job.content[0].reader.parameter.increColumn");
        if (StringUtils.isEmpty(increColumn)) {
            throw new RdosDefineException("增量同步任务必须配置增量字段", ErrorCode.INVALID_PARAMETERS);
        }
    }

    private String createAnnotationText(final ScheduleTaskVO task) {
        if (StringUtils.isNotBlank(task.getSqlText())) {
            return task.getSqlText();
        }
        final String ENTER = "\n";
        final String NOTE_SIGN;
        String type = EJobType.getEJobType(task.getTaskType()).getName();
        final StringBuilder sb = new StringBuilder();

        // 需要代码注释模版的任务类型
        Set<Integer> shouldNoteSqlTypes = Sets.newHashSet(EJobType.SPARK_SQL.getVal(), EJobType.CARBON_SQL.getVal(),
                EJobType.LIBRA_SQL.getVal(), EJobType.IMPALA_SQL.getVal());

        if (EJobType.PYTHON.getVal().equals(task.getTaskType())
                || EJobType.SHELL.getVal().equals(task.getTaskType())) {
            NOTE_SIGN = "#";
            if (EJobType.PYTHON.getVal().equals(task.getTaskType())) {
                type = "Python" + task.getPythonVersion();
            }
        } else if (shouldNoteSqlTypes.contains(task.getTaskType())) {
            NOTE_SIGN = "-- ";
        } else {
            sb.append(StringUtils.isBlank(task.getSqlText()) ? "" : task.getSqlText());
            return sb.toString();
        }
        //包括任务名称、任务类型、作者、创建时间、描述；
        sb.append(NOTE_SIGN).append("name ").append(task.getName()).append(ENTER);
        sb.append(NOTE_SIGN).append("type ").append(type).append(ENTER);
        sb.append(NOTE_SIGN).append("author ").append(userService.getUserName(task.getCreateUserId())).append(ENTER);
        final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sb.append(NOTE_SIGN).append("create time ").append(sdf.format(task.getGmtCreate())).append(ENTER);
        sb.append(NOTE_SIGN).append("desc ").append(StringUtils.isBlank(task.getTaskDesc()) ? "" : task.getTaskDesc().replace(ENTER, " ")).append(ENTER);
        sb.append(StringUtils.isBlank(task.getSqlText()) ? "" : task.getSqlText());

        return sb.toString();
    }

    /**
     * 查询资源是否存在
     * @param resourceIdList
     */
    private Boolean checkResourceType(List<Long> resourceIdList) {
        if (CollectionUtils.isEmpty(resourceIdList)) {
            return true;
        }
        List<BatchResource> resourceList = batchResourceService.getResourceList(resourceIdList);
        return CollectionUtils.isEmpty(resourceList);
    }

    /**
     * 解析子任务依赖关系
     *
     * @param sqlText
     * @return key-taskId, value-parentIdList
     */
    public Map<Long, List<Long>> parseTaskRelationsFromSqlText(final String sqlText) {
        if (StringUtils.isNotBlank(sqlText)) {
            final JSONArray array = JSON.parseArray(sqlText);
            final Map<Long, List<Long>> relations = Maps.newHashMap();
            for (int i = 0; i < array.size(); i++) {
                final JSONObject object = array.getJSONObject(i);
                final JSONObject source = object.getJSONObject("source");
                final JSONObject target = object.getJSONObject("target");
                if (source != null && target != null) {
                    final long parentId = source.getJSONObject("data").getLong("id");
                    final long targetId = target.getJSONObject("data").getLong("id");
                    if (relations.containsKey(targetId)) {
                        relations.get(targetId).add(parentId);
                    } else {
                        relations.put(targetId, Lists.newArrayList(parentId));
                    }
                } else if(object.getJSONObject("data") != null) {
                    if (!relations.containsKey(object.getJSONObject("data").getLong("id"))) {
                        relations.put(object.getJSONObject("data").getLong("id"), Lists.newArrayList());
                    }
                }
            }
            return relations;
        } else {
            throw new RdosDefineException("该工作流不存在子任务");
        }
    }

    public void updateSubTaskScheduleConf(final Long flowWorkId, final JSONObject newScheduleConf) {
        final BatchTask task = this.batchTaskDao.getOne(flowWorkId);
        if (task == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        final int periodType = newScheduleConf.getInteger("periodType");
        if (!Objects.equals(JSON.parseObject(task.getScheduleConf()), newScheduleConf)
                || periodType == ESchedulePeriodType.MIN.getVal() || periodType == ESchedulePeriodType.HOUR.getVal()) {
            final List<BatchTask> batchTasks = this.getFlowWorkSubTasks(flowWorkId);
            newScheduleConf.put("selfReliance", 0);
            //工作流更新调度属性时，子任务同步更新
            for (final BatchTask bTask : batchTasks) {
                //工作流配置的自动取消不同步子任务
                newScheduleConf.remove("isExpire");
                bTask.setScheduleConf(newScheduleConf.toString());
            }
            batchTasks.forEach(batchTask -> this.batchTaskDao.update(batchTask));
        }
    }

    /**
     * 新增/更新任务
     *
     * @param task
     * @param isEditBaseInfo 如果是右键编辑的情况则不更新任务参数
     * @return
     */
    @Transactional
    public BatchTaskBatchVO updateTask(final BatchTaskBatchVO task, final Boolean isEditBaseInfo) {

        if (task.getName() == null) {
            throw new RdosDefineException("任务名称不能为空.", ErrorCode.INVALID_PARAMETERS);
        }

        if (!PublicUtil.matcher(task.getName(), TASK_PATTERN)) {
            throw new RdosDefineException("名称只能由字母、数据、中文、下划线组成", ErrorCode.INVALID_PARAMETERS);
        }

        if (task.getSubmitStatus() == null) {
            task.setSubmitStatus(ESubmitStatus.UNSUBMIT.getStatus());
        }

        task.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        BatchTask batchTask = this.batchTaskDao.getByName(task.getName(), task.getProjectId());

        boolean isAdd = false;
        if (task.getId() > 0) {//update
            task.setSubmitStatus(null);
            BatchTask specialTask = this.getOne(task.getId());
            if (task.getTaskType() == null) {
                task.setTaskType(specialTask.getTaskType());
            }
            String oriTaskName = specialTask.getName();
            if (batchTask != null && !batchTask.getId().equals(task.getId())) {
                throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
            }

            BatchTask specialTask1 = new BatchTask();
            ReadWriteLockVO readWriteLockVO = this.readWriteLockService.dealWithLock(
                    task.getProjectId(),
                    task.getId(),
                    ReadWriteLockType.BATCH_TASK,
                    task.getUserId(),
                    this.getLockVersion(task),
                    task.getVersion(),
                    specialTask.getVersion(),
                    res -> {
                        task.setCreateUser(null);
                        task.setIsDeleted(Deleted.NORMAL.getStatus());
                        if (task.getVersion() == null) {
                            task.setVersion(0);
                        }
                        PublicUtil.copyPropertiesIgnoreNull(task, specialTask1);
                        final Integer updateResult = this.batchTaskDao.update(specialTask1);
                        if (updateResult == 1) {
                            task.setVersion(task.getVersion() + 1);
                        }
                        return updateResult;
                    }, true);

            task.setReadWriteLockVO(readWriteLockVO);
            //如果是工作流任务 更新父节点调度类型时，需要同样更新子节点
            if (EJobType.WORK_FLOW.getVal().equals(task.getTaskType()) && task.getFlowId() == 0 && StringUtils.isNotEmpty(task.getScheduleConf())){
                updateSonTaskPeriodType(task.getId(),task.getPeriodType(),task.getScheduleConf());
            }
            if (!oriTaskName.equals(task.getName())) {//修改名字需要同步到taskShade
                this.scheduleTaskShadeService.updateTaskName(task.getId(), task.getName(), AppType.RDOS.getType());
            }
            logger.info("success update batchTask, taskId:{}", task.getId());

        } else {
            if (batchTask != null) {
                throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
            }
            //初始化task的一些属性
            isAdd = initTaskInfo(task);
            BatchTask insertTask = new BatchTask();
            BeanUtils.copyProperties(task, insertTask);
            //如果是工作流获取父任务的锁 用来保证父任务一定会更新成功 这里有并发问题 如果同时对一个工作流添加子任务 会丢失
            if (task.getFlowId()>0){
                BatchTask parentTask = batchTaskDao.getOne(task.getFlowId());
                ReadWriteLock readWriteLock = readWriteLockDao.getByProjectIdAndRelationIdAndType(parentTask.getProjectId(), parentTask.getId(), ReadWriteLockType.BATCH_TASK.name());
                if (readWriteLock == null) {
                    throw new RdosDefineException("父任务锁不存在");
                }
                if (!readWriteLock.getVersion().equals(task.getParentReadWriteLockVersion())) {
                    throw new RdosDefineException("当前任务已被修改，请重新打开任务后再次提交");
                }
            }
            batchTaskDao.insert(insertTask);
            task.setTaskId(insertTask.getId());
            task.setId(insertTask.getId());
            BatchTaskRecord record = new BatchTaskRecord();
            record.setTaskId(insertTask.getId());
            record.setProjectId(task.getProjectId());
            record.setTenantId(task.getTenantId());
            record.setRecordType(TaskOperateType.CREATE.getType());
            record.setOperatorId(task.getUserId());
            record.setOperateTime(new Timestamp(System.currentTimeMillis()));
            batchTaskRecordService.saveTaskRecord(record);

            parseCreateTaskExeArgs(task);

            //新增锁
            ReadWriteLockVO readWriteLockVO = this.readWriteLockService.getLock(
                    task.getTenantId(),
                    task.getUserId(),
                    ReadWriteLockType.BATCH_TASK.name(),
                    task.getId(),
                    task.getProjectId(),
                    null, null);
            task.setReadWriteLockVO(readWriteLockVO);
            logger.info("success insert batchTask, taskId:{}", task.getId());
        }

        //fixme 右键编辑使用另一个接口
        if (BooleanUtils.isNotTrue(isEditBaseInfo)) {
            if (!EJobType.WORK_FLOW.getVal().equals(task.getTaskType())  &&
                    !EJobType.VIRTUAL.getVal().equals(task.getTaskType())) {
                //新增加不校验自定义参数
                if (!isAdd) {
                    this.batchTaskParamService.checkParams(task.getSqlText(), task.getTaskVariables());
                }
            }
            this.batchTaskParamService.addOrUpdateTaskParam(task);
        }

        final BatchTaskBatchVO batchTaskBatchVO = new BatchTaskBatchVO(task);
        batchTaskBatchVO.setReadWriteLockVO(task.getReadWriteLockVO());
        batchTaskBatchVO.setVersion(task.getVersion());
        return batchTaskBatchVO;
    }

    /**
     *初始化 task的一些基本属性
     *
     * @param task
     * @return
     */
    private boolean initTaskInfo(BatchTaskBatchVO task) {
        if (StringUtils.isBlank(task.getTaskDesc())) {
            task.setTaskDesc("");
        }

        if (StringUtils.isBlank(task.getMainClass())) {
            task.setMainClass("");
        }

        if (StringUtils.isBlank(task.getTaskParams())) {
            task.setTaskParams("");
        }

        if (StringUtils.isBlank(task.getScheduleConf())) {
            task.setScheduleConf(DEFAULT_SCHEDULE_CONF);
        } else {
            final JSONObject scheduleConf = JSON.parseObject(task.getScheduleConf());
            final String beginDate = scheduleConf.getString("beginDate");
            if (StringUtils.isBlank(beginDate) || "null".equalsIgnoreCase(beginDate)) {
                throw new RdosDefineException("生效日期起至时间不能为空");
            }
            final String endDate = scheduleConf.getString("endDate");
            if (StringUtils.isBlank(endDate) || "null".equalsIgnoreCase(endDate)) {
                throw new RdosDefineException("生效日期结束时间不能为空");
            }
        }

        if (task.getVersion() == null) {
            task.setVersion(0);
        }

        if (StringUtils.isBlank(task.getExeArgs())) {
            task.setExeArgs("");
        }

        if (task.getOwnerUserId() == null) {
            task.setOwnerUserId(task.getUserId());
        }
        if (task.getCreateUserId() == null) {
            task.setCreateUserId(task.getUserId());
        }
        task.setGmtCreate(task.getGmtModified());
        // 增加注释
        task.setSqlText(this.createAnnotationText(task));


        if (EJobType.CARBON_SQL.getVal().equals(task.getTaskType())) {
            task.setTaskParams(getDefaultTaskParam(task.getDtuicTenantId(), EngineType.Carbon.getVal(), task.getComputeType(), task.getTaskType()));
        } else {
            task.setTaskParams(getDefaultTaskParam(task.getDtuicTenantId(), task.getEngineType(), task.getComputeType(), task.getTaskType()));
        }
        task.setScheduleStatus(EScheduleStatus.NORMAL.getVal());
        task.setSubmitStatus(ESubmitStatus.UNSUBMIT.getStatus());
        task.setPeriodType(DEFAULT_SCHEDULE_PERIOD);
        String scConf = DEFAULT_SCHEDULE_CONF;
        int period = DEFAULT_SCHEDULE_PERIOD;
        if (task.getFlowId() != null && task.getFlowId() > 0) {
            final BatchTask flow = this.batchTaskDao.getOne(task.getFlowId());
            if (flow != null) {
                scConf = flow.getScheduleConf();
                final ScheduleCron scheduleCron;
                try {
                    scheduleCron = ScheduleFactory.parseFromJson(scConf);
                } catch (Exception e) {
                    throw new RdosDefineException(e.getMessage(), e);
                }
                period = scheduleCron.getPeriodType();
            }
            task.setScheduleConf(scConf);
        }
        task.setPeriodType(period);
        if(Objects.isNull(task.getFlowId())){
            task.setFlowId(0L);
        }
        return true;
    }

    /**
     * 解析新增加任务的exeArgs中包含 系统参数  和 自定义参数
     *
     * @param task
     */
    private void parseCreateTaskExeArgs(final ScheduleTaskVO task) {
        if (null != task && StringUtils.isNotBlank(task.getExeArgs())) {
            try {
                final JSONObject jsonObject = JSON.parseObject(task.getExeArgs());
                final String opts = jsonObject.getString("--cmd-opts");
                if (StringUtils.isNotBlank(opts)) {
                    final String[] opt = opts.split(" ");
                    List<Map> taskVariables = task.getTaskVariables();
                    if (Objects.isNull(taskVariables)) {
                        taskVariables = new ArrayList<>();
                    }
                    for (final String exe : opt) {
                        if (StringUtils.isNotBlank(exe)) {
                            final String command = exe.trim();
                            if (command.startsWith("${") && command.endsWith("}")) {
                                final String line = command.substring(2, command.indexOf("}")).trim();
                                final BatchSysParameter batchSysParamByName = this.batchSysParamService.getBatchSysParamByName(line);
                                boolean hasAdd = false;
                                final Map<String, String> variable = new HashMap<>(3);
                                variable.put("paramName", line);
                                if (Objects.nonNull(batchSysParamByName)) {
                                    for (final Map taskVariable : taskVariables) {
                                        if (batchSysParamByName.getParamName().equalsIgnoreCase((String) taskVariable.get("paramName"))) {
                                            hasAdd = true;
                                        }
                                    }
                                    if (!hasAdd) {
                                        //系统参数
                                        variable.put("paramCommand", batchSysParamByName.getParamCommand());
                                        variable.put("type", EParamType.SYS_TYPE.getType() + "");
                                        taskVariables.add(variable);
                                    }

                                } else {
                                    //自定义参数
                                    for (final Map taskVariable : taskVariables) {
                                        if (line.equalsIgnoreCase((String) taskVariable.get("paramName"))) {
                                            hasAdd = true;
                                        }
                                    }
                                    if (!hasAdd) {
                                        variable.put("paramCommand", "$[]");
                                        variable.put("type", EParamType.CUSTOMIZE_TYPE.getType() + "");
                                        taskVariables.add(variable);
                                    }
                                }
                            }
                        }
                    }
                    task.setTaskVariables(taskVariables);
                }
            } catch (final Exception e) {
                logger.error("parse exeArgs error {} ", task.getExeArgs(), e);
            }
        }
    }

    private Integer getLockVersion(final BatchTaskBatchVO task) {
        final Integer lockVersion;
        //仅更新名字时readWriteLock可能为空
        if (task.getReadWriteLockVO() == null) {
            final ReadWriteLock lock = this.readWriteLockService.getReadWriteLock(task.getProjectId(), task.getId(), ReadWriteLockType.BATCH_TASK.name());
            if (lock.getModifyUserId().equals(task.getUserId())) {
                lockVersion = lock.getVersion();
            } else {
                lockVersion = INIT_LOCK_VERSION;
            }
        } else {
            lockVersion = task.getReadWriteLockVO().getVersion();
        }
        return lockVersion;
    }


    /**
     * 强制任务重命名（不校验taskVersion、lockVersion）
     *
     * @param taskId
     * @param taskName
     * @param projectId
     */
    public void renameTask(Long taskId, String taskName, Long projectId) {
        final BatchTask task = this.batchTaskDao.getOne(taskId);
        if (task == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        if (!taskName.equals(task.getName())) {
            final BatchTask batchTask = this.batchTaskDao.getByName(taskName, projectId);
            if (batchTask != null) {
                throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
            }
            task.setName(taskName);
            final Integer update = this.batchTaskDao.update(task);
            if (update == 1) {
                this.scheduleTaskShadeService.updateTaskName(task.getId(), task.getName(), AppType.RDOS.getType());
            }
        }
    }

    /**
     * 向导模式下的数据同步需要json格式化
     *
     * @param taskVO
     * @param obj
     */
    private void formatSqlText(final ScheduleTaskVO taskVO, final JSONObject obj) {
        taskVO.setCreateModel(obj.get("createModel") == null ? Constant.CREATE_MODEL_GUIDE : Integer.parseInt(String.valueOf(obj.get("createModel"))));
        if (obj.get("job") != null && Constant.CREATE_MODEL_GUIDE == taskVO.getCreateModel()) {
            final Map<String, String> map;
            final String sqlText;
            try {
                map = (Map<String, String>) objectMapper.readValue(String.valueOf(obj.get("job")), Object.class);
                sqlText = JsonUtils.formatJSON(map);
            } catch (final IOException e) {
                logger.error("sqlText的json格式化失败{}" + e);
                throw new RdosDefineException("sqlText的json格式化失败");
            }
            taskVO.setSqlText(sqlText);
        } else if (obj.get("job") != null && Constant.CREATE_MODEL_TEMPLATE == taskVO.getCreateModel()) {
            taskVO.setSqlText(String.valueOf(obj.get("job")));
        } else {
            taskVO.setSqlText("");
        }

        if (obj.get("syncModel") != null) {
            taskVO.setSyncModel(obj.getInteger("syncModel"));
            if (taskVO.getSyncModel() == SyncModel.HAS_INCRE_COL.getModel()) {
                final Object increCol = JSONPath.eval(obj.getJSONObject("parser"), "$.sourceMap.increColumn");
                if (increCol != null) {
                    taskVO.setIncreColumn(increCol.toString());
                }
            }
        }
    }

    /**
     * 更新任务主资源
     *
     * @param taskResourceMap
     * @return
     */
    @Transactional
    public void updateTaskResource(final Map<String, Object> taskResourceMap) {

        Preconditions.checkState(taskResourceMap.containsKey("id"), "need param of id");
        Preconditions.checkState(taskResourceMap.containsKey("resources"), "need param of resources");
        Preconditions.checkState(taskResourceMap.containsKey("projectId"), "need param of projectId");
        Preconditions.checkState(taskResourceMap.containsKey("createUserId"), "need param of createUserId");

        final Long id = MathUtil.getLongVal(taskResourceMap.get("id"));
        final List<Object> oriResourceList = (List<Object>) taskResourceMap.get("resources");

        final BatchTask task = this.batchTaskDao.getOne(id);
        Preconditions.checkNotNull(task, "can not find task by id " + id);

        //删除旧的资源
        this.batchTaskResourceDao.deleteByTaskId(task.getId(), task.getProjectId(), ResourceRefType.MAIN_RES.getType());

        //添加新的资源
        if (CollectionUtils.isNotEmpty(oriResourceList)) {
            final List<Long> resourceIdList = Lists.newArrayList();
            oriResourceList.forEach(tmpId -> resourceIdList.add(MathUtil.getLongVal(tmpId)));
            this.batchTaskResourceService.save(task, resourceIdList, ResourceRefType.MAIN_RES.getType());
        }

    }

    /**
     * 更新任务引用资源
     *
     * @param taskResourceMap
     * @return
     */
    @Transactional
    public void updateTaskRefResource(final Map<String, Object> taskResourceMap) {

        Preconditions.checkState(taskResourceMap.containsKey("id"), "need param of id");
        Preconditions.checkState(taskResourceMap.containsKey("projectId"), "need param of projectId");
        Preconditions.checkState(taskResourceMap.containsKey("createUserId"), "need param of createUserId");

        final Long id = MathUtil.getLongVal(taskResourceMap.get("id"));
        final List<Object> refResourceList = (List<Object>) taskResourceMap.get("refResource");

        final BatchTask task = this.batchTaskDao.getOne(id);
        Preconditions.checkNotNull(task, "can not find task by id " + id);

        //删除旧的资源
        this.batchTaskResourceDao.deleteByTaskId(task.getId(), task.getProjectId(), ResourceRefType.DEPENDENCY_RES.getType());

        //添加新的关联资源
        if (CollectionUtils.isNotEmpty(refResourceList)) {
            final List<Long> refResourceIdList = Lists.newArrayList();
            refResourceList.forEach(tmpId -> refResourceIdList.add(MathUtil.getLongVal(tmpId)));
            this.batchTaskResourceService.save(task, refResourceIdList, ResourceRefType.DEPENDENCY_RES.getType());
        }
    }

    private String getDefaultTaskParam(Long dtuicTenantId, int engineType, final int computeType, final Integer taskType) {
        engineType = EJobType.CARBON_SQL.getVal().equals(taskType) ? EngineType.Carbon.getVal() : engineType;
        return doGetDefaultTaskParam(dtuicTenantId, engineType, computeType);
    }

    /**
     * 从参数默认表里面读取
     *
     * @author toutian
     */
    private String doGetDefaultTaskParam(Long dtuicTenantId, final int engineType, final int computeType) {
        final TaskTemplateResultVO engineParamTmplByComputeType = this.taskParamTemplateService.getEngineParamTmplByComputeType(engineType, computeType, 0);

        if (engineParamTmplByComputeType == null) {
            logger.error("systbatchJobem don't have init param of engineType {} of compute Type: {} ,data {}", engineType, computeType, JSON.toJSONString(engineParamTmplByComputeType));
            throw new RdosDefineException("system don't have init param of engineType: " + engineType + " of compute Type:" + computeType);
        }
        return engineParamTmplByComputeType.getParams();
    }

    /**
     * 数据开发-删除任务
     *
     * @param taskId    任务id
     * @param projectId 项目id
     * @param userId    用户id
     * @return
     * @author toutian
     */
    @Transactional
    public Long deleteTask(Long taskId, Long projectId, Long userId, Long tenantId, String sqlText) {

        final BatchTask batchTask = this.batchTaskDao.getOne(taskId);
        if (batchTask == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        // 判断该任务是否有子任务(调用engine接口) 工作流不需要判断
        if (batchTask.getFlowId() == 0) {
            List<NotDeleteTaskVO> notDeleteTaskVOS = getChildTasks(taskId);
            if (CollectionUtils.isNotEmpty(notDeleteTaskVOS)) {
                throw new RdosDefineException("(当前任务被其他任务依赖)", ErrorCode.CAN_NOT_DELETE_TASK);
            }
        }

        final ScheduleTaskShade dbTask = this.scheduleTaskShadeService.findTaskId(taskId, Deleted.NORMAL.getStatus(), AppType.RDOS.getType());
        if (batchTask.getFlowId() == 0 && Objects.nonNull(dbTask) &&
        batchTask.getScheduleStatus().intValue() == EScheduleStatus.NORMAL.getVal().intValue()){
            throw new RdosDefineException("(当前任务未被冻结)", ErrorCode.CAN_NOT_DELETE_TASK);
        }

        if (batchTask.getTaskType().intValue() == EJobType.WORK_FLOW.getVal() ||
                batchTask.getTaskType().intValue() == EJobType.ALGORITHM_LAB.getVal()) {
            final List<BatchTask> batchTasks = this.getFlowWorkSubTasks(taskId);
            //删除所有子任务相关
            batchTasks.forEach(task -> this.deleteTaskInfos(task.getId(), projectId, userId, tenantId));
        }

        //删除工作流中的子任务同时删除被依赖的关系
        if (batchTask.getFlowId() > 0) {
            this.batchTaskTaskService.deleteTaskTaskByParentId(batchTask.getId(), AppType.RDOS.getType());
        }

        if (StringUtils.isNotBlank(sqlText)) {
            final BatchTask batchTaskBean=new BatchTask();
            batchTaskBean.setId(batchTask.getFlowId());
            batchTaskBean.setSqlText(sqlText);
            this.batchTaskDao.updateSqlText(batchTaskBean);
            logger.info("sqlText 修改成功");
        } else {
            logger.error("deleteTask sqlText is null");
        }
        //删除任务
        this.deleteTaskInfos(taskId, projectId, userId, tenantId);

        return taskId;
    }

    public void deleteTaskInfos(Long taskId, Long projectId, Long userId, Long tenantId) {
        //软删除任务记录
        this.batchTaskDao.deleteById(taskId, Timestamp.valueOf(LocalDateTime.now()), projectId, userId);
        this.batchTaskRecordService.removeTaskRecords(taskId, projectId, userId);
        //删除任务的依赖关系
        this.batchTaskTaskService.deleteTaskTaskByTaskId(taskId);
        //删除关联的数据源资源
        this.dataSourceTaskRefService.removeRef(taskId);
        //删除关联的函数资源
        this.batchTaskResourceService.deleteTaskResource(taskId, projectId);
        this.batchTaskResourceShadeService.deleteByTaskId(taskId);
        //删除关联的参数表信息
        this.batchTaskParamService.deleteTaskParam(taskId);
        //删除发布相关的数据
        this.scheduleTaskShadeService.deleteTask(taskId, userId, AppType.RDOS.getType());

    }

    @Transactional(rollbackFor = Exception.class)
    public void frozenTask(List<Long> taskIdList, int scheduleStatus,
                           Long projectId, Long userId, Long tenantId,
                           Boolean isRoot) {

        final List<BatchTask> batchTasks = this.batchTaskDao.listByIds(taskIdList);
        if (CollectionUtils.isEmpty(batchTasks)) {
            return;
        }

        for (final BatchTask batchTask : batchTasks) {
            this.roleUserService.checkUserRole(userId, RoleValue.OPERATION.getRoleValue(), ErrorCode.PERMISSION_LIMIT.getDescription(),
                    batchTask.getProjectId(), batchTask.getTenantId(), isRoot);
        }

        final EScheduleStatus targetStatus = EScheduleStatus.getStatus(scheduleStatus);
        if (targetStatus == null) {
            throw new RdosDefineException("任务状态参数非法", ErrorCode.INVALID_PARAMETERS);
        }

        //查询子节点 同步冻结
        List<BatchTask> subList = new ArrayList<>();
        for (BatchTask task : batchTasks){
            if (EJobType.WORK_FLOW.getVal().equals(task.getTaskType())){
                List<BatchTask> flowWorkSubTasks = getFlowWorkSubTasks(task.getId());
                if (CollectionUtils.isNotEmpty(flowWorkSubTasks)){
                    subList.addAll(flowWorkSubTasks);
                }
            }
        }
        batchTasks.addAll(subList);
        taskIdList = batchTasks.stream().map(BaseEntity::getId).collect(Collectors.toList());
        //更新taskShade表
        this.scheduleTaskShadeService.frozenTask(taskIdList, scheduleStatus, AppType.RDOS.getType());
        //更新task表
        this.batchTaskDao.batchUpdateTaskScheduleStatus(taskIdList, scheduleStatus);
        final List<BatchTaskRecord> taskRecords = new ArrayList<>(taskIdList.size());
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        taskIdList.forEach(e -> {
            final BatchTaskRecord record = new BatchTaskRecord();
            record.setTenantId(tenantId);
            record.setProjectId(projectId);
            record.setOperateTime(now);
            record.setTaskId(e);
            record.setOperatorId(userId);
            record.setRecordType(scheduleStatus == 1 ? TaskOperateType.THAW.getType() : TaskOperateType.FROZEN.getType());
            taskRecords.add(record);
        });
        this.batchTaskRecordService.saveTaskRecords(taskRecords);
    }

    public BatchTask getBatchTaskById(final long taskId) {
        return this.batchTaskDao.getOne(taskId);
    }

    /**
     * 获取所有需要需要生成调度的task
     *
     * @return
     */
    public List<BatchTask> getAllTaskList() {
        return this.batchTaskDao.listAll();
    }

    public List<BatchTask> getTaskByIds(final List<Long> taskIdArray) {
        if (CollectionUtils.isEmpty(taskIdArray)) {
            return Collections.EMPTY_LIST;
        }

        return this.batchTaskDao.listByIds(taskIdArray);
    }


    /**
     * 判断任务是否可以发布
     * 当前只对sql任务做判断--不允许提交空的sql任务
     *
     * @return
     */
    private boolean checkTaskCanSubmit(final BatchTask task) {

        if ((task.getTaskType().equals(EJobType.SPARK_SQL.getVal()) || task.getTaskType().equals(EJobType.HIVE_SQL.getVal())) && StringUtils.isEmpty(task.getSqlText())) {
            throw new RdosDefineException(task.getName() + "任务的SQL为空", ErrorCode.TASK_CAN_NOT_SUBMIT);
        } else if (task.getTaskType().equals(EJobType.SYNC.getVal())) {
            if (StringUtils.isBlank(task.getSqlText())) {
                throw new RdosDefineException(task.getName() + "任务配置信息为空", ErrorCode.TASK_CAN_NOT_SUBMIT);
            }
            final String sqlText = Base64Util.baseDecode(task.getSqlText());
            final JSONObject jsonObject = JSON.parseObject(sqlText);
            if (jsonObject.containsKey("parser")) {
                final JSONObject parser = jsonObject.getJSONObject("parser");
                if (parser.containsKey("targetMap")) {
                    this.checkDataSource(parser.getJSONObject("targetMap").getLong("sourceId"));
                }

                if (parser.containsKey("sourceMap")) {
                    final JSONObject sourceMap = parser.getJSONObject("sourceMap");
                    if (sourceMap.containsKey("sourceList")) {
                        final JSONArray sourceList = sourceMap.getJSONArray("sourceList");
                        for (final Object o : sourceList) {
                            final JSONObject source = (JSONObject) o;
                            this.checkDataSource(source.getLong("sourceId"));
                        }
                    } else {
                        this.checkDataSource(parser.getJSONObject("sourceMap").getLong("sourceId"));
                    }
                }
            }
        }

        return true;
    }

    private void checkDataSource(Long sourceId) {
        BatchDataSource dataSource = dataSourceService.getOne(sourceId);
        Map<String, Object> kerberosConfig = dataSourceService.fillKerberosConfig(sourceId);
        IClient iClient = ClientCache.getClient(dataSource.getType());
        JSONObject jsonObject = JSONObject.parseObject(dataSource.getDataJson());
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(jsonObject, dataSource.getType(), kerberosConfig);
        Boolean connStatus = iClient.testCon(sourceDTO);
        if (!connStatus) {
            throw new RdosDefineException("数据源:" + dataSource.getDataName() + "获取连接失败，不能正常发布任务:" );
        }
    }

    /**
     * 根据支持的引擎类型返回
     *
     * @return
     */
    public List<BatchTaskGetSupportJobTypesResultVO> getSupportJobTypes(Long dtuicTenantId, Long projectId) {

        List<BatchTaskGetSupportJobTypesResultVO> resultSupportTypes = Lists.newArrayList();
        final List<EJobType> engineInfos = this.multiEngineService.getTenantSupportJobType(dtuicTenantId, projectId);
        engineInfos.forEach(engineInfo -> resultSupportTypes.add(new BatchTaskGetSupportJobTypesResultVO(engineInfo.getVal(), engineInfo.getName())));

        return resultSupportTypes;
    }

    public TaskCatalogueVO forceUpdate(final TaskResourceParam param) {
        return addOrUpdateTask(param);
    }

    /**
     * 数据开发-获取所有系统参数
     */
    public Collection<BatchSysParameter> getSysParams() {
        return this.batchSysParamService.listSystemParam();
    }


    /**
     * 新增离线任务/脚本/资源/自定义脚本，校验名称
     *
     * @param name
     * @param type
     * @param pid
     * @param isFile
     * @param projectId
     */
    public void checkName(String name, String type, Integer pid, Integer isFile, Long projectId) {
        if (StringUtils.isNotEmpty(name)) {
            if (!isFile.equals(IS_FILE)) {
                final BatchCatalogue batchCatalogue = this.batchCatalogueDao.getByPidAndName(projectId, pid, name);
                if (batchCatalogue != null) {
                    throw new RdosDefineException("文件夹已存在", ErrorCode.NAME_ALREADY_EXIST);
                }
            } else {
                final Object obj;
                if (type.equals(CatalogueType.TASK_DEVELOP.name())) {
                    obj = this.batchTaskDao.getByName(name, projectId);
                } else if (type.equals(CatalogueType.RESOURCE_MANAGER.name())) {
                    obj = this.batchResourceDao.listByNameAndProjectId(projectId, name, Deleted.NORMAL.getStatus());
                } else if (type.equals(CatalogueType.CUSTOM_FUNCTION.name())) {
                    obj = this.batchFunctionDao.listByNameAndProjectId(projectId, name, FuncType.CUSTOM.getType());
                } else if (type.equals(CatalogueType.PROCEDURE_FUNCTION.name())) {
                    obj = this.batchFunctionDao.listByNameAndProjectId(projectId, name, FuncType.PROCEDURE.getType());
                } else if (type.equals(CatalogueType.GREENPLUM_CUSTOM_FUNCTION.name())) {
                    obj = this.batchFunctionDao.listByNameAndProjectId(projectId, name, FuncType.CUSTOM.getType());
                } else if (type.equals(CatalogueType.SYSTEM_FUNCTION.name())) {
                    throw new RdosDefineException("不能添加系统函数");
                } else {
                    throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
                }

                if (obj instanceof BatchTask) {
                    if (obj != null) {
                        throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
                    }
                } else if (obj instanceof List) {
                    if (CollectionUtils.isNotEmpty((List) obj)) {
                        throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
                    }
                }
            }
        }
    }

    /**
     * 统计项目内已发布/总任务数
     *
     * @param projectId
     * @param tenantId
     * @return
     */
    public Map<String, Integer> countTask(final Long projectId, final Long tenantId) {
        final Integer allCount = this.batchTaskDao.countByProjectIdAndSubmit(null, projectId, tenantId);
        final Integer submitCount = this.batchTaskDao.countByProjectIdAndSubmit(IS_SUBMIT, projectId, tenantId);
        final BatchTaskDTO batchTaskDTO = new BatchTaskDTO();
        batchTaskDTO.setTenantId(tenantId);
        batchTaskDTO.setProjectId(projectId);
        batchTaskDTO.setSubmitStatus(ESubmitStatus.SUBMIT.getStatus());
        batchTaskDTO.setFlowId(0L);
        final Map<String, Integer> result = new HashMap<>();
        result.put("allCount", allCount);
        result.put("submitCount", submitCount);
        return result;
    }

    public void setOwnerUser(Long ownerUserId, Long taskId, Long userId, Long tenantId, Long projectId,
                             Boolean isRoot) {
        final User ownerUser = userService.getById(ownerUserId);
        if (ownerUser == null) {
            throw new RdosDefineException(ErrorCode.GET_USER_ERROR);
        }
        if (!roleUserService.isAdmin(userId, projectId, isRoot)) {
            throw new RdosDefineException("修改负责人需要管理员权限", ErrorCode.PERMISSION_LIMIT);
        }

        final BatchTask batchTask = this.batchTaskDao.getOne(taskId);
        if (batchTask == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }

        final Long oldOwnUserId = batchTask.getOwnerUserId();
        batchTask.setOwnerUserId(ownerUserId);
        this.batchTaskDao.update(batchTask);
    }

    /**
     * 获取任务流下的所有子任务
     *
     * @param taskId
     * @return
     */
    public List<BatchTask> getFlowWorkSubTasks(final Long taskId) {
        final BatchTaskDTO batchTaskDTO = new BatchTaskDTO();
        batchTaskDTO.setIsDeleted(Deleted.NORMAL.getStatus());
        batchTaskDTO.setFlowId(taskId);
        final PageQuery<BatchTaskDTO> pageQuery = new PageQuery<>(batchTaskDTO);
        final List<BatchTask> batchTasks = this.batchTaskDao.generalQuery(pageQuery);
        return batchTasks;
    }

    public List<BatchTask> getFlowWorkSubTasksWithoutSql(final Long taskId) {
        final BatchTaskDTO batchTaskDTO = new BatchTaskDTO();
        batchTaskDTO.setIsDeleted(Deleted.NORMAL.getStatus());
        batchTaskDTO.setFlowId(taskId);
        final PageQuery<BatchTaskDTO> pageQuery = new PageQuery<>(batchTaskDTO);
        final List<BatchTask> batchTasks = this.batchTaskDao.generalQueryWithoutSql(pageQuery);
        return batchTasks;
    }

    public List<TaskOwnerAndProjectPO>  getTaskOwnerAndProjectId(){
        List<TaskOwnerAndProjectPO> taskOwnerAndProjectId = batchTaskDao.getTaskOwnerAndProjectId();
        return taskOwnerAndProjectId;
    }

    public boolean capableOfCreate(Integer max) {
        if (max == -1) {
            return true;
        }

        return this.batchTaskDao.countAll() < max;
    }

    public BatchTask getByName(String name, Long projectId) {
        return this.batchTaskDao.getByName(name, projectId);
    }

    private Integer updateSubmitStatus(final Long projectId, final Long taskId, final Integer submitStatus) {
        return this.batchTaskDao.updateSubmitStatus(projectId, taskId, submitStatus, Timestamp.valueOf(LocalDateTime.now()));
    }

    public BatchTask getOne(final Long taskId) {
        final BatchTask one = this.batchTaskDao.getOne(taskId);
        if (Objects.isNull(one)) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        return one;
    }

    public Integer maxTaskVersion(final Long taskId) {
        final Integer maxVersionId = this.batchTaskVersionDao.getMaxVersionId(taskId);
        if (maxVersionId == null) {
            logger.error("maxVersion cannot be null, taskId={}", taskId) ;
            throw new RdosDefineException("任务无提交记录");
        }
        return maxVersionId;
    }

    /**
     *根据父任务Id  更新调度类型  调度配置
     */
    private void updateSonTaskPeriodType(Long flowId,Integer periodType,String scheduleConf){
        JSONObject scheduleJson = JSON.parseObject(scheduleConf);
        scheduleJson.put("selfReliance", 0);
        //工作流配置的自动取消不同步子任务
        scheduleJson.remove("isExpire");
        //为什么不toJsonString 是为了兼容历史数据
        batchTaskDao.updateScheduleConf(flowId,periodType,scheduleJson.toString());

    }

    /**
     * 操作增量标示  根据选择 删除或新增
     * @param sqlText  任务的内容
     * @param isIncream
     */
    private void operateIncreamColumn(JSONObject sqlText , Integer isIncream) {
        if (sqlText.containsKey("job")) {
            //获取前端展示的任务内容 忽略额外信息
            JSONObject taskText = sqlText.getJSONObject("job");
            //获取嵌套内容
            JSONObject jobInfo = taskText.getJSONObject("job");
            JSONObject readerParameter = jobInfo.getJSONArray("content").getJSONObject(0).getJSONObject("reader").getJSONObject("parameter");
            if (SyncModel.NO_INCRE_COL.getModel() == isIncream) {
                if (readerParameter.containsKey("increColumn")) {
                    readerParameter.remove("increColumn");
                }
            }
            taskText.put("job", jobInfo);
            sqlText.put("job", taskText);
        }
    }

    /**
     * 获取用户在此项目下的某任务是否还有责任人
     *
     * @param targetUserId
     * @param projectId
     * @return
     */
    public Integer generalCount(Long projectId, Long targetUserId){
        BatchTaskDTO batchTask = new BatchTaskDTO();
        batchTask.setProjectId(projectId);
        batchTask.setOwnerUserId(targetUserId);
        batchTask.setIsDeleted(Deleted.NORMAL.getStatus());
        return batchTaskDao.generalCount(batchTask);
    }

    /**
     * 将此用户创建的任务修改为项目负责人
     *
     * @param oldOwnerUserId
     * @param newOwnerUserId
     * @param projectId
     */
    public void updateTaskOwnerUser(Long oldOwnerUserId, Long newOwnerUserId, Long projectId) {
        batchTaskDao.updateTaskOwnerUser(oldOwnerUserId, newOwnerUserId, projectId);
    }

    /**
     * 查找所有产品提交的任务
     * @param searchVO
     * @return
     */
    public List<ScheduleTaskShadeTypeVO> allProductGlobalSearch(AllProductGlobalSearchVO searchVO) {
        List<ScheduleTaskShadeTypeVO> apiResponse = scheduleTaskShadeService.findFuzzyTaskNameByCondition(
                searchVO.getTaskName(), searchVO.getAppType(), searchVO.getUicTenantId(), searchVO.getProjectId());
        return apiResponse;
    }

    /**
     * 根据dependencyTaskIds解析依赖的任务
     * @param dependencyTaskIds
     * @return
     */
    private List<Map<String, Object>> getDependencyTasks(String dependencyTaskIds) {
        try {
            return JSON.parseObject(dependencyTaskIds, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return Arrays.stream(dependencyTaskIds
                    .split(","))
                    .map(taskId -> {
                        Map<String, Object> map = Maps.newHashMap();
                        map.put("parentAppType", AppType.RDOS.getType());
                        map.put("parentTaskId", taskId);
                        return map;
                    }).collect(Collectors.toList());
        }
    }

    /**
     * 获取当前任务的下游任务
     * @param taskId
     * @return
     */
    public List<NotDeleteTaskVO> getChildTasks(Long taskId) {
        List<NotDeleteTaskVO> notDeleteTaskVOs = scheduleTaskShadeService.getNotDeleteTask(taskId, AppType.RDOS.getType());
        if (CollectionUtils.isEmpty(notDeleteTaskVOs)) {
            return Lists.newArrayList();
        }
        return notDeleteTaskVOs;
    }

    /**
     * 自定义调度周期接下来10个调度日期
     * @param startDate
     * @param endDate
     * @param cron
     * @param num
     * @return
     */
    public BatchTaskRecentlyRunTimeResultVO recentlyRunTime(String startDate, String endDate, String cron, Integer num) {
        CronExceptionVO cronExceptionVO = scheduleTaskShadeService.checkCronExpression(cron, MIN_PERIOD);
        BatchTaskRecentlyRunTimeResultVO recentlyRunTimeResultVO = new BatchTaskRecentlyRunTimeResultVO();
        if (cronExceptionVO != null && StringUtils.isNotEmpty(cronExceptionVO.getErrMessage()) && cronExceptionVO.getErrCode() == 1) {
            recentlyRunTimeResultVO.setIsCronValid(false);
            return recentlyRunTimeResultVO;
        }
        recentlyRunTimeResultVO.setIsCronValid(true);
        List<String> recentlyRunTimes = scheduleTaskShadeService.recentlyRunTime(startDate, endDate, cron, num);
        recentlyRunTimeResultVO.setRecentlyRunTimes(recentlyRunTimes);
        return recentlyRunTimeResultVO;
    }

    /**
     * 自定义调度周期，判断是否合法，并且调度间隔不能小于5分钟
     * @param scheduleConf
     */
    private void checkCronValid(JSONObject scheduleConf) {
        Integer periodType = scheduleConf.getInteger("periodType");
        if (periodType == ESchedulePeriodType.CRON.getVal()) {
            String cron = scheduleConf.getString("cron");
            CronExceptionVO cronExceptionVO = scheduleTaskShadeService.checkCronExpression(cron, MIN_PERIOD);
            if (cronExceptionVO != null && StringUtils.isNotEmpty(cronExceptionVO.getErrMessage())) {
                if (cronExceptionVO.getErrCode() == 1) {
                    throw new RdosDefineException("请填写合法的Cron表达式！");
                } else if (cronExceptionVO.getErrCode() == 2) {
                    throw new RdosDefineException("调度周期间隔不可小于5分钟！");
                } else {
                    throw new RdosDefineException(cronExceptionVO.getErrMessage());
                }
            }
        }
    }

    /**
     * 获取组件版本
     * @param dtuicTenantId
     * @param taskType
     * @return
     */
    public List<BatchTaskGetComponentVersionResultVO> getComponentVersionByTaskType(Long dtuicTenantId, Integer taskType) {
        String engineType = "";
        if (EJobType.SPARK_SQL.getVal().equals(taskType) || EJobType.SPARK.getVal().equals(taskType) || EJobType.SPARK_PYTHON.getVal().equals(taskType)) {
            engineType = EngineType.Spark.getEngineName();
        } else {
            throw new RdosDefineException("这种任务类型不支持多版本选择！");
        }
        List<Component> components = componentService.getComponentVersionByEngineType(dtuicTenantId, engineType);
        List<BatchTaskGetComponentVersionResultVO> componentVersionResultVOS = Lists.newArrayList();
        for (Component component : components) {
            BatchTaskGetComponentVersionResultVO resultVO = new BatchTaskGetComponentVersionResultVO();
            resultVO.setComponentVersion(component.getHadoopVersion());
            resultVO.setIsDefault(component.getIsDefault());
            componentVersionResultVOS.add(resultVO);
        }
        componentVersionResultVOS.sort(sortComponentVersion());
        return componentVersionResultVOS;
    }

    /**
     * 版本号排序，按照版本号新旧排序
     * @return
     */
    private Comparator<BatchTaskGetComponentVersionResultVO> sortComponentVersion() {
        return (o1, o2) -> {
            String[] version1 = o1.getComponentVersion().split("\\.");
            String[] version2 = o2.getComponentVersion().split("\\.");
            if (version1.length > 0 && version2.length > 0) {
                for (int i = 0; i < version1.length; i++) {
                    try {
                        if (Integer.parseInt(version1[i]) > Integer.parseInt(version2[i])) {
                            return -1;
                        } else if (Integer.parseInt(version1[i]) < Integer.parseInt(version2[i])) {
                            return 1;
                        } else {
                            continue;
                        }
                    } catch (Exception e) {
                        logger.info("hadoop版本号：{}, {}", o1, o2);
                    }
                }
            }
            return o2.getComponentVersion().compareTo(o1.getComponentVersion());
        };
    }

    /**
     * 传入一个任务集合 返回已经排序好的任务
     * 流程如下：
     * 1.因为工作流的特殊性 所以要把工作流和普通任务分开
     * 2.非工作流的任务 进行排序  工作流子任务进行排序
     *
     * @param taskShades
     * @return 已经排序好的任务
     */
    public List<ScheduleTaskShade> sortTaskShade(List<ScheduleTaskShade> taskShades){
        List<ScheduleTaskShade> sortResultList = new ArrayList<>();
        //非工作流任务进行排序 工作流子任务进行排序 然后把工作流子任务加在后面
        List<ScheduleTaskShade> subTaskShade = taskShades.stream().filter(bean -> bean.getFlowId() > 0).collect(Collectors.toList());
        List<ScheduleTaskShade> notSubTaskShade = taskShades.stream().filter(bean -> bean.getFlowId() == 0).collect(Collectors.toList());

        //因为虚节点没有上游 工作流子节点不能被依赖 所以不用特殊考虑
        notSubTaskShade = scheduleTaskSort(notSubTaskShade);
        if (CollectionUtils.isNotEmpty(subTaskShade)) {
            subTaskShade = scheduleTaskSort(subTaskShade);
            //工作流子节点要紧跟父节点 用于工作流提前导入
            Map<Long, List<ScheduleTaskShade>> flowIdAndMap = subTaskShade.stream().collect(Collectors.groupingBy(ScheduleTask::getFlowId));
            //遍历不含工作流子节点的任务 把工作流子节点插入工作流的下方
            for (ScheduleTaskShade shade : notSubTaskShade) {
                sortResultList.add(shade);
                if (EJobType.WORK_FLOW.getType().equals(shade.getTaskType())) {
                    sortResultList.addAll((List<ScheduleTaskShade>) MapUtils.getObject(flowIdAndMap, shade.getTaskId(), Lists.newArrayList()));
                }
            }
        } else {
            return notSubTaskShade;
        }
        return sortResultList;
    }

    /**
     * 按照依赖关系对task任务进行排序
     *
     * @param subTaskShade  待排序的集合
     * @return
     */
    public List<ScheduleTaskShade> scheduleTaskSort(List<ScheduleTaskShade> subTaskShade) {
        List<ScheduleTaskShade> sortList = new ArrayList<>();
        List<Long> taskIdSet = subTaskShade.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList());
        Map<Long, ScheduleTaskShade> idTaskMap = subTaskShade.stream().collect(Collectors.toMap(v1 -> v1.getTaskId(), v2 -> v2, (v1, v2) -> v2));
        List<Long> sortTaskId = sortTaskId(taskIdSet);
        sortTaskId.forEach(taskId -> {
            sortList.add(idTaskMap.get(taskId));
        });
        return sortList;
    }

    /**
     * 传入taskId集合返回 根据依赖关系排序完成的集合
     * 此处不用深度遍历的原因是怕深度遍历一直在循环查询
     * 其实深度遍历还可以用记录发记录下当前已经获取过父节点的ID，这样逻辑就和现在一致了，广度和深度的
     *
     * @param taskIds
     * @return
     */
    public List<Long> sortTaskId(List<Long> taskIds){
        HashMap<Long, Integer> taskIndexRel = new HashMap<>();
        for (int i = 0; i < taskIds.size(); i++) {
            taskIndexRel.put(taskIds.get(i), i);
        }

        // 当前节点的IDSet，用set主要是为了优化速率
        Set<Long> taskIdsSet = taskIndexRel.keySet();
        // 获取对应节点父节点的ID属性
        HashMap<Long, Set<Long>> parentTaskRel = new HashMap<>();
        for (Long taskId : taskIdsSet) {
            Set<Long> parentTaskIds = getParentTaskIds(taskId, taskIdsSet);
            if (parentTaskIds.isEmpty()) {
                continue;
            }
            parentTaskRel.put(taskId, parentTaskIds);
        }

        // 循环遍历，添加无父节点的属性
        List<Long> resultTaskIds = new ArrayList<>();

        while (true) {
            // 先判断parentTaskRel是否为空，为空这说明处理完成
            if (parentTaskRel.isEmpty()) {
                resultTaskIds.addAll(taskIdsSet);
                return resultTaskIds;
            }

            Set<Long> parentTaskRelTaskIds = parentTaskRel.keySet();
            // 判断是否是根节点，只是判断 taskIdsSet - parentTotalTaskIds 是否存在，即是否满足
            taskIdsSet.removeAll(parentTaskRelTaskIds);
            if (taskIdsSet.isEmpty()) {
                throw new RdosDefineException("检测任务属性，任务存在成环现象");
            }

            // 将无父节点的ID添加到节点中
            resultTaskIds.addAll(taskIdsSet);

            Set<Long> tmpTaskIdsSet = new HashSet<>();
            // 处理父节点属性，去掉当前加入的taskIdsSet
            for (Map.Entry<Long, Set<Long>> entry : parentTaskRel.entrySet()) {
                entry.getValue().removeAll(taskIdsSet);
                // 如果父节点去除掉当前已经添加的数组，为空，则说明当前节点为根节点
                if (entry.getValue().isEmpty()) {
                    tmpTaskIdsSet.add(entry.getKey());
                    continue;
                }
            }

            // 将parentTaskRel中空节点值去除，并将taskIdsSet赋值为当前的tmpTaskIdsSet -> 即这些节点为下一次要添加的节点
            tmpTaskIdsSet.forEach(tmpTaskId -> parentTaskRel.remove(tmpTaskId));
            taskIdsSet = tmpTaskIdsSet;
        }
    }

    /**
     * 获取 taskId 的父节点ID
     *
     * @param taskId
     * @param currentTaskIds
     * @return
     */
    private Set<Long> getParentTaskIds(Long taskId, Set<Long> currentTaskIds) {
        List<ScheduleTaskTaskShade> allParentTasks = scheduleTaskTaskShadeService.getAllParentTask(taskId, AppType.RDOS.getType());
        Set<Long> resultParentTaskIds = allParentTasks.stream().map(ScheduleTaskTaskShade::getParentTaskId).collect(Collectors.toSet());

        // 取并集
        resultParentTaskIds.retainAll(currentTaskIds);
        return resultParentTaskIds;
    }
}
