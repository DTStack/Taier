package com.dtstack.batch.domain;

import com.dtstack.engine.api.domain.TenantProjectEntity;
import lombok.Data;

/**
 * @author : toutian
 */
@Data
public class BatchTaskVersion extends TenantProjectEntity {

    private Long taskId;

    private String originSql;

    /**
     * 'sql 文本'
     */
    private String sqlText;

    /**
     * 'sql 文本'
     */
    private String publishDesc;

    /**
     * 新建task的用户
     */
    private Long createUserId;

    /**
     * 'task版本'
     */
    private Integer version;


    /**
     * 环境参数
     */
    private String taskParams;
    /**
     * 调度信息
     */
    private String scheduleConf;

    private Integer scheduleStatus;
    /**
     * 依赖的任务id
     */
    private String dependencyTaskIds;

}


