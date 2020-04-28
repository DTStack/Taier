package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.annotation.Forbidden;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.master.scheduler.JobGraphBuilder;
import com.dtstack.engine.api.vo.ScheduleTaskShadeVO;
import com.dtstack.schedule.common.enums.Deleted;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import com.dtstack.schedule.common.enums.ESubmitStatus;
import com.dtstack.schedule.common.enums.Sort;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
public class ScheduleTaskShadeService implements com.dtstack.engine.api.service.ScheduleTaskShadeService {


    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleTaskTaskShadeService taskTaskShadeService;

    private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

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
        } else {
            scheduleTaskShadeDao.insert(batchTaskShadeDTO);
        }
    }

    /**
     * web 接口
     * task删除时触发同步清理
     */
    public void deleteTask(@Param("taskId") Long taskId, @Param("modifyUserId") long modifyUserId,@Param("appType") Integer appType) {
        scheduleTaskShadeDao.delete(taskId, modifyUserId,appType);
        taskTaskShadeService.clearDataByTaskId(taskId,appType);
    }

    @Forbidden
    public List<ScheduleTaskShade> listTaskByType(Long projectId, Integer taskType, String taskName) {
        return scheduleTaskShadeDao.listByType(projectId, taskType, taskName);
    }

    /**
     * 获取所有需要需要生成调度的task 没有sqlText字段
     */
    @Forbidden
    public List<ScheduleTaskShade> listTaskByStatus(Long startId, Integer submitStatus, Integer projectSubmitStatus, Integer batchTaskSize) {
        return scheduleTaskShadeDao.listTaskByStatus(startId, submitStatus, projectSubmitStatus, batchTaskSize);
    }

    @Forbidden
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
    public Map<String ,Object> countTaskByType(@Param("tenantId") Long tenantId,@Param("dtuicTenantId") Long dtuicTenantId,
                                               @Param("projectId") Long projectId, @Param("appType") Integer appType,
                                               @Param("taskTypes") List<Integer> taskTypes){
        return scheduleTaskShadeDao.countTaskByType(tenantId,dtuicTenantId,projectId,appType,taskTypes);
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
    @Forbidden
    public List<ScheduleTaskShade> getSimpleTaskRangeAllByIds(List<Long> taskIdArray) {
        if (CollectionUtils.isEmpty(taskIdArray)) {
            return Collections.EMPTY_LIST;
        }

        return scheduleTaskShadeDao.listSimpleByTaskIds(taskIdArray, null);
    }

    /**
     * 数据开发-根据项目id,任务名 获取任务列表
     *
     * @param projectId
     * @return
     * @author toutian
     */
    public List<ScheduleTaskShade> getTasksByName(@Param("projectId") long projectId,
                                                  @Param("name") String name, @Param("appType") Integer appType) {
        return scheduleTaskShadeDao.listByNameLike(projectId, name,appType,null,null);
    }

    public ScheduleTaskShade getByName(@Param("projectId") long projectId,
                                       @Param("name") String name, @Param("appType") Integer appType,@Param("flowId") Long flowId) {
        //如果appType没传那就默认为ide
        if (Objects.isNull(appType)){
            appType = 1;
        }
        return scheduleTaskShadeDao.getByName(projectId, name,appType,flowId);
    }

    public void updateTaskName(@Param("taskId") long id, @Param("taskName") String taskName,@Param("appType")Integer appType) {
        scheduleTaskShadeDao.updateTaskName(id, taskName,appType);
    }


    /**
     * jobKey 格式：cronTrigger_taskId_time
     *
     * @param jobKey
     * @return
     * @see JobGraphBuilder#getSelfDependencyJobKeys(com.dtstack.task.domain.BatchJob, com.dtstack.task.server.parser.ScheduleCron, java.lang.String)
     */
    @Forbidden
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


    public ScheduleTaskShade getBatchTaskById(@Param("id") Long taskId, @Param("appType")Integer appType) {
        ScheduleTaskShade taskShade = scheduleTaskShadeDao.getOne(taskId, appType);
        if (taskShade == null || Deleted.DELETED.getStatus().equals(taskShade.getIsDeleted())) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        return taskShade;
    }

    public JSONObject queryTasks(@Param("tenantId") Long tenantId,
                                 @Param("projectId") Long projectId,
                                 @Param("name") String name,
                                 @Param("ownerId") Long ownerId,
                                 @Param("startTime") Long startTime,
                                 @Param("endTime") Long endTime,
                                 @Param("scheduleStatus") Integer scheduleStatus,
                                 @Param("taskType") String taskTypeList,
                                 @Param("taskPeriodId") String periodTypeList,
                                 @Param("currentPage") Integer currentPage,
                                 @Param("pageSize") Integer pageSize, @Param("searchType")  String  searchType,
                                 @Param("appType")Integer appType){


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

        List<com.dtstack.engine.master.vo.ScheduleTaskVO> vos = new ArrayList<>(batchTasks.size());

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

        PageResult<List<com.dtstack.engine.master.vo.ScheduleTaskVO>> pageResult = new PageResult<>(vos, count, pageQuery);
        JSONObject result = new JSONObject();
        result.put("currentPage", pageResult.getCurrentPage());
        result.put("data", pageResult.getData());
        result.put("pageSize", pageResult.getPageSize());
        result.put("totalCount", pageResult.getTotalCount());
        result.put("totalPage", pageResult.getTotalPage());
        result.put("attachment", pageResult.getAttachment());
        result.put("publishedTasks", publishedTasks);
        return result;
    }

    private List<com.dtstack.engine.master.vo.ScheduleTaskVO> dealFlowWorkSubTasks(List<com.dtstack.engine.master.vo.ScheduleTaskVO> vos, Integer appType) {
        Map<Long, com.dtstack.engine.master.vo.ScheduleTaskVO> record = Maps.newHashMap();
        Map<Long, Integer> voIndex = Maps.newHashMap();
        vos.forEach(task -> voIndex.put(task.getId(), vos.indexOf(task)));
        Iterator<com.dtstack.engine.master.vo.ScheduleTaskVO> iterator = vos.iterator();
        List<com.dtstack.engine.master.vo.ScheduleTaskVO> vosCopy = new ArrayList<>(vos);
        while (iterator.hasNext()) {
            com.dtstack.engine.master.vo.ScheduleTaskVO vo = iterator.next();
            Long flowId = vo.getFlowId();
            if (flowId > 0) {
                if (record.containsKey(flowId)) {
                    com.dtstack.engine.master.vo.ScheduleTaskVO flowVo = record.get(flowId);
                    flowVo.getRelatedTasks().add(vo);
                    iterator.remove();
                } else {
                    com.dtstack.engine.master.vo.ScheduleTaskVO flowVo;
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


    @Forbidden
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
    public void frozenTask(@Param("taskIdList") List<Long> taskIdList, @Param("scheduleStatus") int scheduleStatus,
                           @Param("projectId") Long projectId, @Param("userId") Long userId,
                           @Param("appType") Integer appType) {
        scheduleTaskShadeDao.batchUpdateTaskScheduleStatus(taskIdList,scheduleStatus,appType);
    }


    /**
     * 查询工作流下子节点
     * @param taskId
     * @return
     */
    public ScheduleTaskVO dealFlowWorkTask(@Param("taskId") Long taskId, @Param("appType")Integer appType,@Param("taskTypes")List<Integer> taskTypes,@Param("ownerId")Long ownerId) {
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
    public List<ScheduleTaskShade> getFlowWorkSubTasks(@Param("taskId") Long taskId, @Param("appType") Integer appType,@Param("taskTypes")List<Integer> taskTypes,@Param("ownerId")Long ownerId) {
        ScheduleTaskShadeDTO batchTaskShadeDTO = new ScheduleTaskShadeDTO();
        batchTaskShadeDTO.setIsDeleted(Deleted.NORMAL.getStatus());
        batchTaskShadeDTO.setFlowId(taskId);
        batchTaskShadeDTO.setAppType(appType);
        batchTaskShadeDTO.setTaskTypeList(taskTypes);
        batchTaskShadeDTO.setOwnerUserId(ownerId);
        PageQuery<ScheduleTaskShadeDTO> pageQuery = new PageQuery<>(batchTaskShadeDTO);
        return scheduleTaskShadeDao.generalQuery(pageQuery);
    }


    public ScheduleTaskShade findTaskId(@Param("taskId") Long taskId, @Param("isDeleted")Integer isDeleted, @Param("appType") Integer appType) {
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
    public List<ScheduleTaskShade> findTaskIds(@Param("taskIds") List<Long> taskIds, @Param("isDeleted")Integer isDeleted, @Param("appType") Integer appType, @Param("isSimple") boolean isSimple) {
        if(CollectionUtils.isEmpty(taskIds)){
            return null;
        }
        if(isSimple){
            return scheduleTaskShadeDao.listSimpleByTaskIds(taskIds,isDeleted);
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
    public void info(@Param("taskId") Long taskId,@Param("appType") Integer appType,@Param("extraInfo")String info) {
        JSONObject extInfo = JSONObject.parseObject(scheduleTaskShadeDao.getExtInfoByTaskId(taskId, appType));
        if (Objects.isNull(extInfo)) {
            extInfo = new JSONObject();
        }
        extInfo.put(TaskConstant.INFO, info);
        scheduleTaskShadeDao.updateTaskExtInfo(taskId, appType, extInfo.toJSONString());
    }


    public List<Map<String, Object>> listDependencyTask(@Param("taskIds") List<Long> taskId, @Param("appType") Integer appType, @Param("name") String name, @Param("projectId") Long projectId) {
        return scheduleTaskShadeDao.listDependencyTask(projectId, name, taskId);
    }


    public List<Map<String, Object>> listByTaskIdsNotIn(@Param("taskIds") List<Long> taskId, @Param("appType") Integer appType, @Param("projectId") Long projectId) {
        return scheduleTaskShadeDao.listByTaskIdsNotIn(projectId, taskId);
    }

    @Forbidden
    public ScheduleTaskShade getById(Long id ){
        return scheduleTaskShadeDao.getById(id);
    }
}
