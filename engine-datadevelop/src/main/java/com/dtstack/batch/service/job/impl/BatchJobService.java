package com.dtstack.batch.service.job.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.enums.TempJobType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.BatchTaskDao;
import com.dtstack.batch.dao.BatchTaskShadeDao;
import com.dtstack.batch.dao.BatchTaskVersionDao;
import com.dtstack.batch.dao.UserDao;
import com.dtstack.batch.domain.*;
import com.dtstack.batch.domain.po.TaskIdAndVersionIdPO;
import com.dtstack.batch.dto.BatchParamDTO;
import com.dtstack.batch.enums.EScheduleType;
import com.dtstack.batch.mapping.TaskTypeEngineTypeMapping;
import com.dtstack.batch.schedule.JobParamReplace;
import com.dtstack.batch.service.impl.*;
import com.dtstack.batch.service.job.IBatchJobExeService;
import com.dtstack.batch.service.table.impl.BatchSelectSqlService;
import com.dtstack.batch.service.task.impl.BatchTaskParamService;
import com.dtstack.batch.service.task.impl.BatchTaskParamShadeService;
import com.dtstack.batch.service.task.impl.BatchTaskResourceShadeService;
import com.dtstack.batch.service.task.impl.BatchTaskService;
import com.dtstack.batch.vo.*;
import com.dtstack.batch.web.job.vo.result.BatchGetSyncTaskStatusInnerResultVO;
import com.dtstack.batch.web.job.vo.result.BatchStartSyncResultVO;
import com.dtstack.dtcenter.common.constant.TaskStatusConstrant;
import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.dtcenter.common.login.SessionUtil;
import com.dtstack.dtcenter.common.thread.RdosThreadFactory;
import com.dtstack.dtcenter.common.util.DateUtil;
import com.dtstack.dtcenter.common.util.JsonUtils;
import com.dtstack.dtcenter.common.util.MathUtil;
import com.dtstack.engine.api.domain.ScheduleEngineJob;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.QueryJobDTO;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.pojo.ParamTaskAction;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobStatusCountVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobStatusVO;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/3
 */
@Service
public class BatchJobService {

    private static final Logger logger = LoggerFactory.getLogger(BatchJobService.class);

    private static final ObjectMapper objMapper = new ObjectMapper();

    private static final String ADD_JAR_WITH = "ADD JAR WITH %s AS ;";

    private static final String DOWNLOAD_URL = "/api/rdos/download/batch/batchDownload/downloadJobLog?jobId=%s&taskType=%s&projectId=%s";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private BatchTaskDao batchTaskDao;

    @Autowired
    private BatchTaskResourceShadeService batchTaskResourceShadeService;

    @Autowired
    private BatchServerLogService batchServerLogService;

    @Autowired
    private BatchTaskParamService batchTaskParamService;

    @Resource(name = "batchUserDao")
    private UserDao userDao;

    @Autowired
    private BatchSelectSqlService batchSelectSqlService;

    @Resource(name = "batchJobParamReplace")
    private JobParamReplace jobParamReplace;

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private MultiEngineServiceFactory multiEngineServiceFactory;

    @Autowired
    private BatchTaskVersionDao batchTaskVersionDao;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private UserService userService;

    @Autowired
    private BatchTaskService batchTaskService;

    @Autowired
    private BatchTaskParamShadeService batchTaskParamShadeService;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private ActionService actionService;

    private static final String IS_CHECK_DDL_KEY = "isCheckDDL";

    private static final Map<Integer, String> PY_VERSION_MAP = new HashMap<>(2);


    static {
        BatchJobService.PY_VERSION_MAP.put(2, " 2.x ");
        BatchJobService.PY_VERSION_MAP.put(3, " 3.x ");
    }

    /**
     * 根据任务id展示任务详情
     *
     * @author toutian
     */
    public ScheduleJob getJobById(long jobId) {
        return this.scheduleJobService.getJobById(jobId);
    }

    public Integer getJobStatus(String jobId) {
        if (StringUtils.isBlank(jobId)) {
            return null;
        }
        final ScheduleJob job = this.scheduleJobService.getByJobId(jobId, Deleted.NORMAL.getStatus());
        if (Objects.isNull(job)) {
            return null;
        }
        return job.getStatus();
    }

    /**
     * 获取各个状态任务的数量
     */
    public Map<String,Object> getStatusCount(long projectId, Long tenantId) {
        Map<String,Object> return_map = new HashMap<>();
        ScheduleJobStatusVO data = scheduleJobService.getStatusCount(projectId, tenantId, AppType.RDOS.getType(), null);
        List<ScheduleJobStatusCountVO> scheduleJobStatusCountVOList = data.getScheduleJobStatusCountVO();
        if (CollectionUtils.isNotEmpty(scheduleJobStatusCountVOList)){
            for (ScheduleJobStatusCountVO vo : scheduleJobStatusCountVOList){
                return_map.put(vo.getTaskStatusName(),vo.getCount());
            }
        }
        // 获取所有任务实例
        return_map.put("ALL", data.getAll());
        return return_map;
    }

