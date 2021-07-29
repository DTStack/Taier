package com.dtstack.batch.vo;

import lombok.Data;

@Data
public class CheckSyntaxResult {

    private Boolean checkResult;

    private String message;

    private String sql;
}
