package com.dtstack.taier.datasource.api.dto;

import lombok.Data;

/**
 * 数据库信息
 *
 * @author ：wangchuan
 * date：Created in 下午1:56 2021/9/14
 * company: www.dtstack.com
 */
@Data
public class Database {

    /**
     * 数据库名称
     */
    private String DbName;

    /**
     * 数据库注释
     */
    private String comment;

    /**
     * 数据库存储位置
     */
    private String location;

    /**
     * 所有者名称
     */
    private String ownerName;

}
