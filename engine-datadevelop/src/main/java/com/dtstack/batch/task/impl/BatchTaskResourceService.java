package com.dtstack.batch.task.impl;

import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.BatchTaskResourceDao;
import com.dtstack.batch.domain.BatchResource;
import com.dtstack.batch.domain.BatchTask;
import com.dtstack.batch.domain.BatchTaskResource;
import com.dtstack.batch.service.impl.BatchResourceService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BatchTaskResourceService {

    private static Logger logger = LoggerFactory.getLogger(com.dtstack.batch.service.task.impl.BatchTaskResourceService.class);

    @Autowired
    private BatchTaskResourceDao batchTaskResourceDao;

    @Autowired
    private BatchResourceService batchResourceService;

    /**
     * 根据资源id，获取可用的 资源-任务 关系记录
     */
    public List<BatchTaskResource> getUseableResources(Long resourceId) {
        return batchTaskResourceDao.listByResourceId(resourceId);
    }

    public List<BatchTaskResource> getTaskResources(Long taskId, Integer type, Long projectId) {
        return batchTaskResourceDao.listByTaskId(taskId, type, projectId);
    }

    /**
     * 获得 资源-任务 列表
     *
     * @param taskId    任务id
     * @param projectId 项目id
     * @return
     */
    public List<BatchResource> getResources(Long taskId, Long projectId, Integer type) {
        List<Long> taskResourceIds = this.getResourceIdList(taskId, type, projectId);
        if (CollectionUtils.isEmpty(taskResourceIds)) {
            return Collections.EMPTY_LIST;
        }

        return batchResourceService.getResourceList(taskResourceIds);
    }

    public void deleteByProjectId(Long projectId) {
        batchTaskResourceDao.deleteByProjectId(projectId);
    }

    public List<Long> getResourceIdList(long taskId, Integer type, long projectId) {
        List<BatchTaskResource> resourceList = batchTaskResourceDao.listByTaskId(taskId, type, projectId);
        List<Long> resultIdList = Lists.newArrayList();
        if (resourceList == null) {
            return resultIdList;
        }

        resourceList.forEach(record -> {
            resultIdList.add(record.getResourceId());
        });

        return resultIdList;
    }

    public BatchTaskResource save(BatchTaskResource streamTaskResource) {
        return addOrUpdate(streamTaskResource);
    }


    /**
     * 根据 任务id 和 项目id，删除《任务资源关系》与《资源》
     *
     * @param taskId    任务id
     * @param projectId 项目id
     * @author toutian
     */
    public void deleteTaskResource(long taskId, long projectId) {

        //删除资源-任务关系
        batchTaskResourceDao.deleteByTaskId(taskId, projectId, null);
    }

    public List<BatchTaskResource> save(BatchTask batchTask, List<Long> resourceIds, Integer refType) {

        List<BatchTaskResource> taskResources = new ArrayList<>(resourceIds.size());

        for (Long resourceId : resourceIds) {

            //检查资源是否存在
            if (batchResourceService.getResource(resourceId) == null) {
                logger.warn("can't find resource from BatchResource table by id:{}", resourceId);
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_RESOURCE);
            }

            //存储
            BatchTaskResource resource = batchTaskResourceDao.getByTaskIdAndResourceId(batchTask.getId()
                    , batchTask.getProjectId(), resourceId, refType);

            if (resource == null) {
                resource = new BatchTaskResource();
            }

            resource.setTaskId(batchTask.getId());
            resource.setTenantId(batchTask.getTenantId());
            resource.setProjectId(batchTask.getProjectId());
            resource.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
            resource.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
            resource.setResourceId(resourceId);
            resource.setResourceType(refType);
            taskResources.add(addOrUpdate(resource));
        }

        return taskResources;
    }

    public BatchTaskResource addOrUpdate(BatchTaskResource batchTaskResource) {
        if (batchTaskResource.getId() > 0) {
            batchTaskResourceDao.update(batchTaskResource);
        } else {
            batchTaskResourceDao.insert(batchTaskResource);
        }
        return batchTaskResource;
    }

    public void copyTaskResource(Long srcTaskId, BatchTask distTask, Integer type){
        List<Long> resourceIds = getResourceIdList(srcTaskId, type, distTask.getProjectId());
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(resourceIds)){
            save(distTask,resourceIds, type);
        }
    }

    public BatchTaskResource getByTaskIdAndResourceId(long taskId, long projectId,
                                                      long resourceId, Integer resourceType){
        return batchTaskResourceDao.getByTaskIdAndResourceId(taskId,projectId,resourceId,resourceType);
    }
}
