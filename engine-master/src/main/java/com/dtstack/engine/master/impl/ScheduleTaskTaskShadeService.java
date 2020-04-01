package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.common.enums.DisplayDirect;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.BatchTaskTaskShadeDao;
import com.dtstack.engine.api.domain.BatchTaskShade;
import com.dtstack.engine.api.domain.BatchTaskTaskShade;
import com.dtstack.engine.master.vo.BatchTaskVO;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Service
public class ScheduleTaskTaskShadeService implements com.dtstack.engine.api.service.ScheduleTaskTaskShadeService {

    private static final Long IS_WORK_FLOW_SUBNODE = 0L;

    @Autowired
    private BatchTaskTaskShadeDao batchTaskTaskShadeDao;

    @Autowired
    private ScheduleTaskShadeService taskShadeService;

    public void clearDataByTaskId(@Param("taskId") Long taskId,@Param("appType")Integer appType) {
        batchTaskTaskShadeDao.deleteByTaskId(taskId,appType);
    }

    @Transactional
    public void saveTaskTaskList(@Param("taskTask") String taskLists) {
        if(StringUtils.isBlank(taskLists)){
            return;
        }
        List<BatchTaskTaskShade> taskTaskList = JSONObject.parseArray(taskLists, BatchTaskTaskShade.class);
        Map<String,BatchTaskTaskShade> keys = new HashMap<>();
        // 去重
        for (BatchTaskTaskShade batchTaskTaskShade : taskTaskList) {
            keys.put(String.format("%s.%s.%s",batchTaskTaskShade.getTaskId(),batchTaskTaskShade.getParentTaskId(),batchTaskTaskShade.getProjectId()),batchTaskTaskShade);
            Preconditions.checkNotNull(batchTaskTaskShade.getTaskId());
            Preconditions.checkNotNull(batchTaskTaskShade.getAppType());
            //清除原来关系
            batchTaskTaskShadeDao.deleteByTaskId(batchTaskTaskShade.getTaskId(), batchTaskTaskShade.getAppType());
        }

        // 保存现有任务关系
        for (BatchTaskTaskShade taskTaskShade : keys.values()) {
            batchTaskTaskShadeDao.insert(taskTaskShade);
        }
    }

    public List<BatchTaskTaskShade> getAllParentTask(@Param("taskId") Long taskId) {
        return batchTaskTaskShadeDao.listParentTask(taskId);
    }


    public BatchTaskVO displayOffSpring(@Param("taskId") Long taskId,
                                        @Param("projectId") Long projectId,
                                        @Param("userId") Long userId,
                                        @Param("level") Integer level,
                                        @Param("type") Integer directType,@Param("appType")Integer appType) {

        BatchTaskShade task = null;
        try {
            task = taskShadeService.getBatchTaskById(taskId,appType);
        } catch (RdosDefineException rdosDefineException) {
            if (rdosDefineException.getErrorCode().equals(ErrorCode.CAN_NOT_FIND_TASK)) {
                return null;
            }
            throw rdosDefineException;
        }
        if (task == null) {
            return null;
        }

        if (level == null || level < 1) {
            level = 1;
        }

        if(directType == null){
            directType = 0;
        }

        return this.getOffSpring(task, level, directType, projectId,appType);
    }

    /**
     * 展开依赖节点
     * 0 展开上下游, 1:展开上游 2:展开下游
     * @author toutian
     */
    private BatchTaskVO getOffSpring(BatchTaskShade taskShade, int level, Integer directType, Long currentProjectId,Integer appType) {

        BatchTaskVO vo = new BatchTaskVO(taskShade, true);
        vo.setCurrentProject(currentProjectId.equals(taskShade.getProjectId()));
        if (taskShade.getTaskType().intValue() == EJobType.WORK_FLOW.getVal()) {
            BatchTaskVO subTaskVO = getAllFlowSubTasks(taskShade.getTaskId(),taskShade.getAppType());
            vo.setSubNodes(subTaskVO);
        }
        if (level == 0) {
            return vo;
        }

        level--;

        List<BatchTaskTaskShade> taskTasks = null;
        List<BatchTaskTaskShade> childTaskTasks = null;

        if(taskShade.getTaskType().intValue() != EJobType.WORK_FLOW.getVal() &&
                !taskShade.getFlowId().equals(IS_WORK_FLOW_SUBNODE)){
            //若为工作流子节点，则展开工作流全部子节点
            return getOnlyAllFlowSubTasks(taskShade.getFlowId(),appType);
        }

        if(DisplayDirect.FATHER_CHILD.getType().equals(directType) || DisplayDirect.FATHER.getType().equals(directType)){//展开上游节点
            taskTasks = batchTaskTaskShadeDao.listParentTask(taskShade.getTaskId());
        }

        if(DisplayDirect.FATHER_CHILD.getType().equals(directType) || DisplayDirect.CHILD.getType().equals(directType)){//展开下游节点
            childTaskTasks = batchTaskTaskShadeDao.listChildTask(taskShade.getTaskId());
        }

        if (CollectionUtils.isEmpty(taskTasks) && CollectionUtils.isEmpty(childTaskTasks)) {
            return vo;
        }

        List<com.dtstack.engine.api.vo.BatchTaskVO> parentTaskList = null;
        List<com.dtstack.engine.api.vo.BatchTaskVO> childTaskList = null;
        if(!CollectionUtils.isEmpty(taskTasks)){
            Set<Long> taskIds = new HashSet<>(taskTasks.size());
            taskTasks.forEach(taskTask -> taskIds.add(taskTask.getParentTaskId()));
            parentTaskList = getRefTask(taskIds, level, DisplayDirect.FATHER.getType(), currentProjectId,appType);
            if(parentTaskList != null){
                vo.setTaskVOS(parentTaskList);
            }
        }

        if(!CollectionUtils.isEmpty(childTaskTasks)){
            Set<Long> taskIds = new HashSet<>(childTaskTasks.size());
            childTaskTasks.forEach(taskTask -> taskIds.add(taskTask.getTaskId()));
            childTaskList = getRefTask(taskIds, level, DisplayDirect.CHILD.getType(), currentProjectId,appType);
            if(childTaskList != null){
                vo.setSubTaskVOS(childTaskList);
            }
        }

        return vo;
    }

