package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.api.enums.TaskRuleEnum;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.common.enums.DisplayDirect;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ScheduleEngineProjectDao;
import com.dtstack.engine.dao.ScheduleTaskTaskShadeDao;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.domain.ScheduleEngineProject;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Service
public class ScheduleTaskTaskShadeService {

    private static final Long IS_WORK_FLOW_SUBNODE = 0L;

    private static final Logger logger = LoggerFactory.getLogger(ScheduleTaskTaskShadeService.class);

    @Autowired
    private ScheduleTaskTaskShadeDao scheduleTaskTaskShadeDao;

    @Autowired
    private ScheduleTaskShadeService taskShadeService;

    @Autowired
    private EnvironmentContext context;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private ScheduleEngineProjectDao scheduleEngineProjectDao;

    public void clearDataByTaskId( Long taskId,Integer appType) {
        scheduleTaskTaskShadeDao.deleteByTaskId(taskId,appType);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveTaskTaskList( String taskLists) {
        if(StringUtils.isBlank(taskLists)){
            return;
        }
        try {
            List<ScheduleTaskTaskShade> taskTaskList = JSONObject.parseArray(taskLists, ScheduleTaskTaskShade.class);
            if(CollectionUtils.isEmpty(taskTaskList)){
                return;
            }
            Map<String, ScheduleTaskTaskShade> keys = new HashMap<>(16);
            // 去重
            for (ScheduleTaskTaskShade scheduleTaskTaskShade : taskTaskList) {
                keys.put(String.format("%s.%s.%s", scheduleTaskTaskShade.getTaskId(), scheduleTaskTaskShade.getParentTaskId(), scheduleTaskTaskShade.getProjectId()), scheduleTaskTaskShade);
                Preconditions.checkNotNull(scheduleTaskTaskShade.getTaskId());
                Preconditions.checkNotNull(scheduleTaskTaskShade.getAppType());
                //清除原来关系
                scheduleTaskTaskShadeDao.deleteByTaskId(scheduleTaskTaskShade.getTaskId(), scheduleTaskTaskShade.getAppType());
            }
            // 保存现有任务关系
            for (ScheduleTaskTaskShade taskTaskShade : keys.values()) {
                if (taskTaskShade.getParentAppType() == null) {
                    taskTaskShade.setParentAppType(taskTaskShade.getAppType());
                }
                scheduleTaskTaskShadeDao.insert(taskTaskShade);
            }
        } catch (Exception e) {
            logger.error("saveTaskTaskList error:{}", ExceptionUtil.getErrorMessage(e));
            throw new RdosDefineException("保存任务依赖列表异常");
        }
    }

    public List<ScheduleTaskTaskShade> getAllParentTask( Long taskId,Integer appType) {
        return scheduleTaskTaskShadeDao.listParentTask(taskId,appType);
    }


    public com.dtstack.engine.master.vo.ScheduleTaskVO displayOffSpring( Long taskId,
                                                                         Long projectId,
                                                                         Integer level,
                                                                         Integer directType, Integer appType) {
        ScheduleTaskShade task = taskShadeService.getBatchTaskById(taskId,appType);
        if(null == task){
            return null;
        }

        if (task == null) {
            return null;
        }

        if (level == null || level < 1) {
            level = 1;
        }
        if( level > context.getJobJobLevel()){
            level = context.getJobJobLevel();
        }
        if(directType == null){
            directType = 0;
        }
        if(context.getUseOptimize()) {
            return this.getOffSpringNew(task, level, directType, projectId, appType, new ArrayList<>());
        }else{
            return this.getOffSpring(task,level,directType,projectId,appType);
        }
    }

    /**
     * 展开依赖节点,优化后
     * 0 展开上下游, 1:展开上游 2:展开下游
     *
     * @author toutian
     */
    private com.dtstack.engine.master.vo.ScheduleTaskVO getOffSpringNew(ScheduleTaskShade taskShade, int level,
                                                                        Integer directType, Long currentProjectId, Integer appType, List<String> taskIdRelations) {
        //1、如果是工作流子节点,则展开全部工作流子节点
        if (!taskShade.getTaskType().equals(EScheduleJobType.WORK_FLOW.getVal()) &&
                !taskShade.getFlowId().equals(IS_WORK_FLOW_SUBNODE)) {
            //若为工作流子节点，则展开工作流全部子节点
            return getOnlyAllFlowSubTasksNew(taskShade.getFlowId(), appType);
        }
        com.dtstack.engine.master.vo.ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO(taskShade, true);
        setTenantAndProjeck(vo,taskShade);
        vo.setCurrentProject(currentProjectId.equals(taskShade.getProjectId()));
        if (EScheduleJobType.WORK_FLOW.getVal().equals(taskShade.getTaskType())) {
            //2、如果是工作流，则获取工作流子节点,包括工作流本身
            //构建父节点信息
            com.dtstack.engine.master.vo.ScheduleTaskVO parentNode = new com.dtstack.engine.master.vo.ScheduleTaskVO(taskShade, true);
            com.dtstack.engine.master.vo.ScheduleTaskVO onlyAllFlowSubTasksNew = getOnlyAllFlowSubTasksNew(taskShade.getTaskId(), taskShade.getAppType());
            parentNode.setSubTaskVOS(Arrays.asList(onlyAllFlowSubTasksNew));
            vo.setSubNodes(parentNode);
        }
        if (level == 0) {
            //控制最多展示多少层，防止一直循环。
            return vo;
        }
        level--;
        List<ScheduleTaskTaskShade> taskTasks = null;
        List<ScheduleTaskTaskShade> childTaskTasks = null;
        //展开上游节点
        if (DisplayDirect.FATHER_CHILD.getType().equals(directType) || DisplayDirect.FATHER.getType().equals(directType)) {
            taskTasks = scheduleTaskTaskShadeDao.listParentTask(taskShade.getTaskId(), taskShade.getAppType());
            if (checkIsLoop(taskIdRelations, taskTasks)) {
                //成环了，直接返回
                return vo;
            }
        }
        //展开下游节点
        if (DisplayDirect.FATHER_CHILD.getType().equals(directType) || DisplayDirect.CHILD.getType().equals(directType)) {
            childTaskTasks = scheduleTaskTaskShadeDao.listChildTask(taskShade.getTaskId(), taskShade.getAppType());
            if (checkIsLoop(taskIdRelations, childTaskTasks)) {
                //成环了，直接返回
                return vo;
            }
        }
        if (CollectionUtils.isEmpty(taskTasks) && CollectionUtils.isEmpty(childTaskTasks)) {
            return vo;
        }
        List<ScheduleTaskVO> parentTaskList = null;
        List<ScheduleTaskVO> childTaskList = null;
        List<ScheduleTaskVO> taskRuleList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(taskTasks)) {
            //向上展开
            Map<Integer, List<ScheduleTaskTaskShade>> listMap = taskTasks.stream().collect(Collectors.groupingBy(ScheduleTaskTaskShade::getParentAppType));
            parentTaskList = getRefTaskNew(listMap, level, DisplayDirect.FATHER.getType(), currentProjectId, taskIdRelations,taskRuleList);
            if (CollectionUtils.isNotEmpty(parentTaskList) && parentTaskList.get(0) != null) {
                vo.setTaskVOS(parentTaskList);
            }

            if (CollectionUtils.isNotEmpty(taskRuleList) && taskRuleList.get(0) != null) {
                vo.setTaskRuleList(taskRuleList);
            }
        }
        if (!CollectionUtils.isEmpty(childTaskTasks)) {
            //向下展开
            Map<Integer, List<ScheduleTaskTaskShade>> listMap = childTaskTasks.stream().collect(Collectors.groupingBy(ScheduleTaskTaskShade::getAppType));
            childTaskList = getRefTaskNew(listMap, level, DisplayDirect.CHILD.getType(), currentProjectId, taskIdRelations,taskRuleList);
            if (CollectionUtils.isNotEmpty(childTaskList) && childTaskList.get(0) != null) {
                vo.setSubTaskVOS(childTaskList);
            }

            if (CollectionUtils.isNotEmpty(taskRuleList) && taskRuleList.get(0) != null) {
                vo.setTaskRuleList(taskRuleList);
            }
        }
        return vo;
    }

