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
import com.dtstack.engine.domain.User;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.batch.dao.*;
import com.dtstack.batch.domain.*;
import com.dtstack.batch.dto.BatchResourceAddDTO;
import com.dtstack.batch.dto.BatchResourceDTO;
import com.dtstack.batch.engine.rdbms.common.HadoopConf;
import com.dtstack.batch.engine.rdbms.common.HdfsOperator;
import com.dtstack.batch.service.task.impl.BatchTaskResourceService;
import com.dtstack.batch.service.task.impl.BatchTaskService;
import com.dtstack.batch.vo.BatchResourceVO;
import com.dtstack.batch.vo.CatalogueVO;
import com.dtstack.batch.web.pager.PageQuery;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.engine.common.enums.Deleted;
import com.dtstack.engine.common.enums.ResourceType;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.master.impl.UserService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author sishu.yss
 */
@Slf4j
@Service
public class BatchResourceService {

    private static final Logger logger = LoggerFactory.getLogger(BatchResourceService.class);

    @Autowired
    private BatchResourceDao batchResourceDao;
    
    @Autowired
    private BatchFunctionResourceDao batchFunctionResourceDao;
    
    @Autowired
    private BatchCatalogueDao batchCatalogueDao;

    @Autowired
    private BatchTaskResourceService batchTaskResourceService;

    @Autowired
    private BatchTaskService batchTaskService;

    @Autowired
    private UserService userService;

    @Autowired
    EnvironmentContext environmentContext;

