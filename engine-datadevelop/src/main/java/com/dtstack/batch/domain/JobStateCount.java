package com.dtstack.batch.domain;

import com.dtstack.dtcenter.base.domain.TenantProjectEntity;
import lombok.Data;

/**
 * @author sanyue
 * @date 2019/2/25
 */
@Data
public class JobStateCount extends TenantProjectEntity {

    private Integer failed;

    private Integer running;

    private Integer waitengine;

    private Integer canceled;

    private Integer unsubmit;

    private Integer submitting;

    private Integer frozen;

    private Integer finished;

}
