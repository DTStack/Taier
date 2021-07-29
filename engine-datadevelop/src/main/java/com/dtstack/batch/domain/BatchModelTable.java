package com.dtstack.batch.domain;

import lombok.Data;

/**
 * @author sanyue
 */
@Data
public class BatchModelTable extends TenantProjectEntity {
    /**
     * 1 层级 2 主题域 3 刷新频率 4 增量定义',
     */
    private Integer type;
    /**
     * 层级编号
     */
    private Integer level;
    /**
     * '名称定义'
     */
    private String name;
    /**
     * 说明
     */
    private String modelDesc;
    /**
     * 前缀标识
     */
    private String prefix;
    /**
     * '生命周期  单位：天',
     */
    private Integer lifeDay;
    /**
     * 是否层级依赖',
     */
    private Integer depend;
    /**
     * 最近修改人id
     */
    private Long modifyUserId;

    /**
     * 创建者用户id
     */
    private Long createUserId;

}
