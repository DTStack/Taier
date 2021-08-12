package com.dtstack.batch.domain;

import com.dtstack.engine.api.domain.TenantProjectEntity;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: jiangbo
 * create: 2017/7/12.
 */
@Data
public class BatchTableProjectDataHistory extends TenantProjectEntity {

    private Integer tableNum;

    private String projectSize;

}
