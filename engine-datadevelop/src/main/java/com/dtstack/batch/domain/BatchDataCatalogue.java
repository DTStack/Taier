package com.dtstack.batch.domain;

import com.dtstack.engine.domain.BaseEntity;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 数据类目
 * @author sanyue
 */
@Data
public class BatchDataCatalogue extends BaseEntity {

    private Long tenantId;

    private String nodeName;

    private Long nodePid;

    private Integer orderVal;

    private String path;

    private Integer level;

    private Long createUserId;

    public BatchDataCatalogue() {
    }

    public BatchDataCatalogue(Long id, Long nodePid, String nodeName, Timestamp gmtModified) {
        this.setId(id);
        this.nodePid = nodePid;
        this.nodeName = nodeName;
        this.setGmtModified(gmtModified);
    }

}
