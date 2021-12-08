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
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.domain.*;
import com.dtstack.engine.dto.QueryJobDTO;
import com.dtstack.engine.dto.ScheduleJobDTO;
import com.dtstack.engine.dto.ScheduleTaskForFillDataDTO;
import com.dtstack.engine.dto.StatusCount;
import com.dtstack.engine.mapper.*;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.impl.pojo.ParamActionExt;
import com.dtstack.engine.master.impl.restartAsync.RestartRunnable;
import com.dtstack.engine.master.impl.vo.ScheduleJobVO;
import com.dtstack.engine.master.impl.vo.ScheduleTaskVO;
import com.dtstack.engine.master.jobdealer.JobStopDealer;
import com.dtstack.engine.master.server.ScheduleBatchJob;
import com.dtstack.engine.master.server.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.server.scheduler.JobGraphBuilder;
import com.dtstack.engine.master.server.scheduler.JobPartitioner;
import com.dtstack.engine.master.server.scheduler.JobRichOperator;
import com.dtstack.engine.master.utils.JobGraphUtils;
import com.dtstack.engine.master.vo.*;
import com.dtstack.engine.master.vo.action.ActionLogVO;
import com.dtstack.engine.master.vo.schedule.job.ScheduleJobStatusCountVO;
import com.dtstack.engine.master.vo.schedule.job.ScheduleJobStatusVO;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.engine.pager.PageQuery;
import com.dtstack.engine.pager.PageResult;
import com.dtstack.engine.pluginapi.constrant.JobResultConstant;
import com.dtstack.engine.pluginapi.enums.ComputeType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.dtstack.engine.pluginapi.exception.ErrorCode;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.util.DateUtil;
import com.dtstack.engine.pluginapi.util.MathUtil;
import com.dtstack.engine.pluginapi.util.RetryUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.params.SetParams;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/3
 */