    /**
     * @author newman
     * @Description 检测任务是否成环
     * @Date 2021/1/6 8:23 下午
     * @param taskIdRelations:
     * @param taskTasks:
     * @return: void
     **/
    private Boolean checkIsLoop(List<String> taskIdRelations, List<ScheduleTaskTaskShade> taskTasks) {

        if(CollectionUtils.isNotEmpty(taskTasks)) {
            for (ScheduleTaskTaskShade taskTask : taskTasks) {
                String taskRelation = taskTask.getTaskId() + "&" + taskTask.getAppType() + "-" + taskTask.getParentTaskId() + "&" + taskTask.getParentAppType();
                if (taskIdRelations.contains(taskRelation)) {
                    logger.error("该任务成环了,taskRelation:{}", taskRelation);
                    return true;
                } else {
                    taskIdRelations.add(taskRelation);
                }
            }
        }
        return false;
    }

    /**
     * 展开依赖节点
     * 0 展开上下游, 1:展开上游 2:展开下游
     * @author toutian
     */
    private com.dtstack.engine.master.vo.ScheduleTaskVO getOffSpring(ScheduleTaskShade taskShade, int level, Integer directType, Long currentProjectId, Integer appType) {

        com.dtstack.engine.master.vo.ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO(taskShade, true);
        vo.setCurrentProject(currentProjectId.equals(taskShade.getProjectId()));
        setTenantAndProjeck(vo,taskShade);
        if (EScheduleJobType.WORK_FLOW.getVal().equals(taskShade.getTaskType())) {
            //如果是工作流，则获取工作流及其子节点
            com.dtstack.engine.master.vo.ScheduleTaskVO subTaskVO = getAllFlowSubTasks(taskShade.getTaskId(),taskShade.getAppType());
            vo.setSubNodes(subTaskVO);
        }
        if (level == 0) {
            //控制最多展示多少层，防止一直循环。
            return vo;
        }
        level--;
        List<ScheduleTaskTaskShade> taskTasks = null;
        List<ScheduleTaskTaskShade> childTaskTasks = null;
        if(taskShade.getTaskType().intValue() != EScheduleJobType.WORK_FLOW.getVal() &&
                !taskShade.getFlowId().equals(IS_WORK_FLOW_SUBNODE)){
            //若为工作流子节点，则展开工作流全部子节点
            return getOnlyAllFlowSubTasks(taskShade.getFlowId(),appType);
        }
        //展开上游节点
        if(DisplayDirect.FATHER_CHILD.getType().equals(directType) || DisplayDirect.FATHER.getType().equals(directType)){
            taskTasks = scheduleTaskTaskShadeDao.listParentTask(taskShade.getTaskId(),taskShade.getAppType());
        }
        //展开下游节点
        if(DisplayDirect.FATHER_CHILD.getType().equals(directType) || DisplayDirect.CHILD.getType().equals(directType)){
            childTaskTasks = scheduleTaskTaskShadeDao.listChildTask(taskShade.getTaskId(),taskShade.getAppType());
        }
        if (CollectionUtils.isEmpty(taskTasks) && CollectionUtils.isEmpty(childTaskTasks)) {
            return vo;
        }
        List<ScheduleTaskVO> parentTaskList = null;
        List<ScheduleTaskVO> childTaskList = null;
        List<ScheduleTaskVO> ruleTaskList = Lists.newArrayList();
        if(!CollectionUtils.isEmpty(taskTasks)){
            //向上展开
            Set<Long> taskIds = new HashSet<>(taskTasks.size());
            taskTasks.forEach(taskTask -> taskIds.add(taskTask.getParentTaskId()));
            parentTaskList = getRefTask(taskIds, level, DisplayDirect.FATHER.getType(), currentProjectId,appType,ruleTaskList);
            if(parentTaskList != null){
                vo.setTaskVOS(parentTaskList);
            }
        }
        if(!CollectionUtils.isEmpty(childTaskTasks)){
            //向下展开
            Set<Long> taskIds = new HashSet<>(childTaskTasks.size());
            childTaskTasks.forEach(taskTask -> taskIds.add(taskTask.getTaskId()));
            childTaskList = getRefTask(taskIds, level, DisplayDirect.CHILD.getType(), currentProjectId,appType,ruleTaskList);
            if(childTaskList != null){
                vo.setSubTaskVOS(childTaskList);
            }
        }
        if (CollectionUtils.isEmpty(ruleTaskList)) {
            vo.setTaskRuleList(ruleTaskList);
        }

        return vo;
    }

