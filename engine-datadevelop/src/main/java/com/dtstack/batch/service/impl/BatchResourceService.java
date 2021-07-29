package com.dtstack.batch.service.impl;

import com.dtstack.batch.common.enums.CatalogueType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
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
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.ResourceType;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
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
    private UserDao userDao;

    @Autowired
    EnvironmentContext environmentContext;

    /**
     * 添加资源
     */
    public CatalogueVO addResource(BatchResourceAddDTO batchResourceAddDTO) {
        final long projectId = batchResourceAddDTO.getProjectId();
        final long userId = batchResourceAddDTO.getUserId();

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

        String hdfsPath = uploadHDFSFileWithResource(batchResourceAddDTO.getDtuicTenantId(), projectId, resourceName,
                batchResourceAddDTO.getOriginalFilename(), batchResourceAddDTO.getTmpPath());

        BatchResource batchResource = null;
        //重新上传资源
        if (resourceId != null) {
            batchResource = resourceDB;
            if (batchResource == null || batchResource.getIsDeleted() == Deleted.DELETED.getStatus()) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_RESOURCE);
            }
            batchResource.setResourceDesc(batchResourceAddDTO.getResourceDesc());
            batchResource.setOriginFileName(batchResourceAddDTO.getOriginalFilename());
            batchResource.setUrl(hdfsPath);
        } else {
            //判断是否已经存在相同的资源了
            this.batchTaskService.checkName(resourceName, CatalogueType.RESOURCE_MANAGER.name(), null, 1, projectId);

            batchResourceAddDTO.setUrl(hdfsPath);
            batchResourceAddDTO.setCreateUserId(userId);
            try {
                batchResource = PublicUtil.objectToObject(batchResourceAddDTO, BatchResource.class);
                batchResource.setOriginFileName(batchResourceAddDTO.getOriginalFilename());
            } catch (IOException e) {
                throw new RdosDefineException(String.format("转化失败：%s", e.getMessage()), e);
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

        final User user = this.userDao.getOne(catalogue.getCreateUserId());
        catalogueVO.setCreateUser(user.getUserName());

        return catalogueVO;
    }

    public Long addResourceWithUrl(Long id, String resourceName, String originFileName, String url, String resourceDesc, Integer resourceType, Long nodePid, Long userId, Long tenantId, Long projectId) {
        Preconditions.checkNotNull(resourceName, "resourceName can not be null");
        Preconditions.checkNotNull(originFileName, "orginFileName can not be null");
        Preconditions.checkNotNull(url, "remoteUrl can not be null");
        Preconditions.checkNotNull(nodePid, "nodePid can not be null");

        BatchResource batchResource = this.buildResource(id, resourceName, originFileName, url, resourceDesc, resourceType, nodePid, userId, tenantId, projectId);
        batchResource = this.addOrUpdate(batchResource);
        return batchResource.getId();
    }

    private BatchResource buildResource(Long id, String resourceName, String originFileName, String url, String resourceDesc, Integer resourceType, Long nodePid, Long userId, Long tenantId, Long projectId) {
        final BatchResource batchResource;
        //重新上传资源
        if (id != null) {
            batchResource = this.batchResourceDao.getOne(id);
            if (batchResource == null || batchResource.getIsDeleted() == Deleted.DELETED.getStatus()) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_RESOURCE);
            }
            batchResource.setResourceDesc(resourceDesc);
            batchResource.setOriginFileName(originFileName);
            batchResource.setUrl(url);
            batchResource.setNodePid(nodePid);
        } else {
            //判断是否已经存在相同的资源了
            this.batchTaskService.checkName(resourceName, CatalogueType.RESOURCE_MANAGER.name(), null, 1, projectId);
            batchResource = new BatchResource();
            batchResource.setResourceName(resourceName);
            batchResource.setOriginFileName(originFileName);
            batchResource.setUrl(url);
            batchResource.setResourceDesc(resourceDesc);
            batchResource.setNodePid(nodePid);
            batchResource.setResourceType(resourceType);
            batchResource.setCreateUserId(userId);
            batchResource.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
            batchResource.setProjectId(projectId);
            batchResource.setTenantId(tenantId);
        }

        batchResource.setResourceType(ResourceType.OTHER.getType());
        batchResource.setModifyUserId(userId);
        batchResource.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        return batchResource;
    }

    public BatchResource addOrUpdate(BatchResource batchResource) {
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
    public List<BatchResource> getResources(Long projectId) {

        return this.batchResourceDao.listByProjectId(projectId);
    }

    /**
     * 删除资源
     */
    public Long deleteResource(Long resourceId, Long projectId, Long dtuicTenantId) {
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
            HdfsOperator.checkAndDele(HadoopConf.getConfiguration(dtuicTenantId), HadoopConf.getHadoopKerberosConf(dtuicTenantId),resource.getUrl());
        } catch (final Exception e) {
            BatchResourceService.logger.error(" taskId:{} projectId:{} userId:{} fail delete resource from HDFS", e);
        }

        //删除资源记录
        batchResourceDao.deleteById(resourceId, projectId);
        logger.info(String.format("detele resource success  resourceId = %s, projectId = %s",resourceId,projectId));
        return resourceId;
    }

    /**
     * 获取资源详情
     */
    public BatchResourceVO getResourceById(long resourceId) {
        BatchResource batchResource = this.getResource(resourceId);
        if (Objects.nonNull(batchResource)) {
            BatchResourceVO vo = BatchResourceVO.toVO(batchResource);
            vo.setCreateUser(this.userDao.getOne(batchResource.getCreateUserId()));
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
     * 根据 资源ids 和 项目id，软删除资源
     */
    public List<Long> deleteByResourceIdsAndProjectId(List<Long> resourceIds, Long projectId) {

        Assert.notNull(resourceIds, "resourceIds must set");
        Assert.notNull(projectId, "projectId must set");

        this.batchResourceDao.deleteByIds(resourceIds, projectId);
        return resourceIds;
    }


    private String getBatchHdfsPath(Long dtuicTenantId, String fileName) {
        final String hdfsURI = HadoopConf.getDefaultFs(dtuicTenantId);
        return hdfsURI + environmentContext.getHdfsBatchPath() + fileName;
    }

   public void deleteByProjectId(Long projectId, Long userId) {
        batchResourceDao.deleteByProjectId(projectId, userId);
    }

    public List<BatchResource> getResourceList(List<Long> resourceIdList) {
        return this.batchResourceDao.listByIds(resourceIdList, Deleted.NORMAL.getStatus());
    }

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

            final List<User> users = this.userDao.listByIds(userIds);
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
        final long projectId = batchResourceAddDTO.getProjectId();
        final long resourceId = batchResourceAddDTO.getId();

        final BatchResource resourceDb = this.batchResourceDao.getOne(resourceId);
        if (Objects.isNull(resourceDb)) {
            throw new RdosDefineException("替换字段不存在");
        }

        final String resourceName = resourceDb.getResourceName();

        String hdfsPath = uploadHDFSFileWithResource(batchResourceAddDTO.getDtuicTenantId(), projectId, resourceName,
                batchResourceAddDTO.getOriginalFilename(), batchResourceAddDTO.getTmpPath());

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

    private String uploadHDFSFileWithResource(final Long dtuicTenantId, final long projectId, final String resourceName, String originalFilename, String tmpPath) {
        if (originalFilename == null || tmpPath == null){
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }

        final String hdfsFileName = projectId + "_" + resourceName + "_" + originalFilename;
        final String hdfsPath = this.getBatchHdfsPath(dtuicTenantId, hdfsFileName);

        try {
            HdfsOperator.uploadLocalFileToHdfs(HadoopConf.getConfiguration(dtuicTenantId), HadoopConf.getHadoopKerberosConf(dtuicTenantId),tmpPath, hdfsPath);
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

}
