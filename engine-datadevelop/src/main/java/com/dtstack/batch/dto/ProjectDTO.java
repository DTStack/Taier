package com.dtstack.batch.dto;


import com.dtstack.batch.domain.Project;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

/**
 * @author sanyue
 */
@Data
public class ProjectDTO extends Project {

    private Integer jobSum = 0;

    private Integer tableCount;

    private String totalSize;

    private Map<String, Integer> taskCountMap;

    private Timestamp stick;

    private Integer stickStatus;

}
