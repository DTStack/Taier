package com.dtstack.taier.datasource.plugin.dorisrestful.request;

public interface HttpAPI {

    /**
     * 获取所有的库 cluster
     */
    String ALL_DATABASE = "/api/meta/namespaces/%s/databases";

    /**
     * 获取库下的表 cluster schema tableName
     */
    String ALL_TABLES = "/api/meta/namespaces/%s/databases/%s:%s/tables";

    /**
     * 获取元数据 cluster cluster schema
     */
    String COLUMN_METADATA = "/api/meta/namespaces/%s/databases/%s/tables/%s/schema";

    /**
     * 数据预览 cluster schema
     */
    String QUERY_DATA = "/api/query/%s/%s";

    /**
     * 获取指定数据库中，指定表的表结构信息，也可用来判断表是否存在
     */
    String QUERY_TABLE_STRUCTURE = "/api/meta/namespaces/%s/databases/%s:%s/tables/%s/schema";
}