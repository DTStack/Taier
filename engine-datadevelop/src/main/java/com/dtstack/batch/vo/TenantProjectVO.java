package com.dtstack.batch.vo;

import lombok.Data;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/5/29 14:25
 * @Description:
 */
@Data
public class TenantProjectVO {

    private Long projectId;

    private String projectName;

    private String projectAlias;

    private Long tenantId;

    private Integer projectType;

    private Integer status;

}
