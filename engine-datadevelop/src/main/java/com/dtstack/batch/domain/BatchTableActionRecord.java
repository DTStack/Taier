package com.dtstack.batch.domain;

import lombok.Data;

/**
 * company: www.dtstack.com
 * author: jiangbo
 * create: 2017/7/5
 */
@Data
public class BatchTableActionRecord extends TenantProjectEntity{

    private Long tableId;

    private Long userId;

    private String actionSql;

    private String operate;

    private String userName;
}
