package com.dtstack.batch.dto;

import com.dtstack.batch.domain.BatchDataSource;
import lombok.Data;

import java.util.List;

@Data
public class BatchDataSourceDTO extends BatchDataSource {
    private String fuzzName;

    private String jdbcUrl;

    private String password;

    private String userName;

    /**
     * 数据源类型列表
     */
    private List<Integer> types;

}
