package com.dtstack.batch.dto;

import lombok.Data;

/**
 * 资源新增DTO
 */
@Data
public class BatchResourceAddDTO {

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 资源ID
     */
    private Long id;

    /**
     * 资源描述
     */
    private String resourceDesc;

    /**
     * 资源存放的目录ID
     */
    private Long nodePid;

    /**
     * uic租户ID
     */
    private Long dtuicTenantId =1L;

    /**
     * 资源类型
     */
    private Integer resourceType;

    /**
     * 资源原始名称
     */
    private String originalFilename;

    /**
     * 资源临时存放地址
     */
    private String tmpPath;

    /**
     * 资源路径
     */
    private String url;

    /**
     * 新建资源的用户ID
     */
    private Long createUserId;
}
