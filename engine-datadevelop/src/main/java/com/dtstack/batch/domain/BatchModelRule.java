package com.dtstack.batch.domain;

import com.dtstack.engine.api.domain.TenantProjectEntity;
import lombok.Data;

/**
 * @author sanyue
 */
@Data
public class BatchModelRule extends TenantProjectEntity {
    /**
     * 建表规则配置  ModelTableRule type的自定义排序
     */
    private String rule;

    /**
     * 最后修改人id
     */
    private Long modifyUserId;

    /**
     * 创建者用户id
     */
    private Long createUserId;

}
