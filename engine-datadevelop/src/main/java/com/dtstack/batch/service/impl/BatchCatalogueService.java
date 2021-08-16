package com.dtstack.batch.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.enums.CatalogueType;
import com.dtstack.batch.common.enums.EngineCatalogueType;
import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.*;
import com.dtstack.batch.domain.*;
import com.dtstack.batch.domain.po.ProjectCataloguePO;
import com.dtstack.batch.enums.RdosBatchCatalogueTypeEnum;
import com.dtstack.batch.enums.TemplateCatalogue;
import com.dtstack.batch.service.task.impl.BatchTaskService;
import com.dtstack.batch.service.task.impl.BatchTaskTemplateService;
import com.dtstack.batch.service.task.impl.ReadWriteLockService;
import com.dtstack.batch.vo.*;
import com.dtstack.batch.web.task.vo.result.BatchTaskGetComponentVersionResultVO;
import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.engine.api.domain.BatchTask;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * @author sishu.yss、toutian
 */

@Service
public class BatchCatalogueService {

    private static final Logger logger = LoggerFactory.getLogger(BatchCatalogueService.class);

    @Autowired
    private BatchCatalogueDao batchCatalogueDao;

    @Autowired
    private BatchResourceDao batchResourceDao;

    @Autowired
    private BatchTaskDao batchTaskDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private BatchFunctionDao batchFunctionDao;

    @Autowired
    private DictService dictService;

    @Autowired
    private BatchUserService batchUserService;

    @Autowired
    private BatchDataCatalogueDao batchDataCatalogueDao;

    @Autowired
    public BatchTaskService batchTaskService;

    @Autowired
    public BatchTaskTemplateService batchTaskTemplateService;

    @Resource(name = "batchProjectService")
    private ProjectService projectService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource(name = "batchTenantService")
    private TenantService tenantService;

    private static List<String> FUNCTION_MANAGER = Lists.newArrayList("函数管理");

    private static final Long DEFAULT_NODE_PID = 0L;

    private static final Integer TABLE_LIMIT = 1000;

    private static final String TABLE_QUERY = "表查询";

    private static final String TASK_DEVELOPE = "任务开发";

    private static final Integer INIT_SIZE = 3;

    private static final String PARAM_COMMAND = "yyyyMMdd-1";

    private static final String PARAM_NAME = "bdp.system.bizdate";

    private static final String TYPE4SYSTEM = "0";

    private static Integer SUB_AMOUNTS_LIMIT = 2000;

    private final static String PROJECT_ROOT_DEFAULT_NAME = "default";

    private static String FOLDER_TYPE = "folder";

    /**
     * 如果没有选择对接引擎 就要默认初始化下列目录
     */
    private static Set<String> NO_ENGINE_CATALOGUE = Sets.newHashSet(CatalogueType.TASK_DEVELOP.getType(), CatalogueType.SCRIPT_MANAGER.getType(), CatalogueType.RESOURCE_MANAGER.getType(), CatalogueType.TABLE_QUERY.getType(), CatalogueType.FUNCTION_MANAGER.getType());

    @Autowired
    private ReadWriteLockService readWriteLockService;

