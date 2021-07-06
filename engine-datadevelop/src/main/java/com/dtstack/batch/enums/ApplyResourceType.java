package com.dtstack.batch.enums;

/**
 * @author jiangbo
 * @time 2018/5/19
 */
public enum ApplyResourceType {

    /**
     * table
     */
    TABLE(0),

    /**
     * function
     */
    FUNCTION(1),

    /**
     * resource
     */
    RESOURCE(2);

    private Integer type;

    ApplyResourceType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
