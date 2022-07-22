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
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.EngineCatalogueType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.DevelopCatalogue;
import com.dtstack.taier.dao.domain.DevelopFunction;
import com.dtstack.taier.dao.domain.DevelopResource;
import com.dtstack.taier.dao.domain.Dict;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.mapper.DevelopCatalogueMapper;
import com.dtstack.taier.develop.dto.devlop.CatalogueVO;
import com.dtstack.taier.develop.dto.devlop.DevelopCatalogueVO;
import com.dtstack.taier.develop.enums.develop.RdosBatchCatalogueTypeEnum;
import com.dtstack.taier.develop.service.console.ClusterTenantService;
import com.dtstack.taier.scheduler.service.ScheduleDictService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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

@Service
public class DevelopCatalogueService {

    @Autowired
    private DevelopCatalogueMapper developCatalogueMapper;

    @Autowired
    private DevelopResourceService developResourceService;

    @Autowired
    private DevelopFunctionService developFunctionService;

    @Autowired
    private ScheduleDictService dictService;

    @Autowired
    public DevelopTaskService developTaskService;

    @Autowired
    private ClusterTenantService clusterTenantService;


    private static final String FUNCTION_MANAGER_NAME = "函数管理";

    private static final Long DEFAULT_NODE_PID = 0L;

    private static Integer SUB_AMOUNTS_LIMIT = 2000;

    private final static String FILE_TYPE_FOLDER = "folder";


    /**
     * 获取任务或资源的上级目录，用于前端搜索定位
     *
     * @return
     */
    public CatalogueVO getLocation(Long tenantId, String catalogueType, Long id, String name) {
        // 获取当前资源的父级类目id，一直到根目录为止
        List<Long> grandCatalogueIds = grandCatalogueIds(tenantId, catalogueType, id, name);
        //获取根目录
        DevelopCatalogue rootCatalogue = getRootCatalogueByType(tenantId, catalogueType);

        CatalogueVO root = CatalogueVO.toVO(rootCatalogue);
        root.setType("folder");
        getTree(root, grandCatalogueIds, tenantId, catalogueType);
        return root;
    }

    public List<Long> grandCatalogueIds(Long tenantId, String catalogueType, Long id, String name) {
        // 获取当前资源的父级类目id，一直到根目录为止
        List<Long> grandCatalogueIds = new ArrayList<>();
        if (CatalogueType.TASK_DEVELOP.getType().equals(catalogueType)) {
            grandCatalogueIds = grandCatalogueTaskIds(tenantId, id, name);
        } else if (CatalogueType.RESOURCE_MANAGER.getType().equals(catalogueType)) {
            grandCatalogueIds = grandCatalogueResourceIds(id);
        }else {
            throw new DtCenterDefException("不支持其他");
        }
        return grandCatalogueIds;
    }


    /**
     * 获取任务和资源的根目录
     *
     * @param tenantId
     * @param catalogueType
     * @return
     */
    private DevelopCatalogue getRootCatalogueByType(Long tenantId, String catalogueType) {
        DevelopCatalogue rootCatalogue = null;
        if (CatalogueType.TASK_DEVELOP.getType().equals(catalogueType)) {
            //获取任务管理的根目录
            rootCatalogue = developCatalogueMapper.selectOne(Wrappers.lambdaQuery(DevelopCatalogue.class)
                    .eq(DevelopCatalogue::getTenantId, tenantId)
                    .eq(DevelopCatalogue::getNodePid, 0)
                    .eq(DevelopCatalogue::getNodeName, "任务管理")
                    .last("limit 1"));
        } else if (CatalogueType.RESOURCE_MANAGER.getType().equals(catalogueType)) {
            //获取资源管理的根目录
            rootCatalogue = developCatalogueMapper.selectOne(Wrappers.lambdaQuery(DevelopCatalogue.class)
                    .eq(DevelopCatalogue::getTenantId, tenantId)
                    .eq(DevelopCatalogue::getNodePid, 0)
                    .eq(DevelopCatalogue::getNodeName, "资源管理")
                    .last("limit 1"));
        }
        if (rootCatalogue == null) {
            throw new DtCenterDefException("根目录找不到");
        }
        return rootCatalogue;
    }