    /**
     * 新增目录
     */
    public CatalogueVO addCatalogue(BatchCatalogue catalogue, long projectId, long userId) {
        if (Objects.isNull(catalogue)) {
            throw new RdosDefineException("新增文件夹信息不能为空");
        }
        if (StringUtils.isBlank(catalogue.getNodeName())) {
            throw new RdosDefineException("文件夹名称不能为空");
        }
        catalogue.setNodeName(catalogue.getNodeName().trim());
        // 校验文件夹中是否含有空格
        if (catalogue.getNodeName().contains(" ")) {
            throw new RdosDefineException("文件夹名称中不能含有空格");
        }
        BatchCatalogue dbCatalogue = batchCatalogueDao.getByPidAndName(projectId, catalogue.getNodePid(), catalogue.getNodeName());
        if (dbCatalogue != null) {
            throw new RdosDefineException("文件夹已存在");
        }

        // 校验当前父级直接一层的子目录或者任务的个数总数不可超过SUB_AMOUNTS_LIMIT(2000)
        Integer subAmountsByNodePid = batchCatalogueDao.getSubAmountsByNodePid(catalogue.getNodePid(), projectId);
        if (subAmountsByNodePid >= SUB_AMOUNTS_LIMIT) {
            throw new RdosDefineException(ErrorCode.SUBDIRECTORY_OR_FILE_AMOUNT_RESTRICTIONS);
        }

        int parentCatalogueLevel = catalogue.getNodePid() == 0L ? 0 : this.isOverLevelLimit(catalogue.getNodePid());

        catalogue.setLevel(parentCatalogueLevel + 1);
        catalogue.setCreateUserId(userId);
        catalogue.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        catalogue.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));

        //保存上级节点引擎类型
        BatchCatalogue parentDbCatalogue = batchCatalogueDao.getOne(catalogue.getNodePid());
        catalogue.setEngineType(null == parentDbCatalogue ? 0 : parentDbCatalogue.getEngineType());
        if (null == catalogue.getCatalogueType()) {
            catalogue.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
        }
        if (RdosBatchCatalogueTypeEnum.PROJECT.getType() == catalogue.getCatalogueType()) {
            if (catalogue.getLevel() > 3) {
                throw new RdosDefineException(ErrorCode.CREATE_PROJECT_CATALOGUE_LEVE);
            }
        }
        addOrUpdate(catalogue);

        CatalogueVO cv = CatalogueVO.toVO(catalogue);
        cv.setType("folder");
        return cv;
    }

    /**
     * 根据项目 ID 删除目录
     * @param projectId
     */
    public void deleteByProjectId(Long projectId) {
        // 获取所有项目的目录信息
        List<BatchCatalogue> batchCatalogues = getByProjectId(projectId);
        if (CollectionUtils.isEmpty(batchCatalogues)) {
            return;
        }

        // 遍历项目目录，如果目录下面存在其他项目，则不允许删除
        // 排序，子节点优先处理，可以从下到上一级一级删除
        batchCatalogues.sort(Comparator.comparing(BatchCatalogue::getLevel, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        for (BatchCatalogue batchCatalogue : batchCatalogues) {
            // 如果为项目文件夹且存在子节点，则不能删除
            if (RdosBatchCatalogueTypeEnum.PROJECT.getType().equals(batchCatalogue.getCatalogueType())
                    && batchCatalogueDao.getSubAmountsByNodePid(batchCatalogue.getId(), null) > 0) {
                continue;
            }

            // 获取所有的项目
            List<Project> projects = projectService.getProjectsByCatalogueId(batchCatalogue.getId());

            // 如果为空说明是任务目录
            if (CollectionUtils.isEmpty(projects)) {
                batchCatalogueDao.deleteById(batchCatalogue.getId());
                continue;
            }

            // 如果超过两个，则直接返回
            if (projects.size() > 1) {
                continue;
            }

            // 如果唯一一个不是该项目，则直接返回
            if (!projectId.equals(projects.get(0).getId())) {
                continue;
            }

            batchCatalogueDao.deleteById(batchCatalogue.getId());
        }
    }

    /**
     * 根据项目 ID 获取全部目录
     *
     * @param projectId
     * @return
     */
    public List<BatchCatalogue> getByProjectId(Long projectId) {
        if (null == projectId) {
            return Lists.newArrayList();
        }

        return batchCatalogueDao.listByProjectId(projectId);
    }

    public BatchCatalogue addOrUpdate(BatchCatalogue batchCatalogue) {
        if (batchCatalogue.getId() > 0) {
            batchCatalogueDao.update(batchCatalogue);
        } else {
            batchCatalogueDao.insert(batchCatalogue);
        }

        return batchCatalogue;
    }


    private Integer getMultiTypeByEngine(String name) {
        if (StringUtils.isEmpty(name)) {
            return 0;
        }
        if (name.equals(EngineCatalogueType.SPARK.getDesc())) {
            return MultiEngineType.HADOOP.getType();
        } else if (name.equals(EngineCatalogueType.LIBRA.getDesc())) {
            return MultiEngineType.LIBRA.getType();
        } else if (name.equals(EngineCatalogueType.TIDB.getDesc())) {
            return MultiEngineType.TIDB.getType();
        } else if (name.equals(EngineCatalogueType.ORACLE.getDesc())) {
            return MultiEngineType.ORACLE.getType();
        } else if (name.equals(EngineCatalogueType.GREENPLUM.getDesc())) {
            return MultiEngineType.GREENPLUM.getType();
        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public void initCatalogue(Long tenantId, Long dtuicTenantId, Long projectId, Long userId, List<ProjectEngineVO> projectEngineVOS,String dtToken) {
        List<Dict> batchCatalogueDicts = dictService.getDictByType(DictType.BATCH_CATALOGUE.getValue());
        List<Integer> supportEngineType = projectEngineVOS.stream().map(ProjectEngineVO::getEngineType).collect(Collectors.toList());
        List<Dict> batchCatalogueDictLevelOne = this.initCatalogueDictLevelByEngineType(supportEngineType);

        Map<Integer, Set<String>> catalogueMapping = batchCatalogueDictLevelOne
                .stream()
                .collect(Collectors.groupingBy(Dict::getDictValue,
                        Collectors.mapping(Dict::getDictNameZH, Collectors.toSet())));
        for (Dict dict : batchCatalogueDicts) {
            BatchCatalogue batchCatalogue = new BatchCatalogue();
            batchCatalogue.setNodeName(dict.getDictNameZH());
            batchCatalogue.setNodePid(DEFAULT_NODE_PID);
            batchCatalogue.setOrderVal(dict.getDictSort());
            batchCatalogue.setEngineType(0);
            batchCatalogue.setLevel(CatalogueLevel.ONE.getLevel());
            batchCatalogue.setProjectId(projectId);
            batchCatalogue.setTenantId(tenantId);
            batchCatalogue.setCreateUserId(userId);
            batchCatalogue.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
            batchCatalogue = addOrUpdate(batchCatalogue);
            if (CollectionUtils.isNotEmpty(catalogueMapping.get(dict.getDictValue()))) {
                for (String name : catalogueMapping.get(dict.getDictValue())) {
                    BatchCatalogue sc1 = new BatchCatalogue();
                    //初始化的时候 设置目录引擎类型
                    sc1.setEngineType(this.getMultiTypeByEngine(name));
                    sc1.setNodeName(name);
                    sc1.setLevel(CatalogueLevel.SECOND.getLevel());
                    sc1.setNodePid(batchCatalogue.getId());
                    sc1.setProjectId(projectId);
                    sc1.setTenantId(tenantId);
                    sc1.setCreateUserId(userId);
                    sc1.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
                    addOrUpdate(sc1);
                    if (name.equals(TASK_DEVELOPE) && supportEngineType.contains(MultiEngineType.HADOOP.getType())) {
                        //初始化任务模版
                        this.initTemplateCatalogue(sc1, tenantId, projectId, userId, supportEngineType,dtToken);
                    }
                    this.initEngineCatalogue(tenantId, projectId, userId, name, sc1);
                }
            }
        }
    }

    /**
     * 初始化项目目录
     *
     * @param tenantId
     * @param userId
     */
    public void initProjectCatalogue(Long tenantId, Long userId) {
        try {
            // 检测根目录是否存在
            BatchCatalogue projectRoot = batchCatalogueDao.getProjectRoot(tenantId, RdosBatchCatalogueTypeEnum.PROJECT.getType());
            if (projectRoot ==null){
                BatchCatalogue batchCatalogue = new BatchCatalogue();
                batchCatalogue.setNodeName(PROJECT_ROOT_DEFAULT_NAME);
                batchCatalogue.setNodePid(DEFAULT_NODE_PID);
                batchCatalogue.setProjectId(-1L);
                batchCatalogue.setOrderVal(0);
                batchCatalogue.setEngineType(-1);
                batchCatalogue.setLevel(CatalogueLevel.ONE.getLevel());
                batchCatalogue.setTenantId(tenantId);
                batchCatalogue.setCreateUserId(userId);
                batchCatalogue.setCatalogueType(RdosBatchCatalogueTypeEnum.PROJECT.getType());
                addOrUpdate(batchCatalogue);
            }
        } catch (Exception e) {
            logger.info("initProjectRootCatalogue {} acquire error", tenantId, e);
        }
    }

    /**
     * 项目 添加 新的引擎后 添加新目录
     *
     * @param tenantId
     * @param projectId
     * @param userId
     * @param addEngineType
     */
    @Transactional(rollbackFor = Exception.class)
    public void initExtEngineCatalogue(Long tenantId, Long projectId, Long userId, List<Integer> addEngineType) {
        if (CollectionUtils.isNotEmpty(addEngineType)) {
            for (Integer engineType : addEngineType) {
                BatchCatalogue catalogue = null;
                EngineCatalogueType engineCatalogueType = EngineCatalogueType.getByEngineType(engineType);
                if (null == engineCatalogueType) {
                    logger.error("not support engine type");
                    return;
                }
                catalogue = batchCatalogueDao.getByLevelAndProjectIdAndName(CatalogueLevel.OTHER.getLevel(), projectId, engineCatalogueType.getDesc());
                if (null != catalogue) {
                    logger.error("project {} has init engine type {} catalogue ", projectId, engineType);
                    continue;
                }
                BatchCatalogue functionManager = batchCatalogueDao.getByLevelAndProjectIdAndName(CatalogueLevel.ONE.getLevel(), projectId, FUNCTION_MANAGER.get(0));
                if (null == functionManager) {
                    throw new RdosDefineException("函数管理未初始化");
                }

                BatchCatalogue sqlCatalogue = batchCatalogueDao.getByLevelAndProjectIdAndName(CatalogueLevel.SECOND.getLevel(), projectId, engineCatalogueType.getDesc());
                if (Objects.isNull(sqlCatalogue)) {
                    BatchCatalogue addEngineCatalogue = new BatchCatalogue();
                    addEngineCatalogue.setEngineType(engineType);
                    addEngineCatalogue.setNodeName(engineCatalogueType.getDesc());
                    addEngineCatalogue.setLevel(CatalogueLevel.SECOND.getLevel());
                    addEngineCatalogue.setNodePid(functionManager.getId());
                    addEngineCatalogue.setProjectId(projectId);
                    addEngineCatalogue.setTenantId(tenantId);
                    addEngineCatalogue.setCreateUserId(userId);
                    addEngineCatalogue.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());

                    //添加spark libra impala function 这一层
                    addOrUpdate(addEngineCatalogue);
                    //初始化下一层目录
                    this.initEngineCatalogue(tenantId, projectId, userId, engineCatalogueType.getDesc(), addEngineCatalogue);
                }
            }
        }

    }

    private List<Dict> initCatalogueDictLevelByEngineType(List<Integer> supportEngineType) {
        List<Dict> dictByType = dictService.getDictByType(DictType.BATCH_CATALOGUE_L1.getValue());
        //根据引擎类型初始化对应的函数管理目录
        if (CollectionUtils.isNotEmpty(supportEngineType)) {
            if (!supportEngineType.contains(MultiEngineType.LIBRA.getType())) {
                //没有libra  移除libra function 目录
                dictByType = dictByType.stream()
                        .filter(dict -> !dict.getDictNameZH().equals(EngineCatalogueType.LIBRA.getDesc()))
                        .collect(Collectors.toList());
            }
            if (!supportEngineType.contains(MultiEngineType.HADOOP.getType())) {
                dictByType = dictByType.stream()
                        .filter(dict -> !dict.getDictNameZH().equals(EngineCatalogueType.SPARK.getDesc()))
                        .collect(Collectors.toList());
            }
            if (!supportEngineType.contains(MultiEngineType.TIDB.getType())) {
                dictByType = dictByType.stream()
                        .filter(dict -> !dict.getDictNameZH().equals(EngineCatalogueType.TIDB.getDesc()))
                        .collect(Collectors.toList());
            }
            if (!supportEngineType.contains(MultiEngineType.ORACLE.getType())) {
                dictByType = dictByType.stream()
                        .filter(dict -> !dict.getDictNameZH().equals(EngineCatalogueType.ORACLE.getDesc()))
                        .collect(Collectors.toList());
            }
            if (!supportEngineType.contains(MultiEngineType.GREENPLUM.getType())) {
                dictByType = dictByType.stream()
                        .filter(dict -> !dict.getDictNameZH().equals(EngineCatalogueType.GREENPLUM.getDesc()))
                        .collect(Collectors.toList());
            }
            return dictByType;
        } else {
            return dictByType.stream().filter(dict -> NO_ENGINE_CATALOGUE.contains(dict.getDictName())).collect(Collectors.toList());
        }
    }

    private void initEngineCatalogue(Long tenantId, Long projectId, Long userId, String name, BatchCatalogue sc1) {
        //一级菜单初始化的时候  函数管理的一级菜单为引擎 原有的一级菜单 系统函数 自定义函数 挂在引擎下 作为二级菜单
        if (isNeedFunction(name)) {
            //自定义函数
            List<Dict> batchFunction = dictService.getDictByType(DictType.BATCH_FUNCTION.getValue());
            if (CollectionUtils.isNotEmpty(batchFunction)) {
                for (Dict func : batchFunction) {
                    if (sc1.getEngineType() == MultiEngineType.LIBRA.getType() || sc1.getEngineType() == MultiEngineType.TIDB.getType() || sc1.getEngineType() == MultiEngineType.ORACLE.getType()) {
                        // libra 函数没有 自定义函数文件
                        if (func.getDictNameEN().equals(CatalogueType.CUSTOM_FUNCTION.getType())) {
                            continue;
                        }
                    }
                    if (func.getDictNameEN().equals(CatalogueType.CUSTOM_FUNCTION.getType()) &&
                            sc1.getEngineType() == MultiEngineType.GREENPLUM.getType()) {
                        continue;
                    }
                    if (func.getDictNameEN().equals(CatalogueType.GREENPLUM_CUSTOM_FUNCTION.getType())) {
                        //greenplum支持存储过程
                        if (sc1.getEngineType() != MultiEngineType.GREENPLUM.getType()) {
                            continue;
                        }
                    }
                    if (func.getDictNameEN().equals(CatalogueType.PROCEDURE_FUNCTION.getType())) {
                        //greenplum支持存储过程
                        if (sc1.getEngineType() != MultiEngineType.GREENPLUM.getType()) {
                            continue;
                        }
                    }
                    //需要 系统函数 自定义函数 挂在当前目录下  libra 没有自定义函数文件夹
                    BatchCatalogue sc2 = new BatchCatalogue();
                    sc2.setNodeName(func.getDictNameZH());
                    sc2.setLevel(CatalogueLevel.SECOND.getLevel());
                    sc2.setNodePid(sc1.getId());
                    sc2.setProjectId(projectId);
                    sc2.setTenantId(tenantId);
                    sc2.setEngineType(sc1.getEngineType());
                    sc2.setCreateUserId(userId);
                    sc2.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
                    addOrUpdate(sc2);
                }
            }
        }
    }

    private boolean isNeedFunction(String name) {
        return EngineCatalogueType.SPARK.getDesc().equalsIgnoreCase(name) || EngineCatalogueType.LIBRA.getDesc().equalsIgnoreCase(name)
                || EngineCatalogueType.TIDB.getDesc().equalsIgnoreCase(name) || EngineCatalogueType.ORACLE.getDesc().equalsIgnoreCase(name)
                || EngineCatalogueType.GREENPLUM.getDesc().equalsIgnoreCase(name);
    }

    private List<BatchCatalogue> initTemplateCatalogue(Catalogue sc1, Long tenantId, Long projectId, Long userId, List<Integer> supportEngineType,String dtToken) {
        int taskType = 0;
        if (supportEngineType.size() == 1 && supportEngineType.contains(MultiEngineType.LIBRA.getType())) {
            taskType = EJobType.LIBRA_SQL.getVal();
        } else {
            taskType = EJobType.SPARK_SQL.getVal();
        }
        List<BatchCatalogue> templateCatalogeuList = new ArrayList<>(TemplateCatalogue.getValues(taskType).size());
        //在任务开发目录下添加默认的任务模版文件
        BatchCatalogue bc = new BatchCatalogue();
        bc.setLevel(CatalogueLevel.OTHER.getLevel());
        bc.setNodePid(sc1.getId());
        bc.setProjectId(projectId);
        bc.setTenantId(tenantId);
        bc.setCreateUserId(userId);
        bc.setEngineType(sc1.getEngineType());
        List<TemplateCatalogue> values = TemplateCatalogue.getValues(taskType);
        HashMap<String, Long> idsMap = new HashMap<>();
        for (TemplateCatalogue temp : values) {
            //相同目录只创建一次
            if (!idsMap.containsKey(temp.getValue())) {
                bc.setNodeName(temp.getValue());
                bc.setId(0L);
                bc.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
                addOrUpdate(bc);
                idsMap.put(temp.getValue(), bc.getId());
            }
            templateCatalogeuList.add(bc);
            try {
                String content = batchTaskTemplateService.getContentByType(taskType, temp.getType());
                //初始化任务
                TaskResourceParam batchTask = new TaskResourceParam();
                batchTask.setName(temp.getFileName());
                batchTask.setTaskType(taskType);
                batchTask.setNodePid(idsMap.get(temp.getValue()));
                batchTask.setComputeType(ComputeType.BATCH.getType());
                batchTask.setLockVersion(0);
                batchTask.setVersion(0);
                batchTask.setTenantId(tenantId);
                batchTask.setProjectId(projectId);
                batchTask.setUserId(userId);
                batchTask.setCreateUserId(userId);
                batchTask.setModifyUserId(userId);

                if (StringUtils.isEmpty(content)) {
                    throw new RdosDefineException("sql模版为空");
                }
                batchTask.setSqlText(content);

                //添加init脚本中带有的任务参数
                List<Map> taskVariables = new ArrayList<>();
                Map<String, String> variable = new HashMap<>(INIT_SIZE);
                variable.put("paramCommand", PARAM_COMMAND);
                variable.put("paramName", PARAM_NAME);
                variable.put("type", TYPE4SYSTEM);
                taskVariables.add(variable);
                batchTask.setTaskVariables(taskVariables);
                if (taskType == EJobType.SPARK_SQL.getVal()) {
                    List<BatchTaskGetComponentVersionResultVO> hadoopVersions = batchTaskService.getComponentVersionByTaskType(tenantService.getDtuicTenantId(tenantId), taskType);
                    if (CollectionUtils.isNotEmpty(hadoopVersions)) {
                        batchTask.setComponentVersion(hadoopVersions.get(0).getComponentVersion());
                    }
                }
                batchTaskService.addOrUpdateTask(batchTask);
            } catch (Exception e) {
                logger.error("", e);
                throw new RdosDefineException("初始化目录失败");
            }
        }
        return templateCatalogeuList;
    }

    /**
     * 获取任务或脚本的上级目录，用于前端搜索定位
     *
     * @return
     */
    public CatalogueVO getLocation(Long projectId, Long userId, String catalogueType, Long id, String name, Long tenantId) {

        // 获取当前资源的父级类目id，一直到根目录为止
        List<Long> grandCatalogueIds = new ArrayList<>();

        if (CatalogueType.TASK_DEVELOP.getType().equals(catalogueType)) {
            if (id != null) {
                BatchTask batchTask = batchTaskDao.getOne(id);
                if (batchTask != null) {
                    getGrandCatalogueIds(batchTask.getNodePid(), grandCatalogueIds);
                }
            } else if (StringUtils.isNotEmpty(name)) {
                List<BatchTask> tasks = batchTaskDao.listByNameFuzzy(projectId, name);
                if (CollectionUtils.isNotEmpty(tasks)) {
                    for (BatchTask task : tasks) {
                        getGrandCatalogueIds(task.getNodePid(), grandCatalogueIds);
                    }
                }
            }
        }

        Catalogue rootCatalogue = null;
        if (CatalogueType.SCRIPT_MANAGER.getType().equals(catalogueType)) {
            rootCatalogue = batchCatalogueDao.getByLevelAndProjectIdAndName(0, projectId, "脚本管理");
        } else if (CatalogueType.TASK_DEVELOP.getType().equals(catalogueType)) {
            rootCatalogue = batchCatalogueDao.getByLevelAndProjectIdAndName(0, projectId, "任务管理");
        }

        if (rootCatalogue == null) {
            throw new RdosDefineException("根目录找不到");
        }

        CatalogueVO root = CatalogueVO.toVO(rootCatalogue);
        root.setType("folder");
        getTree(root, grandCatalogueIds, projectId, userId, catalogueType, tenantId);
        return root;
    }

    /**
     * 根据当前节点递归查询所有父节点列表，包含当前节点
     * @param currentId
     * @param ids
     * @return
     */
    private void getGrandCatalogueIds(Long currentId, List<Long> ids) {
        ids.add(currentId);
        getGrandCatalogueId(currentId, ids);
    }

    /**
     * 根据当前节点递归查询所有父节点列表
     * @param currentId
     * @param ids
     * @return 父节点列表
     */
    private void getGrandCatalogueId(Long currentId, List<Long> ids) {
        BatchCatalogue catalogue = batchCatalogueDao.getOne(currentId);
        if (catalogue != null && catalogue.getLevel() >= 1) {
            ids.add(catalogue.getNodePid());
            getGrandCatalogueId(catalogue.getNodePid(), ids);
        }
    }

    private void getTree(CatalogueVO root, List<Long> grandCatalogueIds, Long projectId, Long userId, String catalogueType, Long tenantId) {
        root.setCatalogueType(catalogueType);

        if (grandCatalogueIds.contains(root.getId())) {
            if (CollectionUtils.isEmpty(root.getChildren()) && root.getType().equals("folder")) {
                root = getChildNode(null, projectId, root, true, userId, tenantId);
            }
        }
        else {
            root = getChildNode(null, projectId, root, false, userId, tenantId);
        }

        if (CollectionUtils.isNotEmpty(root.getChildren())) {
            for (CatalogueVO vo : root.getChildren()) {
                if (vo.getType().equals("folder")) {
                    getTree(vo, grandCatalogueIds, projectId, userId, catalogueType, tenantId);
                }
            }
        }
    }

    /**
     * 获取目录
     */
    public CatalogueVO getCatalogue(Long appointProjectId, long projectId, boolean isGetFile, long nodePid,
                                    String catalogueType, int userId, Long tenantId, Integer taskType, Long parentId) {

        //根目录
        CatalogueVO rootCatalugue = new CatalogueVO();
        if (nodePid == 0) {
            List<CatalogueVO> catalogues = getCatalogueOne(projectId, tenantId);
            rootCatalugue.setChildren(catalogues);
        } else if (taskType != null && EJobType.WORK_FLOW.getVal().intValue() == taskType) {
            rootCatalugue.setId(nodePid);
            rootCatalugue.setCatalogueType(catalogueType);
            List<BatchTask> subTasks = batchTaskService.getFlowWorkSubTasksWithoutSql(nodePid);
            BatchCatalogue currentCatalogue = this.getBatchCatalogueByType(parentId, catalogueType);
            if (currentCatalogue == null) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_CATALOGUE);
            }
            rootCatalugue.setLevel(currentCatalogue.getLevel() + 1);
            List<CatalogueVO> children = build(subTasks, rootCatalugue, projectId, userId);
            rootCatalugue.setChildren(children);
            rootCatalugue.setType("flow");
            rootCatalugue.setTaskType(EJobType.WORK_FLOW.getVal());
            BatchTask task = batchTaskDao.getOne(nodePid);
            ReadWriteLockVO readWriteLockVO = readWriteLockService.getDetail(
                    projectId, nodePid,
                    ReadWriteLockType.BATCH_TASK, userId,
                    task.getModifyUserId(),
                    task.getGmtModified());
            rootCatalugue.setReadWriteLockVO(readWriteLockVO);
            rootCatalugue.setVersion(readWriteLockVO.getVersion());
        } else {
            rootCatalugue.setId(nodePid);
            rootCatalugue.setCatalogueType(catalogueType);
            rootCatalugue = getChildNode(appointProjectId, projectId, rootCatalugue, isGetFile, userId, tenantId);
        }

        return rootCatalugue;
    }

    /**
     * 获取项目目录  因为前端异步加载有问题  所以显示所有的目录
     */
    public CatalogueVO getProjectCatalogue(Long tenantId, boolean isGetFile, Long userId, Boolean isRoot, Boolean isAdmin) {

        CatalogueVO a;
        List<BatchCatalogue> batchCatalogues = batchCatalogueDao.getListByTenantIdAndCatalogueType(tenantId, RdosBatchCatalogueTypeEnum.PROJECT.getType());
        List<CatalogueVO> childerCatalogue = batchCatalogues.stream().map(bean -> {
            CatalogueVO vo = new CatalogueVO();
            BeanUtils.copyProperties(bean, vo);
            vo.setCatalogueType(RdosBatchCatalogueTypeEnum.PROJECT.getType().toString());
            vo.setName(bean.getNodeName());
            vo.setParentId(bean.getNodePid());
            vo.setType("folder");
            vo.setChildren(new ArrayList<>());
            return vo;
        }).collect(Collectors.toList());
        if (isGetFile) {
            Set<Long> usefulProjectIds = projectService.getUsefulProjectIds(userId, tenantId, isAdmin, isRoot);
            if(CollectionUtils.isNotEmpty(usefulProjectIds)){
                List<ProjectCataloguePO> childerroject = projectService.getCatalogueListByTenantIdAndCatalogueId(usefulProjectIds,tenantId, RdosBatchCatalogueTypeEnum.PROJECT.getType());
                childerCatalogue.addAll(childerroject.stream().map(bean -> {
                    CatalogueVO vo = new CatalogueVO();
                    vo.setCatalogueType(RdosBatchCatalogueTypeEnum.PROJECT.getType().toString());
                    vo.setId(bean.getProjectRealId());
                    vo.setProjectAlias(bean.getProjectAlias());
                    vo.setLevel(bean.getLevel() + 1);
                    vo.setName(bean.getProjectName());
                    vo.setParentId(bean.getId());
                    vo.setType("file");
                    vo.setChildren(null);
                    vo.setCreateUser("");
                    return vo;
                }).collect(Collectors.toList()));
            }
        }
        a = getProjectCatalogueTree(childerCatalogue);
        return a;
    }


    /**
     * 更新目录（移动和重命名）
     */
    public void updateCatalogue(BatchCatalogueVO catalogueInput, Long userId) {
        if (RdosBatchCatalogueTypeEnum.PROJECT.getType().equals(catalogueInput.getCatalogueType())) {
            //如果是项目目录且是移动才走下面这方法
            if (catalogueInput.getNodePid() != null && (!FOLDER_TYPE.equals(catalogueInput.getType()))) {
                Project project = new Project();
                project.setId(catalogueInput.getId());
                project.setCatalogueId(catalogueInput.getNodePid());
                project.setModifyUserId(userId);
                projectDao.update(project);
                return;
            }
        }

        BatchCatalogue catalogue = batchCatalogueDao.getOne(catalogueInput.getId());
        catalugeOneNotUpdate(catalogue);
        if (catalogue == null || catalogue.getIsDeleted() == 1) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_CATALOGUE);
        }

        if (canNotMoveCatalogue(catalogueInput.getId(), catalogueInput.getNodePid())) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_MOVE_CATALOGUE);
        }
        BatchCatalogue updateCatalogue = new BatchCatalogue();
        updateCatalogue.setId(catalogueInput.getId());
        //重命名
        if (catalogueInput.getNodeName() != null) {
            updateCatalogue.setNodeName(catalogueInput.getNodeName());
        }
        //移动
        if (catalogueInput.getNodePid() != null && catalogueInput.getNodePid() != 0) {
            int parentLevel = this.isOverLevelLimit(catalogueInput.getNodePid());
            updateCatalogue.setLevel(parentLevel + 1);
            updateCatalogue.setNodePid(catalogueInput.getNodePid());
        }else {
            updateCatalogue.setNodePid(catalogue.getNodePid());
        }
        //判断移动的目录下 有没有相同名称的文件夹
        BatchCatalogue byLevelAndPIdAndProjectIdAndName = batchCatalogueDao.getBeanByTenantIdAndNameAndParentId(catalogue.getTenantId(),updateCatalogue.getNodeName(), updateCatalogue.getNodePid());
        if (byLevelAndPIdAndProjectIdAndName != null && (!byLevelAndPIdAndProjectIdAndName.getId().equals(catalogue.getId()))){
            throw new RdosDefineException(ErrorCode.FILE_NAME_REPETITION);
        }
        updateCatalogue.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        addOrUpdate(updateCatalogue);

    }


    /**
     * 删除目录
     */
    public void deleteCatalogue(BatchCatalogue catalogueInput) {

        BatchCatalogue catalogue = batchCatalogueDao.getOne(catalogueInput.getId());

        catalugeOneNotUpdate(catalogue);

        if (catalogue == null || catalogue.getIsDeleted() == 1) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_CATALOGUE);
        }

        //判断文件夹下任务
        List<BatchTask> taskList = batchTaskDao.listBatchTaskByNodePid(catalogue.getId(), catalogue.getProjectId());
        List<BatchResource> resourceList = batchResourceDao.listByPidAndProjectId(catalogue.getProjectId(), catalogue.getId());

        if (taskList.size() > 0 || resourceList.size() > 0) {
            throw new RdosDefineException(ErrorCode.CATALOGUE_NO_EMPTY);
        }

        //判断文件夹下子目录
        if (CollectionUtils.isNotEmpty(batchCatalogueDao.listByPidAndProjectId(catalogue.getId(), catalogue.getProjectId()))) {
            throw new RdosDefineException(ErrorCode.CATALOGUE_NO_EMPTY);
        }

        catalogue.setIsDeleted(Deleted.DELETED.getStatus());
        catalogue.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        batchCatalogueDao.deleteById(catalogue.getId());
    }


    /**
     * 一级目录不允许修改
     *
     * @param catalogue
     * @author jiangbo
     */
    private void catalugeOneNotUpdate(BatchCatalogue catalogue) {
        if (catalogue.getCatalogueType() == RdosBatchCatalogueTypeEnum.PROJECT.getType()) {
            if (catalogue.getLevel() == 0) {
                throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
            }
        } else {
            if (catalogue.getLevel() == 0 || catalogue.getLevel() == 1) {
                throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
            }
        }
    }


    /**
     * 判断目录级别是否超出限制
     *
     * @param nodePid 父目录id
     * @return
     * @author toutian
     */
    private int isOverLevelLimit(long nodePid) {
        BatchCatalogue parentCatalogue = batchCatalogueDao.getOne(nodePid);
        return parentCatalogue.getLevel();
    }

    public List<CatalogueVO> getCatalogueOne(Long projectId, Long tenantId) {
        List<BatchCatalogue> zeroCatalogues = batchCatalogueDao.listByLevelAndProjectId(0, projectId);
        List<Dict> catalogueOneDicts = dictService.getDictByType(DictType.BATCH_CATALOGUE_L1.getValue());
        List<Dict> dicts = dictService.getDictByType(DictType.BATCH_CATALOGUE.getValue());

        Map<String, String> catalogueOneType = new HashMap<>(catalogueOneDicts.size());
        Map<String, String> catalogueType = new HashMap<>(dicts.size());
        Map<String, Integer> catalogueOrder = new HashMap<>(dicts.size());
        for (Dict oneDict : catalogueOneDicts) {
            catalogueOneType.put(oneDict.getDictNameZH(), oneDict.getDictNameEN());
        }
        for (Dict dict : dicts) {
            catalogueType.put(dict.getDictNameZH(), dict.getDictNameEN());
            catalogueOrder.put(dict.getDictNameZH(), dict.getDictSort());
        }
        //目录排序，新建的项目有默认排序，但需要兼容老项目，无奈
        zeroCatalogues.sort((v1, v2) -> {
            return (catalogueOrder.get(v1.getNodeName()) != null ? (catalogueOrder.get(v2.getNodeName()) != null ?
                    (catalogueOrder.get(v1.getNodeName()) < catalogueOrder.get(v2.getNodeName()) ? -1 : 0) : 1) : 1);
        });

        List<CatalogueVO> zeroCatalogueVOs = new ArrayList<>(zeroCatalogues.size());
        for (BatchCatalogue zeroCatalogue : zeroCatalogues) {
            CatalogueVO zeroCatalogueVO = CatalogueVO.toVO(zeroCatalogue);

            zeroCatalogueVO.setCatalogueType(catalogueType.get(zeroCatalogue.getNodeName()));
            zeroCatalogueVO.setType("folder");
            zeroCatalogueVOs.add(zeroCatalogueVO);

            List<BatchCatalogue> oneCatalogues = batchCatalogueDao.listByPidAndProjectId(zeroCatalogue.getId(), zeroCatalogue.getProjectId());
            if (CatalogueType.TABLE_QUERY.getType().equals(zeroCatalogueVO.getCatalogueType())) {
                //如果是tableQuery类型，则需要查询hive_catalogue表所以需要修改rootId
                Long rootId = batchDataCatalogueDao.getRootIdByTenantId(tenantId);
                if (rootId == null || rootId < 0) {
                    throw new RdosDefineException("该租户下数据类目未生成");
                }
                oneCatalogues.forEach(one -> one.setId(rootId));
            }
            if (FUNCTION_MANAGER.contains(zeroCatalogue.getNodeName())) {
                BatchCatalogue systemFuncCatalogue = batchCatalogueDao.getSystemFunctionCatalogueOne(EngineCatalogueType.SPARK.getType());
                if (systemFuncCatalogue != null ) {
                    oneCatalogues.add(systemFuncCatalogue);
                }
            }
            List<CatalogueVO> oneCatalogueVOs = new ArrayList<>(oneCatalogues.size());
            for (BatchCatalogue oneCatalogue : oneCatalogues) {
                CatalogueVO oneCatalogueVO = CatalogueVO.toVO(oneCatalogue);
                if (EngineCatalogueType.SPARK.getDesc().equals(oneCatalogueVO.getName())
                        || EngineCatalogueType.LIBRA.getDesc().equals(oneCatalogueVO.getName())
                        || EngineCatalogueType.TIDB.getDesc().equals(oneCatalogueVO.getName())
                        || EngineCatalogueType.ORACLE.getDesc().equals(oneCatalogueVO.getName())
                        || EngineCatalogueType.GREENPLUM.getDesc().equalsIgnoreCase(oneCatalogueVO.getName())) {
                    // spark libral  函数管理 不是目录
                    oneCatalogueVO.setType("catalogue");
                } else {
                    oneCatalogueVO.setType("folder");
                }
                //兼容老数据
                if (Objects.isNull(catalogueOneType.get(oneCatalogue.getNodeName())) && EngineCatalogueType.GREENPLUM.getDesc().equalsIgnoreCase(oneCatalogueVO.getName())) {
                    char[] cName = oneCatalogue.getNodeName().toCharArray();
                    //老数据首字母大写
                    cName[0]-=32;
                    String gpName = String.valueOf(cName);
                    String gpManageName = catalogueOneType.get(gpName);
                    oneCatalogueVO.setCatalogueType(gpManageName);
                }else {
                    oneCatalogueVO.setCatalogueType(catalogueOneType.get(oneCatalogue.getNodeName()));
                }
                oneCatalogueVOs.add(oneCatalogueVO);
            }
            zeroCatalogueVO.setChildren(oneCatalogueVOs);
        }
        return zeroCatalogueVOs;
    }

    private List<CatalogueVO> build(List<BatchTask> taskList, CatalogueVO currentCatalogueVO, long projectId, long userId) {
        List<CatalogueVO> files = new ArrayList<>();
        taskList.sort(Comparator.comparing(BatchTask::getName));
        if (CollectionUtils.isNotEmpty(taskList)) {
            for (BatchTask task : taskList) {
                CatalogueVO childTask = new CatalogueVO();
                childTask.setId(task.getId());
                childTask.setName(task.getName());
                childTask.setTaskType(task.getTaskType());
                childTask.setType("file");
                childTask.setLevel(currentCatalogueVO.getLevel() + 1);
                childTask.setChildren(null);
                childTask.setParentId(currentCatalogueVO.getId());
                childTask.setCreateUser(batchUserService.getUserName(task.getCreateUserId()));
                if (task.getTaskType().equals(EJobType.PYTHON.getVal())) {
                    JSONObject exeArgs = JSONObject.parseObject(task.getExeArgs());
                    childTask.setOperateModel(exeArgs.getInteger("operateModel"));
                    childTask.setLearningType(0);
                    childTask.setPythonVersion(exeArgs.getInteger("--python-version"));
                }

                ReadWriteLockVO readWriteLockVO = readWriteLockService.getDetail(
                        projectId, task.getId(),
                        ReadWriteLockType.BATCH_TASK,
                        userId, task.getModifyUserId(), task.getGmtModified());
                childTask.setReadWriteLockVO(readWriteLockVO);
                childTask.setIsSubTask(1);
                files.add(childTask);
            }
        }
        return files;
    }

    /**
     * 获得当前节点的子节点信息，包括子孙文件夹和子孙文件
     *
     * @param appointProjectId 仅对表查询使用，为空则查询整个租户下的表
     * @param projectId        项目id
     * @param isGetFile
     * @param userId
     * @return
     * @author jiangbo、toutian
     */
    private CatalogueVO getChildNode(Long appointProjectId, long projectId, CatalogueVO currentCatalogueVO, boolean isGetFile, long userId, long tenantId) {
        BatchCatalogue currentCatalogue = this.getBatchCatalogueByType(currentCatalogueVO.getId(), currentCatalogueVO.getCatalogueType());
        if (currentCatalogue == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_CATALOGUE);
        }

        currentCatalogueVO.setName(currentCatalogue.getNodeName());
        currentCatalogueVO.setLevel(currentCatalogue.getLevel());
        currentCatalogueVO.setParentId(currentCatalogue.getNodePid());
        currentCatalogueVO.setType("folder");
        currentCatalogueVO.setEngineType(currentCatalogue.getEngineType());


        // 前端是根据type 区分函数目录下的类型的
        // 所以以下皆是 函数管理下的目录
        Predicate<String> testIsFunctionCatalogue = nodeName ->
                Lists.newArrayList(CatalogueType.SYSTEM_FUNCTION.getType(),
                        CatalogueType.CUSTOM_FUNCTION.getType(),
                        "ImpalaSysFunc", "ImpalaSQLFunction",
                        "LibraFunc", "LibraSQLFunction",
                        "TiDBSysFunc", "TiDBSQLFunction",
                        "OracleSysFunc", "OracleSQLFunction",
                        "GreenPlumSysFunc","GreenPlumSQLFunction",
                        "ProcedureFunction", "GreenPlumCustomFunction"
                ).contains(nodeName);


        BatchDataCatalogue hiveCatalogueRoot = null;
        if (currentCatalogueVO.getCatalogueType().equals(CatalogueType.TABLE_QUERY.getType())) {
            hiveCatalogueRoot = batchDataCatalogueDao.getRootByTenantId(tenantId);
            if (hiveCatalogueRoot == null) {
                throw new RdosDefineException("类目管理根目录初始化失败");
            }
        }

        Map<Long, String> userNames = Maps.newHashMap();
        //获取目录下的资源或任务列表
        if (isGetFile) {
            List<CatalogueVO> files = new ArrayList<>();
            if (currentCatalogueVO.getCatalogueType().equals(CatalogueType.TASK_DEVELOP.getType())) {
                Map<Long, List<CatalogueVO>> flowChildren = Maps.newHashMap();
                List<BatchTask> taskList = batchTaskDao.catalogueListBatchTaskByNodePid(currentCatalogueVO.getId(), projectId);
                taskList.sort(Comparator.comparing(BatchTask::getName));
                if (CollectionUtils.isNotEmpty(taskList)) {

                    List<Long> taskIds = taskList.stream().map(BatchTask::getId).collect(Collectors.toList());
                    Map<Long, ReadWriteLockVO> vos = getReadWriteLockVOMap(taskList.get(0).getProjectId(), taskIds, userId, userNames);

                    for (BatchTask task : taskList) {
                        CatalogueVO childTask = new CatalogueVO();
                        childTask.setId(task.getId());
                        childTask.setName(task.getName());
                        childTask.setTaskType(task.getTaskType());
                        if (task.getTaskType().intValue() == EJobType.WORK_FLOW.getVal()) {
                            childTask.setType("flow");
                        } else {
                            childTask.setType("file");
                        }
                        childTask.setStatus(task.getStatus());
                        childTask.setLevel(currentCatalogueVO.getLevel() + 1);
                        childTask.setChildren(null);
                        childTask.setParentId(currentCatalogueVO.getId());
                        childTask.setCreateUser(getUserNameInMemory(userNames, task.getCreateUserId()));

                        if (task.getTaskType().equals(EJobType.PYTHON.getVal())) {
                            JSONObject exeArgs = JSONObject.parseObject(task.getExeArgs());
                            childTask.setOperateModel(exeArgs.getInteger("operateModel"));
                            childTask.setLearningType(0);
                            childTask.setPythonVersion(exeArgs.getInteger("--python-version"));
                        }

                        ReadWriteLockVO readWriteLockVO = vos.get(task.getId());
                        if (readWriteLockVO.getLastKeepLockUserName() == null) {
                            readWriteLockVO.setLastKeepLockUserName(getUserNameInMemory(userNames, task.getModifyUserId()));
                            readWriteLockVO.setGmtModified(task.getGmtModified());
                        }
                        childTask.setReadWriteLockVO(readWriteLockVO);
                        if (task.getFlowId() > 0L) {
                            childTask.setIsSubTask(1);
                            List<CatalogueVO> temp = flowChildren.get(task.getFlowId());
                            if (CollectionUtils.isEmpty(temp)) {
                                temp = Lists.newArrayList();
                                temp.add(childTask);
                                flowChildren.put(task.getFlowId(), temp);
                            } else {
                                flowChildren.get(task.getFlowId()).add(childTask);
                            }
                        } else {
                            files.add(childTask);
                        }
                    }
                    for (CatalogueVO vo : files) {
                        //fixme dataScience适配
                        if (vo.getTaskType().equals(EJobType.ALGORITHM_LAB.getVal())) {
                            continue;
                        }
                        Long id = vo.getId();
                        List<CatalogueVO> children = flowChildren.get(id);
                        if (CollectionUtils.isNotEmpty(children)) {
                            vo.setChildren(children);
                        }
                    }
                }
            } else if (testIsFunctionCatalogue.test(currentCatalogueVO.getCatalogueType())) {
                List<BatchFunction> functionList = batchFunctionDao.listByNodePidAndProjectId(currentCatalogue.getProjectId(), currentCatalogueVO.getId());
                if (CollectionUtils.isNotEmpty(functionList)) {
                    functionList.sort(Comparator.comparing(BatchFunction::getName));
                    for (BatchFunction function : functionList) {
                        CatalogueVO child = new CatalogueVO();
                        child.setId(function.getId());
                        child.setLevel(currentCatalogueVO.getLevel() + 1);
                        child.setName(function.getName());
                        child.setType("file");
                        child.setChildren(null);
                        child.setCreateUser(getUserNameInMemory(userNames, function.getCreateUserId()));
                        child.setParentId(function.getNodePid());
                        child.setEngineType(currentCatalogueVO.getEngineType());
                        child.setCatalogueType(currentCatalogueVO.getCatalogueType());
                        files.add(child);
                    }

                }

            } else if (currentCatalogueVO.getCatalogueType().equals(CatalogueType.RESOURCE_MANAGER.getType())) {
                List<BatchResource> resourceList = batchResourceDao.listByPidAndProjectId(projectId,
                        currentCatalogueVO.getId());
                resourceList.sort(Comparator.comparing(BatchResource::getResourceName));
                if (CollectionUtils.isNotEmpty(resourceList)) {
                    for (BatchResource resource : resourceList) {
                        CatalogueVO childResource = new CatalogueVO();
                        childResource.setId(resource.getId());
                        childResource.setName(resource.getResourceName());
                        childResource.setType("file");
                        childResource.setResourceType(resource.getResourceType());
                        childResource.setLevel(currentCatalogueVO.getLevel() + 1);
                        childResource.setChildren(null);
                        childResource.setParentId(currentCatalogueVO.getId());
                        childResource.setCreateUser(getUserNameInMemory(userNames, resource.getCreateUserId()));
                        files.add(childResource);
                    }
                }
            }
            currentCatalogueVO.setChildren(files);
        }

        //获取目录下的子目录
        List<BatchCatalogue> childCatalogues = this.getChildCataloguesByType(currentCatalogueVO.getId(), currentCatalogueVO.getCatalogueType(), hiveCatalogueRoot, tenantId, currentCatalogue.getProjectId());
        childCatalogues = keepInitCatalogueBeTop(childCatalogues, currentCatalogue, userId);
        List<CatalogueVO> children = new ArrayList<>();
        for (BatchCatalogue catalogue : childCatalogues) {
            CatalogueVO cv = CatalogueVO.toVO(catalogue);
            cv.setType("folder");
            this.changeSQLFunctionCatalogueType(catalogue, cv, currentCatalogueVO);
            children.add(cv);
        }

        if (currentCatalogueVO.getChildren() == null) {
            currentCatalogueVO.setChildren(children);
        } else {
            currentCatalogueVO.getChildren().addAll(0, children);
        }

        return currentCatalogueVO;
    }


    private void changeSQLFunctionCatalogueType(BatchCatalogue catalogue, CatalogueVO cv, CatalogueVO currentCatalogueVO) {
        cv.setCatalogueType(currentCatalogueVO.getCatalogueType());
        //如果是libraSQL 或者是sparkSQl下的function  需要替换child 的catalogueType
        if (CatalogueType.SPARKSQL_FUNCTION.getType().equals(currentCatalogueVO.getCatalogueType()) ||
                CatalogueType.LIBRASQL_FUNCTION.getType().equals(currentCatalogueVO.getCatalogueType()) ||
                CatalogueType.TIDBSQL_FUNCTION.getType().equals(currentCatalogueVO.getCatalogueType()) ||
                CatalogueType.ORACLE_FUNCTION.getType().equals(currentCatalogueVO.getCatalogueType()) ||
                CatalogueType.GREENPLUM_FUNCTION.getType().equals(currentCatalogueVO.getCatalogueType())) {
            if ("自定义函数".equals(catalogue.getNodeName())) {
                if (CatalogueType.GREENPLUM_FUNCTION.getType().equals(currentCatalogueVO.getCatalogueType())) {
                    cv.setCatalogueType(CatalogueType.GREENPLUM_CUSTOM_FUNCTION.getType());
                } else {
                    cv.setCatalogueType(CatalogueType.CUSTOM_FUNCTION.getType());
                }
            }
            if ("系统函数".equals(catalogue.getNodeName())) {
                cv.setCatalogueType(CatalogueType.SYSTEM_FUNCTION.getType());
            }
            if ("存储过程".equals(catalogue.getNodeName())) {
                cv.setCatalogueType(CatalogueType.PROCEDURE_FUNCTION.getType());
            }
        }
        if (CatalogueType.FUNCTION_MANAGER.getType().equals(currentCatalogueVO.getCatalogueType())) {
            if (EngineCatalogueType.SPARK.getDesc().equals(catalogue.getNodeName())) {
                cv.setCatalogueType(CatalogueType.SPARKSQL_FUNCTION.getType());
            }
            if (EngineCatalogueType.LIBRA.getDesc().equals(catalogue.getNodeName())) {
                cv.setCatalogueType(CatalogueType.LIBRASQL_FUNCTION.getType());
            }
            if (EngineCatalogueType.TIDB.getDesc().equals(catalogue.getNodeName())) {
                cv.setCatalogueType(CatalogueType.TIDBSQL_FUNCTION.getType());
            }
            if (EngineCatalogueType.ORACLE.getDesc().equals(catalogue.getNodeName())) {
                cv.setCatalogueType(CatalogueType.ORACLE_FUNCTION.getType());
            }
            if (EngineCatalogueType.GREENPLUM.getDesc().equals(catalogue.getNodeName())) {
                cv.setCatalogueType(CatalogueType.GREENPLUM_FUNCTION.getType());
            }
        }

    }

    private Map<Long, ReadWriteLockVO> getReadWriteLockVOMap(Long projectId, List<Long> taskIds, Long userId, Map<Long, String> names) {
        Map<Long, ReadWriteLockVO> vos = Maps.newHashMap();
        //一次查询800条
        int num = taskIds.size() % 800 == 0 ? taskIds.size() / 800 : taskIds.size() / 800 + 1;
        for (int i = 0; i < num; i++) {
            int begin = i * 800;
            int end = (i + 1) * 800;
            if (i == num - 1) {
                end = taskIds.size();
            }
            vos.putAll(readWriteLockService.getLocks(projectId, ReadWriteLockType.BATCH_TASK, taskIds.subList(begin, end), userId, names));
        }
        return vos;
    }

    private String getUserNameInMemory(Map<Long, String> names, Long userId) {
        if (names.containsKey(userId)) {
            return names.get(userId);
        } else {
            String name = batchUserService.getUserName(userId);
            names.put(userId, name);
            return name;
        }
    }

    /**
     * 对文件夹名称排序，并保证默认创建的5个文件夹在最前面
     * ps--不支持对老项目初始化目录
     *
     * @param childCatalogues
     * @return
     */
    private List<BatchCatalogue> keepInitCatalogueBeTop(List<BatchCatalogue> childCatalogues, Catalogue parentCatalogue, Long userId) {
        if (parentCatalogue.getLevel().equals(CatalogueLevel.SECOND.getLevel()) &&
                parentCatalogue.getNodeName().equals(TASK_DEVELOPE)) {
            //初始化默认目录
            List<BatchCatalogue> collect = childCatalogues.stream().filter(new Predicate<BatchCatalogue>() {
                @Override
                public boolean test(BatchCatalogue batchCatalogue) {
                    List<String> values = TemplateCatalogue.getValues();
                    if (values.contains(batchCatalogue.getNodeName())) {
                        return true;
                    }
                    return false;
                }
            }).collect(Collectors.toList());
            List<BatchCatalogue> batchCatalogueTop5 = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(childCatalogues)) {
                Iterator<BatchCatalogue> iterator = childCatalogues.iterator();
                while (iterator.hasNext()) {
                    if (collect.contains(iterator.next())) {
                        iterator.remove();
                    }
                }
                childCatalogues.sort(Comparator.comparing(BatchCatalogue::getNodeName));
                collect.sort(Comparator.comparing(BatchCatalogue::getId));
                collect.addAll(childCatalogues);
                batchCatalogueTop5 = collect;
            }
            return batchCatalogueTop5;

        } else {
            childCatalogues.sort(Comparator.comparing(BatchCatalogue::getNodeName));
            return childCatalogues;
        }
    }

    private BatchCatalogue getBatchCatalogueByType(Long catalogueId, String catalogueType) {
        BatchCatalogue currentCatalogue = new BatchCatalogue();
        if (catalogueType.equals(CatalogueType.TABLE_QUERY.getType())) {
            BatchDataCatalogue hiveCatalogue = batchDataCatalogueDao.getOne(catalogueId);
            if (hiveCatalogue == null) {
                return null;
            }
            BeanUtils.copyProperties(hiveCatalogue, currentCatalogue);
            if (hiveCatalogue.getLevel() == 1) {
                hiveCatalogue.setNodeName("表查询");
            }
        } else {
            currentCatalogue = batchCatalogueDao.getOne(catalogueId);
        }
        return currentCatalogue;
    }

    private List<BatchCatalogue> getChildCataloguesByType(Long catalogueId, String catalogueType, BatchDataCatalogue hiveCatalogueRoot, Long tenantId, Long projectId) {
        List<BatchCatalogue> childCatalogues = new ArrayList<>();
        if (catalogueType.equals(CatalogueType.TABLE_QUERY.getType())) {
            List<BatchDataCatalogue> hiveCatalogues = batchDataCatalogueDao.listByTenantIdAndPId(tenantId, catalogueId);
            for (BatchDataCatalogue hiveCatalogue : hiveCatalogues) {
                BatchCatalogue batchCatalogue = new BatchCatalogue();
                BeanUtils.copyProperties(hiveCatalogue, batchCatalogue);
                batchCatalogue.setLevel(hiveCatalogueRoot.getLevel() == 0L ? batchCatalogue.getLevel() + 1 : batchCatalogue.getLevel());
                childCatalogues.add(batchCatalogue);
            }
        } else {
            childCatalogues = batchCatalogueDao.listByPidAndProjectId(catalogueId, projectId);
            this.replaceSystemFunction(catalogueId, catalogueType, childCatalogues);
        }
        return childCatalogues;
    }

    private void replaceSystemFunction(Long catalogueId, String catalogueType, List<BatchCatalogue> childCatalogues) {
        if (CatalogueType.SPARKSQL_FUNCTION.getType().equals(catalogueType)
                || CatalogueType.LIBRASQL_FUNCTION.getType().equals(catalogueType)
                || CatalogueType.TIDBSQL_FUNCTION.getType().equals(catalogueType)
                || CatalogueType.ORACLE_FUNCTION.getType().equals(catalogueType)
                || CatalogueType.GREENPLUM_FUNCTION.getType().equals(catalogueType)) {
            BatchCatalogue one = batchCatalogueDao.getOne(catalogueId);
            EngineCatalogueType systemEngineType = EngineCatalogueType.getByeName(one == null ? null : one.getNodeName());
            //需要将系统函数替换对应 引擎的函数模板
            BatchCatalogue systemFuncCatalogue = batchCatalogueDao.getSystemFunctionCatalogueOne(systemEngineType.getType());
            if (systemFuncCatalogue == null) {
                return;
            }

            for (BatchCatalogue childCatalogue : childCatalogues) {
                if ("系统函数".equals(childCatalogue.getNodeName())) {
                    childCatalogue.setNodePid(systemFuncCatalogue.getNodePid());
                    childCatalogue.setProjectId(systemFuncCatalogue.getProjectId());
                    childCatalogue.setId(systemFuncCatalogue.getId());
                }
            }
        }
    }

    public CatalogueVO getProjectTableList(String tableName, String projectIdentifier, Integer taskType, Integer scriptType,
                                           Long userId, Boolean isRoot) {
        return null;
    }

    /**
     * 根据子目录获取目录树
     */
    public Stack<BatchCatalogue> getCatalogueTreeByChild(Long childCatalogue) {
        Stack<BatchCatalogue> stack = new Stack<>();
        BatchCatalogue catalogue = batchCatalogueDao.getOne(childCatalogue);
        while (catalogue != null && catalogue.getLevel() > 0) {
            stack.push(catalogue);
            catalogue = batchCatalogueDao.getOne(catalogue.getNodePid());
        }

        return stack;
    }

    public String getCatalogueNamePathFromBottle(Long catalogueId) {
        BatchCatalogue catalogue = batchCatalogueDao.getAllPathParentCatalogues(catalogueId);
        if (catalogue != null) {
            return buildNamePath(catalogue);
        } else {
            return StringUtils.EMPTY;
        }
    }

    private String buildNamePath(BatchCatalogue catalogue) {
        StringBuilder sb = new StringBuilder();
        sb.append(catalogue.getNodeName()).append(",");
        BatchCatalogue pcat = catalogue.getParentCatalogue();
        if (pcat != null) {
            sb.append(buildNamePath(pcat));
        }
        return sb.toString();
    }

    /**
     * 传入目录id 得到目录路径 例如a/b/c
     *
     * @param catalogueId
     * @return
     */
    public String getCatalogueNamePathByParentId(Long catalogueId) {
        BatchCatalogue catalogue = batchCatalogueDao.getAllPathParentCatalogues(catalogueId);
        if (catalogue != null) {
            return buildPath(catalogue);
        } else {
            return StringUtils.EMPTY;
        }
    }

    private String buildPath(BatchCatalogue catalogue) {
        StringBuilder sb = new StringBuilder();
        BatchCatalogue pcat = catalogue.getParentCatalogue();
        if (pcat != null) {
            sb.append(buildPath(pcat)).append("/");
        }
        sb.append(catalogue.getNodeName());
        return sb.toString();
    }

    /**
     * 新增路径
     *
     * @param pathList 路径列表
     * @param rootName 如 数据开发、资源管理等
     * @return
     */
    public Long createCataloguePath(List<String> pathList, String rootName, Long projectId, Long userId) {
        BatchCatalogue zeroCatalogue = batchCatalogueDao.getByLevelAndProjectIdAndName(1, projectId, rootName);
        Long nodePid = zeroCatalogue.getId();
        if (CollectionUtils.isNotEmpty(pathList)) {
            for (String nodeName : pathList) {
                BatchCatalogue catalogue = batchCatalogueDao.getByPidAndName(projectId, nodePid, nodeName);
                if (catalogue == null) {
                    catalogue = new BatchCatalogue();
                    catalogue.setNodeName(nodeName);
                    catalogue.setNodePid(nodePid);
                    catalogue.setTenantId(zeroCatalogue.getTenantId());
                    catalogue.setProjectId(projectId);
                    CatalogueVO catalogueVO = addCatalogue(catalogue, projectId, userId);
                    nodePid = catalogueVO.getId();
                } else {
                    nodePid = catalogue.getId();
                }
            }
        }
        return nodePid;
    }

    /**
     * 组装成树形结构
     * 两次循环就可以
     *
     * @return
     */
    private CatalogueVO getProjectCatalogueTree(List<CatalogueVO> childer) {
        Map<String, CatalogueVO> treeMap = new HashMap<>();
        CatalogueVO root = new CatalogueVO();
        childer.forEach(bean -> {
            treeMap.put(String.format("%s_%s", bean.getId(), bean.getType()), bean);
        });
        for (CatalogueVO catalogue : childer) {
            if (catalogue.getParentId() == 0) {
                root = treeMap.get(String.format("%s_%s", catalogue.getId(), catalogue.getType()));
            }
            if (treeMap.containsKey(String.format("%s_%s", catalogue.getParentId(), "folder"))) {
                treeMap.get(String.format("%s_%s", catalogue.getParentId(), "folder")).getChildren().add(catalogue);
            }
        }
        return root;
    }

    /**
     * 传入 目录id  按照层级 从当前层级一直到level =0的 全部组装到map中
     * @param catalogueId
     * @param map
     */
    public void getCatalogueMapById(Long catalogueId, Map<Integer, List<BatchCatalogue>> map, Set<Long> catalogueIds) {
        if (catalogueId == null){
            return;
        }
        if (catalogueIds.contains(catalogueId)){
            return;
        }
        BatchCatalogue one = batchCatalogueDao.getOne(catalogueId);
        if (one != null){
            if (map.containsKey(one.getLevel())){
                map.get(one.getLevel()).add(one);
            }else {
                map.put(one.getLevel(), Lists.newArrayList(one));
            }
            if (one.getLevel() > 0){
                getCatalogueMapById(one.getNodePid(), map, catalogueIds);
            }
        }
    }

    /**
     * 获取根节点
     *
     * @param tenantId
     * @param catalogueType
     * @return
     */
    public BatchCatalogue getRootTree(Long tenantId, Integer catalogueType) {
        BatchCatalogue projectRoot = batchCatalogueDao.getProjectRoot(tenantId, catalogueType);
        return projectRoot;
    }

    /**
     * 根据 父节点id、当前节点名称 项目Id 获取当前节点所有信息
     *
     * @param projectId
     * @param nodePid
     * @param name
     * @return
     */
    public BatchCatalogue getByPidAndName(long projectId, long nodePid, String name) {
        BatchCatalogue projectRoot = batchCatalogueDao.getByPidAndName(projectId, nodePid,name);
        return projectRoot;
    }


    /**
     * 根据 目录路径 把未初始化的路径新增
     * @param cataloguePath
     * @param tenantId
     * @param projectId
     * @param engineType
     * @param userId
     * @return
     */
    public Long operateCatalogue(Map<String,Long> catalogueMap, String cataloguePath, Long tenantId, Long projectId, Integer engineType, Long userId, String rootCatalogue) {
        BatchCatalogue byPidAndName;
        if (StringUtils.isEmpty(cataloguePath) && !catalogueMap.containsKey("")) {
            byPidAndName = this.getByPidAndName(projectId, 0L, rootCatalogue);
            catalogueMap.putIfAbsent("", byPidAndName.getId());
            return catalogueMap.get("");
        }
        if (catalogueMap.containsKey(cataloguePath)) {
            return catalogueMap.get(cataloguePath);
        }
        String[] pathSplit = cataloguePath.split("_");
        Long nodePid = 0L;
        StringBuffer nodePath = new StringBuffer();
        for (int i = 0; i < pathSplit.length; i++) {
            nodePath.append(pathSplit[i]);
            byPidAndName = this.getByPidAndName(projectId, nodePid, pathSplit[i]);
            if (byPidAndName == null) {
                byPidAndName = new BatchCatalogue();
                byPidAndName.setEngineType(engineType);
                byPidAndName.setProjectId(projectId);
                byPidAndName.setNodePid(nodePid);
                byPidAndName.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
                byPidAndName.setNodeName(pathSplit[i]);
                byPidAndName.setTenantId(tenantId);
                this.addCatalogue(byPidAndName, projectId, userId);
            }
            nodePid = byPidAndName.getId();
            catalogueMap.put(nodePath.toString(),nodePid);
            nodePath.append("_");
        }
        return catalogueMap.get(cataloguePath);
    }

    /**
     * 判断是否可以移动到当前目录
     * @param catalogueId
     * @param catalogueNodePid
     * @return
     */
    private boolean canNotMoveCatalogue(Long catalogueId, Long catalogueNodePid) {
        List<Long> ids = Lists.newArrayList();
        getGrandCatalogueIds(catalogueNodePid, ids);
        return ids.contains(catalogueId);
    }

    /**
     * 获取发布任务 在测试项目的目录id 对应生产项目 的目录id
     *
     * @param testProject 测试项目信息
     * @param testCatalogueId  任务对应测试项目的 目录id
     * @param userId  用户信息
     * @return
     */
    public Long testProjectCatalogueIdTransitionProducedCatalogueId(Project testProject, Long testCatalogueId, Long userId) {
        Long parentId = null;
        //获取测试项目 任务对应的目录树， 先进后出 栈最上面的是 目录的最顶级节点
        Stack<BatchCatalogue> stack = this.getCatalogueTreeByChild(testCatalogueId);
        while (!stack.empty()) {
            final BatchCatalogue catalogue = stack.pop();
            //获取相同层级的  生产项目中 同样名字的 目录信息   第一次是寻找 任务开发 这个层级的目录信息 所以parentId必定为null 但是一定能找到
            BatchCatalogue produceCatalogue = batchCatalogueDao.getByLevelAndParentIdAndProjectIdAndName(catalogue.getLevel(),parentId, testProject.getProduceProjectId(), catalogue.getNodeName());
            //判断目录是否存在
            if (produceCatalogue == null) {
                // 组装一个新的 目录 准备插入
                produceCatalogue = createBatchCatalogue(testProject, userId, parentId, catalogue);
                //再次校验 待插入的目录 是否存在
                BatchCatalogue dbCatalogue = getByPidAndName(testProject.getProduceProjectId(), produceCatalogue.getNodePid(), produceCatalogue.getNodeName());
                if (dbCatalogue == null) {
                    this.addCatalogue(produceCatalogue, testProject.getProduceProjectId(), userId);
                }else {
                    produceCatalogue = dbCatalogue;
                }
            }
            parentId = produceCatalogue.getId();
        }

        return parentId;
    }

    /**
     * 获取一个新的 待新增的目录 实例
     *
     * @param testProject
     * @param userId
     * @param parentId
     * @param catalogue
     * @return
     */
    private BatchCatalogue createBatchCatalogue(Project testProject, Long userId, Long parentId, BatchCatalogue catalogue) {
        BatchCatalogue produceCatalogue;
        produceCatalogue = new BatchCatalogue();
        PublicUtil.copyPropertiesIgnoreNull(catalogue, produceCatalogue);
        produceCatalogue.setId(0L);
        produceCatalogue.setNodePid(parentId);
        produceCatalogue.setCreateUserId(userId);
        produceCatalogue.setProjectId(testProject.getProduceProjectId());
        produceCatalogue.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        produceCatalogue.setGmtModified(new Timestamp(System.currentTimeMillis()));
        return produceCatalogue;
    }

}