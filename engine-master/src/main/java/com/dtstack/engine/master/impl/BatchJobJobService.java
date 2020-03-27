package com.dtstack.engine.master.impl;

import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.util.MathUtil;
import com.dtstack.engine.common.annotation.Param;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.BatchJobDao;
import com.dtstack.engine.dao.BatchJobJobDao;
import com.dtstack.engine.domain.BatchJob;
import com.dtstack.engine.domain.BatchJobJob;
import com.dtstack.engine.domain.BatchTaskShade;
import com.dtstack.engine.dto.BatchJobJobDTO;
import com.dtstack.engine.dto.BatchJobJobTaskDTO;
import com.dtstack.engine.master.vo.BatchJobVO;
import com.dtstack.engine.master.vo.BatchTaskVO;
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
public class BatchJobJobService {

    private final static Logger logger = LoggerFactory.getLogger(BatchJobJobService.class);

    private static final String WORKFLOW_PARENT = "0";

    @Autowired
    private BatchJobJobDao batchJobJobDao;

    @Autowired
    private BatchJobDao batchJobDao;

    @Autowired
    private BatchJobService batchJobService;

    @Autowired
    private BatchTaskShadeService batchTaskShadeService;

    /**
     * @author toutian
     */
    public BatchJobVO displayOffSpring(@Param("jobId") Long jobId,
                                       @Param("projectId") Long projectId,
                                       @Param("level") Integer level) throws Exception {

        BatchJob job = batchJobDao.getOne(jobId);
        if (job == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }

        if (level == null || level < 1) {
            level = 1;
        }
        if (job.getFlowJobId() != null && !job.getFlowJobId().equals(WORKFLOW_PARENT)) {
            //如果是工作流子节点则返回整个工作流子节点依赖关系
            try {
                BatchJob flowJob = batchJobDao.getByJobId(job.getFlowJobId(), Deleted.NORMAL.getStatus());
                //工作流下全部实例,层级level使用int最大值
                BatchJobVO subJobVO = displayOffSpringForFlowWork(flowJob);
                subJobVO.setProjectId(flowJob.getProjectId());
                return subJobVO;
            } catch (Exception e) {
                logger.error("get flow work subJob error", e);
            }
        }

        // 递归获取level层的子节点
        Map<Integer, List<BatchJobJob>> result = getSpecifiedLevelJobJobs(job.getJobKey(), level, true, null);
        BatchJobJobDTO root = new BatchJobJobDTO();
        root.setJobKey(job.getJobKey());
        getTree(root, result, 1, true);

        Set<String> allJobKeys = new HashSet<>();
        allJobKeys.add(job.getJobKey());
        getAllJobKeys(result, allJobKeys);

        Map<String, BatchJob> keyJobMap = new HashMap<>();
        Map<Long, BatchTaskShade> idTaskMap = new HashMap<>();
        getRelationData(allJobKeys, keyJobMap, idTaskMap);

        //flowJobId不为0时表示是工作流中的子任务
        boolean isSubTask = !StringUtils.equals("0", job.getFlowJobId());

        return getOffSpring(root, keyJobMap, idTaskMap, isSubTask);
    }

    private void getRelationData(Set<String> allJobKeys, Map<String, BatchJob> keyJobMap,
                                 Map<Long, BatchTaskShade> idTaskMap) throws Exception {
        List<Long> taskIds = new ArrayList<>();

        List<BatchJob> jobs = batchJobDao.listJobByJobKeys(allJobKeys);
        for (BatchJob batchJob : jobs) {
            keyJobMap.put(batchJob.getJobKey(), batchJob);
            taskIds.add(batchJob.getTaskId());
        }

        List<BatchTaskShade> taskShades = batchTaskShadeService.getSimpleTaskRangeAllByIds(taskIds);
        taskShades.forEach(item -> idTaskMap.put(item.getTaskId(), item));
    }