    /**
     * 添加资源
     */
    public CatalogueVO addResource(BatchResourceAddDTO batchResourceAddDTO) {
        final Long tenantId = batchResourceAddDTO.getTenantId();
        final Long userId = batchResourceAddDTO.getUserId();

        final String resourceName;
        Long resourceId = null;
        BatchResource resourceDB = null;
        Integer resourceType = null;
        if (batchResourceAddDTO.getId() != null && batchResourceAddDTO.getId() != 0L) {
            resourceId = batchResourceAddDTO.getId();
            resourceDB = this.batchResourceDao.getOne(resourceId);
            resourceName = resourceDB.getResourceName();
            resourceType = resourceDB.getResourceType();
        } else {
            if (StringUtils.isEmpty(batchResourceAddDTO.getResourceName())) {
                throw new RdosDefineException("需要设置参数 resourceName.", ErrorCode.INVALID_PARAMETERS);
            }
            resourceName = batchResourceAddDTO.getResourceName();
            resourceType =  batchResourceAddDTO.getResourceType() == null ? ResourceType.OTHER.getType() : batchResourceAddDTO.getResourceType();
        }

        String hdfsPath = uploadHDFSFileWithResource(tenantId, resourceName, batchResourceAddDTO.getOriginalFilename(), batchResourceAddDTO.getTmpPath());

        BatchResource batchResource = null;
        //重新上传资源
        if (resourceId != null) {
            batchResource = resourceDB;
            if (batchResource == null || batchResource.getIsDeleted().equals(Deleted.DELETED.getStatus())) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_RESOURCE);
            }
            batchResource.setResourceDesc(batchResourceAddDTO.getResourceDesc());
            batchResource.setOriginFileName(batchResourceAddDTO.getOriginalFilename());
            batchResource.setUrl(hdfsPath);
        } else {
            //判断是否已经存在相同的资源了
            this.batchTaskService.checkName(resourceName, CatalogueType.RESOURCE_MANAGER.name(), null, 1, tenantId);

            batchResourceAddDTO.setUrl(hdfsPath);
            batchResourceAddDTO.setCreateUserId(userId);
            try {
                batchResource = PublicUtil.objectToObject(batchResourceAddDTO, BatchResource.class);
                batchResource.setOriginFileName(batchResourceAddDTO.getOriginalFilename());
            } catch (IOException e) {
                throw new RdosDefineException(String.format("转化失败：%s", e.getMessage()));
            }
            batchResource.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
        }

        //resourceType 设置默认值
        resourceType = resourceType != null ? resourceType : ResourceType.OTHER.getType();

        batchResource.setResourceType(resourceType);
        batchResource.setModifyUserId(userId);
        batchResource.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        batchResource = this.addOrUpdate(batchResource);

        final BatchCatalogue catalogue = this.batchCatalogueDao.getOne(batchResource.getNodePid());
        final CatalogueVO catalogueVO = new CatalogueVO();
        catalogueVO.setId(batchResource.getId());
        catalogueVO.setName(batchResource.getResourceName());
        catalogueVO.setType("file");
        catalogueVO.setLevel(catalogue.getLevel() + 1);
        catalogueVO.setChildren(null);
        catalogueVO.setParentId(catalogue.getId());
        catalogueVO.setResourceType(resourceType);

        String username = userService.getUserName(catalogue.getCreateUserId());
        catalogueVO.setCreateUser(username);

        return catalogueVO;
    }


    /**
     * 新增资源组
     * @param id
     * @param resourceName
     * @param originFileName
     * @param url
     * @param resourceDesc
     * @param resourceType
     * @param nodePid
     * @param userId
     * @param tenantId
     * @return
     */
    public Long addResourceWithUrl(Long id, String resourceName, String originFileName, String url, String resourceDesc, Integer resourceType, Long nodePid, Long userId, Long tenantId) {
        Preconditions.checkNotNull(resourceName, "resourceName can not be null");
        Preconditions.checkNotNull(originFileName, "orginFileName can not be null");
        Preconditions.checkNotNull(url, "remoteUrl can not be null");
        Preconditions.checkNotNull(nodePid, "nodePid can not be null");

        BatchResource batchResource = this.buildResource(id, resourceName, originFileName, url, resourceDesc, resourceType, nodePid, userId, tenantId);
        batchResource = this.addOrUpdate(batchResource);
        return batchResource.getId();
    }


    /**
     * 根据参数，构建资源组对象
     * @param id
     * @param resourceName
     * @param originFileName
     * @param url
     * @param resourceDesc
     * @param resourceType
     * @param nodePid
     * @param userId
     * @param tenantId
     * @return
     */
    private BatchResource buildResource(Long id, String resourceName, String originFileName, String url, String resourceDesc, Integer resourceType, Long nodePid, Long userId, Long tenantId) {
        final BatchResource batchResource;
        //重新上传资源
        if (id != null) {
            batchResource = this.batchResourceDao.getOne(id);
            if (batchResource == null || batchResource.getIsDeleted().equals(Deleted.DELETED.getStatus())) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_RESOURCE);
            }
            batchResource.setResourceDesc(resourceDesc);
            batchResource.setOriginFileName(originFileName);
            batchResource.setUrl(url);
            batchResource.setNodePid(nodePid);
        } else {
            //判断是否已经存在相同的资源了
            this.batchTaskService.checkName(resourceName, CatalogueType.RESOURCE_MANAGER.name(), null, 1, tenantId);
            batchResource = new BatchResource();
            batchResource.setResourceName(resourceName);
            batchResource.setOriginFileName(originFileName);
            batchResource.setUrl(url);
            batchResource.setResourceDesc(resourceDesc);
            batchResource.setNodePid(nodePid);
            batchResource.setResourceType(resourceType);
            batchResource.setCreateUserId(userId);
            batchResource.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
            batchResource.setTenantId(tenantId);
        }

        batchResource.setResourceType(ResourceType.OTHER.getType());
        batchResource.setModifyUserId(userId);
        batchResource.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        return batchResource;
    }


    /**
     * 新增或修改
     * @param batchResource
     * @return
     */
    private BatchResource addOrUpdate(BatchResource batchResource) {
        if (batchResource.getId() > 0) {
            this.batchResourceDao.update(batchResource);
        } else {
            this.batchResourceDao.insert(batchResource);
        }

        return batchResource;
    }

    /**
     * 获取资源列表
     */
    public List<BatchResource> getResources(Long tenantId) {
        return this.batchResourceDao.listByTenantId(tenantId);
    }

    /**
     * 删除资源
     */
    public Long deleteResource(Long tenantId, Long resourceId) {
        final List<BatchTaskResource> taskResources = this.batchTaskResourceService.getUseableResources(resourceId);
        if (!CollectionUtils.isEmpty(taskResources)) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_DELETE_RESOURCE);
        }
        final List<BatchFunctionResource> functionResources = this.batchFunctionResourceDao.listByResourceId(resourceId);
        if (!CollectionUtils.isEmpty(functionResources)) {
        	throw new RdosDefineException(ErrorCode.CAN_NOT_DELETE_RESOURCE);
        }
        //删除资源在hdfs的实际存储文件
        final BatchResource resource = getResource(resourceId);
        try {
            HdfsOperator.checkAndDele(HadoopConf.getConfiguration(tenantId), HadoopConf.getHadoopKerberosConf(tenantId),resource.getUrl());
        } catch (final Exception e) {
            BatchResourceService.logger.error(" tenantId:{} taskId:{}  userId:{} fail delete resource from HDFS", e);
        }

        //删除资源记录
        batchResourceDao.deleteById(resourceId);
        return resourceId;
    }

    /**
     * 获取资源详情
     */
    public BatchResourceVO getResourceById(long resourceId) {
        BatchResource batchResource = this.getResource(resourceId);
        if (Objects.nonNull(batchResource)) {
            BatchResourceVO vo = BatchResourceVO.toVO(batchResource);
            vo.setCreateUser(userService.getById(batchResource.getCreateUserId()));
            //是否是该项目成员
            return vo;
        }
        return null;
    }

    /**
     * 修改资源名称
     */
    public BatchResource renameResource(Long userId, long resourceId, String name) {

        BatchResource sr = this.batchResourceDao.getOne(resourceId);
        if (sr != null && sr.getIsDeleted().intValue() == Deleted.DELETED.getStatus().intValue()) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_RESOURCE);
        }
        sr = new BatchResource();
        sr.setId(resourceId);
        sr.setResourceName(name);
        sr.setModifyUserId(userId);
        return this.addOrUpdate(sr);
    }


    /**
     * 获取离线上传的资源到HDFS上的路径
     * @param tenantId
     * @param fileName
     * @return
     */
    private String getBatchHdfsPath(Long tenantId, String fileName) {
        String hdfsURI = HadoopConf.getDefaultFs(tenantId);
        return hdfsURI + environmentContext.getHdfsBatchPath() + fileName;
    }


    /**
     * 根据资源ids 查询资源列表
     * @param resourceIdList
     * @return
     */
    public List<BatchResource> getResourceList(List<Long> resourceIdList) {
        return this.batchResourceDao.listByIds(resourceIdList);
    }

    /**
     * 根据资源id获取资源信息
     * @param resourceId
     * @return
     */
    public BatchResource getResource(long resourceId) {
        return this.batchResourceDao.getOne(resourceId);
    }

    /**
     * 资源分页查询
     * @param resourceDTO
     * @return
     */
    public PageResult<List<BatchResourceVO>> pageQuery(BatchResourceDTO resourceDTO){
        final PageQuery<BatchResourceDTO> query = new PageQuery<>(resourceDTO.getPageIndex(),resourceDTO.getPageSize(),"gmt_modified",resourceDTO.getSort());
        query.setModel(resourceDTO);

        final List<BatchResourceVO> resourceVOS = new ArrayList<>();
        final Integer count = this.batchResourceDao.generalCount(resourceDTO);
        if (count > 0){
            final List<BatchResource> resources = this.batchResourceDao.generalQuery(query);

            final List<Long> userIds = new ArrayList<>();
            resources.forEach(r -> {
                userIds.add(r.getCreateUserId());
                userIds.add(r.getModifyUserId());
            });

            final List<User> users = userService.listByIds(userIds);
            final Map<Long,User> idUserMap = new HashMap<>();
            users.forEach(u -> {
                idUserMap.put(u.getId(),u);
            });

            for (final BatchResource resource : resources) {
                final BatchResourceVO vo = BatchResourceVO.toVO(resource);
                vo.setCreateUser(idUserMap.get(vo.getCreateUserId()));
                vo.setModifyUser(idUserMap.get(vo.getModifyUserId()));
                resourceVOS.add(vo);
            }
        }

        return new PageResult<>(resourceVOS,count,query);
    }


    /**
     * 替换资源
     */
    public void replaceResource(BatchResourceAddDTO batchResourceAddDTO) {
        final long tenantId = batchResourceAddDTO.getTenantId();
        final long resourceId = batchResourceAddDTO.getId();

        final BatchResource resourceDb = this.batchResourceDao.getOne(resourceId);
        if (Objects.isNull(resourceDb)) {
            throw new RdosDefineException("替换字段不存在");
        }

        final String resourceName = resourceDb.getResourceName();

        String hdfsPath = uploadHDFSFileWithResource(tenantId, resourceName, batchResourceAddDTO.getOriginalFilename(), batchResourceAddDTO.getTmpPath());

        resourceDb.setUrl(hdfsPath);
        resourceDb.setOriginFileName(batchResourceAddDTO.getOriginalFilename());
        resourceDb.setResourceDesc(batchResourceAddDTO.getResourceDesc());
        resourceDb.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        batchResourceDao.update(resourceDb);

        final List<BatchFunctionResource> batchFunctionResources = this.batchFunctionResourceDao.listByFunctionResourceId(resourceDb.getId());
        if (CollectionUtils.isEmpty(batchFunctionResources)) {
            return;
        }

    }


    /**
     * 上次资源文件到hdsf上
     * @param tenantId
     * @param resourceName
     * @param originalFilename
     * @param tmpPath
     * @return
     */
    private String uploadHDFSFileWithResource(Long tenantId, String resourceName, String originalFilename, String tmpPath) {
        if (originalFilename == null || tmpPath == null){
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }

        final String hdfsFileName = tenantId + "_" + resourceName + "_" + originalFilename;
        final String hdfsPath = this.getBatchHdfsPath(tenantId, hdfsFileName);

        try {
            HdfsOperator.uploadLocalFileToHdfs(HadoopConf.getConfiguration(tenantId), HadoopConf.getHadoopKerberosConf(tenantId),tmpPath, hdfsPath);
        } catch (final Exception e) {
            BatchResourceService.logger.error("{}", e);
            throw new RdosDefineException(ErrorCode.SERVER_EXCEPTION, e);
        } finally {
            final File tmpFile = new File(tmpPath);
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
        }
        return hdfsPath;
    }

    /**
     * 由functionId获取对应的resource
     * @param functionId
     * @return
     */
    public String getResourceURLByFunctionId(Long functionId) {
        return batchResourceDao.getResourceURLByFunctionId(functionId);
    }

    /**
     * 根据 租户、目录Id 查询资源列表
     * @param tenantId
     * @param nodePid
     * @return
     */
    public List<BatchResource> listByPidAndTenantId(Long tenantId, Long nodePid) {
        return batchResourceDao.listByPidAndTenantId(tenantId, nodePid);
    }
}