    private void getTree(CatalogueVO root, List<Long> grandCatalogueIds, Long tenantId, String catalogueType) {
        if (StringUtils.isBlank(root.getCatalogueType())) {
            root.setCatalogueType(catalogueType);
        }

        if (grandCatalogueIds.contains(root.getId())) {
            if (CollectionUtils.isEmpty(root.getChildren()) && "folder".equals(root.getType())) {
                getChildNode(root, true, tenantId);
            }
        } else {
            getChildNode(root, false, tenantId);
        }

        if (CollectionUtils.isNotEmpty(root.getChildren())) {
            for (CatalogueVO vo : root.getChildren()) {
                if ("folder".equals(vo.getType())) {
                    getTree(vo, grandCatalogueIds, tenantId, catalogueType);
                }
            }
        }
    }

    /**
     * 通过任务ID，获取当前任务的父级类目id，一直到根目录为止
     *
     * @param tenantId
     * @param taskId
     * @param name
     * @return
     */
    private List<Long> grandCatalogueTaskIds(Long tenantId, Long taskId, String name) {
        List<Long> grandCatalogueIds = new ArrayList<>();
        if (taskId != null) {
            Task task = developTaskService.getOne(taskId);
            if (task != null) {
                getGrandCatalogueIds(task.getNodePid(), grandCatalogueIds);
            }
        } else if (StringUtils.isNotEmpty(name)) {
            List<Task> tasks = developTaskService.getByLikeName(name, tenantId);
            if (CollectionUtils.isNotEmpty(tasks)) {
                for (Task task : tasks) {
                    getGrandCatalogueIds(task.getNodePid(), grandCatalogueIds);
                }
            }
        }
        return grandCatalogueIds;
    }


    /**
     * 通过资源ID，获取当前资源的父级类目id，一直到根目录为止
     *
     * @param resourceId
     * @return
     */
    public List<Long> grandCatalogueResourceIds(Long resourceId) {
        List<Long> grandCatalogueIds = new ArrayList<>();
        if (resourceId != null) {
            DevelopResource developResource = developResourceService.getResource(resourceId);
            if (developResource != null) {
                getGrandCatalogueIds(developResource.getNodePid(), grandCatalogueIds);
            }
        }
        return grandCatalogueIds;
    }

    /**
     * 新增 and 修改目录
     *
     * @param catalogue
     * @return
     */
    public CatalogueVO addCatalogue(DevelopCatalogue catalogue) {
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
        DevelopCatalogue dbCatalogue = developCatalogueMapper.selectOne(Wrappers.lambdaQuery(DevelopCatalogue.class)
                .eq(DevelopCatalogue::getTenantId, catalogue.getTenantId())
                .eq(DevelopCatalogue::getNodePid, catalogue.getNodePid())
                .eq(DevelopCatalogue::getNodeName, catalogue.getNodeName())
                .last("limit 1"));
        if (dbCatalogue != null) {
            throw new RdosDefineException(ErrorCode.CATALOGUE_EXISTS);
        }

        // 校验当前父级直接一层的子目录或者任务的个数总数不可超过SUB_AMOUNTS_LIMIT(2000)
        Integer subAmountsByNodePid = developCatalogueMapper.selectCount(
                Wrappers.lambdaQuery(DevelopCatalogue.class)
                        .eq(DevelopCatalogue::getNodePid, catalogue.getNodePid())
                        .eq(DevelopCatalogue::getTenantId, catalogue.getTenantId()));
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
        cv.setType(DevelopCatalogueService.FILE_TYPE_FOLDER);
        return cv;
    }


    /**
     * 新增 and 修改目录
     *
     * @param developCatalogue
     * @return
     */
    private DevelopCatalogue addOrUpdate(DevelopCatalogue developCatalogue) {
        developCatalogue.setGmtModified(new Timestamp(System.currentTimeMillis()));
        if (developCatalogue.getId() != null && developCatalogue.getId() > 0) {
            LambdaUpdateWrapper<DevelopCatalogue> developCatalogueLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            developCatalogueLambdaUpdateWrapper.eq(DevelopCatalogue::getIsDeleted, Deleted.NORMAL.getStatus()).eq(DevelopCatalogue::getId, developCatalogue.getId());
            developCatalogueMapper.update(developCatalogue, developCatalogueLambdaUpdateWrapper);
        } else {
            developCatalogue.setGmtCreate(new Timestamp(System.currentTimeMillis()));
            developCatalogueMapper.insert(developCatalogue);
        }
        return developCatalogue;
    }

