package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.annotation.Forbidden;
import com.dtstack.engine.api.annotation.Param;
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
import com.dtstack.engine.master.vo.ScheduleTaskVO;
import com.dtstack.schedule.common.enums.Deleted;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Service
public class ScheduleJobJobService implements com.dtstack.engine.api.service.ScheduleJobJobService {

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

    /**
     * @author toutian
     */
    public com.dtstack.engine.master.vo.ScheduleJobVO displayOffSpring(@Param("jobId") Long jobId,
                                                                       @Param("projectId") Long projectId,
                                                                       @Param("level") Integer level) throws Exception {

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
                //工作流下全部实例,层级level使用int最大值
                com.dtstack.engine.master.vo.ScheduleJobVO subJobVO = displayOffSpringForFlowWork(flowJob);
                subJobVO.setProjectId(flowJob.getProjectId());
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
        for (ScheduleJob scheduleJob : jobs) {
            keyJobMap.put(scheduleJob.getJobKey(), scheduleJob);
            taskIds.add(scheduleJob.getTaskId());
        }

        List<ScheduleTaskShade> taskShades = batchTaskShadeService.getSimpleTaskRangeAllByIds(taskIds);
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
     * @param rootKey
     * @param level
     * @param getChild
     * @param parentFlowJobJobKey 若root节点为工作流里的子节点，则为工作流父节点的jobKey值，否则为NULL
     *                            在业务上不需要展示从工作流子节点到父节点的关系
     * @return
     */
    private Map<Integer, List<ScheduleJobJob>> getSpecifiedLevelJobJobs(String rootKey, int level, boolean getChild, String parentFlowJobJobKey) {
        ScheduleJob rootJob = scheduleJobDao.getByJobKey(rootKey);

        Map<Integer, List<ScheduleJobJob>> result = new HashMap<>();
        List<String> jobKeys = new ArrayList<>();
        List<Long> taskIdList = new ArrayList<>();

        jobKeys.add(rootKey);
        taskIdList.add(rootJob.getTaskId());
        int j = 1;
        for (int i = level; i > 0; i--) {
            List<ScheduleJobJobTaskDTO> jobJobs;
            if (getChild) {
                jobJobs = scheduleJobJobDao.listByParentJobKeysWithOutSelfTask(jobKeys);
            } else {
                jobJobs = scheduleJobJobDao.listByJobKeysWithOutSelfTask(jobKeys, taskIdList);
            }

            jobJobs = jobJobs.stream().filter(jobjob -> {
                String temp;
                if (getChild) {
                    temp = jobjob.getJobKey();
                } else {
                    temp = jobjob.getParentJobKey();
                }
                return !temp.equals(parentFlowJobJobKey);
            }).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(jobJobs)) {
                return result;
            }

            jobKeys = new ArrayList<>();
            for (ScheduleJobJobTaskDTO jobJob : jobJobs) {
                if (getChild) {
                    jobKeys.add(jobJob.getJobKey());
                    taskIdList.add(jobJob.getTaskId());
                } else {
                    jobKeys.add(jobJob.getParentJobKey());
                    taskIdList.add(jobJob.getTaskId());
                }
            }

            List<ScheduleJobJob> jobJobList = jobJobs.stream().map(ScheduleJobJobTaskDTO::toJobJob).collect(Collectors.toList());
            result.put(j, jobJobList);
            j++;
        }

        return result;
    }

    private Long getJobTaskIdFromJobKey(String jobKey) {
        String[] fields = jobKey.split("_");
        if (fields.length < 3) {
            return null;
        }

        return MathUtil.getLongVal(fields[fields.length - 2]);
    }

