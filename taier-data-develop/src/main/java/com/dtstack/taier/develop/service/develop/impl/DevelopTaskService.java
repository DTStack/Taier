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
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.client.IKerberos;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.enums.*;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.kerberos.KerberosConfigVerify;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.common.util.Strings;
import com.dtstack.taier.dao.domain.*;
import com.dtstack.taier.dao.mapper.DevelopTaskMapper;
import com.dtstack.taier.develop.dto.devlop.TaskCatalogueVO;
import com.dtstack.taier.develop.dto.devlop.TaskCheckResultVO;
import com.dtstack.taier.develop.dto.devlop.TaskGetNotDeleteVO;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import com.dtstack.taier.develop.enums.develop.FlinkVersion;
import com.dtstack.taier.develop.enums.develop.SourceDTOType;
import com.dtstack.taier.develop.enums.develop.TaskCreateModelType;
import com.dtstack.taier.develop.enums.develop.WorkFlowScheduleConfEnum;
import com.dtstack.taier.develop.mapstruct.vo.TaskDirtyDataManageTransfer;
import com.dtstack.taier.develop.mapstruct.vo.TaskMapstructTransfer;
import com.dtstack.taier.develop.service.console.TenantService;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.develop.ITaskSaver;
import com.dtstack.taier.develop.service.develop.TaskConfiguration;
import com.dtstack.taier.develop.service.develop.saver.AbstractTaskSaver;
import com.dtstack.taier.develop.service.schedule.TaskService;
import com.dtstack.taier.develop.service.task.TaskTemplateService;
import com.dtstack.taier.develop.service.user.UserService;
import com.dtstack.taier.develop.utils.develop.sync.format.ColumnType;
import com.dtstack.taier.develop.vo.develop.query.AllProductGlobalSearchVO;
import com.dtstack.taier.develop.vo.develop.query.TaskDirtyDataManageVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopAllProductGlobalReturnVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopTaskGetComponentVersionResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopTaskTypeVO;
import com.dtstack.taier.develop.vo.develop.result.job.TaskProperties;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.dto.schedule.SavaTaskDTO;
import com.dtstack.taier.scheduler.dto.schedule.ScheduleTaskShadeDTO;
import com.dtstack.taier.scheduler.enums.ESchedulePeriodType;
import com.dtstack.taier.scheduler.impl.pojo.ParamTaskAction;
import com.dtstack.taier.scheduler.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class DevelopTaskService extends ServiceImpl<DevelopTaskMapper, Task> {

    public static Logger LOGGER = LoggerFactory.getLogger(DevelopTaskService.class);

    private static final ObjectMapper objMapper = new ObjectMapper();

    @Autowired
    private TenantService tenantService;

    @Autowired
    private DevelopTaskMapper developTaskMapper;

    @Autowired
    private TaskTemplateService taskTemplateService;

    @Autowired
    private DevelopTaskResourceService developTaskResourceService;

    @Autowired
    private TaskDirtyDataManageService taskDirtyDataManageService;

    @Autowired
    private DevelopTaskParamService developTaskParamService;

    @Autowired
    private DevelopTaskTaskService developTaskTaskService;

    @Autowired
    private DatasourceService dataSourceService;

    @Autowired
    private DevelopCatalogueService developCatalogueService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private DevelopSysParamService developSysParamService;

    @Autowired
    private DevelopResourceService developResourceService;

    @Autowired
    private DevelopTaskResourceShadeService developTaskResourceShadeService;

    @Autowired
    private HadoopJobExeService hadoopJobExeService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ScheduleActionService actionService;

    @Autowired
    private FlinkTaskService flinkTaskService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private TaskConfiguration taskConfiguration;

    @Autowired
    private ScheduleTaskTaskService scheduleTaskTaskService;

    @Autowired
    private DevelopFunctionService developFunctionService;

    @Autowired
    private ScheduleDictService scheduleDictService;

    private static final String KERBEROS_CONFIG = "kerberosConfig";

    /**
     * kerberos认证文件在 ftp上的相对路径
     */
    private static final String KERBEROS_DIR = "kerberosDir";

    /**
     * Kerberos 文件上传的时间戳
     */
    private static final String KERBEROS_FILE_TIMESTAMP = "kerberosFileTimestamp";

    private static final Integer IS_FILE = 1;

    public static final String HADOOP_CONFIG = "hadoopConfig";

    /**
     * 按id查询任务详情
     *
     * @param taskVO
     * @return
     */
    public TaskVO getTaskById(TaskVO taskVO) {
        Task task = getOne(taskVO.getId());
        if (null == task) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        TaskMapstructTransfer.INSTANCE.taskToTaskVO(task, taskVO);
        if (EScheduleJobType.FLINK_SQL.getType().equals(task.getTaskType())) {
            taskVO.setSource(flinkTaskService.dealWithSourceName(task.getSourceStr()));
            taskVO.setSink(flinkTaskService.dealWithSourceName(task.getTargetStr()));
            taskVO.setSide(flinkTaskService.dealWithSourceName(task.getSideStr()));
        } else {
            taskVO.setSourceMap(JSON.parseObject(taskVO.getSourceStr(), Map.class));
            taskVO.setTargetMap(JSON.parseObject(taskVO.getTargetStr(), Map.class));
            taskVO.setSettingMap(JSON.parseObject(taskVO.getSettingStr(), Map.class));
            setTaskVariables(taskVO, taskVO.getId());
            JSONObject jsonObject = JSONObject.parseObject(taskVO.getSqlText());
            taskVO.setSqlText(String.valueOf(jsonObject.get("job")));
            taskVO.setDependencyTasks(buildDependTaskList(task.getId()));
        }
        if (task.getFlowId() != null && task.getFlowId() > 0) {
            Task flow = getOne(task.getFlowId());
            if (flow != null) {
                taskVO.setFlowName(flow.getName());
            }
        }
        List<Long> resourceIds = developTaskResourceService.getResourceIdList(taskVO.getId(), ResourceRefType.MAIN_RES.getType());
        taskVO.setResourceIdList(resourceIds);
        TaskDirtyDataManage oneByTaskId = taskDirtyDataManageService.getOneByTaskId(task.getId());
        taskVO.setTaskDirtyDataManageVO(TaskDirtyDataManageTransfer.INSTANCE.taskDirtyDataManageToTaskDirtyDataManageVO(oneByTaskId));
        taskVO.setOpenDirtyDataManage(taskVO.getTaskDirtyDataManageVO() != null);
        return taskVO;
    }

    /**
     * 构建依赖任务信息列表
     *
     * @param taskId
     * @return
     */
    private List<TaskVO> buildDependTaskList(Long taskId) {
        List<DevelopTaskTask> taskTasks = developTaskTaskService.getAllParentTask(taskId);
        List<Long> parentTaskIds = taskTasks.stream()
                .map(DevelopTaskTask::getParentTaskId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(parentTaskIds)) {
            return new ArrayList<>();
        }
        List<ScheduleTaskShade> parentTaskShades = taskService.findTaskByTaskIds(parentTaskIds);
        if (CollectionUtils.isEmpty(parentTaskShades)) {
            return new ArrayList<>();
        }
        List<Long> tenantIds = parentTaskShades.stream().
                map(ScheduleTaskShade::getTenantId)
                .collect(Collectors.toList());
        Map<Long, Tenant> tenantMap = tenantService.getTenants(tenantIds)
                .stream().collect(Collectors.toMap(Tenant::getId, Function.identity()));

        return parentTaskShades.stream().map(pt -> {
            TaskVO taskInfo = new TaskVO();
            taskInfo.setId(pt.getTaskId());
            taskInfo.setName(pt.getName());
            taskInfo.setTenantId(pt.getTenantId());
            Tenant tenant = tenantMap.get(pt.getTenantId());
            taskInfo.setTenantName(tenant == null ? "" : tenant.getTenantName());
            return taskInfo;
        }).collect(Collectors.toList());
    }

    private void setTaskVariables(TaskVO taskVO, final Long taskId) {
        List<DevelopTaskParam> taskParams = this.developTaskParamService.getTaskParam(taskId);
        List<Map<String, Object>> mapParams = new ArrayList<>();
        if (taskParams != null) {
            for (DevelopTaskParam taskParam : taskParams) {
                Map<String, Object> map = new HashMap();
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
    public Task checkIsLoop(Long taskId,
                            Long dependencyTaskId) {
        HashSet<Long> node = Sets.newHashSet(taskId);
        Long loopTaskId = isHasLoop(dependencyTaskId, node);
        if (loopTaskId == 0L) {
            return null;
        }
        return developTaskMapper.selectById(loopTaskId);
    }

    public Long isHasLoop(final Long parentTaskId, final HashSet<Long> node) {
        HashSet<Long> loopNode = new HashSet<>(node.size() + 1);
        loopNode.addAll(node);
        loopNode.add(parentTaskId);
        //出现闭环则返回
        if (loopNode.size() == node.size()) {
            return parentTaskId;
        }

        List<DevelopTaskTask> taskTasks = developTaskTaskService.getAllParentTask(parentTaskId);
        if (CollectionUtils.isEmpty(taskTasks)) {
            return 0L;
        }
        for (DevelopTaskTask subTask : taskTasks) {
            Long loopTaskId = isHasLoop(subTask.getParentTaskId(), loopNode);
            if (loopTaskId != 0L) {
                return loopTaskId;
            }
        }
        return 0L;
    }


    /**
     * 根据任务状态
     * 判断当前任务是否可以启动
     *
     * @param task
     * @return
     */
    public boolean checkTaskCanRunByStatus(Task task) {
        Integer taskStatus;
        try {
            //todo 有问题
            taskStatus = task.getScheduleStatus();
        } catch (Exception e) {
            LOGGER.error("从Engine查询任务状态异常,{}", e.getMessage(), e);
            throw new RdosDefineException(String.format("从Engine查询任务状态异常,Caused by: %s", e.getMessage()), e);
        }
        return taskStatus == null || TaskStatus.CAN_RUN_STATUS.contains(taskStatus);
    }

    @Transactional(rollbackFor = Exception.class)
    public TaskCheckResultVO publishTask(Long id, Long userId) {
        Task task = getOne(id);
        if (Objects.equals(task.getTaskType(), EScheduleJobType.DATA_ACQUISITION.getVal())) {
            if (!checkTaskCanRunByStatus(task)) {
                throw new RdosDefineException("任务状态未提交发布");
            }
        }

        // 需要发布的任务集合
        List<Task> tasks = Lists.newArrayList();
        // 工作流下所有子任务置为发布状态
        if (EScheduleJobType.WORK_FLOW.getVal().equals(task.getTaskType())) {
            final List<Task> subTasks = this.getFlowWorkSubTasks(id);
            tasks.addAll(subTasks);

            HashMap<Long, List<Long>> relations = JSON.parseObject(task.getSqlText(), new TypeReference<HashMap<Long, List<Long>>>() {
            });
            final List<String> noParents = Lists.newArrayList();
            for (final Task taskOne : subTasks) {
                //没有父节点
                if (CollectionUtils.isEmpty(relations.get(taskOne.getId()))) {
                    noParents.add(taskOne.getName());
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
        return publishTaskInfo(task, userId);

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
            Task task = developTaskMapper.selectById(taskId);
            if (Objects.nonNull(task)) {
                throw new RdosDefineException(String.format("%s任务发生依赖闭环", task.getName()));
            }
        }
        node.add(taskId);
        for (Long j : nodeMap.get(taskId)) {
            mapDfs(j, node, nodeMap);
        }
    }


    /**
     * 发布任务至engine
     *
     * @param userId 用户id
     * @return 发布结果
     */
    public TaskCheckResultVO publishTaskInfo(Task task, Long userId) {
        TaskCheckResultVO checkResultVO = new TaskCheckResultVO();

        // 发布任务中所有的依赖关系
        List<ScheduleTaskTaskShade> allTaskTaskList = new ArrayList<>();

        if (EScheduleJobType.WORK_FLOW.getType().equals(task.getTaskType())) {

            final List<Task> subTasks = getFlowWorkSubTasks(task.getId());
            subTasks.add(task);
            if (CollectionUtils.isNotEmpty(subTasks)) {
                for (Task subTask : subTasks) {
                    try {
                        // 构建要发布的任务列表
                        ScheduleTaskShade scheduleTasks = buildScheduleTaskShadeDTO(subTask, allTaskTaskList);

                        // 提交任务参数信息并保存任务记录和更新任务状态
                        sendTaskStartTrigger(subTask.getId(), userId, scheduleTasks);


                        Map<String, ScheduleTaskTaskShade> keys = new HashMap<>(16);
                        // 判断任务依赖关系
                        if (CollectionUtils.isNotEmpty(allTaskTaskList)) {
                            // 去重
                            for (ScheduleTaskTaskShade scheduleTaskTaskShade : allTaskTaskList) {
                                keys.put(String.format("%s.%s.%s", scheduleTaskTaskShade.getTaskId(), scheduleTaskTaskShade.getParentTaskId(), scheduleTaskTaskShade.getTenantId()), scheduleTaskTaskShade);
                                Preconditions.checkNotNull(scheduleTaskTaskShade.getTaskId());
                                // 清除原来关系
                                scheduleTaskTaskService.deleteByTaskId(scheduleTaskTaskShade.getTaskId());
                            }
                            // 保存现有任务关系
                            for (ScheduleTaskTaskShade taskTaskShade : keys.values()) {
                                scheduleTaskTaskService.insert(taskTaskShade);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("send task error {} ", subTask.getName(), e);
                        throw new RdosDefineException(String.format("任务提交异常：%s", e.getMessage()), e);
                    }
                    LOGGER.info("待发布任务参数提交完毕");
                }
            }


        } else {
            try {
                // 构建要发布的任务列表
                ScheduleTaskShade scheduleTasks = buildScheduleTaskShadeDTO(task, allTaskTaskList);

                // 提交任务参数信息并保存任务记录和更新任务状态
                sendTaskStartTrigger(task.getId(), userId, scheduleTasks);

            } catch (Exception e) {
                LOGGER.error("send task error {} ", task.getName(), e);
                throw new RdosDefineException(String.format("任务提交异常：%s", e.getMessage()), e);
            }

            LOGGER.info("待发布任务参数提交完毕");
            return checkResultVO;
        }
        return checkResultVO;
    }

    /**
     * 发送task 执行任务全部信息
     */
    public void sendTaskStartTrigger(Long taskId, Long userId, ScheduleTaskShade scheduleTasks) throws Exception {
        Task task = developTaskMapper.selectById(taskId);
        if (task == null) {
            throw new RdosDefineException("can not find task by id:" + taskId);
        }
        String extraInfo = getExtraInfo(task, userId);
        if (Objects.equals(task.getTaskType(), EScheduleJobType.DATA_ACQUISITION.getValue())) {
            ParamTaskAction paramTaskAction = new ParamTaskAction();
            paramTaskAction.setIsRestart(0);
            scheduleTasks.setExtraInfo(extraInfo);
            if (!scheduleTasks.getScheduleConf().contains("periodType")) {
                JSONObject scheduleConf = JSONObject.parseObject(scheduleTasks.getScheduleConf());
                scheduleConf.put("periodType", ESchedulePeriodType.DAY.getVal());
                scheduleTasks.setScheduleConf(JSON.toJSONString(scheduleConf));
            }
            paramTaskAction.setTask(scheduleTasks);
        } else if (EComputeType.BATCH == EScheduleJobType.getByTaskType(task.getTaskType()).getComputeType()) {
            JSONObject scheduleConf = JSONObject.parseObject(scheduleTasks.getScheduleConf());
            scheduleTasks.setPeriodType(scheduleConf.getInteger("periodType"));
        }
        SavaTaskDTO savaTaskDTO = new SavaTaskDTO();
        scheduleTasks.setExtraInfo(extraInfo);
        savaTaskDTO.setScheduleTaskShade(scheduleTasks);
        List<DevelopTaskTask> allParentTask = developTaskTaskService.getAllParentTask(taskId);
        savaTaskDTO.setParentTaskIdList(allParentTask.stream().map(DevelopTaskTask::getParentTaskId).collect(Collectors.toList()));
        this.taskService.saveTask(savaTaskDTO);
    }


    /**
     * 初始化engine info接口extraInfo信息
     *
     * @param task
     * @param userId
     * @return info信息
     * @throws Exception
     */
    private String getExtraInfo(Task task, Long userId) throws Exception {
        String extraInfo = "";
        Long taskId = task.getId();
        Map<String, Object> actionParam = new HashMap<>();
        List<DevelopTaskParam> taskParam = developTaskParamService.getTaskParam(task.getId());
        ITaskSaver taskSaver = taskConfiguration.getSave(task.getTaskType());
        if (EScheduleJobType.SYNC.getType().equals(task.getTaskType())) {
            hadoopJobExeService.readyForTaskStartTrigger(actionParam, task.getTenantId(), task);
            JSONObject confProp = new JSONObject();
            taskDirtyDataManageService.buildTaskDirtyDataManageArgs(task.getTaskType(), task.getId(), confProp);
            actionParam.put("confProp", JSON.toJSONString(confProp));
        } else {
            String sqlText = taskSaver.processScheduleRunSqlText(task.getTenantId(), task.getTaskType(), task.getSqlText());
            actionParam.put("sqlText", sqlText);
        }
        actionParam.put("taskId", taskId);
        actionParam.put("taskType", task.getTaskType());
        actionParam.put("name", task.getName());
        actionParam.put("computeType", task.getComputeType());
        actionParam.put("tenantId", task.getTenantId());
        actionParam.put("isFailRetry", false);
        actionParam.put("maxRetryNum", 0);
        actionParam.put("taskParamsToReplace", JSON.toJSONString(taskParam));
        actionParam.put("userId", userId);
        actionParam.put("taskParams", PublicUtil.propertiesRemoveEmpty(task.getTaskParams()));

        // 出错重试配置,兼容之前的任务，没有这个参数则默认重试
        final JSONObject scheduleConf = JSON.parseObject(task.getScheduleConf());
        if (scheduleConf != null && scheduleConf.containsKey("isFailRetry")) {
            actionParam.put("isFailRetry", scheduleConf.getBooleanValue("isFailRetry"));
            if (scheduleConf.getBooleanValue("isFailRetry")) {
                final int maxRetryNum = scheduleConf.getIntValue("maxRetryNum") == 0 ? 3 : scheduleConf.getIntValue("maxRetryNum");
                actionParam.put("maxRetryNum", maxRetryNum);
            } else {
                actionParam.put("maxRetryNum", 0);
            }
        }
        extraInfo = objMapper.writeValueAsString(actionParam);
        extraInfo = extraInfo.replaceAll("\r\n", System.getProperty("line.separator"));
        return extraInfo;
    }

    /**
     * 构建一个要发布到engine的任务DTO {@link ScheduleTaskShadeDTO}
     *
     * @param task 要发布的任务集合
     * @return 调度任务DTO
     */
    private ScheduleTaskShade buildScheduleTaskShadeDTO(final Task task, List<ScheduleTaskTaskShade> allTaskTaskList) {
        if (task.getId() <= 0) {
            //只有异常情况才会走到该逻辑
            throw new RdosDefineException("task id can't be 0", ErrorCode.SERVER_EXCEPTION);
        }

        //查询关联任务
        List<ScheduleTaskTaskShade> batchTaskTaskList = scheduleTaskTaskService.getAllParentTask(task.getId());
        // 处理任务之间的依赖关系
        if (CollectionUtils.isNotEmpty(batchTaskTaskList)) {
            for (ScheduleTaskTaskShade batchTaskTask : batchTaskTaskList) {
                batchTaskTask.setTenantId(task.getTenantId());
            }
            allTaskTaskList.addAll(batchTaskTaskList);
        }
        ScheduleTaskShade scheduleTaskShadeDTO = new ScheduleTaskShade();
        BeanUtils.copyProperties(task, scheduleTaskShadeDTO);

        scheduleTaskShadeDTO.setTaskId(task.getId());
        scheduleTaskShadeDTO.setTenantId(scheduleTaskShadeDTO.getTenantId());
        if (Objects.equals(task.getTaskType(), EScheduleJobType.SYNC.getVal())
                && StringUtils.isNotEmpty(task.getScheduleConf())) {
            JSONObject scheduleConfig = JSONObject.parseObject(task.getScheduleConf());
            if (scheduleConfig != null) {
                scheduleTaskShadeDTO.setPeriodType(scheduleConfig.getInteger("periodType"));
            } else {
                scheduleTaskShadeDTO.setPeriodType(2);
            }
        }
        return scheduleTaskShadeDTO;
    }

    /**
     * 新增/更新任务
     *
     * @param taskResourceParam
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public TaskVO addOrUpdateTask(TaskResourceParam taskResourceParam) {
        ITaskSaver taskSaver = taskConfiguration.getSave(taskResourceParam.getTaskType());
        return taskSaver.addOrUpdate(taskResourceParam);
    }

    /**
     * 任务名称校验
     *
     * @param taskName
     * @param tenantId
     * @return
     */
    public Boolean checkTaskNameRepeat(String taskName, Long tenantId) {
        if (StringUtils.isBlank(taskName)) {
            throw new RdosDefineException("名称不能为空", ErrorCode.INVALID_PARAMETERS);
        }
        Task task = developTaskMapper.selectOne(Wrappers.lambdaQuery(Task.class)
                .eq(Task::getName, taskName)
                .eq(Task::getTenantId, tenantId)
                .last("limit 1"));
        if (ObjectUtils.isNotEmpty(task)) {
            throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
        }
        return true;
    }

    /**
     * 新增/更新任务
     * 内部使用 不对外提供
     *
     * @param taskVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Task updateTask(TaskVO taskVO, Boolean taskParam) {
        if (StringUtils.isBlank(taskVO.getName())) {
            throw new RdosDefineException("名称不能为空", ErrorCode.INVALID_PARAMETERS);
        }

        taskVO.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        Task task = developTaskMapper.selectOne(Wrappers.lambdaQuery(Task.class)
                .eq(Task::getName, taskVO.getName())
                .eq(Task::getTenantId, taskVO.getTenantId())
                .last("limit 1"));

        if (taskVO.getId() != null && taskVO.getId() > 0) {//update
            if (task != null && task.getName().equals(taskVO.getName()) && !task.getId().equals(taskVO.getId())) {
                throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
            }
            developTaskParamService.checkParams(taskVO.getSqlText(), taskVO.getTaskVariables());
            updateTask(taskVO);
        } else {
            if (task != null) {
                throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
            }
            addTask(taskVO);
        }
        if (BooleanUtils.isTrue(taskParam)) {
            developTaskParamService.addOrUpdateTaskParam(taskVO.getTaskVariables(), taskVO.getId());
        }

        // 添加任务依赖关系
        developTaskTaskService.addOrUpdateTaskTask(taskVO.getId(), taskVO.getDependencyTasks());

        if (!taskVO.getUpdateSource()) {
            return taskVO;
        }
        return taskVO;
    }

    /**
     * 新增任务
     *
     * @param taskVO 任务信息
     */
    private void addTask(TaskVO taskVO) {
        taskVO.setJobId(actionService.generateUniqueSign());
        taskVO.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));

        if (StringUtils.isBlank(taskVO.getTaskParams())) {
            TaskTemplate taskTemplate = taskTemplateService.getTaskTemplate(TaskTemplateType.TASK_PARAMS.getType(), taskVO.getTaskType(), taskVO.getComponentVersion());
            String content = taskTemplate == null ? "" : taskTemplate.getContent();
            taskVO.setTaskParams(content);
        }
        taskVO.setTenantId(taskVO.getTenantId());
        taskVO.setScheduleStatus(EScheduleStatus.NORMAL.getVal());
        taskVO.setScheduleConf(StringUtils.isBlank(taskVO.getScheduleConf()) ? AbstractTaskSaver.DEFAULT_SCHEDULE_CONF : taskVO.getScheduleConf());
        taskVO.setVersion(Objects.isNull(taskVO.getVersion()) ? 0 : taskVO.getVersion());
        taskVO.setSqlText(createAnnotationText(taskVO));
        taskVO.setMainClass(Objects.isNull(taskVO.getMainClass()) ? "" : taskVO.getMainClass());
        taskVO.setTaskDesc(Objects.isNull(taskVO.getTaskDesc()) ? "" : taskVO.getTaskDesc());
        taskVO.setSubmitStatus(0);
        developTaskMapper.insert(taskVO);
    }

    /**
     * 转化环境参数，不同版本之间切换需要刷新环境参数信息
     *
     * @param before       转化前的 flink 版本
     * @param after        转化后的 flink 版本
     * @param paramsBefore 环境参数
     * @return 转化后的环境参数
     */
    public String convertParams(FlinkVersion before, FlinkVersion after, String paramsBefore, Integer taskType) {
        // 版本一致不需要进行转换
        if (before.equals(after)) {
            return paramsBefore;
        }
        return taskTemplateService.getTaskTemplate(TaskTemplateType.TASK_PARAMS.getType(), taskType, after.getType()).getContent();
    }

    /**
     * 修改任务
     *
     * @param taskVO 任务信息
     */
    private void updateTask(TaskVO taskVO) {
        Task specialTask = developTaskMapper.selectById(taskVO.getId());
        if (specialTask == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        // 转换环境参数
        String convertParams = convertParams(FlinkVersion.getVersion(specialTask.getComponentVersion()),
                FlinkVersion.getVersion(taskVO.getComponentVersion()),
                taskVO.getTaskParams(), taskVO.getTaskType());
        taskVO.setTaskParams(convertParams);
        Task specialTask1 = new Task();
        TaskMapstructTransfer.INSTANCE.taskVOTOTask(taskVO, specialTask1);
        developTaskMapper.updateById(specialTask1);
    }


    public Map<String, Object> getTargetMap(Map<String, Object> targetMap) {
        Map<String, Object> map = new HashMap<>(4);
        map.put("type", targetMap.get("type"));
        map.put("sourceId", targetMap.get("sourceId"));
        map.put("name", targetMap.get("name"));
        return map;
    }

    /**
     * 向导模式转模版
     *
     * @param param
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public TaskCatalogueVO guideToTemplate(final TaskResourceParam param) {
        final Task task = this.developTaskMapper.selectById(param.getId());
        TaskVO taskVO = new TaskVO();
        TaskMapstructTransfer.INSTANCE.taskToTaskVO(task, taskVO);
        taskVO.setCreateModel(TaskCreateModelType.TEMPLATE.getType());
        if (taskVO.getTaskType().equals(EScheduleJobType.FLINK_SQL.getVal())) {
            taskVO.setSqlText(flinkTaskService.generateCreateFlinkSql(task));
        } else {
            JSONObject sqlJson = new JSONObject();
            if (StringUtils.isNotBlank(task.getSqlText())) {
                JSONObject jsonObject = JSON.parseObject(task.getSqlText());
                sqlJson.put("job", jsonObject != null ? jsonObject.get("job") : "");
            }
            sqlJson.put("createModel", TaskCreateModelType.TEMPLATE.getType());

            taskVO.setSqlText(sqlJson.toJSONString());
        }
        this.updateTask(taskVO, true);
        TaskCatalogueVO taskCatalogueVO = new TaskCatalogueVO(param, taskVO.getNodePid());
        TaskDirtyDataManage oneByTaskId = taskDirtyDataManageService.getOneByTaskId(task.getId());
        if (oneByTaskId != null) {
            TaskDirtyDataManageVO taskDirtyDataManageVO = TaskDirtyDataManageTransfer.INSTANCE.taskDirtyDataManageToTaskDirtyDataManageVO(oneByTaskId);
            taskVO.setTaskDirtyDataManageVO(taskDirtyDataManageVO);
            taskVO.setOpenDirtyDataManage(true);
        }
        return taskCatalogueVO;
    }

    /**
     * 创建任务
     *
     * @param task
     * @return
     */
    private String createAnnotationText(TaskVO task) {
        if (StringUtils.isNotBlank(task.getSqlText())) {
            return task.getSqlText();
        }
        String ENTER = "\n";
        String NOTE_SIGN;
        // 需要代码注释模版的任务类型
        Set<Integer> shouldNoteSqlTypes = Sets.newHashSet(EScheduleJobType.SPARK_SQL.getVal(), EScheduleJobType.HIVE_SQL.getVal());

        StringBuilder sb = new StringBuilder();
        if (shouldNoteSqlTypes.contains(task.getTaskType())) {
            NOTE_SIGN = "-- ";
        } else {
            sb.append(StringUtils.isBlank(task.getSqlText()) ? "" : task.getSqlText());
            return sb.toString();
        }
        String type = EScheduleJobType.getByTaskType(task.getTaskType()).getName();
        //包括任务名称、任务类型、作者、创建时间、描述；
        sb.append(NOTE_SIGN).append("name ").append(task.getName()).append(ENTER);
        sb.append(NOTE_SIGN).append("type ").append(type).append(ENTER);
        sb.append(NOTE_SIGN).append("author ").append(userService.getUserName(task.getCreateUserId())).append(ENTER);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sb.append(NOTE_SIGN).append("create time ").append(sdf.format(System.currentTimeMillis())).append(ENTER);
        sb.append(NOTE_SIGN).append("desc ").append(StringUtils.isBlank(task.getTaskDesc()) ? "" : task.getTaskDesc().replace(ENTER, " ")).append(ENTER);
        sb.append(StringUtils.isBlank(task.getSqlText()) ? "" : task.getSqlText());
        return sb.toString();
    }

    /**
     * 删除任务
     *
     * @param taskId 任务id
     * @param userId 用户id
     * @return
     */
    @Transactional
    public Long deleteTask(Long taskId, Long userId) {

        final Task task = this.developTaskMapper.selectById(taskId);
        if (task == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        // 判断该任务是否有子任务(调用engine接口) 工作流不需要判断
        if (task.getFlowId() == 0) {
            List<TaskGetNotDeleteVO> notDeleteTaskVOS = getChildTasks(taskId);
            if (CollectionUtils.isNotEmpty(notDeleteTaskVOS)) {
                throw new RdosDefineException("(当前任务被其他任务依赖)", ErrorCode.CAN_NOT_DELETE_TASK);
            }
        }

        final ScheduleTaskShade dbTask = this.taskService.findTaskByTaskId(taskId);
        if (task.getFlowId() == 0 && Objects.nonNull(dbTask) &&
                task.getScheduleStatus().intValue() == EScheduleStatus.NORMAL.getVal().intValue()) {
            throw new RdosDefineException("(当前任务未被冻结)", ErrorCode.CAN_NOT_DELETE_TASK);
        }

        if (task.getTaskType().intValue() == EScheduleJobType.WORK_FLOW.getVal()) {
            final List<Task> developTasks = this.getFlowWorkSubTasks(taskId);
            //删除所有子任务相关
            developTasks.forEach(task1 -> this.deleteTaskInfos(task1.getId(), userId));
        }

        //删除工作流中的子任务同时删除被依赖的关系
        if (task.getFlowId() > 0) {
            this.developTaskTaskService.deleteTaskTaskByParentId(task.getId());
        }

        //删除任务
        this.deleteTaskInfos(taskId, userId);

        return taskId;
    }

    public void deleteTaskInfos(Long taskId, Long userId) {
        //软删除任务记录
        this.lambdaUpdate()
                .eq(Task::getId, taskId).set(Task::getIsDeleted, Deleted.DELETED.getStatus())
                .set(Task::getModifyUserId, userId)
                .set(Task::getGmtModified, Timestamp.valueOf(LocalDateTime.now()))
                .update();
        //删除任务的依赖关系
        this.developTaskTaskService.deleteTaskTaskByTaskId(taskId);
        //删除关联的函数资源
        this.developTaskResourceService.deleteTaskResource(taskId);
        this.developTaskResourceShadeService.deleteByTaskId(taskId);
        //删除关联的参数表信息
        this.developTaskParamService.deleteTaskParam(taskId);
        //删除发布相关的数据
        this.taskService.deleteTask(taskId, userId);
        taskDirtyDataManageService.deleteByTaskId(taskId);
    }


    public Task getDevelopTaskById(final long taskId) {
        return this.developTaskMapper.selectById(taskId);
    }


    /**
     * 判断任务是否可以发布
     * 当前只对sql任务做判断--不允许提交空的sql任务
     *
     * @return
     */
    private boolean checkTaskCanSubmit(final Task task) {
        if (task.getTaskType().equals(EScheduleJobType.SPARK_SQL.getVal()) && StringUtils.isEmpty(task.getSqlText())) {
            throw new RdosDefineException(task.getName() + "任务的SQL为空", ErrorCode.TASK_CAN_NOT_SUBMIT);
        } else if (task.getTaskType().equals(EScheduleJobType.SYNC.getVal()) || task.getTaskType().equals(EScheduleJobType.DATA_ACQUISITION.getVal())) {
            if (StringUtils.isBlank(task.getSqlText())) {
                throw new RdosDefineException(task.getName() + "任务配置信息为空", ErrorCode.TASK_CAN_NOT_SUBMIT);
            }
            final String sqlText = task.getSqlText();
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

    /**
     * 数据开发-获取所有系统参数
     */
    public Collection<DevelopSysParameter> getSysParams() {
        return this.developSysParamService.listSystemParam();
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
            DevelopCatalogue developCatalogue = developCatalogueService.getByPidAndName(tenantId, pid.longValue(), name);
            if (developCatalogue != null) {
                throw new RdosDefineException("文件夹已存在", ErrorCode.NAME_ALREADY_EXIST);
            }
        } else {
            final Object obj;
            if (type.equals(CatalogueType.TASK_DEVELOP.name())) {
                obj = developTaskMapper.selectOne(Wrappers.lambdaQuery(Task.class)
                        .eq(Task::getName, name)
                        .eq(Task::getTenantId, tenantId)
                        .last("limit 1"));
            } else if (type.equals(CatalogueType.RESOURCE_MANAGER.name())) {
                obj = developResourceService.listByNameAndTenantId(tenantId, name);
            } else if (type.equals(CatalogueType.FUNCTION_MANAGER.name())) {
                obj = developFunctionService.listByNameAndTenantId(tenantId, name);
            } else {
                throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
            }

            if (obj instanceof Task) {
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
    public List<Task> getFlowWorkSubTasks(final Long taskId) {
        Task task = new Task();
        task.setIsDeleted(Deleted.NORMAL.getStatus());
        task.setFlowId(taskId);
        LambdaQueryWrapper<Task> taskLambdaQueryWrapper = Wrappers.lambdaQuery();
        taskLambdaQueryWrapper.eq(Task::getIsDeleted, Deleted.NORMAL.getStatus());
        taskLambdaQueryWrapper.eq(Task::getFlowId, taskId);
        taskLambdaQueryWrapper.last("limit 1000");
        List<Task> developTasks = this.developTaskMapper.selectList(taskLambdaQueryWrapper);
        return developTasks;
    }

    public Task getByName(String name, Long tenantId) {
        return this.developTaskMapper.selectOne(Wrappers.lambdaQuery(Task.class)
                .eq(Task::getName, name)
                .eq(Task::getTenantId, tenantId));
    }

    public List<Task> getByLikeName(String name, Long tenantId) {
        return this.developTaskMapper.selectList(Wrappers.lambdaQuery(Task.class)
                .like(Task::getName, name)
                .eq(Task::getTenantId, tenantId));
    }

    /**
     * 根据ID查询信息
     * 如果不存在，则抛异常
     *
     * @param taskId 任务ID
     * @return
     */
    public Task getOneWithError(final Long taskId) {
        Task one = getOne(taskId);
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
    public Task getOne(Long taskId) {
        return developTaskMapper.selectById(taskId);
    }

    /**
     * 根据dependencyTaskIds解析依赖的任务
     *
     * @param dependencyTaskIds
     * @return
     */
    private List<Map<String, Object>> getDependencyTasks(String dependencyTaskIds) {
        try {
            return JSON.parseObject(dependencyTaskIds, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (Exception e) {
            return Arrays.stream(dependencyTaskIds.split(","))
                    .map(taskId -> {
                        Map<String, Object> map = Maps.newHashMap();
                        map.put("parentTaskId", taskId);
                        return map;
                    }).collect(Collectors.toList());
        }
    }

    /**
     * 获取当前任务的下游任务
     *
     * @param taskId
     * @return
     */
    public List<TaskGetNotDeleteVO> getChildTasks(Long taskId) {
        List<ScheduleTaskShade> notDeleteTaskVOs = taskService.listRelyCurrentTask(taskId);
        return notDeleteTaskVOs.stream()
                .map(taskShade -> {
                    TaskGetNotDeleteVO deleteVO = new TaskGetNotDeleteVO();
                    deleteVO.setTaskId(taskShade.getTaskId());
                    deleteVO.setName(taskShade.getName());
                    return deleteVO;
                }).collect(Collectors.toList());
    }

    /**
     * 获取组件版本
     *
     * @param tenantId
     * @param taskType
     * @return
     */
    public List<DevelopTaskGetComponentVersionResultVO> getComponentVersionByTaskType(Long tenantId, Integer taskType) {
        List<Component> components = componentService.getComponentVersionByEngineType(tenantId, taskType);
        List<DevelopTaskGetComponentVersionResultVO> componentVersionResultVOS = Lists.newArrayList();
        for (Component component : components) {
            DevelopTaskGetComponentVersionResultVO resultVO = new DevelopTaskGetComponentVersionResultVO();
            resultVO.setComponentVersion(component.getVersionValue());
            resultVO.setDefault(component.getIsDefault());
            componentVersionResultVOS.add(resultVO);
        }
        componentVersionResultVOS.sort(sortComponentVersion());
        return componentVersionResultVOS;
    }

    /**
     * 版本号排序，按照版本号新旧排序
     *
     * @return
     */
    private Comparator<DevelopTaskGetComponentVersionResultVO> sortComponentVersion() {
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
     *
     * @param tenantId
     * @param nodePid
     * @return
     */
    public List<Task> listBatchTaskByNodePid(Long tenantId, Long nodePid) {
        return developTaskMapper.selectList(
                Wrappers.lambdaQuery(Task.class)
                        .eq(Task::getNodePid, nodePid)
                        .eq(Task::getTenantId, tenantId));
    }

    /**
     * 根据 租户、目录id 查询任务列表
     * 此方法适合目录信息查询，与上面方法的区别是不返回SqlText等无用的大数据字段
     *
     * @param tenantId
     * @param nodePid
     * @return
     */
    public List<Task> catalogueListBatchTaskByNodePid(Long tenantId, Long nodePid) {
        return developTaskMapper.catalogueListBatchTaskByNodePid(tenantId, nodePid);
    }

    /**
     * 查询表所属字段 可以选择是否需要分区字段
     *
     * @param source
     * @param tableName
     * @param part      是否需要分区字段
     * @return
     * @throws Exception
     */
    private List<JSONObject> getTableColumnIncludePart(DevelopDataSource source, String tableName, Boolean part, String schema) {
        try {
            if (source == null) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
            }
            if (part == null) {
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
                    jsonObject.put("isPart", columnMetaDTO.getPart());
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
        DevelopDataSource source = dataSourceService.getOne(sourceId);
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
        return environmentContext.getTempDir() + File.separator + key;
    }

    private String getSourceKey(Long sourceId) {
        return Optional.ofNullable(sourceId).orElse(0L).toString();
    }

    private void downloadKerberosFromSftp(String kerberosFile, String localKerberosConf, Long tenantId, Timestamp kerberosFileTimestamp) {
        //需要读取配置文件
        Map<String, String> sftpMap = clusterService.getComponentByTenantId(tenantId, EComponentType.SFTP.getTypeCode(), false, Map.class, null);
        try {
            KerberosConfigVerify.downloadKerberosFromSftp(kerberosFile, localKerberosConf, sftpMap, kerberosFileTimestamp);
        } catch (Exception e) {
            //允许下载失败
            LOGGER.info("download kerberosFile failed {}", kerberosFile, e);
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
    private Map<String, Object> handleKerberos(Integer sourceType, Map<String, Object> kerberosMap, String localKerberosConf) {
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
    public List<DevelopAllProductGlobalReturnVO> allProductGlobalSearch(AllProductGlobalSearchVO searchVO) {
        // 过滤掉已经依赖的任务
        List<DevelopTaskTask> taskTasks = this.developTaskTaskService.getAllParentTask(searchVO.getTaskId());
        List<Long> excludeIds = new ArrayList<>(taskTasks.size());
        excludeIds.add(searchVO.getTaskId());
        taskTasks.forEach(taskTask -> excludeIds.add(taskTask.getTaskId()));

        List<ScheduleTaskShade> scheduleTaskShadeList = taskService.findTaskByTaskName(searchVO.getTaskName(), searchVO.getSelectTenantId(), searchVO.getUserId());
        List<ScheduleTaskShade> filterTask = scheduleTaskShadeList.stream().filter(scheduleTask -> !excludeIds.contains(scheduleTask.getTaskId())).collect(Collectors.toList());
        Map<Long, Tenant> tenantMap = tenantService.listAllTenant().stream().collect(Collectors.toMap(Tenant::getId, g -> (g)));

        List<DevelopAllProductGlobalReturnVO> voList = Lists.newArrayList();
        for (ScheduleTaskShade scheduleTaskShade : filterTask) {
            DevelopAllProductGlobalReturnVO vo = new DevelopAllProductGlobalReturnVO();
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
     * @param taskIds        任务编号
     * @param scheduleStatus 调度状态
     * @param userId         用户ID
     */
    public void frozenTask(List<Long> taskIds, Integer scheduleStatus, Long userId) {
        Task task = new Task();
        task.setModifyUserId(userId);
        task.setScheduleStatus(scheduleStatus);
        developTaskMapper.update(task, Wrappers.lambdaQuery(Task.class).in(Task::getId, taskIds));
        taskService.frozenTask(taskIds, scheduleStatus);
    }


    /**
     * 根据支持的任务类型返回
     *
     * @return
     */
    public List<DevelopTaskTypeVO> getSupportJobTypes(Long tenantId) {
        List<Component> engineSupportVOS = componentService.listComponents(tenantId);
        if (CollectionUtils.isEmpty(engineSupportVOS)) {
            throw new DtCenterDefException("该租户对应集群未配置任何组件");
        }
        List<Integer> tenantSupportMultiEngine = engineSupportVOS.stream().map(Component::getComponentTypeCode).collect(Collectors.toList());
        List<EScheduleJobType> eScheduleJobTypes = Arrays.stream(EScheduleJobType.values())
                .filter(a -> a.getComponentType() == null || (tenantSupportMultiEngine.contains(a.getComponentType().getTypeCode())))
                .collect(Collectors.toList());
        List<Dict> dicts = scheduleDictService.listByDictType(DictType.TASK_TYPE_PROPERTIES);
        Map<Integer, String> taskProperties = dicts.stream().collect(Collectors.toMap(d -> Integer.parseInt(d.getDictCode()), Dict::getDictValue));
        return eScheduleJobTypes.stream()
                .map(j -> {
                    DevelopTaskTypeVO taskTypeVO = new DevelopTaskTypeVO(j.getType(), j.getName(), j.getComputeType().getType());
                    String properties = taskProperties.get(j.getType());
                    if (!StringUtils.isBlank(properties)) {
                        taskTypeVO.setTaskProperties(JSONObject.parseObject(properties, TaskProperties.class));
                    }
                    return taskTypeVO;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取可以作为增量标识的字段信息
     *
     * @param sourceId
     * @param table
     * @param schema
     * @return
     */
    public List<JSONObject> getIncreColumn(Long sourceId, Object table, String schema) {
        List<JSONObject> increColumn = new ArrayList<>();

        String tableName;
        if (table instanceof String) {
            tableName = String.valueOf(table);
        } else if (table instanceof List) {
            List tableList = (List) table;
            if (CollectionUtils.isEmpty(tableList)) {
                return new ArrayList<>();
            }
            tableName = String.valueOf(tableList.get(0));
        } else {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        DevelopDataSource developDataSource = dataSourceService.getOne(sourceId);
        List<JSONObject> allColumn = getTableColumnIncludePart(developDataSource, tableName, false, schema);
        for (JSONObject col : allColumn) {
            if (ColumnType.isIncreType(col.getString("type")) || DataSourceType.Oracle.getVal().equals(developDataSource.getType())) {
                increColumn.add(col);
            } else if (DataSourceType.SQLServer.getVal().equals(developDataSource.getType())
                    && ColumnType.NVARCHAR.equals(ColumnType.fromString(col.getString("key")))) {
                increColumn.add(col);
            }
        }

        return increColumn;
    }

    /**
     * 修改任务部分信息
     *
     * @param taskId
     * @param taskName
     * @param catalogueId
     * @param desc
     * @param tenantId
     */
    public void editTask(Long taskId, String taskName, Long catalogueId, String desc, Long tenantId, String componentVersion) {
        getOneWithError(taskId);
        developCatalogueService.getOneWithError(catalogueId);

        Task taskInfo = developTaskMapper.selectOne(Wrappers.lambdaQuery(Task.class)
                .eq(Task::getName, taskName)
                .eq(Task::getIsDeleted, Deleted.NORMAL.getStatus())
                .eq(Task::getTenantId, tenantId));

        Task updateInfo = new Task();
        if (Objects.isNull(taskInfo)) {
            updateInfo.setId(taskId);
            updateInfo.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
            updateInfo.setName(taskName);
            updateInfo.setNodePid(catalogueId);
            updateInfo.setTaskDesc(desc);
            updateInfo.setComponentVersion(componentVersion);
            developTaskMapper.updateById(updateInfo);
            return;
        }
        if (!taskId.equals(taskInfo.getId())) {
            throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
        }
        updateInfo.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        updateInfo.setName(taskName);
        updateInfo.setNodePid(catalogueId);
        updateInfo.setTaskDesc(desc);
        updateInfo.setComponentVersion(componentVersion);
        developTaskMapper.update(updateInfo, Wrappers.lambdaUpdate(Task.class).eq(Task::getId, taskInfo.getId()));

        if (Objects.equals(EScheduleJobType.WORK_FLOW.getType(), taskInfo.getTaskType())) {
            //更新父节点目录时，同步更新子节点
            if (!taskInfo.getNodePid().equals(catalogueId)) {
                updateSonTaskNodePidByFlowId(taskInfo.getId(), catalogueId);
            }
        }
    }


    /**
     * 更新工作流的调度信息
     * 历史任务若父节点和子节点的周期不一致，则在提交时将子节点自动改为与父节点一致
     *
     * @param flowWorkId      工作流id
     * @param newScheduleConf 新调度信息
     */
    public void updateSubTaskScheduleConf(final Long flowWorkId, final JSONObject newScheduleConf) {
        Task task = developTaskMapper.selectById(flowWorkId);
        if (task == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        final List<Task> batchTasks = this.getFlowWorkSubTasks(flowWorkId);
        if (CollectionUtils.isEmpty(batchTasks)) {
            return;
        }
        final int periodType = newScheduleConf.getInteger("periodType");
        newScheduleConf.put("selfReliance", 0);
        //工作流更新调度属性时，子任务同步更新
        for (final Task bTask : batchTasks) {
            //工作流配置的自动取消不同步子任务
            newScheduleConf.remove("isExpire");
            JSONObject subTaskScheduleConf = JSON.parseObject(bTask.getScheduleConf());
            Boolean isFailRetry = MapUtils.getBoolean(subTaskScheduleConf, "isFailRetry", true);
            Integer maxRetryNum = MapUtils.getInteger(subTaskScheduleConf, "maxRetryNum", 3);
            newScheduleConf.put("isFailRetry", isFailRetry);
            newScheduleConf.put("maxRetryNum", maxRetryNum);
            WorkFlowScheduleConfEnum.valueOf(WorkFlowScheduleConfEnum.getCurrentPeriodType(String.valueOf(periodType)))
                    .handleWorkFlowChildScheduleConf(bTask, newScheduleConf);
            bTask.setPeriodType(periodType);
        }
        for (Task batchTask : batchTasks) {
            Task task1 = new Task();
            task1.setScheduleConf(batchTask.getScheduleConf());
            developTaskMapper.update(task1, Wrappers.lambdaUpdate(Task.class).eq(Task::getId, batchTask.getId()));
        }
    }


    /**
     * 根据父任务id，更新所有子任务的目录id
     *
     * @param flowId
     * @param nodePid
     */
    public void updateSonTaskNodePidByFlowId(Long flowId, Long nodePid) {
        Task task = new Task();
        task.setNodePid(nodePid);
        developTaskMapper.update(task, Wrappers.lambdaUpdate(Task.class).eq(Task::getFlowId, flowId));
    }

    public JSONObject getSyncProperties() {
        List<Dict> dicts = scheduleDictService.listByDictType(DictType.SYNC_MAPPING);
        if (CollectionUtils.isEmpty(dicts)) {
            return new JSONObject();
        }
        return JSONObject.parseObject(dicts.get(0).getDictValue());
    }
}
