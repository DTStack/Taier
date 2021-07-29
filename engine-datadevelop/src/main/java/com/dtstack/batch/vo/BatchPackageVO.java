package com.dtstack.batch.vo;

import com.dtstack.batch.domain.BatchPackage;
import lombok.Data;

import java.util.List;

@Data
public class BatchPackageVO extends BatchPackage {

    private String applyUser;

    private String publishUser;

    private List<BatchPackageItemVO> items;

    private Boolean isBinding;

    public List<BatchPackageItemVO> getItems() {
        return items;
    }

    public void setItems(List<BatchPackageItemVO> items) {
        this.items = items;
    }

    public String getApplyUser() {
        return applyUser;
    }

    public void setApplyUser(String applyUser) {
        this.applyUser = applyUser;
    }

    public String getPublishUser() {
        return publishUser;
    }

    public void setPublishUser(String publishUser) {
        this.publishUser = publishUser;
    }

    public Boolean getIsBinding() {
        return isBinding;
    }

    public void setIsBinding(Boolean isBinding) {
        this.isBinding = isBinding;
    }

    @Override
    public String toString() {
        return "BatchPackageVO{" +
                "id='" + getId() + '\'' +
                "TenantId='" + getTenantId() + '\'' +
                "ProjectId='" + getProjectId() + '\'' +
                "isBinding='" + getIsBinding() + '\'' +
                "Name='" + getName() + '\'' +
                "Comment='" + getComment() + '\'' +
                "log='" + getLog() + '\'' +
                "GmtCreate='" + getGmtCreate() + '\'' +
                "GmtModified='" + getGmtModified() + '\'' +
                "Status='" + getStatus() + '\'' +
                "applyUser='" + applyUser + '\'' +
                ", publishUser='" + publishUser + '\'' +
                '}';
    }
}
