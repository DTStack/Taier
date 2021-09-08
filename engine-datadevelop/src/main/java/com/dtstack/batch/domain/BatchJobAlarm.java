package com.dtstack.batch.domain;

import com.dtstack.engine.domain.BaseEntity;
import lombok.Data;

/**
 * Reason:
 * Date: 2017/6/19
 * Company: www.dtstack.com
 * @ahthor xuchao
 */
@Data
public class BatchJobAlarm  extends BaseEntity {

    private String jobId;

    private Long taskId;

    private Integer taskStatus;

}
