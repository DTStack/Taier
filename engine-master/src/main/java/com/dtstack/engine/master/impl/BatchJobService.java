package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.constant.TaskStatusConstrant;
import com.dtstack.dtcenter.common.engine.EngineSend;
import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.hadoop.HadoopConf;
import com.dtstack.dtcenter.common.hadoop.HdfsOperator;
import com.dtstack.dtcenter.common.pager.PageQuery;
import com.dtstack.dtcenter.common.pager.PageResult;
import com.dtstack.dtcenter.common.pager.Sort;
import com.dtstack.dtcenter.common.util.DateUtil;
import com.dtstack.dtcenter.common.util.MathUtil;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.LearningFrameType;
import com.dtstack.engine.common.enums.QueryWorkFlowModel;
import com.dtstack.engine.common.enums.TaskOperateType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.BatchFillDataJobDao;
import com.dtstack.engine.dao.BatchJobDao;
import com.dtstack.engine.dao.BatchJobJobDao;
import com.dtstack.engine.dao.BatchTaskShadeDao;
import com.dtstack.engine.domain.BatchEngineJob;
import com.dtstack.engine.domain.BatchFillDataJob;
import com.dtstack.engine.domain.BatchJob;
import com.dtstack.engine.domain.BatchJobJob;
import com.dtstack.engine.domain.BatchTaskShade;
import com.dtstack.engine.dto.BatchJobDTO;
import com.dtstack.engine.dto.BatchTaskForFillDataDTO;
import com.dtstack.engine.dto.QueryJobDTO;
import com.dtstack.engine.master.WorkNode;
import com.dtstack.task.send.TaskUrlConstant;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.job.impl.BatchHadoopJobStartTrigger;
import com.dtstack.engine.master.job.impl.BatchKylinJobStartTrigger;
import com.dtstack.engine.master.job.impl.BatchLibraJobStartTrigger;
import com.dtstack.engine.master.queue.ClusterQueueInfo;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.JobGraphBuilder;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import com.dtstack.engine.master.scheduler.JobStopSender;
import com.dtstack.engine.master.vo.*;
import com.dtstack.engine.master.zk.ZkService;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/3
 */
@Service
public class BatchJobService {

    private final static Logger logger = LoggerFactory.getLogger(BatchJobService.class);

    private static final ObjectMapper objMapper = new ObjectMapper();

    private static final String DAY_PATTERN = "yyyy-MM-dd";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private DateTimeFormatter dayFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    private DateTimeFormatter dayFormatterAll = DateTimeFormat.forPattern("yyyyMMddHHmmss");

    private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private static final String BUSINESS_DATE = "business_date";

    private static final int TOTAL_HOUR_DAY = 24;

    private static final String ADD_JAR_WITH = "ADD JAR WITH %s AS ;";

    private static final String DOWNLOAD_LOG = "/api/rdos/download/batch/batchDownload/downloadJobLog?jobId=%s&taskType=%s";

    private static final List<Integer> SPECIAL_TASK_TYPES = Lists.newArrayList(EJobType.WORK_FLOW.getVal(), EJobType.ALGORITHM_LAB.getVal());

    @Autowired
    private BatchJobDao batchJobDao;

    @Autowired
    private BatchTaskShadeDao batchTaskShadeDao;

    @Autowired
    private BatchTaskShadeService batchTaskShadeService;

    @Autowired
    private EngineSend engineSend;

    @Autowired
    private JobGraphBuilder jobGraphBuilder;

    @Autowired
    private BatchFillDataJobDao batchFillDataJobDao;

    @Autowired
    private BatchFillDataJobService batchFillDataJobService;

    @Autowired
    private BatchJobJobService batchJobJobService;

    @Autowired
    private BatchAlarmService batchAlarmService;

    @Autowired
    private BatchJobAlarmService batchJobAlarmService;

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private ZkService zkService;

    @Autowired
    private BatchHadoopJobStartTrigger batchHadoopJobStartTrigger;

    @Autowired
    private BatchLibraJobStartTrigger batchLibraJobStartTrigger;

    @Autowired
    private BatchKylinJobStartTrigger batchKylinJobStartTrigger;

    @Autowired
    private JobStopSender jobStopSender;

    @Autowired
    private BatchJobJobDao batchJobJobDao;

    @Autowired
    private JobRichOperator jobRichOperator;


    private final static List<Integer> FINISH_STATUS = Lists.newArrayList(TaskStatus.FINISHED.getStatus(), TaskStatus.MANUALSUCCESS.getStatus(), TaskStatus.CANCELING.getStatus(), TaskStatus.CANCELED.getStatus());
    private final static List<Integer> FAILED_STATUS = Lists.newArrayList(TaskStatus.FAILED.getStatus(), TaskStatus.SUBMITFAILD.getStatus(), TaskStatus.KILLED.getStatus());

    private static final Map<Integer, String> PY_VERSION_MAP = new HashMap<>(2);

    static {
        PY_VERSION_MAP.put(2, " 2.x ");
        PY_VERSION_MAP.put(3, " 3.x ");
    }

    /**
     * 根据任务id展示任务详情
     *
     * @author toutian
     */
    public BatchJob getJobById(@Param("jobId") long jobId) {
        return batchJobDao.getOne(jobId);
    }

    @Forbidden
    public BatchJob getJobByJobKeyAndType(String jobKey, int type) {
        return batchJobDao.getByJobKeyAndType(jobKey, type);
    }

    /**
     * id；并非为 jobId
     */
    public Integer getStatusById(Long id) {
        return batchJobDao.getStatusById(id);
    }


    /**
     * 获取运
     * @param projectId
     * @param tenantId
     * @param appType
     * @param dtuicTenantId
     * @return
     */
    public PageResult getStatusJobList(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType,
                                    @Param("dtuicTenantId") Long dtuicTenantId, @Param("status") Integer status, @Param("pageSize") int pageSize, @Param("pageIndex") int pageIndex) {
        if (Objects.isNull(status) || Objects.isNull(dtuicTenantId)) {
            return null;
        }
        List<Integer> statusCode = TaskStatusConstrant.STATUS.get(status);
        if (CollectionUtils.isEmpty(statusCode)) {
            return null;
        }
        List<Map<String, Object>> data = batchJobDao.countByStatusAndType(EScheduleType.NORMAL_SCHEDULE.getType(), DateUtil.getFormattedDate(DateUtil.calTodayMills(), "yyyyMMddHHmmss"),
                DateUtil.getFormattedDate(DateUtil.TOMORROW_ZERO(), "yyyyMMddHHmmss"), tenantId, projectId, appType, dtuicTenantId, statusCode);
        int count = 0;
        for (Map<String, Object> info : data) {
            count += MathUtil.getIntegerVal(info.get("count"));
        }
        PageQuery pageQuery = new PageQuery(pageIndex, pageSize);
        List<Map<String, Object>> dataMaps = batchJobDao.selectStatusAndType(EScheduleType.NORMAL_SCHEDULE.getType(), DateUtil.getFormattedDate(DateUtil.calTodayMills(), "yyyyMMddHHmmss"),
                DateUtil.getFormattedDate(DateUtil.TOMORROW_ZERO(), "yyyyMMddHHmmss"), tenantId, projectId, appType, dtuicTenantId, statusCode, pageQuery.getStart(), pageQuery.getPageSize());
        return new PageResult(dataMaps, count, pageQuery);
    }

    /**
     * 获取各个状态任务的数量
     */
    public JSONObject getStatusCount(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType,@Param("dtuicTenantId") Long dtuicTenantId) {
        int all = 0;
        JSONObject m = new JSONObject(TaskStatusConstrant.STATUS.size());
        List<Map<String, Object>> data = batchJobDao.countByStatusAndType(EScheduleType.NORMAL_SCHEDULE.getType(), DateUtil.getFormattedDate(DateUtil.calTodayMills(), "yyyyMMddHHmmss"),
                DateUtil.getFormattedDate(DateUtil.TOMORROW_ZERO(), "yyyyMMddHHmmss"), tenantId, projectId, appType,dtuicTenantId,null);
        for (Integer code : TaskStatusConstrant.STATUS.keySet()) {
            List<Integer> status = TaskStatusConstrant.STATUS.get(code);
            int count = 0;
            for (Map<String, Object> info : data) {
                if (status.contains(MathUtil.getIntegerVal(info.get("status")))) {
                    count += MathUtil.getIntegerVal(info.get("count"));
                }
            }
            all += count;
            TaskStatus taskStatus = TaskStatus.getTaskStatusByVal(code);
            m.put(taskStatus.name(), count);
        }

        m.put("ALL", all);

        return m;
    }

    /**
     * 运行时长top排序
     */
    public List<JobTopOrderVO> runTimeTopOrder(@Param("projectId") Long projectId,
                                               @Param("startTime") Long startTime,
                                               @Param("endTime") Long endTime, @Param("appType") Integer appType,@Param("dtuicTenantId") Long dtuicTenantId) {

        if (null != startTime && null != endTime) {
            startTime = startTime * 1000;
            endTime = endTime * 1000;
        } else {
            startTime = DateUtil.calTodayMills();
            endTime = DateUtil.TOMORROW_ZERO();
        }

        PageQuery pageQuery = new PageQuery(1, 10);
        List<Map<String, Object>> list = batchJobDao.listTopRunTime(projectId, new Timestamp(startTime), new Timestamp(endTime), pageQuery, appType,dtuicTenantId);

        List<JobTopOrderVO> jobTopOrderVOS = new ArrayList<>();

        for (Map<String, Object> info : list) {
            //b.id, b.taskId, b.cycTime, b.type, eb.execTime
            Long jobId = MathUtil.getLongVal(info.get("id"));
            Long taskId = MathUtil.getLongVal(info.get("taskId"));
            String cycTime = MathUtil.getString(info.get("cycTime"));
            Integer type = MathUtil.getIntegerVal(info.get("type"));
            Long execTime = MathUtil.getLongVal(info.get("execTime"));
            execTime = execTime == null ? 0 : execTime;

            JobTopOrderVO jobTopOrderVO = new JobTopOrderVO();
            jobTopOrderVO.setRunTime(DateUtil.getTimeDifference(execTime * 1000));
            jobTopOrderVO.setJobId(jobId);
            jobTopOrderVO.setTaskId(taskId);
            jobTopOrderVO.setCycTime(DateUtil.addTimeSplit(cycTime));
            jobTopOrderVO.setType(type);
            jobTopOrderVO.setTaskType(MathUtil.getIntegerVal(info.get("taskType")));
            jobTopOrderVO.setIsDeleted(MathUtil.getIntegerVal(info.get("isDeleted")));
            jobTopOrderVO.setCreateUserId(MathUtil.getLongVal(info.get("createUserId")));

            jobTopOrderVOS.add(jobTopOrderVO);
        }

        return jobTopOrderVOS;
    }

    /**
     * 近30天任务出错排行
     */
    public List<JobTopErrorVO> errorTopOrder(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType,@Param("dtuicTenantId") Long dtuicTenantId) {

        Timestamp time = new Timestamp(DateUtil.getLastDay(30));
        PageQuery pageQuery = new PageQuery(1, 10);
        List<Map<String, Object>> list = batchJobDao.listTopErrorByType(dtuicTenantId,tenantId, projectId, EScheduleType.NORMAL_SCHEDULE.getType(), time, FAILED_STATUS, pageQuery, appType);
        List<JobTopErrorVO> jobTopErrorVOS = new ArrayList<>();

        for (Map<String, Object> info : list) {

            Long taskId = MathUtil.getLongVal(info.get("taskId"));
            Integer count = MathUtil.getIntegerVal(info.get("errorCount"));

            JobTopErrorVO errorVO = new JobTopErrorVO();
            errorVO.setErrorCount(count);
            errorVO.setTaskId(taskId);
            jobTopErrorVOS.add(errorVO);
        }
        return jobTopErrorVOS;
    }


    /**
     * 曲线图数据
     */
    public BatchJobChartVO getJobGraph(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType,@Param("dtuicTenantId") Long dtuicTenantId) {

        List<Integer> statusList = new ArrayList<>(4);
        List<Integer> finishedList = TaskStatusConstrant.STATUS.get(TaskStatus.FINISHED.getStatus());
        List<Integer> failedList = TaskStatusConstrant.STATUS.get(TaskStatus.FAILED.getStatus());
        statusList.addAll(finishedList);
        statusList.addAll(failedList);
        List<Object> todayJobList = finishData(batchJobDao.listTodayJobs(statusList, EScheduleType.NORMAL_SCHEDULE.getType(), projectId, tenantId, appType,dtuicTenantId));
        List<Object> yesterdayJobList = finishData(batchJobDao.listYesterdayJobs(statusList, EScheduleType.NORMAL_SCHEDULE.getType(), projectId, tenantId, appType,dtuicTenantId));
        List<Object> monthJobList = finishData(batchJobDao.listMonthJobs(statusList, EScheduleType.NORMAL_SCHEDULE.getType(), projectId, tenantId, appType,dtuicTenantId));

        for (int i = 0; i < TOTAL_HOUR_DAY; i++) {
            monthJobList.set(i, (Long) monthJobList.get(i) / 30);
        }

        BatchJobChartVO data = new BatchJobChartVO(todayJobList, yesterdayJobList, monthJobList);
        return data;
    }

