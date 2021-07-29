package com.dtstack.batch.vo;

import lombok.Data;

import java.util.List;

/**
 * 表元数据信息
 *
 * date: 2021/6/16 5:53 下午
 * author: zhaiyue
 */
@Data
public class BatchTableMetaInfoDTO {

    /**
     * 所在的db
     */
    private String db;

    /**
     * 所有者
     */
    private String owner;

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 最近访问时间
     */
    private String lastAccess;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String comment;

    /**
     * 分隔符，hive表属性
     */
    private String delim;

    /**
     * 存储格式，hive表属性
     */
    private String storeType;

    /**
     * 表路径
     */
    private String path;

    /**
     * 表类型:EXTERNAL-外部表，MANAGED-内部表
     */
    private String externalOrManaged;

    /**
     * 非分区字段
     */
    private List<BatchTableColumnMetaInfoDTO> columns;

    /**
     * 分区字段
     */
    private List<BatchTableColumnMetaInfoDTO> partColumns;

    /**
     * 是否是事务表
     */
    private Boolean isTransTable = false;

    /**
     * 是否是视图
     */
    private Boolean isView = false;

}