    /**
     * 绑定租户时，初始化目录信息
     *
     * @param tenantId
     * @param userId
     */
    @Transactional(rollbackFor = Exception.class)
    public void initCatalogue(Long tenantId, Long userId) {
        List<Dict> catalogueLevel1List = dictService.listByDictType(DictType.DATA_DEVELOP_CATALOGUE);
        List<Dict> catalogueLevel2List = dictService.listByDictType(DictType.DATA_DEVELOP_CATALOGUE_L1);
        Map<String, Set<String>> oneCatalogueValueAndNameMapping = catalogueLevel2List.stream()
                .collect(Collectors.groupingBy(Dict::getDictValue, Collectors.mapping(Dict::getDictDesc, Collectors.toSet())));
        for (Dict zeroDict : catalogueLevel1List) {
            //初始化 0 级目录
            DevelopCatalogue zeroBatchCatalogue = new DevelopCatalogue();
            zeroBatchCatalogue.setNodeName(zeroDict.getDictDesc());
            zeroBatchCatalogue.setNodePid(DEFAULT_NODE_PID);
            zeroBatchCatalogue.setOrderVal(zeroDict.getSort());
            zeroBatchCatalogue.setLevel(CatalogueLevel.ONE.getLevel());
            zeroBatchCatalogue.setTenantId(tenantId);
            zeroBatchCatalogue.setCreateUserId(userId);
            zeroBatchCatalogue.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
            addOrUpdate(zeroBatchCatalogue);
            if (CollectionUtils.isNotEmpty(oneCatalogueValueAndNameMapping.get(zeroDict.getDictValue()))) {
                for (String oneCatalogueName : oneCatalogueValueAndNameMapping.get(zeroDict.getDictValue())) {
                    //初始化 1 级目录
                    DevelopCatalogue oneBatchCatalogue = new DevelopCatalogue();
                    oneBatchCatalogue.setNodeName(oneCatalogueName);
                    oneBatchCatalogue.setLevel(CatalogueLevel.SECOND.getLevel());
                    oneBatchCatalogue.setNodePid(zeroBatchCatalogue.getId());
                    oneBatchCatalogue.setTenantId(tenantId);
                    oneBatchCatalogue.setCreateUserId(userId);
                    oneBatchCatalogue.setCatalogueType(RdosBatchCatalogueTypeEnum.NORAML.getType());
                    addOrUpdate(oneBatchCatalogue);
                }
            }
        }
    }

    /**
     * 根据当前节点递归查询所有父节点列表，包含当前节点
     *
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
     *
     * @param currentId
     * @param ids
     * @return 父节点列表
     */
    private void getGrandCatalogueId(Long currentId, List<Long> ids) {
        DevelopCatalogue catalogue = developCatalogueMapper.selectById(currentId);
        if (catalogue != null && catalogue.getLevel() >= 1) {
            ids.add(catalogue.getNodePid());
            getGrandCatalogueId(catalogue.getNodePid(), ids);
        }
    }

    /**
     * 条件查询目录
     *
     * @param isGetFile
     * @param nodePid
     * @param catalogueType
     * @param tenantId
     * @return
     */
    public CatalogueVO getCatalogue(Boolean isGetFile, Long nodePid, String catalogueType, Long tenantId) {
        beforeGetCatalogue(tenantId);
        CatalogueVO rootCatalogue = new CatalogueVO();
        //0表示根目录
        if (nodePid == 0) {
            List<CatalogueVO> catalogues = getCatalogueOne(tenantId);
            rootCatalogue.setChildren(catalogues);
        } else {
            rootCatalogue.setId(nodePid);
            rootCatalogue.setCatalogueType(catalogueType);
            rootCatalogue = getChildNode(rootCatalogue, isGetFile, tenantId);
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
     * 更新目录（移动和重命名)
     *
     * @param catalogueInput
     */
    public void updateCatalogue(DevelopCatalogueVO catalogueInput) {
        DevelopCatalogue catalogue = developCatalogueMapper.selectById(catalogueInput.getId());
        catalogueOneNotUpdate(catalogue);
        if (catalogue.getIsDeleted() == 1) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_CATALOGUE);
        }

