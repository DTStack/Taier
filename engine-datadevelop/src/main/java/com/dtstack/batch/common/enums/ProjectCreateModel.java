package com.dtstack.batch.common.enums;

/**
 * 项目创建模式
 * @author sanyue
 * @date 2018/12/17
 */
public enum ProjectCreateModel {
    //默认：创建新的db
    DEFAULT(0),
    //导入已有的db
    intrinsic(1);

    private Integer type;

    ProjectCreateModel(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
