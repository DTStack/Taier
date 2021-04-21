package com.dtstack.engine.api.vo.lineage.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author: ZYD
 * Date: 2021/4/21 10:01
 * Description: 删除数据源传参
 * @since 1.0.0
 */
@ApiModel("删除数据源传参")
public class DeleteDataSourceParam {

    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("平台类型")
    private Integer appType;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }
}
