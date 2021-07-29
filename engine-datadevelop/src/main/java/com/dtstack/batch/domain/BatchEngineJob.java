package com.dtstack.batch.domain;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author jiangbo
 * @time 2017/12/25
 */
@Data
public class BatchEngineJob extends BaseEntity {

    private Integer status;

    private String jobId;

    private String engineJobId;

    private Timestamp execStartTime;

    private Timestamp execEndTime;

    private Integer execTime;

    private String logInfo;

    private String engineLog;

}

