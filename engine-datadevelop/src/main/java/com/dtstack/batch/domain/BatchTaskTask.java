package com.dtstack.batch.domain;

import lombok.Data;

/**
 * @author sishu.yss
 */
@Data
public class BatchTaskTask extends TenantProjectEntity {

    private Long taskId;

    private Long parentTaskId;

    private Integer parentAppType;

}
