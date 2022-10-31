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

package com.dtstack.taier.develop.service.schedule;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.DisplayDirect;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.domain.ScheduleTaskTaskShade;
import com.dtstack.taier.dao.domain.Tenant;
import com.dtstack.taier.dao.mapper.ScheduleTaskTaskShadeMapper;
import com.dtstack.taier.develop.service.console.TenantService;
import com.dtstack.taier.develop.service.user.UserService;
import com.dtstack.taier.develop.utils.JobUtils;
import com.dtstack.taier.develop.vo.schedule.ReturnTaskDisplayVO;
import com.dtstack.taier.develop.vo.schedule.TaskNodeVO;
import com.dtstack.taier.scheduler.dto.schedule.QueryTaskDisplayDTO;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/12/26 10:34 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Service
public class TaskTaskService extends ServiceImpl<ScheduleTaskTaskShadeMapper, ScheduleTaskTaskShade> {

    @Autowired
    private TaskService taskService;

    @Autowired
    private EnvironmentContext context;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private UserService userService;

    private ThreadLocal<HashMap<Long, String>> tenantThreadLocal = ThreadLocal.withInitial(HashMap::new);

    /**
     * 展开任务上下游
     *
     * @return 上下游规则
     */
    public ReturnTaskDisplayVO displayOffSpring(QueryTaskDisplayDTO dto) {
        // 查询的最长层级不能超过 max.jobJob.level
        dto.setLevel(JobUtils.checkLevel(dto.getLevel(), context.getMaxLevel()));

        // 查询任务
        ScheduleTaskShade taskShade = taskService.lambdaQuery()
                .eq(ScheduleTaskShade::getTaskId, dto.getTaskId())
                .eq(ScheduleTaskShade::getIsDeleted, Deleted.NORMAL.getStatus()).one();

        if (taskShade == null) {
            return null;
        }

        // 创建节点
        TaskNodeVO rootNode = new TaskNodeVO();
        setNode(taskShade, rootNode);
        Tenant tenant = tenantService.getTenantById(taskShade.getTenantId());
        if (null != tenant) {
            rootNode.setTenantName(tenant.getTenantName());
            rootNode.setTenantId(tenant.getId());
        }
        String userName = userService.getUserName(taskShade.getCreateUserId());
        rootNode.setOperatorId(taskShade.getCreateUserId());
        rootNode.setOperatorName(userName);


        if (DisplayDirect.CHILD.getType().equals(dto.getDirectType())) {
            rootNode.setChildNode(displayLevelNode(taskShade, dto.getLevel(), dto.getDirectType()));
        } else {
            rootNode.setParentNode(displayLevelNode(taskShade, dto.getLevel(), dto.getDirectType()));
        }

        ReturnTaskDisplayVO vo = new ReturnTaskDisplayVO();
        vo.setDirectType(dto.getDirectType());
        vo.setRootTaskNode(rootNode);
        tenantThreadLocal.remove();
        return vo;
    }


    /**
     * 展示固定层级的节点
     *
     * @param taskShade  头任务
     * @param level      层级
     * @param directType 方向
     * @return
     */
    private List<TaskNodeVO> displayLevelNode(ScheduleTaskShade taskShade, Integer level, Integer directType) {
        List<TaskNodeVO> taskNodeVOS = Lists.newArrayList();
        if (level <= 0) {
            // 已经到达需要查询的节点层数，不需要继续向下查询，直接返回即可
            return taskNodeVOS;
        }

        List<ScheduleTaskTaskShade> taskTaskShades;
        List<Long> taskIdList;

        // 查询节点
        if (DisplayDirect.CHILD.getType().equals(directType)) {
            // 向下
            taskTaskShades = this.lambdaQuery()
                    .eq(ScheduleTaskTaskShade::getParentTaskId, taskShade.getTaskId())
                    .eq(ScheduleTaskTaskShade::getIsDeleted, Deleted.NORMAL.getStatus())
                    .list();
            taskIdList = taskTaskShades.stream().map(ScheduleTaskTaskShade::getTaskId).collect(Collectors.toList());
        } else {
            // 向上
            taskTaskShades = this.lambdaQuery()
                    .eq(ScheduleTaskTaskShade::getTaskId, taskShade.getTaskId())
                    .eq(ScheduleTaskTaskShade::getIsDeleted, Deleted.NORMAL.getStatus())
                    .list();
            taskIdList = taskTaskShades.stream().map(ScheduleTaskTaskShade::getParentTaskId).collect(Collectors.toList());
        }

        if (CollectionUtils.isEmpty(taskTaskShades)) {
            return taskNodeVOS;
        }

        // 查询任务
        List<ScheduleTaskShade> taskShadeList = taskService.lambdaQuery()
                .in(ScheduleTaskShade::getTaskId, taskIdList)
                .eq(ScheduleTaskShade::getIsDeleted, Deleted.NORMAL.getStatus())
                .list();
        Map<Long, ScheduleTaskShade> taskShadeMap = taskShadeList.stream().collect(Collectors.toMap(ScheduleTaskShade::getTaskId, Function.identity()));

        for (ScheduleTaskTaskShade taskTaskShade : taskTaskShades) {
            ScheduleTaskShade taskShadeSon;
            if (DisplayDirect.CHILD.getType().equals(directType)) {
                taskShadeSon = taskShadeMap.get(taskTaskShade.getTaskId());
            } else {
                taskShadeSon = taskShadeMap.get(taskTaskShade.getParentTaskId());
            }

            TaskNodeVO vo = new TaskNodeVO();
            if (taskShadeSon != null) {
                setNode(taskShadeSon, vo);

                if (DisplayDirect.CHILD.getType().equals(directType)) {
                    vo.setChildNode(displayLevelNode(taskShadeSon, level - 1, directType));
                } else {
                    vo.setParentNode(displayLevelNode(taskShadeSon, level - 1, directType));
                }
            }
            taskNodeVOS.add(vo);
        }
        return taskNodeVOS;
    }


