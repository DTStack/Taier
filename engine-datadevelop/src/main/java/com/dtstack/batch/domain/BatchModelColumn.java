package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

/**
 * @author sanyue
 */
@Data
public class BatchModelColumn extends TenantProjectEntity {
    /**
     * 类型
     */
    private Integer type;
    /**
     * 指标类型
     */
    private Integer columnType;
    /**
     * 指标命名
     */
    private String columnName;
    /**
     * '指标名称'
     */
    private String columnNameZh;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 指标口径
     */
    private String modelDesc;
    /**
     * '最近修改人id'
     */
    private Long modifyUserId;

    /**
     * 创建者用户id
     */
    private Long createUserId;

}