@Service
public class ScheduleJobService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleJobService.class);

    private static final ObjectMapper objMapper = new ObjectMapper();

    private static final String DAY_PATTERN = "yyyy-MM-dd";

    private DateTimeFormatter dayFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    private DateTimeFormatter dayFormatterAll = DateTimeFormat.forPattern("yyyyMMddHHmmss");

    private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private static final String BUSINESS_DATE = "business_date";

    private static final String DOWNLOAD_LOG = "/api/rdos/download/batch/batchDownload/downloadJobLog?jobId=%s&taskType=%s";

    private static final List<Integer> SPECIAL_TASK_TYPES = Lists.newArrayList(EScheduleJobType.WORK_FLOW.getVal());

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleTaskShadeService batchTaskShadeService;

    @Autowired
    private JobGraphBuilder jobGraphBuilder;

    @Autowired
    private ScheduleFillDataJobDao scheduleFillDataJobDao;

    @Autowired
    private ScheduleFillDataJobService scheduleFillDataJobService;

    @Autowired
    private ScheduleJobJobService batchJobJobService;

    @Autowired
    private ZkService zkService;

    @Autowired
    private ScheduleJobJobDao scheduleJobJobDao;

    @Autowired
    private JobRichOperator jobRichOperator;

    @Autowired
    private ActionService actionService;

    @Autowired
    private JobPartitioner jobPartitioner;

    @Autowired
    private JobStopDealer jobStopDealer;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JobGraphTriggerDao jobGraphTriggerDao;

    @Autowired
    private ScheduleJobOperatorRecordDao scheduleJobOperatorRecordDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    private final static List<Integer> FINISH_STATUS = Lists.newArrayList(RdosTaskStatus.FINISHED.getStatus(), RdosTaskStatus.MANUALSUCCESS.getStatus(), RdosTaskStatus.CANCELLING.getStatus(), RdosTaskStatus.CANCELED.getStatus());

    /**
     * 根据任务id展示任务详情
     *
     * @author toutian
     */
    public ScheduleJob getJobById( long jobId) {
        ScheduleJob scheduleJob = scheduleJobDao.getOne(jobId);
        if (null!= scheduleJob && StringUtils.isBlank(scheduleJob.getSubmitUserName())) {
            // 如果拿不到用户时，使用默认的用户
            scheduleJob.setSubmitUserName(environmentContext.getHadoopUserName());
        }
        return scheduleJob;
    }

    public ScheduleJob getJobByJobKeyAndType(String jobKey, int type) {
        return scheduleJobDao.getByJobKeyAndType(jobKey, type);
    }


    /**
     * 获取指定状态的工作任务列表
     *
     * @param projectId
     * @param tenantId
     * @param appType
     * @param dtuicTenantId
     * @return
     */
    public PageResult getStatusJobList(Long projectId, Long tenantId, Integer appType,
                                       Long dtuicTenantId, Integer status, int pageSize, int pageIndex) {
        if (null == status || null == dtuicTenantId) {
            return null;
        }
        List<Integer> statusCode = RdosTaskStatus.getCollectionStatus(status);
        if (CollectionUtils.isEmpty(statusCode)) {
            return null;
        }
        List<Map<String, Object>> data = scheduleJobDao.countByStatusAndType(EScheduleType.NORMAL_SCHEDULE.getType(), DateUtil.getUnStandardFormattedDate(DateUtil.calTodayMills()),
                DateUtil.getUnStandardFormattedDate(DateUtil.TOMORROW_ZERO()), tenantId, projectId, appType, dtuicTenantId, statusCode);
        if(CollectionUtils.isEmpty(data)){
            return null;
        }
        int count = 0;
        for (Map<String, Object> info : data) {
            if(null != info.get("count")) {
                count += MathUtil.getIntegerVal(info.get("count"));
            }
        }
        PageQuery<Object> pageQuery = new PageQuery<>(pageIndex, pageSize);
        List<Map<String, Object>> dataMaps = scheduleJobDao.selectStatusAndType(EScheduleType.NORMAL_SCHEDULE.getType(), DateUtil.getUnStandardFormattedDate(DateUtil.calTodayMills()),
                DateUtil.getUnStandardFormattedDate(DateUtil.TOMORROW_ZERO()), tenantId, projectId, appType, dtuicTenantId, statusCode, pageQuery.getStart(), pageQuery.getPageSize());
        return new PageResult<>(dataMaps, count, pageQuery);
    }

    /**
     * 获取各个状态任务的数量
     */
    public ScheduleJobStatusVO getStatusCount( Long projectId,  Long tenantId,  Integer appType,  Long dtuicTenantId) {
        ScheduleJobStatusVO scheduleJobStatusVO =new ScheduleJobStatusVO();
        List<Map<String, Object>> data = scheduleJobDao.countByStatusAndType(EScheduleType.NORMAL_SCHEDULE.getType(), DateUtil.getUnStandardFormattedDate(DateUtil.calTodayMills()),
                DateUtil.getUnStandardFormattedDate(DateUtil.TOMORROW_ZERO()), tenantId, projectId, appType, dtuicTenantId, null);
        buildCount( scheduleJobStatusVO, data);
        return scheduleJobStatusVO;
    }

    private void buildCount(ScheduleJobStatusVO scheduleJobStatusVO, List<Map<String, Object>> data) {
        int all = 0;
        List<ScheduleJobStatusCountVO> scheduleJobStatusCountVOS = Lists.newArrayList();
        for (Integer code : RdosTaskStatus.getCollectionStatus().keySet()) {
            List<Integer> status = RdosTaskStatus.getCollectionStatus(code);
            ScheduleJobStatusCountVO scheduleJobStatusCountVO = new ScheduleJobStatusCountVO();
            int count = 0;
            for (Map<String, Object> info : data) {
                if (status.contains(MathUtil.getIntegerVal(info.get("status")))) {
                    count += MathUtil.getIntegerVal(info.get("count"));
                }
            }
            all += count;
            RdosTaskStatus taskStatus = RdosTaskStatus.getTaskStatus(code);
            if (taskStatus != null) {
                scheduleJobStatusCountVO.setTaskName(taskStatus.name());
                scheduleJobStatusCountVO.setTaskStatusName(taskStatus.name());
            }
            scheduleJobStatusCountVO.setCount(count);
            scheduleJobStatusCountVOS.add(scheduleJobStatusCountVO);
        }
        scheduleJobStatusVO.setAll(all);
        scheduleJobStatusVO.setScheduleJobStatusCountVO(scheduleJobStatusCountVOS);
    }


    /**
     * 任务运维 - 搜索
     *
     * @return
     * @author toutian
     */
    public PageResult<List<com.dtstack.engine.master.vo.ScheduleJobVO>> queryJobs(QueryJobDTO vo) {

        if (vo.getType() == null && CollectionUtils.isEmpty(vo.getTypes())) {
            throw new RdosDefineException("Type parameter is required", ErrorCode.INVALID_PARAMETERS);
        }
        vo.setSplitFiledFlag(true);
        ScheduleJobDTO batchJobDTO = this.createQuery(vo);

        boolean queryAll = false;
        if (StringUtils.isNotBlank(vo.getTaskName()) ||
                vo.getCycEndDay() != null ||
                vo.getCycStartDay() != null ||
                StringUtils.isNotBlank(vo.getJobStatuses()) ||
                StringUtils.isNotBlank(vo.getTaskType())) {
            //条件查询：针对工作流任务，查询全部父子节点
            queryAll = true;
        } else {
            //无条件：只查询工作流父节点
            batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
        }
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(vo.getCurrentPage(), vo.getPageSize(), "gmt_modified", Sort.DESC.name());

        // 设置是模糊匹配类型还是精确匹配
        String searchType = vo.getSearchType();
        changeSearchType(batchJobDTO, searchType);
        batchJobDTO.setPageQuery(true);
        pageQuery.setModel(batchJobDTO);

        int count = 0;
        List<com.dtstack.engine.master.vo.ScheduleJobVO> result = new ArrayList<>();
        //先将满足条件的taskId查出来，缩小过滤范围
        if (StringUtils.isNotBlank(vo.getTaskName()) || null != vo.getOwnerId()) {
            List<ScheduleTaskShade> batchTaskShades = scheduleTaskShadeDao.listByNameLikeWithSearchType(vo.getProjectId(), vo.getTaskName(),
                    vo.getAppType(), vo.getOwnerId(), vo.getProjectIds(), batchJobDTO.getSearchType());
            if (CollectionUtils.isNotEmpty(batchTaskShades)) {
                batchJobDTO.setTaskIds(batchTaskShades.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList()));
            } else {
                return new PageResult<>(result, count, pageQuery);
            }
        }
         count = queryNormalJob(batchJobDTO, queryAll, pageQuery, result);


        return new PageResult<>(result, count, pageQuery);
    }

    private void changeSearchType(ScheduleJobDTO batchJobDTO, String searchType) {
        if (StringUtils.isEmpty(searchType) || "fuzzy".equalsIgnoreCase(searchType)) {
            //全模糊匹配
            batchJobDTO.setSearchType(1);
        } else if ("precise".equalsIgnoreCase(searchType)) {
            //精确匹配
            batchJobDTO.setSearchType(2);
        } else if ("front".equalsIgnoreCase(searchType)) {
            //右模糊匹配
            batchJobDTO.setSearchType(3);
        } else if ("tail".equalsIgnoreCase(searchType)) {
            batchJobDTO.setSearchType(4);
        } else {
            batchJobDTO.setSearchType(1);
        }
    }

    /**
     * 正常查询 分钟小时不归类
     * @param batchJobDTO
     * @param queryAll
     * @param pageQuery
     * @param result
     * @return
     * @throws Exception
     */
    private int queryNormalJob(ScheduleJobDTO batchJobDTO, boolean queryAll, PageQuery<ScheduleJobDTO> pageQuery, List<com.dtstack.engine.master.vo.ScheduleJobVO> result) {
        int count = scheduleJobDao.generalCount(batchJobDTO);
        if (count > 0) {
            List<ScheduleJob> scheduleJobs = scheduleJobDao.generalQuery(pageQuery);
            if (CollectionUtils.isNotEmpty(scheduleJobs)) {
                Map<Long, ScheduleTaskForFillDataDTO> shadeMap = this.prepare(scheduleJobs);
                List<ScheduleJobVO> batchJobVOS = this.transfer(scheduleJobs, shadeMap);

                if (queryAll) {
                    //处理工作流下级
                    dealFlowWorkSubJobs(batchJobVOS);
                } else {
                    //前端异步获取relatedJobs
                    //dealFlowWorkJobs(vos, shadeMap);
                }
                if (CollectionUtils.isNotEmpty(batchJobVOS)) {
                    for (ScheduleJobVO batchJobVO : batchJobVOS) {
                        if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(batchJobVO.getStatus())) {
                            batchJobVO.setStatus(RdosTaskStatus.RUNNING.getStatus());
                        }
                        result.add(batchJobVO);
                    }
                }
            }
        }
        return count;
    }




    public List<SchedulePeriodInfoVO> displayPeriods( boolean isAfter,  Long jobId,  Long projectId,  int limit) {
        ScheduleJob job = scheduleJobDao.getOne(jobId);
        if (job == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        //需要根据查询的job的类型来
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listAfterOrBeforeJobs(job.getTaskId(), isAfter, job.getCycTime(),job.getAppType(),job.getType());
        Collections.sort(scheduleJobs, new Comparator<ScheduleJob>() {

            public int compare(ScheduleJob o1, ScheduleJob o2) {
                if (!NumberUtils.isNumber(o1.getCycTime())) {
                    return 1;
                }

                if (!NumberUtils.isNumber(o2.getCycTime())) {
                    return -1;
                }

                if (Long.parseLong(o1.getCycTime()) < Long.parseLong(o2.getCycTime())) {
                    return 1;
                }
                if (Long.parseLong(o1.getCycTime()) > Long.parseLong(o2.getCycTime())) {
                    return -1;
                }
                return 0;
            }
        });
        if (scheduleJobs.size() > limit) {
            scheduleJobs = scheduleJobs.subList(0, limit);
        }
        List<SchedulePeriodInfoVO> vos = new ArrayList<>(scheduleJobs.size());
        scheduleJobs.forEach(e -> {
            SchedulePeriodInfoVO vo = new SchedulePeriodInfoVO();
            vo.setJobId(e.getId());
            vo.setCycTime(DateUtil.addTimeSplit(e.getCycTime()));
            vo.setStatus(e.getStatus());
            vo.setTaskId(e.getTaskId());
            vo.setVersion(e.getVersionId());
            vos.add(vo);
        });
        return vos;
    }

    /**
     * 获取工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    public ScheduleJobVO getRelatedJobs( String jobId,  String query) {
        QueryJobDTO vo = JSONObject.parseObject(query, QueryJobDTO.class);
        if(null == vo){
            return null;
        }
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, Deleted.NORMAL.getStatus());
        if(null == scheduleJob){
            throw new RdosDefineException("该实例对象不存在");
        }
        Map<Long, ScheduleTaskForFillDataDTO> shadeMap = this.prepare(Lists.newArrayList(scheduleJob));
        List<ScheduleJobVO> transfer = this.transfer(Lists.newArrayList(scheduleJob), shadeMap);
        if (CollectionUtils.isEmpty(transfer)) {
            return null;
        }
        ScheduleJobVO batchJobVO = transfer.get(0);

        if (EScheduleJobType.WORK_FLOW.getVal().equals(batchJobVO.getBatchTask().getTaskType())) {
            vo.setSplitFiledFlag(true);
            //除去任务类型中的工作流类型的条件，用于展示下游节点
            if (StringUtils.isNotBlank(vo.getTaskType())) {
                vo.setTaskType(vo.getTaskType().replace(String.valueOf(EScheduleJobType.WORK_FLOW.getVal()), ""));
            }
            ScheduleJobDTO batchJobDTO = createQuery(vo);
            batchJobDTO.setPageQuery(false);
            batchJobDTO.setFlowJobId(jobId);
            PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(vo.getCurrentPage(), vo.getPageSize(), "gmt_modified", Sort.DESC.name());
            pageQuery.setModel(batchJobDTO);
            batchJobDTO.setNeedQuerySonNode(true);
            List<ScheduleJob> subJobs = scheduleJobDao.generalQuery(pageQuery);
            Map<Long, ScheduleTaskForFillDataDTO> subShadeMap = this.prepare(subJobs);
            List<ScheduleJobVO> subJobVOs = this.transfer(subJobs, subShadeMap);

            List<com.dtstack.engine.master.vo.ScheduleJobVO> relatedJobVOs= new ArrayList<>(subJobVOs.size());
            subJobVOs.forEach(subJobVO -> relatedJobVOs.add(subJobVO));
            batchJobVO.setRelatedJobs(relatedJobVOs);
            return batchJobVO;
        } else {
            throw new RdosDefineException("Only workflow tasks have subordinate nodes");
        }

    }


    //处理工作流子节点
    private void dealFlowWorkSubJobs(List<ScheduleJobVO> vos) {

        Map<String, ScheduleJobVO> record = Maps.newHashMap();
        Map<String, Integer> voIndex = Maps.newHashMap();
        vos.forEach(job -> voIndex.put(job.getJobId(), vos.indexOf(job)));
        List<ScheduleJobVO> copy = Lists.newArrayList(vos);
        Iterator<ScheduleJobVO> iterator = vos.iterator();
        while (iterator.hasNext()) {
            ScheduleJobVO jobVO = iterator.next();
            String flowJobId = jobVO.getFlowJobId();
            if (!"0".equals(flowJobId)) {
                //是工作流子节点
                if (record.containsKey(flowJobId)) {
                    ScheduleJobVO flowVo = record.get(flowJobId);
                    flowVo.getRelatedJobs().add(jobVO);
                    iterator.remove();
                } else {
                    ScheduleJobVO flowVO;
                    if (voIndex.containsKey(flowJobId)) {
                        //查出来的任务列表中有该子结点的工作流
                        flowVO = copy.get(voIndex.get(flowJobId));
                        //将工作流子节点设置为工作流关联jobs
                        flowVO.setRelatedJobs(Lists.newArrayList(jobVO));
                        iterator.remove();
                    } else {
                        ScheduleJob flow = scheduleJobDao.getByJobId(flowJobId, Deleted.NORMAL.getStatus());
                        if (flow == null) {
                            continue;
                        }
                        Map<Long, ScheduleTaskForFillDataDTO> batchTaskShadeMap = this.prepare(Lists.newArrayList(flow));
                        List<ScheduleJobVO> flowVOs = this.transfer(Lists.newArrayList(flow), batchTaskShadeMap);
                        flowVO = flowVOs.get(0);
                        flowVO.setRelatedJobs(Lists.newArrayList(jobVO));
                        //将工作流子结点替换成工作流
                        vos.set(vos.indexOf(jobVO), flowVO);
                    }
                    record.put(flowJobId, flowVO);
                }
            }
        }
    }


    private Map<Long, ScheduleTaskForFillDataDTO> prepare(List<ScheduleJob> scheduleJobs) {
        if (CollectionUtils.isEmpty(scheduleJobs)) {
            return new HashMap<>(0);
        }
        Integer appType = scheduleJobs.get(0).getAppType();

        Set<Long> taskIdList = scheduleJobs.stream().map(ScheduleJob::getTaskId).collect(Collectors.toSet());

        return scheduleTaskShadeDao.listSimpleTaskByTaskIds(taskIdList, null,appType).stream()
                .collect(Collectors.toMap(ScheduleTaskForFillDataDTO::getTaskId, scheduleTaskForFillDataDTO -> scheduleTaskForFillDataDTO));
    }

    private List<ScheduleJobVO> transfer(List<ScheduleJob> scheduleJobs, Map<Long, ScheduleTaskForFillDataDTO> batchTaskShadeMap) {

        if(CollectionUtils.isEmpty(scheduleJobs)){
            return Collections.EMPTY_LIST;
        }
        List<ScheduleJobVO> vos = new ArrayList<>(scheduleJobs.size());
        for (ScheduleJob scheduleJob : scheduleJobs) {
            ScheduleTaskForFillDataDTO taskShade = batchTaskShadeMap.get(scheduleJob.getTaskId());
            if (taskShade == null) {
                continue;
            }
            /*//维持旧接口 数据结构
            ScheduleEngineJob engineJob = new ScheduleEngineJob();
            engineJob.setStatus(scheduleJob.getStatus());
            engineJob.setRetryNum(scheduleJob.getRetryNum());
            String voTaskName = taskShade.getName();
            ScheduleJobVO batchJobVO = new ScheduleJobVO(scheduleJob);
            if (scheduleJob.getExecStartTime() != null) {
                batchJobVO.setExecStartDate(timeFormatter.print(scheduleJob.getExecStartTime().getTime()));
                engineJob.setExecStartTime(new Timestamp(scheduleJob.getExecStartTime().getTime()));
            }
            if (scheduleJob.getExecEndTime() != null) {
                batchJobVO.setExecEndDate(timeFormatter.print(scheduleJob.getExecEndTime().getTime()));
                engineJob.setExecEndTime(new Timestamp(scheduleJob.getExecEndTime().getTime()));
            }
            engineJob.setExecTime(scheduleJob.getExecTime());
            batchJobVO.setScheduleEngineJob(engineJob);

            ScheduleTaskVO taskVO = new ScheduleTaskVO(taskShade);
            taskVO.setName(voTaskName);
            if (scheduleJob.getPeriodType() != null) {
                batchJobVO.setTaskPeriodId(scheduleJob.getPeriodType());
            }
            batchJobVO.setVersion(scheduleJob.getVersionId());
            batchJobVO.setBatchTask(taskVO);
            batchJobVO.setOwnerUserId(taskShade.getOwnerUserId());
            vos.add(batchJobVO);*/
        }
        return vos;
    }


    /**
     * 获取任务的状态统计信息
     *
     * @author toutian
     */
    public Map<String, Long> queryJobsStatusStatistics(QueryJobDTO vo) {

        if (vo.getType() == null) {
            throw new RdosDefineException("Type is required", ErrorCode.INVALID_PARAMETERS);
        }
        vo.setSplitFiledFlag(true);
        ScheduleJobDTO batchJobDTO = createQuery(vo);
        batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
        if (vo.getAppType() == AppType.DATASCIENCE.getType()) {
            batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
        }
        //需要查询工作流的子节点
        batchJobDTO.setNeedQuerySonNode(true);
        filterQuery(vo, batchJobDTO);
        List<StatusCount> statusCountList = scheduleJobDao.getJobsStatusStatistics(batchJobDTO);
        Map<String, Long> attachment = Maps.newHashMap();
        if(CollectionUtils.isEmpty(statusCountList)){
            return attachment;
        }
        long totalNum = 0;
        mergeStatusAndShow(statusCountList, attachment, totalNum);

        return attachment;
    }

    /**
     * @author newman
     * @Description 将数据库细分的任务状态合并展示给前端
     * @Date 2020-12-18 16:16
     * @param statusCountList:
     * @param attachment:
     * @param totalNum:
     * @return: void
     **/
    private void mergeStatusAndShow(List<StatusCount> statusCountList, Map<String, Long> attachment, long totalNum) {
        Map<Integer, List<Integer>> statusMap = RdosTaskStatus.getStatusFailedDetail();
        for (Map.Entry<Integer, List<Integer>> entry : statusMap.entrySet()) {
            String statusName = RdosTaskStatus.getCode(entry.getKey());
            List<Integer> statuses = entry.getValue();
            long num = 0;
            for (StatusCount statusCount : statusCountList) {
                if (statuses.contains(statusCount.getStatus())) {
                    num += statusCount.getCount();
                }
            }
            if (!attachment.containsKey(statusName)) {
                attachment.put(statusName, num);
            } else {
                //上一个该状态的数量
                Long lastNum = attachment.getOrDefault(statusName,0L);
                attachment.put(statusName, num + lastNum);
            }
            totalNum += num;
        }
        attachment.putIfAbsent("ALL", totalNum);
    }

    /**
     * @author newman
     * @Description 提前查出taskId，缩小查询范围
     * @Date 2020-12-18 15:31
     * @param vo:
     * @param batchJobDTO:
     * @return: void
     **/
    private void filterQuery(QueryJobDTO vo, ScheduleJobDTO batchJobDTO) {
        if (StringUtils.isNotBlank(vo.getTaskName()) || null!= vo.getOwnerId()) {
            List<ScheduleTaskShade> batchTaskShades = scheduleTaskShadeDao.listByNameLike(vo.getProjectId(), vo.getTaskName(), vo.getAppType(), vo.getOwnerId(),vo.getProjectIds());
            if (CollectionUtils.isNotEmpty(batchTaskShades)) {
                batchJobDTO.setTaskIds(batchTaskShades.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList()));
            }
        }
    }

    private Map<Integer, List<Integer>> getStatusMap(Boolean splitFiledFlag) {
        Map<Integer, List<Integer>> statusMap;
        if (null != splitFiledFlag && splitFiledFlag) {
            statusMap = RdosTaskStatus.getStatusFailedDetailAndExpire();
        } else {
            statusMap = RdosTaskStatus.getCollectionStatus();
        }
        return statusMap;
    }


    private ScheduleJobDTO createQuery(QueryJobDTO vo) {

        ScheduleJobDTO batchJobDTO = new ScheduleJobDTO();
        this.createBaseQuery(vo, batchJobDTO);

        //任务状态
        if (StringUtils.isNotBlank(vo.getJobStatuses())) {
            List<Integer> statues = new ArrayList<>();
            String[] statuses = vo.getJobStatuses().split(",");
            // 根据失败状态拆分标记来确定具体是哪一个状态map
            Map<Integer, List<Integer>> statusMap = getStatusMap(vo.getSplitFiledFlag());
            for (String status : statuses) {
                List<Integer> statusList = statusMap.get(new Integer(status));
                if(CollectionUtils.isNotEmpty(statusList)){
                    statues.addAll(statusList);
                }
            }
            batchJobDTO.setJobStatuses(statues);
        }

        //任务名
        if (StringUtils.isNotBlank(vo.getTaskName())) {
            batchJobDTO.setTaskNameLike(vo.getTaskName());
        }

        if (StringUtils.isNotBlank(vo.getFillTaskName())) {
            batchJobDTO.setJobNameRightLike(vo.getFillTaskName() + "-");
        }

        //责任人
        if (null != vo.getOwnerId() && vo.getOwnerId() != 0) {
            batchJobDTO.setOwnerUserId(vo.getOwnerId());
        }
        //业务时间
        this.setBizDay(batchJobDTO, vo.getBizStartDay(), vo.getBizEndDay(), vo.getTenantId(), vo.getProjectId());

        this.setCycDay(batchJobDTO, vo.getCycStartDay(), vo.getCycEndDay(), vo.getTenantId(), vo.getProjectId());

        //执行耗时
        if (null != vo.getExecTime()) {
            batchJobDTO.setExecTime(vo.getExecTime() * 1000);
        }
        // baseQuery里已经设置了分页，这里去掉

        if(vo.getExecStartDay()!=null){
            batchJobDTO.setExecStartDay(new Date(vo.getExecStartDay()));
        }
        if (vo.getExecEndDay()!=null) {
            batchJobDTO.setExecEndDay(new Date(vo.getExecEndDay()));
        }
        return batchJobDTO;
    }

    private void createBaseQuery(QueryJobDTO vo, ScheduleJobDTO batchJobDTO) {

        batchJobDTO.setTenantId(vo.getTenantId());
        batchJobDTO.setTaskTypes(convertStringToList(vo.getTaskType()));
        batchJobDTO.setExecTimeSort(vo.getExecTimeSort());
        batchJobDTO.setExecStartSort(vo.getExecStartSort());
        batchJobDTO.setExecEndSort(vo.getExecEndSort());
        batchJobDTO.setCycSort(vo.getCycSort());
        batchJobDTO.setRetryNumSort(vo.getRetryNumSort());
        batchJobDTO.setBusinessDateSort(vo.getBusinessDateSort());
        batchJobDTO.setTaskPeriodId(convertStringToList(vo.getTaskPeriodId()));
        batchJobDTO.setAppType(vo.getAppType());
        batchJobDTO.setBusinessType(vo.getBusinessType());
        batchJobDTO.setTypes(vo.getTypes());

        if (CollectionUtils.isNotEmpty(vo.getProjectIds())) {
            batchJobDTO.setProjectIds(vo.getProjectIds());
        }

        //调度类型
        if (vo.getType() != null) {
            batchJobDTO.setType(vo.getType());
        }

        if (StringUtils.isNotBlank(vo.getTaskName()) ||
                vo.getCycEndDay() != null ||
                vo.getCycStartDay() != null ||
                StringUtils.isNotBlank(vo.getJobStatuses()) ||
                StringUtils.isNotBlank(vo.getTaskType())) {
            //条件查询：针对工作流任务，查询全部父子节点
            batchJobDTO.setNeedQuerySonNode(true);
        } else {
            //无条件：只查询工作流父节点
            batchJobDTO.setNeedQuerySonNode(false);
        }

        //分页
        batchJobDTO.setPageQuery(true);
        //bugfix #19764 为对入参做处理
        if (!Strings.isNullOrEmpty(vo.getJobStatuses())) {
            batchJobDTO.setJobStatuses(Arrays.stream(vo.getJobStatuses().split(",")).map(Integer::parseInt).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(vo.getTaskIds())) {
            batchJobDTO.setTaskIds(vo.getTaskIds());
        }
        if (null != vo.getTaskId()) {
            if (null == batchJobDTO.getTaskIds()) {
                batchJobDTO.setTaskIds(new ArrayList<>());
            }
            batchJobDTO.getTaskIds().add(vo.getTaskId());
        }
    }

    private void setBizDay(ScheduleJobDTO batchJobDTO, Long bizStartDay, Long bizEndDay, Long tenantId, Long projectId) {
        if (bizStartDay != null && bizEndDay != null) {
            String bizStart = dayFormatterAll.print(getTime(bizStartDay * 1000, 0).getTime());
            String bizEnd = dayFormatterAll.print(getTime(bizEndDay * 1000, -1).getTime());
            batchJobDTO.setBizStartDay(bizStart);
            batchJobDTO.setBizEndDay(bizEnd);

            //设置调度始日期为业务开始日期的下一天
            batchJobDTO.setCycStartDay(dayFormatterAll.print(getTime(bizStartDay * 1000, -1).getTime()));
            batchJobDTO.setCycEndDay(dayFormatterAll.print(getTime(bizEndDay * 1000, -2).getTime()));
        }
    }

    private void setCycDay(ScheduleJobDTO batchJobDTO, Long cycStartDay, Long cycEndDay, Long tenantId, Long projectId) {
        if (cycStartDay != null && cycEndDay != null) {
            String cycStart = dayFormatterAll.print(getCycTime(cycStartDay * 1000).getTime());
            String cycEnd = dayFormatterAll.print(getCycTime(cycEndDay * 1000).getTime());
            batchJobDTO.setCycStartDay(cycStart);
            batchJobDTO.setCycEndDay(cycEnd);
        }
    }

    private Timestamp getTime(Long timestamp, int day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -day);
        return new Timestamp(calendar.getTimeInMillis());
    }


    private Timestamp getCycTime(Long timestamp) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);
        return new Timestamp(calendar.getTimeInMillis());
    }

    private List<Integer> convertStringToList(String str) {
        List<Integer> resultList = new ArrayList<>();
        if (StringUtils.isNotBlank(str)) {
            String[] split = str.split(",");
            for (String sp : split) {
                if (StringUtils.isBlank(sp)) {
                    continue;
                }
                resultList.add(new Integer(sp));
            }
        }
        return resultList;
    }


    public List<ScheduleRunDetailVO> jobDetail( Long taskId,  Integer appType) {

        ScheduleTaskShade task = batchTaskShadeService.getBatchTaskById(taskId);

        if (task ==null ) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }

        PageQuery pageQuery = new PageQuery(1, 20, "business_date", Sort.DESC.name());
        List<Map<String, String>> jobs = scheduleJobDao.listTaskExeTimeInfo(task.getTaskId(), FINISH_STATUS, pageQuery,appType);
        List<ScheduleRunDetailVO> details = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(jobs)) {
            for (Map<String, String> job : jobs) {
                String execStartTimeObj = MathUtil.getString(job.get("execStartTime"));
                String execEndTimeObj = MathUtil.getString(job.get("execEndTime"));
                Long execTime = MathUtil.getLongVal(job.get("execTime"));

                ScheduleRunDetailVO runDetail = new ScheduleRunDetailVO();
                if (execTime == null || execTime == 0L) {
                    continue;
                }

                runDetail.setExecTime(execTime);
                runDetail.setStartTime(execStartTimeObj);
                runDetail.setEndTime(execEndTimeObj);

                ScheduleTaskShade jobTask = batchTaskShadeService.getBatchTaskById(taskId);
//                runDetail.setTaskName(jobTask.getName());
                details.add(runDetail);
            }
        }

        return details;
    }

    public Integer updateStatusAndLogInfoAndExecTimeById(String jobId, Integer status, String msg,Date execStartTime,Date execEndTime){
        if (StringUtils.isNotBlank(msg) && msg.length() > 5000) {
            msg = msg.substring(0, 5000) + "...";
        }
        return scheduleJobDao.updateStatusByJobId(jobId, status, msg,null,execStartTime,execEndTime);
    }

    public Integer updateStatusAndLogInfoById(String jobId, Integer status, String msg) {
        if (StringUtils.isNotBlank(msg) && msg.length() > 5000) {
            msg = msg.substring(0, 5000) + "...";
        }
        return scheduleJobDao.updateStatusByJobId(jobId, status, msg,null,null,null);
    }

    public Integer updateStatusByJobId(String jobId, Integer status,Integer versionId) {
        return scheduleJobDao.updateStatusByJobId(jobId, status, null,versionId,null,null);
    }

    public Long startJob(ScheduleJob scheduleJob) throws Exception {
        sendTaskStartTrigger(scheduleJob);
        return scheduleJob.getId();
    }


    public Integer updateStatusWithExecTime(ScheduleJob updateJob) {
        if(null == updateJob || null == updateJob.getJobId() || null == updateJob.getAppType()){
            return 0;
        }
        ScheduleJob job = scheduleJobDao.getByJobId(updateJob.getJobId(), Deleted.NORMAL.getStatus());
        if (null != job.getExecStartTime() && null != updateJob.getExecEndTime()){
            updateJob.setExecTime((updateJob.getExecEndTime().getTime()-job.getExecStartTime().getTime())/1000);
        }
        return scheduleJobDao.updateStatusWithExecTime(updateJob);
    }

    public void testTrigger( String jobId) {
        ScheduleJob rdosJobByJobId = scheduleJobDao.getRdosJobByJobId(jobId);
        if (null != rdosJobByJobId) {
            try {
                this.sendTaskStartTrigger(rdosJobByJobId);
            } catch (Exception e) {
                LOGGER.error(" job  {} run fail with info is null",rdosJobByJobId.getJobId(),e);
            }
        }
    }

    /**
     * 触发 engine 执行指定task
     */
    public void sendTaskStartTrigger(ScheduleJob scheduleJob) throws Exception {

        if(null == scheduleJob.getTaskId() || null == scheduleJob.getAppType() ){
            throw new RdosDefineException("任务id和appType不能为空");
        }
        ScheduleTaskShade batchTask = batchTaskShadeService.getBatchTaskById(scheduleJob.getTaskId());
        if (batchTask == null) {
            throw new RdosDefineException("can not find task by id:" + scheduleJob.getTaskId());
        }
        if (checkIsVirtual(scheduleJob, batchTask)) {
            return;
        }
        if (checkWorkFlow(scheduleJob, batchTask)) {
            return;
        }
        String extInfoByTaskId = scheduleTaskShadeDao.getExtInfoByTaskId(scheduleJob.getTaskId(), scheduleJob.getAppType());
        if (StringUtils.isNotBlank(extInfoByTaskId)) {
            JSONObject extObject = JSONObject.parseObject(extInfoByTaskId);
            if (null != extObject ) {
                JSONObject info = extObject.getJSONObject(TaskConstant.INFO);
                if (null != info ) {
                    ParamActionExt paramActionExt = actionService.paramActionExt(batchTask, scheduleJob, info);
                    if (paramActionExt != null) {
                        this.updateStatusByJobId(scheduleJob.getJobId(), RdosTaskStatus.SUBMITTING.getStatus(),batchTask.getVersionId());
                        actionService.start(paramActionExt);
                        return;
                    }
                }
            }
        }
        //额外信息为空 标记任务为失败
        this.updateStatusAndLogInfoById(scheduleJob.getJobId(), RdosTaskStatus.FAILED.getStatus(), "任务运行信息为空");
        LOGGER.error(" job  {} run fail with info is null",scheduleJob.getJobId());
    }

    /**
     * @author newman
     * @Description 工作流或算法实验，保持提交状态
     * @Date 2020-12-18 16:27
     * @param scheduleJob:
     * @param batchTask:
     * @return: boolean
     **/
    private boolean checkWorkFlow(ScheduleJob scheduleJob, ScheduleTaskShade batchTask) {
        //工作流节点保持提交中状态,状态更新见BatchFlowWorkJobService
        if (batchTask.getTaskType().equals(EScheduleJobType.WORK_FLOW.getVal())){
            ScheduleJob updateJob = new ScheduleJob();
            updateJob.setJobId(scheduleJob.getJobId());
            updateJob.setAppType(scheduleJob.getAppType());
            updateJob.setStatus(RdosTaskStatus.SUBMITTING.getStatus());
            updateJob.setExecStartTime(new Timestamp(System.currentTimeMillis()));
            updateJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
            scheduleJobDao.updateStatusWithExecTime(updateJob);
            return true;
        }
        return false;
    }

    /**
     * @author newman
     * @Description 如果是虚结点，直接完成
     * @Date 2020-12-18 16:25
     * @param scheduleJob:
     * @param batchTask:
     * @return: boolean
     **/
    private boolean checkIsVirtual(ScheduleJob scheduleJob, ScheduleTaskShade batchTask) {
        //判断是不是虚节点---虚节点直接完成
        if (batchTask.getTaskType().equals(EScheduleJobType.VIRTUAL.getType())) {
            //虚节点写入开始时间和结束时间
            ScheduleJob updateJob = new ScheduleJob();
            updateJob.setJobId(scheduleJob.getJobId());
            updateJob.setAppType(scheduleJob.getAppType());
            updateJob.setStatus(RdosTaskStatus.FINISHED.getStatus());
            updateJob.setExecStartTime(new Timestamp(System.currentTimeMillis()));
            updateJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
            updateJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
            updateJob.setExecTime(0L);
            scheduleJobDao.updateStatusWithExecTime(updateJob);
            return true;
        }
        return false;
    }

    public void stopJob( long jobId, Integer appType) {

        ScheduleJob scheduleJob = scheduleJobDao.getOne(jobId);
        stopJobByScheduleJob(appType, scheduleJob);
        // 杀死工作流任务，已经强规则任务
        List<ScheduleJob> jobs = Lists.newArrayList(scheduleJob);
        jobStopDealer.addStopJobs(jobs);
    }

    private void stopJobByScheduleJob(  Integer appType, ScheduleJob scheduleJob) {

        if (scheduleJob == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        ScheduleTaskShade task = batchTaskShadeService.getBatchTaskById(scheduleJob.getTaskId());
        if (task == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        Integer status = scheduleJob.getStatus();
        if (!checkJobCanStop(status)) {
            throw new RdosDefineException(ErrorCode.JOB_CAN_NOT_STOP);
        }

        jobStopDealer.addStopJobs(Lists.newArrayList(scheduleJob));
    }

    public void stopJobByJobId( String jobId, Integer appType) throws Exception{
        if(StringUtils.isBlank(jobId)){
            return;
        }
        LOGGER.info("stop job by jobId {}",jobId);
        ScheduleJob batchJob = scheduleJobDao.getByJobId(jobId,Deleted.NORMAL.getStatus());
        this.stopJobByScheduleJob(appType, batchJob);
    }

    public void stopFillDataJobs( String fillDataJobName,  Long projectId,  Long dtuicTenantId,  Integer appType) {
        //还未发送到engine部分---直接停止
        if (StringUtils.isBlank(fillDataJobName)) {
            return;
        }
        String likeName = fillDataJobName + "-%";
        //发送停止消息到engine
        //查询出所有需要停止的任务
        List<ScheduleJob> needStopIdList = scheduleJobDao.listNeedStopFillDataJob(likeName, RdosTaskStatus.getCanStopStatus(), null, null);
        //通过interceptor的触发状态更新的event
        scheduleJobDao.stopUnsubmitJob(likeName, null, null, RdosTaskStatus.CANCELED.getStatus());
        //发送停止任务消息到engine
        //this.stopSubmittedJob(needStopIdList, dtuicTenantId, appType);
        jobStopDealer.addStopJobs(needStopIdList);
    }


    @Transactional(rollbackFor = Exception.class)
    public int batchStopJobs(List<Long> jobIdList) {
        if (CollectionUtils.isEmpty(jobIdList)) {
            return 0;
        }
        List<ScheduleJob> jobs = new ArrayList<>(scheduleJobDao.listByJobIds(jobIdList));

        return jobStopDealer.addStopJobs(jobs);
    }

    /**
     * jobSize 在负载均衡时 区分 scheduleType（正常调度 和 补数据）
     */
    @Transactional(rollbackFor = Exception.class)
    public Long insertJobList(Collection<ScheduleBatchJob> batchJobCollection, Integer scheduleType) {
        if (CollectionUtils.isEmpty(batchJobCollection)) {
            return null;
        }

        Iterator<ScheduleBatchJob> batchJobIterator = batchJobCollection.iterator();

        //count%20 为一批
        //1: 批量插入BatchJob
        //2: 批量插入BatchJobJobList
        int count = 0;
        int jobBatchSize = environmentContext.getBatchJobInsertSize();
        int jobJobBatchSize = environmentContext.getBatchJobJobInsertSize();
        Long minJobId=null;
        List<ScheduleJob> jobWaitForSave = Lists.newArrayList();
        List<ScheduleJobJob> jobJobWaitForSave = Lists.newArrayList();

        Map<String, Integer> nodeJobSize = computeJobSizeForNode(batchJobCollection.size(), scheduleType);
        for (Map.Entry<String, Integer> nodeJobSizeEntry : nodeJobSize.entrySet()) {
            String nodeAddress = nodeJobSizeEntry.getKey();
            int nodeSize = nodeJobSizeEntry.getValue();
            final int finalBatchNodeSize = nodeSize;
            while (nodeSize > 0 && batchJobIterator.hasNext()) {
                nodeSize--;
                count++;

                ScheduleBatchJob scheduleBatchJob = batchJobIterator.next();

                ScheduleJob scheduleJob = scheduleBatchJob.getScheduleJob();
                scheduleJob.setNodeAddress(nodeAddress);

                jobWaitForSave.add(scheduleJob);
                jobJobWaitForSave.addAll(scheduleBatchJob.getBatchJobJobList());

                LOGGER.debug("insertJobList count:{} batchJobs:{} finalBatchNodeSize:{}", count, batchJobCollection.size(), finalBatchNodeSize);
                if (count % jobBatchSize == 0 || count == (batchJobCollection.size() - 1) || jobJobWaitForSave.size() > jobJobBatchSize) {
                    minJobId = persistJobs(jobWaitForSave, jobJobWaitForSave, minJobId,jobJobBatchSize);
                    LOGGER.info("insertJobList count:{} batchJobs:{} finalBatchNodeSize:{} jobJobSize:{}", count, batchJobCollection.size(), finalBatchNodeSize, jobJobWaitForSave.size());
                }
            }
            LOGGER.info("insertJobList count:{} batchJobs:{} finalBatchNodeSize:{}",count, batchJobCollection.size(), finalBatchNodeSize);
            //结束前persist一次，flush所有jobs
            minJobId = persistJobs(jobWaitForSave, jobJobWaitForSave, minJobId,jobJobBatchSize);

        }
        return minJobId;
    }

    private Map<String, Integer> computeJobSizeForNode(int jobSize, int scheduleType) {
        Map<String, Integer> jobSizeInfo = jobPartitioner.computeBatchJobSize(scheduleType, jobSize);
        if (jobSizeInfo == null) {
            //if empty
            List<String> aliveNodes = zkService.getAliveBrokersChildren();
            jobSizeInfo = new HashMap<>(aliveNodes.size());
            int size = jobSize / aliveNodes.size() + 1;
            for (String aliveNode : aliveNodes) {
                jobSizeInfo.put(aliveNode, size);
            }
        }
        return jobSizeInfo;
    }

    private Long persistJobs(List<ScheduleJob> jobWaitForSave, List<ScheduleJobJob> jobJobWaitForSave, Long minJobId,Integer jobJobBatchSize) {
        try {
            return RetryUtil.executeWithRetry(() -> {
                Long curMinJobId=minJobId;
                if (jobWaitForSave.size() > 0) {
                    scheduleJobDao.batchInsert(jobWaitForSave);
                    if (Objects.isNull(minJobId)) {
                        curMinJobId = jobWaitForSave.stream().map(ScheduleJob::getId).min(Long::compareTo).orElse(null);
                    }
                    jobWaitForSave.clear();
                }
                if (jobJobWaitForSave.size() > 0) {
                    if (jobJobWaitForSave.size() > jobJobBatchSize) {
                        List<List<ScheduleJobJob>> partition = Lists.partition(jobJobWaitForSave, jobJobBatchSize);
                        for (int i = 0; i < partition.size(); i++) {
                            batchJobJobService.batchInsert(partition.get(i));
                            jobJobWaitForSave.removeAll(partition.get(i));
                        }
                    } else {
                        batchJobJobService.batchInsert(jobJobWaitForSave);
                    }
                    jobJobWaitForSave.clear();
                }
                return curMinJobId;
            }, environmentContext.getBuildJobErrorRetry(), 200, false);
        } catch (Exception e) {
            LOGGER.error("!!!!! persistJobs job error !!!! job {} jobjob {}", jobWaitForSave, jobJobWaitForSave, e);
            throw new RdosDefineException(e);
        } finally {
            if (jobWaitForSave.size() > 0) {
                jobWaitForSave.clear();
            }
            if (jobJobWaitForSave.size() > 0) {
                jobJobWaitForSave.clear();
            }
        }
    }


    /**
     * @author newman
     * @Description 校验补数据任务参数
     * @Date 2020-12-14 17:47
     * @param taskJsonStr:
     * @param fillName:
     * @param projectId:
     * @param toDateTime:
     * @param currDateTime:
     * @return: void
     **/
    private void checkFillDataParams(String taskJsonStr, String fillName, Long projectId, DateTime toDateTime, DateTime currDateTime) {

        if (fillName == null) {
            throw new RdosDefineException("(fillName 参数不能为空)", ErrorCode.INVALID_PARAMETERS);
        }

        //补数据的名称中-作为分割名称和后缀信息的分隔符,故不允许使用
        if (fillName.contains("-")) {
            throw new RdosDefineException("(fillName 参数不能包含字符 '-')", ErrorCode.INVALID_PARAMETERS);
        }

        if (!toDateTime.isBefore(currDateTime)) {
            throw new RdosDefineException("(补数据业务日期开始时间不能晚于结束时间)", ErrorCode.INVALID_PARAMETERS);
        }

        //判断补数据的名字每个project必须是唯一的
        boolean existsName = scheduleFillDataJobService.checkExistsName(fillName, projectId);
        if (existsName) {
            throw new RdosDefineException("补数据任务名称已存在", ErrorCode.NAME_ALREADY_EXIST);
        }
    }


    /**
     * 先查询出所有的补数据名称
     * <p>
     * jobName dutyUserId userId 需要关联task表（防止sql慢） 其他情况不需要
     *
     * @param jobName
     * @param runDay
     * @param bizStartDay
     * @param bizEndDay
     * @param dutyUserId
     * @param projectId
     * @param appType
     * @param currentPage
     * @param pageSize
     * @param tenantId
     * @return
     */
    public PageResult<List<ScheduleFillDataJobPreViewVO>> getFillDataJobInfoPreview(String jobName, Long runDay,
                                                Long bizStartDay, Long bizEndDay, Long dutyUserId,
                                                Long projectId, Integer appType,
                                                Integer currentPage, Integer pageSize, Long tenantId,Long dtuicTenantId) {
        final List<ScheduleTaskShade> taskList;
        ScheduleJobDTO batchJobDTO = new ScheduleJobDTO();
        if (!Strings.isNullOrEmpty(jobName)) {
            taskList = batchTaskShadeService.getTasksByName(projectId, jobName, appType);
            if (CollectionUtils.isEmpty(taskList)) {
                return PageResult.EMPTY_PAGE_RESULT;
            } else {
                batchJobDTO.setTaskIds(taskList.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList()));
            }
        }
        //设置分页查询参数
        PageQuery<ScheduleJobDTO> pageQuery = getScheduleJobDTOPageQuery(runDay, bizStartDay, bizEndDay, dutyUserId, projectId, appType, currentPage, pageSize, tenantId, dtuicTenantId, batchJobDTO);
        pageQuery.getModel().setTenantId(tenantId);
        List<ScheduleFillDataJob> fillJobList = scheduleFillDataJobDao.listFillJobByPageQuery(pageQuery);

        if(CollectionUtils.isEmpty(fillJobList)){
            return new PageResult<>(null, 0, pageQuery);
        }

        //内存中按照时间排序
        if (CollectionUtils.isNotEmpty(fillJobList)) {
            fillJobList = fillJobList.stream().sorted((j1, j2) -> {
                return j2.getGmtCreate().compareTo(j1.getGmtCreate());

            }).collect(Collectors.toList());
        }
        List<Map<String, Long>> statistics = new ArrayList<>();
        //查询补数据任务每个状态对应的个数
        if (CollectionUtils.isNotEmpty(fillJobList)) {
            statistics = scheduleJobDao.countByFillDataAllStatus(fillJobList.stream().map(ScheduleFillDataJob::getId).collect(Collectors.toList()), projectId, tenantId,appType);
        }

        List<ScheduleFillDataJobPreViewVO> resultContent = Lists.newArrayList();
        for (ScheduleFillDataJob fillJob : fillJobList) {
            ScheduleFillDataJobPreViewVO preViewVO = new ScheduleFillDataJobPreViewVO();
            preViewVO.setId(fillJob.getId());
            preViewVO.setFillDataJobName(fillJob.getJobName());
            preViewVO.setCreateTime(timeFormatter.print(fillJob.getGmtCreate().getTime()));
            preViewVO.setDutyUserId(fillJob.getCreateUserId());
            preViewVO.setFromDay(fillJob.getFromDay());
            preViewVO.setToDay(fillJob.getToDay());
            //获取补数据执行进度
            this.setFillDataJobProgress(statistics, preViewVO);
            resultContent.add(preViewVO);
        }


        int totalCount = scheduleJobDao.countFillJobNameDistinctWithOutTask(batchJobDTO);

        return new PageResult<>(resultContent, totalCount, pageQuery);
    }


    /**
     * @author newman
     * @Description 设置补数据的分页查询参数
     * @Date 2020-12-21 16:12
     * @param runDay:
     * @param bizStartDay:
     * @param bizEndDay:
     * @param dutyUserId:
     * @param projectId:
     * @param appType:
     * @param currentPage:
     * @param pageSize:
     * @param tenantId:
     * @param batchJobDTO:
     * @return: com.dtstack.engine.common.pager.PageQuery<com.dtstack.engine.dto.ScheduleJobDTO>
     **/
    private PageQuery<ScheduleJobDTO> getScheduleJobDTOPageQuery(Long runDay, Long bizStartDay, Long bizEndDay, Long dutyUserId, Long projectId, Integer appType, Integer currentPage, Integer pageSize, Long tenantId,Long dtuicTenantId, ScheduleJobDTO batchJobDTO) {
        if (runDay != null) {
            batchJobDTO.setStartGmtCreate(new Timestamp(runDay * 1000L));
        }
        this.setBizDay(batchJobDTO, bizStartDay, bizEndDay, tenantId, projectId);
        if (dutyUserId != null) {
            batchJobDTO.setCreateUserId(dutyUserId);
        }
        batchJobDTO.setType(EScheduleType.FILL_DATA.getType());
        batchJobDTO.setNeedQuerySonNode(true);
        batchJobDTO.setAppType(appType);
        batchJobDTO.setOwnerUserId(dutyUserId);
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(currentPage, pageSize, "gmt_create", Sort.DESC.name());
        pageQuery.setModel(batchJobDTO);
        return pageQuery;
    }

    /**
     * 补数据的执行进度
     *
     * @param statistics
     * @param preViewVO
     */
    private void setFillDataJobProgress(List<Map<String, Long>> statistics, ScheduleFillDataJobPreViewVO
            preViewVO) {
        Map<Long, Long> statisticsMap = new HashMap<>();
        for (Map<String, Long> statistic : statistics) {
            Object fillId = statistic.get("fillId");
            long id = preViewVO.getId();
            if (((Integer) fillId).longValue() == id) {
                statisticsMap.put(statistic.get("status"), statistic.get("count"));
            }
        }

        Map<Integer, Long> resultMap = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : RdosTaskStatus.getCollectionStatus().entrySet()) {
            int showStatus = entry.getKey();
            long sum = 0;
            for (Integer value : entry.getValue()) {
                Long statusSum = statisticsMap.get(value);
                sum += statusSum == null ? 0L : statusSum;
            }
            resultMap.put(showStatus, sum);
        }

        Long unSubmit = resultMap.get(RdosTaskStatus.UNSUBMIT.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.UNSUBMIT.getStatus());
        Long running = resultMap.get(RdosTaskStatus.RUNNING.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.RUNNING.getStatus());
        running += resultMap.get(RdosTaskStatus.NOTFOUND.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.NOTFOUND.getStatus());
        Long finished = resultMap.get(RdosTaskStatus.FINISHED.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.FINISHED.getStatus());
        Long failed = resultMap.get(RdosTaskStatus.FAILED.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.FAILED.getStatus());
        Long waitEngine = resultMap.get(RdosTaskStatus.WAITENGINE.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.WAITENGINE.getStatus());
        Long submitting = resultMap.get(RdosTaskStatus.SUBMITTING.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.SUBMITTING.getStatus());
        Long canceled = resultMap.get(RdosTaskStatus.CANCELED.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.CANCELED.getStatus());
        Long frozen = resultMap.get(RdosTaskStatus.FROZEN.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.FROZEN.getStatus());

        preViewVO.setFinishedJobSum(finished);
        preViewVO.setAllJobSum(unSubmit + running + finished + failed + waitEngine + submitting + canceled + frozen);
        preViewVO.setDoneJobSum(failed + canceled + frozen + finished);
    }

    /**
     * @param fillJobName
     * @return
     */
    @Deprecated
    public PageResult<ScheduleFillDataJobDetailVO> getFillDataDetailInfoOld(QueryJobDTO vo,
                                                                             String fillJobName,
                                                                             Long dutyUserId) throws Exception {
        if (Strings.isNullOrEmpty(fillJobName)) {
            throw new RdosDefineException("(The supplementary data name cannot be empty)", ErrorCode.INVALID_PARAMETERS);
        }
        vo.setSplitFiledFlag(true);
        ScheduleJobDTO batchJobDTO = this.createQuery(vo);
        batchJobDTO.setJobNameRightLike(fillJobName + "-");

        this.setBizDay(batchJobDTO, vo.getBizStartDay(), vo.getBizEndDay(), vo.getTenantId(), vo.getProjectId());

        if (dutyUserId != null && dutyUserId > 0) {
            batchJobDTO.setTaskCreateId(dutyUserId);
        }

        if (!Strings.isNullOrEmpty(vo.getTaskName())) {
            batchJobDTO.setTaskNameLike(vo.getTaskName());
        }

        if (!Strings.isNullOrEmpty(vo.getJobStatuses())) {
            List<Integer> statues = new ArrayList<>();
            String[] statuses = vo.getJobStatuses().split(",");
            for (String status : statuses) {
                List<Integer> statusList = RdosTaskStatus.getStatusFailedDetail().get(MathUtil.getIntegerVal(status));
                statues.addAll(statusList);
            }

            batchJobDTO.setJobStatuses(statues);
        }
        batchJobDTO.setTaskTypes(convertStringToList(vo.getTaskType()));

        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(vo.getCurrentPage(), vo.getPageSize(), "business_date", Sort.ASC.name());
        pageQuery.setModel(batchJobDTO);

        // 先查找符合条件的子节点
        batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Only_Workflow_SubNodes.getType());
        batchJobDTO.setPageQuery(false);
        List<ScheduleJob> subScheduleJobs = scheduleJobDao.generalQuery(pageQuery);
        Set<String> matchFlowJobIds = subScheduleJobs.stream().map(ScheduleJob::getFlowJobId).collect(Collectors.toSet());
        List<String> subJobIds = subScheduleJobs.stream().map(ScheduleJob::getJobId).collect(Collectors.toList());

        // 然后查找符合条件的其它任务
        batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
        batchJobDTO.setJobIds(matchFlowJobIds);

        //todo 优化查询次数
        batchJobDTO.setPageQuery(true);

        ScheduleFillDataJobDetailVO scheduleFillDataJobDetailVO = new ScheduleFillDataJobDetailVO();
        scheduleFillDataJobDetailVO.setFillDataJobName(fillJobName);

        batchJobDTO.setNeedQuerySonNode(CollectionUtils.isNotEmpty(batchJobDTO.getTaskTypes()) && batchJobDTO.getTaskTypes().contains(EScheduleJobType.WORK_FLOW.getVal()));
        int totalCount = scheduleJobDao.generalCount(batchJobDTO);
        if (totalCount > 0) {
            List<ScheduleJob> scheduleJobs = scheduleJobDao.generalQuery(pageQuery);

            Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(scheduleJobs);


            for (ScheduleJob scheduleJob : scheduleJobs) {
                if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(scheduleJob.getStatus())) {
                    scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
                }
                scheduleFillDataJobDetailVO.addRecord(transferBatchJob2FillDataRecord(scheduleJob, null, taskShadeMap));
            }

            dealFlowWorkFillDataRecord(scheduleFillDataJobDetailVO.getRecordList(), subJobIds);
        } else if (subScheduleJobs.size() > 0) {
            batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Only_Workflow_SubNodes.getType());
            batchJobDTO.setPageQuery(true);
            batchJobDTO.setJobIds(null);
            batchJobDTO.setFlowJobId(null);
            //【2】 只查询工作流子节点
            totalCount = scheduleJobDao.generalCount(batchJobDTO);
            if (totalCount > 0) {
                subScheduleJobs = scheduleJobDao.generalQuery(pageQuery);

                Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(subScheduleJobs);
                for (ScheduleJob scheduleJob : subScheduleJobs) {
                    if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(scheduleJob.getStatus())) {
                        scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
                    }
                    scheduleFillDataJobDetailVO.addRecord(transferBatchJob2FillDataRecord(scheduleJob, null, taskShadeMap));
                }
            }
        }


        return new PageResult<>(scheduleFillDataJobDetailVO, totalCount, pageQuery);
    }

    public PageResult<ScheduleFillDataJobDetailVO> getJobGetFillDataDetailInfo(String taskName, Long bizStartDay,
                                                                               Long bizEndDay, List<String> flowJobIdList,
                                                                               String fillJobName, Long dutyUserId,
                                                                               String searchType, Integer appType,
                                                                               Long projectId,Long dtuicTenantId,
                                                                               String execTimeSort, String execStartSort,
                                                                               String execEndSort, String cycSort,
                                                                               String businessDateSort, String retryNumSort,
                                                                               String taskType, String jobStatuses,
                                                                               Integer currentPage, Integer pageSize) throws Exception {
        QueryJobDTO vo = new QueryJobDTO();
        vo.setCurrentPage(currentPage);
        vo.setPageSize(pageSize);
        vo.setBizStartDay(bizStartDay);
        vo.setBizEndDay(bizEndDay);
        vo.setFillTaskName(fillJobName);
        vo.setSearchType(searchType);
        vo.setProjectId(projectId);
        vo.setDtuicTenantId(dtuicTenantId);
        vo.setTaskName(taskName);
        vo.setSplitFiledFlag(true);
        vo.setExecTimeSort(execTimeSort);
        vo.setExecStartSort(execStartSort);
        vo.setExecEndSort(execEndSort);
        vo.setCycSort(cycSort);
        vo.setBusinessDateSort(businessDateSort);
        vo.setRetryNumSort(retryNumSort);
        vo.setJobStatuses(jobStatuses);
        vo.setTaskType(taskType);
        return getScheduleFillDataJobDetailVOPageResult(flowJobIdList, fillJobName, dutyUserId, searchType, appType, vo);
    }

    public PageResult<ScheduleFillDataJobDetailVO> getFillDataDetailInfo( String queryJobDTO,
                                                                          List<String> flowJobIdList,
                                                                          String fillJobName,
                                                                          Long dutyUserId,  String searchType,
                                                                          Integer appType) {
        if (Strings.isNullOrEmpty(fillJobName)) {
            throw new RdosDefineException("(The supplementary data name cannot be empty)", ErrorCode.INVALID_PARAMETERS);
        }

        QueryJobDTO vo = JSONObject.parseObject(queryJobDTO, QueryJobDTO.class);
        return getScheduleFillDataJobDetailVOPageResult(flowJobIdList, fillJobName, dutyUserId, searchType, appType, vo);
    }

    private PageResult<ScheduleFillDataJobDetailVO> getScheduleFillDataJobDetailVOPageResult(List<String> flowJobIdList, String fillJobName, Long dutyUserId, String searchType, Integer appType, QueryJobDTO vo) {
        vo.setSplitFiledFlag(true);
        ScheduleJobDTO batchJobDTO = this.createQuery(vo);
        batchJobDTO.setAppType(appType);
        batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
        batchJobDTO.setFillDataJobName(fillJobName);
        batchJobDTO.setNeedQuerySonNode(true);
        //跨租户、项目条件
        batchJobDTO.setTenantId(null);

        this.setBizDay(batchJobDTO, vo.getBizStartDay(), vo.getBizEndDay(), vo.getTenantId(), vo.getProjectId());

        if (dutyUserId != null && dutyUserId > 0) {
            batchJobDTO.setOwnerUserId(dutyUserId);
        }

        if (!Strings.isNullOrEmpty(vo.getTaskName())) {
            batchJobDTO.setTaskNameLike(vo.getTaskName());
        }

        if (!Strings.isNullOrEmpty(vo.getJobStatuses())) {
            List<Integer> statues = new ArrayList<>();
            String[] statuses = vo.getJobStatuses().split(",");
            for (String status : statuses) {
                List<Integer> statusList = RdosTaskStatus.getStatusFailedDetail().get(MathUtil.getIntegerVal(status));
                if (CollectionUtils.isNotEmpty(statusList)) {
                    statues.addAll(statusList);
                }else{
                    statues.add(MathUtil.getIntegerVal(status));
                }
            }

            batchJobDTO.setJobStatuses(statues);
        }
        batchJobDTO.setTaskTypes(convertStringToList(vo.getTaskType()));

        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(vo.getCurrentPage(), vo.getPageSize());
        setPageQueryDefaultOrder(pageQuery, batchJobDTO);

        changeSearchType(batchJobDTO, searchType);
        pageQuery.setModel(batchJobDTO);


        batchJobDTO.setPageQuery(true);
        //根据有无条件来判断是否只查询父节点
        if (StringUtils.isNotEmpty(batchJobDTO.getTaskNameLike()) ||
                CollectionUtils.isNotEmpty(batchJobDTO.getJobStatuses()) ||
                CollectionUtils.isNotEmpty(batchJobDTO.getTaskTypes())) {
            batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Full_Workflow_Job.getType());
        } else {
            batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
        }

        ScheduleFillDataJobDetailVO scheduleFillDataJobDetailVO = new ScheduleFillDataJobDetailVO();
        scheduleFillDataJobDetailVO.setFillDataJobName(fillJobName);

        if (StringUtils.isNotBlank(vo.getTaskName()) || Objects.nonNull(vo.getOwnerId())) {
            List<ScheduleTaskShade> batchTaskShades = scheduleTaskShadeDao.listByNameLikeWithSearchType(vo.getProjectId(), vo.getTaskName(),
                    appType, vo.getOwnerId(), vo.getProjectIds(), batchJobDTO.getSearchType());
            if (CollectionUtils.isNotEmpty(batchTaskShades)) {
                batchJobDTO.setTaskIds(batchTaskShades.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList()));
            } else {
                return new PageResult<>(scheduleFillDataJobDetailVO, 0, pageQuery);
            }
        }

        ScheduleFillDataJob byJobName = scheduleFillDataJobDao.getByJobName(batchJobDTO.getFillDataJobName(), null);

        if (byJobName != null) {
            batchJobDTO.setFillId(byJobName.getId());
        }

        Integer totalCount = scheduleJobDao.countByFillData(batchJobDTO);
        if (totalCount > 0) {

            List<ScheduleJob> scheduleJobListWithFillData = scheduleJobDao.queryFillData(pageQuery);

            Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(scheduleJobListWithFillData);
            if (CollectionUtils.isNotEmpty(scheduleJobListWithFillData)) {
                for (ScheduleJob job : scheduleJobListWithFillData) {
                    if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(job.getStatus())) {
                        job.setStatus(RdosTaskStatus.RUNNING.getStatus());
                    }
                    scheduleFillDataJobDetailVO.addRecord(transferBatchJob2FillDataRecord(job, flowJobIdList, taskShadeMap));
                }
                dealFlowWorkSubJobsInFillData(scheduleFillDataJobDetailVO.getRecordList());
            }
        }

        return new PageResult<>(scheduleFillDataJobDetailVO, totalCount, pageQuery);
    }

    /**
     * 获取补数据实例工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    public ScheduleFillDataJobDetailVO.FillDataRecord getRelatedJobsForFillData( String jobId,  String query,
                                                                                 String fillJobName) {

        QueryJobDTO vo = JSONObject.parseObject(query, QueryJobDTO.class);
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, Deleted.NORMAL.getStatus());

        Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(Arrays.asList(scheduleJob));

        ScheduleFillDataJobDetailVO.FillDataRecord fillDataRecord = transferBatchJob2FillDataRecord(scheduleJob, null, taskShadeMap);

        vo.setSplitFiledFlag(true);
        //除去任务类型中的工作流类型的条件，用于展示下游节点
        if (StringUtils.isNotBlank(vo.getTaskType())) {
            vo.setTaskType(vo.getTaskType().replace(String.valueOf(EScheduleJobType.WORK_FLOW.getVal()), ""));
        }
        ScheduleJobDTO batchJobDTO = this.createQuery(vo);
        batchJobDTO.setFillDataJobName(fillJobName);

        if (scheduleJob != null) {
            if (EScheduleJobType.WORK_FLOW.getVal().intValue() == fillDataRecord.getTaskType().intValue()) {
                fillDataRecord.setRelatedRecords(getRelatedJobsForFillDataByQueryDTO(batchJobDTO, vo, jobId, taskShadeMap));
            }
            return fillDataRecord;
        } else {
            throw new RdosDefineException("The instance object does not exist");
        }
    }

    /**
     * 获取指定工作流子节点（不包括工作流父节点）
     *
     * @param jobId        没有判断该job是否是工作流
     * @param taskShadeMap
     * @return
     * @throws Exception
     */
    private List<ScheduleFillDataJobDetailVO.FillDataRecord> getOnlyRelatedJobsForFillData(String jobId, Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap) {
        taskShadeMap = Optional.ofNullable(taskShadeMap).orElse(Maps.newHashMap());

        List<ScheduleJob> subJobs = scheduleJobDao.getSubJobsByFlowIds(Lists.newArrayList(jobId));
        taskShadeMap.putAll(this.prepare(subJobs));
        List<ScheduleFillDataJobDetailVO.FillDataRecord> fillDataRecord_subNodes = new ArrayList<>();
        for (ScheduleJob subJob : subJobs) {
            ScheduleFillDataJobDetailVO.FillDataRecord subNode = transferBatchJob2FillDataRecord(subJob, null, taskShadeMap);
            fillDataRecord_subNodes.add(subNode);
        }
        return fillDataRecord_subNodes;
    }

    private List<ScheduleFillDataJobDetailVO.FillDataRecord> getRelatedJobsForFillDataByQueryDTO(ScheduleJobDTO queryDTO, QueryJobDTO vo, String jobId,
                                                                                                 Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap) {

        queryDTO.setPageQuery(false);
        queryDTO.setFlowJobId(jobId);
        queryDTO.setNeedQuerySonNode(true);
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(vo.getCurrentPage(), vo.getPageSize(), "gmt_modified", Sort.DESC.name());
        pageQuery.setModel(queryDTO);
        List<ScheduleJob> subJobs = scheduleJobDao.generalQuery(pageQuery);
        taskShadeMap = this.prepareForFillDataDetailInfo(subJobs);

        List<ScheduleFillDataJobDetailVO.FillDataRecord> fillDataRecord_subNodes = new ArrayList<>();
        for (ScheduleJob subJob : subJobs) {
            ScheduleFillDataJobDetailVO.FillDataRecord subNode = transferBatchJob2FillDataRecord(subJob, null, taskShadeMap);
            fillDataRecord_subNodes.add(subNode);
        }
        return fillDataRecord_subNodes;
    }


    /**
     * 添加默认排序
     * 若查询条件中没有执行时间、执行开始时间、执行结束时间、计划时间、业务时间
     * 则默认添加业务时间ASC
     *
     * @param pageQuery
     * @param batchJobDTO
     */
    private void setPageQueryDefaultOrder(PageQuery pageQuery, ScheduleJobDTO batchJobDTO) {
        if (StringUtils.isBlank(batchJobDTO.getExecTimeSort()) &&
                StringUtils.isBlank(batchJobDTO.getExecStartSort()) &&
                StringUtils.isBlank(batchJobDTO.getExecEndSort()) &&
                StringUtils.isBlank(batchJobDTO.getCycSort()) &&
                StringUtils.isBlank(batchJobDTO.getBusinessDateSort())) {
            pageQuery.setOrderBy(BUSINESS_DATE);
            pageQuery.setSort(Sort.ASC.name());
        }
    }


    private void dealFlowWorkSubJobsInFillData(List<ScheduleFillDataJobDetailVO.FillDataRecord> vos) {
        Map<String, ScheduleFillDataJobDetailVO.FillDataRecord> record = Maps.newHashMap();
        Map<String, Integer> voIndex = Maps.newHashMap();
        vos.forEach(job -> voIndex.put(job.getJobId(), vos.indexOf(job)));
        List<ScheduleFillDataJobDetailVO.FillDataRecord> copy = Lists.newArrayList(vos);
        Iterator<ScheduleFillDataJobDetailVO.FillDataRecord> iterator = vos.iterator();
        while (iterator.hasNext()) {
            ScheduleFillDataJobDetailVO.FillDataRecord jobVO = iterator.next();
            String flowJobId = jobVO.getFlowJobId();
            if (!"0".equals(flowJobId)) {
                if (record.containsKey(flowJobId)) {
                    ScheduleFillDataJobDetailVO.FillDataRecord flowVo = record.get(flowJobId);
                    flowVo.getRelatedRecords().add(jobVO);
                    iterator.remove();
                } else {
                    ScheduleFillDataJobDetailVO.FillDataRecord flowVO;
                    if (voIndex.containsKey(flowJobId)) {
                        flowVO = copy.get(voIndex.get(flowJobId));
                        flowVO.setRelatedRecords(Lists.newArrayList(jobVO));
                        iterator.remove();
                    } else {
                        ScheduleJob flow = scheduleJobDao.getByJobId(flowJobId, Deleted.NORMAL.getStatus());
                        if (flow == null) {
                            continue;
                        }
                        Map<Long, ScheduleTaskForFillDataDTO> shadeMap = this.prepare(Lists.newArrayList(flow));
                        flowVO = transferBatchJob2FillDataRecord(flow, null, shadeMap);
                        flowVO.setRelatedRecords(Lists.newArrayList(jobVO));
                        vos.set(vos.indexOf(jobVO), flowVO);
                    }
                    record.put(flowJobId, flowVO);
                }
            }
        }
    }

    private Map<Long, ScheduleTaskForFillDataDTO> prepareForFillDataDetailInfo(List<ScheduleJob> scheduleJobs) {
        if (CollectionUtils.isEmpty(scheduleJobs)) {
            return new HashMap<>();
        }
        Set<Long> taskIdSet = scheduleJobs.stream().map(ScheduleJob::getTaskId).collect(Collectors.toSet());
        Integer appType = scheduleJobs.get(0).getAppType();
        List<ScheduleTaskForFillDataDTO> scheduleTaskForFillDataDTOS = scheduleTaskShadeDao.listSimpleTaskByTaskIds(taskIdSet, null, appType);
        return scheduleTaskForFillDataDTOS.stream().collect(Collectors.toMap(ScheduleTaskForFillDataDTO::getTaskId, scheduleTaskForFillDataDTO -> scheduleTaskForFillDataDTO));

    }

    private void dealFlowWorkFillDataRecord
            (List<ScheduleFillDataJobDetailVO.FillDataRecord> records, List<String> subJobIds) throws Exception {
        if (CollectionUtils.isNotEmpty(records)) {
            List<ScheduleJob> allSubJobs = new ArrayList<>();
            Iterator<ScheduleFillDataJobDetailVO.FillDataRecord> it = records.iterator();
            while (it.hasNext()) {
                ScheduleFillDataJobDetailVO.FillDataRecord record = it.next();
                Integer type = record.getTaskType();
                if (EScheduleJobType.WORK_FLOW.getVal().intValue() == type) {
                    String jobId = record.getJobId();
                    List<ScheduleJob> subJobs = scheduleJobDao.getSubJobsByFlowIds(Lists.newArrayList(jobId));
                    allSubJobs.addAll(subJobs);
                    if (CollectionUtils.isNotEmpty(subJobs)) {
                        Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(subJobs);
                        List<ScheduleFillDataJobDetailVO.FillDataRecord> subList = Lists.newArrayList();
                        for (ScheduleJob subJob : subJobs) {
                            if (subJobIds.contains(subJob.getJobId())) {
                                subList.add(transferBatchJob2FillDataRecord(subJob, null, taskShadeMap));
                            }
                        }
                        record.setRelatedRecords(subList);
                    }
                }
            }

            // 这里处理工作流里的任务
            Iterator<ScheduleFillDataJobDetailVO.FillDataRecord> itInternal = records.iterator();
            for (ScheduleJob subJob : allSubJobs) {
                while (itInternal.hasNext()) {
                    ScheduleFillDataJobDetailVO.FillDataRecord rec = itInternal.next();
                    if (subJob.getJobId().equalsIgnoreCase(rec.getJobId())) {
                        itInternal.remove();
                        break;
                    }
                }
            }
        }
    }


    /**
     * 转化batchjob任务为补数据界面所需格式
     *
     * @param scheduleJob
     * @param flowJobIdList 展开特定工作流子节点
     * @param taskShadeMap
     * @return
     * @throws Exception
     */
    private ScheduleFillDataJobDetailVO.FillDataRecord transferBatchJob2FillDataRecord(ScheduleJob scheduleJob, List<String> flowJobIdList,
                                                                                       Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap) {
        String bizDayVO = scheduleJob.getBusinessDate();
        bizDayVO = bizDayVO.substring(0, 4) + "-" + bizDayVO.substring(4, 6) + "-" + bizDayVO.substring(6, 8);
        int status = scheduleJob.getStatus();

        String cycTimeVO = DateUtil.addTimeSplit(scheduleJob.getCycTime());
        String exeStartTimeVO = null;
        Timestamp exeStartTime = scheduleJob.getExecStartTime();
        if (exeStartTime != null) {
            exeStartTimeVO = timeFormatter.print(exeStartTime.getTime());
        }

        ScheduleTaskForFillDataDTO taskShade = taskShadeMap.get(scheduleJob.getTaskId());
        Integer taskType = 0;
        String taskName = "";
        if (taskShade != null) {
            taskType = taskShade.getTaskType();
            taskName = taskShade.getName();
        }
        ScheduleTaskVO batchTaskVO = new ScheduleTaskVO();
        String exeTime = DateUtil.getTimeDifference(scheduleJob.getExecTime() == null ? 0L : scheduleJob.getExecTime() * 1000);

        try {
            if (scheduleJob.getExecTime() == null || scheduleJob.getExecTime() == 0) {
                exeTime = DateUtil.getTimeDifference(scheduleJob.getExecStartTime() == null ? 0L : (System.currentTimeMillis() - scheduleJob.getExecStartTime().getTime()));
            }
        } catch (Exception e) {
            exeTime =  DateUtil.getTimeDifference(0L);
        }

        Integer showStatus = RdosTaskStatus.getShowStatusWithoutStop(status);
        ScheduleFillDataJobDetailVO.FillDataRecord record = new ScheduleFillDataJobDetailVO.FillDataRecord(scheduleJob.getId(), bizDayVO, taskName,
                taskType, showStatus, cycTimeVO, exeStartTimeVO, exeTime, null);

        record.setJobId(scheduleJob.getJobId());
        record.setFlowJobId(scheduleJob.getFlowJobId());
        record.setIsRestart(scheduleJob.getIsRestart());
        record.setBusinessType(scheduleJob.getBusinessType());

        //展开特定工作流子节点
        if (EScheduleJobType.WORK_FLOW.getVal().equals(taskType) &&
                CollectionUtils.isNotEmpty(flowJobIdList) &&
                flowJobIdList.contains(scheduleJob.getJobId())) {
            record.setRelatedRecords(getOnlyRelatedJobsForFillData(scheduleJob.getJobId(), taskShadeMap));
        }

        if (null != taskShade) {
            batchTaskVO.setId(taskShade.getTaskId());
            batchTaskVO.setGmtModified(taskShade.getGmtModified());
            batchTaskVO.setName(taskShade.getName());
            batchTaskVO.setIsDeleted(taskShade.getIsDeleted());
            batchTaskVO.setOwnerUserId(taskShade.getOwnerUserId());
            batchTaskVO.setCreateUserId(taskShade.getCreateUserId());
            batchTaskVO.setCreateUser(taskShade.getCreateUser());
            batchTaskVO.setOwnerUser(taskShade.getOwnerUser());
        }

        record.setBatchTask(batchTaskVO);
        record.setRetryNum(scheduleJob.getRetryNum());
        return record;
    }


    /**
     * 获取重跑的数据节点信息
     *
     * @return
     */
    public List<RestartJobVO> getRestartChildJob( String jobKey,  Long parentTaskId,  boolean isOnlyNextChild) {

        List<ScheduleJobJob> scheduleJobJobList = batchJobJobService.getJobChild(jobKey);
        List<String> jobKeyList = Lists.newArrayList();
        List<RestartJobVO> batchJobList = Lists.newArrayList();

        String parentJobDayStr = getJobTriggerTimeFromJobKey(jobKey);
        if (Strings.isNullOrEmpty(parentJobDayStr)
                || CollectionUtils.isEmpty(scheduleJobJobList)) {
            return batchJobList;
        }

        for (ScheduleJobJob scheduleJobJob : scheduleJobJobList) {
            //排除自依赖
            String childJobKey = scheduleJobJob.getJobKey();
            Long taskShadeIdFromJobKey = getTaskShadeIdFromJobKey(childJobKey);
            ScheduleTaskShade taskShade = batchTaskShadeService.getById(taskShadeIdFromJobKey);
            if(null != taskShade && taskShade.getTaskId().equals(parentTaskId)){
                continue;
            }

            //排除不是同一天执行的
            if (!parentJobDayStr.equals(getJobTriggerTimeFromJobKey(childJobKey))) {
                continue;
            }

            jobKeyList.add(scheduleJobJob.getJobKey());
        }

        if (CollectionUtils.isEmpty(jobKeyList)) {
            return batchJobList;
        }

        List<ScheduleJob> jobList = scheduleJobDao.listJobByJobKeys(jobKeyList);
        if(CollectionUtils.isNotEmpty(jobList)) {
            for (ScheduleJob childScheduleJob : jobList) {

                //判断job 对应的task是否被删除
                ScheduleTaskShade jobRefTask = batchTaskShadeService.getBatchTaskById(childScheduleJob.getTaskId());
                Integer jobStatus = childScheduleJob.getStatus();
                jobStatus = jobStatus == null ? RdosTaskStatus.UNSUBMIT.getStatus() : jobStatus;

                String taskName = jobRefTask == null ? null : jobRefTask.getName();

                RestartJobVO restartJobVO = new RestartJobVO();
                restartJobVO.setJobId(childScheduleJob.getId());
                restartJobVO.setJobKey(childScheduleJob.getJobKey());
                restartJobVO.setJobStatus(jobStatus);
                restartJobVO.setCycTime(childScheduleJob.getCycTime());
                restartJobVO.setTaskType(childScheduleJob.getTaskType());
                restartJobVO.setTaskName(taskName);
                restartJobVO.setTaskId(childScheduleJob.getTaskId());
                //如果不只是查下一个子结点，则递归查询。
                if (!isOnlyNextChild) {
                    restartJobVO.setChilds(getRestartChildJob(childScheduleJob.getJobKey(), childScheduleJob.getTaskId(), isOnlyNextChild));
                }

                batchJobList.add(restartJobVO);
            }
        }

        return batchJobList;
    }


    /**
     * 此处获取的时候schedule_task_shade 的id 不是task_id
     * @param jobKey
     * @return
     */
    public Long getTaskShadeIdFromJobKey(String jobKey) {
        String[] strings = jobKey.split("_");
        if (strings.length < 2) {
            LOGGER.error("it's not a legal job key, str is {}.", jobKey);
            return -1L;
        }

        String id = strings[strings.length - 2];
        try {
            return MathUtil.getLongVal(id);
        } catch (Exception e) {
            LOGGER.error("it's not a legal job key, str is {}.", jobKey);
            return -1L;
        }
    }

    public String getJobTriggerTimeFromJobKey(String jobKey) {
        String[] strings = jobKey.split("_");
        if (strings.length < 1) {
            LOGGER.error("it's not a legal job key, str is {}.", jobKey);
            return "";
        }

        String timeStr = strings[strings.length - 1];
        if (timeStr.length() < 8) {
            LOGGER.error("it's not a legal job key, str is {}.", jobKey);
            return "";
        }

        return timeStr.substring(0, 8);
    }


    private boolean checkJobCanStop(Integer status) {
        if (status == null) {
            return true;
        }
        return RdosTaskStatus.getCanStopStatus().contains(status);
    }

    public String formatLearnTaskParams(String taskParams) {
        List<String> params = new ArrayList<>();

        for (String param : taskParams.split("\r|\n")) {
            if (StringUtils.isNotEmpty(param.trim()) && !param.trim().startsWith("#")) {
                String[] parts = param.split("=");
                params.add(String.format("%s=%s", parts[0].trim(), parts[1].trim()));
            }
        }

        return StringUtils.join(params, " ");
    }



    /**
     * 根据工作流id获取子任务信息与任务状态
     *
     * @param jobId
     * @return
     */
    public List<ScheduleJob> getSubJobsAndStatusByFlowId(String jobId) {
        return scheduleJobDao.getSubJobsAndStatusByFlowId(jobId);
    }



    public List<String> listJobIdByTaskNameAndStatusList( String taskName,  List<Integer> statusList,  Long projectId, Integer appType) {
        ScheduleTaskShade task = batchTaskShadeService.getByName(projectId, taskName,appType,null);
        if (task != null) {
            return scheduleJobDao.listJobIdByTaskIdAndStatus(task.getTaskId(), null ,statusList);
        }
        return new ArrayList<>();
    }


    /**
     * 返回这些jobId对应的父节点的jobMap
     *
     * @param jobIdList
     * @param projectId
     * @return
     */
    public Map<String, ScheduleJob> getLabTaskRelationMap( List<String> jobIdList,  Long projectId) {

        if(CollectionUtils.isEmpty(jobIdList)){
            return Collections.EMPTY_MAP;
        }
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listByJobIdList(jobIdList, projectId);
        if (CollectionUtils.isNotEmpty(scheduleJobs)) {
            Map<String, ScheduleJob> jobMap = new HashMap<>();
            for (ScheduleJob scheduleJob : scheduleJobs) {
                ScheduleJob flowJob = scheduleJobDao.getByJobId(scheduleJob.getFlowJobId(), Deleted.NORMAL.getStatus());
                jobMap.put(scheduleJob.getJobId(), flowJob);
            }
            return jobMap;
        }
        return new HashMap<>();
    }

    /**
     * 获取任务执行信息
     *
     * @param taskId
     * @param appType
     * @param projectId
     * @param count
     * @return
     */
    public List<Map<String, Object>> statisticsTaskRecentInfo( Long taskId,  Integer appType,  Long projectId,  Integer count) {

        return scheduleJobDao.listTaskExeInfo(taskId, projectId, count, appType);

    }


    /**
     * 批量更新
     *
     * @param jobs
     */
    public Integer BatchJobsBatchUpdate( String jobs) {
        if (StringUtils.isBlank(jobs)) {
            return 0;
        }
        List<ScheduleJob> scheduleJobs = JSONObject.parseArray(jobs, ScheduleJob.class);
        if (CollectionUtils.isEmpty(scheduleJobs)) {
            return 0;
        }
        Integer updateSize = 0;
        for (ScheduleJob job : scheduleJobs) {
            if (null != job.getStatus()) {
                //更新状态 日志信息也要更新
                job.setLogInfo("");
            }

            if (RdosTaskStatus.UNSUBMIT.getStatus().equals(job.getStatus())) {
                job.setPhaseStatus(JobPhaseStatus.CREATE.getCode());
            }

            updateSize += scheduleJobDao.update(job);

        }
        return updateSize;
    }

    /**
     * 把开始时间和结束时间置为null
     *
     * @param jobId
     * @return
     */
    public Integer updateTimeNull( String jobId){

        return scheduleJobDao.updateNullTime(jobId);
    }


    public ScheduleJob getById( Long id) {

        return scheduleJobDao.getOne(id);
    }

    public ScheduleJob getByJobId( String jobId,  Integer isDeleted) {
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, isDeleted);

        if (scheduleJob != null && StringUtils.isBlank(scheduleJob.getSubmitUserName())) {
            scheduleJob.setSubmitUserName(environmentContext.getHadoopUserName());
        }

        return scheduleJob;
    }

    public Integer getJobStatus(String jobId){
        Integer status = scheduleJobDao.getStatusByJobId(jobId);
        if (Objects.isNull(status)) {
            throw new RdosDefineException("job not exist");
        }
        return status;
    }
    public List<ScheduleJob> getByIds( List<Long> ids) {

        if(CollectionUtils.isEmpty(ids)){
            return Collections.EMPTY_LIST;
        }
        return scheduleJobDao.listByJobIds(ids);
    }


    /**
     * 离线调用
     *
     * @param batchJob
     * @param isOnlyNextChild
     * @param appType
     * @return
     */
    public List<ScheduleJob> getSameDayChildJob( String batchJob,
                                                 boolean isOnlyNextChild,  Integer appType) {
        ScheduleJob job = JSONObject.parseObject(batchJob, ScheduleJob.class);
        if (null == job) {
            return new ArrayList<>();
        }
        Integer jobLevel = environmentContext.getJobJobLevel();
        return this.getAllChildJobWithSameDay(job, isOnlyNextChild, appType,jobLevel);
    }

    /**
     * FIXME 注意不要出现死循环
     * 查询出指定job的所有关联的子job
     * 限定同一天并且不是自依赖
     *
     * @param scheduleJob
     * @param level 层数，防止循环依赖一直递归
     * @return
     */
    public List<ScheduleJob> getAllChildJobWithSameDay(ScheduleJob scheduleJob,
                                                        boolean isOnlyNextChild,  Integer appType,int level) {

        if(level<=0){
            //最多执行10层，死循环时跳出
            return Lists.newArrayList();
        }
        String jobKey = scheduleJob.getJobKey();
        //查询子工作任务
        List<ScheduleJobJob> scheduleJobJobList = batchJobJobService.getJobChild(jobKey);
        List<String> jobKeyList = Lists.newArrayList();
        //从jobKey获取父任务的触发时间
        String parentJobDayStr = getJobTriggerTimeFromJobKey(jobKey);
        if (Strings.isNullOrEmpty(parentJobDayStr)) {
            return Lists.newArrayList();
        }
        //获取满足条件的jobKeyList
        filterJobKeyList(scheduleJob, appType, scheduleJobJobList, jobKeyList, parentJobDayStr);
        if (CollectionUtils.isEmpty(jobKeyList)) {
            return Lists.newArrayList();
        }
        List<ScheduleJob> scheduleJobList = Lists.newArrayList();
        List<ScheduleJob> listJobs = scheduleJobDao.listJobByJobKeys(jobKeyList);
        for (ScheduleJob childScheduleJob : listJobs) {
            //判断job 对应的task是否被删除
            ScheduleTaskShade jobRefTask = batchTaskShadeService.getBatchTaskById(childScheduleJob.getTaskId());
            if (jobRefTask == null || Deleted.DELETED.getStatus().equals(jobRefTask.getIsDeleted())) {
                continue;
            }
            scheduleJobList.add(childScheduleJob);
            if (isOnlyNextChild) {
                continue;
            }
            scheduleJobList.addAll(getAllChildJobWithSameDay(childScheduleJob, isOnlyNextChild, appType, level-1));
            LOGGER.info("count info --- scheduleJob jobKey:{} flowJobId:{} jobJobList size:{}", scheduleJob.getJobKey(), scheduleJob.getFlowJobId(),scheduleJobList.size());
        }
        return scheduleJobList;

    }

    private void filterJobKeyList(ScheduleJob scheduleJob, Integer appType, List<ScheduleJobJob> scheduleJobJobList, List<String> jobKeyList, String parentJobDayStr) {
        for (ScheduleJobJob scheduleJobJob : scheduleJobJobList) {
            //排除自依赖
            String childJobKey = scheduleJobJob.getJobKey();
            ScheduleTaskShade taskShade = batchTaskShadeService.getBatchTaskById(scheduleJob.getTaskId());
            if (null != taskShade && taskShade.getId().equals(getTaskShadeIdFromJobKey(childJobKey))) {
                continue;
            }
            //排除不是同一天执行的
            if (!parentJobDayStr.equals(getJobTriggerTimeFromJobKey(childJobKey))) {
                continue;
            }
            jobKeyList.add(scheduleJobJob.getJobKey());
        }
    }


    public Integer generalCount(ScheduleJobDTO query) {
        query.setPageQuery(false);
        return scheduleJobDao.generalCount(query);
    }


    public List<ScheduleJob> generalQuery(PageQuery query) {
        return scheduleJobDao.generalQuery(query);
    }


    /**
     * 获取job最后一次执行
     *
     * @param taskId
     * @param time
     * @return
     */
    public ScheduleJob getLastSuccessJob(Long taskId, Timestamp time, Integer appType) {
        if (null == taskId || null == time || null == appType) {
            return null;
        }
        return scheduleJobDao.getByTaskIdAndStatusOrderByIdLimit(taskId, RdosTaskStatus.FINISHED.getStatus(), time, appType);
    }



    /**
     * @author newman
     * @Description 设置虚结点的日志信息
     * @Date 2020-12-21 16:49
     * @param subNodeDownloadLog:
     * @param subTaskLogInfo:
     * @param subJob:
     * @param subTaskShade:
     * @return: void
     **/
    private void setVirtualLog(Map<String, String> subNodeDownloadLog, StringBuilder subTaskLogInfo, ScheduleJob subJob, ScheduleTaskShade subTaskShade) {
        if (EScheduleJobType.VIRTUAL.getType().intValue() != subTaskShade.getTaskType()) {
            subNodeDownloadLog.put(subTaskShade.getName(), String.format(DOWNLOAD_LOG, subJob.getJobId(), subTaskShade.getTaskType()));
            ActionLogVO logInfoFromEngine = this.getLogInfoFromEngine(subJob.getJobId());
            if (null != logInfoFromEngine ) {
                subTaskLogInfo.append(subTaskShade.getName()).
                        append("\n====================\n").
                        append(logInfoFromEngine.getLogInfo()).
                        append("\n====================\n").
                        append(logInfoFromEngine.getEngineLog()).
                        append("\n");
            }
        }
    }


    /**
     * 获取日志
     *
     * @return
     */
    public ActionLogVO getLogInfoFromEngine(String jobId) {
        return actionService.log(jobId, ComputeType.BATCH.getType());
    }



    /**
     * 更新任务状态和日志
     *
     * @param jobId
     * @param status
     * @param logInfo
     */
    public void updateJobStatusAndLogInfo( String jobId,  Integer status,  String logInfo) {

        scheduleJobDao.updateStatusByJobId(jobId, status, logInfo,null,null,null);
    }


    /**
     * 测试任务 是否可以运行
     *
     * @param jobId
     * @return
     */
    public String testCheckCanRun(String jobId){
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, Deleted.NORMAL.getStatus());
        if (null == scheduleJob) {
            return "任务不存在";
        }

        ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(scheduleJob);
        List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByJobKey(scheduleJob.getJobKey());
        scheduleBatchJob.setJobJobList(scheduleJobJobs);
        ScheduleTaskShade batchTaskById = batchTaskShadeService.getBatchTaskById(scheduleJob.getTaskId());
        if (batchTaskById == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        try {
            JobCheckRunInfo jobCheckRunInfo = jobRichOperator.checkJobCanRun(scheduleBatchJob, scheduleJob.getStatus(), scheduleJob.getType(),batchTaskById);
            return JSONObject.toJSONString(jobCheckRunInfo);
        } catch (Exception e) {
            LOGGER.error("ScheduleJobService.testCheckCanRun error:", e);
        }
        return "";
    }

    /**
     * 生成当天单个任务实例
     *
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void createTodayTaskShade( Long taskId, Integer appType,String date) {
        try {
            //如果appType为空的话则为离线
            if (null == appType) {
                throw new RdosDefineException("appType不能为空");
            }
            ScheduleTaskShade testTask = batchTaskShadeService.getBatchTaskById(taskId);
            if (null == testTask) {
                throw new RdosDefineException("任务不存在");
            }
            List<ScheduleTaskShade> taskShades = new ArrayList<>();
            taskShades.add(testTask);
            if (SPECIAL_TASK_TYPES.contains(testTask.getTaskType())) {
                //工作流算法实验 需要将子节点查询出来运行
                List<ScheduleTaskShade> flowWorkSubTasks = batchTaskShadeService.getFlowWorkSubTasks(testTask.getTaskId(),
                        null, null);
                if (CollectionUtils.isNotEmpty(flowWorkSubTasks)) {
                    taskShades.addAll(flowWorkSubTasks);
                }
            }
            if(StringUtils.isBlank(date)){
                date = new DateTime().toString("yyyy-MM-dd");
            }
            Map<String, String> flowJobId = new ConcurrentHashMap<>();
            List<ScheduleBatchJob> allJobs = new ArrayList<>();
            AtomicInteger count = new AtomicInteger();
            for (ScheduleTaskShade task : taskShades) {
                try {
                    List<ScheduleBatchJob> cronTrigger = jobGraphBuilder.buildJobRunBean(task, "cronTrigger", EScheduleType.NORMAL_SCHEDULE,
                            true, true, date, "cronJob" + "_" + task.getName(),
                            null, null, null,task.getTenantId(),count);
                    allJobs.addAll(cronTrigger);
                    if (SPECIAL_TASK_TYPES.contains(task.getTaskType())) {
                        //工作流或算法实验
                        for (ScheduleBatchJob jobRunBean : cronTrigger) {
                            flowJobId.put(JobGraphUtils.buildFlowReplaceId(task.getTaskId(),jobRunBean.getCycTime(),null),jobRunBean.getJobId());
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("生成当天单个任务实例异常,taskId:{}",task.getTaskId(), e);
                }
            }

            for (ScheduleBatchJob job : allJobs) {
                String flowIdKey = job.getScheduleJob().getFlowJobId();
                job.getScheduleJob().setFlowJobId(flowJobId.getOrDefault(flowIdKey, "0"));
            }
            sortAllJobs(allJobs);

            //需要保存BatchJob, BatchJobJob
            this.insertJobList(allJobs, EScheduleType.NORMAL_SCHEDULE.getType());

        } catch (Exception e) {
            LOGGER.error("createTodayTaskShadeForTest", e);
            throw new RdosDefineException("任务创建失败");
        }
    }

    /**
     * @author newman
     * @Description 对任务列表按照执行时间排序
     * @Date 2020-12-21 16:55
     * @param allJobs:
     * @return: void
     **/
    private void sortAllJobs(List<ScheduleBatchJob> allJobs) {
        allJobs.sort((ebj1, ebj2) -> {
            Long date1 = Long.valueOf(ebj1.getCycTime());
            Long date2 = Long.valueOf(ebj2.getCycTime());
            if (date1 < date2) {
                return -1;
            } else if (date1 > date2) {
                return 1;
            }
            return 0;
        });
    }

    public List<ScheduleJob> listByBusinessDateAndPeriodTypeAndStatusList(ScheduleJobDTO query) {
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(query);
        pageQuery.setModel(query);
        query.setPageQuery(false);
        return scheduleJobDao.listByBusinessDateAndPeriodTypeAndStatusList(pageQuery);
    }

    /**
     * 根据cycTime和jobName获取，如获取当天的周期实例任务
     *
     * @param preCycTime
     * @param preJobName
     * @param scheduleType
     * @return
     */
    public List<ScheduleJob> listByCyctimeAndJobName( String preCycTime,  String preJobName,  Integer scheduleType) {

        return scheduleJobDao.listJobByCyctimeAndJobName(preCycTime, preJobName, scheduleType);
    }

    /**
     * 按批次根据cycTime和jobName获取，如获取当天的周期实例任务
     *
     * @param startId
     * @param preCycTime
     * @param preJobName
     * @param scheduleType
     * @param batchJobSize
     * @return
     */
    public List<ScheduleJob> listByCyctimeAndJobName( Long startId,  String preCycTime,  String preJobName,  Integer scheduleType,  Integer batchJobSize) {

        return scheduleJobDao.listJobByCyctimeAndJobNameBatch(startId, preCycTime, preJobName, scheduleType, batchJobSize);
    }

    public Integer countByCyctimeAndJobName( String preCycTime,  String preJobName,  Integer scheduleType) {
        return scheduleJobDao.countJobByCyctimeAndJobName(preCycTime, preJobName, scheduleType);
    }

    /**
     * 根据jobKey删除job jobjob记录
     *
     * @param jobKeyList
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobsByJobKey( List<String> jobKeyList) {

        if(CollectionUtils.isEmpty(jobKeyList)){
            return;
        }
        scheduleJobDao.deleteByJobKey(jobKeyList);
        scheduleJobJobDao.deleteByJobKey(jobKeyList);
    }


    /**
     * 分批获取batchJob中数据
     * schedule   接口
     * * @param dto
     *
     * @return
     */

    public List<ScheduleJob> syncBatchJob(QueryJobDTO dto) {
        if (null == dto || null == dto.getAppType()) {
            return new ArrayList<>();
        }
        if (null == dto.getCurrentPage()) {
            dto.setCurrentPage(1);
        }
        if (null == dto.getPageSize()) {
            dto.setPageSize(50);
        }
        if (null != dto.getProjectId() && -1L == dto.getProjectId()) {
            // 不采用默认值
            dto.setProjectId(null);
        }
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(dto.getCurrentPage(), dto.getPageSize(), "gmt_modified", Sort.DESC.name());
        ScheduleJobDTO query = this.createQuery(dto);
        pageQuery.setModel(query);
        return scheduleJobDao.syncQueryJob(pageQuery);
    }

    /**
     *
     * 根据taskId、appType 拿到对应的job集合
     * @param taskIds
     * @param appType
     */

    public List<ScheduleJob> listJobsByTaskIdsAndApptype( List<Long> taskIds, Integer appType){

        if(CollectionUtils.isEmpty(taskIds)){
            return Collections.EMPTY_LIST;
        }
        return scheduleJobDao.listJobsByTaskIdAndApptype(taskIds,appType);
    }


    /**
     * 生成指定日期的周期实例(需要数据库无对应记录)
     *
     * @param triggerDay
     */
    public void buildTaskJobGraphTest( String triggerDay) {
        CompletableFuture.runAsync(() -> jobGraphBuilder.buildTaskJobGraph(triggerDay));
    }

    public boolean updatePhaseStatusById(Long id, JobPhaseStatus original, JobPhaseStatus update) {
        if (id==null|| original==null|| update==null) {
            return Boolean.FALSE;
        }

        Integer integer = scheduleJobDao.updatePhaseStatusById(id, original.getCode(), update.getCode());

        if (integer != null && !integer.equals(0)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Long getListMinId(String left, String right) {
        // 如果没有时间限制, 默认返回0
        if (StringUtils.isAnyBlank(left,right)){
            return 0L;
        }
        // 如果当前时间范围没有数据, 返回NULL
        String minJobId = jobGraphTriggerDao.getMinJobIdByTriggerTime(left, right);
        if (StringUtils.isBlank(minJobId)){
            return 0L;
        }
        return Long.parseLong(minJobId);
    }

    public String getJobGraphJSON(String jobId) {
        String jobExtraInfo = scheduleJobDao.getJobExtraInfo(jobId);
        JSONObject jobExtraObj = JSONObject.parseObject(jobExtraInfo);
        if (null != jobExtraObj) {
            return jobExtraObj.getString(JobResultConstant.JOB_GRAPH);
        }
        return "";
    }



    public void updateStatusByJobIdEqualsStatus(String jobId, Integer status, Integer status1) {
        scheduleJobDao.updateStatusByJobIdEqualsStatus(jobId,status,status1);
    }



    private ScheduleJobBeanVO buildScheduleJobBeanVO(ScheduleJob job) {
        ScheduleJobBeanVO vo = new ScheduleJobBeanVO();
        if (job != null) {
            BeanUtils.copyProperties(job, vo);
        }
        return vo;
    }


    /**
     * 异步重跑任务
     *
     * @param id           需要重跑任务的id
     * @param justRunChild 是否只重跑当前
     * @param setSuccess   是否置成功 true的时候 justRunChild 也为true
     * @param subJobIds    重跑当前任务 subJobIds不为空
     * @return
     */
    public boolean syncRestartJob(Long id, Boolean justRunChild, Boolean setSuccess, List<Long> subJobIds) {
        String key = "syncRestartJob" + id;
        if (redisTemplate.hasKey(key)) {
            LOGGER.info("syncRestartJob  {}  is doing ", key);
            return false;
        }
        String execute = redisTemplate.execute((RedisCallback<String>) connection -> {
            JedisCommands commands = (JedisCommands) connection.getNativeConnection();
            SetParams setParams = SetParams.setParams();
            setParams.nx().ex((int) environmentContext.getForkJoinResultTimeOut() * 2);
            return commands.set(key, "-1", setParams);
        });
        if(StringUtils.isBlank(execute)){
            return false;
        }
        if (BooleanUtils.isTrue(justRunChild) && null != id) {
            if (null == subJobIds) {
                subJobIds = new ArrayList<>();
            }
            subJobIds.add(id);
        }
        CompletableFuture.runAsync(new RestartRunnable(id, justRunChild, setSuccess, subJobIds, scheduleJobDao, scheduleTaskShadeDao,
                scheduleJobJobDao, environmentContext, key, redisTemplate,this,scheduleJobOperatorRecordDao));
        return true;
    }


    public Integer stopJobByCondition(ScheduleJobKillJobVO vo) {
        ScheduleJobDTO query = createKillQuery(vo);
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(query);
        pageQuery.setModel(query);
        query.setPageQuery(false);
        int count = scheduleJobDao.generalCount(query);
        if (count > 0) {
            int pageSize = 50;
            int stopSize = 0;
            if (count > pageSize) {
                //分页查询
                int pageCount = (count / pageSize) + (count % pageSize == 0 ? 0 : 1);
                stopSize = count;
                query.setPageQuery(true);
                for (int i = 0; i < pageCount; i++) {
                    PageQuery<ScheduleJobDTO> finalQuery = new PageQuery<>(query);
                    finalQuery.setModel(query);
                    finalQuery.setPageSize(pageSize);
                    finalQuery.setPage(i + 1);
                    CompletableFuture.runAsync(() -> {
                        try {
                            List<ScheduleJob> scheduleJobs = scheduleJobDao.generalQuery(finalQuery);
                            listByJobIdFillFlowSubJobs(scheduleJobs);
                            jobStopDealer.addStopJobs(scheduleJobs);
                        } catch (Exception e) {
                            LOGGER.info("stopJobByCondition  {}  error ", JSONObject.toJSONString(finalQuery));
                        }
                    });
                }
            } else {
                List<ScheduleJob> scheduleJobs = scheduleJobDao.generalQuery(pageQuery);
                listByJobIdFillFlowSubJobs(scheduleJobs);
                stopSize = jobStopDealer.addStopJobs(scheduleJobs);
            }
            return stopSize;
        }
        return 0;
    }

    /**
     * 填充jobs中的工作流和算法类型任务的子任务
     *
     * @param scheduleJobs
     */
    private void listByJobIdFillFlowSubJobs(List<ScheduleJob> scheduleJobs) {
        if (CollectionUtils.isEmpty(scheduleJobs)) {
            return;
        }
        List<String> flowJobIds = scheduleJobs
                .stream()
                .filter(s -> SPECIAL_TASK_TYPES.contains(s.getTaskType()))
                .map(ScheduleJob::getJobId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(flowJobIds)) {
            return;
        }
        List<ScheduleJob> flowSubJobIds = scheduleJobDao.getWorkFlowSubJobId(flowJobIds);
        if (CollectionUtils.isNotEmpty(flowSubJobIds)) {
            //将子任务实例加入
            scheduleJobs.addAll(flowSubJobIds);
        }
    }


    private ScheduleJobDTO createKillQuery(ScheduleJobKillJobVO vo) {
        ScheduleJobDTO ScheduleJobDTO = new ScheduleJobDTO();
        ScheduleJobDTO.setTenantId(vo.getTenantId());
        ScheduleJobDTO.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        ScheduleJobDTO.setTaskPeriodId(convertStringToList(vo.getTaskPeriodId()));
        ScheduleJobDTO.setAppType(vo.getAppType());
        //筛选任务名称
        ScheduleJobDTO.setTaskIds(vo.getTaskIds());
        setBizDay(ScheduleJobDTO, vo.getBizStartDay(), vo.getBizEndDay(), vo.getTenantId(), vo.getProjectId());
        //任务状态
        if (StringUtils.isNotBlank(vo.getJobStatuses())) {
            List<Integer> statues = new ArrayList<>();
            String[] statuses = vo.getJobStatuses().split(",");
            // 根据失败状态拆分标记来确定具体是哪一个状态map
            Map<Integer, List<Integer>> statusMap = this.getStatusMap(false);
            for (String status : statuses) {
                List<Integer> statusList = statusMap.get(new Integer(status));
                if (CollectionUtils.isNotEmpty(statusList)) {
                    statues.addAll(statusList);
                }
            }
            ScheduleJobDTO.setJobStatuses(statues);
        } else {
            ScheduleJobDTO.setJobStatuses(RdosTaskStatus.getCanStopStatus());
        }
        return ScheduleJobDTO;
    }


    public OperatorVO restartJobAndResume(List<Long> jobIdList, Boolean runCurrentJob) {
        final OperatorVO<String> batchOperatorVO = new OperatorVO<>();

        final int successNum = 0;
        int failNum = 0;

        for (final Object idStr : jobIdList) {
            try {
                final Long id = com.dtstack.dtcenter.common.util.MathUtil.getLongVal(idStr);
                if (id == null) {
                    throw new RdosDefineException("convert id: " + idStr + " exception.", ErrorCode.SERVER_EXCEPTION);
                }

                final List<Long> subJobIds = new ArrayList<>();
                if (org.apache.commons.lang.BooleanUtils.isTrue(runCurrentJob)){
                    subJobIds.add(id);
                }

                this.syncRestartJob(id, false, false, subJobIds);
            } catch (final Exception e) {
                LOGGER.error("", e);
                failNum++;
            }
        }

        batchOperatorVO.setSuccessNum(successNum);
        batchOperatorVO.setFailNum(failNum);
        batchOperatorVO.setDetail("");
        return batchOperatorVO;
    }


    /**
     * 移除满足条件的job 操作记录
     * @param jobIds
     * @param records
     */
    public void removeOperatorRecord(Collection<String> jobIds, Collection<ScheduleJobOperatorRecord> records) {
        Map<String, ScheduleJobOperatorRecord> recordMap = records.stream().collect(Collectors.toMap(ScheduleJobOperatorRecord::getJobId, k -> k));
        for (String jobId : jobIds) {
            ScheduleJobOperatorRecord record = recordMap.get(jobId);
            if (null == record) {
                continue;
            }
            EngineJobCache cache = engineJobCacheDao.getOne(jobId);
            if (cache != null && cache.getGmtCreate().after(record.getGmtCreate())) {
                //has submit to cache
                scheduleJobOperatorRecordDao.deleteByJobIdAndType(record.getJobId(), record.getOperatorType());
                LOGGER.info("remove schedule:[{}] operator record:[{}] time: [{}] stage:[{}] type:[{}]", record.getJobId(), record.getId(), cache.getGmtCreate(), cache.getStage(), record.getOperatorType());
            }
            ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, null);
            if (null == scheduleJob) {
                LOGGER.info("schedule job is null ,remove schedule:[{}] operator record:[{}] type:[{}] ", record.getJobId(), record.getId(), record.getOperatorType());
                scheduleJobOperatorRecordDao.deleteByJobIdAndType(record.getJobId(), record.getOperatorType());
            } else if (scheduleJob.getGmtModified().after(record.getGmtCreate())) {
                if (RdosTaskStatus.STOPPED_STATUS.contains(scheduleJob.getStatus()) || RdosTaskStatus.RUNNING.getStatus().equals(scheduleJob.getStatus())) {
                    //has running or finish
                    scheduleJobOperatorRecordDao.deleteByJobIdAndType(record.getJobId(), record.getOperatorType());
                    LOGGER.info("remove schedule:[{}] operator record:[{}] time: [{}] status:[{}] type:[{}]", record.getJobId(), record.getId(), scheduleJob.getGmtModified(), scheduleJob.getStatus(), record.getOperatorType());
                }
            }
        }
    }
    public Integer updateFlowJob(String placeholder, String flowJob) {
        if (StringUtils.isBlank(placeholder)) {
            return 0;
        }

        return scheduleJobDao.updateFlowJob(placeholder, flowJob);
    }

    public void batchRestartScheduleJob(Map<String, String> resumeBatchJobs) {
        if (MapUtils.isNotEmpty(resumeBatchJobs)) {
            List<String> restartJobId = new ArrayList<>(resumeBatchJobs.size());
            resumeBatchJobs.entrySet()
                    .stream()
                    .sorted(Comparator.nullsFirst(Map.Entry.comparingByValue(Comparator.nullsFirst(String::compareTo))))
                    .forEachOrdered(v -> {
                        if (null!= v && StringUtils.isNotBlank(v.getKey())) {
                            restartJobId.add(v.getKey());
                        }
                    });
            List<List<String>> partition = Lists.partition(restartJobId, environmentContext.getRestartOperatorRecordMaxSize());
            for (List<String> scheduleJobs : partition) {
                Set<String> jobIds = new HashSet<>(scheduleJobs.size());
                Set<ScheduleJobOperatorRecord> records = new HashSet<>(scheduleJobs.size());
                //更新任务为重跑任务--等待调度器获取并执行
                for (String jobId : scheduleJobs) {
                    jobIds.add(jobId);
                    ScheduleJobOperatorRecord record = new ScheduleJobOperatorRecord();
                    record.setJobId(jobId);
                    record.setForceCancelFlag(ForceCancelFlag.NO.getFlag());
                    record.setOperatorType(OperatorType.RESTART.getType());
                    record.setNodeAddress(environmentContext.getLocalAddress());
                    records.add(record);
                }
                scheduleJobDao.updateJobStatusAndPhaseStatus(Lists.newArrayList(jobIds), RdosTaskStatus.UNSUBMIT.getStatus(), JobPhaseStatus.CREATE.getCode(), Restarted.RESTARTED.getStatus(),environmentContext.getLocalAddress());
                scheduleJobOperatorRecordDao.insertBatch(records);
                LOGGER.info("reset job {}", jobIds);
            }
        }
    }
}




