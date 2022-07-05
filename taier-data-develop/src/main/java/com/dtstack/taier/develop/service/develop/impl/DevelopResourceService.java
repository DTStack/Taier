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
import com.dtstack.taier.dao.domain.DevelopCatalogue;
import com.dtstack.taier.dao.domain.DevelopFunctionResource;
import com.dtstack.taier.dao.domain.DevelopResource;
import com.dtstack.taier.dao.domain.DevelopTaskResource;
import com.dtstack.taier.dao.mapper.DevelopResourceMapper;
import com.dtstack.taier.develop.dto.devlop.DevelopResourceAddDTO;
import com.dtstack.taier.develop.dto.devlop.DevelopResourceVO;
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
public class DevelopResourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevelopResourceService.class);

    @Autowired
    private DevelopResourceMapper developResourceMapper;

    @Autowired
    private DevelopFunctionResourceService developFunctionResourceService;

    @Autowired
    private DevelopCatalogueService developCatalogueService;

    @Autowired
    private DevelopTaskResourceService developTaskResourceService;

    @Autowired
    private DevelopTaskService developTaskService;

    @Autowired
    private UserService userService;

    @Autowired
    private EnvironmentContext environmentContext;

    public static final String TAIER_RESOURCE = "/taier/resource";

    /**
     * 添加资源
     */
    public CatalogueVO addResource(DevelopResourceAddDTO DevelopResourceAddDTO) {
        Long tenantId = DevelopResourceAddDTO.getTenantId();
        Long userId = DevelopResourceAddDTO.getUserId();

        String resourceName;
        Long resourceId = null;
        DevelopResource resourceDB = null;
        Integer resourceType = null;
        if (DevelopResourceAddDTO.getId() != null && DevelopResourceAddDTO.getId() != 0L) {
            resourceId = DevelopResourceAddDTO.getId();
            resourceDB = this.developResourceMapper.getOne(resourceId);
            resourceName = resourceDB.getResourceName();
            resourceType = resourceDB.getResourceType();
        } else {
            if (StringUtils.isEmpty(DevelopResourceAddDTO.getResourceName())) {
                throw new RdosDefineException("需要设置参数 resourceName.", ErrorCode.INVALID_PARAMETERS);
            }
            resourceName = DevelopResourceAddDTO.getResourceName();
            resourceType = DevelopResourceAddDTO.getResourceType() == null ? ResourceType.OTHER.getType() : DevelopResourceAddDTO.getResourceType();
        }

        String remotePath = EComputeType.STREAM.getType() != DevelopResourceAddDTO.getComputeType() ?
                uploadHDFSFileWithResource(tenantId, resourceName, DevelopResourceAddDTO.getOriginalFilename(), DevelopResourceAddDTO.getTmpPath()) :
                uploadToSftp(tenantId, resourceName, DevelopResourceAddDTO.getOriginalFilename(), DevelopResourceAddDTO.getTmpPath());

        DevelopResource DevelopResource = null;
        //重新上传资源
        if (Objects.nonNull(resourceId)) {
            DevelopResource = resourceDB;
            if (Deleted.DELETED.getStatus().equals(DevelopResource.getIsDeleted())) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_RESOURCE);
            }
            DevelopResource.setResourceDesc(DevelopResourceAddDTO.getResourceDesc());
            DevelopResource.setOriginFileName(DevelopResourceAddDTO.getOriginalFilename());
            DevelopResource.setUrl(remotePath);
        } else {
            //判断是否已经存在相同的资源了
            developTaskService.checkName(resourceName, CatalogueType.RESOURCE_MANAGER.name(), null, 1, tenantId);

            DevelopResourceAddDTO.setUrl(remotePath);
            DevelopResourceAddDTO.setCreateUserId(userId);
            DevelopResource = PublicUtil.objectToObject(DevelopResourceAddDTO, com.dtstack.taier.dao.domain.DevelopResource.class);
            if (Objects.isNull(DevelopResource)) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_RESOURCE);
            }
            DevelopResource.setOriginFileName(DevelopResourceAddDTO.getOriginalFilename());
        }

        //resourceType 设置默认值
        resourceType = resourceType != null ? resourceType : ResourceType.OTHER.getType();

        DevelopResource.setResourceType(resourceType);
        DevelopResource.setComputeType(DevelopResourceAddDTO.getComputeType());
        DevelopResource.setModifyUserId(userId);
        addOrUpdate(DevelopResource);

        DevelopCatalogue catalogue = developCatalogueService.getOne(DevelopResource.getNodePid());
        CatalogueVO catalogueVO = new CatalogueVO();
        catalogueVO.setId(DevelopResource.getId());
        catalogueVO.setName(DevelopResource.getResourceName());
        catalogueVO.setType("file");
        catalogueVO.setLevel(catalogue.getLevel() + 1);
        catalogueVO.setChildren(null);
        catalogueVO.setParentId(catalogue.getId());
        catalogueVO.setResourceType(resourceType);

        String username = userService.getUserName(catalogue.getCreateUserId());

        return catalogueVO;
    }

    /**
     * 新增或修改
     *
     * @param DevelopResource
     * @return
     */
    private void addOrUpdate(DevelopResource DevelopResource) {
        if (DevelopResource.getId() != null && DevelopResource.getId() > 0) {
            developResourceMapper.update(DevelopResource);
            return;
        }
        developResourceMapper.insert(DevelopResource);
    }

    /**
     * 删除资源
     */
    public Long deleteResource(Long tenantId, Long resourceId) {
        List<DevelopTaskResource> taskResources = this.developTaskResourceService.getUseableResources(resourceId);
        if (!CollectionUtils.isEmpty(taskResources)) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_DELETE_RESOURCE);
        }
        List<DevelopFunctionResource> functionResources = developFunctionResourceService.listByResourceId(resourceId);
        if (!CollectionUtils.isEmpty(functionResources)) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_DELETE_RESOURCE);
        }
        //删除资源在hdfs的实际存储文件
        DevelopResource resource = getResource(resourceId);
        try {
            HdfsOperator.checkAndDele(HadoopConf.getConfiguration(tenantId), HadoopConf.getHadoopKerberosConf(tenantId), resource.getUrl());
        } catch (Exception e) {
            LOGGER.error("tenantId:{}  resourceId:{} fail delete resource from HDFS", tenantId, resourceId, e);
        }

        //删除资源记录
        developResourceMapper.deleteById(resourceId);
        return resourceId;
    }

    /**
     * 获取资源详情
     */
    public DevelopResourceVO getResourceById(long resourceId) {
        DevelopResource DevelopResource = this.getResource(resourceId);
        if (Objects.nonNull(DevelopResource)) {
            DevelopResourceVO vo = DevelopResourceVO.toVO(DevelopResource);
            vo.setCreateUser(userService.getById(DevelopResource.getCreateUserId()));
            //是否是该项目成员
            return vo;
        }
        return null;
    }

    /**
     * 获取上传的资源到HDFS上的路径
     *
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
     *
     * @param resourceIdList
     * @return
     */
    public List<DevelopResource> getResourceList(List<Long> resourceIdList) {
        return developResourceMapper.listByIds(resourceIdList);
    }

    /**
     * 根据资源id获取资源信息
     *
     * @param resourceId
     * @return
     */
    public DevelopResource getResource(long resourceId) {
        return developResourceMapper.getOne(resourceId);
    }

    /**
     * 替换资源
     */
    public void replaceResource(DevelopResourceAddDTO DevelopResourceAddDTO) {
        Long tenantId = DevelopResourceAddDTO.getTenantId();
        Long resourceId = DevelopResourceAddDTO.getId();

        DevelopResource resourceDb = developResourceMapper.getOne(resourceId);
        if (Objects.isNull(resourceDb)) {
            throw new RdosDefineException("替换字段不存在");
        }

        String resourceName = resourceDb.getResourceName();

        String remotePath = EComputeType.STREAM.getType() != DevelopResourceAddDTO.getComputeType() ?
                uploadHDFSFileWithResource(tenantId, resourceName, DevelopResourceAddDTO.getOriginalFilename(), DevelopResourceAddDTO.getTmpPath()) :
                uploadToSftp(tenantId, resourceName, DevelopResourceAddDTO.getOriginalFilename(), DevelopResourceAddDTO.getTmpPath());

        resourceDb.setUrl(remotePath);
        resourceDb.setOriginFileName(DevelopResourceAddDTO.getOriginalFilename());
        resourceDb.setResourceDesc(DevelopResourceAddDTO.getResourceDesc());
        resourceDb.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        developResourceMapper.update(resourceDb);
    }

    /**
     * 上次资源文件到hdsf上
     *
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
            HdfsOperator.uploadLocalFileToHdfs(HadoopConf.getConfiguration(tenantId), HadoopConf.getHadoopKerberosConf(tenantId), tmpPath, hdfsPath);
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
        String finalFileName = tenantId + "_" + originalFileName;
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
     *
     * @param functionId
     * @return
     */
    public String getResourceURLByFunctionId(Long functionId) {
        return developResourceMapper.getResourceURLByFunctionId(functionId);
    }

    /**
     * 根据 租户、目录Id 查询资源列表
     *
     * @param tenantId
     * @param nodePid
     * @return
     */
    public List<DevelopResource> listByPidAndTenantId(Long tenantId, Long nodePid) {
        return developResourceMapper.listByPidAndTenantId(tenantId, nodePid);
    }

    /**
     * 根据 租户、名称 获取资源列表
     *
     * @param tenantId     租户ID
     * @param resourceName 资源名称
     * @return
     */
    public List<DevelopResource> listByNameAndTenantId(Long tenantId, String resourceName) {
        return developResourceMapper.listByNameAndTenantId(tenantId, resourceName);
    }

}