    private com.dtstack.engine.master.vo.ScheduleJobVO displayOffSpringForFlowWork(ScheduleJob flowJob) throws Exception {
        com.dtstack.engine.master.vo.ScheduleJobVO vo = null;
        // 递归获取level层的子节点
        Map<Integer, List<ScheduleJobJob>> result = getSpecifiedLevelJobJobs(flowJob.getJobKey(), Integer.MAX_VALUE, true, null);
        List<ScheduleJobJob> firstLevel = result.get(1);
        if (CollectionUtils.isNotEmpty(firstLevel)) {
            Set<String> allJobKeys = new HashSet<>();
            getAllJobKeys(result, allJobKeys);

            Map<String, ScheduleJob> keyJobMap = new HashMap<>();
            Map<Long, ScheduleTaskShade> idTaskMap = new HashMap<>();
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

    @Forbidden
    public com.dtstack.engine.master.vo.ScheduleJobVO getOffSpring(ScheduleJobJobDTO root, Map<String, ScheduleJob> keyJobMap, Map<Long, ScheduleTaskShade> idTaskMap, boolean isSubTask) {
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

        vo.setBatchTask(getTaskVo(batchTaskShade, job));
        if (CollectionUtils.isNotEmpty(root.getChildren())) {
            Iterator<ScheduleJobJobDTO> it = root.getChildren().iterator();
            while (it.hasNext()) {
                ScheduleJobJobDTO jobJob = it.next();
                // 4.0 getTaskIdFromJobKey 获取的是task_shade 的id
                if (batchTaskShade.getId() == batchJobService.getTaskIdFromJobKey(jobJob.getJobKey()).longValue()) {
                    it.remove();
                    continue;
                }

                String jobDayStr = batchJobService.getJobTriggerTimeFromJobKey(job.getJobKey());
                String jobJobDayStr = batchJobService.getJobTriggerTimeFromJobKey(jobJob.getJobKey());
                if (!jobDayStr.equals(jobJobDayStr)) {
                    it.remove();
                }
            }

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


    private ScheduleTaskVO getTaskVo(ScheduleTaskShade batchTaskShade, ScheduleJob job) {
        ScheduleTaskVO taskVO = new ScheduleTaskVO(batchTaskShade, true);
        return taskVO;
    }

    /**
     * 为工作流节点展开子节点
     */
    public com.dtstack.engine.master.vo.ScheduleJobVO displayOffSpringWorkFlow(@Param("jobId") Long jobId, @Param("appType")Integer appType) throws Exception {
        ScheduleJob job = batchJobService.getJobById(jobId);
        ScheduleTaskShade batchTaskShade = batchTaskShadeService.getBatchTaskById(job.getTaskId(),appType);
        com.dtstack.engine.master.vo.ScheduleJobVO vo = new com.dtstack.engine.master.vo.ScheduleJobVO(job);
        vo.setBatchTask(new ScheduleTaskVO(batchTaskShade, true));
        if (batchTaskShade.getTaskType().intValue() == EScheduleJobType.WORK_FLOW.getVal() || batchTaskShade.getTaskType().intValue() == EScheduleJobType.ALGORITHM_LAB.getVal()) {
            try {
                //工作流下全部实例,层级level使用int最大值
                com.dtstack.engine.master.vo.ScheduleJobVO subJobVO = this.displayOffSpringForFlowWork(job);
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

    public com.dtstack.engine.master.vo.ScheduleJobVO displayForefathers(@Param("jobId") Long jobId, @Param("level") Integer level) throws Exception {

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

        return getForefathers(root, keyJobMap, idTaskMap);
    }

    private com.dtstack.engine.master.vo.ScheduleJobVO getForefathers(ScheduleJobJobDTO root, Map<String, ScheduleJob> keyJobMap,
                                                                      Map<Long, ScheduleTaskShade> idTaskMap) {
        ScheduleJob job = keyJobMap.get(root.getJobKey());
        if (job == null) {
            return null;
        }
        com.dtstack.engine.master.vo.ScheduleJobVO vo = new com.dtstack.engine.master.vo.ScheduleJobVO(job);
        ScheduleTaskShade batchTaskShade = idTaskMap.get(job.getTaskId());

        vo.setBatchTask(getTaskVo(batchTaskShade, job));

        if (StringUtils.isBlank(job.getJobKey())) {
            return vo;
        }

        if (CollectionUtils.isNotEmpty(root.getChildren())) {
            root.getChildren().removeIf(jobJobDTO -> job.getTaskId().equals(batchJobService.getTaskIdFromJobKey(jobJobDTO.getJobKey())));

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

    @Forbidden
    public List<ScheduleJobJob> getJobChild(String parentJobKey) {
        return scheduleJobJobDao.listByParentJobKey(parentJobKey);
    }

    public int batchInsert(List<ScheduleJobJob> scheduleJobJobs) {
        return scheduleJobJobDao.batchInsert(scheduleJobJobs);
    }

}