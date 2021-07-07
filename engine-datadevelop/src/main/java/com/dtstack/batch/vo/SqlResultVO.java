package com.dtstack.batch.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author yunliu
 * @date 2020-04-27 09:26
 * @description
 */
@Data
@Accessors(chain = true)
public class SqlResultVO<T> {

    /**
     * sql对应的id
     */
    private String sqlId;

    /**
     * 类型
     */
    private Integer type;

    private List<T> result;

    private String msg;

    private String sqlText;
}
