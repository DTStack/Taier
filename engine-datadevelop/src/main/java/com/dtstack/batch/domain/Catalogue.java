package com.dtstack.batch.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/4
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
public class Catalogue extends TenantProjectEntity {

    /**
     * 文件夹名
     */
    private String nodeName;

    /**
     * 父文件夹
     */
    private Long nodePid;

    /**
     * 创建用户
     */
    private Long createUserId;

    /**
     * 目录层级
     */
    private Integer level;


    /**
     * 引擎类型
     */
    private Integer engineType;


    private Integer orderVal;

    private Integer catalogueType;

}
