package com.dtstack.engine.datadevelop.service.impl;

import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.common.enums.ProjectCreateModel;
import com.dtstack.batch.common.env.EnvironmentContext;
import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.*;
import com.dtstack.batch.domain.*;
import com.dtstack.batch.domain.po.ProjectCataloguePO;
import com.dtstack.batch.dto.ProjectDTO;
import com.dtstack.batch.engine.rdbms.common.HdfsOperator;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.enums.EScheduleType;
import com.dtstack.batch.enums.RdosBatchCatalogueTypeEnum;
import com.dtstack.batch.enums.TaskEngineType;
import com.dtstack.batch.mapping.DataSourceTypeJobTypeMapping;
import com.dtstack.batch.mapping.TableTypeEngineTypeMapping;
import com.dtstack.batch.mapstruct.vo.ProjectMapstructTransfer;
import com.dtstack.batch.service.alarm.impl.BatchAlarmRecordService;
import com.dtstack.batch.service.alarm.impl.BatchAlarmService;
import com.dtstack.batch.service.datamask.impl.DataMaskConfigService;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceMigrationService;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceService;
import com.dtstack.batch.service.datasource.impl.IMultiEngineService;
import com.dtstack.batch.service.model.impl.BatchModelMonitorDataService;
import com.dtstack.batch.service.model.impl.BatchModelTableService;
import com.dtstack.batch.service.multiengine.EngineInfo;
import com.dtstack.batch.service.project.IProjectService;
import com.dtstack.batch.service.table.impl.BatchDataCatalogueService;
import com.dtstack.batch.service.table.impl.BatchHiveSelectSqlService;
import com.dtstack.batch.service.table.impl.BatchTableCountService;
import com.dtstack.batch.service.table.impl.BatchTableInfoService;
import com.dtstack.batch.service.task.impl.BatchTaskService;
import com.dtstack.batch.service.task.impl.ReadWriteLockService;
import com.dtstack.batch.service.testproduct.impl.BatchPackageService;
import com.dtstack.batch.vo.*;
import com.dtstack.batch.web.pager.PageQuery;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.batch.web.project.vo.result.BatchCompareIntrinsicTableResultVO;
import com.dtstack.batch.web.project.vo.result.BatchProjectGetUicNotInProjectResultVO;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.constant.PatternConstant;
import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.dtcenter.common.lock.RedLock;
import com.dtstack.dtcenter.common.login.DtUicUserConnect;
import com.dtstack.dtcenter.common.login.domain.UserTenant;
import com.dtstack.dtcenter.common.thread.RdosThreadFactory;
import com.dtstack.dtcenter.common.util.MathUtil;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.api.param.ScheduleEngineProjectParam;
import com.dtstack.engine.api.service.ClusterService;
import com.dtstack.engine.api.service.DataSourceService;
import com.dtstack.engine.api.service.ScheduleJobService;
import com.dtstack.engine.api.vo.EngineTenantVO;
import com.dtstack.engine.api.vo.lineage.param.DeleteDataSourceParam;
import com.dtstack.engine.api.vo.project.NotDeleteProjectVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobStatusCountVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobStatusVO;
import com.dtstack.science.model.param.project.GetScienceProjectsParam;
import com.dtstack.science.model.vo.project.ScienceProjectsVO;
import com.dtstack.science.service.project.ScienceProjectService;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightApi;
import com.dtstack.uic.client.UIcUserTenantRelApiClient;
import com.dtstack.uic.domain.vo.TenantUsersVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author sishu.yss
 */
