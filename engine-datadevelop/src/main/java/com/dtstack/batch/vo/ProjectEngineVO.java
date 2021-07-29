package com.dtstack.batch.vo;

import com.dtstack.batch.common.enums.ProjectCreateModel;
import lombok.Data;

/**
 * 项目关联的引擎信息
 * Date: 2019/6/1
 * Company: www.dtstack.com
 * @author xuchao
 */
@Data
public class ProjectEngineVO {

    /**
     * 引擎类型：{@link com.dtstack.dtcenter.common.enums.MultiEngineType}
     */
    private Integer engineType;

    /**
     * 数据库名称
     */
    private String database;

    /**
     *创建项目的方式
     * {@link ProjectCreateModel}
     */
    private Integer createModel;

    /**
     * 生命周期
     */
    private Integer lifecycle;

    private Long catalogueId;
}
