package com.dtstack.batch.domain;

import lombok.Data;

@Data
public class BatchTableColumnExt extends BatchTableColumn {

    private String tableName;

    private String userName;

}
