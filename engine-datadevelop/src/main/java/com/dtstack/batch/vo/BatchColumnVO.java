package com.dtstack.batch.vo;

import lombok.Data;

/**
 * company: www.dtstack.com
 * author: jiangbo
 * create: 2017/7/20.
 */
@Data
public class BatchColumnVO {

    private Long id;
    private Long tableId;
    private String columnName;
    private String columnType;
    private String comment;
    private Integer columnIndex;
    private Integer charLen;
    private Integer varcharLen;
    private Integer precision;
    private Integer scale;

    /**
     * 0-需要脱敏字段  1-无需脱敏
     */
    private Integer needMask;
}
