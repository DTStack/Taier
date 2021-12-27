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

package com.dtstack.batch.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.enums.CatalogueType;
import com.dtstack.batch.common.enums.EngineCatalogueType;
import com.dtstack.batch.dao.BatchCatalogueDao;
import com.dtstack.batch.domain.BatchCatalogue;
import com.dtstack.batch.domain.BatchCatalogueVO;
import com.dtstack.batch.domain.BatchFunction;
import com.dtstack.batch.domain.BatchResource;
import com.dtstack.batch.domain.Catalogue;
import com.dtstack.batch.domain.Dict;
import com.dtstack.batch.enums.RdosBatchCatalogueTypeEnum;
import com.dtstack.batch.enums.TemplateCatalogue;
import com.dtstack.batch.service.task.impl.BatchTaskService;
import com.dtstack.batch.service.task.impl.BatchTaskTemplateService;
import com.dtstack.batch.service.task.impl.ReadWriteLockService;
import com.dtstack.batch.vo.CatalogueVO;
import com.dtstack.batch.vo.ReadWriteLockVO;
import com.dtstack.batch.vo.TaskResourceParam;
import com.dtstack.batch.vo.TenantEngineVO;
import com.dtstack.batch.web.task.vo.result.BatchTaskGetComponentVersionResultVO;
import com.dtstack.engine.common.enums.CatalogueLevel;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.Deleted;
import com.dtstack.engine.common.enums.DictType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.common.enums.ReadWriteLockType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.domain.BatchTask;
import com.dtstack.engine.master.impl.UserService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
    private BatchResourceService batchResourceService;

    @Autowired
    private BatchFunctionService batchFunctionService;

    @Autowired
    private DictService dictService;

    @Autowired
    private UserService userService;

    @Autowired
    public BatchTaskService batchTaskService;

    @Autowired
    public BatchTaskTemplateService batchTaskTemplateService;

    private static List<String> FUNCTION_MANAGER = Lists.newArrayList("函数管理");

    private static final Long DEFAULT_NODE_PID = 0L;

    private static final String TASK_DEVELOPE = "任务开发";

    private static final Integer INIT_SIZE = 3;

    private static final String PARAM_COMMAND = "yyyyMMdd-1";

    private static final String PARAM_NAME = "bdp.system.bizdate";

    private static final String TYPE4SYSTEM = "0";

    private static Integer SUB_AMOUNTS_LIMIT = 2000;

    private final static String TENANT_ROOT_DEFAULT_NAME = "default";

    private final static String FILE_TYPE_FOLDER = "folder";

    /**
     * 如果没有选择对接引擎 就要默认初始化下列目录
     */
    private static Set<String> NO_ENGINE_CATALOGUE = Sets.newHashSet(CatalogueType.TASK_DEVELOP.getType(), CatalogueType.SCRIPT_MANAGER.getType(), CatalogueType.RESOURCE_MANAGER.getType(), CatalogueType.FUNCTION_MANAGER.getType());

    @Autowired
    private ReadWriteLockService readWriteLockService;

    /**
     * 新增 and 修改目录
     * @param catalogue
     * @return
     */
    public CatalogueVO addCatalogue(BatchCatalogue catalogue) {
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
        BatchCatalogue dbCatalogue = batchCatalogueDao.getByPidAndName(catalogue.getTenantId(), catalogue.getNodePid(), catalogue.getNodeName());
        if (dbCatalogue != null) {
            throw new RdosDefineException("文件夹已存在");
        }

        // 校验当前父级直接一层的子目录或者任务的个数总数不可超过SUB_AMOUNTS_LIMIT(2000)
        Integer subAmountsByNodePid = batchCatalogueDao.getSubAmountsByNodePid(catalogue.getNodePid(), catalogue.getTenantId());
        if (subAmountsByNodePid >= SUB_AMOUNTS_LIMIT) {
            throw new RdosDefineException(ErrorCode.SUBDIRECTORY_OR_FILE_AMOUNT_RESTRICTIONS);
        }

        int parentCatalogueLevel = catalogue.getNodePid() == 0L ? 0 : this.isOverLevelLimit(catalogue.getNodePid());

        catalogue.setLevel(parentCatalogueLevel + 1);
        catalogue.setCreateUserId(catalogue.getCreateUserId());
        catalogue.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        catalogue.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));

        //保存上级节点引擎类型
        BatchCatalogue parentDbCatalogue = batchCatalogueDao.getOne(catalogue.getNodePid());
        catalogue.setEngineType(null == parentDbCatalogue ? 0 : parentDbCatalogue.getEngineType());
        if (null == catalogue.getCatalogueType()) {
            catalogue.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
        }
        if (RdosBatchCatalogueTypeEnum.TENANT.getType().equals(catalogue.getCatalogueType())) {
            if (catalogue.getLevel() > 3) {
                throw new RdosDefineException(ErrorCode.CREATE_TENANT_CATALOGUE_LEVE);
            }
        }
        addOrUpdate(catalogue);

        CatalogueVO cv = CatalogueVO.toVO(catalogue);
        cv.setType(BatchCatalogueService.FILE_TYPE_FOLDER);
        return cv;
    }


    /**
     * 新增 and 修改目录
     * @param batchCatalogue
     * @return
     */
    private BatchCatalogue addOrUpdate(BatchCatalogue batchCatalogue) {
        if (batchCatalogue.getId() > 0) {
            batchCatalogueDao.update(batchCatalogue);
        } else {
            batchCatalogueDao.insert(batchCatalogue);
        }

        return batchCatalogue;
    }


    /**
     * 根据目录名称获取Engine类型
     * @param name
     * @return
     */
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


    /**
     * 创建租户时，初始化目录信息
     * @param tenantId
     * @param userId
     * @param projectEngineVOS
     */
    @Transactional(rollbackFor = Exception.class)
    public void initCatalogue(Long tenantId, Long userId, List<TenantEngineVO> projectEngineVOS) {
        List<Dict> batchCatalogueDicts = dictService.getDictByType(DictType.BATCH_CATALOGUE.getValue());
        List<Integer> supportEngineType = projectEngineVOS.stream().map(TenantEngineVO::getEngineType).collect(Collectors.toList());
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
                    sc1.setTenantId(tenantId);
                    sc1.setCreateUserId(userId);
                    sc1.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
                    addOrUpdate(sc1);
                    if (name.equals(TASK_DEVELOPE) && supportEngineType.contains(MultiEngineType.HADOOP.getType())) {
                        //初始化任务模版
                        this.initTemplateCatalogue(sc1, tenantId, userId, supportEngineType);
                    }
                    this.initEngineCatalogue(tenantId, userId, name, sc1);
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
    public void initTenantCatalogue(Long tenantId, Long userId) {
        try {
            // 检测根目录是否存在
            BatchCatalogue catalogueRoot = batchCatalogueDao.getTenantRoot(tenantId, RdosBatchCatalogueTypeEnum.TENANT.getType());
            if (catalogueRoot ==null){
                BatchCatalogue batchCatalogue = new BatchCatalogue();
                batchCatalogue.setNodeName(TENANT_ROOT_DEFAULT_NAME);
                batchCatalogue.setNodePid(DEFAULT_NODE_PID);
                batchCatalogue.setOrderVal(0);
                batchCatalogue.setEngineType(-1);
                batchCatalogue.setLevel(CatalogueLevel.ONE.getLevel());
                batchCatalogue.setTenantId(tenantId);
                batchCatalogue.setCreateUserId(userId);
                batchCatalogue.setCatalogueType(RdosBatchCatalogueTypeEnum.TENANT.getType());
                addOrUpdate(batchCatalogue);
            }
        } catch (Exception e) {
            logger.info("initProjectRootCatalogue {} acquire error", tenantId, e);
        }
    }

    /**
     * 租户添加新的引擎后，初始引擎的目录
     *
     * @param tenantId
     * @param userId
     * @param addEngineType
     */
    @Transactional(rollbackFor = Exception.class)
    public void initExtEngineCatalogue(Long tenantId, Long userId, List<Integer> addEngineType) {
        if (CollectionUtils.isNotEmpty(addEngineType)) {
            for (Integer engineType : addEngineType) {
                BatchCatalogue catalogue = null;
                EngineCatalogueType engineCatalogueType = EngineCatalogueType.getByEngineType(engineType);
                if (null == engineCatalogueType) {
                    logger.error("not support engine type");
                    return;
                }
                catalogue = batchCatalogueDao.getByLevelAndTenantIdAndName(CatalogueLevel.OTHER.getLevel(), tenantId, engineCatalogueType.getDesc());
                if (null != catalogue) {
                    logger.error("tenantId {} has init engine type {} catalogue ", tenantId, engineType);
                    continue;
                }
                BatchCatalogue functionManager = batchCatalogueDao.getByLevelAndTenantIdAndName(CatalogueLevel.ONE.getLevel(), tenantId, FUNCTION_MANAGER.get(0));
                if (null == functionManager) {
                    throw new RdosDefineException("函数管理未初始化");
                }

                BatchCatalogue sqlCatalogue = batchCatalogueDao.getByLevelAndTenantIdAndName(CatalogueLevel.SECOND.getLevel(), tenantId, engineCatalogueType.getDesc());
                if (Objects.isNull(sqlCatalogue)) {
                    BatchCatalogue addEngineCatalogue = new BatchCatalogue();
                    addEngineCatalogue.setEngineType(engineType);
                    addEngineCatalogue.setNodeName(engineCatalogueType.getDesc());
                    addEngineCatalogue.setLevel(CatalogueLevel.SECOND.getLevel());
                    addEngineCatalogue.setNodePid(functionManager.getId());
                    addEngineCatalogue.setTenantId(tenantId);
                    addEngineCatalogue.setCreateUserId(userId);
                    addEngineCatalogue.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());

                    //添加spark libra impala function 这一层
                    addOrUpdate(addEngineCatalogue);
                    //初始化下一层目录
                    this.initEngineCatalogue(tenantId, userId, engineCatalogueType.getDesc(), addEngineCatalogue);
                }
            }
        }

    }

    private List<Dict> initCatalogueDictLevelByEngineType(List<Integer> supportEngineType) {
        List<Dict> dictByType = dictService.getDictByType(DictType.BATCH_CATALOGUE_L1.getValue());
        //根据引擎类型初始化对应的函数管理目录
        if (CollectionUtils.isNotEmpty(supportEngineType)) {
            if (!supportEngineType.contains(MultiEngineType.HADOOP.getType())) {
                dictByType = dictByType.stream()
                        .filter(dict -> !dict.getDictNameZH().equals(EngineCatalogueType.SPARK.getDesc()))
                        .collect(Collectors.toList());
            }
            return dictByType;
        } else {
            return dictByType.stream().filter(dict -> NO_ENGINE_CATALOGUE.contains(dict.getDictName())).collect(Collectors.toList());
        }
    }


    /**
     *  初始化函数相关的二级菜单
     * @param tenantId
     * @param userId
     * @param name
     * @param sc1
     */
    private void initEngineCatalogue(Long tenantId, Long userId, String name, BatchCatalogue sc1) {
        //一级菜单初始化的时候  函数管理的一级菜单为引擎 原有的一级菜单 系统函数 自定义函数 挂在引擎下 作为二级菜单
        if (isNeedFunction(name)) {
            //自定义函数
            List<Dict> batchFunction = dictService.getDictByType(DictType.BATCH_FUNCTION.getValue());
            if (CollectionUtils.isNotEmpty(batchFunction)) {
                for (Dict func : batchFunction) {
                    if (func.getDictNameEN().equals(CatalogueType.CUSTOM_FUNCTION.getType()) &&
                            sc1.getEngineType() == MultiEngineType.GREENPLUM.getType()) {
                        continue;
                    }
                    //需要 系统函数 自定义函数 挂在当前目录下
                    BatchCatalogue sc2 = new BatchCatalogue();
                    sc2.setNodeName(func.getDictNameZH());
                    sc2.setLevel(CatalogueLevel.SECOND.getLevel());
                    sc2.setNodePid(sc1.getId());
                    sc2.setTenantId(tenantId);
                    sc2.setEngineType(sc1.getEngineType());
                    sc2.setCreateUserId(userId);
                    sc2.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
                    addOrUpdate(sc2);
                }
            }
        }
    }

    /**
     * 校验是否初始化Spark SQL函数
     * @param name
     * @return
     */
    private boolean isNeedFunction(String name) {
        return EngineCatalogueType.SPARK.getDesc().equalsIgnoreCase(name);
    }


    /**
     * 初始化模板任务和相应的目录
     * @param sc1
     * @param tenantId
     * @param userId
     * @param supportEngineType
     * @return
     */
    private List<BatchCatalogue> initTemplateCatalogue(Catalogue sc1, Long tenantId, Long userId, List<Integer> supportEngineType) {
        int taskType = 0;
        if (supportEngineType.size() == 1 && supportEngineType.contains(MultiEngineType.LIBRA.getType())) {
            taskType = EJobType.GaussDB_SQL.getVal();
        } else {
            taskType = EJobType.SPARK_SQL.getVal();
        }
        List<BatchCatalogue> templateCatalogueList = new ArrayList<>(TemplateCatalogue.getValues(taskType).size());
        //在任务开发目录下添加默认的任务模版文件
        BatchCatalogue bc = new BatchCatalogue();
        bc.setLevel(CatalogueLevel.OTHER.getLevel());
        bc.setNodePid(sc1.getId());
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
            templateCatalogueList.add(bc);
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
                    List<BatchTaskGetComponentVersionResultVO> hadoopVersions = batchTaskService.getComponentVersionByTaskType(tenantId, taskType);
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
        return templateCatalogueList;
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


    /**
     * 条件查询目录
     * @param isGetFile
     * @param nodePid
     * @param catalogueType
     * @param userId
     * @param tenantId
     * @param taskType
     * @param parentId
     * @return
     */
    public CatalogueVO getCatalogue(Boolean isGetFile, Long nodePid, String catalogueType, Long userId, Long tenantId, Integer taskType, Long parentId) {

        //根目录
        CatalogueVO rootCatalugue = new CatalogueVO();
        if (nodePid == 0) {
            List<CatalogueVO> catalogues = getCatalogueOne(tenantId);
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
            List<CatalogueVO> children = build(subTasks, rootCatalugue, tenantId, userId);
            rootCatalugue.setChildren(children);
            rootCatalugue.setType("flow");
            rootCatalugue.setTaskType(EJobType.WORK_FLOW.getVal());
            BatchTask task = batchTaskService.getOne(nodePid);
            ReadWriteLockVO readWriteLockVO = readWriteLockService.getDetail(
                    tenantId, nodePid,
                    ReadWriteLockType.BATCH_TASK, userId,
                    task.getModifyUserId(),
                    task.getGmtModified());
            rootCatalugue.setReadWriteLockVO(readWriteLockVO);
            rootCatalugue.setVersion(readWriteLockVO.getVersion());
        } else {
            rootCatalugue.setId(nodePid);
            rootCatalugue.setCatalogueType(catalogueType);
            rootCatalugue = getChildNode(rootCatalugue, isGetFile, userId, tenantId);
        }

        return rootCatalugue;
    }

    /**
     * 更新目录（移动和重命名）
     */
    public void updateCatalogue(BatchCatalogueVO catalogueInput, Long userId) {

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
        BatchCatalogue byLevelAndPIdAndTenantIdAndName = batchCatalogueDao.getBeanByTenantIdAndNameAndParentId(catalogue.getTenantId(),updateCatalogue.getNodeName(), updateCatalogue.getNodePid());
        if (byLevelAndPIdAndTenantIdAndName != null && (!byLevelAndPIdAndTenantIdAndName.getId().equals(catalogue.getId()))){
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
        List<BatchTask> taskList = batchTaskService.listBatchTaskByNodePid(catalogueInput.getTenantId(), catalogue.getId());
        List<BatchResource> resourceList = batchResourceService.listByPidAndTenantId(catalogueInput.getTenantId(), catalogue.getId());

        if (taskList.size() > 0 || resourceList.size() > 0) {
            throw new RdosDefineException(ErrorCode.CATALOGUE_NO_EMPTY);
        }

        //判断文件夹下子目录
        if (CollectionUtils.isNotEmpty(batchCatalogueDao.listByPidAndTenantId(catalogue.getId(), 0L))) {
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
        if (catalogue.getCatalogueType().equals(RdosBatchCatalogueTypeEnum.TENANT.getType())) {
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

    public List<CatalogueVO> getCatalogueOne(Long tenantId) {
        List<BatchCatalogue> zeroCatalogues = batchCatalogueDao.listByLevelAndTenantId(0, tenantId);
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

            List<BatchCatalogue> oneCatalogues = batchCatalogueDao.listByPidAndTenantId(zeroCatalogue.getId(), 0L);
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

    private List<CatalogueVO> build(List<BatchTask> taskList, CatalogueVO currentCatalogueVO, Long tenantId, Long userId) {
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
                childTask.setCreateUser(userService.getUserName(task.getCreateUserId()));
                if (task.getTaskType().equals(EJobType.PYTHON.getVal())) {
                    JSONObject exeArgs = JSONObject.parseObject(task.getExeArgs());
                    childTask.setOperateModel(exeArgs.getInteger("operateModel"));
                    childTask.setLearningType(0);
                    childTask.setPythonVersion(exeArgs.getInteger("--python-version"));
                }

                ReadWriteLockVO readWriteLockVO = readWriteLockService.getDetail(
                        tenantId, task.getId(),
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
     * @param tenantId        项目id
     * @param isGetFile
     * @param userId
     * @return
     * @author jiangbo、toutian
     */
    private CatalogueVO getChildNode(CatalogueVO currentCatalogueVO, Boolean isGetFile, Long userId, Long tenantId) {
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


        Map<Long, String> userNames = Maps.newHashMap();
        //获取目录下的资源或任务列表
        if (isGetFile) {
            List<CatalogueVO> files = new ArrayList<>();
            if (currentCatalogueVO.getCatalogueType().equals(CatalogueType.TASK_DEVELOP.getType())) {
                Map<Long, List<CatalogueVO>> flowChildren = Maps.newHashMap();
                List<BatchTask> taskList = batchTaskService.catalogueListBatchTaskByNodePid(tenantId, currentCatalogueVO.getId());
                taskList.sort(Comparator.comparing(BatchTask::getName));
                if (CollectionUtils.isNotEmpty(taskList)) {

                    List<Long> taskIds = taskList.stream().map(BatchTask::getId).collect(Collectors.toList());
                    Map<Long, ReadWriteLockVO> vos = getReadWriteLockVOMap(0L, taskIds, userId, userNames);

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
                List<BatchFunction> functionList = batchFunctionService.listByNodePidAndTenantId(tenantId, currentCatalogueVO.getId());
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
                List<BatchResource> resourceList = batchResourceService.listByPidAndTenantId(tenantId, currentCatalogueVO.getId());
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
        List<BatchCatalogue> childCatalogues = this.getChildCataloguesByType(currentCatalogueVO.getId(), currentCatalogueVO.getCatalogueType(), tenantId);
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


    /**
     * 如果是libraSQL 或者是sparkSQl下的function  需要替换child 的catalogueType
     * @param catalogue
     * @param cv
     * @param currentCatalogueVO
     */
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


    /**
     * 获取目录下任务的锁信息
     * @param tenantId
     * @param taskIds
     * @param userId
     * @param names
     * @return
     */
    private Map<Long, ReadWriteLockVO> getReadWriteLockVOMap(Long tenantId, List<Long> taskIds, Long userId, Map<Long, String> names) {
        Map<Long, ReadWriteLockVO> vos = Maps.newHashMap();
        //一次查询800条
        int num = taskIds.size() % 800 == 0 ? taskIds.size() / 800 : taskIds.size() / 800 + 1;
        for (int i = 0; i < num; i++) {
            int begin = i * 800;
            int end = (i + 1) * 800;
            if (i == num - 1) {
                end = taskIds.size();
            }
            vos.putAll(readWriteLockService.getLocks(tenantId, ReadWriteLockType.BATCH_TASK, taskIds.subList(begin, end), userId, names));
        }
        return vos;
    }


    /**
     * 设置用户名称
     * @param names
     * @param userId
     * @return
     */
    private String getUserNameInMemory(Map<Long, String> names, Long userId) {
        if (names.containsKey(userId)) {
            return names.get(userId);
        } else {
            String name = userService.getUserName(userId);
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
            Map<Boolean, List<BatchCatalogue>> listMap = childCatalogues.stream().collect(Collectors.groupingBy(c -> {
                List<String> values = TemplateCatalogue.getValues();
                if (values.contains(c.getNodeName())) {
                    return true;
                }
                return false;
            }, Collectors.toList()));

            List<BatchCatalogue> batchCatalogueTop5 = new ArrayList<>(listMap.get(Boolean.TRUE));
            batchCatalogueTop5.sort(Comparator.comparing(BatchCatalogue::getId));
            if (CollectionUtils.isNotEmpty(listMap.get(Boolean.FALSE))) {
                List<BatchCatalogue> selfCatalogue = listMap.get(Boolean.FALSE);
                selfCatalogue.sort(Comparator.comparing(BatchCatalogue::getNodeName));
                batchCatalogueTop5.addAll(selfCatalogue);
            }
            return batchCatalogueTop5;

        } else {
            childCatalogues.sort(Comparator.comparing(BatchCatalogue::getNodeName));
            return childCatalogues;
        }
    }


    /**
     * 根据类型获取目录信息
     * @param catalogueId
     * @param catalogueType
     * @return
     */
    private BatchCatalogue getBatchCatalogueByType(Long catalogueId, String catalogueType) {
        return batchCatalogueDao.getOne(catalogueId);
    }


    /**
     * 根据目录类型，获取目录的子目录信息
     * @param catalogueId
     * @param catalogueType
     * @param tenantId
     * @param tenantId
     * @return
     */
    private List<BatchCatalogue> getChildCataloguesByType(Long catalogueId, String catalogueType, Long tenantId) {
        List<BatchCatalogue> childCatalogues = batchCatalogueDao.listByPidAndTenantId(catalogueId, tenantId);
        this.replaceSystemFunction(catalogueId, catalogueType, childCatalogues);
        return childCatalogues;
    }


    /**
     * 根据目录类型查询对应的函数根目录
     * @param catalogueId
     * @param catalogueType
     * @param childCatalogues
     */
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
                    childCatalogue.setId(systemFuncCatalogue.getId());
                }
            }
        }
    }


    /**
     * 新增路径
     *
     * @param pathList 路径列表
     * @param rootName 如 数据开发、资源管理等
     * @param tenantId
     * @return
     */
    public Long createCataloguePath(List<String> pathList, String rootName, Long tenantId) {
        BatchCatalogue zeroCatalogue = batchCatalogueDao.getByLevelAndTenantIdAndName(1, tenantId, rootName);
        Long nodePid = zeroCatalogue.getId();
        if (CollectionUtils.isNotEmpty(pathList)) {
            for (String nodeName : pathList) {
                BatchCatalogue catalogue = batchCatalogueDao.getByPidAndName(tenantId, nodePid, nodeName);
                if (catalogue == null) {
                    catalogue = new BatchCatalogue();
                    catalogue.setNodeName(nodeName);
                    catalogue.setNodePid(nodePid);
                    catalogue.setTenantId(zeroCatalogue.getTenantId());
                    CatalogueVO catalogueVO = addCatalogue(catalogue);
                    nodePid = catalogueVO.getId();
                } else {
                    nodePid = catalogue.getId();
                }
            }
        }
        return nodePid;
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
     * 根据 目录Id 查询目录信息
     * @param nodePid
     * @return
     */
    public BatchCatalogue getOne(Long nodePid) {
        return batchCatalogueDao.getOne(nodePid);
    }
}