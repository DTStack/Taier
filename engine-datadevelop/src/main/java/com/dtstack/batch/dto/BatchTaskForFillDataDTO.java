package com.dtstack.batch.dto;

import com.dtstack.engine.domain.TenantProjectEntity;
import com.dtstack.engine.domain.User;
import lombok.Data;

/**
 * 补数据任务所需的任务参数
 * -- ps 简化了batchTask
 *
 * @author sanyue
 * @date 2019/1/18
 */
@Data
public class BatchTaskForFillDataDTO extends TenantProjectEntity {

    private String name;

    private Integer taskType;

    private Long createUserId;

    private Long ownerUserId;

    private User createUser;

    private User ownerUser;

    private String scheduleConf;

}
