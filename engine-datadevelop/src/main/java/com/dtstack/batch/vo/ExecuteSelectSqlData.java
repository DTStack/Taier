
package com.dtstack.batch.vo;

import com.dtstack.batch.domain.BatchHiveSelectSql;
import com.dtstack.batch.domain.BatchTask;
import com.dtstack.batch.service.job.IBatchSelectSqlService;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExecuteSelectSqlData {

    /**
     * 查询sql信息
     */
    private BatchHiveSelectSql batchHiveSelectSql;

    /**
     * 任务信息
     */
    private BatchTask batchTask;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * sql执行Service
     */
    private IBatchSelectSqlService iBatchSelectSqlService;

}
