package com.dtstack.batch.domain;

import com.dtstack.engine.api.domain.TenantProjectEntity;
import lombok.Data;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/6/15 7:36 下午
 */
@Data
public class BatchDataSourceCenter extends TenantProjectEntity {

    /**
     * 数据源中心的id
     */
    private Long dtCenterSourceId;

    /**
     * 新建用户id
     */
    private Long createUserId;


    /**
     * 修改用户id
     */
    private Long modifyUserId;

    /**
     * 是否meta数据源，0=否，1=是
     */
    private Integer isDefault;

}
