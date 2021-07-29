package com.dtstack.batch.enums;

/**
 * @author jiangbo
 * @date 2018/5/22 20:33
 */
public enum TableListType {

    /**
     * 全部
     */
    ALL(0),

    /**
     * 我管理的表
     */
    MANAGED_BY_ME(1),

    /**
     * 被授权的表
     */
    PERMISSION_SUCCESS(2),

    /**
     * 我收藏的表
     */
    COLLECT(3);

    private Integer type;

    TableListType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
