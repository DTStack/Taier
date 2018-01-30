package com.dtstack.rdos.engine.execution.mysql;

import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.mysql.executor.MysqlExeQueue;

/**
 * Reason:
 * Date: 2018/1/30
 * Company: www.dtstack.com
 * @author xuchao
 */

public class MysqlResourceInfo extends EngineResourceInfo {

    private MysqlExeQueue mysqlExeQueue;

    public MysqlResourceInfo(MysqlExeQueue mysqlExeQueue){
        this.mysqlExeQueue = mysqlExeQueue;
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return mysqlExeQueue.checkCanSubmit();
    }
}
