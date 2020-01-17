package com.dtstack.task.server.bo;

import com.dtstack.dtcenter.base.domain.TenantProjectEntity;

/**
 * @author sishu.yss
 */
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

    public Long getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getOriginFileName() {
        return originFileName;
    }

    public void setOriginFileName(String originFileName) {
        this.originFileName = originFileName;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getNodePid() {
        return nodePid;
    }

    public void setNodePid(Long nodePid) {
        this.nodePid = nodePid;
    }

    public String getResourceDesc() {
        return resourceDesc;
    }

    public void setResourceDesc(String resourceDesc) {
        this.resourceDesc = resourceDesc;
    }
}