    /**
     * 查询孩子节点
     *
     * @param root        顶节点
     * @param taskTaskMap 节点关系集合
     * @return 节点列表
     */
    private List<TaskNodeVO> findChildNode(TaskNodeVO root, Map<Long, ScheduleTaskShade> taskMaps, Map<Long, List<ScheduleTaskTaskShade>> taskTaskMap) {
        List<ScheduleTaskTaskShade> scheduleTaskTaskShades = taskTaskMap.get(root.getTaskId());

        List<TaskNodeVO> taskNodeVOS = Lists.newArrayList();

        if (CollectionUtils.isEmpty(scheduleTaskTaskShades)) {
            return taskNodeVOS;
        }

        for (ScheduleTaskTaskShade taskTaskShade : scheduleTaskTaskShades) {
            ScheduleTaskShade taskShade = taskMaps.get(taskTaskShade.getTaskId());
            TaskNodeVO vo = new TaskNodeVO();
            vo.setTaskId(taskTaskShade.getTaskId());

            if (taskShade != null) {
                setNode(taskShade, vo);
            }

            vo.setChildNode(findChildNode(vo, taskMaps, taskTaskMap));
            taskNodeVOS.add(vo);
        }

        return taskNodeVOS;
    }

    /**
     * 设置node节点
     *
     * @param taskShade 任务
     * @param node      节点
     */
    private void setNode(ScheduleTaskShade taskShade, TaskNodeVO node) {
        node.setTaskId(taskShade.getTaskId());
        node.setTaskType(taskShade.getTaskType());
        node.setTaskName(taskShade.getName());
        node.setIsFlowTask(taskShade.getTaskType().equals(EScheduleJobType.WORK_FLOW.getVal()));
        node.setScheduleStatus(taskShade.getScheduleStatus());
        node.setGmtCreate(taskShade.getGmtCreate());
        HashMap<Long, String> tenantMap = tenantThreadLocal.get();
        String tenantName = tenantMap.get(taskShade.getTenantId());
        if (StringUtils.isNotBlank(tenantName)) {
            node.setTenantName(tenantName);
        } else {
            Tenant tenant = tenantService.getTenantById(taskShade.getTenantId());
            node.setTenantName(tenant == null ? "" : tenant.getTenantName());
        }
    }


    /**
     * 获取工作流的头节点
     *
     * @param taskId
     * @return
     */
    public List<Long> getWorkFlowTopTask(Long taskId) {
        ScheduleTaskShade workFlowTask = taskService.findTaskByTaskId(taskId);
        if (null == workFlowTask || !Objects.equals(EScheduleJobType.WORK_FLOW.getType(), workFlowTask.getTaskType())) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        JSONObject workFlowSql = JSONObject.parseObject(workFlowTask.getSqlText());
        List<Long> topTasks = new ArrayList<>();
        for (String taskIdKey : workFlowSql.keySet()) {
            if (workFlowSql.getJSONArray(taskIdKey).isEmpty()) {
                topTasks.add(Long.parseLong(taskIdKey));
            }
        }
        return topTasks;
    }
}
