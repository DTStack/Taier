package com.dtstack.taier.datasource.api.dto;

import lombok.Data;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 15:22 2020/2/26
 * @Description：字段信息
 */
@Data
public class ColumnMetaDTO {
    /**
     * 字段名称
     */
    private String key;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 字段注释
     */
    private String comment;

    /**
     * 是否分区字段
     */
    private Boolean part = false;

    /**
     * 小数点右边的指定列的位数
     */
    private Integer scale;

    /**
     * 指定列的指定列大小
     */
    private Integer precision;
}
