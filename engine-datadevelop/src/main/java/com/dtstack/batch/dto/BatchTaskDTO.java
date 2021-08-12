package com.dtstack.batch.dto;


import com.dtstack.engine.api.domain.BatchTask;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class BatchTaskDTO extends BatchTask {
    private Timestamp startGmtModified;
    private Timestamp endGmtModified;
    private String fuzzName;
    private List<Integer> taskTypeList;
    private List<Integer> periodTypeList;
    private Integer searchType;
}
