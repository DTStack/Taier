package com.dtstack.batch.service.table.impl;

import com.dtstack.batch.dao.BatchHiveSelectSqlDao;
import com.dtstack.batch.domain.BatchHiveSelectSql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BatchHiveSelectSqlService {

    @Autowired
    private BatchHiveSelectSqlDao batchHiveSelectSqlDao;

    public void deleteByProjectId(Long projectId) {
        batchHiveSelectSqlDao.deleteByProjectId(projectId);
    }

    /**
     * 添加记录
     *
     * @param batchHiveSelectSql
     */
    public void insert(BatchHiveSelectSql batchHiveSelectSql){
        batchHiveSelectSqlDao.insert(batchHiveSelectSql);
    }
}