        if (canNotMoveCatalogue(catalogueInput.getId(), catalogueInput.getNodePid())) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_MOVE_CATALOGUE);
        }
        DevelopCatalogue updateCatalogue = new DevelopCatalogue();
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
        } else {
            updateCatalogue.setNodePid(catalogue.getNodePid());
        }
        //判断移动的目录下 有没有相同名称的文件夹
        DevelopCatalogue byLevelAndPIdAndTenantIdAndName = developCatalogueMapper.selectOne(Wrappers.lambdaQuery(DevelopCatalogue.class)
                .eq(DevelopCatalogue::getTenantId, catalogue.getTenantId())
                .eq(DevelopCatalogue::getNodeName, updateCatalogue.getNodeName())
                .eq(DevelopCatalogue::getNodePid, updateCatalogue.getNodePid())
                .last("limit 1"));
        if (byLevelAndPIdAndTenantIdAndName != null && (!byLevelAndPIdAndTenantIdAndName.getId().equals(catalogue.getId()))) {
            throw new RdosDefineException(ErrorCode.FILE_NAME_REPETITION);
        }
        updateCatalogue.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        addOrUpdate(updateCatalogue);

    }

    /**
     * 删除目录
     *
     * @param catalogueInput
     */
    public void deleteCatalogue(DevelopCatalogue catalogueInput) {
        DevelopCatalogue catalogue = developCatalogueMapper.selectById(catalogueInput.getId());
        if (Objects.isNull(catalogue) || catalogue.getIsDeleted() == 1) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_CATALOGUE);
        }

        catalogueOneNotUpdate(catalogue);

        //判断文件夹下任务
        List<Task> taskList = developTaskService.listBatchTaskByNodePid(catalogueInput.getTenantId(), catalogue.getId());
        List<DevelopResource> resourceList = developResourceService.listByPidAndTenantId(catalogueInput.getTenantId(), catalogue.getId());

        if (taskList.size() > 0 || resourceList.size() > 0) {
            throw new RdosDefineException(ErrorCode.CATALOGUE_NO_EMPTY);
        }

        //判断文件夹下子目录
        List<DevelopCatalogue> developCatalogues = developCatalogueMapper.selectList(Wrappers.lambdaQuery(DevelopCatalogue.class)
                .eq(DevelopCatalogue::getTenantId, catalogueInput.getTenantId())
                .eq(DevelopCatalogue::getNodePid, catalogue.getId())
                .orderByDesc(DevelopCatalogue::getGmtCreate));
        if (CollectionUtils.isNotEmpty(developCatalogues)) {
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
     * @author
     */
    private void catalogueOneNotUpdate(DevelopCatalogue catalogue) {
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
     * @author
     */
    private int isOverLevelLimit(long nodePid) {
        DevelopCatalogue parentCatalogue = developCatalogueMapper.selectById(nodePid);
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
        List<DevelopCatalogue> zeroCatalogues = developCatalogueMapper.selectList(Wrappers.lambdaQuery(DevelopCatalogue.class)
                .eq(DevelopCatalogue::getTenantId, tenantId)
                .eq(DevelopCatalogue::getLevel, 0)
                .orderByAsc(DevelopCatalogue::getOrderVal));
        //从字典表中查询出初始化的 0 级目录
        List<Dict> zeroCatalogueDictList = dictService.listByDictType(DictType.DATA_DEVELOP_CATALOGUE);
        //从字典表中查询出初始化的 1 级目录
        List<Dict> oneCatalogueDictList = dictService.listByDictType(DictType.DATA_DEVELOP_CATALOGUE_L1);

        // 0 级目录的中文和英文名称
        Map<String, String> zeroCatalogueType = zeroCatalogueDictList.stream().collect(Collectors.toMap(Dict::getDictDesc, Dict::getDictName, (key1, key2) -> key1));
        // 1 级目录的中文和英文名称
        Map<String, String> oneCatalogueType = oneCatalogueDictList.stream().collect(Collectors.toMap(Dict::getDictDesc, Dict::getDictName, (key1, key2) -> key1));

        List<CatalogueVO> zeroCatalogueVOList = new ArrayList<>(zeroCatalogues.size());
        for (DevelopCatalogue zeroCatalogue : zeroCatalogues) {
            CatalogueVO zeroCatalogueVO = CatalogueVO.toVO(zeroCatalogue);
            zeroCatalogueVO.setCatalogueType(zeroCatalogueType.get(zeroCatalogue.getNodeName()));
            zeroCatalogueVO.setType(FILE_TYPE_FOLDER);
            zeroCatalogueVOList.add(zeroCatalogueVO);

            //查询一级目录下的子目录
            List<DevelopCatalogue> oneChildCatalogues = developCatalogueMapper.selectList(Wrappers.lambdaQuery(DevelopCatalogue.class)
                    .eq(DevelopCatalogue::getTenantId, tenantId)
                    .eq(DevelopCatalogue::getNodePid, zeroCatalogue.getId())
                    .orderByDesc(DevelopCatalogue::getGmtCreate));
            if (FUNCTION_MANAGER_NAME.equals(zeroCatalogue.getNodeName())) {
                //如果是函数目录，默认添加上系统函数目录
                DevelopCatalogue systemFuncCatalogue = developCatalogueMapper.selectOne(Wrappers.lambdaQuery(DevelopCatalogue.class)
                        .eq(DevelopCatalogue::getNodePid, EngineCatalogueType.SPARK.getType())
                        .eq(DevelopCatalogue::getLevel, 1)
                        .eq(DevelopCatalogue::getTenantId, -1)
                        .last("limit 1"));
                if (systemFuncCatalogue != null) {
                    oneChildCatalogues.add(systemFuncCatalogue);
                }
            }
            List<CatalogueVO> oneChildCatalogueVOList = new ArrayList<>(oneChildCatalogues.size());
            for (DevelopCatalogue oneChildCatalogue : oneChildCatalogues) {
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
     * @param tenantId  租户id
     * @param isGetFile
     * @return
     * @author
     */
    private CatalogueVO getChildNode(CatalogueVO currentCatalogueVO, Boolean isGetFile, Long tenantId) {
        DevelopCatalogue currentCatalogue = developCatalogueMapper.selectById(currentCatalogueVO.getId());
        if (Objects.isNull(currentCatalogue)) {
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
            //工作流子节点
            Map<Long, List<CatalogueVO>> flowChildren = Maps.newHashMap();
            //任务目录
            if (CatalogueType.TASK_DEVELOP.getType().equals(currentCatalogueVO.getCatalogueType())) {
                List<Task> taskList = developTaskService.catalogueListBatchTaskByNodePid(tenantId, currentCatalogueVO.getId());
                taskList.sort(Comparator.comparing(Task::getName));
                if (CollectionUtils.isNotEmpty(taskList)) {
                    //遍历目录下的所有任务
                    for (Task task : taskList) {
                        CatalogueVO childCatalogueTask = new CatalogueVO();
                        BeanUtils.copyProperties(task, childCatalogueTask);
                        if (task.getTaskType().intValue() == EScheduleJobType.WORK_FLOW.getVal()) {
                            childCatalogueTask.setType("flow");
                        } else {
                            childCatalogueTask.setType("file");
                        }
                        childCatalogueTask.setLevel(currentCatalogueVO.getLevel() + 1);

                        childCatalogueTask.setParentId(currentCatalogueVO.getId());
                        if (task.getFlowId() > 0L) {
                            List<CatalogueVO> temp = flowChildren.get(task.getFlowId());
                            if (CollectionUtils.isEmpty(temp)) {
                                temp = Lists.newArrayList();
                                temp.add(childCatalogueTask);
                                flowChildren.put(task.getFlowId(), temp);
                            } else {
                                flowChildren.get(task.getFlowId()).add(childCatalogueTask);
                            }
                        } else {
                            catalogueChildFileList.add(childCatalogueTask);
                        }
                    }

                    //封装工作流子任务
                    for (CatalogueVO catalogueVO : catalogueChildFileList) {
                        List<CatalogueVO> children = flowChildren.get(catalogueVO.getId());
                        if (CollectionUtils.isNotEmpty(children)){
                            catalogueVO.setChildren(children);
                        }
                    }
                }
            } else if (CatalogueType.FUNCTION_MANAGER.getType().equals(currentCatalogueVO.getCatalogueType())) {
                //处理函数目录
                List<DevelopFunction> functionList = developFunctionService.listByNodePidAndTenantId(currentCatalogueVO.getTenantId(), currentCatalogueVO.getId());
                if (CollectionUtils.isNotEmpty(functionList)) {
                    functionList.sort(Comparator.comparing(DevelopFunction::getName));
                    for (DevelopFunction function : functionList) {
                        CatalogueVO child = new CatalogueVO();
                        BeanUtils.copyProperties(function, child);
                        child.setLevel(currentCatalogueVO.getLevel() + 1);
                        child.setType("file");
                        child.setParentId(function.getNodePid());
                        catalogueChildFileList.add(child);
                    }
                }
            } else if (CatalogueType.RESOURCE_MANAGER.getType().equals(currentCatalogueVO.getCatalogueType())) {
                //处理资源目录
                List<DevelopResource> resourceList = developResourceService.listByPidAndTenantId(tenantId, currentCatalogueVO.getId());
                resourceList.sort(Comparator.comparing(DevelopResource::getResourceName));
                if (CollectionUtils.isNotEmpty(resourceList)) {
                    for (DevelopResource resource : resourceList) {
                        CatalogueVO childResource = new CatalogueVO();
                        BeanUtils.copyProperties(resource, childResource);
                        childResource.setName(resource.getResourceName());
                        childResource.setType("file");
                        childResource.setLevel(currentCatalogueVO.getLevel() + 1);
                        childResource.setParentId(currentCatalogueVO.getId());
                        catalogueChildFileList.add(childResource);
                    }
                }
            }
            currentCatalogueVO.setChildren(catalogueChildFileList);
        }

        //获取目录下的子目录
        List<DevelopCatalogue> childCatalogues = this.getChildCataloguesByNodePid(currentCatalogueVO.getId());
        childCatalogues.sort(Comparator.comparing(DevelopCatalogue::getNodeName));
        List<CatalogueVO> children = new ArrayList<>();
        for (DevelopCatalogue catalogue : childCatalogues) {
            CatalogueVO cv = CatalogueVO.toVO(catalogue);
            cv.setType(FILE_TYPE_FOLDER);
            children.add(cv);
        }

        if (Objects.isNull(currentCatalogueVO.getChildren())) {
            currentCatalogueVO.setChildren(children);
        } else {
            currentCatalogueVO.getChildren().addAll(0, children);
        }

        return currentCatalogueVO;
    }

    /**
     * 根据目录类型，获取目录的子目录信息
     *
     * @param catalogueId
     * @return
     */
    private List<DevelopCatalogue> getChildCataloguesByNodePid(Long catalogueId) {
        List<DevelopCatalogue> childCatalogues = developCatalogueMapper.selectList(Wrappers.lambdaQuery(DevelopCatalogue.class)
                .eq(DevelopCatalogue::getNodePid, catalogueId)
                .orderByDesc(DevelopCatalogue::getGmtCreate));
        return childCatalogues;
    }

    /**
     * 判断是否可以移动到当前目录
     *
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
     *
     * @param nodeId
     * @return
     */
    public DevelopCatalogue getOne(Long nodeId) {
        return developCatalogueMapper.selectById(nodeId);
    }

    /**
     * 根据 目录Id 查询目录信息
     *
     * @param nodeId
     * @return
     */
    public DevelopCatalogue getOneWithError(Long nodeId) {
        DevelopCatalogue catalogue = getOne(nodeId);
        if (Objects.isNull(catalogue)) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_CATALOGUE);
        }
        return catalogue;
    }

    /**
     * 根据名称和父目录Id查询
     *
     * @param tenantId 租户ID
     * @param nodePid  父目录ID
     * @param name     名称
     * @return
     */
    public DevelopCatalogue getByPidAndName(Long tenantId, Long nodePid, String name) {
        return developCatalogueMapper.selectOne(Wrappers.lambdaQuery(DevelopCatalogue.class)
                .eq(DevelopCatalogue::getTenantId, tenantId)
                .eq(DevelopCatalogue::getNodePid, nodePid)
                .eq(DevelopCatalogue::getNodeName, name)
                .last("limit 1"));
    }

}