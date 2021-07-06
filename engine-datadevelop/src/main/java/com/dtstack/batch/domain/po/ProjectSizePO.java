package com.dtstack.batch.domain.po;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jiangbo
 * @time 2017/12/27
 */
@Data
public class ProjectSizePO implements Serializable {

    private Long projectId;

    private Long tenantId;

    private long size;

    private int tableNum;

}
