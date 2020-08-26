package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeCountTaskVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadePageVO;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.master.executor.CronJobExecutor;
import com.dtstack.engine.master.executor.FillJobExecutor;
import com.dtstack.engine.master.scheduler.JobGraphBuilder;
import com.dtstack.engine.api.vo.ScheduleTaskShadeVO;
import com.dtstack.schedule.common.enums.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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


    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleTaskTaskShadeService scheduleTaskTaskShadeService;

    @Autowired
    private CronJobExecutor cronJobExecutor;

    @Autowired
    private FillJobExecutor fillJobExecutor;

    /**
     * web 接口
     * 例如：离线计算BatchTaskService.publishTaskInfo 触发 batchTaskShade 保存task的必要信息
     */
    public void addOrUpdate(ScheduleTaskShadeDTO batchTaskShadeDTO) {
        //保存batch_task_shade
        if (scheduleTaskShadeDao.getOne(batchTaskShadeDTO.getTaskId(),batchTaskShadeDTO.getAppType()) != null) {
            //更新提交时间
            batchTaskShadeDTO.setGmtModified(new Timestamp(System.currentTimeMillis()));
            scheduleTaskShadeDao.update(batchTaskShadeDTO);
            this.removeTaskCache(batchTaskShadeDTO.getTaskId(),batchTaskShadeDTO.getAppType());
        } else {
            if(Objects.isNull(batchTaskShadeDTO.getProjectScheduleStatus())){
                batchTaskShadeDTO.setProjectScheduleStatus(EProjectScheduleStatus.NORMAL.getStatus());
            }
            if(Objects.isNull(batchTaskShadeDTO.getNodePid())){
                batchTaskShadeDTO.setNodePid(0L);
            }
            scheduleTaskShadeDao.insert(batchTaskShadeDTO);
        }
    }

    /**
     * web 接口
     * task删除时触发同步清理
     */
    public void deleteTask( Long taskId,  long modifyUserId, Integer appType) {
        scheduleTaskShadeDao.delete(taskId, modifyUserId,appType);
        scheduleTaskTaskShadeService.clearDataByTaskId(taskId,appType);
        this.removeTaskCache(taskId,appType);
    }

    public List<ScheduleTaskShade> listTaskByType(Long projectId, Integer taskType, String taskName) {
        return scheduleTaskShadeDao.listByType(projectId, taskType, taskName);
    }

    /**
     * 获取所有需要需要生成调度的task 没有sqlText字段
     */
    public List<ScheduleTaskShade> listTaskByStatus(Long startId, Integer submitStatus, Integer projectSubmitStatus, Integer batchTaskSize) {
        return scheduleTaskShadeDao.listTaskByStatus(startId, submitStatus, projectSubmitStatus, batchTaskSize);
    }

    public Integer countTaskByStatus(Integer submitStatus, Integer projectSubmitStatus) {
        return scheduleTaskShadeDao.countTaskByStatus(submitStatus, projectSubmitStatus);
    }

    /**
     * 根据任务类型查询已提交到task服务的任务数
     * @param tenantId
     * @param dtuicTenantId
     * @param projectId
     * @param appType
     * @param taskTypes
     * @return
     */
    public ScheduleTaskShadeCountTaskVO countTaskByType( Long tenantId, Long dtuicTenantId,
                                                Long projectId,  Integer appType,
                                                List<Integer> taskTypes){
        List<Map<String, Object>> maps = scheduleTaskShadeDao.countTaskByType(tenantId, dtuicTenantId, Lists.newArrayList(projectId), appType, taskTypes, AppType.DATASCIENCE.getType() == appType ? 0L : null);
        if (CollectionUtils.isEmpty(maps)) {
            return new ScheduleTaskShadeCountTaskVO();
        }

        ScheduleTaskShadeCountTaskVO scheduleTaskShadeCountTaskVO = new ScheduleTaskShadeCountTaskVO();
        Map<String, Object> stringObjectMap = maps.get(0);
        buildVO(scheduleTaskShadeCountTaskVO, stringObjectMap);
        return scheduleTaskShadeCountTaskVO;
    }

    private void buildVO(ScheduleTaskShadeCountTaskVO scheduleTaskShadeCountTaskVO, Map<String, Object> stringObjectMap) {
        scheduleTaskShadeCountTaskVO.setDeployCount(stringObjectMap.get("deployCount")!=null?Integer.parseInt(stringObjectMap.get("deployCount").toString()):0);
        scheduleTaskShadeCountTaskVO.setProjectId(stringObjectMap.get("projectId") != null ? stringObjectMap.get("projectId").toString() : "");
    }

    public List<ScheduleTaskShadeCountTaskVO> countTaskByTypes( Long tenantId, Long dtuicTenantId,
                                                List<Long> projectIds,  Integer appType,
                                                List<Integer> taskTypes){
        List<Map<String, Object>> maps = scheduleTaskShadeDao.countTaskByType(tenantId, dtuicTenantId, projectIds, appType, taskTypes, AppType.DATASCIENCE.getType() == appType ? 0L : null);
        List<ScheduleTaskShadeCountTaskVO> scheduleTaskShadeCountTaskVOS = Lists.newArrayList();
        for (Map<String, Object> map : maps) {
            ScheduleTaskShadeCountTaskVO scheduleTaskShadeCountTaskVO = new ScheduleTaskShadeCountTaskVO();
            buildVO(scheduleTaskShadeCountTaskVO, map);
            scheduleTaskShadeCountTaskVOS.add(scheduleTaskShadeCountTaskVO);
        }
        return scheduleTaskShadeCountTaskVOS;
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
        if (Objects.isNull(appType)){
            appType = 1;
        }
        return scheduleTaskShadeDao.getByName(projectId, name,appType,flowId);
    }

    public void updateTaskName( long id,  String taskName,Integer appType) {
        scheduleTaskShadeDao.updateTaskName(id, taskName,appType);
    }


    /**
     * jobKey 格式：cronTrigger_taskId_time
     *
     * @param jobKey
     * @return
     * @see JobGraphBuilder#getSelfDependencyJobKeys(com.dtstack.task.domain.BatchJob, com.dtstack.task.server.parser.ScheduleCron, java.lang.String)
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
    public ScheduleTaskShade getWorkFlowTopNode(Long taskId) {
        if (taskId != null) {
            return scheduleTaskShadeDao.getWorkFlowTopNode(taskId);
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
                taskShadeVO.setId(taskShade.getTaskId());
                taskShadeVO.setTaskName(taskShade.getName());
                taskShadeVO.setTaskType(taskShade.getTaskType());
                taskShadeVO.setGmtModified(taskShade.getGmtModified());
                taskShadeVO.setIsDeleted(taskShade.getIsDeleted());
                data.add(taskShadeVO);
            }
        }
        return new PageResult<>(data, count, query);
    }


    public ScheduleTaskShade getBatchTaskById( Long taskId, Integer appType) {
        ScheduleTaskShade taskShade = scheduleTaskShadeDao.getOne(taskId, appType);
        if (taskShade == null || Deleted.DELETED.getStatus().equals(taskShade.getIsDeleted())) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        return taskShade;
    }

    public ScheduleTaskShadePageVO queryTasks(Long tenantId,
                                              Long projectId,
                                              String name,
                                              Long ownerId,
                                              Long startTime,
                                              Long endTime,
                                              Integer scheduleStatus,
                                              String taskTypeList,
                                              String periodTypeList,
                                              Integer currentPage,
                                              Integer pageSize, String  searchType,
                                              Integer appType){


        ScheduleTaskShadeDTO batchTaskDTO = new ScheduleTaskShadeDTO();
        batchTaskDTO.setTenantId(tenantId);
        batchTaskDTO.setProjectId(projectId);
        batchTaskDTO.setSubmitStatus(ESubmitStatus.SUBMIT.getStatus());
        batchTaskDTO.setTaskTypeList(convertStringToList(taskTypeList));
        batchTaskDTO.setPeriodTypeList(convertStringToList(periodTypeList));

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

        if (StringUtils.isNotBlank(name)) {
            batchTaskDTO.setFuzzName(name);
        }

        if (null != ownerId && ownerId != 0) {
            batchTaskDTO.setCreateUserId(ownerId);
        }

        if (null != startTime && null != endTime) {
            batchTaskDTO.setStartGmtModified(new Timestamp(startTime * 1000));
            batchTaskDTO.setEndGmtModified(new Timestamp(endTime * 1000));
        }

        if (scheduleStatus != null) {
            batchTaskDTO.setScheduleStatus(scheduleStatus);
        }

        PageQuery<ScheduleTaskShadeDTO> pageQuery = new PageQuery<>(currentPage, pageSize, "gmt_modified", Sort.DESC.name());
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
        pageQuery.setModel(batchTaskDTO);
        List<ScheduleTaskShade> batchTasks = scheduleTaskShadeDao.generalQuery(pageQuery);

        int count = scheduleTaskShadeDao.generalCount(batchTaskDTO);

        List<ScheduleTaskVO> vos = new ArrayList<>(batchTasks.size());

        for (ScheduleTaskShade batchTask : batchTasks) {
            vos.add(new com.dtstack.engine.master.vo.ScheduleTaskVO(batchTask,true));
        }
        if (queryAll) {
            vos = dealFlowWorkSubTasks(vos,appType);
        } else {
            //默认不查询全部工作流子节点
            //vos = dealFlowWorkTasks(vos);
        }



        int publishedTasks = scheduleTaskShadeDao.countPublishToProduce(projectId,appType);
        PageResult<List<ScheduleTaskVO>> pageResult = new PageResult<>(vos, count, pageQuery);
        ScheduleTaskShadePageVO scheduleTaskShadeTaskVO = new ScheduleTaskShadePageVO();
        scheduleTaskShadeTaskVO.setPageResult(pageResult);
        scheduleTaskShadeTaskVO.setPublishedTasks(publishedTasks);

        return scheduleTaskShadeTaskVO;
    }

    private List<ScheduleTaskVO> dealFlowWorkSubTasks(List<ScheduleTaskVO> vos, Integer appType) {
        Map<Long, ScheduleTaskVO> record = Maps.newHashMap();
        Map<Long, Integer> voIndex = Maps.newHashMap();
        vos.forEach(task -> voIndex.put(task.getId(), vos.indexOf(task)));
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
                    } else {
                        ScheduleTaskShade flow = scheduleTaskShadeDao.getOne(flowId,appType);
                        flowVo = new com.dtstack.engine.master.vo.ScheduleTaskVO(flow, true);
                        flowVo.setRelatedTasks(Lists.newArrayList(vo));
                        vos.set(vos.indexOf(vo), flowVo);
                    }
                    record.put(flowId, flowVo);
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
     * @param projectId
     * @param userId
     * @param appType
     */
    public void frozenTask(List<Long> taskIdList, int scheduleStatus,
                           Long projectId, Long userId,
                           Integer appType) {
        scheduleTaskShadeDao.batchUpdateTaskScheduleStatus(taskIdList, scheduleStatus, appType);
        for (Long taskId : taskIdList) {
            this.removeTaskCache(taskId, appType);
        }
    }


    /**
     * 查询工作流下子节点
     * @param taskId
     * @return
     */
    public ScheduleTaskVO dealFlowWorkTask( Long taskId, Integer appType,List<Integer> taskTypes,Long ownerId) {
        ScheduleTaskShade taskShade = scheduleTaskShadeDao.getOne(taskId,appType);
        if (taskShade == null) {
            return null;
        }
        ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO(taskShade, true);
        if (EScheduleJobType.WORK_FLOW.getVal().intValue() == vo.getTaskType()) {
            List<ScheduleTaskShade> subtasks = this.getFlowWorkSubTasks(vo.getTaskId(),appType,taskTypes,ownerId);
            if (CollectionUtils.isNotEmpty(subtasks)) {
                List<ScheduleTaskVO> list = Lists.newArrayList();
                subtasks.forEach(task -> list.add(new com.dtstack.engine.master.vo.ScheduleTaskVO(task,true)));
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
    public List<ScheduleTaskShade> getFlowWorkSubTasks( Long taskId,  Integer appType,List<Integer> taskTypes,Long ownerId) {
        ScheduleTaskShadeDTO batchTaskShadeDTO = new ScheduleTaskShadeDTO();
        batchTaskShadeDTO.setIsDeleted(Deleted.NORMAL.getStatus());
        batchTaskShadeDTO.setFlowId(taskId);
        batchTaskShadeDTO.setAppType(appType);
        batchTaskShadeDTO.setTaskTypeList(taskTypes);
        batchTaskShadeDTO.setOwnerUserId(ownerId);
        PageQuery<ScheduleTaskShadeDTO> pageQuery = new PageQuery<>(batchTaskShadeDTO);
        return scheduleTaskShadeDao.generalQuery(pageQuery);
    }


    public ScheduleTaskShade findTaskId( Long taskId, Integer isDeleted,  Integer appType) {
        if(Objects.isNull(taskId)){
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
        if (Objects.isNull(extInfo)) {
            extInfo = new JSONObject();
        }
        extInfo.put(TaskConstant.INFO, info);
        scheduleTaskShadeDao.updateTaskExtInfo(taskId, appType, extInfo.toJSONString());
    }


    public List<Map<String, Object>> listDependencyTask( List<Long> taskId,  Integer appType,  String name,  Long projectId) {
        return scheduleTaskShadeDao.listDependencyTask(projectId, name, taskId);
    }


    public List<Map<String, Object>> listByTaskIdsNotIn( List<Long> taskId,  Integer appType,  Long projectId) {
        return scheduleTaskShadeDao.listByTaskIdsNotIn(projectId, taskId);
    }

    public ScheduleTaskShade getById(Long id ){
        return scheduleTaskShadeDao.getById(id);
    }

    /**
     * 更新调度中的taskCache
     * @param taskId
     * @param appType
     */
    public void removeTaskCache(Long taskId,Integer appType){
        cronJobExecutor.removeTaskCache(taskId,appType);
        fillJobExecutor.removeTaskCache(taskId,appType);
    }
}
