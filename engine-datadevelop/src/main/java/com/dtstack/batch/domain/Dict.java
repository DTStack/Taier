package com.dtstack.batch.domain;

import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/7/18
 */
@Data
public class Dict extends BaseEntity {

    private Integer type;

    private String dictName;

    private Integer dictValue;

    private String dictNameZH;

    private String dictNameEN;

    private Integer dictSort;

}
