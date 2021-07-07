package com.dtstack.batch.vo;

import lombok.Data;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/5/29 10:04
 * @Description:
 */
@Data
public class TenantVO {

    private String tenantName;

    private Long tenantId;

    private Boolean current;

    private Long uicTenantId;

}
