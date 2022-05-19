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

package com.dtstack.taier.develop.service.develop.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.taier.common.enums.CatalogueLevel;
import com.dtstack.taier.common.enums.CatalogueType;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.DictType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EngineCatalogueType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.BatchCatalogue;
import com.dtstack.taier.dao.domain.BatchFunction;
import com.dtstack.taier.dao.domain.BatchResource;
import com.dtstack.taier.dao.domain.Dict;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.mapper.DevelopCatalogueMapper;
import com.dtstack.taier.develop.dto.devlop.BatchCatalogueVO;
import com.dtstack.taier.develop.dto.devlop.CatalogueVO;
import com.dtstack.taier.develop.enums.develop.RdosBatchCatalogueTypeEnum;
import com.dtstack.taier.develop.service.console.ClusterTenantService;
import com.dtstack.taier.develop.service.user.UserService;
import com.dtstack.taier.scheduler.service.ScheduleDictService;
import com.dtstack.taier.scheduler.vo.ComponentVO;
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
    private DevelopCatalogueMapper developCatalogueMapper;

    @Autowired
    private BatchResourceService batchResourceService;

    @Autowired
    private BatchFunctionService batchFunctionService;

    @Autowired
    private ScheduleDictService dictService;

    @Autowired
    private UserService userService;

    @Autowired
    public BatchTaskService batchTaskService;

    @Autowired
    private ClusterTenantService clusterTenantService;


    private static final String FUNCTION_MANAGER_NAME = "函数管理";

    private static final Long DEFAULT_NODE_PID = 0L;

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
        BatchCatalogue dbCatalogue = developCatalogueMapper.selectOne(Wrappers.lambdaQuery(BatchCatalogue.class)
                .eq(BatchCatalogue::getTenantId, catalogue.getTenantId())
                .eq(BatchCatalogue::getNodePid, catalogue.getNodePid())
                .eq(BatchCatalogue::getNodeName, catalogue.getNodeName())
                .last("limit 1"));
        if (dbCatalogue != null) {
            throw new RdosDefineException(ErrorCode.CATALOGUE_EXISTS);
        }

        // 校验当前父级直接一层的子目录或者任务的个数总数不可超过SUB_AMOUNTS_LIMIT(2000)
        Integer subAmountsByNodePid = developCatalogueMapper.selectCount(
                Wrappers.lambdaQuery(BatchCatalogue.class)
                        .eq(BatchCatalogue::getNodePid, catalogue.getNodePid())
                        .eq(BatchCatalogue::getTenantId, catalogue.getTenantId()));
        if (subAmountsByNodePid >= SUB_AMOUNTS_LIMIT) {
            throw new RdosDefineException(ErrorCode.SUBDIRECTORY_OR_FILE_AMOUNT_RESTRICTIONS);
        }

        int parentCatalogueLevel = catalogue.getNodePid() == 0L ? 0 : this.isOverLevelLimit(catalogue.getNodePid());

        catalogue.setLevel(parentCatalogueLevel + 1);
        catalogue.setCreateUserId(catalogue.getCreateUserId());
        catalogue.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        catalogue.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));

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
            LambdaUpdateWrapper<BatchCatalogue> batchCatalogueLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            batchCatalogueLambdaUpdateWrapper.eq(BatchCatalogue::getIsDeleted,Deleted.NORMAL.getStatus()).eq(BatchCatalogue::getId,batchCatalogue.getId());
            developCatalogueMapper.update(batchCatalogue,batchCatalogueLambdaUpdateWrapper);
        } else {
            developCatalogueMapper.insert(batchCatalogue);
        }
        return batchCatalogue;
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
        List<Dict> zeroBatchCatalogueDictList = dictService.listByDictType(DictType.DATA_DEVELOP_CATALOGUE);
        List<Integer> componentTypes = componentVOS.stream().map(ComponentVO::getComponentTypeCode).collect(Collectors.toList());
        //根据控制台配置的组件信息，获取需要初始化的 1 级目录，任务开发、SparkSQL、资源管理 等
        List<Dict> oneBatchCatalogueDictList = this.initCatalogueDictLevelByEngineType(componentTypes);

        Map<String, Set<String>> oneCatalogueValueAndNameMapping = oneBatchCatalogueDictList.stream()
                .collect(Collectors.groupingBy(Dict::getDictValue, Collectors.mapping(Dict::getDictDesc, Collectors.toSet())));
        for (Dict zeroDict : zeroBatchCatalogueDictList) {
            //初始化 0 级目录
            BatchCatalogue zeroBatchCatalogue = new BatchCatalogue();
            zeroBatchCatalogue.setNodeName(zeroDict.getDictDesc());
            zeroBatchCatalogue.setNodePid(DEFAULT_NODE_PID);
            zeroBatchCatalogue.setOrderVal(zeroDict.getSort());
            zeroBatchCatalogue.setLevel(CatalogueLevel.ONE.getLevel());
            zeroBatchCatalogue.setTenantId(tenantId);
            zeroBatchCatalogue.setCreateUserId(userId);
            zeroBatchCatalogue.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
            zeroBatchCatalogue = addOrUpdate(zeroBatchCatalogue);
            if (CollectionUtils.isNotEmpty(oneCatalogueValueAndNameMapping.get(zeroDict.getDictValue()))) {
                for (String oneCatalogueName : oneCatalogueValueAndNameMapping.get(zeroDict.getDictValue())) {
                    //初始化 1 级目录
                    BatchCatalogue oneBatchCatalogue = new BatchCatalogue();
                    oneBatchCatalogue.setNodeName(oneCatalogueName);
                    oneBatchCatalogue.setLevel(CatalogueLevel.SECOND.getLevel());
                    oneBatchCatalogue.setNodePid(zeroBatchCatalogue.getId());
                    oneBatchCatalogue.setTenantId(tenantId);
                    oneBatchCatalogue.setCreateUserId(userId);
                    oneBatchCatalogue.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
                    addOrUpdate(oneBatchCatalogue);
                    this.initEngineCatalogue(tenantId, userId, oneCatalogueName, oneBatchCatalogue);
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
        List<Dict> dictByType = dictService.listByDictType(DictType.DATA_DEVELOP_CATALOGUE_L1);
        //根据组件类型初始化对应的函数管理目录
        if (CollectionUtils.isNotEmpty(componentType)) {
            //如果没有选择SparkThrift组件，则不初始化目录
            if (!componentType.contains(EComponentType.SPARK_THRIFT.getTypeCode())) {
                dictByType = dictByType.stream()
                        .filter(dict -> !dict.getDictDesc().equals(EngineCatalogueType.SPARK.getDesc()))
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
            List<Dict> batchFunctionDictList = dictService.listByDictType(DictType.DATA_DEVELOP_FUNCTION);
            if (CollectionUtils.isNotEmpty(batchFunctionDictList)) {
                for (Dict functionDict : batchFunctionDictList) {
                    //需要 系统函数、自定义函数 挂在当前目录下
                    BatchCatalogue twoBatchCatalogue = new BatchCatalogue();
                    twoBatchCatalogue.setNodeName(functionDict.getDictDesc());
                    twoBatchCatalogue.setLevel(CatalogueLevel.SECOND.getLevel());
                    twoBatchCatalogue.setNodePid(oneBatchCatalogue.getId());
                    twoBatchCatalogue.setTenantId(tenantId);
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
        return EngineCatalogueType.SPARK.getDesc().equalsIgnoreCase(name) || EngineCatalogueType.FLINK.getDesc().equalsIgnoreCase(name);
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
        BatchCatalogue catalogue = developCatalogueMapper.selectById(currentId);
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
        beforeGetCatalogue(tenantId);
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
     * 目录获取前置处理
     *
     * @param tenantId
     */
    public void beforeGetCatalogue(Long tenantId) {
        if (Objects.isNull(tenantId)) {
            throw new RdosDefineException(ErrorCode.TENANT_ID_NOT_NULL);
        }
        Long clusterId = clusterTenantService.getClusterIdByTenantId(tenantId);
        if (Objects.isNull(clusterId)) {
            throw new RdosDefineException(ErrorCode.CLUSTER_NOT_CONFIG);
        }
    }

    /**
     * 更新目录（移动和重命
     */
    public void updateCatalogue(BatchCatalogueVO catalogueInput) {

        BatchCatalogue catalogue = developCatalogueMapper.selectById(catalogueInput.getId());
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
        BatchCatalogue byLevelAndPIdAndTenantIdAndName = developCatalogueMapper.selectOne(Wrappers.lambdaQuery(BatchCatalogue.class)
                .eq(BatchCatalogue::getTenantId, catalogue.getTenantId())
                .eq(BatchCatalogue::getNodeName, updateCatalogue.getNodeName())
                .eq(BatchCatalogue::getNodePid, updateCatalogue.getNodePid())
                .last("limit 1"));
        if (byLevelAndPIdAndTenantIdAndName != null && (!byLevelAndPIdAndTenantIdAndName.getId().equals(catalogue.getId()))) {
            throw new RdosDefineException(ErrorCode.FILE_NAME_REPETITION);
        }
        updateCatalogue.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        addOrUpdate(updateCatalogue);

    }


    /**
     * 删除目录
     */
    public void deleteCatalogue(BatchCatalogue catalogueInput) {

        BatchCatalogue catalogue = developCatalogueMapper.selectById(catalogueInput.getId());

        catalogueOneNotUpdate(catalogue);

        if (catalogue == null || catalogue.getIsDeleted() == 1) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_CATALOGUE);
        }

        //判断文件夹下任务
        List<Task> taskList = batchTaskService.listBatchTaskByNodePid(catalogueInput.getTenantId(), catalogue.getId());
        List<BatchResource> resourceList = batchResourceService.listByPidAndTenantId(catalogueInput.getTenantId(), catalogue.getId());

        if (taskList.size() > 0 || resourceList.size() > 0) {
            throw new RdosDefineException(ErrorCode.CATALOGUE_NO_EMPTY);
        }

        //判断文件夹下子目录
        List<BatchCatalogue> batchCatalogues = developCatalogueMapper.selectList(Wrappers.lambdaQuery(BatchCatalogue.class)
                .eq(BatchCatalogue::getTenantId, catalogueInput.getTenantId())
                .eq(BatchCatalogue::getNodePid, catalogue.getId())
                .orderByDesc(BatchCatalogue::getGmtCreate));
        if (CollectionUtils.isNotEmpty(batchCatalogues)) {
            throw new RdosDefineException(ErrorCode.CATALOGUE_NO_EMPTY);
        }

        catalogue.setIsDeleted(Deleted.DELETED.getStatus());
        catalogue.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        developCatalogueMapper.deleteById(catalogue.getId());
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
        BatchCatalogue parentCatalogue = developCatalogueMapper.selectById(nodePid);
        return parentCatalogue.getLevel();
    }

    /**
     * 获取 租户 下的 0 级目录极其子目录
     *
     * @param tenantId
     * @return
     */
    public List<CatalogueVO> getCatalogueOne(Long tenantId) {
        //查询 0 级目录
        List<BatchCatalogue> zeroCatalogues = developCatalogueMapper.selectList(Wrappers.lambdaQuery(BatchCatalogue.class)
                .eq(BatchCatalogue::getTenantId, tenantId)
                .eq(BatchCatalogue::getLevel, 0)
                .orderByAsc(BatchCatalogue::getOrderVal));
        //从字典表中查询出初始化的 0 级目录
        List<Dict> zeroCatalogueDictList = dictService.listByDictType(DictType.DATA_DEVELOP_CATALOGUE);
        //从字典表中查询出初始化的 1 级目录
        List<Dict> oneCatalogueDictList = dictService.listByDictType(DictType.DATA_DEVELOP_CATALOGUE_L1);

        // 0 级目录的中文和英文名称
        Map<String, String> zeroCatalogueType = zeroCatalogueDictList.stream().collect(Collectors.toMap(Dict::getDictDesc, Dict::getDictName, (key1, key2) -> key1));
        // 1 级目录的中文和英文名称
        Map<String, String> oneCatalogueType = oneCatalogueDictList.stream().collect(Collectors.toMap(Dict::getDictDesc, Dict::getDictName, (key1, key2) -> key1));

        List<CatalogueVO> zeroCatalogueVOList = new ArrayList<>(zeroCatalogues.size());
        for (BatchCatalogue zeroCatalogue : zeroCatalogues) {
            CatalogueVO zeroCatalogueVO = CatalogueVO.toVO(zeroCatalogue);
            zeroCatalogueVO.setCatalogueType(zeroCatalogueType.get(zeroCatalogue.getNodeName()));
            zeroCatalogueVO.setType(FILE_TYPE_FOLDER);
            zeroCatalogueVOList.add(zeroCatalogueVO);

            //查询一级目录下的子目录
            List<BatchCatalogue> oneChildCatalogues = developCatalogueMapper.selectList(Wrappers.lambdaQuery(BatchCatalogue.class)
                    .eq(BatchCatalogue::getTenantId, tenantId)
                    .eq(BatchCatalogue::getNodePid, zeroCatalogue.getId())
                    .orderByDesc(BatchCatalogue::getGmtCreate));
            if (FUNCTION_MANAGER_NAME.equals(zeroCatalogue.getNodeName())) {
                //如果是函数目录，默认添加上系统函数目录
                BatchCatalogue systemFuncCatalogue = developCatalogueMapper.selectOne(Wrappers.lambdaQuery(BatchCatalogue.class)
                        .eq(BatchCatalogue::getNodePid, EngineCatalogueType.SPARK.getType())
                        .eq(BatchCatalogue::getLevel, 1)
                        .eq(BatchCatalogue::getTenantId, -1)
                        .last("limit 1"));
                if (systemFuncCatalogue != null) {
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
        BatchCatalogue currentCatalogue = developCatalogueMapper.selectById(currentCatalogueVO.getId());
        if (currentCatalogue == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_CATALOGUE);
        }

        currentCatalogueVO.setTenantId(currentCatalogue.getTenantId());
        currentCatalogueVO.setName(currentCatalogue.getNodeName());
        currentCatalogueVO.setLevel(currentCatalogue.getLevel());
        currentCatalogueVO.setParentId(currentCatalogue.getNodePid());
        currentCatalogueVO.setType(FILE_TYPE_FOLDER);

        //获取目录下的资源或任务列表
        if (isGetFile) {
            //目录下的文件信息
            List<CatalogueVO> catalogueChildFileList = Lists.newArrayList();
            //用户id 和 名称映射
            Map<Long, String> userIdAndNameMap = Maps.newHashMap();

            //任务目录
            if (CatalogueType.TASK_DEVELOP.getType().equals(currentCatalogueVO.getCatalogueType())) {
                List<Task> taskList = batchTaskService.catalogueListBatchTaskByNodePid(tenantId, currentCatalogueVO.getId());
                taskList.sort(Comparator.comparing(Task::getName));
                if (CollectionUtils.isNotEmpty(taskList)) {
                    //遍历目录下的所有任务
                    for (Task task : taskList) {
                        CatalogueVO childCatalogueTask = new CatalogueVO();
                        BeanUtils.copyProperties(task, childCatalogueTask);
                        childCatalogueTask.setType("file");
                        childCatalogueTask.setLevel(currentCatalogueVO.getLevel() + 1);
                        childCatalogueTask.setParentId(currentCatalogueVO.getId());
                        childCatalogueTask.setCreateUser(getUserNameInMemory(userIdAndNameMap, task.getCreateUserId()));
                        catalogueChildFileList.add(childCatalogueTask);
                    }
                }
            } else if (FUNCTION_CATALOGUE_TYPE.contains(currentCatalogueVO.getCatalogueType())) {
                //处理函数目录
                List<BatchFunction> functionList = batchFunctionService.listByNodePidAndTenantId(currentCatalogueVO.getTenantId(), currentCatalogueVO.getId());
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
        childCatalogues.sort(Comparator.comparing(BatchCatalogue::getNodeName));
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
        if (CatalogueType.SPARKSQL_FUNCTION.getType().equals(currentCatalogueVO.getCatalogueType()) || CatalogueType.FLINKSQL_FUNCTION.getType().equals(currentCatalogueVO.getCatalogueType())) {
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
            if (EngineCatalogueType.FLINK.getDesc().equals(catalogue.getNodeName())) {
                cv.setCatalogueType(CatalogueType.FLINKSQL_FUNCTION.getType());
            }
        }

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
     * 根据目录类型，获取目录的子目录信息
     * @param catalogueId
     * @param catalogueType
     * @param tenantId
     * @param tenantId
     * @return
     */
    private List<BatchCatalogue> getChildCataloguesByType(Long catalogueId, String catalogueType, Long tenantId) {
        List<BatchCatalogue> childCatalogues = developCatalogueMapper.selectList(Wrappers.lambdaQuery(BatchCatalogue.class)
                .eq(BatchCatalogue::getTenantId, tenantId)
                .eq(BatchCatalogue::getNodePid, catalogueId)
                .orderByDesc(BatchCatalogue::getGmtCreate));
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
            replaceSystemFunction(catalogueId, catalogueType, childCatalogues, "系统函数");
        } else if (CatalogueType.FLINKSQL_FUNCTION.getType().equals(catalogueType)) {
            replaceSystemFunction(catalogueId, catalogueType, childCatalogues, "Flink系统函数");
        }
    }


    private void replaceSystemFunction(Long catalogueId, String catalogueType, List<BatchCatalogue> childCatalogues, String catalogName) {
        BatchCatalogue one = developCatalogueMapper.selectById(catalogueId);
        EngineCatalogueType systemEngineType = EngineCatalogueType.getByeName(one == null ? null : one.getNodeName());
        //需要将系统函数替换对应 引擎的函数模板
        BatchCatalogue systemFuncCatalogue = developCatalogueMapper.selectOne(Wrappers.lambdaQuery(BatchCatalogue.class)
                .eq(BatchCatalogue::getNodePid, systemEngineType.getType())
                .eq(BatchCatalogue::getLevel, 1)
                .eq(BatchCatalogue::getNodeName, catalogName)
                .eq(BatchCatalogue::getTenantId, -1)
                .last("limit 1"));
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
        return developCatalogueMapper.selectById(nodePid);
    }

    /**
     * 根据名称和父目录Id查询
     *
     * @param tenantId 租户ID
     * @param nodePid  父目录ID
     * @param name     名称
     * @return
     */
    public BatchCatalogue getByPidAndName(Long tenantId, Long nodePid, String name) {
        return developCatalogueMapper.selectOne(Wrappers.lambdaQuery(BatchCatalogue.class)
                .eq(BatchCatalogue::getTenantId, tenantId)
                .eq(BatchCatalogue::getNodePid, nodePid)
                .eq(BatchCatalogue::getNodeName, name)
                .last("limit 1"));
    }

}