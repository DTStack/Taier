/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.datasource.api.dto;

import com.dtstack.taier.datasource.api.enums.MatchType;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 14:40 2020/2/26
 * @Description：查询信息
 */
@Data
@Builder
public class SqlQueryDTO {
    /**
     * 查询 SQL
     */
    private String sql;

    /**
     * 多条 SQL 一起执行
     */
    private List<SqlMultiDTO> sqlMultiDTOList;

    /**
     * 当需要查询某个指定catalog下的表或其他操作时指定
     */
    private String catalog;

    /**
     * schema/db
     */
    private String schema;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表名称搜索
     */
    private String tableNamePattern;

    /**
     * 表名称匹配类型
     */
    private MatchType matchType;

    /**
     * 表类型 部分支持，建议只使用 view 这个字段
     * {@link java.sql.DatabaseMetaData#getTableTypes()}
     */
    private String[] tableTypes;

    /**
     * 字段名称
     */
    private List<String> columns;

    /**
     * 分区字段:值,用于hive分区预览数据、hbase自定义查询
     */
    private Map<String, String> partitionColumns;

    /**
     * 是否查询视图, 默认false不查询
     */
    private Boolean view;

    /**
     * 是否过滤分区字段，默认 false 不过滤
     */
    private Boolean filterPartitionColumns;

    /**
     * 预览条数，默认100
     */
    private Integer previewNum;

    /**
     * 预编译字段
     */
    private List<Object> preFields;

    /**
     * executorQuery查询超时时间,单位：秒
     */
    private Integer queryTimeout;

    /**
     * mongodb，executorQuery 分页查询，开始行
     */
    private Integer startRow;

    /**
     * mongodb，executorQuery 分页查询，限制条数
     */
    private Integer limit;

    /**
     * 是否设置 maxRow，默认 true
     */
    @Builder.Default
    private Boolean setMaxRow = true;

    /**
     * JDBC 每次读取数据的行数，使用 DBUtil.setFetchSize()
     */
    private Integer fetchSize;


    /**
     * 是否自动提交事务
     */
    @Builder.Default
    private Boolean autoCommit = true;


    /**
     * 是否回滚，autoCommit 必须为false
     */
    @Builder.Default
    private Boolean rollback = false;

    /**
     * 预编译字段 upsert 字段
     * time :2021-12-14 17:50:00
     */
    private List<List<Object>> preUpsertFields;

    /**
     * solr 自定义查询
     */
    private SolrQueryDTO solrQueryDTO;

    private Integer esCommandType;

    /**
     * hive sql拼接参数
     */
    private String hiveSubType;

    /**
     * sql 预处理, 在获取 connection 后执行, 目前仅支持 hive、spark
     */
    private List<String> preSqlList;

    public Boolean getView() {
        if (ArrayUtils.isEmpty(getTableTypes())) {
            return Boolean.TRUE.equals(view);
        }

        return Arrays.stream(getTableTypes()).filter(type -> "VIEW".equalsIgnoreCase(type)).findFirst().isPresent();
    }

    public Integer getPreviewNum() {
        if (this.previewNum == null) {
            return 100;
        }
        return previewNum;
    }

    public Boolean getFilterPartitionColumns() {
        return Boolean.TRUE.equals(filterPartitionColumns);
    }


}