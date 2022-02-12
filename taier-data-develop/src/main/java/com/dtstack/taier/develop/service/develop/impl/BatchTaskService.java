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

package com.dtstack.taier.develop.service.develop.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.TypeReference;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.client.IKerberos;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.constant.PatternConstant;
import com.dtstack.taier.common.enums.CatalogueType;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.DependencyType;
import com.dtstack.taier.common.enums.DictType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EDeployType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.EScheduleStatus;
import com.dtstack.taier.common.enums.ESubmitStatus;
import com.dtstack.taier.common.enums.FuncType;
import com.dtstack.taier.common.enums.PublishTaskStatusEnum;
import com.dtstack.taier.common.enums.ReadWriteLockType;
import com.dtstack.taier.common.enums.ResourceRefType;
import com.dtstack.taier.common.enums.TaskLockStatus;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.kerberos.KerberosConfigVerify;
import com.dtstack.taier.common.util.Base64Util;
import com.dtstack.taier.common.util.DataFilter;
import com.dtstack.taier.common.util.JsonUtils;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.common.util.Strings;
import com.dtstack.taier.dao.domain.BatchCatalogue;
import com.dtstack.taier.dao.domain.BatchDataSource;
import com.dtstack.taier.dao.domain.BatchReadWriteLock;
import com.dtstack.taier.dao.domain.BatchResource;
import com.dtstack.taier.dao.domain.BatchSysParameter;
import com.dtstack.taier.dao.domain.BatchTask;
import com.dtstack.taier.dao.domain.BatchTaskParam;
import com.dtstack.taier.dao.domain.BatchTaskResource;
import com.dtstack.taier.dao.domain.BatchTaskTask;
import com.dtstack.taier.dao.domain.BatchTaskVersion;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.dao.domain.ScheduleDict;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.domain.TaskParamTemplate;
import com.dtstack.taier.dao.domain.Tenant;
import com.dtstack.taier.dao.domain.User;
import com.dtstack.taier.dao.dto.BatchTaskDTO;
import com.dtstack.taier.dao.dto.BatchTaskVersionDetailDTO;
import com.dtstack.taier.dao.dto.UserDTO;
import com.dtstack.taier.dao.mapper.BatchReadWriteLockDao;
import com.dtstack.taier.dao.mapper.BatchTaskDao;
import com.dtstack.taier.dao.pager.PageQuery;
import com.dtstack.taier.dao.pager.Sort;
import com.dtstack.taier.develop.dto.devlop.BatchTaskBatchVO;
import com.dtstack.taier.develop.dto.devlop.CheckSyntaxResult;
import com.dtstack.taier.develop.dto.devlop.ReadWriteLockVO;
import com.dtstack.taier.develop.dto.devlop.TaskCatalogueVO;
import com.dtstack.taier.develop.dto.devlop.TaskCheckResultVO;
import com.dtstack.taier.develop.dto.devlop.TaskGetNotDeleteVO;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.enums.develop.SourceDTOType;
import com.dtstack.taier.develop.enums.develop.SyncModel;
import com.dtstack.taier.develop.enums.develop.TaskCreateModelType;
import com.dtstack.taier.develop.enums.develop.TaskOperateType;
import com.dtstack.taier.develop.parser.ESchedulePeriodType;
import com.dtstack.taier.develop.parser.ScheduleCron;
import com.dtstack.taier.develop.parser.ScheduleFactory;
import com.dtstack.taier.develop.service.console.TenantService;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.schedule.TaskService;
import com.dtstack.taier.develop.service.task.TaskParamTemplateService;
import com.dtstack.taier.develop.service.user.UserService;
import com.dtstack.taier.develop.utils.develop.common.enums.Constant;
import com.dtstack.taier.develop.utils.develop.sync.job.PluginName;
import com.dtstack.taier.develop.utils.develop.sync.job.SyncJobCheck;
import com.dtstack.taier.develop.web.develop.query.AllProductGlobalSearchVO;
import com.dtstack.taier.develop.web.develop.result.BatchAllProductGlobalReturnVO;
import com.dtstack.taier.develop.web.develop.result.BatchTaskGetComponentVersionResultVO;
import com.dtstack.taier.pluginapi.util.MathUtil;
import com.dtstack.taier.scheduler.dto.schedule.SavaTaskDTO;
import com.dtstack.taier.scheduler.dto.schedule.ScheduleTaskShadeDTO;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.ComponentService;
import com.dtstack.taier.scheduler.service.ScheduleDictService;
import com.dtstack.taier.scheduler.vo.ScheduleTaskVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
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
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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

    public static Logger LOGGER = LoggerFactory.getLogger(BatchTaskService.class);

    @Resource(name = "batchJobParamReplace")
    private JobParamReplace jobParamReplace;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private BatchTaskParamShadeService batchTaskParamShadeService;

    @Autowired
    private BatchTaskDao batchTaskDao;

    @Autowired
    private TaskParamTemplateService taskParamTemplateService;

    @Autowired
    private BatchTaskResourceService batchTaskResourceService;

    @Autowired
    private BatchTaskParamService batchTaskParamService;

    @Autowired
    private BatchTaskTaskService batchTaskTaskService;

    @Autowired
    private DatasourceService dataSourceService;

    @Autowired
    private BatchCatalogueService batchCatalogueService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BatchTaskVersionService batchTaskVersionService;

    @Autowired
    private BatchSysParamService batchSysParamService;

    @Autowired
    private BatchResourceService batchResourceService;

    @Autowired
    private BatchFunctionService batchFunctionService;

    @Autowired
    private ScheduleDictService dictService;

    @Autowired
    private BatchSqlExeService batchSqlExeService;

    @Autowired
    private BatchTaskResourceShadeService batchTaskResourceShadeService;

    @Autowired
    private BatchJobService batchJobService;

    @Autowired
    private BatchReadWriteLockDao readWriteLockDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ComponentService componentService;

    private static final String KEY = "key";

    private static final String TYPE = "type";

    private static final String COLUMN = "column";

    private static final String KERBEROS_CONFIG = "kerberosConfig";

    @Autowired
    private ClusterService clusterService;

    /**
     * kerberos认证文件在 ftp上的相对路径
     */
    private static final String KERBEROS_DIR = "kerberosDir";

    /**
     * Kerberos 文件上传的时间戳
     */
    private static final String KERBEROS_FILE_TIMESTAMP = "kerberosFileTimestamp";

    private static Map<Integer, List<Pair<String, String>>> jobSupportTypeMap = Maps.newHashMap();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String DEFAULT_SCHEDULE_CONF = "{\"selfReliance\":1, \"min\":0,\"hour\":0,\"periodType\":\"2\",\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"isFailRetry\":true,\"maxRetryNum\":\"3\"}";

    private static final Integer DEFAULT_SCHEDULE_PERIOD = ESchedulePeriodType.DAY.getVal();

    private static final String CMD_OPTS = "--cmd-opts";

    private static final String OPERATE_MODEL = "operateModel";

    private static final Integer IS_FILE = 1;


    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final Integer INIT_LOCK_VERSION = 0;


    public static final String HADOOP_CONFIG = "hadoopConfig";

    @Autowired
    private ReadWriteLockService readWriteLockService;

    private static final String TASK_PATTERN = "[\\u4e00-\\u9fa5_a-z0-9A-Z-]+";

    @PostConstruct
    public void init() {
        //初始化可以支持的任务类型
        final List<ScheduleDict> yarn = dictService.listByDictType(DictType.DATA_DEVELOP_SUPPORT_TASK_TYPE);
        final List<Pair<String, String>> yarnSupportType = yarn.stream().map(dict -> Pair.of(dict.getDictValue(), dict.getDictDesc())).collect(Collectors.toList());
        jobSupportTypeMap.put(EDeployType.YARN.getType(), yarnSupportType);
    }


    /**
     * 数据开发-根据任务id，查询详情
     *
     * @return
     * @author toutian
     */
    public BatchTaskBatchVO getTaskById(final ScheduleTaskVO scheduleTaskVO) {
        final BatchTask task = this.batchTaskDao.getOne(scheduleTaskVO.getId());
        if (task == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }

        final List<BatchResource> resources = this.batchTaskResourceService.getResources(scheduleTaskVO.getId(), ResourceRefType.MAIN_RES.getType());
        final List<BatchResource> refResourceIdList = this.batchTaskResourceService.getResources(scheduleTaskVO.getId(), ResourceRefType.DEPENDENCY_RES.getType());

        final BatchTaskBatchVO taskVO = new BatchTaskBatchVO(this.batchTaskTaskService.getForefathers(task));
        taskVO.setVersion(task.getVersion());
        if (task.getTaskType().intValue() == EScheduleJobType.SYNC.getVal().intValue()) {  //同步任务类型
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

        final BatchCatalogue catalogue = batchCatalogueService.getOne(task.getNodePid());
        if (catalogue != null) {
            taskVO.setNodePName(catalogue.getNodeName());
        }

        taskVO.setResourceList(resources);
        taskVO.setRefResourceList(refResourceIdList);

        PageQuery pageQuery = new PageQuery(1, 5, "gmt_create", Sort.DESC.name());
        List<BatchTaskVersionDetailDTO> taskVersions = batchTaskVersionService.listByTaskId(scheduleTaskVO.getId(), pageQuery).stream()
                .map(ver -> {
                    if (StringUtils.isNotBlank(ver.getOriginSql())) {
                        if (task.getTaskType().intValue() == EScheduleJobType.SYNC.getVal().intValue()) {
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
        if (task.getTaskType().intValue() == EScheduleJobType.SYNC.getVal().intValue()) {
            try {
                taskVO.setSqlText(JsonUtils.formatJSON(DataFilter.passwordFilter(taskVO.getSqlText())));
            }catch (final Exception e){
                LOGGER.error("同步任务json解析失败 taskId = {}", task.getId(), e);
                taskVO.setSqlText(DataFilter.passwordFilter(taskVO.getSqlText()));
            }

            for (BatchTaskVersionDetailDTO taskVersion : taskVO.getTaskVersions()) {
                try {
                    taskVersion.setSqlText(JsonUtils.formatJSON(DataFilter.passwordFilter(taskVersion.getSqlText())));
                }catch (final Exception e){
                    LOGGER.error("同步任务json解析失败 taskVersionId = {}", taskVersion.getId(), e);
                    taskVersion.setSqlText(DataFilter.passwordFilter(taskVersion.getSqlText()));
                }
            }
        }

        final ReadWriteLockVO readWriteLockVO = this.readWriteLockService.getDetail(
                scheduleTaskVO.getTenantId(), task.getId(),
                ReadWriteLockType.BATCH_TASK, scheduleTaskVO.getUserId(),
                task.getModifyUserId(),
                task.getGmtModified());
        taskVO.setReadWriteLockVO(readWriteLockVO);
        taskVO.setUserId(scheduleTaskVO.getUserId());
        setTaskVariables(taskVO, scheduleTaskVO.getId());
        final List<Long> userIds = new ArrayList<>();
        userIds.add(task.getCreateUserId());
        userIds.add(task.getOwnerUserId());
        final Map<Long, User> userMap = userService.getUserMap(userIds);
        buildUserDTOInfo(userMap,taskVO);
        return taskVO;
    }

    private void setTaskOperatorModelAndOptions(final ScheduleTaskVO taskVO, final BatchTask task) {
        if (task.getTaskType().equals(EScheduleJobType.SPARK.getVal())) {
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
     * 数据开发-检查task与依赖的task是否有构成有向环
     *
     * @author toutian
     */
    public BatchTask checkIsLoop(Long taskId,
                                 Long dependencyTaskId) {

        HashSet<Long> node = Sets.newHashSet(taskId);

        Long loopTaskId = isHasLoop(dependencyTaskId, node);
        if (loopTaskId == 0L) {
            return null;
        }
        return batchTaskDao.getOne(loopTaskId);
    }

    public Long isHasLoop(final Long parentTaskId, final HashSet<Long> node) {
        HashSet<Long> loopNode = new HashSet<>(node.size() + 1);
        loopNode.addAll(node);
        loopNode.add(parentTaskId);
        //出现闭环则返回
        if (loopNode.size() == node.size()) {
            return parentTaskId;
        }

        List<BatchTaskTask> taskTasks = batchTaskTaskService.getAllParentTask(parentTaskId);
        if (CollectionUtils.isEmpty(taskTasks)) {
            return 0L;
        }
        for (BatchTaskTask subTask : taskTasks) {
            Long loopTaskId = isHasLoop(subTask.getParentTaskId(), loopNode);
            if (loopTaskId != 0L) {
                return loopTaskId;
            }
        }
        return 0L;
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


    public void buildUserDTOInfo(final Map<Long, User> userMap, final ScheduleTaskVO vo) {
        if (Objects.nonNull(vo.getCreateUserId())) {
            User createUser = userMap.get(vo.getCreateUserId());
            UserDTO dto = new UserDTO();
            BeanUtils.copyProperties(createUser, dto);
            vo.setCreateUser(dto);
            if (vo.getCreateUserId().equals(vo.getModifyUserId())) {
                vo.setModifyUser(dto);
            } else {
                UserDTO modifyDto = new UserDTO();
                BeanUtils.copyProperties(userMap.getOrDefault(vo.getModifyUserId(),new User()),modifyDto);
                vo.setModifyUser(modifyDto);
            }

            if (vo.getCreateUserId().equals(vo.getOwnerUserId())) {
                vo.setOwnerUser(dto);
            } else {
                UserDTO ownerDto = new UserDTO();
                BeanUtils.copyProperties(userMap.getOrDefault(vo.getOwnerUserId(),new User()), ownerDto);
                vo.setOwnerUser(ownerDto);
            }
        }
    }

    /**
     * 任务发布
     * @param tenantId
     * @param id
     * @param userId
     * @param publishDesc
     * @param isRoot
     * @param ignoreCheck 是否忽略语法校验
     * @return
     * @throws Exception
     */
    @Transactional
    public TaskCheckResultVO publishTask(Long tenantId, Long id, Long userId, String publishDesc, Boolean isRoot, Boolean ignoreCheck) {
        BatchTask task = batchTaskDao.getOne(id);
        if (task == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        return publishBatchTaskInfo(task, tenantId, userId, publishDesc, isRoot, ignoreCheck);
    }

    /**
     * 批量发布任务至engine
     * @param publishTask 要发布的task集合
     * @param tenantId 项目id
     * @param userId 用户id
     * @param publishDesc 发布描述
     * @param isRoot 是否是管理员
     * @param ignoreCheck 忽略检查
     * @return 发布结果
     */
    public TaskCheckResultVO publishBatchTaskInfo(BatchTask publishTask, Long tenantId, Long userId, String publishDesc, Boolean isRoot, Boolean ignoreCheck) {
        //判断任务责任人是否存在 如果任务责任人不存在或无权限 不允许提交
        User user = userService.getById(publishTask.getOwnerUserId());
        if (user == null){
            throw new RdosDefineException(String.format("%s任务责任人在数栈中不存在", publishTask.getName()));
        }

        TaskCheckResultVO checkResultVO = new TaskCheckResultVO();
        checkResultVO.setErrorSign(PublishTaskStatusEnum.NOMAL.getType());

        // 检查任务是否可以发布并记录版本信息
        TaskCheckResultVO resultVO = checkTaskAndSaveVersion(publishTask, tenantId, userId, publishDesc, isRoot, ignoreCheck);
        if (!PublishTaskStatusEnum.NOMAL.getType().equals(resultVO.getErrorSign())){
            //做一下优化 如果是工作流任务的话 把任务名称打印出来
            if (publishTask.getFlowId()>0){
                resultVO.setErrorMessage(String.format("任务:%s提交失败，原因是:%s", publishTask.getName(), resultVO.getErrorMessage()));
            }
            return resultVO;
        }

        // 发布任务中所有的依赖关系
        List<Long> parentTaskIds = Lists.newArrayList();
        ScheduleTaskShadeDTO scheduleTaskShadeDTO = buildScheduleTaskShadeDTO(publishTask, parentTaskIds);

        // 提交任务参数信息并保存任务记录和更新任务状态
        try {
            BatchTask batchTask = getOneWithError(scheduleTaskShadeDTO.getTaskId());
            String extraInfo = this.batchJobService.getExtraInfo(batchTask, userId, null);
            scheduleTaskShadeDTO.setExtraInfo(extraInfo);
            // 无异常保存一条任务记录并更新任务状态
            saveRecordAndUpdateSubmitStatus(batchTask, tenantId, userId, TaskOperateType.COMMIT.getType(), ESubmitStatus.SUBMIT.getStatus());
        } catch (Exception e) {
            LOGGER.error("send task error {} ", scheduleTaskShadeDTO.getTaskId(), e);
            throw new RdosDefineException(String.format("任务提交异常：%s", e.getMessage()), e);
        }

        SavaTaskDTO savaTaskDTO = new SavaTaskDTO();
        savaTaskDTO.setScheduleTaskShadeDTO(scheduleTaskShadeDTO);
        savaTaskDTO.setParentTaskIdList(parentTaskIds);
        // 批量发布任务
        this.taskService.saveTask(savaTaskDTO);

        LOGGER.info("待发布任务参数提交完毕");
        return checkResultVO;
    }

    /**
     * 保存一条任务记录并更新任务状态
     * @param task 任务信息
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskOperateType 任务操作类型 @{@link TaskOperateType}
     * @param submitStatus 发布状态 {@link ESubmitStatus}
     */
    private void saveRecordAndUpdateSubmitStatus(BatchTask task, Long tenantId, Long userId, Integer taskOperateType, Integer submitStatus) {
        this.updateSubmitStatus(tenantId, task.getId(), submitStatus);
    }

    /**
     * 检查要发布的任务并保存版本信息
     *
     * @param task 任务信息
     * @param tenantId 项目id
     * @param userId 用户id
     * @param publishDesc 发布描述
     * @param isRoot 是否是管理员
     * @param ignoreCheck 是否忽略检查
     * @return 检查结果
     */
    private TaskCheckResultVO checkTaskAndSaveVersion(BatchTask task, Long tenantId, Long userId, String publishDesc, Boolean isRoot, Boolean ignoreCheck) {
        TaskCheckResultVO checkVo  = new TaskCheckResultVO();
        checkVo.setErrorSign(PublishTaskStatusEnum.NOMAL.getType());
        checkTaskCanSubmit(task);

        task.setSubmitStatus(ESubmitStatus.SUBMIT.getStatus());
        task.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));

        final BatchTaskVersion version = new BatchTaskVersion();
        version.setCreateUserId(userId);

        String versionSqlText = StringUtils.EMPTY;

        if (EScheduleJobType.SPARK_SQL.getVal().intValue() == task.getTaskType().intValue()) {
            // 语法检测
            List<BatchTaskParam> taskParamsToReplace = batchTaskParamService.getTaskParam(task.getId());
            versionSqlText = this.jobParamReplace.paramReplace(task.getSqlText(), taskParamsToReplace, this.sdf.format(new Date()));
            //避免重复校验
            CheckSyntaxResult syntaxResult = batchSqlExeService.processSqlText(task.getTenantId(), task.getTaskType(), versionSqlText);
            if (!syntaxResult.getCheckResult()){
                checkVo.setErrorSign(PublishTaskStatusEnum.CHECKSYNTAXERROR.getType());
                checkVo.setErrorMessage(syntaxResult.getMessage());
                return checkVo;
            }

        } else if (EScheduleJobType.SYNC.getVal().intValue() == task.getTaskType().intValue()) {
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
        }

        version.setSqlText(versionSqlText);
        version.setOriginSql(task.getSqlText());
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
                map.put("parentTaskId", taskTask.getParentTaskId());
                return map;
            }).collect(Collectors.toList());
            dependencyTaskIds = JSON.toJSONString(parentTasks);
        }

        version.setDependencyTaskIds(dependencyTaskIds);
        version.setPublishDesc(null == publishDesc ? "" : publishDesc);
        // 插入一条记录信息
        batchTaskVersionService.insert(version);
        task.setVersion(version.getId().intValue());
        return checkVo;
    }

    /**
     * 构建一个要发布到engine的任务DTO {@link ScheduleTaskShadeDTO}
     * @param batchTask 要发布的任务集合
     * @param parentTaskIds 父任务的id
     * @return 调度任务DTO
     */
    private ScheduleTaskShadeDTO buildScheduleTaskShadeDTO(final BatchTask batchTask, List<Long> parentTaskIds) {
        if (batchTask.getId() <= 0) {
            //只有异常情况才会走到该逻辑
            throw new RdosDefineException("batchTask id can't be 0", ErrorCode.SERVER_EXCEPTION);
        }

        final long taskId = batchTask.getId();
        //清空任务关联的batch_task_param, task_resource, task_task 表信息
        this.batchTaskParamShadeService.clearDataByTaskId(taskId);
        this.batchTaskResourceShadeService.clearDataByTaskId(taskId);

        final List<BatchTaskParam> batchTaskParamList = this.batchTaskParamService.getTaskParam(batchTask.getId());
        //查询出任务所有的关联的资源(运行主体资源和依赖引用资源)
        final List<BatchTaskResource> batchTaskResourceList = this.batchTaskResourceService.getTaskResources(batchTask.getId(), null);
        List<Long> parentTaskList = this.batchTaskTaskService.getAllParentTaskId(batchTask.getId());
        parentTaskIds.addAll(parentTaskList);

        if (!CollectionUtils.isEmpty(batchTaskResourceList)) {
            this.batchTaskResourceShadeService.saveTaskResource(batchTaskResourceList);
        }
        //保存batch_task_shade
        final ScheduleTaskShadeDTO scheduleTaskShadeDTO = new ScheduleTaskShadeDTO();
        BeanUtils.copyProperties(batchTask, scheduleTaskShadeDTO);
        scheduleTaskShadeDTO.setTaskId(batchTask.getId());
        scheduleTaskShadeDTO.setScheduleStatus(EScheduleStatus.NORMAL.getVal());

        if (!CollectionUtils.isEmpty(batchTaskParamList)) {
            this.batchTaskParamShadeService.saveTaskParam(batchTaskParamList);
        }else{
            scheduleTaskShadeDTO.setTaskParams("");
        }

        return scheduleTaskShadeDTO;
    }

    public List<BatchTaskVersionDetailDTO> getTaskVersionRecord(Long taskId, Integer pageSize, Integer pageNo) {
        if (pageNo == null) {
            pageNo = 0;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        PageQuery pageQuery = new PageQuery(pageNo, pageSize, "gmt_create", Sort.DESC.name());
        List<BatchTaskVersionDetailDTO> res = batchTaskVersionService.listByTaskId(taskId, pageQuery);
        for (BatchTaskVersionDetailDTO detail : res) {
            detail.setUserName(userService.getUserName(detail.getCreateUserId()));
        }
        return res;
    }

    public BatchTaskVersionDetailDTO taskVersionScheduleConf(Long versionId) {
        BatchTaskVersionDetailDTO taskVersion = batchTaskVersionService.getByVersionId(versionId);
        if (taskVersion == null) {
            return null;
        }
        taskVersion.setUserName(userService.getUserName(taskVersion.getCreateUserId()));
        if (StringUtils.isNotBlank(taskVersion.getDependencyTaskIds())) {
            List<Map<String, Object>> dependencyTasks = getDependencyTasks(taskVersion.getDependencyTaskIds());
            JSONObject taskParams = new JSONObject();
            int i = 1;
            for (Map<String, Object> dependencyTask : dependencyTasks) {
                ScheduleTaskShade taskShade = taskService.findTaskByTaskId(MathUtil.getLongVal(dependencyTask.get("parentTaskId")));
                if (taskShade != null) {
                    JSONObject taskParam = new JSONObject();
                    taskParam.put("taskName", taskShade.getName());
                    taskParam.put("tenantName", tenantService.getTenantById(taskShade.getTenantId()).getTenantName());
                    taskParams.put("task" + i++, taskParam);
                }
            }
            taskVersion.setDependencyTasks(taskParams);
        }
        return taskVersion;
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
        this.checkBeforeUpdateTask(param);
        if (StringUtils.isNotBlank(param.getScheduleConf())) {
            //处理调度配置
            JSONObject schduleConf = JSON.parseObject(param.getScheduleConf());
            if (schduleConf.get("isExpire") != null && "false".equals(schduleConf.get("isExpire").toString())) {
                schduleConf.replace("isLastInstance", true);
                param.setScheduleConf(schduleConf.toString());
            }
            param.setPeriodType(schduleConf.getInteger("periodType"));
        }
        if (param.getId() > 0 && param.getTaskType().equals(EScheduleJobType.WORK_FLOW.getVal())) {
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
                        task.setTenantId(param.getTenantId());
                    });
                    batchTaskTaskService.addOrUpdateTaskTask(entry.getKey(), dependencyTasks);
                }
            }
        }

        BatchTaskBatchVO task = PublicUtil.objectToObject(param, BatchTaskBatchVO.class);
        task.setModifyUserId(param.getUserId());
        task.setVersion(Objects.isNull(param.getVersion()) ? 0 : param.getVersion());
        task.parsePeriodType();
        task = this.updateTask(task, param.getIsEditBaseInfo());
        TaskCatalogueVO taskCatalogueVO = new TaskCatalogueVO(task, task.getNodePid());

        // 强行置为更新
        taskCatalogueVO.getReadWriteLockVO().setResult(TaskLockStatus.TO_UPDATE.getVal());

        //更新 关联资源
        if (param.getResourceIdList() != null) {
            final Map<String, Object> params = Maps.newHashMap();
            params.put("id", task.getId());
            params.put("resources", param.getResourceIdList());
            params.put("createUserId", task.getCreateUserId());
            this.updateTaskResource(params);
        }

        if (param.getRefResourceIdList() != null) {
            final Map<String, Object> params = Maps.newHashMap();
            params.put("id", task.getId());
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
        if (param.getId() > 0 && EScheduleJobType.SYNC.getVal().equals(param.getTaskType())) {
            final String context = param.getSqlText();
            if (null == context) {
                return;
            }
            //1、检查上送字段是否存在需要处理的密码，不存在直接跳过
            final Pattern pattern = Pattern.compile(PatternConstant.PASSWORD_FIELD_REGEX, Pattern.CASE_INSENSITIVE);
            final Matcher matcher = pattern.matcher(context);
            if (matcher.find()) {
                LOGGER.info("当前上送信息存在隐藏密码字段，准备执行旧密码回填操作");
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
    private void checkBeforeUpdateTask(TaskResourceParam param) {
        if (EScheduleJobType.SYNC.getVal().equals(param.getTaskType())) {
            operateSyncTask(param);
            return;
        }
        if (CollectionUtils.isNotEmpty(param.getResourceIdList())) {
            throw new RdosDefineException("该任务不能添加资源.", ErrorCode.INVALID_PARAMETERS);
        }
    }

    /**
     * 处理数据同步任务
     * @param param
     * @return
     */
    private void operateSyncTask(TaskResourceParam param) {
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
        LOGGER.info("addOrUpdateTask with createModel {}", param.getCreateModel());

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

        if (!EScheduleJobType.SYNC.getVal().equals(task.getTaskType())) {
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

        String readerPlugin = JSONPath.eval(jobJson, "$.job.content[0].reader.name").toString();
        String writerPlugin = JSONPath.eval(jobJson, "$.job.content[0].writer.name").toString();

        if (!PluginName.RDB_READER.contains(readerPlugin)) {
            throw new RdosDefineException("增量同步任务只支持从关系型数据库读取", ErrorCode.INVALID_PARAMETERS);
        }

        if (!PluginName.HDFS_W.equals(writerPlugin)) {
            throw new RdosDefineException("增量同步任务只支持写入hive和hdfs", ErrorCode.INVALID_PARAMETERS);
        }

        if (!checkIncreCol) {
            return;
        }

        String increColumn = (String) JSONPath.eval(jobJson, "$.job.content[0].reader.parameter.increColumn");
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
        String type = EScheduleJobType.getByTaskType(task.getTaskType()).getName();
        final StringBuilder sb = new StringBuilder();

        // 需要代码注释模版的任务类型
        Set<Integer> shouldNoteSqlTypes = Sets.newHashSet(EScheduleJobType.SPARK_SQL.getVal());

        if (shouldNoteSqlTypes.contains(task.getTaskType())) {
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


        task.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        BatchTask batchTask = this.batchTaskDao.getByName(task.getName(), null);

        boolean isAdd = false;
        if (task.getId() > 0) {//update
            BatchTask specialTask = getOneWithError(task.getId());
            if (task.getTaskType() == null) {
                task.setTaskType(specialTask.getTaskType());
            }
            String oriTaskName = specialTask.getName();
            if (batchTask != null && !batchTask.getId().equals(task.getId())) {
                throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
            }

            BatchTask specialTask1 = new BatchTask();
            ReadWriteLockVO readWriteLockVO = this.readWriteLockService.dealWithLock(
                    task.getTenantId(),
                    task.getId(),
                    ReadWriteLockType.BATCH_TASK,
                    task.getUserId(),
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
                    });

            task.setReadWriteLockVO(readWriteLockVO);
            //如果是工作流任务 更新父节点调度类型时，需要同样更新子节点
            if (EScheduleJobType.WORK_FLOW.getVal().equals(task.getTaskType()) && task.getFlowId() == 0 && StringUtils.isNotEmpty(task.getScheduleConf())){
                updateSonTaskPeriodType(task.getId(),task.getPeriodType(),task.getScheduleConf());
            }
            if (!oriTaskName.equals(task.getName())) {//修改名字需要同步到taskShade
                this.taskService.updateTaskName(task.getId(), task.getName());
            }
            LOGGER.info("success update batchTask, taskId:{}", task.getId());

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
                BatchReadWriteLock readWriteLock = readWriteLockDao.getByTenantIdAndRelationIdAndType(0L, parentTask.getId(), ReadWriteLockType.BATCH_TASK.name());
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

//            parseCreateTaskExeArgs(task);

            //新增锁
            ReadWriteLockVO readWriteLockVO = this.readWriteLockService.getLock(
                    task.getTenantId(),
                    task.getUserId(),
                    ReadWriteLockType.BATCH_TASK.name(),
                    task.getId(),
                     null);
            task.setReadWriteLockVO(readWriteLockVO);
            LOGGER.info("success insert batchTask, taskId:{}", task.getId());
        }

        // 右键编辑时会调用另一个接口
        if (BooleanUtils.isNotTrue(isEditBaseInfo)) {
            if (!EScheduleJobType.WORK_FLOW.getVal().equals(task.getTaskType())  &&
                    !EScheduleJobType.VIRTUAL.getVal().equals(task.getTaskType())) {
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
        if (StringUtils.isBlank(task.getTaskParams())) {
            task.setTaskParams("");
        }

        if (StringUtils.isBlank(task.getMainClass())) {
            task.setMainClass("");
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

        if (task.getOwnerUserId() == null) {
            task.setOwnerUserId(task.getUserId());
        }
        if (task.getCreateUserId() == null) {
            task.setCreateUserId(task.getUserId());
        }
        task.setGmtCreate(task.getGmtModified());
        // 增加注释
        task.setSqlText(this.createAnnotationText(task));
        task.setSubmitStatus(ESubmitStatus.UNSUBMIT.getStatus());
        task.setTaskParams(getDefaultTaskParam(task.getTenantId(),task.getTaskType()));
        task.setScheduleStatus(EScheduleStatus.NORMAL.getVal());
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

    private Integer getLockVersion(final BatchTaskBatchVO task) {
        Integer lockVersion;
        //仅更新名字时readWriteLock可能为空
        if (task.getReadWriteLockVO() == null) {
            BatchReadWriteLock lock = readWriteLockService.getReadWriteLock(0L, task.getId(), ReadWriteLockType.BATCH_TASK.name());
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
                throw new RdosDefineException("sqlText的json格式化失败", e);
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
        Preconditions.checkState(taskResourceMap.containsKey("tenantId"), "need param of tenantId");
        Preconditions.checkState(taskResourceMap.containsKey("createUserId"), "need param of createUserId");

        final Long id = MathUtil.getLongVal(taskResourceMap.get("id"));
        final List<Object> oriResourceList = (List<Object>) taskResourceMap.get("resources");

        final BatchTask task = this.batchTaskDao.getOne(id);
        Preconditions.checkNotNull(task, "can not find task by id " + id);

        //删除旧的资源
        batchTaskResourceService.deleteByTaskId(task.getId(), ResourceRefType.MAIN_RES.getType());

        //添加新的资源
        if (CollectionUtils.isNotEmpty(oriResourceList)) {
            List<Long> resourceIdList = Lists.newArrayList();
            oriResourceList.forEach(tmpId -> resourceIdList.add(MathUtil.getLongVal(tmpId)));
            batchTaskResourceService.save(task, resourceIdList, ResourceRefType.MAIN_RES.getType());
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
        Preconditions.checkState(taskResourceMap.containsKey("tenantId"), "need param of tenantId");
        Preconditions.checkState(taskResourceMap.containsKey("createUserId"), "need param of createUserId");

        final Long id = MathUtil.getLongVal(taskResourceMap.get("id"));
        final List<Object> refResourceList = (List<Object>) taskResourceMap.get("refResource");

        final BatchTask task = getOneWithError(id);

        //删除旧的资源
        batchTaskResourceService.deleteByTaskId(task.getId(), ResourceRefType.DEPENDENCY_RES.getType());

        //添加新的关联资源
        if (CollectionUtils.isNotEmpty(refResourceList)) {
            final List<Long> refResourceIdList = Lists.newArrayList();
            refResourceList.forEach(tmpId -> refResourceIdList.add(MathUtil.getLongVal(tmpId)));
            this.batchTaskResourceService.save(task, refResourceIdList, ResourceRefType.DEPENDENCY_RES.getType());
        }
    }

    /**
     * 获取任务的默认参数
     *
     * @param tenantId 租户ID
     * @param taskType 任务类型
     * @return
     */
    private String getDefaultTaskParam(Long tenantId, Integer taskType) {
        EScheduleJobType eScheduleJobType = EScheduleJobType.getByTaskType(taskType);
        List<Component> componentList = componentService.listComponentsByComponentType(tenantId, eScheduleJobType.getComponentType().getTypeCode());
        if (CollectionUtils.isEmpty(componentList)){
            return Strings.EMPTY_STRING;
        }
        // todo 后续多版本再进行扩展
        String version = componentList.get(0).getVersionName();
        TaskParamTemplate taskParamTemplate = taskParamTemplateService.getTaskParamTemplate(version, taskType);
        return Objects.isNull(taskParamTemplate) ? Strings.EMPTY_STRING : taskParamTemplate.getParams();
    }

    /**
     * 数据开发-删除任务
     *
     * @param taskId    任务id
     * @param tenantId 项目id
     * @param userId    用户id
     * @return
     * @author toutian
     */
    @Transactional
    public Long deleteTask(Long taskId, Long tenantId, Long userId, String sqlText) {

        final BatchTask batchTask = this.batchTaskDao.getOne(taskId);
        if (batchTask == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        // 判断该任务是否有子任务(调用engine接口) 工作流不需要判断
        if (batchTask.getFlowId() == 0) {
            List<TaskGetNotDeleteVO> notDeleteTaskVOS = getChildTasks(taskId);
            if (CollectionUtils.isNotEmpty(notDeleteTaskVOS)) {
                throw new RdosDefineException("(当前任务被其他任务依赖)", ErrorCode.CAN_NOT_DELETE_TASK);
            }
        }

        final ScheduleTaskShade dbTask = this.taskService.findTaskByTaskId(taskId);
        if (batchTask.getFlowId() == 0 && Objects.nonNull(dbTask) &&
        batchTask.getScheduleStatus().intValue() == EScheduleStatus.NORMAL.getVal().intValue()){
            throw new RdosDefineException("(当前任务未被冻结)", ErrorCode.CAN_NOT_DELETE_TASK);
        }

        if (batchTask.getTaskType().intValue() == EScheduleJobType.WORK_FLOW.getVal()) {
            final List<BatchTask> batchTasks = this.getFlowWorkSubTasks(taskId);
            //删除所有子任务相关
            batchTasks.forEach(task -> this.deleteTaskInfos(task.getId(), tenantId, userId));
        }

        //删除工作流中的子任务同时删除被依赖的关系
        if (batchTask.getFlowId() > 0) {
            this.batchTaskTaskService.deleteTaskTaskByParentId(batchTask.getId());
        }

        if (StringUtils.isNotBlank(sqlText)) {
            final BatchTask batchTaskBean=new BatchTask();
            batchTaskBean.setId(batchTask.getFlowId());
            batchTaskBean.setSqlText(sqlText);
            this.batchTaskDao.updateSqlText(batchTaskBean);
            LOGGER.info("sqlText 修改成功");
        } else {
            LOGGER.error("deleteTask sqlText is null");
        }
        //删除任务
        this.deleteTaskInfos(taskId, tenantId, userId);

        return taskId;
    }

    public void deleteTaskInfos(Long taskId, Long tenantId, Long userId) {
        //软删除任务记录
        this.batchTaskDao.deleteById(taskId, Timestamp.valueOf(LocalDateTime.now()), tenantId, userId);
        //删除任务的依赖关系
        this.batchTaskTaskService.deleteTaskTaskByTaskId(taskId);
        //删除关联的函数资源
        this.batchTaskResourceService.deleteTaskResource(taskId);
        this.batchTaskResourceShadeService.deleteByTaskId(taskId);
        //删除关联的参数表信息
        this.batchTaskParamService.deleteTaskParam(taskId);
        //删除发布相关的数据
        this.taskService.deleteTask(taskId, userId);
    }


    public BatchTask getBatchTaskById(final long taskId) {
        return this.batchTaskDao.getOne(taskId);
    }


    public List<BatchTask> getTaskByIds(final List<Long> taskIdArray) {
        if (CollectionUtils.isEmpty(taskIdArray)) {
            return ListUtils.EMPTY_LIST;
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
        if (task.getTaskType().equals(EScheduleJobType.SPARK_SQL.getVal()) && StringUtils.isEmpty(task.getSqlText())) {
            throw new RdosDefineException(task.getName() + "任务的SQL为空", ErrorCode.TASK_CAN_NOT_SUBMIT);
        } else if (task.getTaskType().equals(EScheduleJobType.SYNC.getVal())) {
            if (StringUtils.isBlank(task.getSqlText())) {
                throw new RdosDefineException(task.getName() + "任务配置信息为空", ErrorCode.TASK_CAN_NOT_SUBMIT);
            }
            final String sqlText = Base64Util.baseDecode(task.getSqlText());
            final JSONObject jsonObject = JSON.parseObject(sqlText);
            if (jsonObject.containsKey("parser")) {
                final JSONObject parser = jsonObject.getJSONObject("parser");
                if (parser.containsKey("targetMap")) {
                    dataSourceService.checkConnectionById(parser.getJSONObject("targetMap").getLong("sourceId"));
                }
                if (parser.containsKey("sourceMap")) {
                    final JSONObject sourceMap = parser.getJSONObject("sourceMap");
                    if (sourceMap.containsKey("sourceList")) {
                        final JSONArray sourceList = sourceMap.getJSONArray("sourceList");
                        for (final Object o : sourceList) {
                            final JSONObject source = (JSONObject) o;
                            dataSourceService.checkConnectionById(source.getLong("sourceId"));
                        }
                    } else {
                        dataSourceService.checkConnectionById(parser.getJSONObject("sourceMap").getLong("sourceId"));
                    }
                }
            }
        }
        return true;
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
     * @param tenantId
     */
    public void checkName(String name, String type, Integer pid, Integer isFile, Long tenantId) {
        if (StringUtils.isBlank(name)) {
            return;
        }
        if (!isFile.equals(IS_FILE)) {
            BatchCatalogue batchCatalogue = batchCatalogueService.getByPidAndName(tenantId, pid.longValue(), name);
            if (batchCatalogue != null) {
                throw new RdosDefineException("文件夹已存在", ErrorCode.NAME_ALREADY_EXIST);
            }
        } else {
            final Object obj;
            if (type.equals(CatalogueType.TASK_DEVELOP.name())) {
                obj = this.batchTaskDao.getByName(name, tenantId);
            } else if (type.equals(CatalogueType.RESOURCE_MANAGER.name())) {
                obj = batchResourceService.listByNameAndTenantId(tenantId, name);
            } else if (type.equals(CatalogueType.CUSTOM_FUNCTION.name())) {
                obj = batchFunctionService.listByNameAndTenantId(tenantId, name, FuncType.CUSTOM.getType());
            } else if (type.equals(CatalogueType.PROCEDURE_FUNCTION.name())) {
                obj = batchFunctionService.listByNameAndTenantId(tenantId, name, FuncType.PROCEDURE.getType());
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

    /**
     * 获取任务流下的所有子任务
     *
     * @param taskId
     * @return
     */
    public List<BatchTask> getFlowWorkSubTasks(final Long taskId) {
        BatchTaskDTO batchTaskDTO = new BatchTaskDTO();
        batchTaskDTO.setIsDeleted(Deleted.NORMAL.getStatus());
        batchTaskDTO.setFlowId(taskId);
        PageQuery<BatchTaskDTO> pageQuery = new PageQuery<>(batchTaskDTO);
        List<BatchTask> batchTasks = this.batchTaskDao.generalQuery(pageQuery);
        return batchTasks;
    }

    public BatchTask getByName(String name, Long tenantId) {
        return this.batchTaskDao.getByName(name, tenantId);
    }

    private Integer updateSubmitStatus(final Long tenantId, final Long taskId, final Integer submitStatus) {
        return this.batchTaskDao.updateSubmitStatus(tenantId, taskId, submitStatus, Timestamp.valueOf(LocalDateTime.now()));
    }

    /**
     * 根据ID查询信息
     *  如果不存在，则抛异常
     *
     * @param taskId 任务ID
     * @return
     */
    public BatchTask getOneWithError(final Long taskId) {
        BatchTask one = getOne(taskId);
        if (Objects.isNull(one)) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        return one;
    }

    /**
     * 根据ID查询信息
     *
     * @param taskId 任务ID
     * @return
     */
    public BatchTask getOne(Long taskId) {
        return this.batchTaskDao.getOne(taskId);
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
    public List<TaskGetNotDeleteVO> getChildTasks(Long taskId) {
        List<ScheduleTaskShade> notDeleteTaskVOs = taskService.listRelyCurrentTask(taskId);
        return notDeleteTaskVOs.stream()
                .map(taskShade ->{
                    TaskGetNotDeleteVO deleteVO = new TaskGetNotDeleteVO();
                    deleteVO.setTaskId(taskShade.getTaskId());
                    deleteVO.setName(taskShade.getName());
                    return deleteVO;
                }).collect(Collectors.toList());
    }

    /**
     * 获取组件版本
     * @param tenantId
     * @param taskType
     * @return
     */
    public List<BatchTaskGetComponentVersionResultVO> getComponentVersionByTaskType(Long tenantId, Integer taskType) {
        List<Component> components = componentService.getComponentVersionByEngineType(tenantId, taskType);
        List<BatchTaskGetComponentVersionResultVO> componentVersionResultVOS = Lists.newArrayList();
        for (Component component : components) {
            BatchTaskGetComponentVersionResultVO resultVO = new BatchTaskGetComponentVersionResultVO();
            resultVO.setComponentVersion(component.getVersionValue());
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
                        LOGGER.info("hadoop版本号：{}, {}", o1, o2, e);
                    }
                }
            }
            return o2.getComponentVersion().compareTo(o1.getComponentVersion());
        };
    }


    /**
     * 根据 租户、目录id 查询任务列表
     * @param tenantId
     * @param nodePid
     * @return
     */
    public List<BatchTask> listBatchTaskByNodePid(Long tenantId, Long nodePid) {
        return batchTaskDao.listBatchTaskByNodePid(tenantId, nodePid);
    }

    /**
     * 根据 租户、目录id 查询任务列表
     * 此方法适合目录信息查询，与上面方法的区别是不返回SqlText等无用的大数据字段
     * @param tenantId
     * @param nodePid
     * @return
     */
    public List<BatchTask> catalogueListBatchTaskByNodePid(Long tenantId, Long nodePid) {
        return batchTaskDao.catalogueListBatchTaskByNodePid(tenantId, nodePid);
    }

    public JSONObject trace(final Long taskId) {
        String sqlText = null;
        final BatchTask batchTask = this.getBatchTaskById(taskId);

        if (batchTask == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        } else {
            sqlText = batchTask.getSqlText();
        }

        final String sql = Base64Util.baseDecode(sqlText);
        if (StringUtils.isBlank(sql)) {
            return null;
        }

        final JSONObject sqlJson = JSON.parseObject(sql);
        JSONObject parserJson = sqlJson.getJSONObject("parser");
        if (parserJson != null) {
            parserJson = this.checkTrace(parserJson);
            parserJson.put("sqlText", sqlJson.getString("job"));
            parserJson.put("syncMode", sqlJson.get("syncMode"));
            parserJson.put("taskId", taskId);
        }
        return parserJson;
    }


    private JSONObject checkTrace(final JSONObject jsonObject) {
        final JSONObject keymap = jsonObject.getJSONObject("keymap");
        final JSONArray source = keymap.getJSONArray("source");
        final JSONArray target = keymap.getJSONArray("target");
        final JSONObject sourceMap = jsonObject.getJSONObject("sourceMap");
        final Integer fromId = (Integer) sourceMap.get("sourceId");
        final JSONObject targetMap = jsonObject.getJSONObject("targetMap");
        final Integer toId = (Integer) targetMap.get("sourceId");
        final JSONObject sourceType = sourceMap.getJSONObject("type");
        final List<String> sourceTables = this.getTables(sourceType);
        final JSONObject targetType = targetMap.getJSONObject("type");
        final List<String> targetTables = this.getTables(targetType);
        final BatchDataSource fromDs = dataSourceService.getOne(fromId.longValue());
        final BatchDataSource toDs = dataSourceService.getOne(toId.longValue());

        int fromSourceType = DataSourceType.getSourceType(fromDs.getType()).getVal();
        int toSourceType = DataSourceType.getSourceType(toDs.getType()).getVal();
        if (DataSourceType.HBASE.getVal() == fromSourceType || DataSourceType.HBASE.getVal() == toSourceType) {
            return jsonObject;
        }

        // 处理分库分表的信息
        this.addSourceList(sourceMap);

        if (CollectionUtils.isNotEmpty(sourceTables)) {
            getMetaDataColumns(sourceMap, sourceTables, fromDs);
        }

        if (CollectionUtils.isNotEmpty(targetTables)) {
            getMetaDataColumns(targetMap, targetTables, toDs);
        }
        //因为下面要对keyMap中target中的字段类型进行更新 所以遍历一次目标map 拿出字段和类型的映射
        Map<String,String> newTargetColumnTypeMap = targetMap.getJSONArray(COLUMN)
                .stream().map(column -> (JSONObject)column)
                .collect(Collectors.toMap(column -> column.getString(KEY), column -> column.getString(TYPE)));


        final Collection<BatchSysParameter> sysParams = this.getSysParams();

        final JSONArray newSource = new JSONArray();
        final JSONArray newTarget = new JSONArray();
        for (int i = 0; i < source.size(); ++i) {
            boolean srcTag = true;
            final JSONArray srcColumns = sourceMap.getJSONArray("column");
            if (CollectionUtils.isNotEmpty(sourceTables)) {
                int j = 0;
                final String srcColName;
                String colValue = "";
                if (!(source.get(i) instanceof JSONObject)) {
                    srcColName = source.getString(i);
                } else {
                    //source 可能含有系统变量
                    srcColName = source.getJSONObject(i).getString("key");
                    colValue = source.getJSONObject(i).getString("value");
                }

                //srcColumns 源表中的字段
                for (; j < srcColumns.size(); ++j) {
                    final JSONObject srcColumn = srcColumns.getJSONObject(j);
                    if (srcColumn.getString("key").equals(srcColName)) {
                        break;
                    }
                }
                boolean isSysParam = false;
                for (final BatchSysParameter sysParam : sysParams) {
                    if (sysParam.strIsSysParam(colValue)) {
                        isSysParam = true;
                        break;
                    }
                }
                // 没有系统变量 还需要判断是否有自定义变量
                if(!isSysParam){
                    isSysParam = StringUtils.isNotBlank(colValue);
                }
                //兼容系统变量
                if (isSysParam) {
                    boolean hasThisKey = false;
                    for (int k = 0; k < srcColumns.size(); ++k) {
                        final JSONObject srcColumn = srcColumns.getJSONObject(k);
                        if (srcColumn.getString("key").equals(srcColName)) {
                            hasThisKey = true;
                            break;
                        }

                    }
                    if (!hasThisKey) {
                        //创建出系统变量colume
                        final JSONObject jsonColumn = new JSONObject();
                        jsonColumn.put("key", srcColName);
                        jsonColumn.put("value", colValue);
                        jsonColumn.put("type",source.getJSONObject(i).getString("type"));
                        jsonColumn.put("format",source.getJSONObject(i).getString("format"));
                        srcColumns.add(jsonColumn);
                    }
                }
                if (j == srcColumns.size() && !isSysParam) {
                    srcTag = false;
                }
            }

            boolean destTag = true;
            final JSONArray destColumns = targetMap.getJSONArray("column");
            if (CollectionUtils.isNotEmpty(targetTables)) {
                int k = 0;
                final String destColName;
                if (!(target.get(i) instanceof JSONObject)) {
                    destColName = target.getString(i);
                } else {
                    destColName = target.getJSONObject(i).getString("key");
                    //更新dest表中字段类型
                    final String newType = newTargetColumnTypeMap.get(destColName);
                    if (StringUtils.isNotEmpty(newType)){
                        target.getJSONObject(i).put("type",newType);
                    }
                }
                for (; k < destColumns.size(); ++k) {
                    final JSONObject destColumn = destColumns.getJSONObject(k);
                    if (destColumn.getString("key").equals(destColName)) {
                        break;
                    }
                }

                if (k == destColumns.size()) {
                    destTag = false;
                }
            }

            if (srcTag && destTag) {
                newSource.add(source.get(i));
                newTarget.add(target.get(i));
            }
        }

        keymap.put("source", newSource);
        keymap.put("target", newTarget);

        return jsonObject;
    }


    private List<String> getTables(final Map<String, Object> map) {
        final List<String> tables = new ArrayList<>();
        if (map.get("table") instanceof String) {
            tables.add(map.get("table").toString());
        } else {
            final List<String> tableList = (List<String>) map.get("table");
            if (CollectionUtils.isNotEmpty(tableList)) {
                tables.addAll((List<String>) map.get("table"));
            }
        }

        return tables;
    }

    private void addSourceList(final JSONObject sourceMap) {
        if (sourceMap.containsKey("sourceList")) {
            return;
        }

        if (!DataSourceType.MySQL.getVal().equals(sourceMap.getJSONObject("type").getInteger("type"))) {
            return;
        }

        final JSONArray sourceList = new JSONArray();
        final JSONObject source = new JSONObject();
        source.put("sourceId", sourceMap.get("sourceId"));
        source.put("name", sourceMap.getString("name"));
        source.put("type", sourceMap.getJSONObject("type").getInteger("type"));
        source.put("tables", Arrays.asList(sourceMap.getJSONObject("type").getString("table")));
        sourceList.add(source);

        sourceMap.put("sourceList", sourceList);
    }

    /**
     * 刷新sourceMap中的字段信息
     * 这个方法做了3个事情
     * 1.拿到sourceMap的中的原字段信息
     * 2.拿到对应表的 元数据最新字段信息
     * 3.和原字段信息进行匹配，
     * 如果原字段中的某个字段 不在最新字段中 那就忽略大小写再匹配一次，如果能匹配到就用原字段信息
     * 原因是 Hive执行alter语句增加字段会把源信息所有字段变小写  导致前端映射关系丢失 这里做一下处理
     * @param sourceMap
     * @param sourceTables
     * @param fromDs
     */
    private void getMetaDataColumns(JSONObject sourceMap, List<String> sourceTables, BatchDataSource fromDs) {
        JSONArray srcColumns = new JSONArray();
        List<JSONObject> custColumns = new ArrayList<>();
        List<String> allOldColumnsName = new ArrayList<>();
        Map<String,String> newNameToOldName = new HashMap<>();
        //对原有的字段进行处理 处理方式看方法注释
        getAllTypeColumnsMap(sourceMap, custColumns, allOldColumnsName, newNameToOldName);
        //获取原有字段
        JSONArray sourceColumns = sourceMap.getJSONArray(COLUMN);
        try {
            //获取一下schema
            String schema = sourceMap.getString("schema");
            List<JSONObject> tableColumns = getTableColumnIncludePart(fromDs, sourceTables.get(0), true, schema);
            for (JSONObject tableColumn : tableColumns) {
                String columnName = tableColumn.getString(KEY);
                //获取前端需要的真正的字段名称
                columnName = getRealColumnName(allOldColumnsName, newNameToOldName, columnName);

                String columnType = tableColumn.getString(TYPE);
                JSONObject jsonColumn = new JSONObject();
                jsonColumn.put(KEY, columnName);
                jsonColumn.put(TYPE, columnType);
                if (StringUtils.isNotEmpty(tableColumn.getString("isPart"))) {
                    jsonColumn.put("isPart", tableColumn.get("isPart"));
                }

                if (!(sourceColumns.get(0) instanceof String)) {
                    //这个是兼容原来的desc table 出来的结果 因为desc出来的不仅仅是字段名
                    for (int i = 0; i < sourceColumns.size(); i++) {
                        final JSONObject item = sourceColumns.getJSONObject(i);
                        if (item.get(KEY).equals(columnName) && item.containsKey("format")) {
                            jsonColumn.put("format", item.getString("format"));
                            break;
                        }
                    }
                }
                srcColumns.add(jsonColumn);
            }
        } catch (Exception ignore) {
            LOGGER.error("数据同步获取表字段异常 : {}", ignore.getMessage(), ignore);
            srcColumns = sourceColumns;
        }
        if (CollectionUtils.isNotEmpty(custColumns)) {
            srcColumns.addAll(custColumns);
        }
        sourceMap.put(COLUMN, srcColumns);
    }

    /**
     * 拿到真实的字段名
     * 判断 如果
     * @param allOldColumnsName  原有的所有字段的字段名
     * @param newNameToOldName   key是原有字段名的小写  value是原有字段名
     * @param columnName  元数据字段名
     * @return
     */
    private String getRealColumnName(List<String> allOldColumnsName, Map<String, String> newNameToOldName, String columnName) {
        if (allOldColumnsName.contains(columnName)){
            //认为字段名没有改动 直接返回
            return columnName;
        }

        String oldColumnName = newNameToOldName.get(columnName);
        if (StringUtils.isBlank(oldColumnName)){
            //认为字段名没有从大写变小写
            return columnName;
        }
        //字段名大写变小写了 所以返回原有字段名 保证前端映射无问题
        return oldColumnName;
    }

    /**
     * 这个方法 是对老数据中的字段做一下处理 会出来3个集合
     * @param sourceMap 源信息
     * @param custColumns 用户自定义字段
     * @param allOldColumnsName  老字段名称集合
     * @param newNameToOldName  新字段名称和老字段名字对应集合  key：字段名小写  value 原字段名 用处hive增加字段 字段名全小写导致对应关系丢失
     */
    private void getAllTypeColumnsMap(JSONObject sourceMap,List<JSONObject> custColumns,List<String> allOldColumnsName,Map<String,String> newNameToOldName ){
        JSONArray sourceColumns = sourceMap.getJSONArray(COLUMN);
        if (sourceColumns == null){
            return;
        }
        for (int i = 0; i < sourceColumns.size(); ++i) {
            JSONObject column = sourceColumns.getJSONObject(i);
            if (column.containsKey("value")) {
                custColumns.add(column);
                continue;
            }
            String key = column.getString(KEY);
            if (StringUtils.isBlank(key)){
                continue;
            }
            allOldColumnsName.add(key);
            newNameToOldName.put(key.toLowerCase(),key);
        }

    }

    /**
     * 查询表所属字段 可以选择是否需要分区字段
     * @param source
     * @param tableName
     * @param part 是否需要分区字段
     * @return
     * @throws Exception
     */
    private List<JSONObject> getTableColumnIncludePart(BatchDataSource source, String tableName, Boolean part, String schema)  {
        try {
            if (source == null) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
            }
            if (part ==null){
                part = false;
            }
            JSONObject dataJson = JSONObject.parseObject(source.getDataJson());
            Map<String, Object> kerberosConfig = fillKerberosConfig(source.getId());
            IClient iClient = ClientCache.getClient(source.getType());
            SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder()
                    .tableName(tableName)
                    .schema(schema)
                    .filterPartitionColumns(part)
                    .build();
            ISourceDTO iSourceDTO = SourceDTOType.getSourceDTO(dataJson, source.getType(), kerberosConfig, Maps.newHashMap());
            List<ColumnMetaDTO> columnMetaData = iClient.getColumnMetaData(iSourceDTO, sqlQueryDTO);
            List<JSONObject> list = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(columnMetaData)) {
                for (ColumnMetaDTO columnMetaDTO : columnMetaData) {
                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(columnMetaDTO));
                    jsonObject.put("isPart",columnMetaDTO.getPart());
                    list.add(jsonObject);
                }
            }
            return list;
        } catch (DtCenterDefException e) {
            throw e;
        } catch (Exception e) {
            throw new RdosDefineException(ErrorCode.GET_COLUMN_ERROR, e);
        }

    }

    /**
     * 下载检查kerberos配置
     *
     * @param sourceId
     * @return 返回该数据源的完整kerberos配置
     */
    public Map<String, Object> fillKerberosConfig(Long sourceId) {
        BatchDataSource source = dataSourceService.getOne(sourceId);
        Long tenantId = tenantService.getDtTenantId(source.getTenantId());
        JSONObject dataJson = JSON.parseObject(source.getDataJson());
        JSONObject kerberosConfig = dataJson.getJSONObject(KERBEROS_CONFIG);
        if (MapUtils.isNotEmpty(kerberosConfig)) {
            String localKerberosConf = getLocalKerberosConf(sourceId);
            downloadKerberosFromSftp(kerberosConfig.getString(KERBEROS_DIR), localKerberosConf, tenantId, dataJson.getTimestamp(KERBEROS_FILE_TIMESTAMP));
            return handleKerberos(source.getType(), kerberosConfig, localKerberosConf);
        }
        return new HashMap<>();
    }

    private String getLocalKerberosConf(Long sourceId) {
        String key = getSourceKey(sourceId);
        return environmentContext.getKerberosLocalPath() + File.separator + key;
    }

    private String getSourceKey(Long sourceId) {
        return Optional.ofNullable(sourceId).orElse(0L).toString();
    }

    private void downloadKerberosFromSftp(String kerberosFile, String localKerberosConf, Long tenantId, Timestamp kerberosFileTimestamp) {
        //需要读取配置文件
        Map<String, String> sftpMap =  clusterService.getComponentByTenantId(tenantId,EComponentType.SFTP.getTypeCode(), false,Map.class,null);
        try {
            KerberosConfigVerify.downloadKerberosFromSftp(kerberosFile, localKerberosConf, sftpMap, kerberosFileTimestamp);
        } catch (Exception e) {
            //允许下载失败
            LOGGER.info("download kerberosFile failed {}",kerberosFile, e);
        }
    }


    /**
     * kerberos配置预处理、替换相对路径为绝对路径等操作
     *
     * @param sourceType
     * @param kerberosMap
     * @param localKerberosConf
     * @return
     */
    private Map<String, Object> handleKerberos (Integer sourceType, Map<String, Object> kerberosMap, String localKerberosConf) {
        IKerberos kerberos = ClientCache.getKerberos(sourceType);
        HashMap<String, Object> tmpKerberosConfig = new HashMap<>(kerberosMap);
        try {
            kerberos.prepareKerberosForConnect(tmpKerberosConfig, localKerberosConf);
        } catch (Exception e) {
            throw new RdosDefineException("common-loader中kerberos配置文件处理失败", e);
        }
        return tmpKerberosConfig;
    }

    /**
     * 查找所有产品提交的任务
     *
     * @param searchVO
     * @return
     */
    public List<BatchAllProductGlobalReturnVO> allProductGlobalSearch(AllProductGlobalSearchVO searchVO) {
        BatchTask batchTask = batchTaskDao.getOne(searchVO.getTaskId());

        if (batchTask == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        if (batchTask.getTaskType().intValue() == EScheduleJobType.VIRTUAL.getVal().intValue()) {
            throw new RdosDefineException(ErrorCode.VIRTUAL_TASK_UNSUPPORTED_OPERATION);
        }

        // 过滤掉已经依赖的任务
        final List<BatchTaskTask> taskTasks = this.batchTaskTaskService.getByParentTaskId(searchVO.getTaskId());
        final List<Long> excludeIds = new ArrayList<>(taskTasks.size());
        excludeIds.add(searchVO.getTaskId());
        taskTasks.forEach(taskTask -> excludeIds.add(taskTask.getTaskId()));

        List<ScheduleTaskShade> scheduleTaskShadeList = taskService.findTaskByTaskName(searchVO.getTaskName(), searchVO.getSelectTenantId(), searchVO.getUserId());
        List<ScheduleTaskShade> filterTask = scheduleTaskShadeList.stream().filter(scheduleTask -> !excludeIds.contains(scheduleTask.getTaskId())).collect(Collectors.toList());
        Map<Long, Tenant> tenantMap = tenantService.listAllTenant().stream().collect(Collectors.toMap(Tenant::getId, g -> (g)));

        List<BatchAllProductGlobalReturnVO> voList = Lists.newArrayList();
        for (ScheduleTaskShade scheduleTaskShade : filterTask) {
            BatchAllProductGlobalReturnVO vo = new BatchAllProductGlobalReturnVO();
            vo.setTaskId(scheduleTaskShade.getTaskId());
            vo.setTaskName(scheduleTaskShade.getName());

            Tenant tenant = tenantMap.get(scheduleTaskShade.getTenantId());

            if (tenant != null) {
                vo.setTenantId(tenant.getId());
                vo.setTenantName(tenant.getTenantName());
            }

            voList.add(vo);
        }
        return voList;
    }

    /**
     * 冻结任务
     *
     * @param taskId         任务编号
     * @param scheduleStatus 调度状态
     * @param userId         用户ID
     */
    public void frozenTask(Long taskId, Integer scheduleStatus, Long userId) {
        BatchTask batchTask = getOneWithError(taskId);
        EScheduleStatus targetStatus = EScheduleStatus.getStatus(scheduleStatus);
        if (Objects.isNull(targetStatus)) {
            throw new RdosDefineException("任务状态参数非法", ErrorCode.INVALID_PARAMETERS);
        }
        batchTask.setModifyUserId(userId);
        batchTask.setScheduleStatus(scheduleStatus);
        batchTaskDao.update(batchTask);
        taskService.frozenTask(Lists.newArrayList(taskId), scheduleStatus);
    }

}
