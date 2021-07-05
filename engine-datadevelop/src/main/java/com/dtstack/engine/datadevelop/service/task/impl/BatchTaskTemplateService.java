package com.dtstack.engine.datadevelop.service.task.impl;

import com.dtstack.batch.dao.BatchTaskTemplateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BatchTaskTemplateService {

    @Autowired
    private BatchTaskTemplateDao batchTaskTemplateDao;

    /**
     * 根据条件 获取模版
     *
     * @param taskType
     * @param type
     * @return
     */
    public String getContentByType(Integer taskType, Integer type) {
        return batchTaskTemplateDao.getContentByType(taskType, type);
    }
}