    private void setTenantAndProjeck(com.dtstack.engine.master.vo.ScheduleTaskVO vo, ScheduleTaskShade taskShade) {
        Tenant byDtUicTenantId = tenantDao.getByDtUicTenantId(taskShade.getDtuicTenantId());

        if (byDtUicTenantId != null) {
            vo.setTenantName(byDtUicTenantId.getTenantName());
        }

        ScheduleEngineProject projectByProjectIdAndApptype = scheduleEngineProjectDao.getProjectByProjectIdAndApptype(taskShade.getProjectId(), taskShade.getAppType());

        if (projectByProjectIdAndApptype != null) {
            vo.setProjectName(projectByProjectIdAndApptype.getProjectName());
        }
    }

    public List<ScheduleTaskVO> getRefTaskNew(Map<Integer, List<ScheduleTaskTaskShade>> listMap, int level, Integer directType,
                                              Long currentProjectId,List<String> taskIdRelations,List<ScheduleTaskVO> taskVOList){
        List<ScheduleTaskShade> tasks = getScheduleTaskShades(listMap,directType);

        if (CollectionUtils.isEmpty(tasks)) {
            return null;
        }
        List<ScheduleTaskVO> refTaskVoList = new ArrayList<>(tasks.size());
        for (ScheduleTaskShade task : tasks) {
            Integer taskRule = task.getTaskRule();

            if (TaskRuleEnum.NO_RULE.getCode().equals(taskRule)) {
                refTaskVoList.add(this.getOffSpringNew(task, level, directType, currentProjectId,task.getAppType(),taskIdRelations));
            } else {
                // 规则任务
                taskVOList.add(this.getOffSpringNew(task, level, directType, currentProjectId,task.getAppType(),taskIdRelations));
            }
        }
        return refTaskVoList;
    }

