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
import com.dtstack.batch.service.user.UserService;
import com.dtstack.batch.vo.CatalogueVO;
import com.dtstack.batch.vo.ReadWriteLockVO;
import com.dtstack.batch.vo.TaskResourceParam;
import com.dtstack.batch.web.task.vo.result.BatchTaskGetComponentVersionResultVO;
import com.dtstack.engine.common.enums.CatalogueLevel;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.Deleted;
import com.dtstack.engine.common.enums.DictType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.common.enums.ReadWriteLockType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.domain.BatchTask;
import com.dtstack.engine.master.vo.ComponentVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import java.util.stream.Collectors;


/**
 * @author sishu.yss、toutian
 */

@Service
public class BatchCatalogueService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchCatalogueService.class);

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

    @Autowired
    private ReadWriteLockService readWriteLockService;


    private static final String FUNCTION_MANAGER_NAME = "函数管理";

    private static final Long DEFAULT_NODE_PID = 0L;

    private static final String TASK_DEVELOPE = "任务开发";

    private static final String PARAM_COMMAND = "yyyyMMdd-1";

    private static final String PARAM_NAME = "bdp.system.bizdate";

    private static final String TYPE4SYSTEM = "0";

    private static Integer SUB_AMOUNTS_LIMIT = 2000;

    private final static String FILE_TYPE_FOLDER = "folder";

    /**
     * 前端是根据type 区分函数目录下的类型的，所以以下皆是 函数管理下的目录
     */
    private static List<String> FUNCTION_CATALOGUE_TYPE = Lists.newArrayList(CatalogueType.SYSTEM_FUNCTION.getType(), CatalogueType.CUSTOM_FUNCTION.getType(), CatalogueType.PROCEDURE_FUNCTION.getType());

    /**
     * 如果没有选择对接引擎 就要默认初始化下列目录
     */
    private static Set<String> NO_ENGINE_CATALOGUE = Sets.newHashSet(CatalogueType.TASK_DEVELOP.getType(), CatalogueType.RESOURCE_MANAGER.getType(), CatalogueType.FUNCTION_MANAGER.getType());

    /**
     * 新增 and 修改目录
     * @param catalogue
     * @return
     */
    public CatalogueVO addCatalogue(BatchCatalogue catalogue) {
        if (Objects.isNull(catalogue)) {
            throw new RdosDefineException(ErrorCode.CATALOGUE_NOT_EMPTY);
        }
        if (StringUtils.isBlank(catalogue.getNodeName())) {
            throw new RdosDefineException(ErrorCode.CATALOGUE_NAME_NOT_EMPTY);
        }
        catalogue.setNodeName(catalogue.getNodeName().trim());
        // 校验文件夹中是否含有空格
        if (catalogue.getNodeName().contains(" ")) {
            throw new RdosDefineException(ErrorCode.CATALOGUE_NAME_CANNOT_CONTAIN_SPACES);
        }
        BatchCatalogue dbCatalogue = batchCatalogueDao.getByPidAndName(catalogue.getTenantId(), catalogue.getNodePid(), catalogue.getNodeName());
        if (dbCatalogue != null) {
            throw new RdosDefineException(ErrorCode.CATALOGUE_EXISTS);
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
        if (batchCatalogue.getId() != null && batchCatalogue.getId() > 0) {
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
        }
        return 0;
    }


    /**
     * 绑定租户时，初始化目录信息
     * @param tenantId
     * @param userId
     * @param componentVOS 根据控制台配置的组件信息，初始化相应的目录
     */
    @Transactional(rollbackFor = Exception.class)
    public void initCatalogue(Long tenantId, Long userId, List<ComponentVO> componentVOS) {
        //离线各模块的 0 级目录，任务管理、函数管理、资源管理
        List<Dict> zeroBatchCatalogueDictList = dictService.getDictByType(DictType.BATCH_CATALOGUE.getValue());
        List<Integer> componentTypes = componentVOS.stream().map(ComponentVO::getComponentTypeCode).collect(Collectors.toList());
        //根据控制台配置的组件信息，获取需要初始化的 1 级目录，任务开发、SparkSQL、资源管理 等
        List<Dict> oneBatchCatalogueDictList = this.initCatalogueDictLevelByEngineType(componentTypes);

        Map<Integer, Set<String>> oneCatalogueValueAndNameMapping = oneBatchCatalogueDictList.stream()
                .collect(Collectors.groupingBy(Dict::getDictValue, Collectors.mapping(Dict::getDictNameZH, Collectors.toSet())));
        for (Dict zeroDict : zeroBatchCatalogueDictList) {
            //初始化 0 级目录
            BatchCatalogue zeroBatchCatalogue = new BatchCatalogue();
            zeroBatchCatalogue.setNodeName(zeroDict.getDictNameZH());
            zeroBatchCatalogue.setNodePid(DEFAULT_NODE_PID);
            zeroBatchCatalogue.setOrderVal(zeroDict.getDictSort());
            zeroBatchCatalogue.setEngineType(0);
            zeroBatchCatalogue.setLevel(CatalogueLevel.ONE.getLevel());
            zeroBatchCatalogue.setTenantId(tenantId);
            zeroBatchCatalogue.setCreateUserId(userId);
            zeroBatchCatalogue.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
            zeroBatchCatalogue = addOrUpdate(zeroBatchCatalogue);
            if (CollectionUtils.isNotEmpty(oneCatalogueValueAndNameMapping.get(zeroDict.getDictValue()))) {
                for (String oneCatalogueName : oneCatalogueValueAndNameMapping.get(zeroDict.getDictValue())) {
                    //初始化 1 级目录
                    BatchCatalogue oneBatchCatalogue = new BatchCatalogue();
                    oneBatchCatalogue.setEngineType(this.getMultiTypeByEngine(oneCatalogueName));
                    oneBatchCatalogue.setNodeName(oneCatalogueName);
                    oneBatchCatalogue.setLevel(CatalogueLevel.SECOND.getLevel());
                    oneBatchCatalogue.setNodePid(zeroBatchCatalogue.getId());
                    oneBatchCatalogue.setTenantId(tenantId);
                    oneBatchCatalogue.setCreateUserId(userId);
                    oneBatchCatalogue.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
                    addOrUpdate(oneBatchCatalogue);
                    if (TASK_DEVELOPE.equals(oneCatalogueName)) {
                        //初始化任务模版
                        this.initTemplateCatalogue(oneBatchCatalogue, tenantId, userId);
                    }
                    this.initEngineCatalogue(tenantId, userId, oneCatalogueName, oneBatchCatalogue);
                }
            }
        }
    }


    /**
     * 租户添加新的组件后，初始组件的目录
     *
     * @param tenantId
     * @param userId
     * @param componentTypeList
     */
    @Transactional(rollbackFor = Exception.class)
    public void initComponentCatalogue(Long tenantId, Long userId, List<Integer> componentTypeList) {
        if (CollectionUtils.isNotEmpty(componentTypeList)) {
            for (Integer componentType : componentTypeList) {
                BatchCatalogue catalogue = null;
                EngineCatalogueType engineCatalogueType = EngineCatalogueType.getByComponentType(componentType);
                if(engineCatalogueType == null){
                    continue;
                }
                //判断函数管理下的一级目录是否存在
                catalogue = batchCatalogueDao.getByLevelAndTenantIdAndName(CatalogueLevel.SECOND.getLevel(), tenantId, engineCatalogueType.getDesc());
                if (null != catalogue) {
                    continue;
                }
                BatchCatalogue functionManager = batchCatalogueDao.getByLevelAndTenantIdAndName(CatalogueLevel.ONE.getLevel(), tenantId, FUNCTION_MANAGER_NAME);
                if (null == functionManager) {
                    throw new RdosDefineException(ErrorCode.CATALOGUE_FUNCTION_MANAGE_UN_INIT);
                }

                //函数管理下的一级目录
                BatchCatalogue sqlCatalogue = batchCatalogueDao.getByLevelAndTenantIdAndName(CatalogueLevel.SECOND.getLevel(), tenantId, engineCatalogueType.getDesc());
                if (Objects.isNull(sqlCatalogue)) {
                    BatchCatalogue addEngineCatalogue = new BatchCatalogue();
                    addEngineCatalogue.setEngineType(componentType);
                    addEngineCatalogue.setNodeName(engineCatalogueType.getDesc());
                    addEngineCatalogue.setLevel(CatalogueLevel.SECOND.getLevel());
                    addEngineCatalogue.setNodePid(functionManager.getId());
                    addEngineCatalogue.setTenantId(tenantId);
                    addEngineCatalogue.setCreateUserId(userId);
                    addEngineCatalogue.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());

                    //添加spark 这一层
                    addOrUpdate(addEngineCatalogue);
                    //初始化下一层目录
                    this.initEngineCatalogue(tenantId, userId, engineCatalogueType.getDesc(), addEngineCatalogue);
                }
            }
        }

    }


    /**
     * 根据控制台配置的组件信息，获取需要初始化的 1 级目录，任务开发、SparkSQL、资源管理 等
     * @param componentType
     * @return
     */
    private List<Dict> initCatalogueDictLevelByEngineType(List<Integer> componentType) {
        List<Dict> dictByType = dictService.getDictByType(DictType.BATCH_CATALOGUE_L1.getValue());
        //根据组件类型初始化对应的函数管理目录
        if (CollectionUtils.isNotEmpty(componentType)) {
            //如果没有选择SparkThrift组件，则不初始化目录
            if (!componentType.contains(EComponentType.SPARK_THRIFT.getTypeCode())) {
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
     *  初始化函数相关的二级菜单，系统函数目录 和 自定义函数目录
     * @param tenantId
     * @param userId
     * @param name
     * @param oneBatchCatalogue
     */
    private void initEngineCatalogue(Long tenantId, Long userId, String name, BatchCatalogue oneBatchCatalogue) {
        //一级菜单初始化的时候  函数管理的一级菜单为引擎 原有的一级菜单 系统函数 自定义函数 挂在引擎下 作为二级菜单
        if (isNeedFunction(name)) {
            //离线需要初始化的函数目录
            List<Dict> batchFunctionDictList = dictService.getDictByType(DictType.BATCH_FUNCTION.getValue());
            if (CollectionUtils.isNotEmpty(batchFunctionDictList)) {
                for (Dict functionDict : batchFunctionDictList) {
                    //需要 系统函数、自定义函数 挂在当前目录下
                    BatchCatalogue twoBatchCatalogue = new BatchCatalogue();
                    twoBatchCatalogue.setNodeName(functionDict.getDictNameZH());
                    twoBatchCatalogue.setLevel(CatalogueLevel.SECOND.getLevel());
                    twoBatchCatalogue.setNodePid(oneBatchCatalogue.getId());
                    twoBatchCatalogue.setTenantId(tenantId);
                    twoBatchCatalogue.setEngineType(oneBatchCatalogue.getEngineType());
                    twoBatchCatalogue.setCreateUserId(userId);
                    twoBatchCatalogue.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
                    addOrUpdate(twoBatchCatalogue);
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
     * 初始化任务开发下的 模板目录 和 模板任务
     * @param oneCatalogue
     * @param tenantId
     * @param userId
     * @return
     */
    private List<BatchCatalogue> initTemplateCatalogue(Catalogue oneCatalogue, Long tenantId, Long userId) {
        List<BatchCatalogue> templateCatalogueList = new ArrayList<>(TemplateCatalogue.getValues().size());

        //需要初始化的模板任务所在的目录
        BatchCatalogue batchTempTaskCatalogue = new BatchCatalogue();
        batchTempTaskCatalogue.setLevel(CatalogueLevel.OTHER.getLevel());
        batchTempTaskCatalogue.setNodePid(oneCatalogue.getId());
        batchTempTaskCatalogue.setTenantId(tenantId);
        batchTempTaskCatalogue.setCreateUserId(userId);
        batchTempTaskCatalogue.setEngineType(oneCatalogue.getEngineType());

        List<TemplateCatalogue> templateTaskCatalogues = TemplateCatalogue.getValues();
        HashMap<String, Long> idsMap = new HashMap<>();
        for (TemplateCatalogue templateCatalogue : templateTaskCatalogues) {
            //相同目录只创建一次
            if (!idsMap.containsKey(templateCatalogue.getValue())) {
                batchTempTaskCatalogue.setNodeName(templateCatalogue.getValue());
                batchTempTaskCatalogue.setId(0L);
                batchTempTaskCatalogue.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
                addOrUpdate(batchTempTaskCatalogue);
                idsMap.put(templateCatalogue.getValue(), batchTempTaskCatalogue.getId());
            }
            templateCatalogueList.add(batchTempTaskCatalogue);

            try {
                String content = batchTaskTemplateService.getContentByType(EJobType.SPARK_SQL.getVal(), templateCatalogue.getType());
                //初始化任务
                TaskResourceParam batchTask = new TaskResourceParam();
                batchTask.setName(templateCatalogue.getFileName());
                batchTask.setTaskType(EJobType.SPARK_SQL.getVal());
                batchTask.setNodePid(idsMap.get(templateCatalogue.getValue()));
                batchTask.setComputeType(ComputeType.BATCH.getType());
                batchTask.setLockVersion(0);
                batchTask.setVersion(0);
                batchTask.setTenantId(tenantId);
                batchTask.setUserId(userId);
                batchTask.setCreateUserId(userId);
                batchTask.setModifyUserId(userId);

                if (StringUtils.isEmpty(content)) {
                    throw new RdosDefineException(ErrorCode.TEMPLATE_TASK_CONTENT_NOT_NULL);
                }
                batchTask.setSqlText(content);

                //添加init脚本中带有的任务参数
                List<Map> taskVariables = new ArrayList<>();
                Map<String, String> variable = new HashMap<>(3);
                variable.put("paramCommand", PARAM_COMMAND);
                variable.put("paramName", PARAM_NAME);
                variable.put("type", TYPE4SYSTEM);
                taskVariables.add(variable);
                batchTask.setTaskVariables(taskVariables);

                //SparkSQL任务支持多版本运行，添加默认的组件版本
                List<BatchTaskGetComponentVersionResultVO> hadoopVersions = batchTaskService.getComponentVersionByTaskType(tenantId, EJobType.SPARK_SQL.getVal());
                if (CollectionUtils.isNotEmpty(hadoopVersions)) {
                    batchTask.setComponentVersion(hadoopVersions.get(0).getComponentVersion());
                }
                batchTaskService.addOrUpdateTask(batchTask);
            } catch (Exception e) {
                LOGGER.error(ErrorCode.CATALOGUE_INIT_FAILED.getDescription(), e);
                throw new RdosDefineException(ErrorCode.CATALOGUE_INIT_FAILED);
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
     * @return
     */
    public CatalogueVO getCatalogue(Boolean isGetFile, Long nodePid, String catalogueType, Long userId, Long tenantId) {
        CatalogueVO rootCatalogue = new CatalogueVO();
        //0表示根目录
        if (nodePid == 0) {
            List<CatalogueVO> catalogues = getCatalogueOne(tenantId);
            rootCatalogue.setChildren(catalogues);
        } else {
            rootCatalogue.setId(nodePid);
            rootCatalogue.setCatalogueType(catalogueType);
            rootCatalogue = getChildNode(rootCatalogue, isGetFile, userId, tenantId);
        }

        return rootCatalogue;
    }

    /**
     * 更新目录（移动和重命名）
     */
    public void updateCatalogue(BatchCatalogueVO catalogueInput, Long userId) {

        BatchCatalogue catalogue = batchCatalogueDao.getOne(catalogueInput.getId());
        catalogueOneNotUpdate(catalogue);
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

        catalogueOneNotUpdate(catalogue);

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
        if (CollectionUtils.isNotEmpty(batchCatalogueDao.listByPidAndTenantId(catalogue.getId(), catalogueInput.getTenantId()))) {
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
    private void catalogueOneNotUpdate(BatchCatalogue catalogue) {
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

    /**
     * 获取 租户 下的 0 级目录极其子目录
     * @param tenantId
     * @return
     */
    public List<CatalogueVO> getCatalogueOne(Long tenantId) {
        //查询 0 级目录
        List<BatchCatalogue> zeroCatalogues = batchCatalogueDao.listByLevelAndTenantId(0, tenantId);
        //从字典表中查询出初始化的 0 级目录
        List<Dict> zeroCatalogueDictList = dictService.getDictByType(DictType.BATCH_CATALOGUE.getValue());
        //从字典表中查询出初始化的 1 级目录
        List<Dict> oneCatalogueDictList = dictService.getDictByType(DictType.BATCH_CATALOGUE_L1.getValue());

        // 0 级目录的中文和英文名称
        Map<String, String> zeroCatalogueType = zeroCatalogueDictList.stream().collect(Collectors.toMap(Dict::getDictNameZH, Dict::getDictNameEN, (key1, key2) -> key1));
        // 1 级目录的中文和英文名称
        Map<String, String> oneCatalogueType = oneCatalogueDictList.stream().collect(Collectors.toMap(Dict::getDictNameZH, Dict::getDictNameEN, (key1, key2) -> key1));

        List<CatalogueVO> zeroCatalogueVOList = new ArrayList<>(zeroCatalogues.size());
        for (BatchCatalogue zeroCatalogue : zeroCatalogues) {
            CatalogueVO zeroCatalogueVO = CatalogueVO.toVO(zeroCatalogue);
            zeroCatalogueVO.setCatalogueType(zeroCatalogueType.get(zeroCatalogue.getNodeName()));
            zeroCatalogueVO.setType(FILE_TYPE_FOLDER);
            zeroCatalogueVOList.add(zeroCatalogueVO);

            //查询一级目录下的子目录
            List<BatchCatalogue> oneChildCatalogues = batchCatalogueDao.listByPidAndTenantId(zeroCatalogue.getId(), tenantId);
            if (FUNCTION_MANAGER_NAME.equals(zeroCatalogue.getNodeName())) {
                //如果是函数目录，默认添加上系统函数目录
                BatchCatalogue systemFuncCatalogue = batchCatalogueDao.getSystemFunctionCatalogueOne(EngineCatalogueType.SPARK.getType());
                if (systemFuncCatalogue != null ) {
                    oneChildCatalogues.add(systemFuncCatalogue);
                }
            }
            List<CatalogueVO> oneChildCatalogueVOList = new ArrayList<>(oneChildCatalogues.size());
            for (BatchCatalogue oneChildCatalogue : oneChildCatalogues) {
                CatalogueVO oneChildCatalogueVO = CatalogueVO.toVO(oneChildCatalogue);
                if (EngineCatalogueType.SPARK.getDesc().equals(oneChildCatalogueVO.getName())) {
                    // spark  函数管理 不是目录
                    oneChildCatalogueVO.setType("catalogue");
                } else {
                    oneChildCatalogueVO.setType("folder");
                }
                oneChildCatalogueVO.setCatalogueType(oneCatalogueType.get(oneChildCatalogue.getNodeName()));
                oneChildCatalogueVOList.add(oneChildCatalogueVO);
            }
            zeroCatalogueVO.setChildren(oneChildCatalogueVOList);
        }
        return zeroCatalogueVOList;
    }


    /**
     * 获得当前节点的子节点信息，包括子孙文件夹和子孙文件
     *
     * @param tenantId   租户id
     * @param isGetFile
     * @param userId
     * @return
     * @author jiangbo、toutian
     */
    private CatalogueVO getChildNode(CatalogueVO currentCatalogueVO, Boolean isGetFile, Long userId, Long tenantId) {
        BatchCatalogue currentCatalogue = batchCatalogueDao.getOne(currentCatalogueVO.getId());
        if (currentCatalogue == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_CATALOGUE);
        }

        currentCatalogueVO.setName(currentCatalogue.getNodeName());
        currentCatalogueVO.setLevel(currentCatalogue.getLevel());
        currentCatalogueVO.setParentId(currentCatalogue.getNodePid());
        currentCatalogueVO.setType(FILE_TYPE_FOLDER);
        currentCatalogueVO.setEngineType(currentCatalogue.getEngineType());

        //获取目录下的资源或任务列表
        if (isGetFile) {
            //目录下的文件信息
            List<CatalogueVO> catalogueChildFileList = Lists.newArrayList();
            //用户id 和 名称映射
            Map<Long, String> userIdAndNameMap = Maps.newHashMap();

            //任务目录
            if (currentCatalogueVO.getCatalogueType().equals(CatalogueType.TASK_DEVELOP.getType())) {
                List<BatchTask> taskList = batchTaskService.catalogueListBatchTaskByNodePid(tenantId, currentCatalogueVO.getId());
                taskList.sort(Comparator.comparing(BatchTask::getName));
                if (CollectionUtils.isNotEmpty(taskList)) {
                    List<Long> taskIds = taskList.stream().map(BatchTask::getId).collect(Collectors.toList());
                    Map<Long, ReadWriteLockVO> readWriteLockIdAndVOMap = getReadWriteLockVOMap(tenantId, taskIds, userId, userIdAndNameMap);

                    //遍历目录下的所有任务
                    for (BatchTask task : taskList) {
                        CatalogueVO childCatalogueTask = new CatalogueVO();
                        BeanUtils.copyProperties(task, childCatalogueTask);
                        childCatalogueTask.setType("file");
                        childCatalogueTask.setLevel(currentCatalogueVO.getLevel() + 1);
                        childCatalogueTask.setParentId(currentCatalogueVO.getId());
                        childCatalogueTask.setCreateUser(getUserNameInMemory(userIdAndNameMap, task.getCreateUserId()));

                        //设置任务的读写锁信息
                        ReadWriteLockVO readWriteLockVO = readWriteLockIdAndVOMap.get(task.getId());
                        if (readWriteLockVO.getLastKeepLockUserName() == null) {
                            readWriteLockVO.setLastKeepLockUserName(getUserNameInMemory(userIdAndNameMap, task.getModifyUserId()));
                            readWriteLockVO.setGmtModified(task.getGmtModified());
                        }
                        childCatalogueTask.setReadWriteLockVO(readWriteLockVO);

                        catalogueChildFileList.add(childCatalogueTask);
                    }
                }
            } else if (FUNCTION_CATALOGUE_TYPE.contains(currentCatalogueVO.getCatalogueType())) {
                //处理函数目录
                List<BatchFunction> functionList = batchFunctionService.listByNodePidAndTenantId(tenantId, currentCatalogueVO.getId());
                if (CollectionUtils.isNotEmpty(functionList)) {
                    functionList.sort(Comparator.comparing(BatchFunction::getName));
                    for (BatchFunction function : functionList) {
                        CatalogueVO child = new CatalogueVO();
                        BeanUtils.copyProperties(function, child);
                        child.setLevel(currentCatalogueVO.getLevel() + 1);
                        child.setType("file");
                        child.setCreateUser(getUserNameInMemory(userIdAndNameMap, function.getCreateUserId()));
                        child.setParentId(function.getNodePid());
                        catalogueChildFileList.add(child);
                    }
                }
            } else if (CatalogueType.RESOURCE_MANAGER.getType().equals(currentCatalogueVO.getCatalogueType())) {
                //处理资源目录
                List<BatchResource> resourceList = batchResourceService.listByPidAndTenantId(tenantId, currentCatalogueVO.getId());
                resourceList.sort(Comparator.comparing(BatchResource::getResourceName));
                if (CollectionUtils.isNotEmpty(resourceList)) {
                    for (BatchResource resource : resourceList) {
                        CatalogueVO childResource = new CatalogueVO();
                        BeanUtils.copyProperties(resource, childResource);
                        childResource.setName(resource.getResourceName());
                        childResource.setType("file");
                        childResource.setLevel(currentCatalogueVO.getLevel() + 1);
                        childResource.setParentId(currentCatalogueVO.getId());
                        childResource.setCreateUser(getUserNameInMemory(userIdAndNameMap, resource.getCreateUserId()));
                        catalogueChildFileList.add(childResource);
                    }
                }
            }
            currentCatalogueVO.setChildren(catalogueChildFileList);
        }

        //获取目录下的子目录
        List<BatchCatalogue> childCatalogues = this.getChildCataloguesByType(currentCatalogueVO.getId(), currentCatalogueVO.getCatalogueType(), currentCatalogue.getTenantId());
        childCatalogues = keepInitCatalogueBeTop(childCatalogues, currentCatalogue, userId);
        List<CatalogueVO> children = new ArrayList<>();
        for (BatchCatalogue catalogue : childCatalogues) {
            CatalogueVO cv = CatalogueVO.toVO(catalogue);
            cv.setType(FILE_TYPE_FOLDER);
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
        if (CatalogueType.SPARKSQL_FUNCTION.getType().equals(currentCatalogueVO.getCatalogueType())) {
            if ("自定义函数".equals(catalogue.getNodeName())) {
                cv.setCatalogueType(CatalogueType.CUSTOM_FUNCTION.getType());
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
                List<TemplateCatalogue> templateCatalogueList = TemplateCatalogue.getValues();
                List<String> values = templateCatalogueList.stream().map(TemplateCatalogue::getValue).collect(Collectors.toList());
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
        if (CatalogueType.SPARKSQL_FUNCTION.getType().equals(catalogueType)) {
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