@Service
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(com.dtstack.batch.service.impl.ProjectService.class);

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private RoleUserDao roleUserDao;

    @Autowired
    private BatchTableInfoDao batchTableInfoDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private com.dtstack.batch.service.impl.RoleUserService roleUserService;

    @Autowired
    private com.dtstack.batch.service.impl.RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private com.dtstack.batch.service.impl.TenantService tenantService;

    @Autowired
    private com.dtstack.batch.service.impl.ProjectStickService projectStickService;

    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private ProjectEngineDao projectEngineDao;

    @Autowired
    private BatchDataCatalogueService batchDataCatalogueService;

    @Autowired
    private BatchCatalogueService batchCatalogueService;

    @Autowired
    private BatchModelTableService batchModelTableService;

    @Autowired
    private BatchTableCountService batchTableCountService;

    @Autowired
    private BatchTaskService batchTaskService;

    @Autowired
    private BatchTableInfoService batchTableInfoService;

    @Resource(name = "multiEngineService")
    private IMultiEngineService multiEngineService;

    @Autowired
    com.dtstack.engine.api.service.TenantService engineTenantService;

    @Autowired
    private MultiEngineServiceFactory multiEngineServiceFactory;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private com.dtstack.engine.api.service.ProjectService engineProjectService;

    @Autowired
    private BatchAlarmRecordService batchAlarmRecordService;

    @Autowired
    private BatchApplyService batchApplyService;

    @Autowired
    private BatchDataSourceService batchDataSourceService;

    @Autowired
    private BatchDataSourceMigrationService batchDataSourceMigrationService;

    @Autowired
    private BatchFunctionService batchFunctionService;

    @Autowired
    private BatchResourceService batchResourceService;

    @Autowired
    private BatchScriptService batchScriptService;

    @Autowired
    private NotifyRecordService notifyRecordService;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private BatchHiveSelectSqlService batchHiveSelectSqlService;

    @Autowired
    private ReadWriteLockService readWriteLockService;

    @Autowired
    private BatchPackageService batchPackageService;

    @Autowired
    private DataMaskConfigService dataMaskConfigService;

    @Autowired
    private BatchDirtyDataService batchDirtyDataService;

    @Autowired
    private BatchModelMonitorDataService batchModelMonitorDataService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private DtInsightApi engineApi;

    @Autowired
    private BatchAlarmService batchAlarmService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScienceProjectService scienceProjectService;

    @Autowired
    private UIcUserTenantRelApiClient uIcUserTenantRelApiClient;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private BatchFileManageRuleService batchFileManageRuleService;

    private RedLock dealIntrinsicTableLock;

    private final Lock createProjectLock = new ReentrantLock();

    private static final Integer STICKY = 1;

    private static final Integer NOT_STICKY = 0;

    private static final String OrderBy_STICK = "rps.stick";

    private static final String OrderBy_JOBSUM = "jobSum";

    public static final List<Integer> FAILED_STATUS = Lists.newArrayList(TaskStatus.FAILED.getStatus(), TaskStatus.SUBMITFAILD.getStatus());

    private final ExecutorService executorService = new ThreadPoolExecutor(8, 8, 60L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(5000), new RdosThreadFactory("project"), new ThreadPoolExecutor.CallerRunsPolicy());

    private static final String CHECK_EMPTY_FUNC_NAME = "isEmpty";

    private static final String BACKUP_DB_PRE = "backup_";

    private static final Integer NORMAL_TABLE = 0;

    private static final List<String> CHECK_EMPTY_OBJECT_NAMES = Arrays.asList(
            "batchFunctionService",
            "batchTableInfoService"
    );

    // 租户支持的表类型
    private static final List<Integer> supportTenantTableList = Lists.newArrayList(ETableType.HIVE.getType(),
            ETableType.TIDB.getType(), ETableType.ADB_FOR_PG.getType());

    // 项目支持的表类型
    private static final List<Integer> supportProjectTableList = Lists.newArrayList(ETableType.HIVE.getType(),
            ETableType.TIDB.getType(), ETableType.LIBRA.getType(), ETableType.ORACLE.getType(), ETableType.GREENPLUM.getType());

    @PostConstruct
    private void init(){
        dealIntrinsicTableLock = new RedLock(redisTemplate).init("dealIntrinsicTable");
    }

    /**
     * 删除所有和项目有关的业务数据
     * @param projectId 项目ID
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(Long tenantId, Long projectId, Long userId) {
        //删除项目相关
        deleteProjectRelated(projectId, userId);
        //删除任务相关
        deleteTaskRelated(projectId, userId);
        //删除表相关
        deleteTableRelated(projectId, userId);
        //删除告警相关
        deleteAlarmRelated(projectId);
        //删除数据源、数据源和任务关联
        batchDataSourceService.deleteByProjectId(tenantId, projectId, userId);
        //删除整库同步配置、任务、日志
        batchDataSourceMigrationService.deleteByProjectId(projectId);
        //删除权限申请记录
        batchApplyService.deleteByProjectId(projectId, userId);
        //删除目录
        batchCatalogueService.deleteByProjectId(projectId);
        //删除函数
        batchFunctionService.deleteByProjectId(projectId, userId);
        //删除资源
        batchResourceService.deleteByProjectId(projectId, userId);
        //删除脚本
        batchScriptService.deleteByProjectId(projectId, userId);
        //删除角色
        roleService.deleteByProjectId(projectId, userId);
        //删除读写锁
        readWriteLockService.deleteByProjectId(projectId, userId);
        //删除发布包信息
        batchPackageService.deleteByProjectId(projectId);
        //删除对应的治理规则
        batchFileManageRuleService.deleteRuleByProjectId(projectId, userId);
    }

    /**
     * 删除项目相关数据
     * @param projectId
     */
    private void deleteProjectRelated(Long projectId, Long userId) {
        //删除项目
        projectDao.deleteById(projectId, userId);
        //删除项目置顶信息
        projectStickService.deleteByProjectId(projectId, userId);
        //删除项目engine信息
        projectEngineService.deleteByProjectId(projectId, userId);
        engineProjectService.deleteProject(projectId, AppType.RDOS.getType());
    }

    /**
     * 删除告警相关数据
     * @param projectId
     */
    private void deleteAlarmRelated(Long projectId) {
        //删除告警相关信息
        batchAlarmService.deleteByProjectId(projectId);
        //删除告警记录
        batchAlarmRecordService.deleteByProjectId(projectId);
        //删除通知
        notifyService.deleteByProjectId(projectId);
        //删除消息记录
        notifyRecordService.deleteByProjectId(projectId);
    }

    /**
     * 删除任务相关数据
     * @param projectId
     * @param userId
     */
    private void deleteTaskRelated(Long projectId, Long userId) {
        //删除任务
        batchTaskService.deleteByProjectId(projectId, userId);
        //删除sql查询临时
        batchHiveSelectSqlService.deleteByProjectId(projectId);
    }

    /**
     * 删除表相关数据
     * @param projectId
     * @param userId
     */
    private void deleteTableRelated(Long projectId, Long userId) {
        //删除表相关信息
        batchTableInfoService.deleteByProjectId(projectId, userId);
        //删除表和项目数量历史数据
        batchTableCountService.deleteByProjectId(projectId);
        //删除数据脱敏配置
        dataMaskConfigService.deleteByProjectId(projectId, userId);
        //删除脏数据数量统计
        batchDirtyDataService.deleteByProjectId(projectId);
        //删除字段不规范
        batchModelMonitorDataService.deleteByProjectId(projectId);
    }

    /**
     * 删除项目
     * @param isRoot 是否超管用户
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param projectId 项目ID
     * @return 被删除的项目ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long deleteProject(Boolean isRoot, Long userId, Long tenantId, Long projectId) {
        ProjectDelCheckResultVo checkResultVo = preDeleteProject(isRoot, userId, tenantId, projectId);
        if (StringUtils.isNotEmpty(checkResultVo.getErrorMsg()) || CollectionUtils.isNotEmpty(checkResultVo.getTaskList())) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_DELETE_PROJECT);
        }
        deleteAll(tenantId, projectId, userId);
        closeOrOpenSchedule(tenantId, projectId, 1, userId);
        return projectId;
    }

    /**
     * 删除项目前置判断
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param projectId 项目ID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ProjectDelCheckResultVo preDeleteProject(Boolean isRoot, Long userId, Long tenantId, Long projectId) {
        ProjectDelCheckResultVo checkResult = new ProjectDelCheckResultVo();
        Project project = getProjectById(projectId);
        //判断项目状态是否正常
        if (ProjectStatus.INITIALIZE.getStatus() == project.getStatus()){
            checkResult.setErrorMsg("该项目正在创建中，不允许被删除！");
            return checkResult;
        }
        if (project.getProduceProjectId() != null) {
            Project produceProject = getProjectById(project.getProduceProjectId());
            checkResult.setErrorMsg(String.format("该项目绑定了项目\"%s\"，无法删除！", produceProject.getProjectAlias()));
            return checkResult;
        }

        if(!isRoot && !roleUserService.isTenantOwner(userId, tenantId)) {
            //不是超管和租户所有者时只能删除创建失败的项目
            if (project.getStatus() != ProjectStatus.FAIL.getStatus()) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_DELETE_NORMAL_PROJECT);
            } else {
                projectDao.deleteById(projectId, userId);
                engineProjectService.deleteProject(projectId, AppType.RDOS.getType());
                return checkResult;
            }
        }

        List<NotDeleteProjectVO> projectVOS = engineProjectService.getNotDeleteTaskByProjectId(projectId, AppType.RDOS.getType()).getData();
        if (CollectionUtils.isNotEmpty(projectVOS)) {
            checkResult.setTaskList(projectVOS);
        }
        return checkResult;
    }

    /**
     * 获取租户支持引擎类型
     *
     * @return
     */
    public List<Integer> getSupportEngineType(Long dtuicTenantId) {
        return this.getEngineTypeByTenant(dtuicTenantId);
    }

    /**
     * 获取Hadoop meta数据源名称
     *
     * @return
     */
    public String getHadoopMetaDataSourceName(Long dtuicTenantId) {
        return multiEngineService.getTenantSupportHadoopMetaDataSource(dtuicTenantId).getName();
    }

    /**
     * 获取项目在用的引擎信息
     *
     * @param tenantId
     * @param projectId
     * @return
     */
    public Map<Integer, EngineInfo> getProjectUsedEngineInfo(Long tenantId, Long dtuicTenantId, Long projectId) {
        List<Integer> engineTypeList = projectEngineService.getUsedEngineTypeList(projectId);
        Map<Integer, EngineInfo> resultMap = Maps.newHashMap();

        engineTypeList.forEach(type -> {
            EngineInfo engineInfo = multiEngineService.getEnginePluginInfo(dtuicTenantId, type, projectId);
            resultMap.put(engineInfo.getEngineTypeEnum().getType(), engineInfo);
        });

        return resultMap;
    }

    /**
     * 获取项目未使用的引擎信息
     *
     * @param tenantId
     * @param dtuicTenantId
     * @param projectId
     * @return
     */
    public Map<Integer, EngineInfo> getProjectUnusedEngineInfo(Long tenantId, Long dtuicTenantId, Long projectId) {
        List<Integer> engineTypeList = projectEngineService.getUsedEngineTypeList(projectId);
        List<Integer> supportType = getEngineTypeByTenant(dtuicTenantId);
        engineTypeList.forEach(key -> supportType.remove(key));

        Map<Integer, EngineInfo> resultMap = Maps.newHashMap();

        supportType.forEach(type -> {
            EngineInfo engineInfo = multiEngineService.getEnginePluginInfo(dtuicTenantId, type, projectId);
            if (null != engineInfo) {
                resultMap.put(engineInfo.getEngineTypeEnum().getType(), engineInfo);
            }
        });

        return resultMap;
    }


    /**
     * 项目添加新的引擎类型
     *
     * @param projectEngineVO
     * @param userId
     * @param dtuicTenantId
     * @param projectId
     */
    public Long addNewEngine(ProjectEngineVO projectEngineVO, Long userId, Long dtuicTenantId, Long projectId) {

        //判断引擎是否已经使用过
        ProjectEngine projectEngine = projectEngineService.getProjectDb(projectId, projectEngineVO.getEngineType());
        if (projectEngine != null) {
            throw new RdosDefineException(String.format("当前项目已经添加过该引擎"));
        }

        Project project = projectDao.getOne(projectId);
        final String key = this.getAddNewEngineRedisKey(projectId,projectEngineVO.getEngineType());
        this.executorService.execute(() -> {
            try {
                //初始状态为0
                this.redisTemplate.opsForValue().set(key,"0",1L, TimeUnit.HOURS);
                initSingleProjectEngine(projectEngineVO, project, userId, dtuicTenantId);
                this.redisTemplate.opsForValue().set(key,"1",1L, TimeUnit.HOURS);
            } catch (Exception e) {
                this.redisTemplate.opsForValue().set(key,"-1",1L, TimeUnit.HOURS);
                final String infoKey = this.getAddNewEngineRedisErrorInfoKey(projectId, projectEngineVO.getEngineType());
                this.redisTemplate.opsForValue().set(infoKey,e.getMessage(),1L, TimeUnit.HOURS);
                logger.error("添加项目 {} 引擎失败 {} ", projectId, e);
                throw new RdosDefineException(String.format("添加引擎失败"));
            }
        });

        return projectId;
    }

    private String getAddNewEngineRedisKey(final Long projectId, final Integer engineType){
        return String.format("IDE:ADD_NEW_ENGINE:STATUS:%s_%s",projectId,engineType);
    }

    private String getAddNewEngineRedisErrorInfoKey(final Long projectId, final Integer engineType){
        return String.format("IDE:ADD_NEW_ENGINE:INFO:%s_%s",projectId,engineType);
    }

    public Integer hasSuccessInitEngine(Long projectId, Integer engineType) {
        final String statusKey = this.getAddNewEngineRedisKey(projectId,engineType);
        final String s = this.redisTemplate.opsForValue().get(statusKey);
        if ("1".equalsIgnoreCase(s)){
            return 1;
        }else if ("-1".equalsIgnoreCase(s)){
            final String infoKey = this.getAddNewEngineRedisErrorInfoKey(projectId, engineType);
            final String errorInfo = this.redisTemplate.opsForValue().get(infoKey);
            throw new RdosDefineException("添加引擎失败:"+errorInfo);
        }else {
            return 0;
        }
    }

    private List<Integer> getEngineTypeByTenant(Long dtuicTenantId) {
        return multiEngineService.getTenantSupportMultiEngine(dtuicTenantId);
    }


    private List<BatchTableTypeVo> convertEngineTypeToTableType(List<Integer> engineTypes) {
        if (CollectionUtils.isEmpty(engineTypes)) {
            return new ArrayList<>();
        } else {

            final List<BatchTableTypeVo> list = new ArrayList<>();

             engineTypes.forEach(engineType -> {
                        final ETableType tableTypeByEngineType = TableTypeEngineTypeMapping.getTableTypeByEngineType(engineType);
                        if (tableTypeByEngineType !=null) {
                            list.add(new BatchTableTypeVo(tableTypeByEngineType.getType(), ETableType.getTableType(tableTypeByEngineType.getType()).getContent()));
                        }
                    });
            return list;
        }

    }

    /**
     * 获取租户支持表类型
     *
     * @param dtuicTenantId
     * @return
     */
    public List<BatchTableTypeVo> getSupportTableType(Long dtuicTenantId) {
        // 得到当前租户支持的引擎类型
        List<Integer> integerList = getEngineTypeByTenant(dtuicTenantId);
        // 得到租户下支持的表类型对应的引擎类型
        List<Integer> supportEngineList = supportTenantTableList.stream().map(tableType ->
            TableTypeEngineTypeMapping.getEngineTypeByTableType(tableType).getType()
        ).collect(Collectors.toList());
        integerList.retainAll(supportEngineList);
        return convertEngineTypeToTableType(integerList);
    }

    /**
     * 获取项目支持表类型
     *
     * @param projectId
     * @return
     */
    public List<BatchTableTypeVo> getProjectSupportTableType(Long projectId) {
        // 得到当前项目支持的引擎类型
        List<Integer> getUsedEngineTypeList = projectEngineService.getUsedEngineTypeList(projectId);
        // 得到项目中支持的表类型对应的引擎类型
        List<Integer> supportEngineList = supportProjectTableList.stream().map(tableType ->
                TableTypeEngineTypeMapping.getEngineTypeByTableType(tableType).getType()
        ).collect(Collectors.toList());
        getUsedEngineTypeList.retainAll(supportEngineList);
        return convertEngineTypeToTableType(getUsedEngineTypeList);
    }


    /**
     * 获取项目引擎
     *
     * @param projectId
     * @return
     */
    public List<BatchTableTypeVo> getProjectSupportEngineType(Long projectId) {
        List<Integer> engineTypes = projectEngineService.getUsedEngineTypeList(projectId);
        return engineTypes.stream()
                .map(engineType -> {
                    MultiEngineType multiEngineType = MultiEngineType.getByType(engineType);
                    return null == multiEngineType ? null :
                            new BatchTableTypeVo(multiEngineType.getType(), multiEngineType.getName());
                }).collect(Collectors.toList());
    }

    /**
     * @TODO 判断对比租户集合中是否存在租户和当前租户在一个集群
     * @param tenantId 当前租户id
     * @param tidList  对比租户id集合
     * @return true：对比租户中存在租户和当前租户在一个集群
     *         false：对比租户中不存在租户和当前租户在一个集群
     */
    private boolean isSameCluster(Long tenantId, List<Long> tidList){
        ClusterService slbApiClient = engineApi.getSlbApiClient(ClusterService.class);
        ApiResponse<Boolean> clusterVOApiResponse = slbApiClient.isSameCluster(tenantId, tidList);
        return clusterVOApiResponse.getData();
    }

    /**
     * 判断项目标识在当前集群下是否已被其他租户创建
     * @param dtuicTenantId
     * @param projectIdentifier
     */
    private void judgeProjectIdentifierIsExist(Long dtuicTenantId, String projectIdentifier){
        // 根据项目标识获取到创建租户的tenantId
        List<Long> tenantIdList = projectDao.getTenantIdListByProjectIdentifier(projectIdentifier);
        if (CollectionUtils.isEmpty(tenantIdList)) {
            return;
        }
        // 根据tenantId获取到dtUicTenantId
        List<Long> dtuicTenantIdList = tenantService.getDtUicTenantListByTenantIds(tenantIdList);
        if (CollectionUtils.isEmpty(dtuicTenantIdList)) {
            throw new RdosDefineException("该项目标识的创建租户不存在");
        }
        // 判断当前项目标识是否已被当前租户创建
        if (dtuicTenantIdList.contains(dtuicTenantId)){
            throw new RdosDefineException(ErrorCode.PROJECT_NAME_ALREADY_EXISIT);
        }
        // 判断当前租户和已经创建该项目租户是否属于同一个集群
        if (isSameCluster(dtuicTenantId, dtuicTenantIdList)) {
            throw new RdosDefineException(ErrorCode.PROJECT_NAME_ALREADY_EXISIT);
        }
    }

    /**
     * 只有管理员才能创建项目
     *
     * @param project
     * @param userId
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createProject(ProjectVO project, Long userId, Long dtuicTenantId, String dtToken, Long tenantId) {

        //保证projectName唯一性
        createProjectLock.lock();
        try {
            //检查是否是租户所有者，只有所有者才能创建
            //create project
            if (!PublicUtil.matcher(project.getProjectName(), PatternConstant.PROJECTPATTERN)) {
                throw new RdosDefineException(ErrorCode.NAME_FORMAT_ERROR);
            }

            String projectName = project.getProjectName().toLowerCase();  //保存时使用小写
            project.setProjectName(projectName);
            project.setProjectIdentifier(projectName);

            // 判断项目标识是否存在
            judgeProjectIdentifierIsExist(dtuicTenantId, project.getProjectIdentifier());

            //项目别名
            if (StringUtils.isBlank(project.getProjectAlias())) {
                project.setProjectAlias(projectName);
            }

            if (CollectionUtils.isNotEmpty(projectDao.listByProjectAlias(project.getProjectAlias()))) {
                throw new RdosDefineException(ErrorCode.PROJECT_ALIAS_ALREADY_EXISIT);
            }

            project.setProjectType(ProjectType.GENERAL.getType());
            project.setStatus(ProjectStatus.INITIALIZE.getStatus());
            project.setScheduleStatus(Optional.ofNullable(project.getScheduleStatus()).orElse(0));
            project.setIsAllowDownload(Optional.ofNullable(project.getIsAllowDownload()).orElse(0));
            if (project.getCatalogueId() == null){
                BatchCatalogue rootTree = batchCatalogueService.getRootTree(tenantId, RdosBatchCatalogueTypeEnum.PROJECT.getType());
                project.setCatalogueId(rootTree.getId());
            }
            //新增操作
            projectDao.insert(project);
            Project pro = projectDao.getOne(project.getId());
            Date gmtCreate = new Date(pro.getGmtCreate().getTime());
            Date gmtModified = new Date(pro.getGmtModified().getTime());
            ScheduleEngineProjectParam scheduleEngineProjectParam = new ScheduleEngineProjectParam();
            BeanUtils.copyProperties(project, scheduleEngineProjectParam);
            scheduleEngineProjectParam.setId(null);
            scheduleEngineProjectParam.setProjectId(project.getId());
            scheduleEngineProjectParam.setAppType(AppType.RDOS.getType());
            scheduleEngineProjectParam.setUicTenantId(dtuicTenantId);
            scheduleEngineProjectParam.setCreateUserId(project.getCreateUserId().intValue());
            scheduleEngineProjectParam.setGmtCreate(gmtCreate);
            scheduleEngineProjectParam.setGmtModified(gmtModified);
            scheduleEngineProjectParam.setCreateUserId(project.getCreateUserId().intValue());
            engineProjectService.addProject(scheduleEngineProjectParam);
        } finally {
            createProjectLock.unlock();
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                allInit(project, userId, dtuicTenantId, project.getProjectEngineList(), dtToken);
            }
        });

        return project.getId();
    }

    /**
     * 比较并返回本地和元数据的差异表信息
     *
     * @param projectId
     * @param dtuicTenantId
     * @param tenantId
     * @param dataSourceType
     * @return
     */
    public BatchCompareIntrinsicTableResultVO compareIntrinsicTable(Long projectId, Long dtuicTenantId, Long tenantId,
                                                                    Integer dataSourceType) {
        Integer jobType = DataSourceTypeJobTypeMapping.getJobTypeByDataSourceType(dataSourceType);
        MultiEngineType engineType = TaskEngineType.getEngineTypeByTaskTypeInt(jobType);
        if (Objects.isNull(engineType)){
            throw new RdosDefineException(String.format("无法获取到引擎类型通过任务类型：%s", jobType));
        }
        ETableType tableType = ETableType.getDatasourceType(dataSourceType);

        ProjectEngine projectEngine = projectEngineService.getProjectDb(projectId, engineType.getType());
        List<String> tableNames;
        try {
            tableNames = getTableNameList(dtuicTenantId, projectEngine.getEngineIdentity(), projectId, tableType.getType());
        } catch (Exception e) {
            throw new RdosDefineException(ErrorCode.GET_TABLE_INFO_FAILED, e);
        }

        List<String> localTableNames = batchTableInfoService.listNameByProjectIdAndTenantId(projectId, tenantId, tableType);
        //解析并填充需要添加、删除的表
        BatchCompareIntrinsicTableResultVO intrinsicTableResultVO = getMetaTableAndLocalTableDiff(tableNames, localTableNames);
        intrinsicTableResultVO.setLifecycle(environmentContext.getTempTableLifecycle());
        return intrinsicTableResultVO;
    }

    /**
     * 检测元数据同步状态
     *
     * @return
     */
    public Integer checkDealStatus() {
        try {
            Boolean isLock =  dealIntrinsicTableLock.acquire(1000L);
            if (BooleanUtils.isTrue(isLock)) {
                dealIntrinsicTableLock.release();
                return 1;
            }
        } catch (Exception e) {
            logger.info("checkDealStatus acquire error " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * hive元数据同步功能
     * @param projectId
     * @param catalogueId
     * @param lifecycle
     * @param userId
     * @param dtuicTenantId
     * @param addTableNamesParam
     * @param dropTableNamesParam
     */
    public void dealIntrinsicTable(Long projectId, Long catalogueId, Integer lifecycle, Long userId, Long dtuicTenantId,
                                   List<String> addTableNamesParam, List<String> dropTableNamesParam, Boolean synchronizeAllTable,
                                   Integer dataSourceType, Long tenantId) {
        checkIntrinsicTableParams(dataSourceType, catalogueId, lifecycle);
        Integer jobType = DataSourceTypeJobTypeMapping.getJobTypeByDataSourceType(dataSourceType);
        MultiEngineType engineType = TaskEngineType.getEngineTypeByTaskTypeInt(jobType);
        if (Objects.isNull(engineType)){
            throw new RdosDefineException(String.format("无法获取到引擎类型通过任务类型：%s", jobType));
        }
        Integer tableType = ETableType.getDatasourceType(dataSourceType).getType();

        //获取Redis分布式锁，在上一次同步未处理完之前，不允许再次同步，锁超时时间为600秒
        Boolean isLock = false;
        try {
            isLock = dealIntrinsicTableLock.acquire(600L * 1000L);
        } catch (Exception e) {
            throw new RdosDefineException("元数据同步获取锁出现异常!" + e.getMessage(), e);
        }
        if (BooleanUtils.isNotTrue(isLock)) {
            throw new RdosDefineException("上次同步还在进行中，请稍后再试！");
        }
        try {
            ProjectEngine projectEngine = projectEngineService.getProjectDb(projectId, engineType.getType());
            String dbName = projectEngine.getEngineIdentity();

            //默认添加和删除页面指定的表
            List<String> addTableNames = CollectionUtils.isEmpty(addTableNamesParam) ? Lists.newArrayList() : addTableNamesParam;
            List<String> dropTableNames = CollectionUtils.isEmpty(dropTableNamesParam) ? Lists.newArrayList() : dropTableNamesParam;
            //判断是否同步所有的表
            if (BooleanUtils.isTrue(synchronizeAllTable)) {
                BatchCompareIntrinsicTableResultVO intrinsicTableResultVO = compareIntrinsicTable(projectId, dtuicTenantId, tenantId, dataSourceType);
                addTableNames = intrinsicTableResultVO.getAddTablesName();
                dropTableNames = intrinsicTableResultVO.getDropTablesName();
            }

            List<BatchTableInfo> tableInfoList = batchTableInfoService.listByTableNames(dropTableNames, projectId, tableType, tenantId);
            //删除表需要通过id来删除，所以这里直接将需要删除的表名转换为id，避免在循环中查询id
            List<Long> dropTableIds = tableInfoList.stream().map(tableInfo -> tableInfo.getId()).collect(Collectors.toList());
            Map<Long, String> dropTableIdAndNameMap = tableInfoList.stream().collect(Collectors.toMap(BatchTableInfo::getId, BatchTableInfo::getTableName, (key1, key2) -> key1));

            //开始执行添加表和删除表逻辑，创建一个map，key为表名，value为创建还是删除表，方便下面做区分
            Map<Object,Boolean> tableNameOrIdMap = Maps.newHashMap();
            addTableNames.forEach(str -> tableNameOrIdMap.put(str, true));
            dropTableIds.forEach(str -> tableNameOrIdMap.put(str, false));
            if (MapUtils.isEmpty(tableNameOrIdMap)) {
                dealIntrinsicTableLock.release();
                logger.info("dealIntrinsicTable method. No tables need to be synchronized, release lock. tenantId:{}, projectId:{}", tenantId, projectId);
                return;
            }
            //交给线程池执行具体的表添加和删除逻辑
            syncTableMetadata(dbName, tableNameOrIdMap, projectId, dtuicTenantId, tenantId, catalogueId,
                    userId, lifecycle, dropTableIdAndNameMap, tableType);
        } catch (Exception e) {
            try {
                dealIntrinsicTableLock.release();
            } catch (Exception releaseException) {
                logger.error("dealIntrinsicTable method. Metadata synchronization successful, but it failed to release the lock. ProjectId:{}" + e.getMessage(), projectId, e);
            }
            throw new RdosDefineException("元数据同步异常!" + e.getMessage(), e);
        }
    }

    /**
     * 检查元数据同步参数
     *
     * @param dataSourceType
     * @param catalogueId
     * @param lifecycle
     */
    public void checkIntrinsicTableParams(Integer dataSourceType, Long catalogueId, Integer lifecycle){
        Set<Integer> hiveTableDataSourceType = Sets.newHashSet(DataSourceType.HIVE1X.getVal(), DataSourceType.HIVE.getVal(),
                                                               DataSourceType.HIVE3X.getVal(), DataSourceType.SparkThrift2_1.getVal(),
                                                               DataSourceType.IMPALA.getVal());
        if (Objects.isNull(catalogueId)) {
            throw new RdosDefineException("所属类目必填");
        }
        if (hiveTableDataSourceType.contains(dataSourceType) && Objects.isNull(lifecycle)) {
            throw new RdosDefineException("生命周期必填");
        }
    }

    /**
     * 通过线程池，真正执行表添加和删除的逻辑
     * @param dbNameFinal
     * @param tableNameOrIdMap
     * @param projectId
     * @param dtuicTenantId
     * @param tenantId
     * @param catalogueId
     * @param userId
     * @param lifecycle
     * @param dropTableIdAndNameMap
     * @param tableType 表类型
     */
    private void syncTableMetadata(String dbNameFinal, final Map<Object,Boolean> tableNameOrIdMap, final Long projectId,
                                   Long dtuicTenantId, Long tenantId, Long catalogueId, Long userId, Integer lifecycle,
                                   Map<Long, String> dropTableIdAndNameMap, Integer tableType){
        //使用线程安全的统计值，当统计值减为0时，则释放锁
        AtomicInteger count= new AtomicInteger(tableNameOrIdMap.size());
        for (Map.Entry<Object, Boolean> entry : tableNameOrIdMap.entrySet()) {
            executorService.execute(() -> {
                try {
                    if(entry.getValue()){
                        batchTableInfoService.addTableFromSql(dtuicTenantId, tenantId, projectId, (String)entry.getKey(), lifecycle, catalogueId, userId, NORMAL_TABLE, false, tableType, dbNameFinal);
                    }else{
                        batchTableInfoService.dropTable(tenantId, projectId, userId, (Long)entry.getKey(), dtuicTenantId, true);
                    }
                } catch (Exception e) {
                    if(entry.getValue()){
                        logger.error("syncTableMetadata method. projectId:{},[{}] Table synchronization failure!" + e.getMessage(), entry.getKey(), projectId, e);
                    }else{
                        logger.error("syncTableMetadata method. projectId:{},[{}] Table synchronization failure!" + e.getMessage(), dropTableIdAndNameMap.get(entry.getKey()), projectId, e);
                    }
                } finally {
                    count.getAndDecrement();
                    if(count.get() == 0){
                        try {
                            dealIntrinsicTableLock.release();
                        } catch (Exception e) {
                            logger.error("syncTableMetadata method. Metadata synchronization successful, but it failed to release the lock. projectId:{}. " + e.getMessage(), projectId, e);
                        }
                        logger.info("syncTableMetadata method. Metadata synchronization successful, lock released successfully. projectId:{}", projectId);
                    }
                }
            });
        }
    }

    public void allInit(ProjectVO project, Long userId, Long dtuicTenantId, List<ProjectEngineVO> projectEngineVOS, String dtToken) {
        int status = ProjectStatus.NORMAL.getStatus();
        setDatabaseName(project);
        try {
            initPermissions(project, userId, dtuicTenantId);
            //初始化离线目录
            batchCatalogueService.initCatalogue(project.getTenantId(), dtuicTenantId, project.getId(), project.getCreateUserId(), projectEngineVOS,dtToken);
            //数据类目创建
            batchDataCatalogueService.initTenantRootCatalogue(project.getTenantId());
            // 初始化数据模型数据
            batchModelTableService.initProjectModel(project.getTenantId(), project.getId(), project.getCreateUserId());

            for (ProjectEngineVO projectEngineVO : project.getProjectEngineList()) {
                initSingleProjectEngine(projectEngineVO, project, userId, dtuicTenantId);
            }

            //update project status
            projectDao.updateStatusById(project.getId(), status, userId);
        } catch (Exception e) {
            logger.error("{}:create project error: {}", project.getProjectName(), e);
            status = ProjectStatus.FAIL.getStatus();
            projectDao.updateStatusById(project.getId(), status, userId);
            //项目创建失败，需要删除projectId下引擎创建的数据源
            DeleteDataSourceParam deleteDataSourceParam = new DeleteDataSourceParam();
            deleteDataSourceParam.setAppType(AppType.RDOS.getType());
            deleteDataSourceParam.setProjectId(project.getId());
            dataSourceService.deleteDataSourceByProjectId(deleteDataSourceParam);
        }
        project.setStatus(status);
        ScheduleEngineProjectParam scheduleEngineProjectParam = new ScheduleEngineProjectParam();
        BeanUtils.copyProperties(project, scheduleEngineProjectParam);
        scheduleEngineProjectParam.setId(null);
        scheduleEngineProjectParam.setProjectId(project.getId());
        scheduleEngineProjectParam.setAppType(AppType.RDOS.getType());
        scheduleEngineProjectParam.setGmtModified(new Date(System.currentTimeMillis()));
        scheduleEngineProjectParam.setUicTenantId(dtuicTenantId);
        engineProjectService.updateProject(scheduleEngineProjectParam);
    }

    /**
     * 初始化项目成员及其角色权限
     *
     * TODO: 平台管理员、租户所有者需等uic接口完善，然后补齐功能
     *
     * @param project
     * @param userId
     * @param dtuicTenantId
     */
    private void initPermissions(ProjectVO project, Long userId, Long dtuicTenantId) {
        // 初始化项目角色
        initProjectRole(project.getId(), project.getTenantId(), userId);

        // 得到角色映射关系
        List<Integer> roleValueList =  Lists.newArrayList(
                RoleValue.APPADMIN.getRoleValue(), RoleValue.TEANTOWNER.getRoleValue(),
                RoleValue.TEANTADMIN.getRoleValue(), RoleValue.PROJECTOWNER.getRoleValue(),
                RoleValue.MEMBER.getRoleValue());
        List<Role> roleList = roleService.getByProjectIdAndRoleValues(project.getId(), roleValueList);
        Map<Integer, Role> roleValueRoleMap = roleList.stream().collect(Collectors.toMap(Role::getRoleValue, Function.identity(), (key1, key2) -> key2));

        // 得到需要增加的项目成员信息
        List<TenantUsersVO> tenantUsersVOList = tenantService.findUicAdminRoleUserByDtuicTenantId(dtuicTenantId);
        List<User> userList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tenantUsersVOList)) {
            userList = userService.dealTenantUicUserList(tenantUsersVOList);
        }
        Map<Long, User> dtuicUserIdUserMap = userList.stream().collect(Collectors.toMap(User::getDtuicUserId, Function.identity(), (key1, key2) -> key2));

        // 初始化 平台管理员、租户所有者、租户管理员 用户到项目成员中
        Map<Long, List<Long>> userRoleMap = new HashMap<>();
        for (TenantUsersVO tenantUsersVO : tenantUsersVOList) {
            List<Long> userRoleIdList = new ArrayList<>();
            User user = (User) MapUtils.getObject(dtuicUserIdUserMap, tenantUsersVO.getId());
            if (Objects.isNull(user)) {
                continue;
            }
            // 判断是否是 超级管理员 用户
            if (BooleanUtils.isTrue(tenantUsersVO.getRoot()) && roleValueRoleMap.containsKey(RoleValue.APPADMIN.getRoleValue())) {
                userRoleIdList.add(roleValueRoleMap.get(RoleValue.APPADMIN.getRoleValue()).getId());
            }
            // 判断是否是 租户所有者 用户 (待uic接口完善加上)
            // 判断是否是 租户管理者 用户
            if (BooleanUtils.isTrue(tenantUsersVO.getAdmin()) && roleValueRoleMap.containsKey(RoleValue.TEANTADMIN.getRoleValue())) {
                userRoleIdList.add(roleValueRoleMap.get(RoleValue.TEANTADMIN.getRoleValue()).getId());
            }
            // 默认添加一个访客角色
            if (roleValueRoleMap.containsKey(RoleValue.MEMBER.getRoleValue())) {
                userRoleIdList.add(roleValueRoleMap.get(RoleValue.MEMBER.getRoleValue()).getId());
            }
            userRoleMap.put(user.getId(), userRoleIdList);
        }

        // 处理项目创建人的角色
        List<Long> createUserRoleIdList = userRoleMap.getOrDefault(userId, Lists.newArrayList(roleValueRoleMap.get(RoleValue.MEMBER.getRoleValue()).getId()));
        createUserRoleIdList.add(roleValueRoleMap.get(RoleValue.PROJECTOWNER.getRoleValue()).getId());
        userRoleMap.put(userId, createUserRoleIdList);

        // 添加用户默认角色
        userRoleMap.forEach((targetUserId, targetUserRoleIdList) ->{
            roleUserService.addDefaultRoleUserList(targetUserRoleIdList, targetUserId, project.getTenantId(), project.getId());
        });
    }

    private void setDatabaseName(ProjectVO project) {
        for (ProjectEngineVO projectEngineVO : project.getProjectEngineList()) {
            if (ProjectCreateModel.DEFAULT.getType().equals(projectEngineVO.getCreateModel())) {
                projectEngineVO.setDatabase(project.getProjectName());
            }
        }
    }

    /**
     * 初始化单个项目管理的引擎
     *
     * @param projectEngineVO
     * @param project
     * @param userId
     * @param dtuicTenantId
     */
    private void initSingleProjectEngine(ProjectEngineVO projectEngineVO, Project project, Long userId, Long dtuicTenantId) throws Exception {
        checkDatabaseCanUse(project.getTenantId(), projectEngineVO.getEngineType(), projectEngineVO.getDatabase());

        ProjectEngine projectEngine = new ProjectEngine();
        IProjectService projectService = multiEngineServiceFactory.getProjectService(projectEngineVO.getEngineType());
        if (Objects.isNull(projectService)) {
            throw new RdosDefineException(String.format("不支持该引擎类别 : %d", projectEngineVO.getEngineType()));
        }

        //插入项目-引擎关联关系
        projectEngine.setCreateUserId(userId);
        projectEngine.setProjectId(project.getId());
        projectEngine.setTenantId(project.getTenantId());
        projectEngine.setEngineType(projectEngineVO.getEngineType());
        //导入schema使用projectEngineVo的database，创建项目使用项目名小写
        if (ProjectCreateModel.intrinsic.getType().equals(projectEngineVO.getCreateModel())){
            projectEngine.setEngineIdentity(projectEngineVO.getDatabase());
        }else {
            projectEngine.setEngineIdentity(project.getProjectName().toLowerCase());
        }
        projectEngine.setStatus(ProjectStatus.NORMAL.getStatus());

        //生成目录
        batchCatalogueService.initExtEngineCatalogue(project.getTenantId(), project.getId(), userId, Lists.newArrayList(projectEngineVO.getEngineType()));
        projectEngineService.insert(projectEngine);
        projectService.createProject(project.getId(), project.getProjectName(), project.getProjectDesc(), userId,
                project.getTenantId(), dtuicTenantId, projectEngineVO);
    }

    private void checkDatabaseCanUse(Long tenantId, Integer engineType, String database) {
        ProjectEngine projectEngine = projectEngineDao.getByIdentityAndEngineTypeAndTenantId(database, engineType, tenantId);
        if (projectEngine != null) {
            throw new RdosDefineException(String.format("数据库[%s] 已经绑定其它项目", database));
        }
    }

    /**
     * 初始化项目的角色
     *
     * @param projectId
     * @param tenantId
     * @param userId
     */
    @Transactional(rollbackFor = Exception.class)
    public void initProjectRole(long projectId, long tenantId, Long userId) {
        //复制系统默认角色
        List<Role> roles = roleService.copySystemDefaultRole(projectId, tenantId, userId);
        //复制角色对应的权限点
        roleService.copyRolePermission(roles, userId, tenantId, projectId);
    }

    /**
     * ps:首页显示内容-不做权限设置
     * 控制台-项目与告警信息
     *
     * @param userId   用户id
     * @param tenantId 租户id
     * @return
     * @author toutian
     */
    /**
     * @sanyue 测试通过 2018.01.03。
     */
    public Map<String, Integer> getProjectInfo(Long userId, Long tenantId, Boolean isRoot) {

        Map<String, Integer> info = new HashMap<>(3);

        List<RoleUser> roleUsers = roleUserService.getRoleUserByUserId(userId, tenantId);
        List<Long> projectIds = roleUsers.stream().map(RoleUser::getProjectId).distinct().collect(Collectors.toList());

        List<Project> allProjects = this.getAllProjects(tenantId, Boolean.TRUE, userId, isRoot);
        List<Long> allProjectIds = allProjects.stream().map(Project::getId).collect(Collectors.toList());

        int joinProject = 0;
        for (Long projectId : projectIds) {
            if (allProjectIds.contains(projectId)) {
                joinProject++;
            }
        }

        info.put("joinProjects", joinProject);
        info.put("allProjects", allProjects.size());

        return info;
    }

    /**
     * ps:首页显示内容-不做权限设置
     * 控制台-项目列表
     *
     * @param userId      用户id
     * @param tenantId    租户id
     * @param projectName 项目名称
     * @param isAdmin     是否管理员
     * @param currentPage 分页索引
     * @param pageSize    分页大小
     * @return
     * @author toutian
     */
    /**
     * @sanyue 测试通过 2018.01.03。
     */
    public PageResult<List<ProjectVO>> queryProjects(Long userId, Long tenantId, String projectName, Boolean isAdmin, Integer currentPage, Integer pageSize, Boolean isRoot) {

        Set<Long> projectIds = this.getUsefulProjectIds(userId, tenantId, isAdmin, isRoot);
        if (CollectionUtils.isEmpty(projectIds)) {
            return PageResult.EMPTY_PAGE_RESULT;
        }

        PageQuery<Project> pageQuery = new PageQuery<Project>(currentPage, pageSize, "gmt_Modified", Sort.DESC.name());
        List<Project> projects = projectDao.listByIdsAndProjectAlias(projectIds, projectName, pageQuery);

        List<Long> pIds = new ArrayList<>();
        projects.forEach(p -> {
            pIds.add(p.getId());
            if (p.getProduceProjectId() != null) {
                pIds.add(p.getProduceProjectId());
            }
        });

        List<Project> linkProjects = projectDao.listByIdsAndTenantId(pIds, tenantId);
        Map<Long, Project> pIdMap = new HashMap<>();
        linkProjects.forEach(p -> {
            pIdMap.put(p.getId(), p);
        });

        if (CollectionUtils.isNotEmpty(projects)) {
            List<ProjectVO> projectVOs = new ArrayList<>(projects.size());
            projects.forEach(project -> {
                ProjectVO vo = this.mapperProject(project, isRoot, userId);
                if (!ProjectType.TEST.getType().equals(vo.getProjectType())) {
                    vo.setProduceProject(pIdMap.get(vo.getProduceProjectId()).getProjectAlias());
                }
                projectVOs.add(vo);
            });

            int count = projectDao.countByIdsAndProjectAlias(new ArrayList<>(projectIds), projectName);
            PageResult pageResult = new PageResult(projectVOs, count, pageQuery);
            return pageResult;
        }

        return PageResult.EMPTY_PAGE_RESULT;
    }

    /**
     * ps:首页显示内容-不做权限设置
     * 控制台顶端-项目下拉列表
     *
     * @param userId   用户id : 获取该userId参加的项目/（null - 获取整个租户内的所有project）
     * @param isAdmin  是否是管理员及以上的用户
     * @param tenantId
     * @param isRoot   是否是uic的root用户
     * @return
     * @author toutian
     */
    public List<Project> getProjects(Long userId, Boolean isAdmin, Long tenantId, Boolean isRoot, Integer appType) {
        appType = appType == null ? AppType.RDOS.getType() : appType;
        List<Project> projects = Lists.newArrayList();
        Long dtuicTenantId = tenantService.getDtuicTenantId(tenantId);
        User user = userService.getUser(userId);
        Long dtuicUserId = user.getDtuicUserId();
        if (appType.intValue() == AppType.RDOS.getType()) {
            Set<Long> usefulProjectIds = this.getUsefulProjectIds(userId, tenantId, isAdmin, isRoot);
            if (CollectionUtils.isNotEmpty(usefulProjectIds)) {
                projects = projectDao.listByIds(usefulProjectIds);
            }
        } else if (appType.intValue() == AppType.DATASCIENCE.getType()) {
            projects = getDataScienceProjects(dtuicTenantId, dtuicUserId, isRoot);
        } else if (appType.intValue() == AppType.TAG.getType()){
            // 目前先这样做，等和标签进行任务打通时完善
            projects = new ArrayList<>();
        }
        return CollectionUtils.isEmpty(projects) ? Lists.newArrayList() : projects;
    }

    /**
     * 获取数据科学项目信息
     *
     * @param dtuicTenantId
     * @return
     */
    private List<Project> getDataScienceProjects(Long dtuicTenantId, Long dtuicUserId, Boolean isRoot){
        List<Project> projects = Lists.newArrayList();
        GetScienceProjectsParam projectsParam = new GetScienceProjectsParam();
        projectsParam.setDtuicTenantId(dtuicTenantId);
        projectsParam.setDtuicUserId(dtuicUserId);
        projectsParam.setIsRoot(isRoot);
        projectsParam.setTotal(false);
        List<ScienceProjectsVO> scienceProjectsVOS = scienceProjectService.getProjectsByUserAndTenant(projectsParam).getData();
        if (CollectionUtils.isNotEmpty(scienceProjectsVOS)) {
            projects = ProjectMapstructTransfer.INSTANCE.scienceProjectVOToProject(scienceProjectsVOS);
        }
        return projects;
    }

    /**
     * 获取租户下所有的项目
     *
     * @param tenantId
     * @return
     */
    public List<Project> getTenantProjects(Long tenantId) {
        List<RoleUser> roleUsers = roleUserService.getRoleUserInTenant(tenantId);

        if (CollectionUtils.isEmpty(roleUsers)) {
            return Collections.EMPTY_LIST;
        }

        Set<Long> projectIds = Sets.newHashSet();
        roleUsers.forEach(item -> projectIds.add(item.getProjectId()));

        return projectDao.listByIds(projectIds);
    }

    /**
     * 获取用户指定角色的项目列表
     *
     * @param userId
     * @param roleValues
     * @param tenantId
     * @return
     */
    public List<Project> getProjectsByRoleValues(Long userId, List<Integer> roleValues, Long tenantId) {
        List<RoleUser> roleUsers = roleUserService.getRoleUserByRoleValues(userId, roleValues, tenantId);
        List<Project> result = new ArrayList<>(roleUsers.size());
        if (CollectionUtils.isNotEmpty(roleUsers)) {
            List<Long> projectIds = new ArrayList<>(roleUsers.size());
            roleUsers.forEach(item -> {
                projectIds.add(item.getProjectId());
            });

            result = projectDao.listByIds(projectIds);
        }
        return result;
    }

    /**
     * 获取所有项目，在筛选下拉框里使用
     *
     * @param total    全部/该userId有参加的项目
     * @param tenantId
     * @param userId
     * @return
     */
    public List<Project> getAllProjects(Long tenantId, Boolean total, Long userId, Boolean isRoot) {
        Set<Long> projectIds = new HashSet<>();
        if (BooleanUtils.isFalse(total)) {
            //total 为false ，查出该userId有参加的项目
            projectIds = this.getUsefulProjectIds(userId, tenantId, null, isRoot);
            if (CollectionUtils.isEmpty(projectIds)) {
                return Collections.EMPTY_LIST;
            }
        }
        //total 为True/null，查出该租户下全部项目
        //根据项目置顶和项目修改时间排序
        return this.projectDao.listByIdsAndTenantId(new ArrayList<>(projectIds), tenantId);
    }

    /**
     * 角色权限改版后，项目成员管理
     */
    /**
     * @sanyue 测试通过 2018.01.03。
     */
    public PageResult<List<UserRoleVO>> getProjectUsers(Long projectId, Long tenantId, Long userId, String name, Integer currentPage, Integer pageSize) {
        PageQuery pageQuery = new PageQuery(currentPage, pageSize);
        return roleUserService.getUserAllRoles(projectId, tenantId, userId, name, pageQuery);
    }

    /**
     * 修改任务责任人时调用
     *
     * @param projectId
     * @param tenantId
     * @param userId
     * @param name
     * @param currentPage
     * @param pageSize
     * @return
     */
    public PageResult<List<UserRoleVO>> getProjectTaskUsers(Long projectId, Long tenantId,
                                                                                              Long userId, String name,
                                                                                              Integer currentPage, Integer pageSize) {
        PageQuery pageQuery = new PageQuery(currentPage, pageSize);
        return roleUserService.getUserRoleVOSWithoutMember(projectId, tenantId, userId, name, pageQuery);
    }

    /**
     * 项目成员管理-添加成员，获取除项目外的所有成员
     */
    /**
     * @sanyue 测试通过 2018.01.03。
     */
    public List<User> getUsersNotInProject(Long projectId, String name) {

        List<RoleUser> roleUsers = roleUserDao.listByProjectId(projectId);
        List<Long> userInIds = roleUsers.stream().map(RoleUser::getUserId).collect(Collectors.toList());
        userInIds.add(-1L);
        return userDao.listByNotInIdsAndName(userInIds, name);
    }

    /**
     * 获取该租户下没有在该项目下的用户信息
     *
     * @param projectId
     * @param dtuicTenantId
     * @return
     */
    public List<BatchProjectGetUicNotInProjectResultVO> getUicUsersNotInProject(Long projectId, Long dtuicTenantId) {
        // 获取该租户下所有的用户
        List<TenantUsersVO> tenantUserList = uIcUserTenantRelApiClient.findUsersByTenantId(dtuicTenantId).getData();

        // 获取已经存在该项目下的用户
        List<RoleUser> roleUsers = roleUserDao.listByProjectId(projectId);
        List<Long> userInIds = roleUsers.stream()
                .map((roleUser) -> roleUser.getUser().getDtuicUserId())
                .distinct().collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(tenantUserList)) {
            //过滤掉已经在项目里的用户
            tenantUserList = tenantUserList.stream()
                    .filter((uicUser) -> !userInIds.contains(uicUser.getId()))
                    .collect(Collectors.toList());
            return ProjectMapstructTransfer.INSTANCE.tenantUsersVOListToResultVO(tenantUserList);
        }
        return Lists.newArrayList();
    }

    /**
     * 获得项目详情
     *
     * @param projectId 项目id
     * @return
     * @author toutian
     */
    public ProjectVO getProjectByProjectId(Long projectId, boolean isRoot, Long userId) {
        final Project project = this.projectDao.getOne(projectId);
        if (project == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_PROJECT);
        }
        Integer alarmStatus = this.batchAlarmService.getProjectAlarmStatusByProjectId(projectId);
        alarmStatus = alarmStatus == null ? 1 : alarmStatus;
        project.setAlarmStatus(alarmStatus);
        final ProjectVO projectVO = this.mapperProject(project, isRoot, userId);
        //为了页面展示效果 增加默认值
        projectVO.setProduceProject("(未绑定生产项目)");
        if (!ProjectType.GENERAL.getType().equals(project.getProjectType())) {
            Project produceProject = projectDao.getOne(project.getProduceProjectId());
            if (produceProject == null) {
                return projectVO;
            }

            if (ProjectType.TEST.getType().equals(project.getProjectType())) {
                projectVO.setProduceProject(produceProject.getProjectAlias());
            } else {
                projectVO.setTestProject(produceProject.getProjectAlias());
                projectVO.setTestProjectId(produceProject.getId());
            }
        }

        return projectVO;
    }

    /**
     * 修改项目信息
     *
     * @author jiangbo
     * @Param projectId     项目ID
     * @Param projectDesc   项目描述
     * @Param userId        用户ID
     */
    public void updateProjectInfo(Long projectId, String projectDesc, String projectAlias, Long userId) {
        Project project = projectDao.getOne(projectId);
        if (project == null || project.getIsDeleted().intValue() == Deleted.DELETED.getStatus().intValue()) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_PROJECT);
        }

        List<Project> byProjectAlias = projectDao.listByProjectAlias(projectAlias);
        if (CollectionUtils.isNotEmpty(byProjectAlias)) {
            for (Project alias : byProjectAlias) {
                if (!alias.getId().equals(projectId)) {
                    throw new RdosDefineException(ErrorCode.PROJECT_ALIAS_ALREADY_EXISIT);
                }
            }
        }
        project = new Project();
        project.setId(projectId);
        project.setProjectDesc(projectDesc);
        project.setProjectAlias(projectAlias);
        project.setModifyUserId(userId);
        projectDao.update(project);
        project = projectDao.getOne(projectId);
        Date gmtModified = new Date(project.getGmtModified().getTime());
        ScheduleEngineProjectParam scheduleEngineProjectParam = new ScheduleEngineProjectParam();
        BeanUtils.copyProperties(project, scheduleEngineProjectParam);
        scheduleEngineProjectParam.setId(null);
        scheduleEngineProjectParam.setProjectId(project.getId());
        scheduleEngineProjectParam.setGmtModified(gmtModified);
        scheduleEngineProjectParam.setAppType(AppType.RDOS.getType());
        engineProjectService.updateProject(scheduleEngineProjectParam);
    }

    /**
     * 获得用户下的项目
     *
     * @param userId           用户id
     * @param tenantId         租户id
     * @param defaultProjectId 默认项目id
     * @return
     */
    public List<Project> getProjectUserIn(Long userId, Long tenantId, Long defaultProjectId) {
        List<RoleUser> roleUsers = roleUserService.getRoleUserByUserId(userId, tenantId);
        if (roleUsers == null || roleUsers.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        List<Long> projectIds = Lists.newArrayList();
        roleUsers.forEach(item -> {
            if (defaultProjectId != null && !item.getProjectId().equals(defaultProjectId)) {
                projectIds.add(item.getProjectId());
            }
        });
        if (defaultProjectId != null) {
            projectIds.add(defaultProjectId);
        }
        return projectDao.listByIds(projectIds);
    }

    /**
     * 对象转换
     *
     * @param project 项目
     * @return
     * @author toutian
     */
    private ProjectVO mapperProject(Project project, boolean isRoot, Long userId) {
        ProjectVO projectVO = new ProjectVO();
        BeanUtils.copyProperties(project, projectVO);
        projectVO.setCreateUser(userService.getUser(project.getCreateUserId()));
        projectVO.setAdminUsers(roleUserService.getProjectAdminUser(project.getId(), isRoot, userId));
        return projectVO;
    }

    /**
     * 获取项目，如果为空，或报异常
     *
     * @param projectId
     * @return
     */
    public Project getProjectById(Long projectId) {
        if (projectId == null) {
            throw new RdosDefineException(ErrorCode.PROJECT_ID_CANNOT_BE_NULL);
        }
        Project project = projectDao.getOne(projectId);
        if (project == null) {
            logger.error("Project not exist, by projectId : {}", projectId);
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_PROJECT);
        }
        return project;
    }

    /**
     * 根据项目名称和Uic租户Id查找对应的项目信息
     *
     * @param projectName
     * @param dtUicTenantId
     * @return
     */
    @Forbidden
    public Project getProjectByName(String projectName, Long dtUicTenantId) {
        Tenant tenant = tenantService.getTenantByDtUicTenantId(dtUicTenantId);
        Project project = projectDao.getByName(projectName, tenant.getId());
        if (project == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_PROJECT);
        }
        return project;
    }

    /**
     * 根据id查询项目 忽略已删除标示位置
     *
     * @param projectIds
     * @return
     */
    public List<Project> getProjectByIdWithoutDeleted(List<Long> projectIds) {
        List<Project> project = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(projectIds)) {
            project = projectDao.getOneWithoutDeleted(projectIds);
        }
        return project;
    }

    /**
     * 根据ids和projectName 模糊查询 list
     * @param ids
     * @param projectName
     * @return
     */
    public List<Project> getListByIdsAndProjectName(List<Long> ids, String projectName){
        List<Project> listByIdsAndProjectName = projectDao.getListByIdsAndProjectName(ids, projectName);
        return listByIdsAndProjectName;
    }

    /**
     * 获取该userId下的projectIds
     *
     * @param userId
     * @param tenantId
     * @param isAdmin  是否是管理员及以上角色
     * @param isRoot   如果是root用户则直接返回所有project
     * @return
     */
    public Set<Long> getUsefulProjectIds(Long userId, Long tenantId, Boolean isAdmin, Boolean isRoot) {
        List<RoleUser> roleUsers = null;
        if (BooleanUtils.isTrue(isRoot)) {
            List<Long> projectIds = projectDao.listIdByTenantId(tenantId);
            return projectIds != null ? new HashSet(projectIds) : new HashSet<>();
        }
        //isAdmin:true，所管理的项目，即roleValue>3
        if (BooleanUtils.isTrue(isAdmin)) {
            roleUsers = roleUserService.getRoleUserIsAdmin(userId, tenantId);
        } else {
            roleUsers = roleUserService.getRoleUserByUserId(userId, tenantId);
        }
        if (CollectionUtils.isEmpty(roleUsers)) {
            return Sets.newHashSet();
        }

        Set<Long> projectIds = roleUsers.stream().map(RoleUser::getProjectId).collect(Collectors.toSet());
        return projectIds;
    }


    /**
     * 根据项目名/别名 分页查询；
     * 默认排序为置顶排序，可以项目失败任务数排序
     *
     * @param fuzzyName
     * @param isAdmin   是否是管理员及以上的用户
     * @param userId
     * @param tenantId
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PageResult<List<ProjectOverviewVO>> getProjectList(String fuzzyName, Long userId, Boolean isAdmin, Integer projectType, Long tenantId, String orderBy, String sort, Integer page, Integer pageSize, Boolean isRoot, Long catalogueId) throws InterruptedException {
        if (StringUtils.isEmpty(sort)) {
            sort = Sort.DESC.name();
        }

        // 如果排序方式为空，则默认按照置顶方式排序
        // 如果排序方式不为空，只支持项目今日任务失败数 和 创建时间排序，不支持其他排序方式
        if (StringUtils.isEmpty(orderBy)) {
            orderBy = OrderBy_STICK;
        } else if (!OrderBy_JOBSUM.equals(orderBy) && !"gmtCreate".equals(orderBy)) {
            throw new RdosDefineException(String.format("不支持排序方式: %s", orderBy));
        }

        Set<Long> usefulProjectIds = this.getUsefulProjectIds(userId, tenantId, isAdmin, isRoot);
        List<Long> usefulProjectIdList = Lists.newArrayList(usefulProjectIds);
        if (CollectionUtils.isEmpty(usefulProjectIds)) {
            return PageResult.EMPTY_PAGE_RESULT;
        }
        // _：表示任意单个字符 需要转义
        if ("_".equals(fuzzyName)) {
            fuzzyName = "/" + fuzzyName;
        }
        PageQuery pageQuery = new PageQuery(page, pageSize, orderBy, sort);
        Integer totalCount = projectDao.countByIdsAndFuzzyNameAndType(usefulProjectIdList, fuzzyName, projectType, FAILED_STATUS, pageQuery, tenantId,catalogueId);
        if (totalCount == 0) {
            return PageResult.EMPTY_PAGE_RESULT;
        }

        checkAndInsertProjectStick(usefulProjectIdList, userId, tenantId);

        List<ProjectDTO> projects = projectDao.listJobSumByIdsAndFuzzyNameAndType(usefulProjectIdList, fuzzyName, projectType, userId, pageQuery, tenantId,
                null, null, null, EScheduleType.NORMAL_SCHEDULE.getType(), catalogueId);
        for (ProjectDTO projectDTO : projects) {
            projectDTO.setCreateUserName(userService.getUserName(projectDTO.getCreateUserId()));
        }

        Map<Long, ProjectDTO> projectDTOMap = projects.stream().collect(Collectors.toMap(ProjectDTO::getId, Function.identity()));
        List<Long> projectDTOMapIds = projects.stream().map(ProjectDTO::getId).collect(Collectors.toList());
        // engine返回的项目统计数据
        List<ScheduleJobStatusVO> projectStatusVOs = scheduleJobService.getStatusCountByProjectIds(projectDTOMapIds,
                tenantId, AppType.RDOS.getType(), null).getData();
        Map<Long, Map<String, Integer>> jobCounts = getJobCounts(projectStatusVOs);

        if (OrderBy_JOBSUM.equals(orderBy)) {
            projectDTOMapIds = jobCounts.entrySet().stream()
                    .sorted(Comparator.comparingInt(o -> o.getValue().get(TaskStatus.FAILED.name())))
                    .collect(Collectors.toList()).stream().map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            if (sort.equals(Sort.DESC.name().toLowerCase())) {
                Collections.reverse(projectDTOMapIds);
            }
            projectDTOMapIds = projectDTOMapIds.subList((page - 1) * pageSize, page * pageSize < totalCount ? page * pageSize : totalCount);
        }
        // 线程安全，防止projectOverviewVOList数据没有刷新到主内存，两个线程拿到同一个对象
        List<ProjectOverviewVO> projectOverviewVOList = Collections.synchronizedList(Lists.newArrayList());
        List<ProjectOverviewVO> projectOverviewVOSortList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(projectDTOMapIds)) {
            CountDownLatch countDownLatch = new CountDownLatch(projectDTOMapIds.size());
            for (Long pId : projectDTOMapIds) {
                executorService.submit(() -> {
                    try {
                        ProjectOverviewVO projectOverviewVO = createProjectOverview(projectDTOMap.get(pId), pId, userId, tenantId);
                        projectOverviewVO.setJobSum(jobCounts.get(pId).get(TaskStatus.FAILED.name()));
                        projectOverviewVOList.add(projectOverviewVO);
                    } catch (Exception e) {
                        logger.error("获取项目概况异常", e);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
            countDownLatch.await();
            Map<Long, ProjectOverviewVO> projectOverviewVOSortMap = projectOverviewVOList.stream().collect(
                    Collectors.toMap(ProjectOverviewVO::getId, Function.identity()));
            for (Long pId : projectDTOMapIds) {
                projectOverviewVOSortList.add(projectOverviewVOSortMap.get(pId));
            }
        }
        return new PageResult(projectOverviewVOSortList, totalCount, pageQuery);
    }

    /**
     * engine项目统计数据转Map
     *
     * @param scheduleJobStatusVOS
     * @return
     */
    private Map<Long, Map<String, Integer>> getJobCounts(List<ScheduleJobStatusVO> scheduleJobStatusVOS) {
        Map<Long, Map<String, Integer>> result = Maps.newHashMap();
        for (ScheduleJobStatusVO vo : scheduleJobStatusVOS) {
            Map<String, Integer> statusCountMap = vo.getScheduleJobStatusCountVO().stream()
                    .collect(Collectors.toMap(ScheduleJobStatusCountVO::getTaskStatusName, ScheduleJobStatusCountVO::getCount));
            result.put(vo.getProjectId(), statusCountMap);
        }
        return result;
    }

    private ProjectOverviewVO createProjectOverview(ProjectDTO projectDTO, Long projectId, Long userId, Long
            tenantId) {

        //表数量
        Integer tableCount = batchTableCountService.tableCount(userId, projectId, null, null, tenantId);
        //项目所占存储空间
        String totalSize = batchTableCountService.totalSize(userId, projectId, null, null, tenantId);
        //统计任务数：已发布、总任务数
        Map<String, Integer> taskCountMap = batchTaskService.countTask(projectId, tenantId);
        //填充支持的引擎类型
        List<Integer> projectEngineList = projectEngineService.getUsedEngineTypeList(projectId);

        String cataloguePath = batchCatalogueService.getCatalogueNamePathByParentId(Long.parseLong(projectDTO.getCatalogueId().toString()));


        ProjectOverviewVO project = new ProjectOverviewVO();
        BeanUtils.copyProperties(projectDTO, project);

        //是否置顶
        project.setStickStatus(projectDTO.getStick() == null ? NOT_STICKY : STICKY);
        project.setTableCount(tableCount);
        project.setTotalSize(totalSize);
        project.setTaskCountMap(taskCountMap);
        project.setSupportEngineType(projectEngineList);
        project.setCataloguePath(cataloguePath);

        return project;
    }

    /**
     * 获取该租户下所有项目
     * @param tenantId
     */
    public List<ProjectCataloguePO> getCatalogueListByTenantIdAndCatalogueId(Set<Long> projectIds, Long tenantId, Integer catalogueType){
        return projectDao.getCatalogueListByTenantIdAndCatalogueId(projectIds,tenantId,catalogueType);
    }


    /**
     * 将项目设置为置顶
     *
     * @param appointProjectId
     * @param userId
     * @param tenantId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer setSticky(Long appointProjectId, Integer stickStatus, Long userId, Long tenantId) {
        Project project = projectDao.getOne(appointProjectId);
        if (project == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_PROJECT);
        }
        ProjectStick projectStick = projectStickService.getByUserIdAndProjectId(userId, tenantId, appointProjectId);
        if (projectStick == null) {
            projectStick = new ProjectStick();
            projectStick.setCreateUserId(userId);
            projectStick.setProjectId(appointProjectId);
            projectStick.setTenantId(tenantId);
        }

        if (STICKY.equals(stickStatus)) {
            projectStick.setStick(Timestamp.valueOf(LocalDateTime.now()));
        } else {
            projectStick.setStick(null);
        }

        Integer update = this.insertOrUpDateProjectStick(projectStick);

        if (update != 1) {
            throw new RdosDefineException("更新失败");
        }
        return update;
    }

    private Integer insertOrUpDateProjectStick(ProjectStick projectStick) {
        Integer rows;
        if (projectStick.getId() != null && projectStick.getId() > 0) {
            rows = projectStickService.updateStick(projectStick);
        } else {
            rows = projectStickService.insert(projectStick);
        }
        return rows;
    }

    private void checkAndInsertProjectStick(List<Long> usefulProjectIds, Long userId, Long tenantId) {
        Set<Long> projectIds ;
        List<ProjectStick> projectSticks = projectStickService.listByProjectIdsAndUserId(usefulProjectIds, userId, tenantId);
        projectIds = projectSticks.stream().map(ProjectStick::getProjectId).collect(Collectors.toSet());
        for (Long projectId : usefulProjectIds) {
            if (CollectionUtils.isEmpty(projectIds) || !projectIds.contains(projectId)) {
                ProjectStick projectStick = new ProjectStick();
                projectStick.setTenantId(tenantId);
                projectStick.setProjectId(projectId);
                projectStick.setCreateUserId(userId);
                insertOrUpDateProjectStick(projectStick);
            }
        }
    }

    public Map<Long, Project> getCreateStatus(List<Long> projectIdList) {
        if (CollectionUtils.isEmpty(projectIdList)) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        Map<Long, Project> result = new HashMap<>(projectIdList.size());
        List<Project> projects = projectDao.listByIds(projectIdList);
        if (CollectionUtils.isEmpty(projects) || projects.size() != projectIdList.size()) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_PROJECT);
        }
        for (Project project : projects) {
            result.put(project.getId(), project);
        }
        return result;
    }

    public Map<Long, Project> getProjectMap(Collection<Long> projectIds) {
        Map<Long, Project> idProjectMap = new HashMap<>();

        List<Project> projects = projectDao.listByIds(projectIds);
        for (Project project : projects) {
            idProjectMap.put(project.getId(), project);
        }

        return idProjectMap;
    }

    public Map<Long, Project> getProjectsForMask(Collection<Long> projectIds) {
        if (CollectionUtils.isEmpty(projectIds)) {
            return Maps.newHashMap();
        }
        return getProjectMap(projectIds);
    }

    public Map<Long, String> mapProjectNames(Long tenantId) {
        Map<Long, String> result = new HashMap<>();
        List<Map<String, Object>> maps = projectDao.mapProjectNames(tenantId);
        for (Map<String, Object> map : maps) {
            result.put(MathUtil.getLongVal(map.get("id")), String.valueOf(map.get("project_name")));
        }
        return result;
    }

    /**
     * 获取待绑定的项目列表
     *
     * @param tenantId
     */
    public List<Project> getBindingProjects(Long tenantId, Long projectId, String projectAlias, Long targetTenantId, String dtToken) {
        if (targetTenantId == null) {
            targetTenantId = tenantId;
        }
        //只能绑定 到对应的 引擎目录下
        List<Integer> nowEngineType = projectEngineService.getUsedEngineTypeList(projectId);

        checkUserInTenant(dtToken, targetTenantId);
        List<Project> projects = projectDao.listByType(targetTenantId, null, ProjectType.GENERAL.getType());
        if (CollectionUtils.isNotEmpty(projects)) {
            projects.removeIf(project -> {
                if (project.getId().equals(projectId)) {
                    return true;
                    }
                List<Integer> targetEngines = projectEngineService.getUsedEngineTypeList(project.getId());
                if (CollectionUtils.isNotEmpty(targetEngines)) {
                    return !(nowEngineType.containsAll(targetEngines) && targetEngines.containsAll(nowEngineType));
                }
                return false;
            });
        }
        return projects;
    }

    /**
     * 绑定测试环境和生产环境
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindingProject(Long tenantId, Long projectId, Long produceProjectId, Long userId) {
        checkIfProjectCanBinding(projectId);
        checkIfProjectCanBinding(produceProjectId);
        checkIfProjectEngineCanBind(projectId, produceProjectId);

        // 被绑定的生产项目是否为空
        getProjectById(produceProjectId);
        projectDao.updateProduceProject(projectId, produceProjectId, ProjectType.TEST.getType(), userId);
        projectDao.updateProduceProject(produceProjectId, projectId, ProjectType.PRODUCE.getType(), userId);

        // 绑定完关闭调度
        this.closeOrOpenSchedule(tenantId, projectId, 1, userId);
    }

    /**
     * 绑定发布目标时，需判断发布目标与当前项目具有同类计算引擎
     * 若没有，则提示用户发布目标与当前项目不一致，且不允许用户绑定
     */
    private void checkIfProjectEngineCanBind(Long testProjectId, Long produceProjectId) {
        List<ProjectEngine> testEngines = projectEngineDao.getByProjectId(testProjectId);
        List<ProjectEngine> produceEngines = projectEngineDao.getByProjectId(produceProjectId);
        List<Integer> engineTypes = produceEngines.stream().map(ProjectEngine::getEngineType).collect(Collectors.toList());

        for (ProjectEngine testEngine : testEngines) {
            if (!engineTypes.contains(testEngine.getEngineType())) {
                throw new RdosDefineException("目标项目没有配置引擎：" + MultiEngineType.getByType(testEngine.getEngineType()).name());
            }
        }
    }


    /**
     * 开启或关闭调度
     */
    public void closeOrOpenSchedule(Long tenantId, Long projectId, int status, Long userId) {
        if (status != 0 && status != 1) {
            status = 0;
        }
        projectDao.updateScheduleById(tenantId, projectId, status, userId);
        engineProjectService.updateSchedule(projectId, AppType.RDOS.getType(),status);
    }

    /**
     * 是否开启下载查询结果
     *
     * @param tenantId
     * @param projectId
     * @param status
     */
    public void closeOrOpenDownloadSelect(Long tenantId, Long projectId, int status, Long userId) {
        if (status != 0 && status != 1) {
            status = 0;
        }
        projectDao.updateAllowDownload(tenantId, projectId, status, userId);
    }


    /**
     * 判断项目是否为空，包括：函数（实时，离线），任务（实时，离线），资源（实时，离线），表（离线）
     */
    public boolean isNotEmptyProject(Long tenantId, Long projectId) {
        try {
            boolean isEmpty;
            Object service;
            for (String checkEmptyObjectName : CHECK_EMPTY_OBJECT_NAMES) {
                service = context.getBean(checkEmptyObjectName);
                isEmpty = (Boolean) service.getClass().getMethod(CHECK_EMPTY_FUNC_NAME, Long.class, Long.class)
                        .invoke(service, new Object[]{tenantId, projectId});
                if (!isEmpty) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("{}", e);
            throw new RdosDefineException("判断项目是否为空失败");
        }
    }

    /**
     * 检测项目是否可绑定
     *
     * @param projectId
     */
    public void checkIfProjectCanBinding(Long projectId) {
        Project project = getProjectById(projectId);

        if (project == null) {
            throw new RdosDefineException("项目不存在");
        }

        // 此项目是否已绑定其它项目
        if (!ProjectType.GENERAL.getType().equals(project.getProjectType())) {
            throw new RdosDefineException("此项目已绑定其它项目,无法再进行绑定");
        }

        // 此项目是否已被其它项目绑定
        Project produceProject = projectDao.getByProduceProjectId(projectId);
        if (produceProject != null) {
            throw new RdosDefineException(String.format("项目[%s]已被项目[%s]绑定", project.getProjectName(), produceProject.getProjectName()));
        }
    }

    public List<Long> getCloseScheduleProjectIds() {
        return projectDao.listIdByScheduleStatus(1);
    }

    public void checkCanMakePackage(Long projectId) {
        Project project = getProjectById(projectId);
        if (ProjectType.GENERAL.getType().equals(project.getProjectType())) {
            throw new RdosDefineException("该项目还未绑定生产项目，不能创建包");
        }

        if (ProjectType.PRODUCE.getType().equals(project.getProjectType())) {
            throw new RdosDefineException("该项目为生产项目，不能创建包");
        }
    }

    /**
     * 获取表名列表
     *
     * @param dtuicTenantId
     * @param database
     * @return
     * @throws Exception
     */
    public List<String> getTableNameList(Long dtuicTenantId, String database, Long projectId, Integer tableType) {
        DataSourceType dataSourceType = null;
        if (ETableType.HIVE.getType() == tableType) {
            dataSourceType = batchDataSourceService.getHadoopDefaultDataSourceByProjectId(projectId);
        } else {
            MultiEngineType multiEngineType = TableTypeEngineTypeMapping.getEngineTypeByTableType(tableType);
            dataSourceType = batchDataSourceService.getDataSourceTypeByEngineType(multiEngineType.getType(), projectId);
        }
        List<String> tableNameList = jdbcServiceImpl.getTableList(dtuicTenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType.getVal()), database);
        return tableNameList;
    }


    /**
     * 获取可创建项目的database
     *
     * @param dtuicTenantId
     * @return
     * @throws Exception
     */
    public Map<Integer, List<String>> getRetainDB(Long dtuicTenantId, Boolean backupFilter, Long userId, Integer engineType) {

        Map<Integer, List<String>> retainDB = Maps.newHashMap();
        IProjectService projectService = multiEngineServiceFactory.getProjectService(engineType);
        if (Objects.isNull(projectService)) {
            throw new RdosDefineException("获取不到相关的引擎信息！");
        }

        try {
            List<String> dbList = projectService.getRetainDB(dtuicTenantId, userId);

            if (dbList.size() == 0) {
                retainDB.put(engineType, dbList);
                return retainDB;
            }

            //移除已经使用过的db
            List<EngineTenantVO> engineTenantList = engineTenantService.listEngineTenant(dtuicTenantId, engineType).getData();
            if (engineTenantList == null){
                engineTenantList = new ArrayList<>();
            }
            List<Long> dtuicTenantIds  = engineTenantList.stream().map(EngineTenantVO::getTenantId).collect(Collectors.toList());
            List<String> projectNameList = projectEngineService.getTenantUsedDbName(dtuicTenantIds, engineType);
            for (String projectName : projectNameList) {
                dbList.remove(projectName);
            }

            //过滤用于back_up的db
            if (BooleanUtils.isNotFalse(backupFilter)) {
                //过滤掉backup库
                dbList = dbList.stream().filter(db -> {
                    return !db.startsWith(BACKUP_DB_PRE);
                }).collect(Collectors.toList());
            }

            retainDB.put(engineType, dbList);
        } catch (Exception e){
            throw new RdosDefineException(String.format("链接%s数据源连接失败,错误是: %s", MultiEngineType.getByType(engineType).getName(), e.getMessage()), e);
        }


        return retainDB;
    }

    /**
     * 获取当前租户下指定引擎类型db的表信息
     *
     * @param projectId
     * @param engineType
     * @param dbName
     * @return
     */
    public List<String> getDBTableList(Long projectId, Long dtuicTenantId, Integer engineType, String dbName, Long userId) {
        IProjectService projectService = multiEngineServiceFactory.getProjectService(engineType);
        if (Objects.isNull(projectService)) {
            throw new RdosDefineException(String.format("不支持该引擎类别 : %d", engineType));
        }
        try {
            return projectService.getDBTableList(dtuicTenantId, userId, dbName, projectId);
        } catch (Exception e) {
            throw new RdosDefineException(ErrorCode.SHOW_TABLE_ERROR, e);
        }
    }


    public Project getByName(String projectName, Long tenantId) {
        Project pj = projectDao.getByName(projectName, tenantId);
        if (pj == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_PROJECT);
        }
        return pj;
    }

    public List<TenantProjectVO> getAllByTenantId(Long userId, Long tenantId, String dtToken, Boolean isAdmin, Boolean isRoot) {
        this.checkUserInTenant(dtToken, tenantId);
        Set<Long> usefulProjectIds = getUsefulProjectIds(userId, tenantId, isAdmin, isRoot);
        if (CollectionUtils.isEmpty(usefulProjectIds)) {
            return Lists.newArrayList();
        }
        final List<Project> projects = this.projectDao.listByIds(usefulProjectIds);
        if (CollectionUtils.isEmpty(projects)) {
            return Collections.EMPTY_LIST;
        }
        return projects.stream().map(p -> {
            TenantProjectVO vo = new TenantProjectVO();
            vo.setProjectId(p.getId());
            vo.setProjectName(p.getProjectName());
            vo.setProjectAlias(p.getProjectAlias());
            vo.setProjectType(p.getProjectType());
            vo.setStatus(p.getStatus());
            vo.setTenantId(tenantId);
            return vo;
        }).collect(Collectors.toList());
    }


    private void checkUserInTenant(String token, Long tenantId) {
        Tenant tenant = tenantService.getTenantById(tenantId);
        if (tenant == null) {
            throw new RdosDefineException("租户不存在", ErrorCode.NOT_USED);
        }
        List<UserTenant> userTenants = DtUicUserConnect.getUserTenants(environmentContext.getDtUicUrl(), token, tenant.getTenantName());
        if (CollectionUtils.isEmpty(userTenants)) {
            throw new RdosDefineException("您不是该租户成员", ErrorCode.NOT_USED);
        }
        Long dtUicTenantId = tenant.getDtuicTenantId();

        Optional<UserTenant> first = userTenants.stream().filter(ut -> {
            Long uicTenantId = ut.getTenantId();
            return uicTenantId.equals(dtUicTenantId);
        }).findFirst();
        if (!first.isPresent()) {
            throw new RdosDefineException("您不是该租户成员", ErrorCode.NOT_USED);
        }
    }

    /**
     * 以数据源中的数据为准，比较并返回需要添加和删除的表
     * 添加：数据源中存在，本地不存在的表
     * 删除：数据源中不存在，本地存在的表
     * @param metaTableNames 数据源中的表
     * @param localTableNames 本地中的表（数据地图）
     * @return
     */
    private BatchCompareIntrinsicTableResultVO getMetaTableAndLocalTableDiff(List<String> metaTableNames, List<String> localTableNames) {
        BatchCompareIntrinsicTableResultVO intrinsicTableResultVO = new BatchCompareIntrinsicTableResultVO();
        metaTableNames = CollectionUtils.isEmpty(metaTableNames) ? Lists.newArrayList() : metaTableNames;
        localTableNames = localTableNames == null ? Lists.newArrayList() : localTableNames;
        intrinsicTableResultVO.setAddTablesName(metaTableNames);
        intrinsicTableResultVO.setDropTablesName(localTableNames);

        // 如果 hiveTableNames 或 localTableNames 为空，则无需判断，直接返回
        if (CollectionUtils.isEmpty(metaTableNames) || CollectionUtils.isEmpty(localTableNames)) {
            return intrinsicTableResultVO;
        }

        //将表名转为小写放到map中，使用map是方便使用map的get方法进行判断，提高效率
        Map<String, Integer> metaTableNamesMap = new HashMap(metaTableNames.size());
        metaTableNames.forEach(tableName -> metaTableNamesMap.put(tableName.toLowerCase(), 1));
        Set<String> localTableNameSets = localTableNames.stream().map(String::toLowerCase).collect(Collectors.toSet());

        Iterator<String> iterator = localTableNameSets.iterator();
        while (iterator.hasNext()){
            String localTableName = iterator.next();
            //如果本地和数据源底层都存在这张表，则不需要添加和删除
            if(metaTableNamesMap.get(localTableName) != null){
                metaTableNamesMap.remove(localTableName);
                iterator.remove();
            }
        }

        intrinsicTableResultVO.setAddTablesName(new ArrayList<>(metaTableNamesMap.keySet()));
        intrinsicTableResultVO.setDropTablesName(new ArrayList<>(localTableNameSets));
        return intrinsicTableResultVO;
    }


    /**
     * 获取项目首页的统计信息
     *
     * @return
     */
    public HomePageVo getHomePages(Long userId, Boolean isAdmin, Long tenantId, Boolean isRoot) {
        HomePageVo pageVo = new HomePageVo();
        //项目总数
        Integer totalCount = 0;
        Set<Long> usefulProjectIds = this.getUsefulProjectIds(userId, tenantId, isAdmin, isRoot);
        if (CollectionUtils.isNotEmpty(usefulProjectIds)) {
            totalCount = projectDao.countByIdsAndFuzzyNameAndType(new ArrayList<>(usefulProjectIds), null, null, FAILED_STATUS, null, tenantId,null);
            pageVo.setTotalProjects(totalCount);
            //总占用存储
            Long size = batchTableInfoDao.countProjectTableSize(usefulProjectIds);
            pageVo.setTotalDataSize(HdfsOperator.unitConverter(Optional.ofNullable(size).orElse(0L)));
            //今日失败总数
            List<ScheduleJobStatusVO> scheduleJobStatusVOS = scheduleJobService.getStatusCountByProjectIds(new ArrayList<>(usefulProjectIds), tenantId, AppType.RDOS.getType(), null).getData();
            Map<Long, Map<String, Integer>> jobCounts = getJobCounts(scheduleJobStatusVOS);
            int jobFaileds = jobCounts.values().stream().map(p -> p.get(TaskStatus.FAILED.name()))
                    .collect(Collectors.toList()).stream().mapToInt(Integer::intValue).sum();
            pageVo.setTotalFailJobs(jobFaileds);
        } else {
            pageVo.setTotalFailJobs(0);
            pageVo.setTotalProjects(0);
            pageVo.setTotalDataSize("0");
        }

        return pageVo;
    }

    /**
     * 根据目录获取所有的项目信息
     *
     * @param catalogueId
     * @return
     */
    public List<Project> getProjectsByCatalogueId(Long catalogueId) {
        if (catalogueId == null) {
            return Collections.EMPTY_LIST;
        }

        return projectDao.getByCatalogueId(catalogueId);
    }

    /**
     * 获取项目名称
     * @param projectId
     * @return
     */
    public String getProjectNameById(Long projectId){
        if(projectId == null){
            return "";
        }
        Project project = projectDao.getOne(projectId);
        if (project == null || project.getProjectName() == null) {
            return "";
        }
        return project.getProjectName();
    }

    /**
     * 获取所有项目
     *
     * @param dtuicTenantId
     * @param total         全部/该userId有参加的项目
     * @param dtuicUserId
     * @param isRoot        是否是uic的root用户
     * @return
     */
    public List<Project> getProjectsByUserAndTenant(Long dtuicTenantId, Boolean total, Long dtuicUserId, Boolean isRoot) {
        if (Objects.isNull(dtuicTenantId) || Objects.isNull(dtuicUserId)) {
            throw new RdosDefineException("dtuicTenantId or dtuicUserId is null");
        }
        Tenant tenant = tenantService.getTenantByDtUicTenantId(dtuicTenantId);
        if (Objects.isNull(tenant)) {
            return Collections.EMPTY_LIST;
        }
        User user = userService.getUserByDtUicUserId(dtuicUserId);
        if (Objects.isNull(user)) {
            return Collections.EMPTY_LIST;
        }
        return getAllProjects(tenant.getId(), total, user.getId(), isRoot);
    }

    /**
     * 根据租户id列表查询项目
     * @param tenantIds
     * @return
     */
    public List<Project> listByTenantIds(List<Long> tenantIds) {
        return projectDao.listByTenantIds(tenantIds);
    }

    /**
     * 获取租户下所有的项目信息
     *
     * @param tenantId
     * @return
     */
    public List<Project> listByTenantId(Long tenantId) {
        return projectDao.listByTenantId(tenantId);
    }
}