    private List<ScheduleTaskShade> getScheduleTaskShades(Map<Integer, List<ScheduleTaskTaskShade>> listMap,Integer directType) {
        List<ScheduleTaskShade> tasks = Lists.newArrayList();
        //获得所有父节点task
        for (Map.Entry<Integer, List<ScheduleTaskTaskShade>> entry : listMap.entrySet()) {
            Integer appType = entry.getKey();
            List<ScheduleTaskTaskShade> value = entry.getValue();
            if (CollectionUtils.isNotEmpty(value)) {
                List<Long> taskId = Lists.newArrayList();
                if (DisplayDirect.FATHER.getType().equals(directType)) {
                   taskId = value.stream().map(ScheduleTaskTaskShade::getParentTaskId).collect(Collectors.toList());
                } else if (DisplayDirect.CHILD.getType().equals(directType)) {
                   taskId = value.stream().map(ScheduleTaskTaskShade::getTaskId).collect(Collectors.toList());
                }
                tasks.addAll(taskShadeService.getTaskByIds(new ArrayList<>(taskId), appType));

            }
        }
        return tasks;
    }

    public List<ScheduleTaskVO> getRefTask(Set<Long> taskIds, int level, Integer directType, Long currentProjectId, Integer appType, List<ScheduleTaskVO> ruleTaskList){

        if (CollectionUtils.isEmpty(ruleTaskList)) {
            ruleTaskList = Lists.newArrayList();
        }

        //获得所有父节点task
        List<ScheduleTaskShade> tasks = taskShadeService.getTaskByIds(new ArrayList<>(taskIds),appType);
        if (CollectionUtils.isEmpty(tasks)) {
            return null;
        }
        List<ScheduleTaskVO> refTaskVoList = new ArrayList<>(tasks.size());
        for (ScheduleTaskShade task : tasks) {
            Integer taskRule = task.getTaskRule();

            if (TaskRuleEnum.NO_RULE.getCode().equals(taskRule)) {
                refTaskVoList.add(this.getOffSpring(task, level, directType, currentProjectId,task.getAppType()));
            } else {
                ruleTaskList.add(this.getOffSpring(task, level, directType, currentProjectId,task.getAppType()));
            }
        }
        return refTaskVoList;
    }