    /**
     * 获取数据科学的曲线图
     *
     * @return
     */
    public BatchSecienceJobChartVO getScienceJobGraph(@Param("projectId") long projectId, @Param("tenantId") Long tenantId,
                                                      @Param("taskType") Integer taskType) {
        List<Integer> finishedList = Lists.newArrayList(TaskStatus.FINISHED.getStatus());
        List<Integer> failedList = Lists.newArrayList(TaskStatus.FAILED.getStatus(), TaskStatus.SUBMITFAILD.getStatus());
        List<Integer> deployList = Lists.newArrayList(TaskStatus.UNSUBMIT.getStatus(), TaskStatus.SUBMITTING.getStatus(), TaskStatus.WAITENGINE.getStatus());
        List<Map<String, Object>> successCnt = batchJobDao.listThirtyDayJobs(finishedList, EScheduleType.NORMAL_SCHEDULE.getType(), taskType, projectId, tenantId);
        List<Map<String, Object>> failCnt = batchJobDao.listThirtyDayJobs(failedList, EScheduleType.NORMAL_SCHEDULE.getType(), taskType, projectId, tenantId);
        List<Map<String, Object>> deployCnt = batchJobDao.listThirtyDayJobs(deployList, EScheduleType.NORMAL_SCHEDULE.getType(), taskType, projectId, tenantId);
        List<Map<String, Object>> totalCnt = batchJobDao.listThirtyDayJobs(null, EScheduleType.NORMAL_SCHEDULE.getType(), taskType, projectId, tenantId);
        BatchSecienceJobChartVO result = new BatchSecienceJobChartVO();
        return result.format(totalCnt, successCnt, failCnt, deployCnt);
    }

    public Map<String, Object> countScienceJobStatus(@Param("projectIds") List<Long> projectIds, @Param("tenantId") Long tenantId, @Param("runStatus") Integer runStatus, @Param("type") Integer type, @Param("taskType") Integer taskType) {
        return batchJobDao.countScienceJobStatus(runStatus, projectIds, type, taskType, tenantId);
    }

    private Long objCastLong(Object obj) {
        if (Objects.isNull(obj)) {
            return 0L;
        }
        return Long.valueOf(obj.toString());
    }

    @Forbidden
    private List<Object> finishData(List<Map<String, Object>> metadata) {
        Map<String, Long> dataMap = new HashMap<>();
        List<Object> dataList = new ArrayList<>();

        for (Map<String, Object> data : metadata) {
            if (dataMap.get("hour") != null) {
                dataMap.put(MathUtil.getString(data.get("hour")), dataMap.get(MathUtil.getString(data.get("hour"))) + MathUtil.getLongVal(data.get("data")));
            }
            dataMap.put(MathUtil.getString(data.get("hour")), MathUtil.getLongVal(data.get("data")));
        }

        String hour;
        for (int i = 0; i < TOTAL_HOUR_DAY; i++) {
            if (i < 10) {
                hour = "0" + i;
            } else {
                hour = "" + i;
            }

            if (dataMap.containsKey(hour)) {
                dataList.add(dataMap.get(hour));
            } else {
                dataList.add(0L);
            }
        }
        return dataList;
    }


    /**
     * 任务运维 - 搜索
     *
     * @return
     * @author toutian
     */
    public PageResult<List<BatchJobVO>> queryJobs(QueryJobDTO vo) throws Exception {

        if (vo.getType() == null) {
            throw new RdosDefineException("类型参数必填", ErrorCode.INVALID_PARAMETERS);
        }
        vo.setSplitFiledFlag(true);
        BatchJobDTO batchJobDTO = this.createQuery(vo);

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

        String searchType = vo.getSearchType();
        PageQuery<BatchJobDTO> pageQuery = new PageQuery<>(vo.getCurrentPage(), vo.getPageSize(), "gmt_modified", Sort.DESC.name());

        if (StringUtils.isEmpty(searchType) || "fuzzy".equalsIgnoreCase(searchType)) {
            batchJobDTO.setSearchType(1);
        } else if ("precise".equalsIgnoreCase(searchType)) {
            batchJobDTO.setSearchType(2);
        } else if ("front".equalsIgnoreCase(searchType)) {
            batchJobDTO.setSearchType(3);
        } else if ("tail".equalsIgnoreCase(searchType)) {
            batchJobDTO.setSearchType(4);
        } else {
            batchJobDTO.setSearchType(1);
        }
        pageQuery.setModel(batchJobDTO);

        if (StringUtils.isNotBlank(vo.getTaskName()) || Objects.nonNull(vo.getOwnerId())) {
            List<BatchTaskShade> batchTaskShades = batchTaskShadeDao.listByNameLike(vo.getProjectId(), vo.getTaskName(), vo.getAppType(), vo.getOwnerId());
            if (CollectionUtils.isNotEmpty(batchTaskShades)) {
                batchJobDTO.setTaskIds(batchTaskShades.stream().map(BatchTaskShade::getTaskId).collect(Collectors.toList()));
            }
        }
        batchJobDTO.setPageQuery(true);

        int count = batchJobDao.generalCount(batchJobDTO);
        List<BatchJobVO> vos = new ArrayList<>();

        if (count > 0) {
            List<BatchJob> batchJobs = batchJobDao.generalQuery(pageQuery);
            if (CollectionUtils.isEmpty(batchJobs)) {
                return new PageResult<>(vos, count, pageQuery);
            }

            Map<Long, BatchTaskForFillDataDTO> shadeMap = this.prepare(batchJobs);
            vos = this.transfer(batchJobs, shadeMap);


            if (queryAll) {
                //处理工作流下级
                dealFlowWorkSubJobs(vos);
            } else {
                //前端异步获取relatedJobs
                //dealFlowWorkJobs(vos, shadeMap);
            }
        }

        return new PageResult<>(vos, count, pageQuery);
    }

