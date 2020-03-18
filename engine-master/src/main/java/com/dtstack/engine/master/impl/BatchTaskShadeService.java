package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.ESubmitStatus;
import com.dtstack.dtcenter.common.enums.Sort;
import com.dtstack.dtcenter.common.pager.PageQuery;
import com.dtstack.dtcenter.common.pager.PageResult;
import com.dtstack.dtcenter.common.util.MathUtil;
import com.dtstack.engine.common.annotation.Param;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.BatchTaskShadeDao;
import com.dtstack.engine.domain.BatchJob;
import com.dtstack.engine.domain.BatchTaskShade;
import com.dtstack.engine.dto.BatchTaskShadeDTO;
import com.dtstack.task.send.TaskUrlConstant;
import com.dtstack.engine.master.scheduler.JobGraphBuilder;
import com.dtstack.engine.master.vo.BatchTaskShadeVO;
import com.dtstack.engine.master.vo.BatchTaskVO;
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
public class BatchTaskShadeService {


    @Autowired
    private BatchTaskShadeDao batchTaskShadeDao;

    @Autowired
    private BatchTaskTaskShadeService taskTaskShadeService;

    private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * web 接口
     * 例如：离线计算BatchTaskService.publishTaskInfo 触发 batchTaskShade 保存task的必要信息
     */
    public void addOrUpdate(BatchTaskShadeDTO batchTaskShadeDTO) {
        //保存batch_task_shade
        if (batchTaskShadeDao.getOne(batchTaskShadeDTO.getTaskId(),batchTaskShadeDTO.getAppType()) != null) {
            batchTaskShadeDao.update(batchTaskShadeDTO);
        } else {
            batchTaskShadeDao.insert(batchTaskShadeDTO);
        }
    }

    /**
     * web 接口
     * task删除时触发同步清理
     */
    public void deleteTask(@Param("taskId") Long taskId, @Param("modifyUserId") long modifyUserId, @Param("appType") Integer appType) {
        batchTaskShadeDao.delete(taskId, modifyUserId,appType);
        taskTaskShadeService.clearDataByTaskId(taskId);
    }

    @Forbidden
    public List<BatchTaskShade> listTaskByType(Long projectId, Integer taskType, String taskName) {
        return batchTaskShadeDao.listByType(projectId, taskType, taskName);
    }

    /**
     * 获取所有需要需要生成调度的task 没有sqlText字段
     */
    @Forbidden
    public List<BatchTaskShade> listTaskByStatus(Long startId, Integer submitStatus, Integer projectSubmitStatus, Integer batchTaskSize) {
        return batchTaskShadeDao.listTaskByStatus(startId, submitStatus, projectSubmitStatus, batchTaskSize);
    }

    @Forbidden
    public Integer countTaskByStatus(Integer submitStatus, Integer projectSubmitStatus) {
        return batchTaskShadeDao.countTaskByStatus(submitStatus, projectSubmitStatus);
    }

