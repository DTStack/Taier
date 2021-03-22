package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.dto.QueryJobDTO;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.api.dto.ScheduleTaskForFillDataDTO;
import com.dtstack.engine.api.enums.TaskRuleEnum;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobScienceJobStatusVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobStatusCountVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobStatusVO;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.RetryUtil;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.domain.ScheduleEngineProject;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.jobdealer.JobStopDealer;
import com.dtstack.engine.master.queue.JobPartitioner;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.JobGraphBuilder;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import com.dtstack.engine.master.vo.BatchSecienceJobChartVO;
import com.dtstack.engine.master.vo.ScheduleJobVO;
import com.dtstack.engine.master.vo.ScheduleTaskVO;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.schedule.common.enums.Deleted;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import com.dtstack.schedule.common.enums.Sort;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/3
 */
@Service
public class ScheduleJobService {

    private final static Logger logger = LoggerFactory.getLogger(ScheduleJobService.class);

    private static final ObjectMapper objMapper = new ObjectMapper();

    private static final String DAY_PATTERN = "yyyy-MM-dd";

    private DateTimeFormatter dayFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    private DateTimeFormatter dayFormatterAll = DateTimeFormat.forPattern("yyyyMMddHHmmss");

    private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private static final String BUSINESS_DATE = "business_date";

    private static final int TOTAL_HOUR_DAY = 24;

    private final String LOG_TEM = "%s: %s(所属租户：%s,所属项目：%s)";

    private static final String DOWNLOAD_LOG = "/api/rdos/download/batch/batchDownload/downloadJobLog?jobId=%s&taskType=%s";

    private static final List<Integer> SPECIAL_TASK_TYPES = Lists.newArrayList(EScheduleJobType.WORK_FLOW.getVal(), EScheduleJobType.ALGORITHM_LAB.getVal());

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
    private TenantDao tenantDao;

    @Autowired
    private ScheduleEngineProjectDao scheduleEngineProjectDao;

    private final static List<Integer> FINISH_STATUS = Lists.newArrayList(RdosTaskStatus.FINISHED.getStatus(), RdosTaskStatus.MANUALSUCCESS.getStatus(), RdosTaskStatus.CANCELLING.getStatus(), RdosTaskStatus.CANCELED.getStatus());
    private final static List<Integer> FAILED_STATUS = Lists.newArrayList(RdosTaskStatus.FAILED.getStatus(), RdosTaskStatus.SUBMITFAILD.getStatus(), RdosTaskStatus.KILLED.getStatus());


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
    public PageResult getStatusJobList( Long projectId,  Long tenantId,  Integer appType,
                                        Long dtuicTenantId,  Integer status,  int pageSize,  int pageIndex) {
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
        int all = 0;
        ScheduleJobStatusVO scheduleJobStatusVO =new ScheduleJobStatusVO();
        List<Map<String, Object>> data = scheduleJobDao.countByStatusAndType(EScheduleType.NORMAL_SCHEDULE.getType(), DateUtil.getUnStandardFormattedDate(DateUtil.calTodayMills()),
                DateUtil.getUnStandardFormattedDate(DateUtil.TOMORROW_ZERO()), tenantId, projectId, appType, dtuicTenantId, null);
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
        return scheduleJobStatusVO;
    }

    public List<ScheduleJobStatusVO> getStatusCountByProjectIds(List<Long> projectIds, Long tenantId, Integer appType, Long dtuicTenantId) {
        List<ScheduleJobStatusVO> scheduleJobStatusVOS = Lists.newArrayList();

        if (CollectionUtils.isEmpty(projectIds)) {
            return scheduleJobStatusVOS;
        }

        for (Long projectId : projectIds) {
            ScheduleJobStatusVO statusCount = getStatusCount(projectId, tenantId, appType, dtuicTenantId);
            statusCount.setProjectId(projectId);
            scheduleJobStatusVOS.add(statusCount);
        }

        return scheduleJobStatusVOS;
    }

