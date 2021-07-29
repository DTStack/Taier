package com.dtstack.batch.domain.po;

import com.dtstack.batch.domain.BatchCatalogue;
import lombok.Data;

@Data
public class ProjectCataloguePO extends BatchCatalogue {

    /**
     * projectId
     */
    private Long projectRealId;

    private String  projectName;

    private String projectAlias;

}
