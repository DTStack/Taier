package com.dtstack.batch.service.impl;

import com.dtstack.batch.dao.BatchFunctionResourceDao;
import com.dtstack.batch.domain.BatchFunctionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchFunctionResourceService {

    @Autowired
    private BatchFunctionResourceDao batchFunctionResourceDao;


    /**
     * 根据functionId 获取 和资源的关联关系
     *
     * @param functionId
     * @return
     */
    public List<BatchFunctionResource> listByFunctionId(Long functionId) {
        return batchFunctionResourceDao.listByFunctionId(functionId);
    }

    /**
     * 新增记录
     *
     * @param batchFunctionResource
     */
    public void insert(BatchFunctionResource batchFunctionResource) {
        batchFunctionResourceDao.insert(batchFunctionResource);
    }

    /**
     * 根据functionId 更新记录
     *
     * @param batchFunctionResource
     */
    public void updateByFunctionId(BatchFunctionResource batchFunctionResource) {
        batchFunctionResourceDao.updateByFunctionId(batchFunctionResource);
    }

    /**
     * 根据函数id获取函数资源关联关系
     *
     * @param functionId
     * @return
     */
    public BatchFunctionResource getResourceFunctionByFunctionId(Long functionId) {
        return batchFunctionResourceDao.getResourceFunctionByFunctionId(functionId);
    }

    /**
     * 根据projectId 删除记录
     *
     * @param projectId
     * @return
     */
    public Integer deleteByProjectId(Long projectId) {
        return batchFunctionResourceDao.deleteByProjectId(projectId);
    }

    /**
     * 根据functionId 删除记录
     *
     * @param functionId
     */
    public void deleteByFunctionId(Long functionId) {
        batchFunctionResourceDao.deleteByFunctionId(functionId);
    }

    /**
     * 根据 functionId 和 resourceId 获取关联记录
     *
     * @param resourceId
     * @param functionId
     * @return
     */
    public BatchFunctionResource getBeanByResourceIdAndFunctionId(Long resourceId, Long functionId) {
        return batchFunctionResourceDao.getBeanByResourceIdAndFunctionId(resourceId, functionId);
    }

    /**
     * 根据资源id 获取列表
     * @param resourceId
     * @return
     */
    public  List<BatchFunctionResource> listByResourceId(Long resourceId) {
        return batchFunctionResourceDao.listByResourceId(resourceId);
    }

    /**
     * 根据resource_Id  获取列表
     * @param resource_Id
     * @return
     */
    public List<BatchFunctionResource> listByFunctionResourceId(Long resource_Id) {
        return batchFunctionResourceDao.listByFunctionResourceId(resource_Id);
    }
}
