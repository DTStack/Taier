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
import com.dtstack.taier.common.enums.EComputeType;
import com.dtstack.taier.common.enums.ResourceType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.sftp.SFTPHandler;
import com.dtstack.taier.common.util.AssertUtils;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.dao.domain.BatchCatalogue;
import com.dtstack.taier.dao.domain.BatchFunctionResource;
import com.dtstack.taier.dao.domain.BatchResource;
import com.dtstack.taier.dao.domain.BatchTaskResource;
import com.dtstack.taier.dao.mapper.DevelopResourceDao;
import com.dtstack.taier.develop.dto.devlop.BatchResourceAddDTO;
import com.dtstack.taier.develop.dto.devlop.BatchResourceVO;
import com.dtstack.taier.develop.dto.devlop.CatalogueVO;
import com.dtstack.taier.develop.service.user.UserService;
import com.dtstack.taier.develop.utils.develop.common.HadoopConf;
import com.dtstack.taier.develop.utils.develop.common.HdfsOperator;
import com.dtstack.taier.develop.utils.develop.service.impl.Engine2DTOService;
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
import java.util.Map;
import java.util.Objects;

/**
 * @author sishu.yss
 */
@Service
public class BatchResourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchResourceService.class);

    @Autowired
    private DevelopResourceDao developResourceDao;
    
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

   public static final String TAIER_RESOURCE = "/taier/resource";
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
            resourceDB = this.developResourceDao.getOne(resourceId);
            resourceName = resourceDB.getResourceName();
            resourceType = resourceDB.getResourceType();
        } else {
            if (StringUtils.isEmpty(batchResourceAddDTO.getResourceName())) {
                throw new RdosDefineException("需要设置参数 resourceName.", ErrorCode.INVALID_PARAMETERS);
            }
            resourceName = batchResourceAddDTO.getResourceName();
            resourceType =  batchResourceAddDTO.getResourceType() == null ? ResourceType.OTHER.getType() : batchResourceAddDTO.getResourceType();
        }

      String remotePath = EComputeType.STREAM.getType() != batchResourceAddDTO.getComputeType() ?
              uploadHDFSFileWithResource(tenantId, resourceName, batchResourceAddDTO.getOriginalFilename(), batchResourceAddDTO.getTmpPath()):
              uploadToSftp(tenantId, resourceName, batchResourceAddDTO.getOriginalFilename(), batchResourceAddDTO.getTmpPath());

        BatchResource batchResource = null;
        //重新上传资源
        if (Objects.nonNull(resourceId)) {
            batchResource = resourceDB;
            if (Deleted.DELETED.getStatus().equals(batchResource.getIsDeleted())) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_RESOURCE);
            }
            batchResource.setResourceDesc(batchResourceAddDTO.getResourceDesc());
            batchResource.setOriginFileName(batchResourceAddDTO.getOriginalFilename());
            batchResource.setUrl(remotePath);
        } else {
            //判断是否已经存在相同的资源了
            batchTaskService.checkName(resourceName, CatalogueType.RESOURCE_MANAGER.name(), null, 1, tenantId);

            batchResourceAddDTO.setUrl(remotePath);
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
        batchResource.setComputeType(batchResourceAddDTO.getComputeType());
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
            developResourceDao.update(batchResource);
            return;
        }
        developResourceDao.insert(batchResource);
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
        developResourceDao.deleteById(resourceId);
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
        return developResourceDao.listByIds(resourceIdList);
    }

    /**
     * 根据资源id获取资源信息
     * @param resourceId
     * @return
     */
    public BatchResource getResource(long resourceId) {
        return developResourceDao.getOne(resourceId);
    }

    /**
     * 替换资源
     */
    public void replaceResource(BatchResourceAddDTO batchResourceAddDTO) {
        Long tenantId = batchResourceAddDTO.getTenantId();
        Long resourceId = batchResourceAddDTO.getId();

        BatchResource resourceDb = developResourceDao.getOne(resourceId);
        if (Objects.isNull(resourceDb)) {
            throw new RdosDefineException("替换字段不存在");
        }

        String resourceName = resourceDb.getResourceName();

        String remotePath = EComputeType.STREAM.getType() != batchResourceAddDTO.getComputeType() ?
                uploadHDFSFileWithResource(tenantId, resourceName, batchResourceAddDTO.getOriginalFilename(), batchResourceAddDTO.getTmpPath()):
                uploadToSftp(tenantId, resourceName, batchResourceAddDTO.getOriginalFilename(), batchResourceAddDTO.getTmpPath());

        resourceDb.setUrl(remotePath);
        resourceDb.setOriginFileName(batchResourceAddDTO.getOriginalFilename());
        resourceDb.setResourceDesc(batchResourceAddDTO.getResourceDesc());
        resourceDb.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        developResourceDao.update(resourceDb);
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

    private String uploadToSftp(Long tenantId, String resourceName, String oriFileName, String tmpFilePath) {
        Map<String, String> sftpConf = Engine2DTOService.getSftp(tenantId);
        String path = sftpConf.get("path");
        if (StringUtils.isBlank(path)) {
            throw new DtCenterDefException("sftp路径配置错误");
        }
        File localFile = renameTmpFileName(tenantId, oriFileName, tmpFilePath);
        String sftpFileName = String.format("%s_%s", tenantId, resourceName);
        String remotePath = new StringBuilder(path)
                .append(TAIER_RESOURCE)
                .append(File.separator)
                .append(sftpFileName)
                .toString();
        SFTPHandler instance = null;
        try {
            instance = SFTPHandler.getInstance(sftpConf);
            boolean success = instance.upload(remotePath, localFile.getPath());
            AssertUtils.isTrue(success, "上传sftp异常");
        } catch (Exception e) {
            throw new DtCenterDefException(e.getMessage(), e);
        } finally {
            if (localFile.exists()) {
                localFile.delete();
            }
        }
        return remotePath + "/" + localFile.getName();
    }

    private File renameTmpFileName(long tenantId, String originalFileName, String tmpFilePath) {
        String finalFileName = tenantId  + "_" + originalFileName;
        File file = new File(tmpFilePath);
        File renameFile = new File(file.getParent() + File.separator + finalFileName);
        boolean checkRename = file.renameTo(renameFile);
        if (!checkRename) {
            throw new DtCenterDefException(String.format("rename file [%s] to [%s] fail...", file.getAbsolutePath(), renameFile.getAbsolutePath()));
        }
        return renameFile;
    }

    /**
     * 由functionId获取对应的resource
     * @param functionId
     * @return
     */
    public String getResourceURLByFunctionId(Long functionId) {
        return developResourceDao.getResourceURLByFunctionId(functionId);
    }

    /**
     * 根据 租户、目录Id 查询资源列表
     * @param tenantId
     * @param nodePid
     * @return
     */
    public List<BatchResource> listByPidAndTenantId(Long tenantId, Long nodePid) {
        return developResourceDao.listByPidAndTenantId(tenantId, nodePid);
    }

    /**
     * 根据 租户、名称 获取资源列表
     *
     * @param tenantId     租户ID
     * @param resourceName 资源名称
     * @return
     */
    public List<BatchResource> listByNameAndTenantId(Long tenantId, String resourceName) {
        return developResourceDao.listByNameAndTenantId(tenantId, resourceName);
    }

}
