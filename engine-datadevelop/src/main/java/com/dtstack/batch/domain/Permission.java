package com.dtstack.batch.domain;

import com.dtstack.engine.domain.BaseEntity;
import lombok.Data;

/**
 * @author toutian
 */
@Data
public class Permission extends BaseEntity {

    private String code;

    private String name;

    private String display;

    private Long parentId;

    private Integer type;

}