    /**
     * 运行时长top排序
     */
    public List<JobTopOrderVO> runTimeTopOrder( Long projectId,
                                                Long startTime,
                                                Long endTime,  Integer appType,  Long dtuicTenantId) {

        if (null != startTime && null != endTime) {
            startTime = startTime * 1000;
            endTime = endTime * 1000;
        } else {
            startTime = DateUtil.calTodayMills();
            endTime = DateUtil.TOMORROW_ZERO();
        }

        PageQuery<Object> pageQuery = new PageQuery<>(1, 10);
        List<Map<String, Object>> list = scheduleJobDao.listTopRunTime(projectId, new Timestamp(startTime), new Timestamp(endTime), pageQuery, appType,dtuicTenantId);

        List<JobTopOrderVO> jobTopOrderVOS = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)) {
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
                jobTopOrderVO.setJobId( null != jobId ? jobId : 0);
                jobTopOrderVO.setTaskId( null != taskId ? taskId : 0);
                if(null != cycTime) {
                    jobTopOrderVO.setCycTime(DateUtil.addTimeSplit(cycTime));
                }
                jobTopOrderVO.setType( null != type ? type : 0);
                Integer taskType = MathUtil.getIntegerVal(info.get("taskType"));
                jobTopOrderVO.setTaskType( null != taskType ? taskType : 0);
                jobTopOrderVO.setIsDeleted(MathUtil.getIntegerVal(info.get("isDeleted")));
                jobTopOrderVO.setCreateUserId(MathUtil.getLongVal(info.get("createUserId")));
                jobTopOrderVOS.add(jobTopOrderVO);
            }
        }
        return jobTopOrderVOS;
    }

    /**
     * 近30天任务出错排行
     */
    public List<JobTopErrorVO> errorTopOrder( Long projectId,  Long tenantId,  Integer appType,  Long dtuicTenantId) {

        Timestamp time = new Timestamp(DateUtil.getLastDay(30));
        PageQuery<Object> pageQuery = new PageQuery<>(1, 10);
        String startCycTime = dayFormatterAll.print(getTime(time.getTime(), 0).getTime());
        return scheduleJobDao.listTopErrorByType(dtuicTenantId,tenantId, projectId, EScheduleType.NORMAL_SCHEDULE.getType(), startCycTime, FAILED_STATUS, pageQuery, appType);
    }


    /**
     * 曲线图数据
     */
    public ScheduleJobChartVO getJobGraph( Long projectId,  Long tenantId,  Integer appType,  Long dtuicTenantId) {

        List<Integer> statusList = new ArrayList<>(4);
        List<Integer> finishedList = RdosTaskStatus.getCollectionStatus(RdosTaskStatus.FINISHED.getStatus());
        List<Integer> failedList = RdosTaskStatus.getCollectionStatus(RdosTaskStatus.FAILED.getStatus());
        statusList.addAll(finishedList);
        statusList.addAll(failedList);
        String today = DateTime.now().plusDays(0).withTime(0,0,0,0).toString(timeFormatter);
        String yesterday = DateTime.now().plusDays(-1).withTime(0,0,0,0).toString(timeFormatter);
        String lastMonth = DateTime.now().plusDays(-30).withTime(0,0,0,0).toString(timeFormatter);
        List<Object> todayJobList = finishData(scheduleJobDao.listTodayJobs(today,statusList, EScheduleType.NORMAL_SCHEDULE.getType(), projectId, tenantId, appType,dtuicTenantId));
        List<Object> yesterdayJobList = finishData(scheduleJobDao.listYesterdayJobs(yesterday,today,statusList, EScheduleType.NORMAL_SCHEDULE.getType(), projectId, tenantId, appType,dtuicTenantId));
        List<Object> monthJobList = finishData(scheduleJobDao.listMonthJobs(lastMonth,statusList, EScheduleType.NORMAL_SCHEDULE.getType(), projectId, tenantId, appType,dtuicTenantId));

        for (int i = 0; i < TOTAL_HOUR_DAY; i++) {
            monthJobList.set(i, (Long) monthJobList.get(i) / 30);
        }
        return new ScheduleJobChartVO(todayJobList, yesterdayJobList, monthJobList);
    }

    /**
     * 获取数据科学的曲线图
     *
     * @return
     */
    public ChartDataVO getScienceJobGraph( long projectId,  Long tenantId,
                                           String taskType) {

        List<Integer> finishedList = Lists.newArrayList(RdosTaskStatus.FINISHED.getStatus());
        List<Integer> failedList = Lists.newArrayList(RdosTaskStatus.FAILED.getStatus(), RdosTaskStatus.SUBMITFAILD.getStatus());
        List<Integer> deployList = Lists.newArrayList(RdosTaskStatus.UNSUBMIT.getStatus(), RdosTaskStatus.SUBMITTING.getStatus(), RdosTaskStatus.WAITENGINE.getStatus());
        List<Integer> taskTypes =  convertStringToList(taskType);
        List<Map<String, Object>> successCnt = scheduleJobDao.listThirtyDayJobs(finishedList, EScheduleType.NORMAL_SCHEDULE.getType(), taskTypes, projectId, tenantId);
        List<Map<String, Object>> failCnt = scheduleJobDao.listThirtyDayJobs(failedList, EScheduleType.NORMAL_SCHEDULE.getType(), taskTypes, projectId, tenantId);
        List<Map<String, Object>> deployCnt = scheduleJobDao.listThirtyDayJobs(deployList, EScheduleType.NORMAL_SCHEDULE.getType(), taskTypes, projectId, tenantId);
        List<Map<String, Object>> totalCnt = scheduleJobDao.listThirtyDayJobs(null, EScheduleType.NORMAL_SCHEDULE.getType(), taskTypes, projectId, tenantId);
        BatchSecienceJobChartVO result = new BatchSecienceJobChartVO();
        return result.format(totalCnt, successCnt, failCnt, deployCnt);
    }

    public ScheduleJobScienceJobStatusVO countScienceJobStatus( List<Long> projectIds,  Long tenantId,  Integer runStatus,  Integer type,  String taskType,
                                                      String cycStartTime,  String cycEndTime) {
        if(StringUtils.isBlank(taskType)){
            throw new RdosDefineException("任务类型不能为空");
        }
        Map<String, Object> stringObjectMap = scheduleJobDao.countScienceJobStatus(runStatus, projectIds, type, convertStringToList(taskType), tenantId, cycStartTime, cycEndTime);
        ScheduleJobScienceJobStatusVO scienceJobStatusVO = new ScheduleJobScienceJobStatusVO();
        if(null == stringObjectMap){
            return scienceJobStatusVO;
        }
        scienceJobStatusVO.setTotal(stringObjectMap.get("total")==null?0:Integer.parseInt(stringObjectMap.get("total").toString()));
        scienceJobStatusVO.setDeployCount(stringObjectMap.get("deployCount")==null?0:Integer.parseInt(stringObjectMap.get("deployCount").toString()));
        scienceJobStatusVO.setFailCount(stringObjectMap.get("failCount")==null?0:Integer.parseInt(stringObjectMap.get("failCount").toString()));
        scienceJobStatusVO.setSuccessCount(stringObjectMap.get("successCount")==null?0:Integer.parseInt(stringObjectMap.get("successCount").toString()));
        return scienceJobStatusVO;
    }

    private List<Object> finishData(List<Map<String, Object>> metadata) {
        Map<String, Long> dataMap = new HashMap<>();
        List<Object> dataList = new ArrayList<>();

        for (Map<String, Object> data : metadata) {
            //只要看到每个时间点完成的任务数
            dataMap.put(MathUtil.getString(data.get("hour")), MathUtil.getLongVal(data.get("data")));
        }

        String hour;
        for (int i = 0; i < TOTAL_HOUR_DAY; i++) {
            if (i < 10) {
                hour = "0" + i;
            } else {
                hour = "" + i;
            }
            dataList.add(dataMap.getOrDefault(hour, 0L));
        }
        return dataList;
    }


    /**
     * 任务运维 - 搜索
     *
     * @return
     * @author toutian
     */
    public PageResult<List<com.dtstack.engine.api.vo.ScheduleJobVO>> queryJobs(QueryJobDTO vo) throws Exception {

        if (vo.getType() == null) {
            throw new RdosDefineException("类型参数必填", ErrorCode.INVALID_PARAMETERS);
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
        List<com.dtstack.engine.api.vo.ScheduleJobVO> result = new ArrayList<>();
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
        if (AppType.DATASCIENCE.getType() == vo.getAppType()) {
            batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
            count = queryScienceJob(batchJobDTO, queryAll, pageQuery, result);
        } else {
            count = queryNormalJob(batchJobDTO, queryAll, pageQuery, result);
        }
        
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
    private int queryNormalJob(ScheduleJobDTO batchJobDTO, boolean queryAll, PageQuery<ScheduleJobDTO> pageQuery, List<com.dtstack.engine.api.vo.ScheduleJobVO> result) throws Exception {
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
                    batchJobVOS.forEach(batchJobVO -> result.add(batchJobVO));
                }
            }
        }
        return count;
    }



    /**
     * 算法查询 分钟小时归类
     * @param batchJobDTO
     * @param queryAll
     * @param pageQuery
     * @param result
     * @return
     * @throws Exception
     */
    private int queryScienceJob(ScheduleJobDTO batchJobDTO, boolean queryAll, PageQuery<ScheduleJobDTO> pageQuery, List<com.dtstack.engine.api.vo.ScheduleJobVO> result) throws Exception {

        int count = scheduleJobDao.generalScienceCount(batchJobDTO);
        if (count > 0) {
            List<ScheduleJob> scheduleJobs = scheduleJobDao.generalScienceQuery(pageQuery);
            if (CollectionUtils.isNotEmpty(scheduleJobs)) {

                //获取任务id->任务的map
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
                    batchJobVOS.forEach(batchJobVO -> result.add(batchJobVO));
                }
            }
        }
        return count;
    }

    public List<SchedulePeriodInfoVO> displayPeriods( boolean isAfter,  Long jobId,  Long projectId,  int limit) throws Exception {
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
    public ScheduleJobVO getRelatedJobs( String jobId,  String query) throws Exception {
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
            //将子节点列表转换成client包中的子节点列表
            List<com.dtstack.engine.api.vo.ScheduleJobVO> relatedJobVOs= new ArrayList<>(subJobVOs.size());
            subJobVOs.forEach(subJobVO -> relatedJobVOs.add(subJobVO));
            batchJobVO.setRelatedJobs(relatedJobVOs);
            return batchJobVO;
        } else {
            throw new RdosDefineException("只有工作流任务有下属节点");
        }

    }


    //处理工作流子节点
    private void dealFlowWorkSubJobs(List<ScheduleJobVO> vos) throws Exception {

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
            //维持旧接口 数据结构
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
        ScheduleJobDTO batchJobDTO = createQuery(vo);
        batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_ParentNodes.getType());
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
            statusMap = RdosTaskStatus.getStatusFailedDetail();
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
        batchJobDTO.setProjectId(vo.getProjectId());
        batchJobDTO.setTaskTypes(convertStringToList(vo.getTaskType()));
        batchJobDTO.setExecTimeSort(vo.getExecTimeSort());
        batchJobDTO.setExecStartSort(vo.getExecStartSort());
        batchJobDTO.setExecEndSort(vo.getExecEndSort());
        batchJobDTO.setCycSort(vo.getCycSort());
        batchJobDTO.setRetryNumSort(vo.getRetryNumSort());
        batchJobDTO.setBusinessDateSort(vo.getBusinessDateSort());
        batchJobDTO.setTaskPeriodId(convertStringToList(vo.getTaskPeriodId()));
        batchJobDTO.setAppType(vo.getAppType());

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

        ScheduleTaskShade task = batchTaskShadeService.getBatchTaskById(taskId, appType);

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

                ScheduleTaskShade jobTask = batchTaskShadeService.getBatchTaskById(taskId, appType);
                runDetail.setTaskName(jobTask.getName());
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
                logger.error(" job  {} run fail with info is null",rdosJobByJobId.getJobId(),e);
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
        ScheduleTaskShade batchTask = batchTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), scheduleJob.getAppType());
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
        logger.error(" job  {} run fail with info is null",scheduleJob.getJobId());
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
        if (batchTask.getTaskType().equals(EScheduleJobType.WORK_FLOW.getVal()) ||
                batchTask.getTaskType().equals(EScheduleJobType.ALGORITHM_LAB.getVal())) {
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

    public String stopJob( long jobId, Integer appType) throws Exception {

        ScheduleJob scheduleJob = scheduleJobDao.getOne(jobId);
        return stopJobByScheduleJob( appType, scheduleJob);
    }

    private String stopJobByScheduleJob(  Integer appType, ScheduleJob scheduleJob) throws Exception {

        if (scheduleJob == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        ScheduleTaskShade task = batchTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), appType);
        if (task == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        Integer status = scheduleJob.getStatus();
        if (!checkJobCanStop(status)) {
            throw new RdosDefineException(ErrorCode.JOB_CAN_NOT_STOP);
        }

        if (RdosTaskStatus.UNSUBMIT.getStatus().equals(status)) {
            //stopSubmittedJob(Lists.newArrayList(scheduleJob), dtuicTenantId, appType);
            jobStopDealer.addStopJobs(Lists.newArrayList(scheduleJob));
            //return stopUnsubmitJob(scheduleJob);
            return "";
        } else if (RdosTaskStatus.RUNNING_STATUS.contains(status) || RdosTaskStatus.WAIT_STATUS.contains(status)) {
            //return stopSubmittedJob(Lists.newArrayList(scheduleJob), dtuicTenantId, appType);
            jobStopDealer.addStopJobs(Lists.newArrayList(scheduleJob));
            return "";
        } else {
            throw new RdosDefineException(ErrorCode.JOB_CAN_NOT_STOP);
        }
    }

    public String stopJobByJobId( String jobId, Integer appType) throws Exception{
        if(StringUtils.isBlank(jobId)){
            return "";
        }
        logger.info("stop job by jobId {}",jobId);
        ScheduleJob batchJob = scheduleJobDao.getByJobId(jobId,Deleted.NORMAL.getStatus());
        return stopJobByScheduleJob(appType, batchJob);
    }

    public void stopFillDataJobs( String fillDataJobName,  Long projectId,  Long dtuicTenantId,  Integer appType) throws Exception {
        //还未发送到engine部分---直接停止
        if (StringUtils.isBlank(fillDataJobName) || null == projectId || null == appType) {
            return;
        }
        String likeName = fillDataJobName + "-%";
        //发送停止消息到engine
        //查询出所有需要停止的任务
        List<ScheduleJob> needStopIdList = scheduleJobDao.listNeedStopFillDataJob(likeName, RdosTaskStatus.getCanStopStatus(), projectId, appType);
        //通过interceptor的触发状态更新的event
        scheduleJobDao.stopUnsubmitJob(likeName, projectId, appType, RdosTaskStatus.CANCELED.getStatus());
        //发送停止任务消息到engine
        //this.stopSubmittedJob(needStopIdList, dtuicTenantId, appType);
        jobStopDealer.addStopJobs(needStopIdList);
    }


    @Transactional(rollbackFor = Exception.class)
    public int batchStopJobs( List<Long> jobIdList) {
        if (CollectionUtils.isEmpty(jobIdList)) {
            return 0;
        }
        List<ScheduleJob> jobs = new ArrayList<>(scheduleJobDao.listByJobIds(jobIdList));

        List<String> flowJobIds = scheduleJobDao.getWorkFlowJobId(jobIdList, SPECIAL_TASK_TYPES);
        if (CollectionUtils.isNotEmpty(flowJobIds)) {
            List<ScheduleJob> subJobs = scheduleJobDao.getSubJobsByFlowIds(flowJobIds);
            if (CollectionUtils.isNotEmpty(subJobs)) {
                //将子任务实例加入
                jobs.addAll(subJobs);
            }
        }
        return jobStopDealer.addStopJobs(jobs);
    }


    /**
     * jobSize 在负载均衡时 区分 scheduleType（正常调度 和 补数据）
     */
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
        List<ScheduleJob> jobWaitForSave = Lists.newArrayList();
        List<ScheduleJobJob> jobJobWaitForSave = Lists.newArrayList();

        Map<String, Integer> nodeJobSize = computeJobSizeForNode(batchJobCollection.size(), scheduleType);
        for (Map.Entry<String, Integer> nodeJobSizeEntry : nodeJobSize.entrySet()) {
            String nodeAddress = nodeJobSizeEntry.getKey();
            int nodeSize = nodeJobSizeEntry.getValue();
            while (nodeSize > 0 && batchJobIterator.hasNext()) {
                nodeSize--;
                ScheduleBatchJob scheduleBatchJob = batchJobIterator.next();

                ScheduleJob scheduleJob = scheduleBatchJob.getScheduleJob();
                scheduleJob.setNodeAddress(nodeAddress);

                jobWaitForSave.add(scheduleJob);
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

    private void persisteJobs(List<ScheduleJob> jobWaitForSave, List<ScheduleJobJob> jobJobWaitForSave) {
        try {
            RetryUtil.executeWithRetry(() -> {
                if (jobWaitForSave.size() > 0) {
                    scheduleJobDao.batchInsert(jobWaitForSave);
                    jobWaitForSave.clear();
                }
                if (jobJobWaitForSave.size() > 0) {
                    batchJobJobService.batchInsert(jobJobWaitForSave);
                    jobJobWaitForSave.clear();
                }
                return null;
            }, environmentContext.getBuildJobErrorRetry(), 200, false);
        } catch (Exception e) {
            logger.error("!!!!! persisteJobs job error !!!! job {} jobjob {}", jobWaitForSave, jobJobWaitForSave, e);
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
     * 补数据的时候，选中什么业务日期，参数替换结果是业务日期+1天
     */
    @Transactional
    public String fillTaskData( String taskJsonStr,  String fillName,
                                Long fromDay,  Long toDay,
                                String beginTime,  String endTime,
                                Long projectId,  Long userId,
                                Long tenantId,
                                Boolean isRoot,  Integer appType,  Long dtuicTenantId) throws Exception {

        if(StringUtils.isEmpty(taskJsonStr)){
            throw new RdosDefineException("(taskJsonStr 参数不能为空)", ErrorCode.INVALID_PARAMETERS);
        }
        ArrayNode jsonNode = null;
        try {
            jsonNode = objMapper.readValue(taskJsonStr, ArrayNode.class);
        } catch (IOException e) {
            throw new RdosDefineException("takJsonStr格式错误");
        }
        //计算从fromDay--toDay之间的天数
        DateTime fromDateTime = new DateTime(fromDay * 1000L);
        DateTime toDateTime = new DateTime(toDay * 1000L);

        String fromDayStr = fromDateTime.toString(dayFormatter);
        String toDayStr = toDateTime.toString(dayFormatter);

        DateTime currDateTime = DateTime.now();
        currDateTime = currDateTime.withTime(0, 0, 0, 0);

        checkFillDataParams(taskJsonStr, fillName, projectId, toDateTime, currDateTime);

        Map<String, ScheduleBatchJob> addBatchMap = Maps.newLinkedHashMap();
        //存储fill_job name
        String currDayStr = currDateTime.toString(dayFormatter);
        ScheduleFillDataJob scheduleFillDataJob = scheduleFillDataJobService.saveData(fillName, tenantId, projectId, currDayStr, fromDayStr, toDayStr, userId, appType, dtuicTenantId);

        for (; !toDateTime.isBefore(fromDateTime); ) {
            try {
                DateTime cycTime = fromDateTime.plusDays(1);
                String triggerDay = cycTime.toString(DAY_PATTERN);
                Map<String, ScheduleBatchJob> result;
                if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
                    result = jobGraphBuilder.buildFillDataJobGraph(jsonNode, fillName, false, triggerDay, userId, beginTime, endTime, projectId, tenantId, isRoot, appType, scheduleFillDataJob.getId(),dtuicTenantId);
                } else {
                    result = jobGraphBuilder.buildFillDataJobGraph(jsonNode, fillName, false, triggerDay, userId, projectId, tenantId, isRoot, appType, scheduleFillDataJob.getId(),dtuicTenantId);
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

        return fillName;
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
                                                Integer currentPage, Integer pageSize, Long tenantId) {
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
        PageQuery<ScheduleJobDTO> pageQuery = getScheduleJobDTOPageQuery(runDay, bizStartDay, bizEndDay, dutyUserId, projectId, appType, currentPage, pageSize, tenantId, batchJobDTO);

        List<Long> fillIdList = scheduleJobDao.listFillIdListWithOutTask(pageQuery);

        if(CollectionUtils.isEmpty(fillIdList)){
            return new PageResult<>(null, 0, pageQuery);
        }
        //根据补数据名称查询出记录
        List<ScheduleFillDataJob> fillJobList = scheduleFillDataJobDao.getFillJobList(fillIdList, projectId, tenantId);

        //内存中按照时间排序
        if (CollectionUtils.isNotEmpty(fillJobList)) {
            fillJobList = fillJobList.stream().sorted((j1, j2) -> {
                return j2.getGmtCreate().compareTo(j1.getGmtCreate());

            }).collect(Collectors.toList());
        }
        List<Map<String, Long>> statistics = new ArrayList<>();
        //查询补数据任务每个状态对应的个数
        if (CollectionUtils.isNotEmpty(fillJobList)) {
            statistics = scheduleJobDao.countByFillDataAllStatus(fillJobList.stream().map(ScheduleFillDataJob::getId).collect(Collectors.toList()), projectId, tenantId);
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
     * @return: com.dtstack.engine.api.pager.PageQuery<com.dtstack.engine.api.dto.ScheduleJobDTO>
     **/
    private PageQuery<ScheduleJobDTO> getScheduleJobDTOPageQuery(Long runDay, Long bizStartDay, Long bizEndDay, Long dutyUserId, Long projectId, Integer appType, Integer currentPage, Integer pageSize, Long tenantId, ScheduleJobDTO batchJobDTO) {
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
            throw new RdosDefineException("(补数据名称不能为空)", ErrorCode.INVALID_PARAMETERS);
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
                    scheduleFillDataJobDetailVO.addRecord(transferBatchJob2FillDataRecord(scheduleJob, null, taskShadeMap));
                }
            }
        }

        return new PageResult<>(scheduleFillDataJobDetailVO, totalCount, pageQuery);
    }

    public PageResult<ScheduleFillDataJobDetailVO> getFillDataDetailInfo( String queryJobDTO,
                                                                          List<String> flowJobIdList,
                                                                          String fillJobName,
                                                                          Long dutyUserId,  String searchType,
                                                                          Integer appType) throws Exception {
        if (Strings.isNullOrEmpty(fillJobName)) {
            throw new RdosDefineException("(补数据名称不能为空)", ErrorCode.INVALID_PARAMETERS);
        }

        QueryJobDTO vo = JSONObject.parseObject(queryJobDTO, QueryJobDTO.class);
        vo.setSplitFiledFlag(true);
        ScheduleJobDTO batchJobDTO = this.createQuery(vo);
        batchJobDTO.setAppType(appType);
        batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
        batchJobDTO.setFillDataJobName(fillJobName);
        batchJobDTO.setNeedQuerySonNode(true);
        //跨租户、项目条件
        batchJobDTO.setProjectId(null);
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

        Integer totalCount = scheduleJobDao.countByFillData(batchJobDTO);
        if (totalCount > 0) {

            List<ScheduleJob> scheduleJobListWithFillData = scheduleJobDao.queryFillData(pageQuery);

            Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(scheduleJobListWithFillData);
            if (CollectionUtils.isNotEmpty(scheduleJobListWithFillData)) {
                for (ScheduleJob job : scheduleJobListWithFillData) {
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
                                                                                 String fillJobName) throws Exception {

        QueryJobDTO vo = JSONObject.parseObject(query, QueryJobDTO.class);
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, Deleted.NORMAL.getStatus());
        Map<String, ScheduleEngineJob> engineJobMap = Maps.newHashMap();
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
    private List<ScheduleFillDataJobDetailVO.FillDataRecord> getOnlyRelatedJobsForFillData(String jobId, Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap) throws Exception {
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

    private List<ScheduleFillDataJobDetailVO.FillDataRecord> getRelatedJobsForFillDataByQueryDTO(ScheduleJobDTO queryDTO, QueryJobDTO vo, String jobId, Map<String, ScheduleEngineJob> engineJobMap,
                                                                                                 Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap) throws Exception {

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


    private void dealFlowWorkSubJobsInFillData(List<ScheduleFillDataJobDetailVO.FillDataRecord> vos) throws Exception {
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

    private Map<Long, ScheduleTaskForFillDataDTO> prepareForFillDataDetailInfo(List<ScheduleJob> scheduleJobs) throws Exception {
        if (CollectionUtils.isEmpty(scheduleJobs)) {
            return new HashMap<>();
        }
        Set<Long> taskIdSet = scheduleJobs.stream().map(ScheduleJob::getTaskId).collect(Collectors.toSet());
        Integer appType = scheduleJobs.get(0).getAppType();
        return scheduleTaskShadeDao.listSimpleTaskByTaskIds(taskIdSet, null,appType).stream().collect(Collectors.toMap(ScheduleTaskForFillDataDTO::getTaskId, scheduleTaskForFillDataDTO -> scheduleTaskForFillDataDTO));

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
                                                                                       Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap) throws Exception {
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
        Integer showStatus = RdosTaskStatus.getShowStatusWithoutStop(status);
        ScheduleFillDataJobDetailVO.FillDataRecord record = new ScheduleFillDataJobDetailVO.FillDataRecord(scheduleJob.getId(), bizDayVO, taskName,
                taskType, showStatus, cycTimeVO, exeStartTimeVO, exeTime, null);

        record.setJobId(scheduleJob.getJobId());
        record.setFlowJobId(scheduleJob.getFlowJobId());
        record.setIsRestart(scheduleJob.getIsRestart());

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
            batchTaskVO.setProjectId(taskShade.getProjectId());
            batchTaskVO.setOwnerUserId(taskShade.getOwnerUserId());
            batchTaskVO.setCreateUserId(taskShade.getCreateUserId());
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
                ScheduleTaskShade jobRefTask = batchTaskShadeService.getBatchTaskById(childScheduleJob.getTaskId(), childScheduleJob.getAppType());
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
            logger.error("it's not a legal job key, str is {}.", jobKey);
            return -1L;
        }

        String id = strings[strings.length - 2];
        try {
            return MathUtil.getLongVal(id);
        } catch (Exception e) {
            logger.error("it's not a legal job key, str is {}.", jobKey);
            return -1L;
        }
    }

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


    private boolean checkJobCanStop(Integer status) {
        if (status == null) {
            return true;
        }
        return RdosTaskStatus.getCanStopStatus().contains(status);
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

    /**
     * 获取工作流顶级子节点
     *
     * @param jobId 工作流jobId
     * @return
     */
    public ScheduleJob getWorkFlowTopNode(String jobId) {
        return scheduleJobDao.getWorkFlowTopNode(jobId);
    }


    public List<String> listJobIdByTaskNameAndStatusList( String taskName,  List<Integer> statusList,  Long projectId, Integer appType) {
        ScheduleTaskShade task = batchTaskShadeService.getByName(projectId, taskName,appType,null);
        if (task != null) {
            return scheduleJobDao.listJobIdByTaskIdAndStatus(task.getTaskId(), task.getAppType() ,statusList);
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
        return scheduleJobDao.getByJobId(jobId, isDeleted);
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
            ScheduleTaskShade jobRefTask = batchTaskShadeService.getBatchTaskById(childScheduleJob.getTaskId(), appType);
            if (jobRefTask == null || Deleted.DELETED.getStatus().equals(jobRefTask.getIsDeleted())) {
                continue;
            }
            scheduleJobList.add(childScheduleJob);
            if (isOnlyNextChild) {
                continue;
            }
            level --;
            scheduleJobList.addAll(getAllChildJobWithSameDay(childScheduleJob, isOnlyNextChild, appType,level));
            logger.info("count info --- scheduleJob jobKey:{} flowJobId:{} jobJobList size:{}", scheduleJob.getJobKey(), scheduleJob.getFlowJobId(),scheduleJobList.size());
        }
        return scheduleJobList;

    }

    private void filterJobKeyList(ScheduleJob scheduleJob, Integer appType, List<ScheduleJobJob> scheduleJobJobList, List<String> jobKeyList, String parentJobDayStr) {
        for (ScheduleJobJob scheduleJobJob : scheduleJobJobList) {
            //排除自依赖
            String childJobKey = scheduleJobJob.getJobKey();
            ScheduleTaskShade taskShade = batchTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), appType);
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

    public Integer generalCountWithMinAndHour(ScheduleJobDTO query) {
        query.setPageQuery(false);
        return scheduleJobDao.generalCountWithMinAndHour(query);
    }


    public List<ScheduleJob> generalQuery(PageQuery query) {
        return scheduleJobDao.generalQuery(query);
    }

    public List<ScheduleJob> generalQueryWithMinAndHour(PageQuery query) {
        return scheduleJobDao.generalQueryWithMinAndHour(query);
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
     * 设置算法实验日志
     * 获取全部子节点日志
     *
     * @param status
     * @param taskType
     * @param jobId
     * @param logVo
     * @throws Exception
     */
    public ScheduleServerLogVO setAlogrithmLabLog( Integer status,  Integer taskType,  String jobId,
                                                   String info,  String logVo,  Integer appType) throws Exception {

        ScheduleServerLogVO scheduleServerLogVO = JSONObject.parseObject(logVo, ScheduleServerLogVO.class);
        if(!taskType.equals(EScheduleJobType.ALGORITHM_LAB.getVal())){
            return scheduleServerLogVO;
        }
        if (RdosTaskStatus.FAILED_STATUS.contains(status) ||
                RdosTaskStatus.FINISH_STATUS.contains(status)) {
            List<ScheduleJob> subJobs = scheduleJobDao.getSubJobsByFlowIds(Lists.newArrayList(jobId));
            if(CollectionUtils.isEmpty(subJobs)){
                return scheduleServerLogVO;
            }
            Map<String, String> subNodeDownloadLog = new HashMap<>(subJobs.size());
            StringBuilder subTaskLogInfo = new StringBuilder();
            List<Long> taskIds = subJobs.stream().map(ScheduleJob::getTaskId).collect(Collectors.toList());
            List<ScheduleTaskShade> taskShades = scheduleTaskShadeDao.listByTaskIds(taskIds, null, appType);
            if (CollectionUtils.isEmpty(taskShades)) {
                return scheduleServerLogVO;
            }
            Map<Long, ScheduleTaskShade> shadeMap = taskShades
                    .stream()
                    .collect(Collectors.toMap(ScheduleTaskShade::getTaskId, batchTaskShade -> batchTaskShade));
            for (ScheduleJob subJob : subJobs) {
                ScheduleTaskShade subTaskShade = shadeMap.get(subJob.getTaskId());
                if (null == subTaskShade ) {
                    continue;
                }
                setVirtualLog(subNodeDownloadLog, subTaskLogInfo, subJob, subTaskShade);
            }
            JSONObject infoObject = JSONObject.parseObject(info);
            infoObject.put("msg_info", subTaskLogInfo.toString());
            scheduleServerLogVO.setSubNodeDownloadLog(subNodeDownloadLog);
            scheduleServerLogVO.setLogInfo(infoObject.toJSONString());
        }
        return scheduleServerLogVO;
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
            Long dtuicTenantId = subTaskShade.getDtuicTenantId();
            if (dtuicTenantId != null) {
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
    }


    /**
     * 获取日志
     *
     * @return
     */
    public ActionLogVO getLogInfoFromEngine(String jobId) {
        try {
            return actionService.log(jobId, ComputeType.BATCH.getType());
        } catch (Exception e) {
            logger.error("Exception when getLogInfoFromEngine by jobId: {} and computeType: {}", jobId, ComputeType.BATCH.getType(), e);
        }
        return null;
    }


    /**
     * 周期实例列表
     * 分钟任务和小时任务 展开按钮显示
     * @return
     */
    public List<com.dtstack.engine.api.vo.ScheduleJobVO> minOrHourJobQuery(ScheduleJobDTO batchJobDTO) {
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(batchJobDTO.getCurrentPage(), batchJobDTO.getPageSize(), "gmt_modified", Sort.DESC.name());

        batchJobDTO.setPageQuery(false);
        batchJobDTO.setProjectId(null);
        pageQuery.setModel(batchJobDTO);

        Integer count = scheduleJobDao.minOrHourJobCount(batchJobDTO);
        if (count < 0) {
            return null;
        }

        List<ScheduleJob> scheduleJobs = scheduleJobDao.minOrHourJobQuery(pageQuery);
        Map<Long, ScheduleTaskForFillDataDTO> prepare = this.prepare(scheduleJobs);
        List<ScheduleJobVO> transfer = this.transfer(scheduleJobs, prepare);
        transfer.forEach(b -> b.setIsGroupTask(false));
        //处理工作流子节点
        try {
            this.dealFlowWorkSubJobs(transfer);
        } catch (Exception e) {
            throw new RdosDefineException("处理工作流字节点异常");
        }
        List<com.dtstack.engine.api.vo.ScheduleJobVO> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(transfer)) {
            transfer.forEach(vo -> result.add(vo));
        }
        return result;
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
        ScheduleTaskShade batchTaskById = batchTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), scheduleJob.getAppType());
        if (batchTaskById == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        try {
            JobCheckRunInfo jobCheckRunInfo = jobRichOperator.checkJobCanRun(scheduleBatchJob, scheduleJob.getStatus(), scheduleJob.getType(),batchTaskById);
            return JSONObject.toJSONString(jobCheckRunInfo);
        } catch (Exception e) {
            logger.error("ScheduleJobService.testCheckCanRun error:", e);
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
            ScheduleTaskShade testTask = batchTaskShadeService.getBatchTaskById(taskId, appType);
            if (null == testTask) {
                throw new RdosDefineException("任务不存在");
            }
            List<ScheduleTaskShade> taskShades = new ArrayList<>();
            taskShades.add(testTask);
            if (SPECIAL_TASK_TYPES.contains(testTask.getTaskType())) {
                //工作流算法实验 需要将子节点查询出来运行
                List<ScheduleTaskShade> flowWorkSubTasks = batchTaskShadeService.getFlowWorkSubTasks(testTask.getTaskId(), testTask.getAppType(),
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

            for (ScheduleTaskShade task : taskShades) {
                try {
                    List<ScheduleBatchJob> cronTrigger = jobGraphBuilder.buildJobRunBean(task, "cronTrigger", EScheduleType.NORMAL_SCHEDULE,
                            true, true, date, "cronJob" + "_" + task.getName(),
                            null, task.getProjectId(), task.getTenantId());
                    allJobs.addAll(cronTrigger);
                    if (SPECIAL_TASK_TYPES.contains(task.getTaskType())) {
                        //工作流或算法实验
                        for (ScheduleBatchJob jobRunBean : cronTrigger) {
                            flowJobId.put(jobGraphBuilder.buildFlowReplaceId(task.getTaskId(),jobRunBean.getCycTime(),task.getAppType()),jobRunBean.getJobId());
                        }
                    }
                } catch (Exception e) {
                    logger.error("生成当天单个任务实例异常,taskId:{}",task.getTaskId(), e);
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
            logger.error("createTodayTaskShadeForTest", e);
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

    public Long getListMinId(String nodeAddress,Integer scheduleType, String left, String right,Integer isRestart) {
        return scheduleJobDao.getListMinId(nodeAddress, scheduleType, left, right, JobPhaseStatus.CREATE.getCode(),isRestart);
    }

    public String getJobGraphJSON(String jobId) {
        return scheduleJobDao.getJobGraph(jobId);
    }

    public void updateNotRuleResult(String jobId,Integer rule,String result) {
        logger.info("updateNotRuleResult start jobId:{} , rule:{} result:{} ",jobId,rule,result);
        ScheduleJob job = scheduleJobDao.getByJobId(jobId, 0);

        JSONObject json = new JSONObject();
        json.put("jobId",jobId);
        json.put("result",result);
        if (job != null && EScheduleJobType.NOT_DO_TASK.getType().equals(job.getTaskType())) {
            if (rule == 1) {
                json.put("msg_info","Application callback succeeded");
                updateStatusAndLogInfoAndExecTimeById(jobId, RdosTaskStatus.FINISHED.getStatus(), json.toJSONString(),null,new Date());
            } else if (rule == 2) {
                json.put("msg_info","Application callback failure");
                updateStatusAndLogInfoAndExecTimeById(jobId, RdosTaskStatus.FAILED.getStatus(), json.toJSONString(),null,new Date());
            }
        } else {
            logger.info("updateNotRuleResult update  error jobId:{} , rule:{} result:{} ",jobId,rule,result);
            throw new RdosDefineException("job status error,so update failure");
        }
    }

    public void updateStatusByJobIdEqualsStatus(String jobId, Integer status, Integer status1) {
        scheduleJobDao.updateStatusByJobIdEqualsStatus(jobId,status,status1);
    }

    public List<ScheduleJob> listJobByJobKeys(List<String> parentJobKeys) {
        if (CollectionUtils.isNotEmpty(parentJobKeys)) {
            return scheduleJobDao.listJobByJobKeys(parentJobKeys);
        }
        return Lists.newArrayList();
    }

    public Map<String, List<ScheduleJob>> getParantJobKeyMap(List<String> parentJobKeys) {
        return null;
    }


    public void handleTaskRule(ScheduleJob scheduleJob,Integer bottleStatus) {
        String jobKey = scheduleJob.getJobKey();
        // 查询当前任务的所有父任务的运行状态
        List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByParentJobKey(jobKey);
        if (CollectionUtils.isNotEmpty(scheduleJobJobs)) {
            List<String> parentJobKeys = scheduleJobJobs.stream().map(ScheduleJobJob::getJobKey).collect(Collectors.toList());
            // 查询所有父任务
            List<ScheduleJob> scheduleJobs = this.listJobByJobKeys(parentJobKeys);
            // 查询所有父任务下的子任务关系
            Map<String,List<ScheduleJob>> parentAndSon = this.getParantJobKeyMap(parentJobKeys);

            for (ScheduleJob scheduleJobParent : scheduleJobs) {
                // 判断状态父任务的状态
                List<ScheduleJob> scheduleJobsSon = parentAndSon.get(scheduleJobParent.getJobKey());
                updateFatherStatus(scheduleJobParent,scheduleJob,scheduleJobsSon,bottleStatus);
            }

        }
    }

    private void updateFatherStatus(ScheduleJob fatherScheduleJob, ScheduleJob currentScheduleJob, List<ScheduleJob> sonScheduleJobs, Integer bottleStatus) {
        if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(fatherScheduleJob.getStatus()) && CollectionUtils.isNotEmpty(sonScheduleJobs)) {
            String nameByDtUicTenantId = tenantDao.getNameByDtUicTenantId(currentScheduleJob.getDtuicTenantId());
            ScheduleEngineProject project = scheduleEngineProjectDao.getProjectByProjectIdAndApptype(currentScheduleJob.getProjectId(),currentScheduleJob.getAppType());
            if (RdosTaskStatus.FAILED_STATUS.contains(bottleStatus)) {
                // 当前强任务执行失败，执行更新成失败
                String log = getLog(fatherScheduleJob, currentScheduleJob,nameByDtUicTenantId,project);
                this.updateStatusAndLogInfoById(fatherScheduleJob.getJobId(), RdosTaskStatus.FAILED.getStatus(), log);
            } else if (RdosTaskStatus.FINISH_STATUS.contains(bottleStatus)) {
                // 当前任务执行成功,判断父任务下其他子任务是否有强规则任务
                List<ScheduleJob> jobs = sonScheduleJobs.stream().filter(job -> TaskRuleEnum.STRONG_RULE.getCode().equals(job.getTaskRule()) && job.getJobKey().equals(currentScheduleJob.getJobKey())).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(jobs)) {
                    List<ScheduleJob> noFinishJobs = jobs.stream().filter(job -> !RdosTaskStatus.FINISH_STATUS.contains(job.getStatus())).collect(Collectors.toList());

                    if (CollectionUtils.isEmpty(noFinishJobs)) {
                        // 为查到未完成的任务
                        String log = String.format(LOG_TEM, currentScheduleJob.getJobName(), "运行成功", nameByDtUicTenantId, project.getProjectAlias());
                        this.updateStatusAndLogInfoById(fatherScheduleJob.getJobId(), RdosTaskStatus.FINISHED.getStatus(), addLog(fatherScheduleJob.getLogInfo(),log));
                    }
                } else {
                    String log = String.format(LOG_TEM, currentScheduleJob.getJobName(), "运行成功", nameByDtUicTenantId, project.getProjectAlias());
                    this.updateStatusAndLogInfoById(fatherScheduleJob.getJobId(), RdosTaskStatus.FINISHED.getStatus(), addLog(fatherScheduleJob.getLogInfo(),log));
                }
            }
        }
    }

    private String getLog(ScheduleJob fatherScheduleJob,ScheduleJob currentScheduleJob,String nameByDtUicTenantId,ScheduleEngineProject project) {
        String logInfo = fatherScheduleJob.getLogInfo();
        // %s: %s(所属租户：%s,所属项目：%s)
        String addLog = LOG_TEM;

        boolean isRule = Boolean.FALSE;
        if (EScheduleJobType.WORK_FLOW.getType().equals(currentScheduleJob.getTaskType())) {
            // 如果工作流任务，查询是否有null任务
            List<ScheduleJob> subJobsAndStatusByFlowId = this.getSubJobsAndStatusByFlowId(currentScheduleJob.getJobId());
            List<ScheduleJob> jobs = subJobsAndStatusByFlowId.stream().filter(job -> EScheduleJobType.NOT_DO_TASK.getType().equals(job.getTaskType())).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(jobs)) {
                // 有空任务
                for (ScheduleJob job : jobs) {
                    if (RdosTaskStatus.FAILED_STATUS.contains(job.getStatus())) {
                        // 存在空任务失败的情况
                        addLog = String.format(addLog, currentScheduleJob.getJobName(), "校验不通过", nameByDtUicTenantId, project.getProjectAlias());
                        isRule = Boolean.TRUE;
                        break;
                    }
                }


            }
        }

        if (!isRule) {
            addLog = String.format(addLog, currentScheduleJob.getJobName(), "运行失败", nameByDtUicTenantId, project.getProjectAlias());
        }

        return addLog(logInfo, addLog);


    }

    private String addLog(String logInfo, String addLog) {
        try {
            JSONObject jsonObject = JSON.parseObject(logInfo);
            JSONArray jsonArray = jsonObject.getJSONArray(GlobalConst.RULE_LOG_FILED);

            if (jsonArray != null) {
                jsonArray.add(addLog);
            } else {
                jsonArray = new JSONArray();
                List<String> ruleLogList = Lists.newArrayList();
                ruleLogList.add(addLog);
                jsonArray.add(ruleLogList);
            }

            jsonObject.put(GlobalConst.RULE_LOG_FILED,jsonArray);
            return jsonObject.toJSONString();
        } catch (Exception e) {
            logInfo+= "===============================================================\n";
            logInfo+=addLog;
            return logInfo;
        }
    }


    public List<ScheduleJobBeanVO> findTaskRuleJobById(Long id) {
        // 查询 jobId 的所有子节点
        ScheduleJob scheduleJob = getJobById(id);

        List<ScheduleJobBeanVO> vos = Lists.newArrayList();
        if (scheduleJob == null) {
            throw new RdosDefineException("job not exist,please checking jobId");
        }

        // 查询该任务下所有的规则任务
        List<ScheduleJob> scheduleJobs = getTaskRuleSonJob(scheduleJob);

        buildScheduleJobBeanVOs(vos,scheduleJobs);
        return vos;
    }

    public List<ScheduleJob> getTaskRuleSonJob(ScheduleJob scheduleJob) {
        List<ScheduleJob> scheduleJobs = Lists.newArrayList();
        List<ScheduleJobJob> jobJobs = scheduleJobJobDao.listByParentJobKey(scheduleJob.getJobKey());
        if (CollectionUtils.isNotEmpty(jobJobs)) {
            List<String> jobKeys = jobJobs.stream().map(ScheduleJobJob::getJobKey).collect(Collectors.toList());
            List<ScheduleJob> jobs = scheduleJobDao.listJobByJobKeys(jobKeys);

            for (ScheduleJob job : jobs) {
                if (!TaskRuleEnum.NO_RULE.getCode().equals(job.getTaskRule())) {
                    scheduleJobs.add(job);
                }
            }
        }
        return scheduleJobs;
    }

    private ScheduleJobBeanVO buildScheduleJobBeanVO(ScheduleJob job) {
        ScheduleJobBeanVO vo = new ScheduleJobBeanVO();
        if (job != null) {
            BeanUtils.copyProperties(job, vo);
        }
        return vo;
    }

    private void buildScheduleJobBeanVOs(List<ScheduleJobBeanVO> vos, List<ScheduleJob> subJobsAndStatusByFlowId) {
        if (CollectionUtils.isNotEmpty(subJobsAndStatusByFlowId)) {
            for (ScheduleJob scheduleJob : subJobsAndStatusByFlowId) {
                vos.add(buildScheduleJobBeanVO(scheduleJob));
            }
        }
    }

    public ScheduleJobDetailsVO findTaskRuleJob(String jobId) {
        // 查询 jobId 的所有子节点
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, Deleted.NORMAL.getStatus());
        List<ScheduleJob> taskRuleSonJob = getTaskRuleSonJob(scheduleJob);
        ScheduleJobDetailsVO vo = new ScheduleJobDetailsVO();

        buildScheduleJobDetailsVO(vo, scheduleJob);

        List<ScheduleJobDetailsVO> vos = Lists.newArrayList();
        for (ScheduleJob job : taskRuleSonJob) {
            ScheduleJobDetailsVO voSon = new ScheduleJobDetailsVO();
            buildScheduleJobDetailsVO(voSon, job);
            vos.add(voSon);
        }

        vo.setScheduleJobDetailsVOList(vos);
        return vo;
    }

    private void buildScheduleJobDetailsVO(ScheduleJobDetailsVO vo, ScheduleJob scheduleJob) {
        if (scheduleJob != null) {
            vo.setAppType(scheduleJob.getAppType());
            vo.setName(scheduleJob.getJobName());
            vo.setTaskRule(scheduleJob.getTaskRule());
            vo.setTaskType(scheduleJob.getTaskType());

            Tenant byDtUicTenantId = tenantDao.getByDtUicTenantId(scheduleJob.getDtuicTenantId());

            if (byDtUicTenantId != null) {
                vo.setTenantName(byDtUicTenantId.getTenantName());
            }

            ScheduleEngineProject projectByProjectIdAndApptype = scheduleEngineProjectDao.getProjectByProjectIdAndApptype(scheduleJob.getProjectId(), scheduleJob.getAppType());

            if (projectByProjectIdAndApptype != null) {
                vo.setProjectName(projectByProjectIdAndApptype.getProjectName());
            }
        }
    }
}