    public List<com.dtstack.engine.api.vo.BatchTaskVO> getRefTask(Set<Long> taskIds, int level, Integer directType, Long currentProjectId, Integer appType){

        //获得所有父节点task
        List<BatchTaskShade> tasks = taskShadeService.getTaskByIds(new ArrayList<>(taskIds),appType);
        if (CollectionUtils.isEmpty(tasks)) {
            return null;
        }

        List<com.dtstack.engine.api.vo.BatchTaskVO> refTaskVoList = new ArrayList<>(tasks.size());
        for (BatchTaskShade task : tasks) {
            refTaskVoList.add(this.getOffSpring(task, level, directType, currentProjectId,appType));
        }

        return refTaskVoList;
    }

    /**
     * 获取工作流全部子节点信息 -- 依赖树
     *  ps- 不包括工作流父节点
     *
     * @param flowId 工作流父节点id
     * @return
     */
    @Forbidden
    private BatchTaskVO getOnlyAllFlowSubTasks(Long flowId,Integer appType) {
        BatchTaskVO vo = new BatchTaskVO();
        BatchTaskShade beginTaskShade = taskShadeService.getWorkFlowTopNode(flowId);
        if(beginTaskShade!=null) {
            vo = getFlowWorkOffSpring(beginTaskShade, 1, DisplayDirect.CHILD.getType(),appType);
        }
        return vo;
    }


    /**
     * 查询工作流全部节点信息 -- 依赖树
     *
     * @param taskId
     * @return
     */
    public BatchTaskVO getAllFlowSubTasks(@Param("taskId") Long taskId,@Param("appType") Integer appType) {
        BatchTaskShade task = taskShadeService.getBatchTaskById(taskId,appType);
        if (task == null) {
            return null;
        }
        BatchTaskVO parentNode = new BatchTaskVO(task, true);
        BatchTaskVO vo = new BatchTaskVO();

        BatchTaskShade beginTaskShade = taskShadeService.getWorkFlowTopNode(taskId);
        if(beginTaskShade!=null) {
            vo = getFlowWorkOffSpring(beginTaskShade, 1, DisplayDirect.CHILD.getType(),appType);
        }
        parentNode.setSubTaskVOS(Arrays.asList(vo));
        return parentNode;
    }

    /**
     * 向下展开工作流全部节点
     * @param taskShade
     * @param level
     * @param directType
     * @return
     */
    @Forbidden
    public BatchTaskVO getFlowWorkOffSpring(BatchTaskShade taskShade, int level, Integer directType,Integer appType) {
        BatchTaskVO vo = new BatchTaskVO(taskShade, true);
        List<BatchTaskTaskShade> childTaskTasks = null;
        childTaskTasks = batchTaskTaskShadeDao.listChildTask(taskShade.getTaskId());
        if (CollectionUtils.isEmpty(childTaskTasks)) {
            return vo;
        }
        Set<Long> taskIds = new HashSet<>(childTaskTasks.size());
        childTaskTasks.forEach(taskTask -> taskIds.add(taskTask.getTaskId()));
        List<com.dtstack.engine.api.vo.BatchTaskVO> childTaskList = getFlowWorkSubTasksRefTask(taskIds, level, DisplayDirect.CHILD.getType(),appType);
        if (childTaskList != null) {
            vo.setSubTaskVOS(childTaskList);
        }
        return vo;
    }

    @Forbidden
    public List<com.dtstack.engine.api.vo.BatchTaskVO> getFlowWorkSubTasksRefTask(Set<Long> taskIds, int level, Integer directType, Integer appType) {

        //获得所有父节点task
        List<BatchTaskShade> tasks = taskShadeService.getTaskByIds(new ArrayList<>(taskIds),appType);
        if (CollectionUtils.isEmpty(tasks)) {
            return null;
        }

        List<com.dtstack.engine.api.vo.BatchTaskVO> refTaskVoList = new ArrayList<>(tasks.size());
        for (BatchTaskShade task : tasks) {
            refTaskVoList.add(this.getFlowWorkOffSpring(task, level, directType,appType));
        }

        return refTaskVoList;
    }


}
