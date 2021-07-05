package com.dtstack.engine.datadevelop.service.task.impl;

import com.dtstack.batch.dao.BatchTaskResourceShadeDao;
import com.dtstack.batch.domain.BatchResource;
import com.dtstack.batch.domain.BatchTaskResource;
import com.dtstack.batch.domain.BatchTaskResourceShade;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Reason:
 * Date: 2017/8/23
 * Company: www.dtstack.com
 * @author xuchao
 */

@Service
public class BatchTaskResourceShadeService {

    private static Logger logger = LoggerFactory.getLogger(com.dtstack.batch.service.task.impl.BatchTaskResourceShadeService.class);

    @Autowired
    private BatchTaskResourceShadeDao batchTaskResourceShadeDao;

    public void clearDataByTaskId(Long taskId) {
        batchTaskResourceShadeDao.deleteByTaskId(taskId);
        logger.info(String.format("clear taskResource success  taskId = %s",taskId));

    }

    public void saveTaskResource(List<BatchTaskResource> taskResourceList) {
        for (BatchTaskResource resource : taskResourceList) {
            BatchTaskResourceShade shade = new BatchTaskResourceShade();
            BeanUtils.copyProperties(resource, shade);
            addOrUpdate(shade);
        }
    }

    public void addOrUpdate(BatchTaskResourceShade batchTaskResourceShade) {
        if (batchTaskResourceShadeDao.getOne(batchTaskResourceShade.getId()) != null) {
            batchTaskResourceShadeDao.update(batchTaskResourceShade);
        } else {
            batchTaskResourceShadeDao.insert(batchTaskResourceShade);
        }
    }

    public List<Long> getResourceIdList(long taskId, long projectId, int type) {
        List<BatchTaskResourceShade> resourceList = batchTaskResourceShadeDao.listByTaskId(taskId, type, projectId);
        List<Long> resultIdList = Lists.newArrayList();
        if (resourceList == null) {
            return resultIdList;
        }

        resourceList.forEach(record -> resultIdList.add(record.getResourceId()));
        return resultIdList;
    }

    public List<BatchResource> listResourceByTaskId (Long taskId, Integer resourceType, Long projectId) {
        List<BatchResource> resourceList = batchTaskResourceShadeDao.listResourceByTaskId(taskId, resourceType, projectId);
        if (CollectionUtils.isEmpty(resourceList)) {
            return new ArrayList<>();
        } else {
            return resourceList;
        }
    }

    public void deleteByProjectId(Long projectId) {
        batchTaskResourceShadeDao.deleteByProjectId(projectId);
    }
}
