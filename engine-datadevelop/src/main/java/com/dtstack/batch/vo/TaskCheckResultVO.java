package com.dtstack.batch.vo;

import lombok.Data;

@Data
public class TaskCheckResultVO {

    /**
     * 从boolean转为integer 用于后期排查错误
     * 0 无错误 1 权限校验错误 2 语法校验错误
     */
    private Integer errorSign;

    private String errorMessage;

}