    /**
     * 根据任务id获取对应的taskShade
     * @param taskIds
     * @return
     */
    public List<BatchTaskShade> getTaskByIds(List<Long> taskIds,Integer appType) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return Collections.EMPTY_LIST;
        }

        return batchTaskShadeDao.listByTaskIds(taskIds, Deleted.NORMAL.getStatus(),appType);
    }

    /**
     * ps- 省略了一些大字符串 如 sql_text、task_params
     *
     * @param taskIdArray
     * @return
     */
    @Forbidden
    public List<BatchTaskShade> getSimpleTaskRangeAllByIds(List<Long> taskIdArray) {
        if (CollectionUtils.isEmpty(taskIdArray)) {
            return Collections.EMPTY_LIST;
        }

        return batchTaskShadeDao.listSimpleByTaskIds(taskIdArray, null);
    }

    /**
     * 数据开发-根据项目id,任务名 获取任务列表
     *
     * @param projectId
     * @return
     * @author toutian
     */
    public List<BatchTaskShade> getTasksByName(@Param("projectId") long projectId,
                                               @Param("name") String name,@Param("appType") Integer appType) {
        return batchTaskShadeDao.listByNameLike(projectId, name,appType,null);
    }

    public BatchTaskShade getByName(@Param("projectId") long projectId,
                                    @Param("name") String name) {
        return batchTaskShadeDao.getByName(projectId, name);
    }

    public void updateTaskName(@Param("taskId") long id, @Param("taskName") String taskName,@Param("appType")Integer appType) {
        batchTaskShadeDao.updateTaskName(id, taskName,appType);
    }


    /**
     * jobKey 格式：cronTrigger_taskId_time
     *
     * @param jobKey
     * @return
     * @see JobGraphBuilder#getSelfDependencyJobKeys(BatchJob, com.dtstack.engine.master.parser.ScheduleCron, java.lang.String)
     */
    @Forbidden
    public String getTaskNameByJobKey(String jobKey,Integer appType) {
        String[] jobKeySplit = jobKey.split("_");
        if (jobKeySplit.length < 3) {
            return "";
        }

        String taskIdStr = jobKeySplit[jobKeySplit.length - 2];
        Long taskShadeId = MathUtil.getLongVal(taskIdStr);
        BatchTaskShade taskShade = batchTaskShadeDao.getById(taskShadeId);
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
    public BatchTaskShade getWorkFlowTopNode(Long taskId) {
        if (taskId != null) {
            return batchTaskShadeDao.getWorkFlowTopNode(taskId);
        } else {
            return null;
        }
    }

    /**
     * 分页查询已提交的任务
     */
    public PageResult<List<BatchTaskShadeVO>> pageQuery(BatchTaskShadeDTO dto) {
        PageQuery<BatchTaskShadeDTO> query = new PageQuery<>(dto.getPageIndex(),dto.getPageSize(),"gmt_modified",dto.getSort());
        query.setModel(dto);
        Integer count = batchTaskShadeDao.simpleCount(dto);
        List<BatchTaskShadeVO> data = new ArrayList<>();
        if (count > 0) {
            List<BatchTaskShade> taskShades = batchTaskShadeDao.simpleQuery(query);
            for (BatchTaskShade taskShade : taskShades) {
                BatchTaskShadeVO taskShadeVO = new BatchTaskShadeVO();
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


    public BatchTaskShade getBatchTaskById(@Param("id") Long taskId,@Param("appType")Integer appType) {
        BatchTaskShade taskShade = batchTaskShadeDao.getOne(taskId, appType);
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


        BatchTaskShadeDTO batchTaskDTO = new BatchTaskShadeDTO();
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

        PageQuery<BatchTaskShadeDTO> pageQuery = new PageQuery<>(currentPage, pageSize, "gmt_modified", Sort.DESC.name());
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
        List<BatchTaskShade> batchTasks = batchTaskShadeDao.generalQuery(pageQuery);

        int count = batchTaskShadeDao.generalCount(batchTaskDTO);

        List<BatchTaskVO> vos = new ArrayList<>(batchTasks.size());

        for (BatchTaskShade batchTask : batchTasks) {
            vos.add(new BatchTaskVO(batchTask,true));
        }
        if (queryAll) {
            vos = dealFlowWorkSubTasks(vos,appType);
        } else {
            //默认不查询全部工作流子节点
            //vos = dealFlowWorkTasks(vos);
        }

        int publishedTasks = batchTaskShadeDao.countPublishToProduce(projectId,appType);

        PageResult<List<BatchTaskVO>> pageResult = new PageResult<>(vos, count, pageQuery);
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

    private List<BatchTaskVO> dealFlowWorkSubTasks(List<BatchTaskVO> vos,Integer appType) {
        Map<Long, BatchTaskVO> record = Maps.newHashMap();
        Map<Long, Integer> voIndex = Maps.newHashMap();
        vos.forEach(task -> voIndex.put(task.getId(), vos.indexOf(task)));
        Iterator<BatchTaskVO> iterator = vos.iterator();
        List<BatchTaskVO> vosCopy = new ArrayList<>(vos);
        while (iterator.hasNext()) {
            BatchTaskVO vo = iterator.next();
            Long flowId = vo.getFlowId();
            if (flowId > 0) {
                if (record.containsKey(flowId)) {
                    BatchTaskVO flowVo = record.get(flowId);
                    flowVo.getRelatedTasks().add(vo);
                    iterator.remove();
                } else {
                    BatchTaskVO flowVo;
                    if (voIndex.containsKey(flowId)) {
                        flowVo = vosCopy.get(voIndex.get(flowId));
                        flowVo.setRelatedTasks(Lists.newArrayList(vo));
                        iterator.remove();
                    } else {
                        BatchTaskShade flow = batchTaskShadeDao.getOne(flowId,appType);
                        flowVo = new BatchTaskVO(flow, true);
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
        batchTaskShadeDao.batchUpdateTaskScheduleStatus(taskIdList,scheduleStatus,appType);
    }


    /**
     * 查询工作流下子节点
     * @param taskId
     * @return
     */
    public BatchTaskVO dealFlowWorkTask(@Param("taskId") Long taskId,@Param("appType")Integer appType) {
        BatchTaskShade taskShade = batchTaskShadeDao.getOne(taskId,appType);
        if (taskShade == null) {
            return null;
        }
        BatchTaskVO vo = new BatchTaskVO(taskShade, true);
        if (EJobType.WORK_FLOW.getVal().intValue() == vo.getTaskType()) {
            List<BatchTaskShade> subtasks = this.getFlowWorkSubTasks(vo.getTaskId(),appType);
            if (CollectionUtils.isNotEmpty(subtasks)) {
                List<BatchTaskVO> list = Lists.newArrayList();
                subtasks.forEach(task -> list.add(new BatchTaskVO(task,true)));
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
    public List<BatchTaskShade> getFlowWorkSubTasks(@Param("taskId") Long taskId,@Param("appType") Integer appType) {
        BatchTaskShadeDTO batchTaskShadeDTO = new BatchTaskShadeDTO();
        batchTaskShadeDTO.setIsDeleted(Deleted.NORMAL.getStatus());
        batchTaskShadeDTO.setFlowId(taskId);
        batchTaskShadeDTO.setAppType(appType);
        PageQuery<BatchTaskShadeDTO> pageQuery = new PageQuery<>(batchTaskShadeDTO);
        return batchTaskShadeDao.generalQuery(pageQuery);
    }


    public BatchTaskShade findTaskId(@Param("taskId") Long taskId,@Param("isDeleted")Integer isDeleted,@Param("appType") Integer appType) {
        if(Objects.isNull(taskId)){
            return null;
        }
        List<BatchTaskShade> batchTaskShades = batchTaskShadeDao.listByTaskIds(Lists.newArrayList(taskId), isDeleted,appType);
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
    public List<BatchTaskShade> findTaskIds(@Param("taskIds") List<Long> taskIds,@Param("isDeleted")Integer isDeleted,@Param("appType") Integer appType,@Param("isSimple") boolean isSimple) {
        if(CollectionUtils.isEmpty(taskIds)){
            return null;
        }
        if(isSimple){
            return batchTaskShadeDao.listSimpleByTaskIds(taskIds,isDeleted);
        }
        return  batchTaskShadeDao.listByTaskIds(taskIds, isDeleted,appType);
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
        JSONObject extInfo = JSONObject.parseObject(batchTaskShadeDao.getExtInfoByTaskId(taskId, appType));
        if (Objects.isNull(extInfo)) {
            extInfo = new JSONObject();
        }
        extInfo.put(TaskUrlConstant.INFO, info);
        batchTaskShadeDao.updateTaskExtInfo(taskId, appType, extInfo.toJSONString());
    }


    public List<Map<String, Object>> listDependencyTask(@Param("taskIds") List<Long> taskId, @Param("appType") Integer appType, @Param("name") String name, @Param("projectId") Long projectId) {
        return batchTaskShadeDao.listDependencyTask(projectId, name, taskId);
    }


    public List<Map<String, Object>> listByTaskIdsNotIn(@Param("taskIds") List<Long> taskId, @Param("appType") Integer appType, @Param("projectId") Long projectId) {
        return batchTaskShadeDao.listByTaskIdsNotIn(projectId, taskId);
    }

    @Forbidden
    public BatchTaskShade getById(Long id ){
        return batchTaskShadeDao.getById(id);
    }
}