    /**
     * 获取工作流全部子节点信息 -- 依赖树
     *  ps- 不包括工作流父节点
     * 优化新
     * @param flowId 工作流父节点id
     * @return
     */
    private com.dtstack.engine.master.vo.ScheduleTaskVO getOnlyAllFlowSubTasksNew(Long flowId, Integer appType) {

        //工作流最多展开多少层
        Integer level = context.getWorkFlowLevel();
        com.dtstack.engine.master.vo.ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO();
        //获取工作流顶部节点
        ScheduleTaskShade beginTaskShade = taskShadeService.getWorkFlowTopNode(flowId,appType);
        if(beginTaskShade!=null) {
            //展开工作流全部节点，不包括工作流父节点
            vo = getFlowWorkOffSpringNew(beginTaskShade,appType,level,new ArrayList<>());
        }
        return vo;
    }

    /**
     * 获取工作流全部子节点信息 -- 依赖树
     *  ps- 不包括工作流父节点
     *
     * @param flowId 工作流父节点id
     * @return
     */
    private com.dtstack.engine.master.vo.ScheduleTaskVO getOnlyAllFlowSubTasks(Long flowId, Integer appType) {

        com.dtstack.engine.master.vo.ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO();
        //获取工作流顶部节点
        ScheduleTaskShade beginTaskShade = taskShadeService.getWorkFlowTopNode(flowId,appType);
        if(beginTaskShade!=null) {
            //展开工作流全部节点，不包括工作流父节点
            Integer workFlowLevel = context.getWorkFlowLevel();
            vo = getFlowWorkOffSpring(beginTaskShade, 1,appType,workFlowLevel);
        }
        return vo;
    }




    /**
     * 查询工作流全部节点信息 -- 依赖树
     *
     * @param taskId
     * @return
     */
    public com.dtstack.engine.master.vo.ScheduleTaskVO getAllFlowSubTasks( Long taskId,  Integer appType) {

        //工作流任务信息
        ScheduleTaskShade task = taskShadeService.getBatchTaskById(taskId,appType);
        //构建父节点信息
        com.dtstack.engine.master.vo.ScheduleTaskVO parentNode = new com.dtstack.engine.master.vo.ScheduleTaskVO(task, true);
        com.dtstack.engine.master.vo.ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO();
        //获取工作流最顶层结点
        ScheduleTaskShade beginTaskShade = taskShadeService.getWorkFlowTopNode(taskId,appType);
        if(beginTaskShade!=null) {
            //获取工作流下游结点
            Integer workFlowLevel = context.getWorkFlowLevel();
            vo = getFlowWorkOffSpring(beginTaskShade, 1,appType,workFlowLevel);
        }
        parentNode.setSubTaskVOS(Arrays.asList(vo));
        return parentNode;
    }

