package com.dtstack.batch.vo;

import com.dtstack.batch.web.pager.PageResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author jiangbo
 * @time 2017/12/11
 */
@Data
public class BatchTableBloodVO {

    @JsonProperty("belongProjectId")
    private Long belongProjectId;

    private Long dataSourceId;

    private String dataSource;

    private String tableName;

    private Long tableId;

    private Integer dataSourceType;

    private List<String> columns;

    private List<BatchTableBloodVO> parentTables;

    private List<BatchTableBloodVO> childTables;

    private PageResult<List<BatchTableBloodVO>> parentResult;

    private PageResult<List<BatchTableBloodVO>> childResult;
}
