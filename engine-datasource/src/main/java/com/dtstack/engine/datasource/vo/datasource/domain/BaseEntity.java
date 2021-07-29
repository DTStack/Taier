package com.dtstack.engine.datasource.vo.datasource.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class BaseEntity implements Serializable {

    @Builder.Default
    private Long id = 0L;

    /**
     * 创建时间
     */
    private Timestamp gmtCreate;

    /**
     * 修改时间
     */
    private Timestamp gmtModified;

    /**
     * 是否删除
     */
    @Builder.Default
    private Integer isDeleted = 0;


}
