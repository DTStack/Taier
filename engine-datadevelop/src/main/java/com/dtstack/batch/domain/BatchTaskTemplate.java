package com.dtstack.batch.domain;

import lombok.Data;

/**
 * @author sanyue
 */
@Data
public class BatchTaskTemplate extends BaseEntity{

    /**
     * 任务类型：EJobType
     */
    private Integer taskType;

    /**
     * 模版类型：TemplateCatalogue
     */
    private Integer type;

    private String content;
}