    /**
     * 向下展开工作流全部节点,增加max参数，万一循环依赖,优化的新方法
     * 最多查询10层就返回，防止内存溢出
     * @param taskShade
     * @param level
     * @param taskIdRelations 任务id关联列表，用来做成环检测
     * @return
     */
    private com.dtstack.engine.master.vo.ScheduleTaskVO getFlowWorkOffSpringNew(ScheduleTaskShade taskShade, Integer appType,int level,List<String> taskIdRelations) {


        com.dtstack.engine.master.vo.ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO(taskShade, true);
        //查询子任务列表
        List<ScheduleTaskTaskShade> childTaskTasks = scheduleTaskTaskShadeDao.listChildTask(taskShade.getTaskId(),taskShade.getAppType());
        if (CollectionUtils.isEmpty(childTaskTasks)) {
            return vo;
        }
        if(checkIsLoop(taskIdRelations,childTaskTasks)){
            //如果成环，则返回null
            return vo;
        }
        if(level<=0){
            return vo;
        }
        //获取子任务taskId集合
        Set<Long> taskIds = childTaskTasks.stream().map(ScheduleTaskTaskShade::getTaskId).collect(Collectors.toSet());
        level--;
        //获得所有父节点task
        List<ScheduleTaskShade> tasks = taskShadeService.getTaskByIds(new ArrayList<>(taskIds),appType);
        if (CollectionUtils.isEmpty(tasks)) {
            return vo;
        }
        List<ScheduleTaskVO> refTaskVoList = new ArrayList<>(tasks.size());
        for (ScheduleTaskShade task : tasks) {
            refTaskVoList.add(this.getFlowWorkOffSpringNew(task,appType,level,taskIdRelations));
        }
        if (CollectionUtils.isNotEmpty(refTaskVoList) && refTaskVoList.get(0)!=null) {
            vo.setSubTaskVOS(refTaskVoList);
        }
        return vo;
    }

    /**
     * 向下展开工作流全部节点,增加max参数，万一循环依赖
     * 最多查询10层就返回，防止内存溢出
     * @param taskShade
     * @param level
     * @return
     */
    private com.dtstack.engine.master.vo.ScheduleTaskVO getFlowWorkOffSpring(ScheduleTaskShade taskShade, int level, Integer appType,int max) {

        if(max<=0){
            return null;
        }
        com.dtstack.engine.master.vo.ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO(taskShade, true);
        List<ScheduleTaskTaskShade> childTaskTasks = null;
        //查询子任务列表
        childTaskTasks = scheduleTaskTaskShadeDao.listChildTask(taskShade.getTaskId(),taskShade.getAppType());
        if (CollectionUtils.isEmpty(childTaskTasks)) {
            return vo;
        }
        Set<Long> taskIds = new HashSet<>(childTaskTasks.size());
        //获取子任务id集合
        childTaskTasks.forEach(taskTask -> taskIds.add(taskTask.getTaskId()));
        max--;
        List<ScheduleTaskVO> childTaskList = getFlowWorkSubTasksRefTask(taskIds, level, DisplayDirect.CHILD.getType(),appType,max);
        if (childTaskList != null) {
            vo.setSubTaskVOS(childTaskList);
        }
        return vo;
    }

    /**
     * @author newman
     * @Description 获取所有工作流子节点的子任务
     * @Date 2020-12-17 10:25
     * @param taskIds:
     * @param level:
     * @param directType:
     * @param appType:
     * @return: java.util.List<com.dtstack.engine.api.vo.ScheduleTaskVO>
     **/
    public List<ScheduleTaskVO> getFlowWorkSubTasksRefTask(Set<Long> taskIds, int level, Integer directType, Integer appType,int max) {

        //获得所有父节点task
        List<ScheduleTaskShade> tasks = taskShadeService.getTaskByIds(new ArrayList<>(taskIds),appType);
        if (CollectionUtils.isEmpty(tasks)) {
            return null;
        }
        List<ScheduleTaskVO> refTaskVoList = new ArrayList<>(tasks.size());
        for (ScheduleTaskShade task : tasks) {
            refTaskVoList.add(this.getFlowWorkOffSpring(task, level,appType,max));
        }

        return refTaskVoList;
    }


    public List<ScheduleTaskTaskShade> listChildTask(Long taskId, Integer appType,Integer limit) {
        return scheduleTaskTaskShadeDao.listChildTaskLimit(taskId,appType,limit);
    }
}
