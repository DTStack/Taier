package com.dtstack.batch.domain;

import lombok.Data;

@Data
public class BatchPackageItem extends TenantProjectEntity {

    private Long id;

    private Long tenantId;

    private Long projectId;

    private Long packageId;

    /**
     * 存放的是task_id
     */
    private Long itemId;

    /**
     * '资源类型：0-任务，1-表，2-资源，3-函数'
     */
    private Integer itemType;

    private Integer itemInnerType;

    private String publishParam;

    /**
     * 0 未发布 1发布失败 2发布完成
     */
    private Integer status;

    private String log;

    /**
     * 0 一键发布  1导入导出
     */
    private Integer type;

    /**
     * 冗余字段
     */
    private String itemName;

}
