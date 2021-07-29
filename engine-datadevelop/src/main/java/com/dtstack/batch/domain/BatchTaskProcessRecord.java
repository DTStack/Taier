package com.dtstack.batch.domain;

import lombok.Data;

/**
 * @author jiangbo
 * @date 2018/6/15 21:33
 */
@Data
public class BatchTaskProcessRecord {

    private Long id;

    private String jobId;

    private Integer status;
}
