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

import com.dtstack.taier.common.enums.CatalogueType;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.ResourceType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.dao.domain.BatchCatalogue;
import com.dtstack.taier.dao.domain.BatchFunctionResource;
import com.dtstack.taier.dao.domain.BatchResource;
import com.dtstack.taier.dao.domain.BatchTaskResource;
import com.dtstack.taier.dao.mapper.BatchResourceDao;
import com.dtstack.taier.develop.dto.devlop.BatchResourceAddDTO;
import com.dtstack.taier.develop.dto.devlop.BatchResourceVO;
import com.dtstack.taier.develop.dto.devlop.CatalogueVO;
import com.dtstack.taier.develop.service.user.UserService;
import com.dtstack.taier.develop.utils.develop.common.HadoopConf;
import com.dtstack.taier.develop.utils.develop.common.HdfsOperator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author sishu.yss
 */
@Service
public class BatchResourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchResourceService.class);

    @Autowired
    private BatchResourceDao batchResourceDao;
    
    @Autowired
    private BatchFunctionResourceService batchFunctionResourceService;
    
    @Autowired
    private BatchCatalogueService batchCatalogueService;

    @Autowired
    private BatchTaskResourceService batchTaskResourceService;

    @Autowired
    private BatchTaskService batchTaskService;

    @Autowired
    private UserService userService;

    @Autowired
    private EnvironmentContext environmentContext;

    /**
     * 添加资源
     */
    public CatalogueVO addResource(BatchResourceAddDTO batchResourceAddDTO) {
        Long tenantId = batchResourceAddDTO.getTenantId();
        Long userId = batchResourceAddDTO.getUserId();

        String resourceName;
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
        if (Objects.nonNull(resourceId)) {
            batchResource = resourceDB;
            if (Deleted.DELETED.getStatus().equals(batchResource.getIsDeleted())) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_RESOURCE);
            }
            batchResource.setResourceDesc(batchResourceAddDTO.getResourceDesc());
            batchResource.setOriginFileName(batchResourceAddDTO.getOriginalFilename());
            batchResource.setUrl(hdfsPath);
        } else {
            //判断是否已经存在相同的资源了
            batchTaskService.checkName(resourceName, CatalogueType.RESOURCE_MANAGER.name(), null, 1, tenantId);

            batchResourceAddDTO.setUrl(hdfsPath);
            batchResourceAddDTO.setCreateUserId(userId);
            batchResource = PublicUtil.objectToObject(batchResourceAddDTO, BatchResource.class);
            if (Objects.isNull(batchResource)){
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_RESOURCE);
            }
            batchResource.setOriginFileName(batchResourceAddDTO.getOriginalFilename());
        }

        //resourceType 设置默认值
        resourceType = resourceType != null ? resourceType : ResourceType.OTHER.getType();

        batchResource.setResourceType(resourceType);
        batchResource.setModifyUserId(userId);
        addOrUpdate(batchResource);

        BatchCatalogue catalogue = batchCatalogueService.getOne(batchResource.getNodePid());
        CatalogueVO catalogueVO = new CatalogueVO();
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
     * 新增或修改
     * @param batchResource
     * @return
     */
    private void addOrUpdate(BatchResource batchResource) {
        if (batchResource.getId() != null && batchResource.getId() > 0) {
            batchResourceDao.update(batchResource);
            return;
        }
        batchResourceDao.insert(batchResource);
    }

    /**
     * 删除资源
     */
    public Long deleteResource(Long tenantId, Long resourceId) {
        List<BatchTaskResource> taskResources = this.batchTaskResourceService.getUseableResources(resourceId);
        if (!CollectionUtils.isEmpty(taskResources)) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_DELETE_RESOURCE);
        }
        List<BatchFunctionResource> functionResources = batchFunctionResourceService.listByResourceId(resourceId);
        if (!CollectionUtils.isEmpty(functionResources)) {
        	throw new RdosDefineException(ErrorCode.CAN_NOT_DELETE_RESOURCE);
        }
        //删除资源在hdfs的实际存储文件
        BatchResource resource = getResource(resourceId);
        try {
            HdfsOperator.checkAndDele(HadoopConf.getConfiguration(tenantId), HadoopConf.getHadoopKerberosConf(tenantId),resource.getUrl());
        } catch (Exception e) {
            LOGGER.error("tenantId:{}  resourceId:{} fail delete resource from HDFS", tenantId, resourceId, e);
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
     * 获取离线上传的资源到HDFS上的路径
     * @param tenantId
     * @param fileName
     * @return
     */
    private String getBatchHdfsPath(Long tenantId, String fileName) {
        String hdfsURI = HadoopConf.getDefaultFs(tenantId);
        return String.format("%s%s%s", hdfsURI, environmentContext.getHdfsBatchPath(), fileName);
    }

    /**
     * 根据资源ids 查询资源列表
     * @param resourceIdList
     * @return
     */
    public List<BatchResource> getResourceList(List<Long> resourceIdList) {
        return batchResourceDao.listByIds(resourceIdList);
    }

    /**
     * 根据资源id获取资源信息
     * @param resourceId
     * @return
     */
    public BatchResource getResource(long resourceId) {
        return batchResourceDao.getOne(resourceId);
    }

    /**
     * 替换资源
     */
    public void replaceResource(BatchResourceAddDTO batchResourceAddDTO) {
        Long tenantId = batchResourceAddDTO.getTenantId();
        Long resourceId = batchResourceAddDTO.getId();

        BatchResource resourceDb = batchResourceDao.getOne(resourceId);
        if (Objects.isNull(resourceDb)) {
            throw new RdosDefineException("替换字段不存在");
        }

        String resourceName = resourceDb.getResourceName();
        String hdfsPath = uploadHDFSFileWithResource(tenantId, resourceName, batchResourceAddDTO.getOriginalFilename(), batchResourceAddDTO.getTmpPath());

        resourceDb.setUrl(hdfsPath);
        resourceDb.setOriginFileName(batchResourceAddDTO.getOriginalFilename());
        resourceDb.setResourceDesc(batchResourceAddDTO.getResourceDesc());
        resourceDb.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        batchResourceDao.update(resourceDb);
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
        if (StringUtils.isBlank(originalFilename) || StringUtils.isBlank(tmpPath)) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }

        String hdfsFileName = String.format("%s_%s_%s", tenantId, resourceName, originalFilename);
        String hdfsPath = this.getBatchHdfsPath(tenantId, hdfsFileName);

        try {
            HdfsOperator.uploadLocalFileToHdfs(HadoopConf.getConfiguration(tenantId), HadoopConf.getHadoopKerberosConf(tenantId),tmpPath, hdfsPath);
        } catch (Exception e) {
            throw new RdosDefineException(ErrorCode.SERVER_EXCEPTION, e);
        } finally {
            File tmpFile = new File(tmpPath);
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

    /**
     * 根据 租户、名称 获取资源列表
     *
     * @param tenantId     租户ID
     * @param resourceName 资源名称
     * @return
     */
    public List<BatchResource> listByNameAndTenantId(Long tenantId, String resourceName) {
        return batchResourceDao.listByNameAndTenantId(tenantId, resourceName);
    }

}