    public List<BatchPeriodInfoVO> displayPeriods(@Param("isAfter") boolean isAfter, @Param("jobId") Long jobId, @Param("projectId") Long projectId, @Param("limit") int limit) throws Exception {
        BatchJob job = batchJobDao.getOne(jobId);
        if (job == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        List<BatchJob> batchJobs = batchJobDao.listAfterOrBeforeJobs(job.getTaskId(), isAfter, job.getCycTime());
        Collections.sort(batchJobs, new Comparator<BatchJob>() {
            @Override
            public int compare(BatchJob o1, BatchJob o2) {
                if (Long.parseLong(o1.getCycTime()) < Long.parseLong(o2.getCycTime())) {
                    return 1;
                }
                if (Long.parseLong(o1.getCycTime()) > Long.parseLong(o2.getCycTime())) {
                    return -1;
                }
                return 0;
            }
        });
        if (batchJobs.size() > limit) {
            batchJobs = batchJobs.subList(0, limit);
        }
        List<BatchPeriodInfoVO> vos = new ArrayList<>(batchJobs.size());
        batchJobs.forEach(e -> {
            BatchPeriodInfoVO vo = new BatchPeriodInfoVO();
            vo.setJobId(e.getId());
            vo.setCycTime(DateUtil.addTimeSplit(e.getCycTime()));
            vo.setStatus(e.getStatus());
            vos.add(vo);
        });
        return vos;
    }

    private void dealFlowWorkJobs(List<BatchJobVO> vos, Map<Long, BatchTaskShade> batchTaskShadeMap) throws Exception {
        for (BatchJobVO vo : vos) {
            Integer type = batchTaskShadeMap.get(vo.getTaskId()).getTaskType();
            if (EJobType.WORK_FLOW.getVal().intValue() == type) {
                String jobId = vo.getJobId();
                List<BatchJob> subJobs = batchJobDao.getSubJobsByFlowIds(Lists.newArrayList(jobId));
                List<BatchTaskForFillDataDTO> batchTaskShadeList = Lists.newArrayList();
                Map<Long, BatchTaskForFillDataDTO> shadeMap = this.prepare(subJobs);
                List<BatchJobVO> subJobVOs = this.transfer(subJobs, shadeMap);
                vo.setRelatedJobs(subJobVOs);
            }
        }
    }

    /**
     * 获取工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    public BatchJobVO getRelatedJobs(@Param("jobId") String jobId, @Param("vo") String query) throws Exception {
        QueryJobDTO vo = JSONObject.parseObject(query, QueryJobDTO.class);
        BatchJob batchJob = batchJobDao.getByJobId(jobId, Deleted.NORMAL.getStatus());
        Map<Long, BatchTaskForFillDataDTO> shadeMap = this.prepare(Lists.newArrayList(batchJob));
        List<BatchJobVO> transfer = this.transfer(Lists.newArrayList(batchJob), shadeMap);
        if (CollectionUtils.isEmpty(transfer)) {
            return null;
        }
        BatchJobVO batchJobVO = transfer.get(0);

        if (batchJob != null) {
            if (EJobType.WORK_FLOW.getVal().intValue() == batchJobVO.getBatchTask().getTaskType()) {
                vo.setSplitFiledFlag(true);
                //除去任务类型中的工作流类型的条件，用于展示下游节点
                if (StringUtils.isNotBlank(vo.getTaskType())) {
                    vo.setTaskType(vo.getTaskType().replace(String.valueOf(EJobType.WORK_FLOW.getVal()), ""));
                }
                BatchJobDTO batchJobDTO = createQuery(vo);
                batchJobDTO.setPageQuery(false);
                batchJobDTO.setFlowJobId(jobId);
                PageQuery<BatchJobDTO> pageQuery = new PageQuery<>(vo.getCurrentPage(), vo.getPageSize(), "gmt_modified", Sort.DESC.name());
                pageQuery.setModel(batchJobDTO);
                batchJobDTO.setNeedQuerySonNode(true);
                List<BatchJob> subJobs = batchJobDao.generalQuery(pageQuery);
                Map<Long, BatchTaskForFillDataDTO> subShadeMap = this.prepare(subJobs);
                List<BatchJobVO> subJobVOs = this.transfer(subJobs, subShadeMap);
                batchJobVO.setRelatedJobs(subJobVOs);
                return batchJobVO;
            } else {
                throw new RdosDefineException("只有工作流任务有下属节点");
            }
        } else {
            throw new RdosDefineException("该实例对象不存在");
        }

    }


    private void dealFlowWorkSubJobs(List<BatchJobVO> vos) throws Exception {
        Map<String, BatchJobVO> record = Maps.newHashMap();
        Map<String, Integer> voIndex = Maps.newHashMap();
        vos.forEach(job -> voIndex.put(job.getJobId(), vos.indexOf(job)));
        List<BatchJobVO> copy = Lists.newArrayList(vos);
        Iterator<BatchJobVO> iterator = vos.iterator();
        while (iterator.hasNext()) {
            BatchJobVO jobVO = iterator.next();
            String flowJobId = jobVO.getFlowJobId();
            if (!"0".equals(flowJobId)) {
                if (record.containsKey(flowJobId)) {
                    BatchJobVO flowVo = record.get(flowJobId);
                    flowVo.getRelatedJobs().add(jobVO);
                    iterator.remove();
                } else {
                    BatchJobVO flowVO;
                    if (voIndex.containsKey(flowJobId)) {
                        flowVO = copy.get(voIndex.get(flowJobId));
                        flowVO.setRelatedJobs(Lists.newArrayList(jobVO));
                        iterator.remove();
                    } else {
                        BatchJob flow = batchJobDao.getByJobId(flowJobId, Deleted.NORMAL.getStatus());
                        if (flow == null) {
                            continue;
                        }
                        Map<Long, BatchTaskForFillDataDTO> batchTaskShadeMap = this.prepare(Lists.newArrayList(flow));
                        List<BatchJobVO> flowVOs = this.transfer(Lists.newArrayList(flow), batchTaskShadeMap);
                        flowVO = flowVOs.get(0);
                        flowVO.setRelatedJobs(Lists.newArrayList(jobVO));
                        vos.set(vos.indexOf(jobVO), flowVO);
                    }
                    record.put(flowJobId, flowVO);
                }
            }
        }
    }


    private Map<Long, BatchTaskForFillDataDTO> prepare(List<BatchJob> batchJobs) {
        if (CollectionUtils.isEmpty(batchJobs)) {
            return new HashMap<>(0);
        }

        List<Long> taskIdList = batchJobs.stream().map(BatchJob::getTaskId).collect(Collectors.toList());

        return batchTaskShadeDao.listSimpleTaskByTaskIds(taskIdList, null).stream()
                .collect(Collectors.toMap(BatchTaskForFillDataDTO::getTaskId, batchTaskForFillDataDTO -> batchTaskForFillDataDTO));
    }

    private List<BatchJobVO> transfer(List<BatchJob> batchJobs, Map<Long, BatchTaskForFillDataDTO> batchTaskShadeMap) {
        List<BatchJobVO> vos = new ArrayList<>(batchJobs.size());
        for (BatchJob batchJob : batchJobs) {
            BatchTaskForFillDataDTO taskShade = batchTaskShadeMap.get(batchJob.getTaskId());
            if (taskShade == null) {
                continue;
            }

            //维持旧接口 数据结构
            BatchEngineJob engineJob = new BatchEngineJob();
            engineJob.setStatus(batchJob.getStatus());
            engineJob.setRetryNum(batchJob.getRetryNum());
            String voTaskName = taskShade.getName();
            BatchJobVO batchJobVO = new BatchJobVO(batchJob);
            if (batchJob.getExecStartTime() != null) {
                batchJobVO.setExecStartDate(timeFormatter.print(batchJob.getExecStartTime().getTime()));
                engineJob.setExecStartTime(new Timestamp(batchJob.getExecStartTime().getTime()));
            }
            if (batchJob.getExecEndTime() != null) {
                batchJobVO.setExecEndDate(timeFormatter.print(batchJob.getExecEndTime().getTime()));
                engineJob.setExecEndTime(new Timestamp(batchJob.getExecEndTime().getTime()));
            }
            engineJob.setExecTime(batchJob.getExecTime());
            batchJobVO.setBatchEngineJob(engineJob);

            BatchTaskVO taskVO = new BatchTaskVO(taskShade);
            taskVO.setName(voTaskName);
            if (batchJob.getPeriodType() != null) {
                batchJobVO.setTaskPeriodId(batchJob.getPeriodType());
            }
            batchJobVO.setBatchTask(taskVO);
            batchJobVO.setOwnerUserId(taskShade.getOwnerUserId());
            vos.add(batchJobVO);
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
            throw new RdosDefineException("类型必填", ErrorCode.INVALID_PARAMETERS);
        }
        vo.setSplitFiledFlag(true);
        BatchJobDTO batchJobDTO = createQuery(vo);
        batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_ParentNodes.getType());
        //需要查询工作流的子节点
        batchJobDTO.setNeedQuerySonNode(true);
        if (StringUtils.isNotBlank(vo.getTaskName()) || Objects.nonNull(vo.getOwnerId())) {
            List<BatchTaskShade> batchTaskShades = batchTaskShadeDao.listByNameLike(vo.getProjectId(), vo.getTaskName(), vo.getAppType(), vo.getOwnerId());
            if (CollectionUtils.isNotEmpty(batchTaskShades)) {
                batchJobDTO.setTaskIds(batchTaskShades.stream().map(BatchTaskShade::getTaskId).collect(Collectors.toList()));
            }
        }
        List<Map<Integer, Long>> statusCount = batchJobDao.getJobsStatusStatistics(batchJobDTO);

        Map<String, Long> attachment = Maps.newHashMap();
        long totalNum = 0;

        Map<Integer, List<Integer>> statusMap = TaskStatusConstrant.STATUS_FAILED_DETAIL;
        for (Map.Entry<Integer, List<Integer>> entry : statusMap.entrySet()) {
            String statusName = TaskStatusConstrant.getCode(entry.getKey());
            List<Integer> statuses = entry.getValue();
            long num = 0;
            for (Map<Integer, Long> statusCountMap : statusCount) {
                if (statuses.contains(statusCountMap.get("status"))) {
                    num += statusCountMap.get("count");
                }
            }
            attachment.put(statusName, num);
            totalNum += num;
        }

        attachment.putIfAbsent("ALL", totalNum);

        return attachment;
    }

    private Map<Integer, List<Integer>> getStatusMap(Boolean splitFiledFlag) {
        Map<Integer, List<Integer>> statusMap;
        if (splitFiledFlag) {
            statusMap = TaskStatusConstrant.STATUS_FAILED_DETAIL;
        } else {
            statusMap = TaskStatusConstrant.STATUS;
        }
        return statusMap;
    }

    private BatchJobDTO createKillQuery(KillJobVO vo) {
        BatchJobDTO batchJobDTO = new BatchJobDTO();
        batchJobDTO.setTenantId(vo.getTenantId());
        batchJobDTO.setProjectId(vo.getProjectId());
        batchJobDTO.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        batchJobDTO.setTaskPeriodId(convertStringToList(vo.getTaskPeriodId()));

        setBizDay(batchJobDTO, vo.getBizStartDay(), vo.getBizEndDay(), vo.getTenantId(), vo.getProjectId());
        //任务状态
        if (StringUtils.isNotBlank(vo.getJobStatuses())) {
            List<Integer> statues = new ArrayList<>();
            String[] statuses = vo.getJobStatuses().split(",");
            // 根据失败状态拆分标记来确定具体是哪一个状态map
            Map<Integer, List<Integer>> statusMap = getStatusMap(false);
            for (String status : statuses) {
                List<Integer> statusList = statusMap.get(new Integer(status));
                if (CollectionUtils.isNotEmpty(statusList)) {
                    statues.addAll(statusList);
                }
            }
            batchJobDTO.setJobStatuses(statues);
        }
        return batchJobDTO;
    }

    private BatchJobDTO createQuery(QueryJobDTO vo) {

        BatchJobDTO batchJobDTO = new BatchJobDTO();
        this.createBaseQuery(vo, batchJobDTO);

        //任务状态
        if (StringUtils.isNotBlank(vo.getJobStatuses())) {
            List<Integer> statues = new ArrayList<>();
            String[] statuses = vo.getJobStatuses().split(",");
            // 根据失败状态拆分标记来确定具体是哪一个状态map
            Map<Integer, List<Integer>> statusMap = getStatusMap(vo.getSplitFiledFlag());
            for (String status : statuses) {
                List<Integer> statusList = statusMap.get(new Integer(status));
                statues.addAll(statusList);
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
        //分页
        batchJobDTO.setPageQuery(true);

        return batchJobDTO;
    }

    private void createBaseQuery(QueryJobDTO vo, BatchJobDTO batchJobDTO) {
        batchJobDTO.setTenantId(vo.getTenantId());
        batchJobDTO.setProjectId(vo.getProjectId());
        batchJobDTO.setTaskTypes(convertStringToList(vo.getTaskType()));
        batchJobDTO.setExecTimeSort(vo.getExecTimeSort());
        batchJobDTO.setExecStartSort(vo.getExecStartSort());
        batchJobDTO.setExecEndSort(vo.getExecEndSort());
        batchJobDTO.setCycSort(vo.getCycSort());
        batchJobDTO.setRetryNumSort(vo.getRetryNumSort());
        batchJobDTO.setBusinessDateSort(vo.getBusinessDateSort());
        batchJobDTO.setTaskPeriodId(convertStringToList(vo.getTaskPeriodId()));

        //调度类型
        if (vo.getType() != null) {
            batchJobDTO.setType(vo.getType());
        }

        //只有工作流 需要查询子节点
        if(batchJobDTO.getTaskTypes().contains(EJobType.WORK_FLOW.getType())){
            batchJobDTO.setNeedQuerySonNode(true);
        }
        //分页
        batchJobDTO.setPageQuery(true);
        //bugfix #19764 为对入参做处理
        if (!Strings.isNullOrEmpty(vo.getJobStatuses())) {
            batchJobDTO.setJobStatuses(Arrays.stream(vo.getJobStatuses().split(",")).map(Integer::parseInt).collect(Collectors.toList()));
        }
    }

    @Forbidden
    private void setBizDay(BatchJobDTO batchJobDTO, Long bizStartDay, Long bizEndDay, Long tenantId, Long projectId) {
        if (bizStartDay != null && bizEndDay != null) {
            String bizStart = dayFormatterAll.print(getTime(bizStartDay * 1000, 0).getTime());
            String bizEnd = dayFormatterAll.print(getTime(bizEndDay * 1000, -1).getTime());
            batchJobDTO.setBizStartDay(bizStart);
            batchJobDTO.setBizEndDay(bizEnd);
        }
    }

    @Forbidden
    private void setCycDay(BatchJobDTO batchJobDTO, Long cycStartDay, Long cycEndDay, Long tenantId, Long projectId) {
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

    @Forbidden
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

    public List<BatchRunDetailVO> jobDetail(@Param("taskId") Long taskId, @Param("appType") Integer appType) {

        BatchTaskShade task = batchTaskShadeService.getBatchTaskById(taskId, appType);

        PageQuery pageQuery = new PageQuery(1, 20, "business_date", Sort.DESC.name());
        List<Map<String, String>> jobs = batchJobDao.listTaskExeTimeInfo(task.getId(), FINISH_STATUS, pageQuery);
        List<BatchRunDetailVO> details = null;
        if (CollectionUtils.isNotEmpty(jobs)) {
            details = new ArrayList<>(jobs.size());
            for (Map<String, String> job : jobs) {
                Object execStartTimeObj = MathUtil.getString(job.get("execStartTime"));
                Object ExecEndTimeObj = MathUtil.getString(job.get("execEndTime"));
                Long execTime = MathUtil.getLongVal(job.get("execTime"));

                BatchRunDetailVO runDetail = new BatchRunDetailVO();
                if (execTime == null || execTime == 0L) {
                    continue;
                }

                runDetail.setExecTime(execTime);
                runDetail.setStartTime(DateUtil.getFormattedDate(((Timestamp) execStartTimeObj).getTime(), "yyyy-MM-dd HH:mm:ss"));
                runDetail.setEndTime(DateUtil.getFormattedDate(((Timestamp) ExecEndTimeObj).getTime(), "yyyy-MM-dd HH:mm:ss"));

                BatchTaskShade jobTask = batchTaskShadeService.getBatchTaskById(taskId, appType);
                runDetail.setTaskName(jobTask.getName());
                details.add(runDetail);
            }
        }

        return details;
    }

    @Forbidden
    public Integer updateStatusAndLogInfoById(Long id, Integer status, String msg) {
        if (StringUtils.isNotBlank(msg) && msg.length() > 500) {
            msg = msg.substring(0,500) + "...";
        }
        return batchJobDao.updateStatusAndLogInfoById(id, status, msg);
    }

    @Forbidden
    public Integer updateStatusByJobId(String jobId, Integer status) {
        return batchJobDao.updateStatusByJobId(jobId, status, null);
    }

    @Forbidden
    public Long startJob(BatchJob batchJob) throws Exception {
        updateStatusByJobId(batchJob.getJobId(), TaskStatus.SUBMITTING.getStatus());
        sendTaskStartTrigger(batchJob);
        return batchJob.getId();
    }


    /**
     * 触发 engine 执行指定task
     */
    public void sendTaskStartTrigger(BatchJob batchJob) throws Exception {

        BatchTaskShade batchTask = batchTaskShadeService.getBatchTaskById(batchJob.getTaskId(), batchJob.getAppType());
        if (batchTask == null) {
            throw new RdosDefineException("can not find task by id:" + batchJob.getTaskId());
        }

        //判断是不是虚节点---虚节点直接完成
        if (batchTask.getTaskType().equals(EJobType.VIRTUAL.getVal())) {
            updateStatusByJobId(batchJob.getJobId(), TaskStatus.FINISHED.getStatus());
            return;
        }

        //工作流节点保持提交中状态,状态更新见BatchFlowWorkJobService
        if (batchTask.getTaskType().equals(EJobType.WORK_FLOW.getVal()) ||
                batchTask.getTaskType().equals(EJobType.ALGORITHM_LAB.getVal())) {
            BatchJob updateJob = new BatchJob();
            updateJob.setId(batchJob.getId());
            updateJob.setStatus(TaskStatus.SUBMITTING.getStatus());
            updateJob.setExecStartTime(new Timestamp(System.currentTimeMillis()));
            updateJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
            batchJobDao.update(updateJob);
            return;
        }

        String extInfoByTaskId = batchTaskShadeDao.getExtInfoByTaskId(batchJob.getTaskId(), batchJob.getAppType());
        JSONObject extObject = JSONObject.parseObject(extInfoByTaskId);
        if (Objects.nonNull(extObject)) {
            JSONObject info = extObject.getJSONObject(TaskUrlConstant.INFO);
            if (Objects.nonNull(info)) {
                Integer multiEngineType = info.getInteger("multiEngineType");
                String ldapUserName = info.getString("ldapUserName");
                String ldapPassword = info.getString("ldapPassword");
                String dbName = info.getString("dbName");
                if (StringUtils.isNotBlank(ldapUserName)) {
                    info.remove("ldapUserName");
                    info.remove("ldapPassword");
                    info.remove("dbName");
                }
                Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
                if (MultiEngineType.HADOOP.getType() == multiEngineType) {
                    batchHadoopJobStartTrigger.readyForTaskStartTrigger(actionParam, batchTask, batchJob);
                } else if (MultiEngineType.LIBRA.getType() == multiEngineType) {
                    batchLibraJobStartTrigger.readyForTaskStartTrigger(actionParam, batchTask, batchJob);
                } else if (MultiEngineType.KYLIN.getType() == multiEngineType) {
                    batchKylinJobStartTrigger.readyForTaskStartTrigger(actionParam, batchTask, batchJob);
                }
                actionParam.put("name", batchJob.getJobName());
                actionParam.put("taskId", batchJob.getJobId());

                //TODO, 拼装控制台的集群信息

//                taskJson = objMapper.writeValueAsString(actionParam);
                if (EJobType.HIVE_SQL.getType().equals(batchJob.getTaskType()) || EJobType.IMPALA_SQL.getType().equals(batchJob.getTaskType())) {
                    //TODO, 拼装是区别ldap
//                    engineSend.sendTask(taskJson, ldapUserName, ldapPassword, dbName, null, null);
                    return;
                }
                //TODO 放入队列
//                engineSend.sendTask(taskJson, null, null);
                WorkNode.getInstance().addSubmitJob(actionParam);
                return;
            }
        }
        //额外信息为空 标记任务为失败
        this.updateStatusAndLogInfoById(batchJob.getId(), TaskStatus.FAILED.getStatus(), "任务运行信息为空");
    }


    public String getTableName(String table) {
        String simpleTableName = table;
        if (StringUtils.isNotEmpty(table)) {
            String[] tablePart = table.split("\\.");
            if (tablePart.length == 1) {
                simpleTableName = tablePart[0];
            } else if (tablePart.length == 2) {
                simpleTableName = tablePart[1];
            }
        }

        return simpleTableName;
    }


    private void treatInputAndOutput(JSONObject exeArgsJson) {
        if (!exeArgsJson.containsKey("--app-type")) {
            return;
        }
        String appType = exeArgsJson.getString("--app-type");
        if (appType.equals(LearningFrameType.MXNet.getName()) || appType.equals(LearningFrameType.TensorFlow.getName())) {
            String input = exeArgsJson.getString("--input");
            String output = exeArgsJson.getString("--output");
            if (StringUtils.isBlank(input)) {
                exeArgsJson.remove("--input");
            } else if (appType.equals(LearningFrameType.MXNet.getName())) {
                exeArgsJson.put("--cacheFile", exeArgsJson.getString("--input"));
                exeArgsJson.remove("--input");
            }
            if (StringUtils.isBlank(output)) {
                exeArgsJson.remove("--output");
            }
        }
    }

    /**
     * 1.兼容之前配置
     * like：
     * --app-type python --python-version 2
     * 2.转换深度学习pythonVersion为 2.x\3.x
     */
    private void setPythonVersion(JSONObject exeArgsJson) {
        if (!exeArgsJson.containsKey("--app-type")) {
            return;
        }
        String appType = exeArgsJson.getString("--app-type");
        if (appType.equals("python")) {
            int pyVersion = exeArgsJson.getIntValue("--python-version");
            if (pyVersion == 0) {
                exeArgsJson.put("--app-type", EngineType.Python3.getVal());
            } else {
                exeArgsJson.put("--app-type", EngineType.getByPythonVersion(pyVersion).getEngineName());
            }
        } else if (appType.equals(LearningFrameType.TensorFlow.getName()) ||
                appType.equals(LearningFrameType.MXNet.getName())) {
            int pyVersion = exeArgsJson.getIntValue("--python-version");
            exeArgsJson.put("--python-version", convertPYVersion(pyVersion));
        }
    }

    /**
     * 将 --python-version 的2或3 转换为 2.x AND 3.x
     *
     * @param version
     * @return
     */
    private String convertPYVersion(int version) {
        String ver = PY_VERSION_MAP.get(version);
        if (ver != null) {
            return ver;
        }
        throw new RdosDefineException("python不支持2.x和3.x之外的版本类型");
    }

    public String stopJob(@Param("jobId") long jobId, @Param("userId") Long userId, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("dtuicTenantId") Long dtuicTenantId,
                          @Param("isRoot") Boolean isRoot, @Param("appType") Integer appType) throws Exception {

        BatchJob batchJob = batchJobDao.getOne(jobId);
        if (batchJob == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }

        BatchTaskShade task = batchTaskShadeService.getBatchTaskById(batchJob.getTaskId(), appType);
        if (task == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }

        Integer status = batchJob.getStatus();
        if (!checkJobCanStop(status)) {
            throw new RdosDefineException(ErrorCode.JOB_CAN_NOT_STOP);
        }

        if (status.equals(TaskStatus.UNSUBMIT.getStatus())) {
            return stopUnsubmitJob(batchJob);
        } else if (TaskStatusConstrant.RUNNING_STATUS.contains(status) || TaskStatusConstrant.WAIT_STATUS.contains(status)) {
            return stopSubmittedJob(Lists.newArrayList(batchJob), dtuicTenantId, appType);
        } else {
            throw new RdosDefineException(ErrorCode.JOB_CAN_NOT_STOP);
        }
    }


    public void stopFillDataJobs(@Param("fillDataJobName") String fillDataJobName, @Param("projectId") Long projectId, @Param("dtuicTenantId") Long dtuicTenantId, @Param("appType") Integer appType) throws Exception {
        //还未发送到engine部分---直接停止
        if (StringUtils.isBlank(fillDataJobName) || null == projectId || null == appType) {
            return;
        }
        String likeName = fillDataJobName + "-%";
        batchJobDao.stopUnsubmitJob(likeName, projectId, appType, TaskStatus.CANCELED.getStatus());
        //发送停止消息到engine
        //查询出所有需要停止的任务
        List<BatchJob> needStopIdList = batchJobDao.listNeedStopFillDataJob(likeName, TaskStatus.getCanStopStatus(), projectId, appType);
        //发送停止任务消息到engine
        this.stopSubmittedJob(needStopIdList, dtuicTenantId, appType);
    }


    @Transactional(rollbackFor = Exception.class)
    public int batchStopJobs(@Param("jobIdList") List<Long> jobIdList,
                             @Param("projectId") Long projectId,
                             @Param("dtuicTenantId") Long dtuicTenantId,
                             @Param("appType") Integer appType) {
        if (CollectionUtils.isEmpty(jobIdList)) {
            return 0;
        }
        List<BatchJob> jobs = new ArrayList<>(batchJobDao.listByJobIds(jobIdList, projectId));

        if (CollectionUtils.isNotEmpty(jobIdList)) {
            List<String> flowJobIds = batchJobDao.getWorkFlowJobId(jobIdList, SPECIAL_TASK_TYPES);
            if (CollectionUtils.isNotEmpty(flowJobIds)) {
                List<BatchJob> subJobs = batchJobDao.getSubJobsByFlowIds(flowJobIds);
                if (CollectionUtils.isNotEmpty(subJobs)) {
                    //将子任务实例加入
                    jobs.addAll(subJobs);
                }
            }
        }

        int stopCount = 0;
        List<BatchJob> needSendStopJobs = new ArrayList<>(jobIdList.size());
        if (CollectionUtils.isNotEmpty(jobs)) {
            List<Long> unSubmitJob = new ArrayList<>(jobs.size());
            for (BatchJob job : jobs) {
                //除了未提交的任务--其他都是发消息到engine端停止
                if (checkJobCanStop(job.getStatus())) {
                    stopCount++;
                    if (TaskStatus.UNSUBMIT.getStatus().equals(job.getStatus())) {
                        unSubmitJob.add(job.getId());
                    } else {
                        needSendStopJobs.add(job);
                    }
                }
            }
            //更新未提交任务状态
            if (CollectionUtils.isNotEmpty(unSubmitJob)) {
                batchJobDao.updateJobStatus(TaskStatus.CANCELED.getStatus(), unSubmitJob);
            }

            // 停止已提交的
            if (CollectionUtils.isNotEmpty(needSendStopJobs)) {
                jobStopSender.addStopJob(needSendStopJobs, dtuicTenantId, appType);
            }
        }
        return stopCount;
    }

    @Forbidden
    public String stopUnsubmitJob(BatchJob batchJob) {
        //还未提交的只需要将本地的任务设置为取消状态即可
        updateStatusByJobId(batchJob.getJobId(), TaskStatus.CANCELED.getStatus());
        return "success";
    }

    /**
     * FIXME 如果任务太多咋办--是否会导致发送的json太大？---- 是否修改为定时任务发送停止消息
     *
     * @param batchJobList
     * @return
     * @throws IOException
     */
    @Forbidden
    public String stopSubmittedJob(List<BatchJob> batchJobList, Long dtuicTenantId, Integer appType) throws Exception {

        if (CollectionUtils.isEmpty(batchJobList)) {
            return null;
        }

        JSONObject sendData = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for (BatchJob batchJob : batchJobList) {
            BatchTaskShade batchTask = batchTaskShadeDao.getOne(batchJob.getTaskId(), appType);
            //fix 任务被删除
            if (batchTask == null) {
                List<BatchTaskShade> deleteTask = batchTaskShadeService.getSimpleTaskRangeAllByIds(Lists.newArrayList(batchJob.getTaskId()));
                if (CollectionUtils.isEmpty(deleteTask)) {
                    continue;
                }
                batchTask = deleteTask.get(0);
            }
            Integer status = batchJob.getStatus();
            if (!TaskStatus.getCanStopStatus().contains(status)) {
                continue;
            }

            JSONObject params = new JSONObject();
            params.put("engineType", EngineType.getEngineName(batchTask.getEngineType()));
            params.put("taskId", batchJob.getJobId());
            params.put("computeType", batchTask.getComputeType());
            params.put("taskType", batchTask.getTaskType());
            //dtuicTenantId
            params.put("tenantId", dtuicTenantId);
            if (batchTask.getTaskType().equals(EJobType.DEEP_LEARNING.getVal())) {
                params.put("engineType", EngineType.Learning.getEngineName());
                params.put("taskType", EJobType.SPARK_PYTHON.getVal());
            } else if (batchTask.getTaskType().equals(EJobType.PYTHON.getVal()) || batchTask.getTaskType().equals(EJobType.SHELL.getVal())) {
                params.put("engineType", EngineType.DtScript.getEngineName());
                params.put("taskType", EJobType.SPARK_PYTHON.getVal());
            }

            jsonArray.add(params);
        }
        sendData.put("jobs", jsonArray);
        String sendJson = objMapper.writeValueAsString(sendData);
        engineSend.stopTask(sendJson, null, 2);
        return "";
    }


    /**
     * jobSize 在负载均衡时 区分 scheduleType（正常调度 和 补数据）
     */
    @Forbidden
    @Transactional(rollbackFor = Exception.class)
    public void insertJobList(Collection<ScheduleBatchJob> batchJobCollection, Integer scheduleType) {
        if (CollectionUtils.isEmpty(batchJobCollection)) {
            return;
        }

        Iterator<ScheduleBatchJob> batchJobIterator = batchJobCollection.iterator();

        //count%20 为一批
        //1: 批量插入BatchJob
        //2: 批量插入BatchJobJobList
        int count = 0;
        List<BatchJob> jobWaitForSave = Lists.newArrayList();
        List<BatchJobJob> jobJobWaitForSave = Lists.newArrayList();

        Map<String, Integer> nodeJobSize = computeJobSizeForNode(batchJobCollection.size(), scheduleType);
        //TODO
        for (Map.Entry<String, Integer> nodeJobSizeEntry : nodeJobSize.entrySet()) {
            String nodeAddress = nodeJobSizeEntry.getKey();
            int nodeSize = nodeJobSizeEntry.getValue();
            while (nodeSize > 0 && batchJobIterator.hasNext()) {
                nodeSize--;
                ScheduleBatchJob scheduleBatchJob = batchJobIterator.next();

                BatchJob batchJob = scheduleBatchJob.getBatchJob();
                batchJob.setNodeAddress(nodeAddress);

                jobWaitForSave.add(batchJob);
                jobJobWaitForSave.addAll(scheduleBatchJob.getBatchJobJobList());

                if (count++ % 20 == 0 || count == (batchJobCollection.size() - 1)) {
                    persisteJobs(jobWaitForSave, jobJobWaitForSave);
                }
            }
            //结束前persist一次，flush所有jobs
            persisteJobs(jobWaitForSave, jobJobWaitForSave);
        }
    }

    private Map<String, Integer> computeJobSizeForNode(int jobSize, int scheduleType) {
        Map<String, Integer> jobSizeInfo = ClusterQueueInfo.getInstance().computeQueueJobSize(scheduleType, jobSize);
        if (jobSizeInfo == null) {
            //if empty
            List<String> aliveNodes = zkService.getAliveBrokersChildren();
            jobSizeInfo = new HashMap<String, Integer>(aliveNodes.size());
            int size = jobSize / aliveNodes.size() + 1;
            for (String aliveNode : aliveNodes) {
                jobSizeInfo.put(aliveNode, size);
            }
        }
        return jobSizeInfo;
    }

    private void persisteJobs(List<BatchJob> jobWaitForSave, List<BatchJobJob> jobJobWaitForSave) {
        if (jobWaitForSave.size() > 0) {
            batchJobDao.batchInsert(jobWaitForSave);
            jobWaitForSave.clear();
        }
        if (jobJobWaitForSave.size() > 0) {
            batchJobJobService.batchInsert(jobJobWaitForSave);
            jobJobWaitForSave.clear();
        }
    }


    /**
     * 批量更新
     * FIXME 暂时一条条插入,最好优化成批量提交
     *
     * @param batchJobList
     */
    @Forbidden
    @Transactional
    public void updateJobListForRestart(List<ScheduleBatchJob> batchJobList) {

        for (ScheduleBatchJob scheduleBatchJob : batchJobList) {
            if (scheduleBatchJob.getBatchJob() != null) {
                batchJobDao.update(scheduleBatchJob.getBatchJob());
            }

            //batchEngineJobService.saveOrUpdateEngineJob(scheduleBatchJob.getBatchEngineJob(), null);
//            batchEngineJobService.resetJobForRestart(scheduleBatchJob.getBatchEngineJob().getId(), TaskStatus.UNSUBMIT.getStatus(), scheduleBatchJob.getBatchEngineJob().getVersionId());
        }
    }

    /**
     * 补数据的时候，选中什么业务日期，参数替换结果是业务日期+1天
     */
    @Transactional
    public String fillTaskData(@Param("taskJson") String taskJsonStr, @Param("fillName") String fillName,
                               @Param("fromDay") Long fromDay, @Param("toDay") Long toDay,
                               @Param("concreteStartTime") String beginTime, @Param("concreteEndTime") String endTime,
                               @Param("projectId") Long projectId, @Param("userId") Long userId,
                               @Param("tenantId") Long tenantId,
                               @Param("isRoot") Boolean isRoot, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId) throws Exception {

        taskJsonStr = StringUtils.isEmpty(taskJsonStr) ? "{}" : taskJsonStr;
        ArrayNode jsonNode = objMapper.readValue(taskJsonStr, ArrayNode.class);

        //计算从fromDay--toDay之间的天数
        DateTime fromDateTime = new DateTime(fromDay * 1000L);
        DateTime toDateTime = new DateTime(toDay * 1000L);

        String fromDayStr = fromDateTime.toString(dayFormatter);
        String toDayStr = toDateTime.toString(dayFormatter);

        DateTime currDateTime = DateTime.now();
        currDateTime = currDateTime.withTime(0, 0, 0, 0);

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
        boolean existsName = batchFillDataJobService.checkExistsName(fillName, projectId);
        if (existsName) {
            throw new RdosDefineException("补数据任务名称已存在", ErrorCode.NAME_ALREADY_EXIST);
        }

        Map<String, ScheduleBatchJob> addBatchMap = Maps.newLinkedHashMap();
        //存储fill_job name
        String currDayStr = currDateTime.toString(dayFormatter);
        BatchFillDataJob batchFillDataJob = batchFillDataJobService.saveData(fillName, tenantId, projectId, currDayStr, fromDayStr, toDayStr, userId, appType, dtuicTenantId);

        for (; !toDateTime.isBefore(fromDateTime); ) {
            try {
                DateTime cycTime = fromDateTime.plusDays(1);
                String triggerDay = cycTime.toString(DAY_PATTERN);
                Map<String, ScheduleBatchJob> result;
                if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
                    result = jobGraphBuilder.buildFillDataJobGraph(jsonNode, fillName, false, triggerDay, userId, beginTime, endTime, projectId, tenantId, isRoot, appType, batchFillDataJob.getId(),dtuicTenantId);
                } else {
                    result = jobGraphBuilder.buildFillDataJobGraph(jsonNode, fillName, false, triggerDay, userId, projectId, tenantId, isRoot, appType, batchFillDataJob.getId(),dtuicTenantId);
                }
                if (MapUtils.isEmpty(result)) {
                    continue;
                }

                insertJobList(result.values(), EScheduleType.FILL_DATA.getType());
                addBatchMap.putAll(result);

            } catch (RdosDefineException rde) {
                throw rde;
            } catch (Exception e) {
                logger.error("", e);
                throw new RdosDefineException("build fill job error:" + e.getMessage(), ErrorCode.SERVER_EXCEPTION);
            } finally {
                fromDateTime = fromDateTime.plusDays(1);
            }
        }

        if (addBatchMap.size() == 0) {
            throw new RdosDefineException("请检查所选择的具体日期范围", ErrorCode.NO_FILLDATA_TASK_IS_GENERATE);
        }

        List<Long> needMonitorTaskIdList = batchAlarmService.getAllNeedMonitorTaskId(projectId);
        for (ScheduleBatchJob batchJob : addBatchMap.values()) {
            if (needMonitorTaskIdList.contains(batchJob.getTaskId())) {
                Integer status = batchJob.getStatus();
                BatchJob job = batchJobDao.getByJobId(batchJob.getJobId(), Deleted.NORMAL.getStatus());
                if (Objects.isNull(job)) {
                    logger.info("fill task data job id {} not found job", batchJob.getJobId());
                    continue;
                }
                job.setStatus(status);
                batchJobAlarmService.saveBatchJobAlarm(job);
            }
        }
        return fillName;
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
     * @param userId
     * @param currentPage
     * @param pageSize
     * @param tenantId
     * @return
     */
    public PageResult<BatchFillDataJobPreViewVO> getFillDataJobInfoPreview(@Param("jobName") String jobName, @Param("runDay") Long runDay,
                                                                           @Param("bizStartDay") Long bizStartDay, @Param("bizEndDay") Long bizEndDay, @Param("dutyUserId") Long dutyUserId,
                                                                           @Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("user") Integer userId,
                                                                           @Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize, @Param("tenantId") Long tenantId) {
        final List<BatchTaskShade> taskList;
        BatchJobDTO batchJobDTO = new BatchJobDTO();
        //是否需要关联task表查询
        boolean needQueryTask = false;
        if (!Strings.isNullOrEmpty(jobName)) {
            taskList = batchTaskShadeService.getTasksByName(projectId, jobName, appType);
            if (taskList.size() == 0) {
                return PageResult.EMPTY_PAGE_RESULT;
            } else {
                needQueryTask = true;
                batchJobDTO.setTaskIds(taskList.stream().map(BatchTaskShade::getTaskId).collect(Collectors.toList()));
            }
        }


        if (runDay != null) {
            batchJobDTO.setStartGmtCreate(new Timestamp(runDay * 1000L));
        }

        this.setBizDay(batchJobDTO, bizStartDay, bizEndDay, tenantId, projectId);

        if (dutyUserId != null) {
            batchJobDTO.setCreateUserId(dutyUserId);
        }

        batchJobDTO.setProjectId(projectId);
        batchJobDTO.setType(EScheduleType.FILL_DATA.getType());
        batchJobDTO.setNeedQuerySonNode(true);
        batchJobDTO.setAppType(appType);
        batchJobDTO.setOwnerUserId(dutyUserId);

        PageQuery pageQuery = new PageQuery(currentPage, pageSize, "gmt_create", Sort.DESC.name());
        pageQuery.setModel(batchJobDTO);

        if (Objects.nonNull(userId) || Objects.nonNull(dutyUserId)) {
            needQueryTask = true;
        }


        List<Long> fillIdList = null;
        if (needQueryTask) {
            fillIdList = batchJobDao.listFillIdList(pageQuery);
        } else {
            fillIdList = batchJobDao.listFillIdListWithOutTask(pageQuery);
        }

        List<BatchFillDataJob> fillJobList = null;
        if (CollectionUtils.isNotEmpty(fillIdList)) {
            //根据补数据名称查询出记录
            fillJobList = batchFillDataJobDao.getFillJobList(fillIdList, projectId, tenantId);
        } else {
            fillJobList = new ArrayList<>();
        }
        //内存中按照时间排序
        if (CollectionUtils.isNotEmpty(fillJobList)) {
            fillJobList = fillJobList.stream().sorted((j1, j2) -> {
                return j2.getGmtCreate().compareTo(j1.getGmtCreate());

            }).collect(Collectors.toList());
        }
        List<Map<String, Long>> statistics = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(fillJobList)) {
            statistics = batchJobDao.countByFillDataAllStatus(fillJobList.stream().map(BatchFillDataJob::getId).collect(Collectors.toList()), projectId, tenantId);
        }

        List<BatchFillDataJobPreViewVO> resultContent = Lists.newArrayList();
        for (BatchFillDataJob fillJob : fillJobList) {
            BatchFillDataJobPreViewVO preViewVO = new BatchFillDataJobPreViewVO();
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

        int totalCount = 0;
        if (needQueryTask) {
            totalCount = batchJobDao.countFillJobNameDistinct(batchJobDTO);
        } else {
            totalCount = batchJobDao.countFillJobNameDistinctWithOutTask(batchJobDTO);
        }

        return new PageResult(resultContent, totalCount, pageQuery);
    }

    /**
     * 补数据的执行进度
     *
     * @param statistics
     * @param preViewVO
     */
    private void setFillDataJobProgress(List<Map<String, Long>> statistics, BatchFillDataJobPreViewVO
            preViewVO) {
        Map<Long, Long> statisticsMap = new HashMap<>();
        for (Map<String, Long> statistic : statistics) {
            Object fillId = statistic.get("fillId");
            long id = preViewVO.getId().longValue();
            if (((Integer) fillId).longValue() == id) {
                statisticsMap.put(statistic.get("status"), statistic.get("count"));
            }
        }

        Map<Integer, Long> resultMap = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : TaskStatusConstrant.STATUS.entrySet()) {
            int showStatus = entry.getKey();
            long sum = 0;
            for (Integer value : entry.getValue()) {
                Long statusSum = statisticsMap.get(value);
                sum += statusSum == null ? 0L : statusSum;
            }
            resultMap.put(showStatus, sum);
        }

        Long unSubmit = resultMap.get(TaskStatus.UNSUBMIT.getStatus()) == null ? 0L : resultMap.get(TaskStatus.UNSUBMIT.getStatus());
        Long running = resultMap.get(TaskStatus.RUNNING.getStatus()) == null ? 0L : resultMap.get(TaskStatus.RUNNING.getStatus());
        Long finished = resultMap.get(TaskStatus.FINISHED.getStatus()) == null ? 0L : resultMap.get(TaskStatus.FINISHED.getStatus());
        Long failed = resultMap.get(TaskStatus.FAILED.getStatus()) == null ? 0L : resultMap.get(TaskStatus.FAILED.getStatus());
        Long waitEngine = resultMap.get(TaskStatus.WAITENGINE.getStatus()) == null ? 0L : resultMap.get(TaskStatus.WAITENGINE.getStatus());
        Long submitting = resultMap.get(TaskStatus.SUBMITTING.getStatus()) == null ? 0L : resultMap.get(TaskStatus.SUBMITTING.getStatus());
        Long canceled = resultMap.get(TaskStatus.CANCELED.getStatus()) == null ? 0L : resultMap.get(TaskStatus.CANCELED.getStatus());
        Long frozen = resultMap.get(TaskStatus.FROZEN.getStatus()) == null ? 0L : resultMap.get(TaskStatus.FROZEN.getStatus());

        preViewVO.setFinishedJobSum(finished);
        preViewVO.setAllJobSum(unSubmit + running + finished + failed + waitEngine + submitting + canceled + frozen);
        preViewVO.setDoneJobSum(failed + canceled + frozen + finished);
    }