    /**
     * 运行时长top排序
     */
    public List<JobTopOrderVO> runTimeTopOrder(long projectId, Long startTime, Long endTime, Long dtuicTenantId) {
        final List<JobTopOrderVO> jobTopOrderVo = this.scheduleJobService.runTimeTopOrder(projectId, startTime, endTime, AppType.RDOS.getType(), dtuicTenantId);
        if (CollectionUtils.isEmpty(jobTopOrderVo)) {
            return new ArrayList<>();
        }
        final Map<Long, BatchTask> batchTaskMap = this.batchTaskDao.listByIds(jobTopOrderVo.stream().map(JobTopOrderVO::getTaskId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(BatchTask::getId, t -> t));
        final List<Long> userIds = batchTaskMap.values().stream().map(BatchTask::getCreateUserId).collect(Collectors.toList());
        final Map<Long, User> userMap = this.userDao.listByIds(userIds).stream().collect(Collectors.toMap(BaseEntity::getId, t -> t));
        for (final JobTopOrderVO jobTopOrderVO : jobTopOrderVo) {
            BatchTask task = batchTaskMap.get(jobTopOrderVO.getTaskId());
            if(Objects.isNull(task)){
                //已删除
                task = this.batchTaskDao.getOneWithDeleted(jobTopOrderVO.getTaskId());
            }
            if (Objects.nonNull(task)) {
                jobTopOrderVO.setTaskName(task.getName());
                jobTopOrderVO.setIsDeleted(task.getIsDeleted());
                jobTopOrderVO.setTaskTypeName(EJobType.getEJobType(task.getTaskType()).getName());
                jobTopOrderVO.setCreateUser(userMap.getOrDefault(task.getCreateUserId(), new User()).getUserName());
            }
        }
        return jobTopOrderVo;
    }

    /**
     * 近30天任务出错排行
     */
    public List<JobTopErrorVO> errorTopOrder(long projectId, Long tenantId) {
        final List<JobTopErrorVO> errors = this.scheduleJobService.errorTopOrder(projectId, tenantId, AppType.RDOS.getType(), null);
        if (CollectionUtils.isEmpty(errors)) {
            return errors;
        }
        final Map<Long, BatchTask> batchTaskMap = this.batchTaskDao.listByIds(errors.stream().map(JobTopErrorVO::getTaskId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(BatchTask::getId, t -> t));
        final Map<Long, User> userMap = this.userDao.listByIds(batchTaskMap.values().stream().map(BatchTask::getCreateUserId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(User::getId, u -> u));
        for (final JobTopErrorVO error : errors) {
            BatchTask task = batchTaskMap.get(error.getTaskId());
            if (Objects.isNull(task)) {
                //已删除
                task = this.batchTaskDao.getOneWithDeleted(error.getTaskId());
            }
            if(Objects.isNull(task)){
                continue;
            }
            error.setTaskName(task.getName());
            error.setCreateUser(userMap.getOrDefault(task.getCreateUserId(), new User()).getUserName());
            error.setIsDeleted(task.getIsDeleted());
        }

        return errors;
    }


    /**
     * 曲线图数据
     */
    public ScheduleJobChartVO getJobGraph(long projectId, Long tenantId) {
        return this.scheduleJobService.getJobGraph(projectId, tenantId, AppType.RDOS.getType(), null);
    }



    /**
     * 任务运维 - 周期实例
     *
     * @return
     * @author toutian
     */
    public com.dtstack.batch.web.pager.PageResult<List<ScheduleJobVO>> queryJobs(QueryJobDTO vo, String searchType) {
        if (Objects.nonNull(vo)) {
            vo.setSearchType(searchType);
            vo.setAppType(AppType.RDOS.getType());
        }
        PageResult pageResult = this.scheduleJobService.queryJobs(vo);
        if (Objects.isNull(pageResult) || Objects.isNull(pageResult.getData())) {
            return com.dtstack.batch.web.pager.PageResult.EMPTY_PAGE_RESULT;
        }
        com.dtstack.batch.web.pager.PageResult returnPageResult = new com.dtstack.batch.web.pager.PageResult();
        BeanUtils.copyProperties(pageResult, returnPageResult);

        List<ScheduleJobVO> ScheduleJobVOS  = (List<ScheduleJobVO>) pageResult.getData();

        final List<Long> taskIds = new ArrayList<>(ScheduleJobVOS.size());
        final List<Long> userIds = new ArrayList<>(ScheduleJobVOS.size());
        for (final com.dtstack.engine.api.vo.ScheduleJobVO ScheduleJobVO : ScheduleJobVOS) {
            taskIds.add(ScheduleJobVO.getTaskId());
            userIds.add(ScheduleJobVO.getCreateUserId());
            userIds.add(ScheduleJobVO.getOwnerUserId());
        }
        Map<Long, BatchTask> taskMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(taskIds)) {
            taskMap = this.batchTaskDao.listByIds(taskIds).stream().collect(Collectors.toMap(BatchTask::getId, t -> t));
        }
        Map<Long, User> userMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userIds)) {
            userMap = this.userDao.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, u -> u));
        }

        for (final ScheduleJobVO jobVO : ScheduleJobVOS) {
            BatchTask task = taskMap.get(jobVO.getTaskId());
            if(Objects.isNull(task)){
                //已删除的任务
                task = this.batchTaskDao.getOneWithDeleted(jobVO.getTaskId());
            }
            if (Objects.isNull(task)) {
                continue;
            }
            final ScheduleTaskVO ScheduleTaskVO = new ScheduleTaskVO();
            BeanUtils.copyProperties(task, ScheduleTaskVO);
            final UserDTO ownerDTO = new UserDTO();
            BeanUtils.copyProperties(userMap.getOrDefault(ScheduleTaskVO.getOwnerUserId(),new User()), ownerDTO);
            ScheduleTaskVO.setOwnerUser(ownerDTO);
            final UserDTO createDTO = new UserDTO();
            BeanUtils.copyProperties(userMap.getOrDefault(ScheduleTaskVO.getCreateUserId(),new User()), createDTO);
            ScheduleTaskVO.setCreateUser(createDTO);
            jobVO.setBatchTask(ScheduleTaskVO);
            final ScheduleEngineJob ScheduleEngineJob = new ScheduleEngineJob();
            ScheduleEngineJob.setRetryNum(jobVO.getRetryNum());
            jobVO.setBatchEngineJob(ScheduleEngineJob);
        }
        returnPageResult.setData(ScheduleJobVOS);
        return returnPageResult;
    }

    public void fillProjectAndUserInfo(final List<ScheduleJobVO> vos) {
        final HashSet<Long> userIds = new HashSet<>();
        final HashSet<Long> projectIds = new HashSet<>();
        for (final ScheduleJobVO vo : vos) {
            userIds.add(vo.getOwnerUserId());
            userIds.add(vo.getCreateUserId());
            projectIds.add(vo.getProjectId());
            if(CollectionUtils.isNotEmpty(vo.getRelatedJobs())){
                for (final ScheduleJobVO relatedJob : vo.getRelatedJobs()) {
                    userIds.add(relatedJob.getOwnerUserId());
                    userIds.add(relatedJob.getCreateUserId());
                    projectIds.add(relatedJob.getProjectId());
                }
            }
        }
        final Map<Long, User> userMap = this.userDao.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, u -> u));
        final Map<Long, Project> projectMap = this.projectService.getProjectMap(projectIds);
        fillDataInfoWithQuery(vos, projectMap, userMap);
    }

    private void fillDataInfoWithQuery(final List<ScheduleJobVO> vos, final Map<Long, Project> projectsMap, final Map<Long, User> userMaps) {
        if (CollectionUtils.isEmpty(vos)) {
            return;
        }
        for (final ScheduleJobVO vo : vos) {
            final Project project = projectsMap.get(vo.getProjectId());
            final ScheduleTaskVO batchTask = vo.getBatchTask();
            if (Objects.nonNull(project)) {
                vo.setProjectName(project.getProjectName());
                batchTask.setProjectName(project.getProjectName());
            }
            if (Objects.nonNull(batchTask)) {
                final UserDTO ownerUser = new UserDTO();
                BeanUtils.copyProperties(userMaps.getOrDefault(batchTask.getOwnerUserId(),new User()), ownerUser);
                batchTask.setOwnerUser(ownerUser);
                final UserDTO createUser = new UserDTO();
                BeanUtils.copyProperties(userMaps.getOrDefault(batchTask.getCreateUserId(),new User()), createUser);
                batchTask.setCreateUser(createUser);
                final UserDTO modifyUser = new UserDTO();
                BeanUtils.copyProperties(userMaps.getOrDefault(batchTask.getModifyUserId(),new User()), modifyUser);
                batchTask.setModifyUser(modifyUser);
                vo.setBatchTask(batchTask);
            }
            if (CollectionUtils.isNotEmpty(vo.getJobVOS())) {
                fillDataInfoWithQuery(vo.getJobVOS(), projectsMap, userMaps);
            }
            if (CollectionUtils.isNotEmpty(vo.getRelatedJobs())) {
                fillDataInfoWithQuery(vo.getRelatedJobs(), projectsMap, userMaps);
            }
        }
    }

    public List<SchedulePeriodInfoVO> displayPeriods(boolean isAfter, Long jobId, Long projectId, int limit) {
        List<SchedulePeriodInfoVO> data = scheduleJobService.displayPeriods(isAfter, jobId, projectId, limit);
        return data == null ? Lists.newArrayList() : data;
    }


    /**
     * 获取工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    public ScheduleJobVO getRelatedJobs(String jobId, QueryJobDTO vo) {
        final ScheduleJobVO data = this.scheduleJobService.getRelatedJobs(jobId, JSON.toJSONString(vo));
        fillProjectAndUserInfo(Lists.newArrayList(data));
        return data;
    }


    /**
     * 获取任务的状态统计信息
     *
     * @author toutian
     */
    public Map queryJobsStatusStatistics(QueryJobDTO vo) {

        if (vo.getType() == null) {
            throw new RdosDefineException("类型必填", ErrorCode.INVALID_PARAMETERS);
        }
        vo.setAppType(AppType.RDOS.getType());
        return this.scheduleJobService.queryJobsStatusStatistics(vo);
    }

    public List<ScheduleRunDetailVO> jobDetail(Long taskId) {
        return this.scheduleJobService.jobDetail(taskId, AppType.RDOS.getType());
    }

    public String updateStatusById(String jobId, Integer status) {
        BatchJobService.logger.info("jobId:{} status:{}", jobId, status);
        this.scheduleJobService.updateJobStatusAndLogInfo(jobId, status, "");
        return jobId;
    }


    public String updateStatus(String jobId, Integer status, String msg) {
        BatchJobService.logger.info("jobId:{} status:{} msg:{}", jobId, status, msg);
        this.scheduleJobService.updateJobStatusAndLogInfo(jobId, status, "");
        return jobId;
    }

    /**
     * 初始化engine info接口extroInfo信息
     * @param batchTask
     * @param userId
     * @return info信息
     * @throws Exception
     */
    private String getExtraInfo(BatchTask batchTask, Long userId, List<BatchTaskParamShade> taskParamsToReplace) throws Exception {
        String extroInfo = "";
        Long taskId = batchTask.getId();
        // 跨项目的时候 需要依赖 task的project
        final Project project = this.projectService.getProjectById(batchTask.getProjectId());
        final Long dtuicTenantId = this.tenantService.getDtuicTenantId(batchTask.getTenantId());

        final Map<String, Object> actionParam = new HashMap<>(10);

        MultiEngineType multiEngineType = TaskTypeEngineTypeMapping.getEngineTypeByTaskType(batchTask.getTaskType());
        if (Objects.isNull(multiEngineType)) {
            //防止虚节点提交
            multiEngineType = MultiEngineType.HADOOP;
        }
        taskParamsToReplace = taskParamsToReplace == null ? this.batchTaskParamShadeService.getTaskParam(batchTask.getId()) : taskParamsToReplace;
        final IBatchJobExeService jobExecuteService = this.multiEngineServiceFactory.getBatchJobExeService(multiEngineType.getType());
        jobExecuteService.readyForTaskStartTrigger(actionParam, dtuicTenantId, project, batchTask, taskParamsToReplace);

        actionParam.put("taskId", taskId);
        actionParam.put("engineType", EngineType.getEngineName(batchTask.getEngineType()));
        actionParam.put("taskType", EJobType.getEngineJobType(batchTask.getTaskType()));
        actionParam.put("name", batchTask.getName());
        actionParam.put("computeType", batchTask.getComputeType());
        //dtuicTenantId
        actionParam.put("tenantId", dtuicTenantId);
        actionParam.put("isFailRetry", false);
        actionParam.put("maxRetryNum", 0);
        actionParam.put("multiEngineType", multiEngineType.getType());
        actionParam.put("taskParamsToReplace", JSON.toJSONString(taskParamsToReplace));

        if (EJobType.PYTHON.getVal().equals(batchTask.getTaskType()) || EJobType.SHELL.getVal().equals(batchTask.getTaskType())) {
            actionParam.put("engineType", EngineType.DtScript.getEngineName());
        } else if (EJobType.SPARK_PYTHON.getVal().equals(batchTask.getTaskType())) {


            actionParam.put("engineType", EngineType.Spark.getEngineName());
            actionParam.put("taskType", EJobType.SPARK_PYTHON.getVal());
        } else if (EJobType.HADOOP_MR.getVal().equals(batchTask.getTaskType())) {
            //mr任务配置main函数，并增加自定义参数支持。兼容老版本
            if (StringUtils.isEmpty(batchTask.getMainClass())){
                actionParam.put("sqlText", this.getHadoopMRSqlText(batchTask.getId(), project.getId()));
                // MR 任务提交不需要带--cmd-opts
                final JSONObject args = JSON.parseObject(batchTask.getExeArgs());
                actionParam.put("exeArgs", Objects.nonNull(args) ? args.get("--cmd-opts") : "");
            }
        } else if (EJobType.CARBON_SQL.getVal().equals(batchTask.getTaskType())) {
            actionParam.put("taskType", EJobType.SPARK_SQL.getVal());
        } else if (EJobType.SPARK.getVal().equals(batchTask.getTaskType())) {
            actionParam.put("engineType", EngineType.Spark.getEngineName());
        } else if (EJobType.HIVE_SQL.getVal().equals(batchTask.getTaskType())) {
            actionParam.put("engineType", EngineType.HIVE.getEngineName());
            actionParam.put("taskType", EJobType.HIVE_SQL.getEngineJobType());
        } else if (EJobType.TIDB_SQL.getVal().equals(batchTask.getTaskType())) {
            actionParam.put("engineType", EngineType.TIDB.getEngineName());
        } else if (EJobType.ORACLE_SQL.getVal().equals(batchTask.getTaskType())) {
            actionParam.put("engineType", EngineType.ORACLE.getEngineName());
        } else if (EJobType.GREENPLUM_SQL.getVal().equals(batchTask.getTaskType())) {
            actionParam.put("engineType", EngineType.GREENPLUM.getEngineName());
        }
        User user;
        if (userId == null) {
            user = userService.getUser(batchTask.getOwnerUserId());
        } else {
            user = userService.getUser(userId);
        }
        if (user != null) {
            actionParam.put("userId", user.getDtuicUserId());
        } else {
            throw new RdosDefineException(String.format("当前用户已被移除，userId：%d", userId == null ? batchTask.getOwnerUserId() : userId));
        }
        // 出错重试配置,兼容之前的任务，没有这个参数则默认重试
        final JSONObject scheduleConf = JSON.parseObject(batchTask.getScheduleConf());
        if (scheduleConf.containsKey("isFailRetry")) {
            actionParam.put("isFailRetry", scheduleConf.getBooleanValue("isFailRetry"));
            if (scheduleConf.getBooleanValue("isFailRetry")) {
                final int maxRetryNum = scheduleConf.getIntValue("maxRetryNum") == 0 ? 3 : scheduleConf.getIntValue("maxRetryNum");
                actionParam.put("maxRetryNum", maxRetryNum);
            } else {
                actionParam.put("maxRetryNum", 0);
            }
        }
        extroInfo = objMapper.writeValueAsString(actionParam);

        extroInfo = extroInfo.replaceAll("\r\n", System.getProperty("line.separator"));
        return extroInfo;
    }

    /**
     * 发送task 执行任务全部信息
     */
    public void sendTaskStartTrigger(Long taskId, Long userId, String commitId) throws Exception {
        BatchTask batchTask = this.batchTaskDao.getOne(taskId);
        if (batchTask == null) {
            throw new RdosDefineException("can not find task by id:" + taskId);
        }
        String extroInfo = getExtraInfo(batchTask, userId, null);
        //任务批量提交，调用infoCommit接口，此时只有调用taskCommit接口才会真正被engine调度
        this.scheduleTaskShadeService.infoCommit(taskId, AppType.RDOS.getType(), extroInfo, commitId);
    }

    private String getHadoopMRSqlText(final Long taskId, final Long projectId) {
        final List<BatchResource> resources = this.batchTaskResourceShadeService.listResourceByTaskId(taskId, ResourceRefType.MAIN_RES.getType(), projectId);
        if (CollectionUtils.isEmpty(resources)) {
            throw new RdosDefineException("HadoopMR任务资源不能为空");
        }
        return String.format(BatchJobService.ADD_JAR_WITH, resources.get(0).getUrl());
    }

    public String getTableName(final String table) {
        String simpleTableName = table;
        if (StringUtils.isNotEmpty(table)) {
            final String[] tablePart = table.split("\\.");
            if (tablePart.length == 1) {
                simpleTableName = tablePart[0];
            } else if (tablePart.length == 2) {
                simpleTableName = tablePart[1];
            }
        }

        return simpleTableName;
    }

    public String stopJob(long jobId, Long userId, Long projectId, Long tenantId, Long dtuicTenantId, Boolean isRoot) {

        final ScheduleJob ScheduleJob = this.scheduleJobService.getById(jobId);
        if (ScheduleJob == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }

        this.checkJobOperateValid(ScheduleJob, userId, ScheduleJob.getProjectId(), ScheduleJob.getTenantId(), isRoot);
        this.scheduleJobService.stopJob(jobId, AppType.RDOS.getType());
        return "success";
    }

    /**
     * 校验实例的操作权限
     * （主要针对跨项目实例）
     *
     * @param ScheduleJob
     * @param userId
     * @param projectId 项目id需要为任务本身id
     * @param tenantId
     * @param isRoot
     */
    private void checkJobOperateValid(final ScheduleJob ScheduleJob, final Long userId, final Long projectId, final Long tenantId, final Boolean isRoot) {
        final ScheduleTaskShade task = this.scheduleTaskShadeService.findTaskId(ScheduleJob.getTaskId(), Deleted.NORMAL.getStatus(), AppType.RDOS.getType());
        if (task != null) {
            this.roleUserService.checkUserRole(userId, RoleValue.OPERATION.getRoleValue(), ErrorCode.PERMISSION_LIMIT.getDescription(), projectId, tenantId, isRoot);
        }
    }

    public String stopJobByCondition(Long dtuicTenantId, KillJobVo vo, Long userId, Boolean isRoot) {
        ScheduleJobKillJobVO engineVO = new ScheduleJobKillJobVO();
        BeanUtils.copyProperties(vo, engineVO);
        engineVO.setAppType(AppType.RDOS.getType());
        engineVO.setDtuicTenantId(dtuicTenantId);
        engineVO.setRoot(isRoot);
        engineVO.setUserId(userId);
        Integer stringApiResponse = scheduleJobService.stopJobByCondition(engineVO);
        return "取消了" +  stringApiResponse + "个任务";
    }

    public void stopFillDataJobs(String fillDataJobName, Long projectId, Long dtuicTenantId) {
        this.scheduleJobService.stopFillDataJobs(fillDataJobName, projectId, dtuicTenantId, AppType.RDOS.getType());
    }


    @Transactional(rollbackFor = Exception.class)
    public int batchStopJobs(List<Long> jobIdList, Long userId, Long projectId, Long dtuicTenantId, Boolean isRoot) {
        if (CollectionUtils.isEmpty(jobIdList)) {
            return 0;
        }
        final List<ScheduleJob> jobs = this.scheduleJobService.getByIds(jobIdList);
        if (CollectionUtils.isEmpty(jobs)) {
            return 0;
        }

        final Set<Long> tenantIds = jobs.parallelStream().map(ScheduleJob::getTenantId).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(tenantIds)) {
            //校验权限
            for (final Long tenantId : tenantIds) {
                this.roleUserService.checkUserRole(userId, RoleValue.OPERATION.getRoleValue(), ErrorCode.PERMISSION_LIMIT.getDescription(), projectId, tenantId, isRoot);
            }
        }

        return this.scheduleJobService.batchStopJobs(jobIdList);
    }

    public String stopUnsubmitJob(ScheduleJob ScheduleJob) {
        //还未提交的只需要将本地的任务设置为取消状态即可
        this.updateStatusById(ScheduleJob.getJobId(), TaskStatus.CANCELED.getStatus());
        return "success";
    }
    /**
     * 补数据的时候，选中什么业务日期，参数替换结果是业务日期+1天
     */
    public String fillTaskData(String taskJson, String fillName, Long fromDay, Long toDay, String concreteStartTime, String concreteEndTime, Long projectId, Long userId, Long tenantId, Boolean isRoot, Long dtuicTenantId) {
        taskJson = StringUtils.isEmpty(taskJson) ? "[]" : taskJson;
        return this.scheduleJobService.fillTaskData(taskJson, fillName, fromDay, toDay, concreteStartTime, concreteEndTime, projectId, userId, tenantId, isRoot, AppType.RDOS.getType(), dtuicTenantId, false);
    }

    /**
     * 先查询出所有的补数据名称
     */
    public com.dtstack.batch.web.pager.PageResult<List<ScheduleFillDataJobPreViewVO>> getFillDataJobInfoPreview(String jobName, Long runDay, Long bizStartDay, Long bizEndDay, Long dutyUserId, Long projectId, Long userId, Long bizDay, Integer currentPage, Integer pageSize, Long tenantId) {

        if (Objects.isNull(bizStartDay)) {
            bizStartDay = bizDay;
        }
        if (Objects.isNull(bizEndDay)) {
            bizEndDay = bizDay;
        }
        PageResult fillDataJobInfoPreview = scheduleJobService.getFillDataJobInfoPreview(jobName, runDay, bizStartDay, bizEndDay, dutyUserId, projectId, AppType.RDOS.getType(), currentPage, pageSize, tenantId);
        if (Objects.isNull(fillDataJobInfoPreview) || Objects.isNull(fillDataJobInfoPreview.getData())) {
            return com.dtstack.batch.web.pager.PageResult.EMPTY_PAGE_RESULT;
        }
        com.dtstack.batch.web.pager.PageResult<List<ScheduleFillDataJobPreViewVO>> pageResult = new com.dtstack.batch.web.pager.PageResult<>();
        BeanUtils.copyProperties(fillDataJobInfoPreview, pageResult);

        List<ScheduleFillDataJobPreViewVO> batchFillDataJobPreViewVOS = (List<ScheduleFillDataJobPreViewVO>) fillDataJobInfoPreview.getData();
        List<Long> dutyUsers = batchFillDataJobPreViewVOS.stream().map(ScheduleFillDataJobPreViewVO::getDutyUserId).collect(Collectors.toList());
        Map<Long, User> userMap = userDao.listByIds(dutyUsers).stream().collect(Collectors.toMap(User::getId, u -> u));

        for (ScheduleFillDataJobPreViewVO batchFillDataJobPreViewVO : batchFillDataJobPreViewVOS) {
            if (null != batchFillDataJobPreViewVO.getDutyUserId()) {
                final User user = userMap.get(batchFillDataJobPreViewVO.getDutyUserId());
                if (Objects.nonNull(user)) {
                    //手动填写责任人
                    batchFillDataJobPreViewVO.setDutyUserName(user.getUserName());
                }
            }
        }
        pageResult.setData(batchFillDataJobPreViewVOS);
        return pageResult;
    }

    public com.dtstack.batch.web.pager.PageResult<ScheduleFillDataJobDetailVO> getFillDataDetailInfo(QueryJobDTO vo, List<String> flowJobIdList, String fillJobName, Long dutyUserId, String searchType) {
        if (Strings.isNullOrEmpty(fillJobName)) {
            throw new RdosDefineException("(补数据名称不能为空)", ErrorCode.INVALID_PARAMETERS);
        }
        vo.setSplitFiledFlag(true);
        PageResult<ScheduleFillDataJobDetailVO> fillDataDetailInfo = scheduleJobService.getFillDataDetailInfo(JSONObject.toJSONString(vo), flowJobIdList, fillJobName, dutyUserId, searchType, AppType.RDOS.getType());
        if (Objects.isNull(fillDataDetailInfo) || Objects.isNull(fillDataDetailInfo.getData())) {
            return com.dtstack.batch.web.pager.PageResult.EMPTY_PAGE_RESULT;
        }
        ScheduleFillDataJobDetailVO data = fillDataDetailInfo.getData();
        List<ScheduleFillDataJobDetailVO.FillDataRecord> ScheduleJobVOS = data.getRecordList();
        List<Long> userIds = ScheduleJobVOS.stream()
                .map(r -> r.getBatchTask().getCreateUserId())
                .collect(Collectors.toList());
        userIds.addAll(ScheduleJobVOS.stream()
                .map(r -> r.getBatchTask().getOwnerUserId())
                .collect(Collectors.toList()));
        if (CollectionUtils.isNotEmpty(userIds)) {
            Map<Long, User> userMap = userDao.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, u -> u));
            for (ScheduleFillDataJobDetailVO.FillDataRecord ScheduleJobVO : ScheduleJobVOS) {
                ScheduleTaskVO batchTask = ScheduleJobVO.getBatchTask();
                if (Objects.nonNull(batchTask)) {
                    final User user = userMap.getOrDefault(batchTask.getCreateUserId(),new User());
                    final UserDTO createUserDto = new UserDTO();
                    BeanUtils.copyProperties(user, createUserDto);
                    batchTask.setCreateUser(createUserDto);
                    final User ownUser = userMap.getOrDefault(batchTask.getOwnerUserId(),new User());
                    final UserDTO ownerUserDto = new UserDTO();
                    BeanUtils.copyProperties(ownUser, ownerUserDto);
                    batchTask.setOwnerUser(ownerUserDto);
                    if (Objects.nonNull(ownUser)) {
                        ScheduleJobVO.setDutyUserName(ownUser.getUserName());
                    }
                }
            }
        }
        com.dtstack.batch.web.pager.PageResult<ScheduleFillDataJobDetailVO> pageResult = new com.dtstack.batch.web.pager.PageResult<>();
        BeanUtils.copyProperties(fillDataDetailInfo, pageResult);
        return pageResult;
    }

    /**
     * 获取补数据实例工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    public ScheduleFillDataJobDetailVO.FillDataRecord  getRelatedJobsForFillData(String jobId, QueryJobDTO vo, String fillJobName) {
        final ScheduleFillDataJobDetailVO.FillDataRecord relatedJobsForFillData = this.scheduleJobService.getRelatedJobsForFillData(jobId, JSON.toJSONString(vo), fillJobName);
        final List<ScheduleFillDataJobDetailVO.FillDataRecord> records = new ArrayList<>();
        records.add(relatedJobsForFillData);
        if(CollectionUtils.isNotEmpty(relatedJobsForFillData.getRelatedRecords())){
            records.addAll(relatedJobsForFillData.getRelatedRecords());
        }
        final Set<Long> userIds = new HashSet<>();
        for (final ScheduleFillDataJobDetailVO.FillDataRecord record : records) {
            if(Objects.nonNull(record.getBatchTask())){
                userIds.add(record.getBatchTask().getCreateUserId());
                userIds.add(record.getBatchTask().getOwnerUserId());
                userIds.add(record.getBatchTask().getModifyUserId());
            }
        }
        final Map<Long, User> userMap = this.userService.getUserMap(userIds);
        if (Objects.nonNull(relatedJobsForFillData.getBatchTask())) {
            final ScheduleTaskVO batchTask = relatedJobsForFillData.getBatchTask();
            this.batchTaskService.buildUserDTOInfo(userMap, batchTask);
            if(Objects.nonNull(batchTask.getOwnerUser())){
                relatedJobsForFillData.setDutyUserName(batchTask.getOwnerUser().getUserName());
            }
        }
        if(CollectionUtils.isNotEmpty(relatedJobsForFillData.getRelatedRecords())){
            for (final ScheduleFillDataJobDetailVO.FillDataRecord relatedRecord : relatedJobsForFillData.getRelatedRecords()) {
                final ScheduleTaskVO batchTask = relatedRecord.getBatchTask();
                this.batchTaskService.buildUserDTOInfo(userMap, batchTask);
                if(Objects.nonNull(batchTask.getOwnerUser())){
                    relatedRecord.setDutyUserName(batchTask.getOwnerUser().getUserName());
                }
            }
        }
        return relatedJobsForFillData;
    }

    @Autowired
    private RoleUserService roleUserService;

    /**
     * 重跑并恢复调度/重跑下游并恢复调度/置为成功并恢复调度
     * 重跑指定job,只能重跑未运行、成功、失败状态的任务
     * 只会恢复同一天的调度任务
     * <p>
     *
     * @param jobId
     */
    @Transactional(rollbackFor = Exception.class)
    public Long restartJobAndResume(Long jobId, Boolean justRunChild, Boolean setSuccess, List<Long> subJobIds) {
        scheduleJobService.syncRestartJob(jobId, justRunChild, setSuccess, subJobIds);
        return jobId;
    }

    /**
     * @param jobIdList
     * @param runCurrentJob 只重跑当前节点
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public BatchOperatorVO batchRestartJobAndResume(List<Object> jobIdList, Boolean runCurrentJob) {

        final BatchOperatorVO<String> batchOperatorVO = new BatchOperatorVO<>();

        final int successNum = 0;
        int failNum = 0;

        for (final Object idStr : jobIdList) {
            try {
                final Long id = MathUtil.getLongVal(idStr);
                if (id == null) {
                    throw new RdosDefineException("convert id: " + idStr + " exception.", ErrorCode.SERVER_EXCEPTION);
                }

                final List<Long> subJobIds = new ArrayList<>();
                if (BooleanUtils.isTrue(runCurrentJob)){
                    subJobIds.add(id);
                }

                this.restartJobAndResume(id, false, false, subJobIds);
            } catch (final Exception e) {
                BatchJobService.logger.error("", e);
                failNum++;
            }
        }

        batchOperatorVO.setSuccessNum(successNum);
        batchOperatorVO.setFailNum(failNum);
        batchOperatorVO.setDetail("");
        return batchOperatorVO;
    }


    /**
     * 获取重跑的数据节点信息
     *
     * @param ScheduleJob
     * @param isOnlyNextChild
     * @return
     */
    public List<RestartJobVO> getRestartChildJob(ScheduleJob ScheduleJob, boolean isOnlyNextChild) {
        return this.scheduleJobService.getRestartChildJob(ScheduleJob.getJobKey(), ScheduleJob.getTaskId(), isOnlyNextChild);
    }



    /**
     * 运行同步任务
     * TODO 内容也需要迁移出去
     *
     * @return
     */
    public BatchStartSyncResultVO startSyncImmediately(Long taskId, Long userId, Boolean isRoot, Long dtuicTenantId, String taskParams) {
        BatchStartSyncResultVO batchStartSyncResultVO = new BatchStartSyncResultVO();
        batchStartSyncResultVO.setMsg(null);
        batchStartSyncResultVO.setJobId(null);
        batchStartSyncResultVO.setStatus(TaskStatus.SUBMITTING.getStatus());

        final BatchTask batchTask = this.batchTaskDao.getOne(taskId);
        if (batchTask == null) {
            throw new RdosDefineException("can not find task by id:" + taskId);
        }

        if (!batchTask.getTaskType().equals(EJobType.SYNC.getVal())) {
            throw new RdosDefineException("只支持同步任务直接运行");
        }

        try {
            List<String> resourceLimitErrors = scheduleTaskShadeService.checkResourceLimit(dtuicTenantId, batchTask.getTaskType(), taskParams, null);
            if (CollectionUtils.isNotEmpty(resourceLimitErrors)) {
                batchStartSyncResultVO.setMsg(StringUtils.join(resourceLimitErrors, ","));
                batchStartSyncResultVO.setStatus(TaskStatus.FAILED.getStatus());
                return batchStartSyncResultVO;
            }

            final IBatchJobExeService batchJobExeService = this.multiEngineServiceFactory.getBatchJobExeService(MultiEngineType.HADOOP.getType());
            final Map<String, Object> actionParam = batchJobExeService.readyForSyncImmediatelyJob(batchTask, dtuicTenantId, isRoot);
            String extroInfo = JSON.toJSONString(actionParam);
            ParamTaskAction paramTaskAction = new ParamTaskAction();
            ScheduleTaskShade scheduleTaskShade = JSON.parseObject(extroInfo, ScheduleTaskShade.class);
            JSONObject extroInfoObj = JSON.parseObject(extroInfo);
            extroInfoObj.put("engineType", EngineType.Flink.getEngineName());
            scheduleTaskShade.setExtraInfo(JSON.toJSONString(extroInfoObj));
            scheduleTaskShade.setAppType(AppType.RDOS.getType());
            scheduleTaskShade.setEngineType(batchTask.getEngineType());
            scheduleTaskShade.setTaskId(batchTask.getId());
            scheduleTaskShade.setDtuicTenantId(dtuicTenantId);
            scheduleTaskShade.setScheduleConf(batchTask.getScheduleConf());
            scheduleTaskShade.setComponentVersion(batchTask.getComponentVersion());
            paramTaskAction.setBatchTask(scheduleTaskShade);
            ParamActionExt paramActionExt = actionService.paramActionExt(paramTaskAction.getBatchTask(),paramTaskAction.getJobId(),paramTaskAction.getFlowJobId());
            String jobId = paramActionExt.getTaskId();
            actionService.start(paramActionExt);
            String name = MathUtil.getString(actionParam.get("name"));
            String job = MathUtil.getString(actionParam.get("job"));
            this.batchSelectSqlService.addSelectSql(jobId, name, TempJobType.SYNC_TASK.getType(), batchTask.getTenantId(),
                    batchTask.getProjectId(), job, userId, MultiEngineType.HADOOP.getType());

            batchStartSyncResultVO.setMsg("任务提交成功,名称为:" + name);
            batchStartSyncResultVO.setJobId(jobId);
            batchStartSyncResultVO.setStatus(TaskStatus.SUBMITTING.getStatus());

        } catch (final Exception e) {
            BatchJobService.logger.warn("startSyncImmediately-->", e);

            batchStartSyncResultVO.setMsg(e.getMessage());
            batchStartSyncResultVO.setStatus(TaskStatus.SUBMITFAILD.getStatus());
        }

        return batchStartSyncResultVO;
    }

    /**
     * 获取同步任务运行状态
     */
    public BatchGetSyncTaskStatusInnerResultVO getSyncTaskStatus(Long tenantId, String jobId, Long userId, Long projectId) {
        return this.getSyncTaskStatusInner(tenantId, jobId, userId, 0, projectId);
    }

    private BatchGetSyncTaskStatusInnerResultVO getSyncTaskStatusInner(final Long tenantId, final String jobId, final Long userId, int retryTimes, Long projectId) {
        final BatchGetSyncTaskStatusInnerResultVO resultVO = new BatchGetSyncTaskStatusInnerResultVO();
        resultVO.setMsg(null);
        resultVO.setStatus(TaskStatus.RUNNING.getStatus());

        try {
            final ScheduleJob job = this.scheduleJobService.getByJobId(jobId, Deleted.NORMAL.getStatus());
            if (job == null) {
                resultVO.setMsg("无法获取engine数据");
                return resultVO;
            }

            final Integer status = TaskStatusConstrant.getShowStatus(job.getStatus());
            resultVO.setStatus(status);
            if (TaskStatus.RUNNING.getStatus().equals(status)) {
                resultVO.setMsg("运行中");
            }

            final JSONObject logsBody = new JSONObject(2);
            logsBody.put("jobId", jobId);
            logsBody.put("jobIds", Lists.newArrayList(jobId));
            logsBody.put("computeType", ComputeType.BATCH.getType());
            ActionLogVO actionLogVO = actionService.log(jobId, ComputeType.BATCH.getType());
            String engineLogStr = actionLogVO.getEngineLog();
            final String logInfoStr = actionLogVO.getLogInfo();
            if(StringUtils.isNotBlank(engineLogStr)){
                //移除increConf 信息
                try {
                    final JSONObject engineLogJson = JSON.parseObject(engineLogStr);
                    engineLogJson.remove("increConf");
                    engineLogStr = engineLogJson.toJSONString();
                } catch (final Exception e) {
                    logger.error("", e);
                    if (TaskStatus.FINISHED.getStatus().equals(status) || TaskStatus.CANCELED.getStatus().equals(status)
                            || TaskStatus.FAILED.getStatus().equals(status)) {
                        resultVO.setMsg(engineLogStr);
                        resultVO.setDownload(String.format(BatchJobService.DOWNLOAD_URL, jobId, EJobType.SYNC.getVal(), projectId));
                    }
                    return resultVO;
                }
            }

            if (StringUtils.isEmpty(engineLogStr) && StringUtils.isEmpty(logInfoStr)) {
                return resultVO;
            }

            try {
                final JSONObject engineLog = JSON.parseObject(engineLogStr);
                final JSONObject logIngo = JSON.parseObject(logInfoStr);
                final StringBuilder logBuild = new StringBuilder();

                // 读取prometheus的相关信息
                final Tenant tenantById = this.tenantService.getTenantById(tenantId);
                if (tenantById == null) {
                    BatchJobService.logger.info("can not find job tenent{}.", tenantId);
                    throw new RdosDefineException(ErrorCode.SERVER_EXCEPTION);
                }
                List<ActionJobEntityVO> engineEntities = actionService.entitys(Collections.singletonList(jobId));

                String applicationId = "";
                if (CollectionUtils.isNotEmpty(engineEntities)) {
                    applicationId = engineEntities.get(0).getEngineJobId();
                }
                final long startTime = Objects.isNull(job.getExecStartTime()) ? System.currentTimeMillis(): job.getExecStartTime().getTime();
                final String perf = StringUtils.isBlank(applicationId) ? null : this.batchServerLogService.formatPerfLogInfo(applicationId,jobId, startTime, System.currentTimeMillis(), tenantById.getDtuicTenantId());
                if (StringUtils.isNotBlank(perf)) {
                    logBuild.append(perf.replace("\n", "  "));
                }

                if (TaskStatus.FAILED.getStatus().equals(status)) {
                    // 失败的话打印失败日志
                    logBuild.append("\n");
                    logBuild.append("====================Flink日志====================\n");


                    if (engineLog != null) {
                        if (StringUtils.isEmpty(engineLog.getString("root-exception")) && retryTimes < 3) {
                            retryTimes++;
                            Thread.sleep(500);
                            return this.getSyncTaskStatusInner(tenantId, jobId, userId, retryTimes, projectId);
                        } else {
                            if (engineLog.containsKey("engineLogErr")) {
                                // 有这个字段表示日志没有获取到，目前engine端只对flink任务做了这种处理，这里先提前加上
                                logBuild.append(engineLog.getString("engineLogErr"));
                            } else {
                                logBuild.append(engineLog.getString("root-exception"));
                            }
                            logBuild.append("\n");
                        }
                    }

                    if (logIngo != null) {
                        logBuild.append(logIngo.getString("msg_info"));
                        logBuild.append("\n");
                    }

                    final BatchHiveSelectSql batchHiveSelectSql = this.batchSelectSqlService.getByJobId(jobId, tenantId, 0);
                    if (batchHiveSelectSql != null) {
                        logBuild.append("====================任务信息====================\n");
                        final String sqlLog=batchHiveSelectSql.getCorrectSqlText().replaceAll("(\"password\"[^\"]+\")([^\"]+)(\")","$1**$3");
                        logBuild.append(JsonUtils.formatJSON(sqlLog));
                        logBuild.append("\n");
                    }
                } else if (TaskStatus.FINISHED.getStatus().equals(status) && retryTimes < 3) {
                    // FIXME perjob模式运行任务，任务完成后统计信息可能还没收集到，这里等待1秒后再请求一次结果
                    Thread.sleep(1000);
                    return this.getSyncTaskStatusInner(tenantId, jobId, userId, 3, projectId);
                }
                if (TaskStatus.FINISHED.getStatus().equals(status) || TaskStatus.CANCELED.getStatus().equals(status)
                        || TaskStatus.FAILED.getStatus().equals(status)) {
                    resultVO.setDownload(String.format(BatchJobService.DOWNLOAD_URL, jobId, EJobType.SYNC.getVal(), projectId));
                }
                resultVO.setMsg(logBuild.toString());
            } catch (final Exception e) {
                // 日志解析失败，可能是任务失败，日志信息为非json格式
                BatchJobService.logger.error("", e);
                resultVO.setMsg(StringUtils.isEmpty(engineLogStr) ? "engine调度失败" : engineLogStr);
            }
        } catch (final Exception e) {
            BatchJobService.logger.error("获取同步任务状态失败", e);
        }

        return resultVO;
    }

    /**
     * 停止同步任务
     */
    public void stopSyncJob(String jobId) {
        actionService.stop(Collections.singletonList(jobId));
    }


    /**
     * @param userId
     * @param tenantId
     * @param projectId
     * @param taskId
     * @param uniqueKey
     * @param sql
     * @param taskVariables
     * @param dtToken
     * @param isCheckDDL
     * @param isRoot
     * @param isEnd         是否是当前session最后一条sql
     * @param dtuicTenantId
     * @return
     */
    //通过数据地图权限判断
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, long projectId, long taskId, String uniqueKey, String sql, List<Map> taskVariables, String dtToken, Integer isCheckDDL, Boolean isRoot,  Boolean isEnd, Long dtuicTenantId, String taskParams) {

        // 更新ddl检查设置
        if (!Objects.isNull(isCheckDDL)) {
            SessionUtil.setValue(dtToken, BatchJobService.IS_CHECK_DDL_KEY, isCheckDDL);
        }
        final User user = this.userService.getUser(userId);
        dtToken = String.format("%s;dt_user_id=%s;dt_username=%s;",dtToken,user.getDtuicUserId(),user.getUserName());
        ExecuteResultVO result = new ExecuteResultVO();
        MultiEngineType multiEngineType = null;
        try {
            final Project project = this.projectService.getProjectById(projectId);
            if (project == null) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_PROJECT);
            }
            final BatchTask task = this.batchTaskDao.getOne(taskId);
            if (task == null) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
            }

            List<String> resourceLimitErrors = scheduleTaskShadeService.checkResourceLimit(dtuicTenantId, task.getTaskType(), taskParams, null);
            if (CollectionUtils.isNotEmpty(resourceLimitErrors)) {
                result.setMsg(StringUtils.join(resourceLimitErrors, ","));
                result.setStatus(TaskStatus.FAILED.getStatus());
                result.setSqlText(sql);
                return result;
            }
            List<BatchParamDTO> batchParamDTOS = this.batchTaskParamService.paramResolver(taskVariables);
            final List<BatchTaskParam> params = this.batchTaskParamService.convertParam(batchParamDTOS);
            List<BatchTaskParamShade> taskParamsToReplace = this.batchTaskParamService.convertShade(params);
            task.setSqlText(sql);
            ParamTaskAction paramTaskAction = getParamTaskAction(task, userId, taskParamsToReplace);
            ParamActionExt paramActionExt = actionService.paramActionExt(paramTaskAction.getBatchTask(),paramTaskAction.getJobId(),paramTaskAction.getFlowJobId());
            sql = paramActionExt.getSqlText();
            String jobId = paramActionExt.getTaskId();
            task.setTaskParams(paramActionExt.getTaskParams());
            multiEngineType = TaskTypeEngineTypeMapping.getEngineTypeByTaskType(task.getTaskType());
            final IBatchJobExeService batchJobService = this.multiEngineServiceFactory.getBatchJobExeService(multiEngineType.getType());
            result = batchJobService.startSqlImmediately(userId, tenantId, uniqueKey, projectId, taskId, sql, isRoot, dtuicTenantId, task, dtToken, isEnd, jobId);
        } catch (final Exception e) {
            BatchJobService.logger.warn("startSqlImmediately-->", e);
            result.setMsg(e.getMessage());
            result.setStatus(TaskStatus.FAILED.getStatus());
            result.setSqlText(sql);
            return result;
        } finally {
            if (null != multiEngineType && Objects.nonNull(result)) {
                result.setEngineType(multiEngineType.getType());
            }
        }
        return result;
    }

    /**
     * 高级运行sparkSql从引擎执行逻辑
     * @param userId
     * @param tenantId
     * @param projectId
     * @param taskId
     * @param uniqueKey
     * @param sqlList
     * @param taskVariables
     * @param dtToken
     * @param isCheckDDL
     * @param isRoot
     * @param dtuicTenantId
     * @return
     */
    public ExecuteSqlParseVO startSqlSophisticated(Long userId, Long tenantId, long projectId, long taskId, String uniqueKey, List<String> sqlList, List<Map> taskVariables, String dtToken, Integer isCheckDDL, Boolean isRoot, Long dtuicTenantId){
        // 更新ddl检查设置
        if (!Objects.isNull(isCheckDDL)) {
            SessionUtil.setValue(dtToken, BatchJobService.IS_CHECK_DDL_KEY, isCheckDDL);
        }
        ExecuteSqlParseVO result = new ExecuteSqlParseVO();
        MultiEngineType multiEngineType = null;
        try {

            final Project project = this.projectService.getProjectById(projectId);
            if (project == null) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_PROJECT);
            }
            final BatchTask task = this.batchTaskDao.getOne(taskId);
            if (task == null) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
            }
            final List<BatchTaskParam> params = this.batchTaskParamService.saveTaskParams(taskId, this.batchTaskParamService.paramResolver(taskVariables));
            multiEngineType = TaskTypeEngineTypeMapping.getEngineTypeByTaskType(task.getTaskType());
            final IBatchJobExeService batchJobExeService = this.multiEngineServiceFactory.getBatchJobExeService(multiEngineType.getType());
            sqlList = this.jobParamReplace.batchParamReplace(sqlList, this.batchTaskParamService.convertShade(params),
                    DateUtil.getFormattedDate(System.currentTimeMillis(), "yyyyMMddHHmmss"));
            result = batchJobExeService.startSqlSophisticated(userId,tenantId,uniqueKey,projectId,taskId,sqlList,isRoot,dtuicTenantId,task,dtToken,project.getProjectIdentifier());
        }catch (final Exception e) {
            BatchJobService.logger.warn("startSqlSophisticated-->", e);
            result.setMsg(e.getMessage());
            if (String.valueOf(e.getCause()).split(":").length>1) {
                result.setSqlText(Optional.ofNullable(String.valueOf(e.getCause()).split(":")[1]).orElse(StringUtils.EMPTY));
            }
            result.setStatus(TaskStatus.FAILED.getStatus());
            return result;
        } finally {
            if (null != multiEngineType && Objects.nonNull(result)) {
                result.setEngineType(multiEngineType.getType());
            }
        }
        return result;
    }


    /**
     * 停止通过sql任务执行的sql查询语句
     */
    public void stopSqlImmediately(String jobId, Long tenantId, Long projectId, Long dtuicTenantId) {
        if (StringUtils.isNotBlank(jobId)) {
            this.batchSelectSqlService.stopSelectJob(jobId, tenantId, projectId);
        }
    }


    /**
     * 运行报告
     *
     * @param taskId
     * @param count
     * @param projectId
     * @return
     */
    //FIXME 任务类型只统计了完成和失败的。。然后其他状态呢？运行中。。提交中。。。
    public ScheduleJobExeStaticsVO statisticsTaskRecentInfo(Long taskId, int count, Long projectId)  {
        final BatchTask task = this.batchTaskDao.getOne(taskId);
        if (task == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }

        final ScheduleJobExeStaticsVO result = new ScheduleJobExeStaticsVO();
        result.setTaskType(task.getTaskType());
        Integer fillDataNum = 0;
        Integer cronNum = 0;
        Integer failNum = 0;

        final List<Map<String, Object>> resultMap = this.scheduleJobService.statisticsTaskRecentInfo(taskId, AppType.RDOS.getType(), projectId, count);
        for ( Map<String, Object> map : resultMap) {
            Object execStartTime = map.getOrDefault("execStartTime","0");
            Object execEndTime = map.getOrDefault("execEndTime","0");
            String jobId = MapUtils.getString(map, "jobId");
            Object application = jobId;
            //需要转化出ApplicationId
            JSONObject logsBody = new JSONObject(2);
            logsBody.put("jobId", jobId);
            logsBody.put("computeType", ComputeType.BATCH.getType());

            ActionLogVO log = actionService.log(jobId, ComputeType.BATCH.getType());
            if (null != log){
                JSONObject logInfo =  log.getLogInfo() != null && log.getLogInfo().contains("jobid") ? JSONObject.parseObject(log.getLogInfo()) : null;
                if (logInfo != null){
                    application = logInfo.getOrDefault("jobid",jobId);
                }
            }

            final Object status = map.get("status");
            final Object execTime = map.get("execTime");
            final Integer type = MathUtil.getIntegerVal(map.get("type"));

            if (EScheduleType.NORMAL_SCHEDULE.getType() == type) {
                cronNum++;
            } else {
                fillDataNum++;
            }
            if (status != null) {
                final Integer statusVal = MathUtil.getIntegerVal(status);
                if (statusVal.equals(TaskStatus.FINISHED.getStatus())) {
                    final ScheduleJobExeStaticsVO.BatchJobInfo vo = new ScheduleJobExeStaticsVO.BatchJobInfo();
                    vo.setJobId(MathUtil.getString(jobId));
                    vo.setExeTime(MathUtil.getIntegerVal(execTime));
                    vo.setExeStartTime(MathUtil.getLongVal(execStartTime));

                    if (EJobType.SYNC.getVal().equals(task.getTaskType())) {
                        //需要添加读取数据条数和脏数据信息
                        try {
                            /**
                             * engine_job表中得log_info字段获取jobid从prometheus中取任务执行信息
                             */
                            final SyncStatusLogInfoVO syncJobLogInfo = this.batchServerLogService.getSyncJobLogInfo(application.toString(),taskId,  Long.parseLong(execStartTime.toString()), Long.parseLong( execEndTime.toString()), this.tenantService.getDtuicTenantId(task.getTenantId()));
                            vo.setTotalCount(Math.toIntExact(syncJobLogInfo.getNumRead()));
                            vo.setDirtyNum(Math.toIntExact(syncJobLogInfo.getNErrors()));
                        } catch (final Exception e) {
                            BatchJobService.logger.error("", e);
                        }
                    }
                    result.addBatchJob(vo);
                } else if (statusVal.equals(TaskStatus.FAILED.getStatus())) {
                    failNum++;
                }
            }
        }

        result.setFillDataExeNum(fillDataNum);
        result.setCronExeNum(cronNum);
        result.setFailNum(failNum);

        return result;
    }

    public List<String> listJobIdByTaskNameAndStatusList(String taskName, List<Integer> statusList, Long projectId) {
        return this.scheduleJobService.listJobIdByTaskNameAndStatusList(taskName, statusList, projectId, AppType.RDOS.getType());
    }


    /**
     * 返回这些jobId对应的父节点的jobMap
     *
     * @param jobIdList
     * @param projectId
     * @return
     */
    public Map<String, ScheduleJob> getLabTaskRelationMap(List<String> jobIdList, Long projectId) {
        return this.scheduleJobService.getLabTaskRelationMap(jobIdList, projectId);
    }


    public String getEngineJobId(String jobId) {
        List<ActionJobEntityVO> engineEntities = actionService.entitys(Lists.newArrayList(jobId));
        if (CollectionUtils.isNotEmpty(engineEntities)) {
            return engineEntities.get(0).getEngineJobId();
        }
        return "";
    }

    /**
     * 从engine获取engineLog
     *
     * @param jobId
     * @return
     */
    public JSONObject getLogInfoFromEngine(String jobId) {
        //先获取job的日志 如果日志为空 再去engine获取
        ActionLogVO log = actionService.log(jobId, ComputeType.BATCH.getType());
        if (Objects.isNull(log)) {
            return new JSONObject();
        }
        return JSONObject.parseObject(JSONObject.toJSONString(log));
    }

    /**
     * 迁移对应的task 任务信息到
     */
    private volatile boolean isJobPrevious;

    protected static final Logger LOG = LoggerFactory.getLogger(BatchJobService.class);

    @Autowired
    private BatchTaskShadeDao batchTaskShadeDao;

    /**
     * 迁移对应的task 任务信息到 调度
     */
    public void previousJobData() {
        List<TaskIdAndVersionIdPO> taskIds = batchTaskShadeDao.listTaskIdAndVersionId();
        if (CollectionUtils.isEmpty(taskIds)) {
            return;
        }
        if (isJobPrevious) {
            return;
        }
        if (!isJobPrevious) {
            isJobPrevious = true;
        }
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(5000), new RdosThreadFactory("previousJobData"), new ThreadPoolExecutor.CallerRunsPolicy());
        for (TaskIdAndVersionIdPO task : taskIds) {
            executor.submit(() -> {
                BatchTaskVersionDetail bytaskIdAndVersionId = batchTaskVersionDao.getBytaskIdAndVersionId(task.getTaskId(), task.getVersionId());
                LOG.info("previous taskVersion {} success", bytaskIdAndVersionId.toString());
                String commitId = null;
                try {
                    ScheduleTaskShadeDTO dto = new ScheduleTaskShadeDTO();
                    dto.setTaskId(task.getTaskId());
                    dto.setProjectId(task.getProjectId());
                    dto.setVersionId(Integer.parseInt(bytaskIdAndVersionId.getId().toString()));
                    dto.setAppType(AppType.RDOS.getType());
                    commitId = scheduleTaskShadeService.addOrUpdateBatchTask(Lists.newArrayList(dto), null);
                    this.sendTaskStartTrigger(task.getTaskId(), null, commitId);
                    scheduleTaskShadeService.taskCommit(commitId);
                    LOG.info("previous sendTask {} success", task.toString());
                } catch (Exception e) {
                    LOG.error("previous sendTask {} error", task.toString(), e);
                }
            });
        }
        LOG.info("-------------submit previous task  finish -------------");
        isJobPrevious = false;
    }

    /**
     * 初始化engine paramActionExt 入参
     * @param batchTask
     * @param userId
     * @param taskParamsToReplace
     * @return
     * @throws Exception
     */
    private ParamTaskAction getParamTaskAction(BatchTask batchTask, Long userId, List<BatchTaskParamShade> taskParamsToReplace) throws Exception {
        ParamTaskAction paramTaskAction = new ParamTaskAction();
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        BeanUtils.copyProperties(batchTask, scheduleTaskShade);
        scheduleTaskShade.setTaskId(batchTask.getId());
        scheduleTaskShade.setAppType(AppType.RDOS.getType());
        scheduleTaskShade.setTaskType(getParamTaskActionTaskType(batchTask.getTaskType()));
        scheduleTaskShade.setEngineType(batchTask.getEngineType());
        scheduleTaskShade.setDtuicTenantId(tenantService.getDtuicTenantId(batchTask.getTenantId()));
        String extraInfo = getExtraInfo(batchTask, userId, taskParamsToReplace);
        JSONObject jsonObject = JSON.parseObject(extraInfo);
        if (jsonObject.containsKey("sqlText")) {
            jsonObject.put("sqlText", batchTask.getSqlText());
        }
        extraInfo = jsonObject.toJSONString();
        scheduleTaskShade.setExtraInfo(extraInfo);
        paramTaskAction.setBatchTask(scheduleTaskShade);
        return paramTaskAction;
    }

    /**
     * 根据任务类型生成engine任务类型
     * @param taskType 离线任务类型
     * @return 引擎任务类型
     */
    private Integer getParamTaskActionTaskType(Integer taskType) {
        Integer paramTaskActionTaskType = EJobType.getEngineJobType(taskType);
        if (EJobType.CARBON_SQL.getVal().equals(taskType)) {
            paramTaskActionTaskType = EJobType.SPARK_SQL.getEngineJobType();
        }
        return paramTaskActionTaskType;
    }

    /**
     * 根据jobId查询质量任务
     * @param jobId
     * @return
     */
    public ScheduleDetailsVO findTaskRuleJob(String jobId) {
        return scheduleJobService.findTaskRuleJob(jobId);
    }
}

