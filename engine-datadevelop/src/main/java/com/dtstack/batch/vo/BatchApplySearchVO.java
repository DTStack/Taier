package com.dtstack.batch.vo;

import lombok.Data;

import java.util.List;

/**
 * @author jiangbo
 * @date 2018/5/24 10:07
 */
@Data
public class BatchApplySearchVO {

    private Long tenantId;

    private Long userId;

    private Integer listType = 0;

    private Integer pageIndex = 1;

    private Integer pageSize = 10;

    private String resourceName;

    private Long startTime;

    private Long endTime;

    private Long belongProjectId;

    private String sortColumn = "gmt_modified";

    private String sort = "desc";

    private Long applyUserId;

    private List<Integer> status;

    private Integer tableType;
}