    /**
     * @param fillJobName
     * @return
     */
    @Deprecated
    public PageResult<BatchFillDataJobDetailVO> getFillDataDetailInfoOld(QueryJobDTO vo,
                                                                         @Param("fillJobName") String fillJobName,
                                                                         @Param("dutyUserId") Long dutyUserId) throws Exception {
        if (Strings.isNullOrEmpty(fillJobName)) {
            throw new RdosDefineException("(补数据名称不能为空)", ErrorCode.INVALID_PARAMETERS);
        }
        vo.setSplitFiledFlag(true);
        BatchJobDTO batchJobDTO = this.createQuery(vo);
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
                List<Integer> statusList = TaskStatusConstrant.STATUS_FAILED_DETAIL.get(MathUtil.getIntegerVal(status));
                statues.addAll(statusList);
            }

            batchJobDTO.setJobStatuses(statues);
        }
        batchJobDTO.setTaskTypes(convertStringToList(vo.getTaskType()));

        PageQuery pageQuery = new PageQuery(vo.getCurrentPage(), vo.getPageSize(), "business_date", Sort.ASC.name());
        pageQuery.setModel(batchJobDTO);

        // 先查找符合条件的子节点
        batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Only_Workflow_SubNodes.getType());
        batchJobDTO.setPageQuery(false);
        List<BatchJob> subBatchJobs = batchJobDao.generalQuery(pageQuery);
        Set<String> matchFlowJobIds = subBatchJobs.stream().map(BatchJob::getFlowJobId).collect(Collectors.toSet());
        List<String> subJobIds = subBatchJobs.stream().map(BatchJob::getJobId).collect(Collectors.toList());

        // 然后查找符合条件的其它任务
        batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
        batchJobDTO.setJobIds(matchFlowJobIds);

        //todo 优化查询次数
        batchJobDTO.setPageQuery(true);

        BatchFillDataJobDetailVO batchFillDataJobDetailVO = new BatchFillDataJobDetailVO();
        batchFillDataJobDetailVO.setFillDataJobName(fillJobName);

        batchJobDTO.setNeedQuerySonNode(CollectionUtils.isNotEmpty(batchJobDTO.getTaskTypes()) && batchJobDTO.getTaskTypes().contains(EJobType.WORK_FLOW.getVal()));
        int totalCount = batchJobDao.generalCount(batchJobDTO);
        if (totalCount > 0) {
            List<BatchJob> batchJobs = batchJobDao.generalQuery(pageQuery);

            Map<Long, BatchTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(batchJobs);


            for (BatchJob batchJob : batchJobs) {
                batchFillDataJobDetailVO.addRecord(transferBatchJob2FillDataRecord(batchJob, null, taskShadeMap));
            }

            dealFlowWorkFillDataRecord(batchFillDataJobDetailVO.getRecordList(), subJobIds);
        } else if (subBatchJobs.size() > 0) {
            batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Only_Workflow_SubNodes.getType());
            batchJobDTO.setPageQuery(true);
            batchJobDTO.setJobIds(null);
            batchJobDTO.setFlowJobId(null);
            //【2】 只查询工作流子节点
            totalCount = batchJobDao.generalCount(batchJobDTO);
            if (totalCount > 0) {
                subBatchJobs = batchJobDao.generalQuery(pageQuery);

                Map<Long, BatchTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(subBatchJobs);
                for (BatchJob batchJob : subBatchJobs) {
                    batchFillDataJobDetailVO.addRecord(transferBatchJob2FillDataRecord(batchJob, null, taskShadeMap));
                }
            }
        }

        return new PageResult(batchFillDataJobDetailVO, totalCount, pageQuery);
    }

    public PageResult<BatchFillDataJobDetailVO> getFillDataDetailInfo(@Param("vo") String queryJobDTO,
                                                                      @Param("flowJobIdList") List<String> flowJobIdList,
                                                                      @Param("fillJobName") String fillJobName,
                                                                      @Param("dutyUserId") Long dutyUserId, @Param("searchType") String searchType) throws Exception {
        if (Strings.isNullOrEmpty(fillJobName)) {
            throw new RdosDefineException("(补数据名称不能为空)", ErrorCode.INVALID_PARAMETERS);
        }

        QueryJobDTO vo = JSONObject.parseObject(queryJobDTO, QueryJobDTO.class);
        vo.setSplitFiledFlag(true);
        BatchJobDTO batchJobDTO = this.createQuery(vo);
        batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
        batchJobDTO.setFillDataJobName(fillJobName);
        batchJobDTO.setNeedQuerySonNode(true);
        //跨租户、项目条件
        batchJobDTO.setProjectId(null);
        batchJobDTO.setTenantId(null);

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
                List<Integer> statusList = TaskStatusConstrant.STATUS_FAILED_DETAIL.get(MathUtil.getIntegerVal(status));
                statues.addAll(statusList);
            }

            batchJobDTO.setJobStatuses(statues);
        }
        batchJobDTO.setTaskTypes(convertStringToList(vo.getTaskType()));

        PageQuery pageQuery = new PageQuery(vo.getCurrentPage(), vo.getPageSize());
        setPageQueryDefaultOrder(pageQuery, batchJobDTO);

        if (StringUtils.isEmpty(searchType) || "fuzzy".equalsIgnoreCase(searchType)) {
            batchJobDTO.setSearchType(1);
        } else if ("precise".equalsIgnoreCase(searchType)) {
            batchJobDTO.setSearchType(2);
        } else if ("front".equalsIgnoreCase(searchType)) {
            batchJobDTO.setSearchType(3);
        } else if ("tail".equalsIgnoreCase(searchType)) {
            batchJobDTO.setSearchType(4);
        } else {
            batchJobDTO.setSearchType(1);
        }
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

        BatchFillDataJobDetailVO batchFillDataJobDetailVO = new BatchFillDataJobDetailVO();
        batchFillDataJobDetailVO.setFillDataJobName(fillJobName);


        Integer totalCount = batchJobDao.countByFillData(batchJobDTO);
        if (totalCount > 0) {

            List<BatchJob> batchJobListWithFillData = batchJobDao.queryFillData(pageQuery);

            Map<Long, BatchTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(batchJobListWithFillData);
            if (CollectionUtils.isNotEmpty(batchJobListWithFillData)) {
                for (BatchJob job : batchJobListWithFillData) {
                    batchFillDataJobDetailVO.addRecord(transferBatchJob2FillDataRecord(job, flowJobIdList, taskShadeMap));
                }
                dealFlowWorkSubJobsInFillData(batchFillDataJobDetailVO.getRecordList());
            }
        }

        return new PageResult(batchFillDataJobDetailVO, totalCount, pageQuery);
    }

    /**
     * 获取补数据实例工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    public BatchFillDataJobDetailVO.FillDataRecord getRelatedJobsForFillData(@Param("jobId") String jobId, @Param("vo") String query,
                                                                             @Param("fillJobName") String fillJobName) throws Exception {
        QueryJobDTO vo = JSONObject.parseObject(query, QueryJobDTO.class);
        BatchJob batchJob = batchJobDao.getByJobId(jobId, Deleted.NORMAL.getStatus());
        Map<String, BatchEngineJob> engineJobMap = Maps.newHashMap();
        Map<Long, BatchTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(Arrays.asList(batchJob));

        BatchFillDataJobDetailVO.FillDataRecord fillDataRecord = transferBatchJob2FillDataRecord(batchJob, null, taskShadeMap);

        vo.setSplitFiledFlag(true);
        //除去任务类型中的工作流类型的条件，用于展示下游节点
        if (StringUtils.isNotBlank(vo.getTaskType())) {
            vo.setTaskType(vo.getTaskType().replace(String.valueOf(EJobType.WORK_FLOW.getVal()), ""));
        }
        BatchJobDTO batchJobDTO = this.createQuery(vo);
        batchJobDTO.setFillDataJobName(fillJobName);

        if (batchJob != null) {
            if (EJobType.WORK_FLOW.getVal().intValue() == fillDataRecord.getTaskType()) {
                fillDataRecord.setRelatedRecords(getRelatedJobsForFillDataByQueryDTO(batchJobDTO, vo, jobId, engineJobMap, taskShadeMap));
            }
            return fillDataRecord;
        } else {
            throw new RdosDefineException("该实例对象不存在");
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
    private List<BatchFillDataJobDetailVO.FillDataRecord> getOnlyRelatedJobsForFillData(String jobId, Map<Long, BatchTaskForFillDataDTO> taskShadeMap) throws Exception {
        taskShadeMap = Optional.ofNullable(taskShadeMap).orElse(Maps.newHashMap());

        List<BatchJob> subJobs = batchJobDao.getSubJobsByFlowIds(Lists.newArrayList(jobId));
        taskShadeMap.putAll(this.prepare(subJobs));
        List<BatchFillDataJobDetailVO.FillDataRecord> fillDataRecord_subNodes = new ArrayList<>();
        for (BatchJob subJob : subJobs) {
            BatchFillDataJobDetailVO.FillDataRecord subNode = transferBatchJob2FillDataRecord(subJob, null, taskShadeMap);
            fillDataRecord_subNodes.add(subNode);
        }
        return fillDataRecord_subNodes;
    }

    private List<BatchFillDataJobDetailVO.FillDataRecord> getRelatedJobsForFillDataByQueryDTO(BatchJobDTO queryDTO, QueryJobDTO vo, String jobId, Map<String, BatchEngineJob> engineJobMap,
                                                                                              Map<Long, BatchTaskForFillDataDTO> taskShadeMap) throws Exception {

        queryDTO.setPageQuery(false);
        queryDTO.setFlowJobId(jobId);
        queryDTO.setNeedQuerySonNode(true);
        PageQuery<BatchJobDTO> pageQuery = new PageQuery<>(vo.getCurrentPage(), vo.getPageSize(), "gmt_modified", Sort.DESC.name());
        pageQuery.setModel(queryDTO);
        List<BatchJob> subJobs = batchJobDao.generalQuery(pageQuery);
        taskShadeMap = this.prepareForFillDataDetailInfo(subJobs);

        List<BatchFillDataJobDetailVO.FillDataRecord> fillDataRecord_subNodes = new ArrayList<>();
        for (BatchJob subJob : subJobs) {
            BatchFillDataJobDetailVO.FillDataRecord subNode = transferBatchJob2FillDataRecord(subJob, null, taskShadeMap);
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
    private void setPageQueryDefaultOrder(PageQuery pageQuery, BatchJobDTO batchJobDTO) {
        if (StringUtils.isBlank(batchJobDTO.getExecTimeSort()) &&
                StringUtils.isBlank(batchJobDTO.getExecStartSort()) &&
                StringUtils.isBlank(batchJobDTO.getExecEndSort()) &&
                StringUtils.isBlank(batchJobDTO.getCycSort()) &&
                StringUtils.isBlank(batchJobDTO.getBusinessDateSort())) {
            pageQuery.setOrderBy(BUSINESS_DATE);
            pageQuery.setSort(Sort.ASC.name());
        }
    }


    private void dealFlowWorkSubJobsInFillData(List<BatchFillDataJobDetailVO.FillDataRecord> vos) throws Exception {
        Map<String, BatchFillDataJobDetailVO.FillDataRecord> record = Maps.newHashMap();
        Map<String, Integer> voIndex = Maps.newHashMap();
        vos.forEach(job -> voIndex.put(job.getJobId(), vos.indexOf(job)));
        List<BatchFillDataJobDetailVO.FillDataRecord> copy = Lists.newArrayList(vos);
        Iterator<BatchFillDataJobDetailVO.FillDataRecord> iterator = vos.iterator();
        while (iterator.hasNext()) {
            BatchFillDataJobDetailVO.FillDataRecord jobVO = iterator.next();
            String flowJobId = jobVO.getFlowJobId();
            if (!"0".equals(flowJobId)) {
                if (record.containsKey(flowJobId)) {
                    BatchFillDataJobDetailVO.FillDataRecord flowVo = record.get(flowJobId);
                    flowVo.getRelatedRecords().add(jobVO);
                    iterator.remove();
                } else {
                    BatchFillDataJobDetailVO.FillDataRecord flowVO;
                    if (voIndex.containsKey(flowJobId)) {
                        flowVO = copy.get(voIndex.get(flowJobId));
                        flowVO.setRelatedRecords(Lists.newArrayList(jobVO));
                        iterator.remove();
                    } else {
                        BatchJob flow = batchJobDao.getByJobId(flowJobId, Deleted.NORMAL.getStatus());
                        if (flow == null) {
                            continue;
                        }
                        Map<Long, BatchTaskForFillDataDTO> shadeMap = this.prepare(Lists.newArrayList(flow));
                        flowVO = transferBatchJob2FillDataRecord(flow, null, shadeMap);
                        flowVO.setRelatedRecords(Lists.newArrayList(jobVO));
                        vos.set(vos.indexOf(jobVO), flowVO);
                    }
                    record.put(flowJobId, flowVO);
                }
            }
        }
    }

    private Map<Long, BatchTaskForFillDataDTO> prepareForFillDataDetailInfo(List<BatchJob> batchJobs) throws Exception {
        if (CollectionUtils.isEmpty(batchJobs)) {
            return new HashMap<>();
        }
        Set<Long> taskIdSet = batchJobs.stream().map(BatchJob::getTaskId).collect(Collectors.toSet());
        return batchTaskShadeDao.listSimpleTaskByTaskIds(taskIdSet, null).stream().collect(Collectors.toMap(BatchTaskForFillDataDTO::getTaskId, batchTaskForFillDataDTO -> batchTaskForFillDataDTO));

    }

    private void dealFlowWorkFillDataRecord
            (List<BatchFillDataJobDetailVO.FillDataRecord> records, List<String> subJobIds) throws Exception {
        if (CollectionUtils.isNotEmpty(records)) {
            List<BatchJob> allSubJobs = new ArrayList<>();
            Iterator<BatchFillDataJobDetailVO.FillDataRecord> it = records.iterator();
            while (it.hasNext()) {
                BatchFillDataJobDetailVO.FillDataRecord record = it.next();
                Integer type = record.getTaskType();
                if (EJobType.WORK_FLOW.getVal().intValue() == type) {
                    String jobId = record.getJobId();
                    List<BatchJob> subJobs = batchJobDao.getSubJobsByFlowIds(Lists.newArrayList(jobId));
                    allSubJobs.addAll(subJobs);
                    if (CollectionUtils.isNotEmpty(subJobs)) {
                        Map<Long, BatchTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(subJobs);
                        List<BatchFillDataJobDetailVO.FillDataRecord> subList = Lists.newArrayList();
                        for (BatchJob subJob : subJobs) {
                            if (subJobIds.contains(subJob.getJobId())) {
                                subList.add(transferBatchJob2FillDataRecord(subJob, null, taskShadeMap));
                            }
                        }
                        record.setRelatedRecords(subList);
                    }
                }
            }

            // 这里处理工作流里的任务
            Iterator<BatchFillDataJobDetailVO.FillDataRecord> itInternal = records.iterator();
            for (BatchJob subJob : allSubJobs) {
                while (itInternal.hasNext()) {
                    BatchFillDataJobDetailVO.FillDataRecord rec = itInternal.next();
                    if (subJob.getJobId().equalsIgnoreCase(rec.getJobId())) {
                        itInternal.remove();
                        break;
                    }
                }
            }
        }
    }

    private void dealFlowWorkSubFillDataRecord(List<BatchFillDataJobDetailVO.FillDataRecord> records) throws
            Exception {
        Map<String, BatchFillDataJobDetailVO.FillDataRecord> temp = Maps.newHashMap();
        Map<String, Integer> indexMap = Maps.newHashMap();
        records.forEach(r -> indexMap.put(r.getJobId(), records.indexOf(r)));
        Iterator<BatchFillDataJobDetailVO.FillDataRecord> iterator = records.iterator();
        List<BatchFillDataJobDetailVO.FillDataRecord> recordsCopy = new ArrayList<>(records);
        while (iterator.hasNext()) {
            BatchFillDataJobDetailVO.FillDataRecord record = iterator.next();
            String flowJobId = record.getFlowJobId();
            if (!"0".equals(flowJobId)) {
                if (temp.containsKey(flowJobId)) {
                    BatchFillDataJobDetailVO.FillDataRecord flowRecord = temp.get(flowJobId);
                    flowRecord.getRelatedRecords().add(record);
                    iterator.remove();
                } else {
                    BatchFillDataJobDetailVO.FillDataRecord flowRecord;
                    if (indexMap.containsKey(flowJobId)) {
                        flowRecord = recordsCopy.get(indexMap.get(flowJobId));
                        flowRecord.setRelatedRecords(Lists.newArrayList(record));
                        iterator.remove();
                    } else {
                        BatchJob flowJob = batchJobDao.getByJobId(flowJobId, Deleted.NORMAL.getStatus());
                        if (flowJob == null) {
                            continue;
                        }
                        Map<Long, BatchTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(Lists.newArrayList(flowJob));
                        flowRecord = transferBatchJob2FillDataRecord(flowJob, null, taskShadeMap);
                        flowRecord.setRelatedRecords(Lists.newArrayList(record));
                        records.set(recordsCopy.indexOf(record), flowRecord);
                    }
                    temp.put(flowJobId, flowRecord);
                }
            }
        }
    }

    /**
     * 转化batchjob任务为补数据界面所需格式
     *
     * @param batchJob
     * @param flowJobIdList 展开特定工作流子节点
     * @param taskShadeMap
     * @return
     * @throws Exception
     */
    private BatchFillDataJobDetailVO.FillDataRecord transferBatchJob2FillDataRecord(BatchJob batchJob, List<String> flowJobIdList,
                                                                                    Map<Long, BatchTaskForFillDataDTO> taskShadeMap) throws Exception {
        String bizDayVO = batchJob.getBusinessDate();
        bizDayVO = bizDayVO.substring(0, 4) + "-" + bizDayVO.substring(4, 6) + "-" + bizDayVO.substring(6, 8);
        int status = batchJob.getStatus();

        String cycTimeVO = DateUtil.addTimeSplit(batchJob.getCycTime());
        String exeStartTimeVO = null;
        Timestamp exeStartTime = batchJob.getExecStartTime();
        if (exeStartTime != null) {
            exeStartTimeVO = timeFormatter.print(exeStartTime.getTime());
        }

        BatchTaskForFillDataDTO taskShade = taskShadeMap.get(batchJob.getTaskId());
        Integer taskType = 0;
        if (taskShade != null) {
            taskType = taskShade.getTaskType();
        }

        BatchTaskVO batchTaskVO = new BatchTaskVO();

        BatchFillDataJobDetailVO.FillDataRecord record = new BatchFillDataJobDetailVO.FillDataRecord(batchJob.getId(), bizDayVO, taskShade.getName(),
                taskType, status, cycTimeVO, exeStartTimeVO, batchJob.getExecTime(), null);

        record.setJobId(batchJob.getJobId());
        record.setFlowJobId(batchJob.getFlowJobId());
        record.setIsRestart(batchJob.getIsRestart());

        // 判断taskType为2的，查出脏数据量，判断增加标识
        if (taskType == 2) {
            //fixme redmine[16670]暂时关闭脏数据读取，后续版本优化
            /*BatchServerLogVO batchServerLogVO = batchServerLogService.getLogsByJobId(batchJob.getJobId(),-1);
            if (batchServerLogVO.getSyncJobInfo() != null) {
                Float dirtyPercent = batchServerLogVO.getSyncJobInfo().getDirtyPercent();
                if (dirtyPercent != null && dirtyPercent > 0.0f) {
                    record.setIsDirty(1);
                }
            }*/
        }

        //展开特定工作流子节点
        if (EJobType.WORK_FLOW.getVal().equals(taskType) &&
                CollectionUtils.isNotEmpty(flowJobIdList) &&
                flowJobIdList.contains(batchJob.getJobId())) {
            record.setRelatedRecords(getOnlyRelatedJobsForFillData(batchJob.getJobId(), taskShadeMap));
        }

        batchTaskVO.setId(taskShade.getTaskId());
        batchTaskVO.setGmtModified(taskShade.getGmtModified());
        batchTaskVO.setName(taskShade.getName());
        batchTaskVO.setIsDeleted(taskShade.getIsDeleted());
        batchTaskVO.setProjectId(taskShade.getProjectId());
        batchTaskVO.setOwnerUserId(taskShade.getOwnerUserId());
        batchTaskVO.setCreateUserId(taskShade.getCreateUserId());
        record.setBatchTask(batchTaskVO);
        record.setRetryNum(batchJob.getRetryNum());
        return record;
    }


    /**
     * 获取重跑的数据节点信息
     *
     * @param batchJob
     * @param isOnlyNextChild
     * @return
     */
    public List<RestartJobVO> getRestartChildJob(@Param("jobKey") String jobKey, @Param("taskId") Long parentTaskId, @Param("isOnlyNextChild") boolean isOnlyNextChild) {

        List<BatchJobJob> batchJobJobList = batchJobJobService.getJobChild(jobKey);
        List<String> jobKeyList = Lists.newArrayList();
        List<RestartJobVO> batchJobList = Lists.newArrayList();

        String parentJobDayStr = getJobTriggerTimeFromJobKey(jobKey);
        if (Strings.isNullOrEmpty(parentJobDayStr)) {
            return batchJobList;
        }

        for (BatchJobJob batchJobJob : batchJobJobList) {
            //排除自依赖
            String childJobKey = batchJobJob.getJobKey();
            if (parentTaskId.equals(getTaskIdFromJobKey(childJobKey))) {
                continue;
            }

            //排除不是同一天执行的
            if (!parentJobDayStr.equals(getJobTriggerTimeFromJobKey(childJobKey))) {
                continue;
            }

            jobKeyList.add(batchJobJob.getJobKey());
        }

        if (CollectionUtils.isEmpty(jobKeyList)) {
            return batchJobList;
        }

        List<BatchJob> jobList = batchJobDao.listJobByJobKeys(jobKeyList);

        for (BatchJob childBatchJob : jobList) {

            //判断job 对应的task是否被删除
            BatchTaskShade jobRefTask = batchTaskShadeService.getBatchTaskById(childBatchJob.getTaskId(), childBatchJob.getAppType());
            if (jobRefTask == null || Deleted.DELETED.getStatus().equals(jobRefTask.getIsDeleted())) {
                continue;
            }
            Integer jobStatus = childBatchJob.getStatus();
            jobStatus = jobStatus == null ? TaskStatus.UNSUBMIT.getStatus() : jobStatus;

            String taskName = jobRefTask == null ? null : jobRefTask.getName();

            RestartJobVO restartJobVO = new RestartJobVO();
            restartJobVO.setJobId(childBatchJob.getId());
            restartJobVO.setJobKey(childBatchJob.getJobKey());
            restartJobVO.setJobStatus(jobStatus);
            restartJobVO.setCycTime(childBatchJob.getCycTime());
            restartJobVO.setTaskType(childBatchJob.getTaskType());
            restartJobVO.setTaskName(taskName);
            restartJobVO.setTaskId(childBatchJob.getTaskId());

            if (!isOnlyNextChild) {
                restartJobVO.setChilds(getRestartChildJob(childBatchJob.getJobKey(), childBatchJob.getTaskId(), isOnlyNextChild));
            }

            batchJobList.add(restartJobVO);
        }

        return batchJobList;
    }


    @Forbidden
    public Long getTaskIdFromJobKey(String jobKey) {
        String[] strings = jobKey.split("_");
        if (strings.length < 2) {
            logger.error("it's not a legal job key, str is {}.", jobKey);
            return -1L;
        }

        String taskId = strings[strings.length - 2];
        try {
            return MathUtil.getLongVal(taskId);
        } catch (Exception e) {
            logger.error("it's not a legal job key, str is {}.", jobKey);
            return -1L;
        }
    }

    @Forbidden
    public String getJobTriggerTimeFromJobKey(String jobKey) {
        String[] strings = jobKey.split("_");
        if (strings.length < 1) {
            logger.error("it's not a legal job key, str is {}.", jobKey);
            return "";
        }

        String timeStr = strings[strings.length - 1];
        if (timeStr.length() < 8) {
            logger.error("it's not a legal job key, str is {}.", jobKey);
            return "";
        }

        return timeStr.substring(0, 8);
    }

    @Forbidden
    public List<Long> getJobByTaskIdAndStatus(Long taskId, List<Integer> statusList, Integer appType) {
        return batchJobDao.listIdByTaskIdAndStatus(taskId, statusList, appType);
    }

    private boolean checkJobCanStop(Integer status) {
        if (status == null) {
            return true;
        }

        return TaskStatus.getCanStopStatus().contains(status);
    }

    private Integer parseSyncChannel(JSONObject syncJob) {
        //解析出并发度---sync 消耗资源是: 并发度*1
        try {
            JSONObject jobJson = syncJob.getJSONObject("job").getJSONObject("job");
            JSONObject settingJson = jobJson.getJSONObject("setting");
            JSONObject speedJson = settingJson.getJSONObject("speed");
            return speedJson.getInteger("channel");
        } catch (Exception e) {
            //默认1
            return 1;
        }

    }

    public String replaceSyncParll(String taskParams, int parallelism) throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(taskParams.getBytes("UTF-8")));
        properties.put("mr.job.parallelism", parallelism);
        StringBuilder sb = new StringBuilder("");
        for (Map.Entry<Object, Object> tmp : properties.entrySet()) {
            sb.append(tmp.getKey())
                    .append(" = ")
                    .append(tmp.getValue())
                    .append(LINE_SEPARATOR);
        }
        return sb.toString();
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

    public String uploadSqlTextToHdfs(Long dtuicTenantId, String content, Integer taskType, String taskName, Long
            tenantId, Long projectId) {
        String hdfsPath = null;
        try {
            // shell任务，创建脚本文件
            String fileName = null;
            if (taskType.equals(EJobType.SHELL.getVal())) {
                fileName = String.format("shell_%s_%s_%s_%s.sh", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EJobType.PYTHON.getVal()) ||
                    taskType.equals(EJobType.NOTEBOOK.getVal())) {
                fileName = String.format("python_%s_%s_%s_%s.py", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EJobType.DEEP_LEARNING.getVal())) {
                fileName = String.format("learning_%s_%s_%s_%s.py", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EJobType.SPARK_PYTHON.getVal())) {
                fileName = String.format("pyspark_%s_%s_%s_%s.py", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            }

            if (fileName != null) {
                hdfsPath = env.getHdfsBatchPath() + fileName;
                if (taskType.equals(EJobType.SHELL.getVal())) {
                    content = content.replaceAll("\r\n", System.getProperty("line.separator"));
                }
                HdfsOperator.uploadInputStreamToHdfs(HadoopConf.getConfiguration(dtuicTenantId), content.getBytes(), hdfsPath);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new RdosDefineException("Update task to HDFS failure:" + e.getMessage());
        }

        return HadoopConf.getDefaultFs(dtuicTenantId) + hdfsPath;
    }

    /**
     * 根据工作流id获取子任务信息与任务状态
     *
     * @param jobId
     * @return
     */
    @Forbidden
    public List<BatchJob> getSubJobsAndStatusByFlowId(String jobId) {
        return batchJobDao.getSubJobsAndStatusByFlowId(jobId);
    }

    /**
     * 获取工作流顶级子节点
     *
     * @param jobId 工作流jobId
     * @return
     */
    @Forbidden
    public BatchJob getWorkFlowTopNode(String jobId) {
        return batchJobDao.getWorkFlowTopNode(jobId);
    }

    public Integer getPySparkOperateModel(String exeArgs) {
        Integer operateModel = TaskOperateType.RESOURCE.getType();
        if (exeArgs != null) {
            Integer model = JSONObject.parseObject(exeArgs).getInteger("operateModel");
            operateModel = model != null ? model : TaskOperateType.RESOURCE.getType();
        }
        return operateModel;
    }

    public List<String> listJobIdByTaskNameAndStatusList(@Param("taskName") String taskName, @Param("statusList") List<Integer> statusList, @Param("projectId") Long projectId) {
        BatchTaskShade task = batchTaskShadeService.getByName(projectId, taskName);
        if (task != null) {
            List<String> jobIdList = batchJobDao.listJobIdByTaskIdAndStatus(task.getId(), statusList);
            return jobIdList;
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
    public Map<String, BatchJob> getLabTaskRelationMap(@Param("jobIdList") List<String> jobIdList, @Param("projectId") Long projectId) {
        List<BatchJob> batchJobs = batchJobDao.listByJobIdList(jobIdList, projectId);
        if (CollectionUtils.isNotEmpty(batchJobs)) {
            Map<String, BatchJob> jobMap = new HashMap<>();
            for (BatchJob batchJob : batchJobs) {
                BatchJob flowJob = batchJobDao.getByJobId(batchJob.getFlowJobId(), Deleted.NORMAL.getStatus());
                jobMap.put(batchJob.getJobId(), flowJob);
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
    public List<Map<String, Object>> statisticsTaskRecentInfo(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("projectId") Long projectId, @Param("count") Integer count) {
        return batchJobDao.listTaskExeInfo(taskId, projectId, count, appType);

    }


    /**
     * 批量更新
     *
     * @param jobs
     */
    public Integer BatchJobsBatchUpdate(@Param("jobs") String jobs) {
        if (StringUtils.isBlank(jobs)) {
            return 0;
        }
        List<BatchJob> batchJobs = JSONObject.parseArray(jobs, BatchJob.class);
        if (CollectionUtils.isEmpty(batchJobs)) {
            return 0;
        }
        Integer updateSize = 0;
        for (BatchJob job : batchJobs) {
            if(Objects.nonNull(job.getStatus())){
                //更新状态 日志信息也要更新
                job.setLogInfo("");
            }
            updateSize += batchJobDao.update(job);

        }
        return updateSize;
    }


    public BatchJob getById(@Param("id") Long id) {
        return batchJobDao.getOne(id);
    }

    public BatchJob getByJobId(@Param("jobId") String jobId, @Param("isDeleted") Integer isDeleted) {
        return batchJobDao.getByJobId(jobId, isDeleted);
    }

    public List<BatchJob> getByIds(@Param("ids") List<Long> ids, @Param("project") Long projectId) {
        return batchJobDao.listByJobIds(ids, projectId);
    }


    /**
     * 离线调用
     *
     * @param batchJob
     * @param isOnlyNextChild
     * @param appType
     * @return
     */
    public List<BatchJob> getSameDayChildJob(@Param("batchJob") String batchJob,
                                             @Param("isOnlyNextChild") boolean isOnlyNextChild, @Param("appType") Integer appType) {
        BatchJob job = JSONObject.parseObject(batchJob, BatchJob.class);
        if (Objects.isNull(job)) {
            return new ArrayList<>();
        }
        return this.getAllChildJobWithSameDay(job, isOnlyNextChild, appType);
    }

    /**
     * FIXME 注意不要出现死循环
     * 查询出指定job的所有关联的子job
     * 限定同一天并且不是自依赖
     *
     * @param batchJob
     * @return
     */
    public List<BatchJob> getAllChildJobWithSameDay(BatchJob batchJob,
                                                    @Param("isOnlyNextChild") boolean isOnlyNextChild, @Param("appType") Integer appType) {

        String jobKey = batchJob.getJobKey();
        List<BatchJobJob> batchJobJobList = batchJobJobService.getJobChild(jobKey);
        List<String> jobKeyList = Lists.newArrayList();
        List<BatchJob> batchJobList = Lists.newArrayList();

        String parentJobDayStr = getJobTriggerTimeFromJobKey(batchJob.getJobKey());
        if (Strings.isNullOrEmpty(parentJobDayStr)) {
            return batchJobList;
        }

        for (BatchJobJob batchJobJob : batchJobJobList) {
            //排除自依赖
            String childJobKey = batchJobJob.getJobKey();
            if (batchJob.getTaskId() != null && batchJob.getTaskId().equals(getTaskIdFromJobKey(childJobKey))) {
                continue;
            }

            //排除不是同一天执行的
            if (!parentJobDayStr.equals(getJobTriggerTimeFromJobKey(childJobKey))) {
                continue;
            }

            jobKeyList.add(batchJobJob.getJobKey());
        }

        if (CollectionUtils.isEmpty(jobKeyList)) {
            return batchJobList;
        }


        for (BatchJob childBatchJob : batchJobDao.listJobByJobKeys(jobKeyList)) {

            //判断job 对应的task是否被删除
            BatchTaskShade jobRefTask = batchTaskShadeService.getBatchTaskById(childBatchJob.getTaskId(), appType);
            if (jobRefTask == null || Deleted.DELETED.getStatus().equals(jobRefTask.getIsDeleted())) {
                continue;
            }

            batchJobList.add(childBatchJob);

            if (isOnlyNextChild) {
                continue;
            }
            batchJobList.addAll(getAllChildJobWithSameDay(childBatchJob, isOnlyNextChild, appType));
        }

        return batchJobList;

    }


    public Integer generalCount(BatchJobDTO query) {
        query.setPageQuery(false);
        return batchJobDao.generalCount(query);
    }

    public Integer generalCountWithMinAndHour(BatchJobDTO query) {
        query.setPageQuery(false);
        return batchJobDao.generalCountWithMinAndHour(query);
    }


    public List<BatchJob> generalQuery(PageQuery query) {
        return batchJobDao.generalQuery(query);
    }

    public List<BatchJob> generalQueryWithMinAndHour(PageQuery query) {
        return batchJobDao.generalQueryWithMinAndHour(query);
    }

    /**
     * 获取job最后一次执行
     *
     * @param taskId
     * @param time
     * @return
     */
    public BatchJob getLastSuccessJob(@Param("taskId") Long taskId, @Param("time") Timestamp time) {
        return batchJobDao.getByTaskIdAndStatusOrderByIdLimit(taskId, TaskStatus.FINISHED.getStatus(), time);
    }


    /**
     * 设置算法实验日志
     * 获取全部子节点日志
     *
     * @param status
     * @param taskType
     * @param jobId
     * @param logVo
     * @throws Exception
     */
    public BatchServerLogVO setAlogrithmLabLog(@Param("status") Integer status, @Param("taskType") Integer taskType, @Param("jobId") String jobId,
                                               @Param("info") String info, @Param("logVo") String logVo, @Param("appType") Integer appType) throws Exception {
        BatchServerLogVO batchServerLogVO = JSONObject.parseObject(logVo, BatchServerLogVO.class);
        if (taskType.equals(EJobType.ALGORITHM_LAB.getVal())) {
            if (TaskStatusConstrant.FAILED_STATUS.contains(status) ||
                    TaskStatusConstrant.FINISH_STATUS.contains(status)) {
                List<BatchJob> subJobs = batchJobDao.getSubJobsByFlowIds(Lists.newArrayList(jobId));
                if (CollectionUtils.isNotEmpty(subJobs)) {
                    Map<String, String> subNodeDownloadLog = new HashMap<>(subJobs.size());
                    StringBuilder subTaskLogInfo = new StringBuilder();
                    List<Long> taskIds = subJobs.stream().map(BatchJob::getTaskId).collect(Collectors.toList());
                    List<BatchTaskShade> taskShades = batchTaskShadeDao.listByTaskIds(taskIds, null, appType);
                    if (CollectionUtils.isEmpty(taskShades)) {
                        return batchServerLogVO;
                    }
                    Map<Long, BatchTaskShade> shadeMap = taskShades
                            .stream()
                            .collect(Collectors.toMap(BatchTaskShade::getTaskId, batchTaskShade -> batchTaskShade));
                    for (BatchJob subJob : subJobs) {
                        BatchTaskShade subTaskShade = shadeMap.get(subJob.getTaskId());
                        if (Objects.isNull(subTaskShade)) {
                            continue;
                        }
                        if (!EJobType.VIRTUAL.getVal().equals(subTaskShade.getTaskType())) {
                            Long dtuicTenantId = subTaskShade.getDtuicTenantId();
                            if (dtuicTenantId != null) {
                                subNodeDownloadLog.put(subTaskShade.getName(), String.format(DOWNLOAD_LOG, subJob.getJobId(), subTaskShade.getTaskType()));
                                JSONObject logInfoFromEngine = this.getLogInfoFromEngine(subJob.getJobId());
                                if (Objects.nonNull(logInfoFromEngine)) {
                                    subTaskLogInfo.append(subTaskShade.getName()).
                                            append("\n====================\n").
                                            append(logInfoFromEngine.getString("logInfo")).
                                            append("\n====================\n").
                                            append(logInfoFromEngine.getString("engineLog")).
                                            append("\n");
                                }
                            }
                        }
                    }
                    JSONObject infoObject = JSONObject.parseObject(info);
                    infoObject.put("msg_info", subTaskLogInfo.toString());
                    batchServerLogVO.setSubNodeDownloadLog(subNodeDownloadLog);
                    batchServerLogVO.setLogInfo(infoObject.toJSONString());
                }
            }
        }
        return batchServerLogVO;
    }


    /**
     * 获取日志
     *
     * @return
     */
    @Forbidden
    public JSONObject getLogInfoFromEngine(String jobId) {
        JSONObject logsBody = new JSONObject(2);
        logsBody.put("jobId", jobId);
        logsBody.put("computeType", ComputeType.BATCH.getType());
        String log = engineSend.log(logsBody.toJSONString(), null, null);
        return JSONObject.parseObject(log);
    }


    /**
     * 周期实例列表
     * 分钟任务和小时任务 展开按钮显示
     */
    public List<BatchJobVO> minOrHourJobQuery(BatchJobDTO batchJobDTO) {
        PageQuery<BatchJobDTO> pageQuery = new PageQuery<>(batchJobDTO.getCurrentPage(), batchJobDTO.getPageSize(), "gmt_modified", Sort.DESC.name());

        batchJobDTO.setPageQuery(false);
        batchJobDTO.setProjectId(null);
        pageQuery.setModel(batchJobDTO);

        Integer count = batchJobDao.minOrHourJobCount(batchJobDTO);
        if (count < 0) {
            return null;
        }

        List<BatchJob> batchJobs = batchJobDao.minOrHourJobQuery(pageQuery);
        Map<Long, BatchTaskForFillDataDTO> prepare = this.prepare(batchJobs);
        List<BatchJobVO> transfer = this.transfer(batchJobs, prepare);
        transfer.forEach(b -> b.setIsGroupTask(false));
        //处理工作流子节点
        try {
            this.dealFlowWorkSubJobs(transfer);
        } catch (Exception e) {
        }
        return transfer;
    }


    /**
     * 更新任务状态和日志
     *
     * @param jobId
     * @param status
     * @param logInfo
     */
    public void updateJobStatusAndLogInfo(@Param("jobId") String jobId, @Param("status") Integer status, @Param("logInfo") String logInfo) {
        batchJobDao.updateStatusByJobId(jobId, status, logInfo);
    }


    /**
     * 测试任务 是否可以运行
     * @param jobId
     * @return
     */
    public String testCheckCanRun(@Param("jobId")String jobId){
        BatchJob batchJob = batchJobDao.getByJobId(jobId, Deleted.NORMAL.getStatus());
        if (Objects.isNull(batchJob)) {
            return "任务不存在";
        }

        ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(batchJob);
        List<BatchJobJob> batchJobJobs = batchJobJobDao.listByJobKey(batchJob.getJobKey());
        scheduleBatchJob.setJobJobList(batchJobJobs);
        BatchTaskShade batchTaskById = batchTaskShadeService.getBatchTaskById(batchJob.getTaskId(), 1);
        Map<Long, BatchTaskShade> taskShadeMap = new HashMap<>();
        taskShadeMap.put(batchJob.getTaskId(), batchTaskById);
        try {
            JobCheckRunInfo jobCheckRunInfo = jobRichOperator.checkJobCanRun(scheduleBatchJob, batchJob.getStatus(), batchJob.getType(), new HashSet<>(), new HashMap<>(), taskShadeMap);
            return JSONObject.toJSONString(jobCheckRunInfo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 生成当天任务实例
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void createTodayTaskShade(@Param("taskId") Long taskId) {
        try {
            BatchTaskShade task = batchTaskShadeService.getBatchTaskById(taskId, 1);
            Map<String, String> flowJobId = new ConcurrentHashMap<>();
            List<ScheduleBatchJob> cronTrigger = jobGraphBuilder.buildJobRunBean(task, "cronTrigger", EScheduleType.NORMAL_SCHEDULE,
                    true, true, new DateTime().toString("yyyy-MM-dd"), "cronJob" + "_" + task.getName(), null, task.getProjectId(), task.getTenantId());
            if (JobGraphBuilder.SPECIAL_TASK_TYPES.contains(task.getTaskType())) {
                for (ScheduleBatchJob jobRunBean : cronTrigger) {
                    flowJobId.put(jobRunBean.getTaskId() + "_" + jobRunBean.getCycTime(), jobRunBean.getJobId());
                }
            }
            for (ScheduleBatchJob job : cronTrigger) {
                String flowIdKey = job.getBatchJob().getFlowJobId();
                job.getBatchJob().setFlowJobId(flowJobId.getOrDefault(flowIdKey, "0"));
            }

            cronTrigger.sort((ebj1, ebj2) -> {
                Long date1 = Long.valueOf(ebj1.getCycTime());
                Long date2 = Long.valueOf(ebj2.getCycTime());
                if (date1 < date2) {
                    return -1;
                } else if (date1 > date2) {
                    return 1;
                }
                return 0;
            });

            //需要保存BatchJob, BatchJobJob
            this.insertJobList(cronTrigger, EScheduleType.NORMAL_SCHEDULE.getType());
            //添加到告警监控表里面
            jobGraphBuilder.saveJobAlarm(cronTrigger);
            if (CollectionUtils.isNotEmpty(cronTrigger)) {
                for (ScheduleBatchJob job : cronTrigger) {
                    logger.info("create job task shade for test {}", job.getJobKey());
                    if (CollectionUtils.isNotEmpty(job.getBatchJobJobList())) {
                        for (BatchJobJob batchJobJob : job.getBatchJobJobList()) {
                            logger.info("create job task shade job {} parent job for test {}", batchJobJob.getJobKey(), batchJobJob.getParentJobKey());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("createTodayTaskShadeForTest", e);
            throw new DtCenterDefException("任务创建失败");
        }
    }

    public List<BatchJob> listByBusinessDateAndPeriodTypeAndStatusList(BatchJobDTO query) {
        PageQuery<BatchJobDTO> pageQuery = new PageQuery<>(query);
        pageQuery.setModel(query);
        query.setPageQuery(false);
        return batchJobDao.listByBusinessDateAndPeriodTypeAndStatusList(pageQuery);
    }

}
