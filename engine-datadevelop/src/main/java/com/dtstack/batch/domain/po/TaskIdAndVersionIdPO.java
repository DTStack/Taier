package com.dtstack.batch.domain.po;

import lombok.Data;

@Data
public class TaskIdAndVersionIdPO {

    private Long taskId;

    private Long versionId;

    private Long projectId;
}
