package com.dtstack.batch.domain;

import lombok.Data;

/**
 * @author sishu.yss
 */
@Data
public class BatchResource extends TenantProjectEntity {
    public BatchResource(String url) {
        this.url = url;
    }

    public BatchResource() {

    }

    /**
     * 资源路径
     */
    private String url;

    /**
     * 资源类型 1,jar 2 sql
     */
    private Integer resourceType;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 源文件名
     */
    private String originFileName;

    private Long createUserId;

    private Long modifyUserId;

    private Long nodePid;

    private String resourceDesc;

}
