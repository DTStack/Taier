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

package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.domain.Tenant;
import com.dtstack.engine.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.mapper.ScheduleTaskShadeDao;
import com.dtstack.engine.mapper.ScheduleTaskShadeMapper;
import com.dtstack.engine.mapper.TenantMapper;
import com.dtstack.engine.master.impl.vo.CronExceptionVO;
import com.dtstack.engine.master.vo.ScheduleTaskShadeVO;
import com.dtstack.engine.master.vo.ScheduleTaskVO;
import com.dtstack.engine.master.vo.schedule.task.shade.ScheduleTaskShadePageVO;
import com.dtstack.engine.master.vo.schedule.task.shade.ScheduleTaskShadeTypeVO;
import com.dtstack.engine.master.vo.task.NotDeleteTaskVO;
import com.dtstack.engine.master.vo.task.TaskTypeVO;
import com.dtstack.engine.pager.PageQuery;
import com.dtstack.engine.pager.PageResult;
import com.dtstack.engine.pluginapi.exception.ExceptionUtil;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.util.DateUtil;
import com.dtstack.engine.pluginapi.util.MathUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Service
public class ScheduleTaskShadeService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskShadeService.class);

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleTaskTaskShadeService scheduleTaskTaskShadeService;

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleTaskShadeMapper scheduleTaskShadeMapper;

    /**
     * web 接口
     * 例如：离线计算BatchTaskService.publishTaskInfo 触发 batchTaskShade 保存task的必要信息
     */
    public void addOrUpdate(ScheduleTaskShadeDTO batchTaskShadeDTO) {

        //保存batch_task_shade
        if (scheduleTaskShadeDao.getOne(batchTaskShadeDTO.getTaskId()) != null) {
            //更新提交时间
            batchTaskShadeDTO.setGmtModified(new Timestamp(System.currentTimeMillis()));
            scheduleTaskShadeDao.update(batchTaskShadeDTO);
        } else {
            if (null == batchTaskShadeDTO.getFlowId()) {
                batchTaskShadeDTO.setFlowId(0L);
            }
            if (StringUtils.isNotBlank(batchTaskShadeDTO.getComponentVersion())) {
                batchTaskShadeDTO.setComponentVersion(batchTaskShadeDTO.getComponentVersion());
            }
            scheduleTaskShadeDao.insert(batchTaskShadeDTO);
        }
    }

    /**
     * web 接口
     * task删除时触发同步清理
     */
    public void deleteTask(Long taskId, long modifyUserId, Integer appType) {
        scheduleTaskShadeDao.delete(taskId, modifyUserId, appType);
        scheduleTaskTaskShadeService.clearDataByTaskId(taskId, appType);
    }

    public List<NotDeleteTaskVO> getNotDeleteTask(Long taskId, Integer appType) {
        List<ScheduleTaskShade> shades = scheduleTaskShadeDao.getChildTaskByOtherPlatform(taskId, appType, environmentContext.getListChildTaskLimit());
        return buildNotDeleteTaskVO( shades,appType);

    }

    public List<NotDeleteTaskVO> buildNotDeleteTaskVO(List<ScheduleTaskShade> shades,Integer appType) {
        List<NotDeleteTaskVO> notDeleteTaskVOS = Lists.newArrayList();
        /*if (CollectionUtils.isNotEmpty(shades)) {
            List<Long> projectIds = shades.stream().map(ScheduleTaskShade::getProjectId).collect(Collectors.toList());
            List<Long> tenantIds = shades.stream().map(ScheduleTaskShade::getDtuicTenantId).collect(Collectors.toList());

            List<Tenant> tenants = tenantDao.listAllTenantByDtUicTenantIds(tenantIds);

            Map<Long, Tenant> tenantMap = tenants.stream().collect(Collectors.toMap(Tenant::getDtUicTenantId, g -> (g)));
            for (ScheduleTaskShade shade : shades) {
                NotDeleteTaskVO notDeleteTaskVO = new NotDeleteTaskVO();
                notDeleteTaskVO.setAppType(shade.getAppType());
                ScheduleEngineProject project = scheduleEngineProjectDao.getProjectByProjectIdAndApptype(shade.getProjectId(), shade.getAppType());
                if (project != null) {
                    notDeleteTaskVO.setProjectAlias(project.getProjectAlias());
                    notDeleteTaskVO.setProjectName(project.getProjectName());
                }
                Tenant tenant = tenantMap.get(shade.getDtuicTenantId());
                if (tenant != null) {
                    notDeleteTaskVO.setTenantName(tenant.getTenantName());
                }

                notDeleteTaskVO.setTaskName(shade.getName());
                notDeleteTaskVOS.add(notDeleteTaskVO);
            }
        }*/
        return notDeleteTaskVOS;
    }

    /**
     * 获取所有需要需要生成调度的task 没有sqlText字段
     */
    public List<ScheduleTaskShade> listTaskByStatus(Long startId, Integer submitStatus, Integer projectSubmitStatus, Integer batchTaskSize,Collection<Long> projectIds,Integer appType) {
        return scheduleTaskShadeDao.listTaskByStatus(startId, submitStatus, projectSubmitStatus, batchTaskSize,projectIds,appType);
    }

    public Integer countTaskByStatus(Integer submitStatus, Integer projectSubmitStatus,Collection<Long> projectIds,Integer appType) {
        return scheduleTaskShadeDao.countTaskByStatus(submitStatus, projectSubmitStatus,projectIds,appType);
    }





    /**
     * 根据任务id获取对应的taskShade
     * @param taskIds
     * @return
     */
    public List<ScheduleTaskShade> getTaskByIds(List<Long> taskIds, Integer appType) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return Collections.EMPTY_LIST;
        }

        return scheduleTaskShadeDao.listByTaskIds(taskIds, Deleted.NORMAL.getStatus(),appType);
    }

    /**
     * ps- 省略了一些大字符串 如 sql_text、task_params
     *
     * @param taskIdArray
     * @return
     */
    public List<ScheduleTaskShade> getSimpleTaskRangeAllByIds(List<Long> taskIdArray, Integer appType) {
        if (CollectionUtils.isEmpty(taskIdArray)) {
            return Collections.EMPTY_LIST;
        }

        return scheduleTaskShadeDao.listSimpleByTaskIds(taskIdArray, null,appType);
    }

    /**
     * 数据开发-根据项目id,任务名 获取任务列表
     *
     * @param projectId
     * @return
     * @author toutian
     */
    public List<ScheduleTaskShade> getTasksByName( long projectId,
                                                   String name,  Integer appType) {

        return scheduleTaskShadeDao.listByNameLike(projectId, name,appType,null,null);
    }

    public ScheduleTaskShade getByName( long projectId,
                                        String name,  Integer appType, Long flowId) {
        //如果appType没传那就默认为ide
        if (null == appType){
            appType = 1;
        }
        return scheduleTaskShadeDao.getByName(projectId, name,appType,flowId);
    }

    public void updateTaskName(long taskId,  String taskName,Integer appType) {
        scheduleTaskShadeDao.updateTaskName(taskId, taskName,appType);
    }


    /**
     * jobKey 格式：cronTrigger_taskId_time
     *
     * @param jobKey
     * @return
     */
    public String getTaskNameByJobKey(String jobKey,Integer appType) {
        String[] jobKeySplit = jobKey.split("_");
        if (jobKeySplit.length < 3) {
            return "";
        }

        String taskIdStr = jobKeySplit[jobKeySplit.length - 2];
        Long taskShadeId = MathUtil.getLongVal(taskIdStr);
        ScheduleTaskShade taskShade = scheduleTaskShadeDao.getById(taskShadeId);
        if (taskShade == null) {
            return "";
        }

        return taskShade.getName();
    }


    /**
     * 获取工作流中的最顶层的子节点
     *
     * @param taskId
     * @return
     */
    public ScheduleTaskShade getWorkFlowTopNode(Long taskId,Integer appType) {
        if (taskId != null) {
            return scheduleTaskShadeDao.getWorkFlowTopNode(taskId,appType);
        } else {
            return null;
        }
    }

    /**
     * 分页查询已提交的任务
     */
    public PageResult<List<ScheduleTaskShadeVO>> pageQuery(ScheduleTaskShadeDTO dto) {
        PageQuery<ScheduleTaskShadeDTO> query = new PageQuery<>(dto.getPageIndex(),dto.getPageSize(),"gmt_modified",dto.getSort());
        query.setModel(dto);
        Integer count = scheduleTaskShadeDao.simpleCount(dto);
        List<ScheduleTaskShadeVO> data = new ArrayList<>();
        if (count > 0) {
            List<ScheduleTaskShade> taskShades = scheduleTaskShadeDao.simpleQuery(query);
            for (ScheduleTaskShade taskShade : taskShades) {
                ScheduleTaskShadeVO taskShadeVO = new ScheduleTaskShadeVO();
                BeanUtils.copyProperties(taskShade,taskShadeVO);
//                taskShadeVO.setId(taskShade.getTaskId());
                taskShadeVO.setTaskName(taskShade.getName());
//                taskShadeVO.setTaskType(taskShade.getTaskType());
//                taskShadeVO.setGmtModified(taskShade.getGmtModified());
//                taskShadeVO.setIsDeleted(taskShade.getIsDeleted());
                data.add(taskShadeVO);
            }
        }
        return new PageResult<>(data, count, query);
    }


    public ScheduleTaskShade getBatchTaskById(Long taskId) {

        if (null == taskId) {
            throw new RdosDefineException("taskId或appType不能为空");
        }
        ScheduleTaskShade taskShade = scheduleTaskShadeDao.getOne(taskId);
        if (taskShade == null || Deleted.DELETED.getStatus().equals(taskShade.getIsDeleted())) {
            return null;
        }
        return taskShade;
    }

    public ScheduleTaskShade getByTaskId(Long taskId) {
        ScheduleTaskShade taskShade = scheduleTaskShadeMapper.selectOne(
                Wrappers.lambdaQuery(ScheduleTaskShade.class).eq(ScheduleTaskShade::getTaskId,taskId));
        if (taskShade == null) {
            return null;
        }
        return taskShade;
    }



    public ScheduleTaskShadePageVO queryTasks(Long tenantId,
                                              Long dtTenantId,
                                              Long projectId,
                                              String name,
                                              Long ownerId,
                                              Long startTime,
                                              Long endTime,
                                              Integer scheduleStatus,
                                              String taskTypeList,
                                              String periodTypeList,
                                              Integer currentPage,
                                              Integer pageSize,
                                              String  searchType,
                                              Integer appType){


        ScheduleTaskShadeDTO batchTaskDTO = new ScheduleTaskShadeDTO();
        boolean queryAll = false;
        if (StringUtils.isNotBlank(name) ||
                CollectionUtils.isNotEmpty(batchTaskDTO.getTaskTypeList()) ||
                CollectionUtils.isNotEmpty(batchTaskDTO.getPeriodTypeList())) {
            queryAll = true;
            batchTaskDTO.setFlowId(null);
        } else {
            //过滤掉任务流中的子任务
            batchTaskDTO.setFlowId(0L);
        }
        setBatchTaskDTO(tenantId,dtTenantId, projectId, name, ownerId, startTime, endTime, scheduleStatus, taskTypeList, periodTypeList, searchType, batchTaskDTO,appType);
        PageQuery<ScheduleTaskShadeDTO> pageQuery = new PageQuery<>(currentPage, pageSize, "gmt_modified", Sort.DESC.name());
        pageQuery.setModel(batchTaskDTO);
        ScheduleTaskShadePageVO scheduleTaskShadeTaskVO = new ScheduleTaskShadePageVO();
        int publishedTasks = scheduleTaskShadeDao.countPublishToProduce(projectId,appType);
        scheduleTaskShadeTaskVO.setPublishedTasks(publishedTasks);
        int count = scheduleTaskShadeDao.generalCount(batchTaskDTO);
        if(count<=0){
            scheduleTaskShadeTaskVO.setPageResult(new PageResult<>(new ArrayList<>(),count,pageQuery));
            return scheduleTaskShadeTaskVO;
        }
        List<ScheduleTaskShade> batchTasks = scheduleTaskShadeDao.generalQuery(pageQuery);
        List<ScheduleTaskVO> vos = new ArrayList<>(batchTasks.size());

        for (ScheduleTaskShade batchTask : batchTasks) {
            vos.add(new com.dtstack.engine.master.impl.vo.ScheduleTaskVO(batchTask,true));
        }
        if (queryAll) {
            vos = dealFlowWorkSubTasks(vos,appType);
        } else {
            //默认不查询全部工作流子节点
            //vos = dealFlowWorkTasks(vos);
        }

        PageResult<List<ScheduleTaskVO>> pageResult = new PageResult<>(vos, count, pageQuery);
        scheduleTaskShadeTaskVO.setPageResult(pageResult);
        return scheduleTaskShadeTaskVO;
    }




    /**
     * @author newman
     * @Description 设置分页任务查询参数
     * @Date 2020-12-21 18:12
     * @param tenantId:
     * @param projectId:
     * @param name:
     * @param ownerId:
     * @param startTime:
     * @param endTime:
     * @param scheduleStatus:
     * @param taskTypeList:
     * @param periodTypeList:
     * @param searchType:
     * @param batchTaskDTO:
     * @return: void
     **/
    private void setBatchTaskDTO(Long tenantId,Long dtTenantId, Long projectId, String name, Long ownerId, Long startTime, Long endTime, Integer scheduleStatus, String taskTypeList, String periodTypeList, String searchType, ScheduleTaskShadeDTO batchTaskDTO,Integer appType) {
//        batchTaskDTO.setTenantId(tenantId);
//        batchTaskDTO.setSubmitStatus(ESubmitStatus.SUBMIT.getStatus());
        batchTaskDTO.setTaskTypeList(convertStringToList(taskTypeList));
        batchTaskDTO.setPeriodTypeList(convertStringToList(periodTypeList));
        if (StringUtils.isNotBlank(name)) {
            batchTaskDTO.setFuzzName(name);
        }
        if (null != ownerId && ownerId != 0) {
            batchTaskDTO.setOwnerUserId(ownerId);
        }
        if (null != startTime && null != endTime) {
            batchTaskDTO.setStartGmtModified(new Timestamp(startTime * 1000));
            batchTaskDTO.setEndGmtModified(new Timestamp(endTime * 1000));
        }
        if (scheduleStatus != null) {
            batchTaskDTO.setScheduleStatus(scheduleStatus);
        }
        if (StringUtils.isEmpty(searchType) || "fuzzy".equalsIgnoreCase(searchType)) {
            batchTaskDTO.setSearchType(1);
        } else if ("precise".equalsIgnoreCase(searchType)) {
            batchTaskDTO.setSearchType(2);
        } else if ("front".equalsIgnoreCase(searchType)) {
            batchTaskDTO.setSearchType(3);
        } else if ("tail".equalsIgnoreCase(searchType)) {
            batchTaskDTO.setSearchType(4);
        } else {
            batchTaskDTO.setSearchType(1);
        }
    }

    private List<ScheduleTaskVO> dealFlowWorkSubTasks(List<ScheduleTaskVO> vos, Integer appType) {
        Map<Long, ScheduleTaskVO> record = Maps.newHashMap();
        Map<Long, Integer> voIndex = Maps.newHashMap();
        vos.forEach(task -> voIndex.put(task.getTaskId(), vos.indexOf(task)));
        Iterator<ScheduleTaskVO> iterator = vos.iterator();
        List<ScheduleTaskVO> vosCopy = new ArrayList<>(vos);
        while (iterator.hasNext()) {
            ScheduleTaskVO vo = iterator.next();
            Long flowId = vo.getFlowId();
            if (flowId > 0) {
                if (record.containsKey(flowId)) {
                    ScheduleTaskVO flowVo = record.get(flowId);
                    flowVo.getRelatedTasks().add(vo);
                    iterator.remove();
                } else {
                    ScheduleTaskVO flowVo;
                    if (voIndex.containsKey(flowId)) {
                        flowVo = vosCopy.get(voIndex.get(flowId));
                        flowVo.setRelatedTasks(Lists.newArrayList(vo));
                        iterator.remove();
                        record.put(flowId, flowVo);
                    } else {
                        ScheduleTaskShade flow = scheduleTaskShadeDao.getOne(flowId);
                        if (flow != null) {
                            flowVo = new com.dtstack.engine.master.impl.vo.ScheduleTaskVO(flow, true);
                            flowVo.setRelatedTasks(Lists.newArrayList(vo));
                            vos.set(vos.indexOf(vo), flowVo);
                            record.put(flowId, flowVo);
                        }
                    }
                }
            }
        }
        return vos;
    }


    private List<Integer> convertStringToList(String str) {
        if(StringUtils.isBlank(str)){
            return new ArrayList<>();
        }
        return Arrays.stream(str.split(",")).map(Integer::valueOf).collect(Collectors.toList());
    }


    /**
     * 冻结任务
     * @param taskIdList
     * @param scheduleStatus
     * @param appType
     */
    public void frozenTask(List<Long> taskIdList, int scheduleStatus,
                           Integer appType) {
        scheduleTaskShadeDao.batchUpdateTaskScheduleStatus(taskIdList, scheduleStatus, appType);
    }


    /**
     * 查询工作流下子节点
     * @param taskId
     * @return
     */
    public ScheduleTaskVO dealFlowWorkTask( Long taskId, Integer appType,List<Integer> taskTypes,Long ownerId) {

        ScheduleTaskShade taskShade = scheduleTaskShadeDao.getOne(taskId);
        if (taskShade == null) {
            return null;
        }
        ScheduleTaskVO vo = new com.dtstack.engine.master.impl.vo.ScheduleTaskVO(taskShade, true);
        if (EScheduleJobType.WORK_FLOW.getVal().equals(vo.getTaskType())) {
            List<ScheduleTaskShade> subtasks = this.getFlowWorkSubTasks(vo.getTaskId(),taskTypes,ownerId);
            if (CollectionUtils.isNotEmpty(subtasks)) {
                List<ScheduleTaskVO> list = Lists.newArrayList();
                subtasks.forEach(task -> list.add(new com.dtstack.engine.master.impl.vo.ScheduleTaskVO(task,true)));
                vo.setRelatedTasks(list);
            }
        }
        return vo;
    }

    /**
     * 获取任务流下的所有子任务
     *
     * @param taskId
     * @return
     */
    public List<ScheduleTaskShade> getFlowWorkSubTasks( Long taskId,List<Integer> taskTypes,Long ownerId) {
        ScheduleTaskShadeDTO batchTaskShadeDTO = new ScheduleTaskShadeDTO();
        batchTaskShadeDTO.setIsDeleted(Deleted.NORMAL.getStatus());
        batchTaskShadeDTO.setFlowId(taskId);
        batchTaskShadeDTO.setTaskTypeList(taskTypes);
        batchTaskShadeDTO.setOwnerUserId(ownerId);
        PageQuery<ScheduleTaskShadeDTO> pageQuery = new PageQuery<>(batchTaskShadeDTO);
        return scheduleTaskShadeDao.generalQuery(pageQuery);
    }


    public ScheduleTaskShade findTaskId( Long taskId, Integer isDeleted,  Integer appType) {
        if(null == taskId){
            return null;
        }
        List<ScheduleTaskShade> batchTaskShades = scheduleTaskShadeDao.listByTaskIds(Lists.newArrayList(taskId), isDeleted,appType);
        if(CollectionUtils.isEmpty(batchTaskShades)){
            return null;
        }
        return batchTaskShades.get(0);
    }

    /**
     *
     * @param taskIds
     * @param isDeleted
     * @param appType
     * @param isSimple 不查询sql
     * @return
     */
    public List<ScheduleTaskShade> findTaskIds( List<Long> taskIds, Integer isDeleted,  Integer appType,  boolean isSimple) {
        if(CollectionUtils.isEmpty(taskIds)){
            return null;
        }
        if(isSimple){
            return scheduleTaskShadeDao.listSimpleByTaskIds(taskIds,isDeleted,appType);
        }
        return  scheduleTaskShadeDao.listByTaskIds(taskIds, isDeleted,appType);
    }



    /**
     *
     * 保存任务提交engine的额外信息
     * @param taskId
     * @param appType
     * @param info
     * @return
     */
    public void info( Long taskId, Integer appType,String info) {

        JSONObject extInfo = JSONObject.parseObject(scheduleTaskShadeDao.getExtInfoByTaskId(taskId, appType));
        if (null == extInfo) {
            extInfo = new JSONObject();
        }
        extInfo.put(TaskConstant.INFO, info);
        scheduleTaskShadeDao.updateTaskExtInfo(taskId, appType, extInfo.toJSONString());
    }


    public List<Map<String, Object>> listDependencyTask( List<Long> taskId, String name,  Long projectId) {

        return scheduleTaskShadeDao.listDependencyTask(projectId, name, taskId);
    }


    public List<Map<String, Object>> listByTaskIdsNotIn( List<Long> taskId,  Integer appType,  Long projectId) {
        return scheduleTaskShadeDao.listByTaskIdsNotIn(projectId, taskId);
    }

    public ScheduleTaskShade getById(Long id ){
        return scheduleTaskShadeDao.getById(id);
    }

    public List<ScheduleTaskShadeTypeVO> findFuzzyTaskNameByCondition(String name, Integer appType, Long uicTenantId, Long projectId) {
        if (appType == null) {
            throw new RdosDefineException("appType must be passed");
        }

        if (uicTenantId == null) {
            throw new RdosDefineException("uicTenantId must be passed");
        }

        if (projectId == null) {
            throw new RdosDefineException("projectId must be passed");
        }

        if (StringUtils.isNotBlank(name)) {
            name = handlerStr(name);
        }

        if (StringUtils.isBlank(name)) {
            return buildTypeVo(null);
        }
        List<ScheduleTaskShade> tasks = scheduleTaskShadeDao.findFuzzyTaskNameByCondition(name, appType, uicTenantId, projectId, environmentContext.getFuzzyProjectByProjectAliasLimit(),EProjectScheduleStatus.NORMAL.getStatus());

        return buildTypeVo(tasks);
    }

    private String handlerStr(String name) {
        name = name.replaceAll("%", "\\%");
        name = name.replaceAll("'", "");
        name = name.replaceAll("_", "\\_");
        return name;
    }

    private List<ScheduleTaskShadeTypeVO> buildTypeVo(List<ScheduleTaskShade> tasks) {
        if (CollectionUtils.isEmpty(tasks)) {
            return Lists.newArrayList();
        }

        List<ScheduleTaskShadeTypeVO> vos = Lists.newArrayList();
        for (ScheduleTaskShade task : tasks) {
            ScheduleTaskShadeTypeVO vo = new ScheduleTaskShadeTypeVO();
//            vo.setId(task.getId());
            vo.setTaskId(task.getTaskId());
            vo.setName(task.getName());
            vo.setTaskType(task.getTaskType());
            vo.setComputeType(task.getComputeType());

            Tenant tenant = tenantMapper.selectById(task.getTenantId());

            if (tenant != null) {
                vo.setTenantName(tenant.getTenantName());
            }


            vos.add(vo);

        }
        return vos;
    }

    /**
     * 按照appType和taskId分组查询
     * @param groupByAppMap 分组数据
     * @return
     */
    public Map<Integer,List<ScheduleTaskShade>> listTaskShadeByIdAndType(Map<Integer,Set<Long>> groupByAppMap){
        if (MapUtils.isEmpty(groupByAppMap)){
            throw new RdosDefineException("taskId或appType不能为空");
        }
        Map<Integer,List<ScheduleTaskShade>> scheduleTaskShadeMap=new HashMap<>(groupByAppMap.size());
        for (Map.Entry<Integer, Set<Long>> entry : groupByAppMap.entrySet()) {
            scheduleTaskShadeMap.put(entry.getKey(),scheduleTaskShadeDao.listByTaskIds(entry.getValue(), Deleted.NORMAL.getStatus(), entry.getKey()));
        }
        return scheduleTaskShadeMap;
    }

    /**
     * 校验cron表达式
     * @param cron
     * @return
     */
    public CronExceptionVO checkCronExpression(String cron,Long minPeriod) {
        CronExpression cronExpression = null;
        try {
            CronExpression.validateExpression(cron);
            cronExpression = new CronExpression(cron);
        }catch (Exception e){
            return new CronExceptionVO(CronExceptionVO.CHECK_EXCEPTION,ExceptionUtil.getErrorMessage(e));
        }
        minPeriod*=1000;
        // 第一次执行的时间
        Date curRunTime = cronExpression.getNextValidTimeAfter(new Date()), nextRunTime ;
        Date startDateTime = new Date(curRunTime.toInstant().atOffset(DateUtil.DEFAULT_ZONE)
                .toLocalDate().atStartOfDay().plusSeconds(-1L).toInstant(DateUtil.DEFAULT_ZONE).toEpochMilli());

        Date endTDateTime = new Date(curRunTime.toInstant().atOffset(DateUtil.DEFAULT_ZONE)
                .toLocalDate().plusDays(1).atStartOfDay().toInstant(DateUtil.DEFAULT_ZONE).toEpochMilli());
        while (curRunTime.after(startDateTime) && curRunTime.before(endTDateTime)){
            nextRunTime = cronExpression.getNextValidTimeAfter(curRunTime);
            if (nextRunTime.getTime()- minPeriod < curRunTime.getTime()){
                return new CronExceptionVO(CronExceptionVO.PERIOD_EXCEPTION,String.format("%s run too frequency and min period = %sS",cron,minPeriod/1000));
            }
            curRunTime = nextRunTime;
        }
        return null;
    }

    /**
     * 指定范围内最近多少条运行时间
     * @param startDate 开始
     * @param endDate 结束
     * @param cron cron
     * @param num 条数
     * @return 运行数据
     */
    public List<String> recentlyRunTime(String startDate, String endDate, String cron, int num) {
        CronExpression cronExpression;
        try {
            cronExpression = new CronExpression(cron);
        }catch (Exception e){
            throw new RdosDefineException("illegal cron expression");
        }
        List<String > recentlyList = new ArrayList<>(num);
        Date nowDate = new Date();
        Date start = DateUtil.parseDate(startDate, DateUtil.DATE_FORMAT);
        // 当前时间在开始时间后,以下一天开始的时间为起始时间
        if (nowDate.after(start)){
            start = new Date(nowDate.toInstant().atOffset(DateUtil.DEFAULT_ZONE)
                    .toLocalDate().plusDays(1).atStartOfDay().toInstant(DateUtil.DEFAULT_ZONE).toEpochMilli());
        }else {
            start = new Date(start.getTime()-1000);
        }
        Date end = new Date(DateUtil.parseDate(endDate,DateUtil.DATE_FORMAT).toInstant().atOffset(DateUtil.DEFAULT_ZONE)
                .toLocalDate().plusDays(1).atStartOfDay().toInstant(DateUtil.DEFAULT_ZONE).toEpochMilli());

        Date curDate = cronExpression.getNextValidTimeAfter(start);
        while (num-- > 0 && curDate.before(end) && curDate.after(start)){
            recentlyList.add(DateUtil.getDate(curDate,DateUtil.STANDARD_DATETIME_FORMAT));
            curDate = cronExpression.getNextValidTimeAfter(curDate);
        }
        return recentlyList;
    }


    public List<TaskTypeVO> getTaskType() {
        EScheduleJobType[] values = EScheduleJobType.values();
        List<TaskTypeVO> taskTypeVOS = Lists.newArrayList();
        for (EScheduleJobType value : values) {
            TaskTypeVO vo = new TaskTypeVO();
            vo.setCode(value.getType());
            vo.setName(value.getName());
            vo.setEnumName(value.name());
            taskTypeVOS.add(vo);
        }
        return taskTypeVOS;
    }
}
