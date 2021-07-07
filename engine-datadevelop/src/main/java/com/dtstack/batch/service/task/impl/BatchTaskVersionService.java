package com.dtstack.batch.service.task.impl;

import com.dtstack.batch.dao.BatchTaskVersionDao;
import com.dtstack.batch.domain.BatchTaskVersion;
import com.dtstack.batch.domain.BatchTaskVersionDetail;
import com.dtstack.batch.web.pager.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchTaskVersionService {

    @Autowired
    private BatchTaskVersionDao batchTaskVersionDao;

    /**
     * 根据taskId 查询记录
     * @param taskId
     * @param pageQuery
     * @return
     */
    public List<BatchTaskVersionDetail> listByTaskId(Long taskId, PageQuery pageQuery) {
        return batchTaskVersionDao.listByTaskId(taskId, pageQuery);
    }

    /**
     * 单条记录插入
     * @param batchTaskVersion
     * @return
     */
    public Integer insert(BatchTaskVersion batchTaskVersion) {
        return batchTaskVersionDao.insert(batchTaskVersion);
    }

    /**
     * 根据versionId 查询记录
     * @param versionId
     * @return
     */
    public BatchTaskVersionDetail getByVersionId(Long versionId){
        return batchTaskVersionDao.getByVersionId(versionId);
    }

    /**
     * 根据versionIds 查询记录
     * @param versionId
     * @return
     */
    public List<BatchTaskVersionDetail> getByVersionIds(List<Integer> versionId){
        return batchTaskVersionDao.getByVersionIds(versionId);
    }

    /**
     * 根据taskIds 查询记录
     * @param taskIds
     * @return
     */
    public List<BatchTaskVersionDetail> getByTaskIds(List<Long> taskIds){
        return batchTaskVersionDao.getByTaskIds(taskIds);
    }

    /**
     * 根据taskIds 查询记录 不返回sqlText
     * @param taskIds
     * @return
     */
    public List<BatchTaskVersionDetail> getWithoutSqlByTaskIds(List<Long> taskIds){
        return batchTaskVersionDao.getWithoutSqlByTaskIds(taskIds);
    }

    /**
     * 根据taskIds 查询记录  返回最后的版本信息
     * @param taskIds
     * @return
     */
    public List<BatchTaskVersionDetail> getLatestTaskVersionByTaskIds(List<Long> taskIds){
        return batchTaskVersionDao.getLatestTaskVersionByTaskIds(taskIds);
    }

    /**
     * 根据taskId 获取 版本最大值
     * @param taskId
     * @return
     */
    public Integer getMaxVersionId(Long taskId){
        return batchTaskVersionDao.getMaxVersionId(taskId);
    }

    /**
     * 根据taskId 和版本id 获取固定记录的
     * @param taskId
     * @param versionId
     * @return
     */
    public BatchTaskVersionDetail getBytaskIdAndVersionId(Long taskId, Long versionId){
        return batchTaskVersionDao.getBytaskIdAndVersionId(taskId, versionId);
    }

    /**
     * 根据taskId versionId 查询固定记录
     * @param taskId
     * @param version
     * @return
     */
    public BatchTaskVersion getByTaskIdAndVersion(Long taskId, Integer version){
        return batchTaskVersionDao.getByTaskIdAndVersion(taskId, version);
    }

    /**
     * 根据projectId 删除记录
     * @param projectId
     * @return
     */
    public Integer deleteByProjectId(Long projectId){
        return batchTaskVersionDao.deleteByProjectId(projectId);
    }
}
