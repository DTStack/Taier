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

import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.DevelopResource;
import com.dtstack.taier.dao.domain.DevelopTaskResource;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.mapper.DevelopTaskResourceMapper;
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
public class DevelopTaskResourceService {

    private static Logger logger = LoggerFactory.getLogger(DevelopTaskResourceService.class);

    @Autowired
    private DevelopTaskResourceMapper developTaskResourceDao;

    @Autowired
    private DevelopResourceService DevelopResourceService;

    /**
     * 根据资源id，获取可用的 资源-任务 关系记录
     */
    public List<DevelopTaskResource> getUseableResources(Long resourceId) {
        return developTaskResourceDao.listByResourceId(resourceId);
    }

    public List<DevelopTaskResource> getTaskResources(Long taskId, Integer type) {
        return developTaskResourceDao.listByTaskId(taskId, type);
    }

    /**
     * 获得 资源-任务 列表
     *
     * @param taskId 任务id
     * @return
     */
    public List<DevelopResource> getResources(Long taskId, Integer type) {
        List<Long> taskResourceIds = this.getResourceIdList(taskId, type);
        if (CollectionUtils.isEmpty(taskResourceIds)) {
            return Collections.EMPTY_LIST;
        }

        return DevelopResourceService.getResourceList(taskResourceIds);
    }

    public void deleteByTenantId(Long tenantId) {
        developTaskResourceDao.deleteByTenantId(tenantId);
    }

    public List<Long> getResourceIdList(long taskId, Integer type) {
        List<DevelopTaskResource> resourceList = developTaskResourceDao.listByTaskId(taskId, type);
        List<Long> resultIdList = Lists.newArrayList();
        if (resourceList == null) {
            return resultIdList;
        }

        resourceList.forEach(record -> {
            resultIdList.add(record.getResourceId());
        });

        return resultIdList;
    }

    /**
     * 根据 任务id 和 项目id，删除《任务资源关系》与《资源》
     *
     * @param taskId 任务id
     * @author toutian
     */
    public void deleteTaskResource(Long taskId) {

        //删除资源-任务关系
        developTaskResourceDao.deleteByTaskId(taskId, null);
    }

    public List<DevelopTaskResource> save(Task task, List<Long> resourceIds, Integer refType) {

        List<DevelopTaskResource> taskResources = new ArrayList<>(resourceIds.size());

        for (Long resourceId : resourceIds) {

            //检查资源是否存在
            if (DevelopResourceService.getResource(resourceId) == null) {
                logger.warn("can't find resource from DevelopResource table by id:{}", resourceId);
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_RESOURCE);
            }

            //存储
            DevelopTaskResource resource = developTaskResourceDao.getByTaskIdAndResourceId(task.getId(), resourceId, refType);

            if (resource == null) {
                resource = new DevelopTaskResource();
            }

            resource.setTaskId(task.getId());
            resource.setTenantId(task.getTenantId());
            resource.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
            resource.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
            resource.setResourceId(resourceId);
            resource.setResourceType(refType);
            taskResources.add(addOrUpdate(resource));
        }

        return taskResources;
    }

    public DevelopTaskResource addOrUpdate(DevelopTaskResource developTaskResource) {
        if (developTaskResource.getId() > 0) {
            developTaskResourceDao.updateById(developTaskResource);
        } else {
            developTaskResource.setIsDeleted(Deleted.NORMAL.getStatus());
            developTaskResourceDao.insert(developTaskResource);
        }
        return developTaskResource;
    }

    /**
     * 根据 任务Id、资源类型 查询
     *
     * @param taskId       任务ID
     * @param resourceType 资源类型
     * @return
     */
    public Integer deleteByTaskId(Long taskId, Integer resourceType) {
        return developTaskResourceDao.deleteByTaskId(taskId, resourceType);
    }

}
