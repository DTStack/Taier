package com.dtstack.batch.domain;

import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/10
 */
@Data
public class BatchDataSource extends TenantProjectEntity {

    /**
     * '数据源名称'
     */
    private String dataName;

    /**
     * 数据源描述
     */
    private String dataDesc;

    /**
     * 加密的数据源信息
     */
    private String dataJson;

    /**
     * 数据源类型
     */
    private Integer type;

    /**
     * 新建用户id
     */
    private Long createUserId;


    /**
     * 修改用户id
     */
    private Long modifyUserId;

    /**
     * 是否启用
     */
    private Integer active;

    /**
     * 连接是否可用
     */
    private Integer linkState;

    /**
     * 是不是项目下的默认数据库
     */
    private Integer isDefault;

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }
}
