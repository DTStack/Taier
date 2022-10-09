package com.dtstack.taier.datasource.api.dto;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 11:58 2020/9/12
 * @Description：表信息
 */
@Data
public class Table {
    /**
     * 表所在数据库
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
     * 字段
     */
    private List<ColumnMetaDTO> columns = Lists.newArrayList();

    /**
     * 是否是事务表
     */
    private Boolean isTransTable = false;

    /**
     * 是否是视图
     */
    private Boolean isView = false;

    /**
     * 是否是分区表
     */
    private Boolean isPartitionTable = false;

    /**
     * 是否是单字符作为分隔符
     */
    private Boolean isLazySimpleSerDe = true;
}