    private BatchJobJobDTO getTree(BatchJobJobDTO root, Map<Integer, List<BatchJobJob>> result, int level, boolean isChild) {
        if (level <= result.size()) {
            List<BatchJobJob> currentLevel = result.get(level);
            List<BatchJobJobDTO> children = new ArrayList<>();
            for (BatchJobJob jobJob : currentLevel) {
                if (isChild) {
                    if (jobJob.getParentJobKey().equals(root.getJobKey())) {
                        BatchJobJobDTO child = new BatchJobJobDTO();
                        child.setJobKey(jobJob.getJobKey());
                        children.add(child);
                    }
                } else {
                    if (jobJob.getJobKey().equals(root.getJobKey())) {
                        BatchJobJobDTO parent = new BatchJobJobDTO();
                        parent.setJobKey(jobJob.getParentJobKey());
                        children.add(parent);
                    }
                }
            }

            int nextLevel = ++level;
            for (BatchJobJobDTO child : children) {
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
    private Map<Integer, List<BatchJobJob>> getSpecifiedLevelJobJobs(String rootKey, int level, boolean getChild, String parentFlowJobJobKey) {
        BatchJob rootJob = batchJobDao.getByJobKey(rootKey);

        Map<Integer, List<BatchJobJob>> result = new HashMap<>();
        List<String> jobKeys = new ArrayList<>();
        List<Long> taskIdList = new ArrayList<>();

        jobKeys.add(rootKey);
        taskIdList.add(rootJob.getTaskId());
        int j = 1;
        for (int i = level; i > 0; i--) {
            List<BatchJobJobTaskDTO> jobJobs;
            if (getChild) {
                jobJobs = batchJobJobDao.listByParentJobKeysWithOutSelfTask(jobKeys);
            } else {
                jobJobs = batchJobJobDao.listByJobKeysWithOutSelfTask(jobKeys, taskIdList);
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
            for (BatchJobJobTaskDTO jobJob : jobJobs) {
                if (getChild) {
                    jobKeys.add(jobJob.getJobKey());
                    taskIdList.add(jobJob.getTaskId());
                } else {
                    jobKeys.add(jobJob.getParentJobKey());
                    taskIdList.add(jobJob.getTaskId());
                }
            }

            List<BatchJobJob> jobJobList = jobJobs.stream().map(BatchJobJobTaskDTO::toJobJob).collect(Collectors.toList());
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

    private BatchJobVO displayOffSpringForFlowWork(BatchJob flowJob) throws Exception {
        BatchJobVO vo = null;
        // 递归获取level层的子节点
        Map<Integer, List<BatchJobJob>> result = getSpecifiedLevelJobJobs(flowJob.getJobKey(), Integer.MAX_VALUE, true, null);
        List<BatchJobJob> firstLevel = result.get(1);
        if (CollectionUtils.isNotEmpty(firstLevel)) {
            Set<String> allJobKeys = new HashSet<>();
            getAllJobKeys(result, allJobKeys);

            Map<String, BatchJob> keyJobMap = new HashMap<>();
            Map<Long, BatchTaskShade> idTaskMap = new HashMap<>();
            getRelationData(allJobKeys, keyJobMap, idTaskMap);

            BatchJobJob beginJobJob = null;
            for (BatchJobJob jobjob : firstLevel) {
                BatchJob subJob = keyJobMap.get(jobjob.getJobKey());
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

            BatchJobJobDTO root = new BatchJobJobDTO();
            root.setJobKey(beginJobJob.getJobKey());
            //第一层作为root
            getTree(root, result, 2, true);


            vo = getOffSpring(root, keyJobMap, idTaskMap, true);
        }
        return vo;
    }

    @Forbidden
    public BatchJobVO getOffSpring(BatchJobJobDTO root, Map<String, BatchJob> keyJobMap, Map<Long, BatchTaskShade> idTaskMap, boolean isSubTask) {
        BatchJob job = keyJobMap.get(root.getJobKey());
        BatchJobVO vo = new BatchJobVO(job);
        vo.setProjectId(job.getProjectId());
        BatchTaskShade batchTaskShade = idTaskMap.get(job.getTaskId());
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
            Iterator<BatchJobJobDTO> it = root.getChildren().iterator();
            while (it.hasNext()) {
                BatchJobJobDTO jobJob = it.next();

                if (job.getTaskId().longValue() == batchJobService.getTaskIdFromJobKey(jobJob.getJobKey()).longValue()) {
                    it.remove();
                    continue;
                }

                String jobDayStr = batchJobService.getJobTriggerTimeFromJobKey(job.getJobKey());
                String jobJobDayStr = batchJobService.getJobTriggerTimeFromJobKey(jobJob.getJobKey());
                if (!jobDayStr.equals(jobJobDayStr)) {
                    it.remove();
                }
            }

            List<BatchJobVO> subJobVOs = new ArrayList<>(root.getChildren().size());
            for (BatchJobJobDTO jobJobDTO : root.getChildren()) {
                BatchJobVO subVO = getOffSpring(jobJobDTO, keyJobMap, idTaskMap, isSubTask);
                if (subVO != null) {
                    subJobVOs.add(subVO);
                }
            }
            vo.setJobVOS(subJobVOs);
        }

        return vo;
    }


    private BatchTaskVO getTaskVo(BatchTaskShade batchTaskShade, BatchJob job) {
        BatchTaskVO taskVO = new BatchTaskVO(batchTaskShade, true);
        return taskVO;
    }

    /**
     * 为工作流节点展开子节点
     */
    public BatchJobVO displayOffSpringWorkFlow(@Param("jobId") Long jobId,@Param("appType")Integer appType) throws Exception {
        BatchJob job = batchJobService.getJobById(jobId);
        BatchTaskShade batchTaskShade = batchTaskShadeService.getBatchTaskById(job.getTaskId(),appType);
        BatchJobVO vo = new BatchJobVO(job);
        vo.setBatchTask(new BatchTaskVO(batchTaskShade, true));
        if (batchTaskShade.getTaskType().intValue() == EJobType.WORK_FLOW.getVal() || batchTaskShade.getTaskType().intValue() == EJobType.ALGORITHM_LAB.getVal()) {
            try {
                //工作流下全部实例,层级level使用int最大值
                BatchJobVO subJobVO = this.displayOffSpringForFlowWork(job);
                vo.setSubNodes(subJobVO);
            } catch (Exception e) {
                logger.error("get flow work subJob error", e);
            }
        }
        return vo;
    }

    private void getAllJobKeys(Map<Integer, List<BatchJobJob>> result, Set<String> jobKeys) {
        result.forEach((key, value) -> {
            for (BatchJobJob jobJob : value) {
                jobKeys.add(jobJob.getJobKey());
                jobKeys.add(jobJob.getParentJobKey());
            }
        });
    }

    public BatchJobVO displayForefathers(@Param("jobId") Long jobId, @Param("level") Integer level) throws Exception {

        BatchJob job = batchJobDao.getOne(jobId);
        if (job == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }

        if (level == null || level < 1) {
            level = 1;
        }
        //如果是工作流子节点，则获取父节点的jobKey以便不展示工作流父节点
        String parentFlowJobJobKey = null;
        if (StringUtils.isNotEmpty(job.getFlowJobId()) && !job.getFlowJobId().equals(WORKFLOW_PARENT)) {
            BatchJob parentFlowJob = batchJobDao.getByJobId(job.getFlowJobId(),Deleted.NORMAL.getStatus());
            parentFlowJobJobKey = parentFlowJob.getJobKey();
        }

        // 递归获取level层的子节点
        Map<Integer, List<BatchJobJob>> result = getSpecifiedLevelJobJobs(job.getJobKey(), level, false, parentFlowJobJobKey);
        BatchJobJobDTO root = new BatchJobJobDTO();
        root.setJobKey(job.getJobKey());
        getTree(root, result, 1, false);

        Set<String> allJobKeys = new HashSet<>();
        allJobKeys.add(job.getJobKey());
        getAllJobKeys(result, allJobKeys);

        Map<String, BatchJob> keyJobMap = new HashMap<>();
        Map<Long, BatchTaskShade> idTaskMap = new HashMap<>();
        getRelationData(allJobKeys, keyJobMap, idTaskMap);

        return getForefathers(root, keyJobMap, idTaskMap);
    }

    private BatchJobVO getForefathers(BatchJobJobDTO root, Map<String, BatchJob> keyJobMap,
                                      Map<Long, BatchTaskShade> idTaskMap) {
        BatchJob job = keyJobMap.get(root.getJobKey());
        if (job == null) {
            return null;
        }
        BatchJobVO vo = new BatchJobVO(job);
        BatchTaskShade batchTaskShade = idTaskMap.get(job.getTaskId());

        vo.setBatchTask(getTaskVo(batchTaskShade, job));

        if (StringUtils.isBlank(job.getJobKey())) {
            return vo;
        }

        if (CollectionUtils.isNotEmpty(root.getChildren())) {
            root.getChildren().removeIf(jobJobDTO -> job.getTaskId().equals(batchJobService.getTaskIdFromJobKey(jobJobDTO.getJobKey())));

            List<BatchJobVO> fatherVOs = new ArrayList<>();
            for (BatchJobJobDTO jobJobDTO : root.getChildren()) {
                BatchJobVO item = this.getForefathers(jobJobDTO, keyJobMap, idTaskMap);
                if (item != null) {
                    fatherVOs.add(item);
                }
            }
            vo.setJobVOS(fatherVOs);
        }

        return vo;
    }

    @Forbidden
    public List<BatchJobJob> getJobChild(String parentJobKey) {
        return batchJobJobDao.listByParentJobKey(parentJobKey);
    }

    public int batchInsert(List<BatchJobJob> batchJobJobs) {
        return batchJobJobDao.batchInsert(batchJobJobs);
    }

}