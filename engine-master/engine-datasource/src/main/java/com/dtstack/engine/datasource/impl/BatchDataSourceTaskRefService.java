//package com.dtstack.batch.service.datasource.impl;
//
//import com.dtstack.batch.dao.BatchDataSourceTaskRefDao;
//import com.dtstack.batch.domain.BatchDataSourceTaskRef;
//import com.dtstack.batch.domain.BatchTask;
//import com.dtstack.dtcenter.common.annotation.Forbidden;
//import com.dtstack.dtcenter.common.enums.Deleted;
//import org.apache.commons.collections.CollectionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.util.List;
//
///**
// * company: www.dtstack.com
// * author: toutian
// * create: 2021/4/16
// */
//@Service
//public class BatchDataSourceTaskRefService {
//
//    @Autowired
//    private BatchDataSourceTaskRefDao dataSourceTaskRefDao;
//
//    @Forbidden
//    public void addRef(Long dataSourceId, long taskId, Long projectId, Long tenantId) {
//        BatchDataSourceTaskRef ref = dataSourceTaskRefDao.getBySourceIdAndTaskId(dataSourceId, taskId);
//        Timestamp current = Timestamp.valueOf(LocalDateTime.now());
//
//        if (ref == null) {//不存在则插入
//            ref = new BatchDataSourceTaskRef();
//            ref.setDataSourceId(dataSourceId);
//            ref.setTaskId(taskId);
//            ref.setProjectId(projectId);
//            ref.setTenantId(tenantId);
//            ref.setGmtCreate(current);
//            ref.setGmtModified(current);
//            ref.setIsDeleted(Deleted.NORMAL.getStatus());
//        } else {//更新修改时间
//            ref.setGmtModified(current);
//            ref.setTenantId(tenantId);
//            ref.setProjectId(projectId);
//        }
//
//        addOrUpdate(ref);
//    }
//
//    public void deleteByProjectId(Long projectId) {
//        dataSourceTaskRefDao.deleteByProjectId(projectId);
//    }
//
//    @Forbidden
//    public BatchDataSourceTaskRef addOrUpdate(BatchDataSourceTaskRef batchDataSourceTaskRef) {
//        if (batchDataSourceTaskRef.getId() > 0) {
//            dataSourceTaskRefDao.update(batchDataSourceTaskRef);
//        } else {
//            dataSourceTaskRefDao.insert(batchDataSourceTaskRef);
//        }
//        return batchDataSourceTaskRef;
//    }
//
//    @Forbidden
//    public void removeRef(Long taskId) {
//        if (taskId != null && taskId > 0){
//            dataSourceTaskRefDao.deleteByTaskId(taskId);
//        }
//    }
//
//    @Forbidden
//    public Integer getSourceRefCount(long dataSourceId) {
//        return dataSourceTaskRefDao.countBySourceId(dataSourceId,null);
//    }
//
//    @Forbidden
//    public void copyTaskDataSource(Long srcTaskId, BatchTask distTask){
//        List<Long> sourceIds = dataSourceTaskRefDao.listSourceIdByTaskId(srcTaskId);
//        if(CollectionUtils.isNotEmpty(sourceIds)){
//            for (Long sourceId : sourceIds) {
//                addRef(sourceId,distTask.getId(),distTask.getProjectId(),distTask.getTenantId());
//            }
//        }
//    }
//}
