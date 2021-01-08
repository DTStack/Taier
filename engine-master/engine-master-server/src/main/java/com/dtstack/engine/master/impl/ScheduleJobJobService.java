package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleJobJobDTO;
import com.dtstack.engine.api.dto.ScheduleJobJobTaskDTO;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.vo.ScheduleTaskVO;
import com.dtstack.schedule.common.enums.Deleted;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Service
public class ScheduleJobJobService {

    private final static Logger logger = LoggerFactory.getLogger(ScheduleJobJobService.class);

    private static final String WORKFLOW_PARENT = "0";

    @Autowired
    private ScheduleJobJobDao scheduleJobJobDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleJobService batchJobService;

    @Autowired
    private ScheduleTaskShadeService batchTaskShadeService;

    @Autowired
    private EnvironmentContext context;



    /**
     * @author newman
     * @Description
     * @Date 2021/1/5 4:09 下午
     * @param jobId: 任务id
     * @param level: 展开层数
     * @return: com.dtstack.engine.master.vo.ScheduleJobVO
     **/
    public com.dtstack.engine.master.vo.ScheduleJobVO displayOffSpringNew( Long jobId,
                                                                        Integer level) throws Exception {

        ScheduleJob job = scheduleJobDao.getOne(jobId);
        if (job == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        level = checkLevel(level);
        if (job.getFlowJobId() != null && !job.getFlowJobId().equals(WORKFLOW_PARENT)) {
            //如果是工作流子节点则返回整个工作流子节点依赖关系
            try {
                ScheduleJob flowJob = scheduleJobDao.getByJobId(job.getFlowJobId(), Deleted.NORMAL.getStatus());
                if(null == flowJob){
                    return null;
                }
                //工作流下全部实例
                com.dtstack.engine.master.vo.ScheduleJobVO subJobVO = displayOffSpringForFlowWorkNew(flowJob);
                if(null!=subJobVO) {
                    subJobVO.setProjectId(flowJob.getProjectId());
                }
                return subJobVO;
            } catch (Exception e) {
                logger.error("get flow work subJob error", e);
            }
        }
        // 递归获取level层的子节点
        Map<Integer, List<ScheduleJobJob>> result = getSpecifiedLevelJobJobsNew(job.getJobKey(), level, true);
        ScheduleJobJobDTO root = new ScheduleJobJobDTO();
        root.setJobKey(job.getJobKey());
        getTree(root, result, 1, true);
        Set<String> allJobKeys = new HashSet<>();
        allJobKeys.add(job.getJobKey());
        getAllJobKeys(result, allJobKeys);
        Map<String, ScheduleJob> keyJobMap = new HashMap<>();
        Map<Long, ScheduleTaskShade> idTaskMap = new HashMap<>();
        //获取keyJobMap和taskId-TaskShade map组
        getRelationData(allJobKeys, keyJobMap, idTaskMap);
        return getOffSpring(root, keyJobMap, idTaskMap, false);
    }

    /**
     * @author newman
     * @Description 校验level值
     * @Date 2021/1/5 4:29 下午
     * @param level:
     * @return: void
     **/
    private Integer checkLevel(Integer level) {
        if (level == null || level < 1) {
            //控制展开层数
            level = 1;
        }
        Integer jobLevel = context.getJobJobLevel();
        if(level !=null && level > jobLevel){
            level = jobLevel;
        }
        return level;
    }

    /**
     * @author toutian
     */
    public com.dtstack.engine.master.vo.ScheduleJobVO displayOffSpring( Long jobId,
                                                                        Integer level) throws Exception {
        ScheduleJob job = scheduleJobDao.getOne(jobId);
        if (job == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }

        if (level == null || level < 1) {
            level = 1;
        }
        if (job.getFlowJobId() != null && !job.getFlowJobId().equals(WORKFLOW_PARENT)) {
            //如果是工作流子节点则返回整个工作流子节点依赖关系
            try {
                ScheduleJob flowJob = scheduleJobDao.getByJobId(job.getFlowJobId(), Deleted.NORMAL.getStatus());
                //工作流下全部实例,层级level使用int最大值，这个改成最大10层，否则可能会oom
                com.dtstack.engine.master.vo.ScheduleJobVO subJobVO = displayOffSpringForFlowWork(flowJob);
                if(null!=subJobVO) {
                    subJobVO.setProjectId(flowJob.getProjectId());
                }
                return subJobVO;
            } catch (Exception e) {
                logger.error("get flow work subJob error", e);
            }
        }
        // 递归获取level层的子节点
        Map<Integer, List<ScheduleJobJob>> result = getSpecifiedLevelJobJobs(job.getJobKey(), level, true, null);
        ScheduleJobJobDTO root = new ScheduleJobJobDTO();
        root.setJobKey(job.getJobKey());
        getTree(root, result, 1, true);
        Set<String> allJobKeys = new HashSet<>();
        allJobKeys.add(job.getJobKey());
        getAllJobKeys(result, allJobKeys);
        Map<String, ScheduleJob> keyJobMap = new HashMap<>();
        Map<Long, ScheduleTaskShade> idTaskMap = new HashMap<>();
        getRelationData(allJobKeys, keyJobMap, idTaskMap);
        //flowJobId不为0时表示是工作流中的子任务
        boolean isSubTask = !StringUtils.equals("0", job.getFlowJobId());

        return getOffSpring(root, keyJobMap, idTaskMap, isSubTask);
    }

    private void getRelationData(Set<String> allJobKeys, Map<String, ScheduleJob> keyJobMap,
                                 Map<Long, ScheduleTaskShade> idTaskMap) throws Exception {
        List<Long> taskIds = new ArrayList<>();
        List<ScheduleJob> jobs = scheduleJobDao.listJobByJobKeys(allJobKeys);
        if(CollectionUtils.isEmpty(jobs)){
            return;
        }
        Integer appType = null;
        for (ScheduleJob scheduleJob : jobs) {
            keyJobMap.put(scheduleJob.getJobKey(), scheduleJob);
            taskIds.add(scheduleJob.getTaskId());
            if (null == appType &&  null != scheduleJob.getAppType()) {
                appType = scheduleJob.getAppType();
            }
        }
        List<ScheduleTaskShade> taskShades = batchTaskShadeService.getSimpleTaskRangeAllByIds(taskIds,appType);
        taskShades.forEach(item -> idTaskMap.put(item.getTaskId(), item));
    }

    private ScheduleJobJobDTO getTree(ScheduleJobJobDTO root, Map<Integer, List<ScheduleJobJob>> result, int level, boolean isChild) {
        if (level <= result.size()) {
            List<ScheduleJobJob> currentLevel = result.get(level);
            List<ScheduleJobJobDTO> children = new ArrayList<>();
            for (ScheduleJobJob jobJob : currentLevel) {
                if (isChild) {
                    if (jobJob.getParentJobKey().equals(root.getJobKey())) {
                        ScheduleJobJobDTO child = new ScheduleJobJobDTO();
                        child.setJobKey(jobJob.getJobKey());
                        children.add(child);
                    }
                } else {
                    if (jobJob.getJobKey().equals(root.getJobKey())) {
                        ScheduleJobJobDTO parent = new ScheduleJobJobDTO();
                        parent.setJobKey(jobJob.getParentJobKey());
                        children.add(parent);
                    }
                }
            }
            int nextLevel = ++level;
            for (ScheduleJobJobDTO child : children) {
                getTree(child, result, nextLevel, isChild);
            }
            root.setChildren(children);
        }
        return root;
    }

    /**
     * @Desc 各层任务依赖关系
     * @param rootKey
     * @param level 防止一直递归
     * @param getChild
     * @return
     */
    private Map<Integer, List<ScheduleJobJob>> getSpecifiedLevelJobJobsNew(String rootKey, int level, boolean getChild) {

        Map<Integer, List<ScheduleJobJob>> result = new HashMap<>(16);
        List<String> jobKeys = new ArrayList<>();
        List<String> jobKeyRelations = new ArrayList<>();
        jobKeys.add(rootKey);
        int jobLoop = 1;
        for (int leveCount = level; leveCount > 0; leveCount--) {
            List<ScheduleJobJobTaskDTO> jobJobs;
            if (getChild) {
                //向下获取
                jobJobs = scheduleJobJobDao.listByParentJobKeysWithOutSelfTask(jobKeys);
            } else {
                //向上获取
                jobJobs = scheduleJobJobDao.listByJobKeysWithOutSelfTask(jobKeys);
            }
            List<String> finalJobKeys = jobKeys;
            //过滤掉不满足条件的jobKey
            jobJobs = filterJobJobs(getChild, jobJobs, finalJobKeys);
            if (CollectionUtils.isEmpty(jobJobs)) {
                return result;
            }
            //获取jobKeys
            jobKeys = getJobKeys(getChild, jobJobs);
            //校验是否成环
            if(checkIsLoop(jobJobs, jobKeyRelations)){
                return result;
            }
            List<ScheduleJobJob> jobJobList = jobJobs.stream().map(ScheduleJobJobTaskDTO::toJobJob).collect(Collectors.toList());
            logger.info("count info --- rootKey:{} jobJobList size:{} jobLoop:{}", rootKey, jobJobList.size(), jobLoop);
            result.put(jobLoop, jobJobList);
            jobLoop++;
        }
        return result;
    }

    /**
     * @author newman
     * @Description 检测工作实例是否成环
     * @Date 2021/1/6 2:39 下午
     * @param jobJobs:
     * @param jobKeyRelations:
     * @return: void
     **/
    private Boolean checkIsLoop(List<ScheduleJobJobTaskDTO> jobJobs, List<String> jobKeyRelations) {

        for (ScheduleJobJobTaskDTO jobJob : jobJobs) {
            String jobKeyRelation = jobJob.getParentJobKey()+"-"+jobJob.getJobKey();
            if(jobKeyRelations.contains(jobKeyRelation)){
                logger.error("该工作实例成环了,jobKeyRelation:{}",jobKeyRelation);
                return true;
            }
            jobKeyRelations.add(jobKeyRelation);
        }
        return false;

    }


    private List<String> getJobKeys(boolean getChild, List<ScheduleJobJobTaskDTO> jobJobs) {

        List<String> jobKeys = new ArrayList<>();
        for (ScheduleJobJobTaskDTO jobJob : jobJobs) {
            if (getChild) {
                jobKeys.add(jobJob.getJobKey());
            } else {
                jobKeys.add(jobJob.getParentJobKey());
            }
        }
        return jobKeys;
    }

    private List<ScheduleJobJobTaskDTO> filterJobJobs(boolean getChild, List<ScheduleJobJobTaskDTO> jobJobs, List<String> finalJobKeys) {
        jobJobs = jobJobs.stream().filter(jobjob -> {
            String temp;
            if (!getChild) {
                temp = jobjob.getJobKey();
            } else {
                temp = jobjob.getParentJobKey();
            }
            return finalJobKeys.contains(temp);
        }).collect(Collectors.toList());
        return jobJobs;
    }

    /**
     * @param rootKey
     * @param level 这里设置为10，防止一直递归
     * @param getChild
     * @param parentFlowJobJobKey 若root节点为工作流里的子节点，则为工作流父节点的jobKey值，否则为NULL
     *                            在业务上不需要展示从工作流子节点到父节点的关系
     * @return
     */
    private Map<Integer, List<ScheduleJobJob>> getSpecifiedLevelJobJobs(String rootKey, int level, boolean getChild, String parentFlowJobJobKey) {

        Map<Integer, List<ScheduleJobJob>> result = new HashMap<>(16);
        List<String> jobKeys = new ArrayList<>();

        jobKeys.add(rootKey);
        int jobLoop = 1;
        for (int leveCount = level; leveCount > 0; leveCount--) {
            List<ScheduleJobJobTaskDTO> jobJobs;
            if (getChild) {
                jobJobs = scheduleJobJobDao.listByParentJobKeysWithOutSelfTask(jobKeys);
            } else {
                jobJobs = scheduleJobJobDao.listByJobKeysWithOutSelfTask(jobKeys);
            }
            List<String> finalJobKeys = jobKeys;
            //过滤掉不满足条件的jobJob
            jobJobs = filterJobJobs(getChild, jobJobs, finalJobKeys);
            if (CollectionUtils.isEmpty(jobJobs)) {
                return result;
            }
            //重新给jobKeys赋值
            jobKeys = getJobKeys(getChild, jobJobs);
            List<ScheduleJobJob> jobJobList = jobJobs.stream().map(ScheduleJobJobTaskDTO::toJobJob).collect(Collectors.toList());
            logger.info("count info --- rootKey:{} jobJobList size:{} jobLoop:{}", rootKey, jobJobList.size(), jobLoop);
            result.put(jobLoop, jobJobList);
            jobLoop++;
        }
        return result;
    }

    /**
     * @author newman
     * @Description 展开工作流子任务
     * @Date 2021/1/5 4:40 下午
     * @param flowJob:
     * @return: com.dtstack.engine.master.vo.ScheduleJobVO
     **/
    private com.dtstack.engine.master.vo.ScheduleJobVO displayOffSpringForFlowWorkNew(ScheduleJob flowJob) throws Exception {
        com.dtstack.engine.master.vo.ScheduleJobVO vo = null;
        // 递归获取level层的子节点
        Integer level = context.getWorkFlowLevel();
        //获取工作流子节点各层任务依赖关系
        Map<Integer, List<ScheduleJobJob>> result = getSpecifiedLevelJobJobsNew(flowJob.getJobKey(), level, true);
        List<ScheduleJobJob> firstLevel = result.get(1);
        if (CollectionUtils.isNotEmpty(firstLevel)) {
            Set<String> allJobKeys = new HashSet<>();
            getAllJobKeys(result, allJobKeys);
            Map<String, ScheduleJob> keyJobMap = new HashMap<>(16);
            Map<Long, ScheduleTaskShade> idTaskMap = new HashMap<>(16);
            //获取keyJobMap和taskId-TaskShade map组
            getRelationData(allJobKeys, keyJobMap, idTaskMap);
            ScheduleJobJob beginJobJob = null;
            for (ScheduleJobJob jobjob : firstLevel) {
                ScheduleJob subJob = keyJobMap.get(jobjob.getJobKey());
                //找出工作流的起始节点
                if (StringUtils.equals(subJob.getFlowJobId(), flowJob.getJobId())) {
                    beginJobJob = jobjob;
                    break;
                }
            }
            if (beginJobJob == null) {
                logger.error("displayOffSpringForFlowWork end with no subTasks with flowJobKey [{}]", flowJob.getJobKey());
                return null;
            }
            ScheduleJobJobDTO root = new ScheduleJobJobDTO();
            root.setJobKey(beginJobJob.getJobKey());
            //第一层作为root
            getTree(root, result, 2, true);
            vo = getOffSpring(root, keyJobMap, idTaskMap, true);
        }
        return vo;
    }



    private com.dtstack.engine.master.vo.ScheduleJobVO displayOffSpringForFlowWork(ScheduleJob flowJob) throws Exception {
        com.dtstack.engine.master.vo.ScheduleJobVO vo = null;
        // 递归获取level层的子节点
        Map<Integer, List<ScheduleJobJob>> result = getSpecifiedLevelJobJobs(flowJob.getJobKey(), 10, true, null);
        List<ScheduleJobJob> firstLevel = result.get(1);
        if (CollectionUtils.isNotEmpty(firstLevel)) {
            Set<String> allJobKeys = new HashSet<>();
            getAllJobKeys(result, allJobKeys);

            Map<String, ScheduleJob> keyJobMap = new HashMap<>(16);
            Map<Long, ScheduleTaskShade> idTaskMap = new HashMap<>(16);
            getRelationData(allJobKeys, keyJobMap, idTaskMap);

            ScheduleJobJob beginJobJob = null;
            for (ScheduleJobJob jobjob : firstLevel) {
                ScheduleJob subJob = keyJobMap.get(jobjob.getJobKey());
                //找出工作流的起始节点
                if (StringUtils.equals(subJob.getFlowJobId(), flowJob.getJobId())) {
                    beginJobJob = jobjob;
                    break;
                }
            }
            if (beginJobJob == null) {
                logger.error("displayOffSpringForFlowWork end with no subTasks with flowJobKey [{}]", flowJob.getJobKey());
                return null;
            }
            ScheduleJobJobDTO root = new ScheduleJobJobDTO();
            root.setJobKey(beginJobJob.getJobKey());
            //第一层作为root
            getTree(root, result, 2, true);
            vo = getOffSpring(root, keyJobMap, idTaskMap, true);
        }
        return vo;
    }

    public com.dtstack.engine.master.vo.ScheduleJobVO getOffSpring(
            ScheduleJobJobDTO root, Map<String, ScheduleJob> keyJobMap, Map<Long, ScheduleTaskShade> idTaskMap, boolean isSubTask) {

        ScheduleJob job = keyJobMap.get(root.getJobKey());
        com.dtstack.engine.master.vo.ScheduleJobVO vo = new com.dtstack.engine.master.vo.ScheduleJobVO(job);
        vo.setProjectId(job.getProjectId());
        ScheduleTaskShade batchTaskShade = idTaskMap.get(job.getTaskId());
        if (batchTaskShade == null) {
            return null;
        }
        //展示非工作流中的任务节点时，过滤掉工作流中的节点
        if (!isSubTask) {
            if (!vo.getFlowJobId().equals("0")) {
                return null;
            }
        }
        vo.setBatchTask(getTaskVo(batchTaskShade));
        if (CollectionUtils.isNotEmpty(root.getChildren())) {
            //移除不符合条件的jobjob
            filterJobJob(root, job, batchTaskShade);
            List<ScheduleJobVO> subJobVOs = new ArrayList<>(root.getChildren().size());
            for (ScheduleJobJobDTO jobJobDTO : root.getChildren()) {
                com.dtstack.engine.master.vo.ScheduleJobVO subVO = getOffSpring(jobJobDTO, keyJobMap, idTaskMap, isSubTask);
                if (subVO != null) {
                    subJobVOs.add(subVO);
                }
            }
            vo.setJobVOS(subJobVOs);
        }
        return vo;
    }

    private void filterJobJob(ScheduleJobJobDTO root, ScheduleJob job, ScheduleTaskShade batchTaskShade) {
        Iterator<ScheduleJobJobDTO> it = root.getChildren().iterator();
        while (it.hasNext()) {
            ScheduleJobJobDTO jobJob = it.next();
            // 4.0 getTaskIdFromJobKey 获取的是task_shade 的id
            if (batchTaskShade.getId().equals(batchJobService.getTaskShadeIdFromJobKey(jobJob.getJobKey()))) {
                //移除本身
                it.remove();
                continue;
            }
            String jobDayStr = batchJobService.getJobTriggerTimeFromJobKey(job.getJobKey());
            String jobJobDayStr = batchJobService.getJobTriggerTimeFromJobKey(jobJob.getJobKey());
            if (!jobDayStr.equals(jobJobDayStr)) {
                //如果执行时间不是同一天，移除
                it.remove();
            }
        }
    }


    private ScheduleTaskVO getTaskVo(ScheduleTaskShade batchTaskShade) {

        return  new ScheduleTaskVO(batchTaskShade, true);
    }

    /**
     * 为工作流节点展开子节点
     */
    public com.dtstack.engine.master.vo.ScheduleJobVO displayOffSpringWorkFlow( Long jobId, Integer appType) throws Exception {

        ScheduleJob job = batchJobService.getJobById(jobId);
        if(null == job){
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }
        ScheduleTaskShade batchTaskShade = batchTaskShadeService.getBatchTaskById(job.getTaskId(),appType);
        if(null == batchTaskShade){
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }
        com.dtstack.engine.master.vo.ScheduleJobVO vo = new com.dtstack.engine.master.vo.ScheduleJobVO(job);
        vo.setBatchTask(new ScheduleTaskVO(batchTaskShade, true));
        if (batchTaskShade.getTaskType().intValue() == EScheduleJobType.WORK_FLOW.getVal() || batchTaskShade.getTaskType().intValue() == EScheduleJobType.ALGORITHM_LAB.getVal()) {
            try {
                com.dtstack.engine.master.vo.ScheduleJobVO subJobVO;
                //是否使用优化后的接口
                if(context.getUseOptimize()) {
                    subJobVO  = this.displayOffSpringForFlowWorkNew(job);
                }else{
                    subJobVO  = this.displayOffSpringForFlowWork(job);
                }
                vo.setSubNodes(subJobVO);
            } catch (Exception e) {
                logger.error("get flow work subJob error", e);
            }
        }
        return vo;
    }

    private void getAllJobKeys(Map<Integer, List<ScheduleJobJob>> result, Set<String> jobKeys) {
        result.forEach((key, value) -> {
            for (ScheduleJobJob jobJob : value) {
                jobKeys.add(jobJob.getJobKey());
                jobKeys.add(jobJob.getParentJobKey());
            }
        });
    }

    /**
     * @author newman
     * @Description 向上展开工作实例，优化后接口
     * @Date 2021/1/6 7:22 下午
     * @param jobId:
     * @param level:
     * @return: com.dtstack.engine.master.vo.ScheduleJobVO
     **/
    public com.dtstack.engine.master.vo.ScheduleJobVO displayForefathersNew( Long jobId,  Integer level) throws Exception {

        ScheduleJob job = scheduleJobDao.getOne(jobId);
        if (job == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        checkLevel(level);
        // 递归获取level层的子节点
        Map<Integer, List<ScheduleJobJob>> result = getSpecifiedLevelJobJobsNew(job.getJobKey(), level, false);
        ScheduleJobJobDTO root = new ScheduleJobJobDTO();
        root.setJobKey(job.getJobKey());
        getTree(root, result, 1, false);
        Set<String> allJobKeys = new HashSet<>();
        allJobKeys.add(job.getJobKey());
        getAllJobKeys(result, allJobKeys);
        Map<String, ScheduleJob> keyJobMap = new HashMap<>();
        Map<Long, ScheduleTaskShade> idTaskMap = new HashMap<>();
        getRelationData(allJobKeys, keyJobMap, idTaskMap);
        return getForefathers(root, keyJobMap, idTaskMap);
    }

    public com.dtstack.engine.master.vo.ScheduleJobVO displayForefathers( Long jobId,  Integer level) throws Exception {

        ScheduleJob job = scheduleJobDao.getOne(jobId);
        if (job == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        if (level == null || level < 1) {
            level = 1;
        }
        //如果是工作流子节点，则获取父节点的jobKey以便不展示工作流父节点
        String parentFlowJobJobKey = null;
        if (StringUtils.isNotEmpty(job.getFlowJobId()) && !job.getFlowJobId().equals(WORKFLOW_PARENT)) {
            ScheduleJob parentFlowJob = scheduleJobDao.getByJobId(job.getFlowJobId(), Deleted.NORMAL.getStatus());
            parentFlowJobJobKey = parentFlowJob.getJobKey();
        }

        // 递归获取level层的子节点
        Map<Integer, List<ScheduleJobJob>> result = getSpecifiedLevelJobJobs(job.getJobKey(), level, false, parentFlowJobJobKey);
        ScheduleJobJobDTO root = new ScheduleJobJobDTO();
        root.setJobKey(job.getJobKey());
        getTree(root, result, 1, false);

        Set<String> allJobKeys = new HashSet<>();
        allJobKeys.add(job.getJobKey());
        getAllJobKeys(result, allJobKeys);

        Map<String, ScheduleJob> keyJobMap = new HashMap<>();
        Map<Long, ScheduleTaskShade> idTaskMap = new HashMap<>();
        getRelationData(allJobKeys, keyJobMap, idTaskMap);

        return getForefathersNew(root, keyJobMap, idTaskMap);
    }

    private com.dtstack.engine.master.vo.ScheduleJobVO getForefathersNew(ScheduleJobJobDTO root, Map<String, ScheduleJob> keyJobMap,
                                                                      Map<Long, ScheduleTaskShade> idTaskMap) {
        ScheduleJob job = keyJobMap.get(root.getJobKey());
        if (job == null) {
            return null;
        }
        com.dtstack.engine.master.vo.ScheduleJobVO vo = new com.dtstack.engine.master.vo.ScheduleJobVO(job);
        ScheduleTaskShade batchTaskShade = idTaskMap.get(job.getTaskId());
        vo.setBatchTask(getTaskVo(batchTaskShade));
        if (StringUtils.isBlank(job.getJobKey())) {
            return vo;
        }
        if (CollectionUtils.isNotEmpty(root.getChildren())) {
            //移除自依赖
            root.getChildren().removeIf(jobJobDTO ->
                    batchJobService.getTaskShadeIdFromJobKey(jobJobDTO.getJobKey()).equals(batchTaskShade.getId()));
            if(CollectionUtils.isEmpty(root.getChildren())){
                return vo;
            }
            List<ScheduleJobVO> fatherVOs = new ArrayList<>();
            for (ScheduleJobJobDTO jobJobDTO : root.getChildren()) {
                com.dtstack.engine.master.vo.ScheduleJobVO item = this.getForefathersNew(jobJobDTO, keyJobMap, idTaskMap);
                if (item != null) {
                    fatherVOs.add(item);
                }
            }
            vo.setJobVOS(fatherVOs);
        }
        return vo;
    }

    private com.dtstack.engine.master.vo.ScheduleJobVO getForefathers(ScheduleJobJobDTO root, Map<String, ScheduleJob> keyJobMap,
                                                                      Map<Long, ScheduleTaskShade> idTaskMap) {
        ScheduleJob job = keyJobMap.get(root.getJobKey());
        if (job == null) {
            return null;
        }
        com.dtstack.engine.master.vo.ScheduleJobVO vo = new com.dtstack.engine.master.vo.ScheduleJobVO(job);
        ScheduleTaskShade batchTaskShade = idTaskMap.get(job.getTaskId());
        vo.setBatchTask(getTaskVo(batchTaskShade));
        if (StringUtils.isBlank(job.getJobKey())) {
            return vo;
        }
        if (CollectionUtils.isNotEmpty(root.getChildren())) {
            root.getChildren().removeIf(jobJobDTO ->
                    batchJobService.getTaskShadeIdFromJobKey(jobJobDTO.getJobKey()).equals(batchTaskShade.getId()));
//            root.getChildren().removeIf(jobJobDTO -> job.getTaskId().equals(batchJobService.getTaskIdFromJobKey(jobJobDTO.getJobKey())));
            if(CollectionUtils.isEmpty(root.getChildren())){
                return vo;
            }
            List<ScheduleJobVO> fatherVOs = new ArrayList<>();
            for (ScheduleJobJobDTO jobJobDTO : root.getChildren()) {
                com.dtstack.engine.master.vo.ScheduleJobVO item = this.getForefathers(jobJobDTO, keyJobMap, idTaskMap);
                if (item != null) {
                    fatherVOs.add(item);
                }
            }
            vo.setJobVOS(fatherVOs);
        }
        return vo;
    }

    public List<ScheduleJobJob> getJobChild(String parentJobKey) {
        return scheduleJobJobDao.listByParentJobKey(parentJobKey);
    }

    public int batchInsert(List<ScheduleJobJob> scheduleJobJobs) {
        return scheduleJobJobDao.batchInsert(scheduleJobJobs);
    }

}