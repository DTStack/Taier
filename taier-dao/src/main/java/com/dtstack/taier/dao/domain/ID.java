package com.dtstack.taier.dao.domain;

import java.io.Serializable;

/**
 * ID接口,内置实体主键ID
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/15
 */
@FunctionalInterface
public interface ID<PK extends Serializable> {
    /**
     * 获取主键ID
     *
     * @return
     */
    PK getId();
}
