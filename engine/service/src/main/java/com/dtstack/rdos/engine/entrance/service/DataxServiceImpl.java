package com.dtstack.rdos.engine.entrance.service;

import com.dtstack.rdos.common.annotation.Param;
import com.dtstack.rdos.engine.db.dao.RdosBatchJobDAO;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/31
 */
public class DataxServiceImpl {


    private RdosBatchJobDAO rdosBatchJobDAO = new RdosBatchJobDAO();


    public void notify(@Param("jobid") String jobid,
                       @Param("status") String status) throws Exception {

        Integer taskStatus = RdosTaskStatus.FAILED.getStatus();
        if (status != null && status.equals("0")) {
            taskStatus = RdosTaskStatus.FINISHED.getStatus();
        }

        rdosBatchJobDAO.updateJobStatus(jobid, taskStatus);
    }
}
