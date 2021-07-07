package com.dtstack.batch.vo;

import lombok.Data;

/**
 * 表字段元数据信息
 *
 * date: 2021/6/16 5:53 下午
 * author: zhaiyue
 */
@Data
public class BatchTableColumnMetaInfoDTO {

    /**
     * 字段名
     */
    private String key;

    /**
     * 类型
     */
    private String type;

    /**
     * 备注
     */
    private String comment;

    /**
     * 是否是分区字段
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
