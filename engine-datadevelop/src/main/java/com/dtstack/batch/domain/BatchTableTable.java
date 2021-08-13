package com.dtstack.batch.domain;

import com.dtstack.engine.api.domain.BaseEntity;
import lombok.Data;

/**
 * @author jiangbo
 * @time 2017/12/7
 */
@Data
public class BatchTableTable  extends BaseEntity {

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 数据源id，hive类型的id为-1
     */
    private Long dataSourceId;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 字段名称
     */
    private String col;

    /**
     * 数据来源表所属项目id
     */
    private Long inputProjectId;

    /**
     * 数据来源表的数据源id，hive为-1
     */
    private Long inputDataSourceId;

    /**
     * 数据来源表的数据类型
     */
    private String inputTableName;

    /**
     * 数据来源表的数据类型
     */
    private String inputCol;

    /**
     * 血缘唯一标识
     */
    private String uniqueKey;

    /**
     * 所属任务
     */
    private Long taskId;


    public BatchTableTable() {
    }

    public BatchTableTable(String tableName, String col, String inputTableName, String inputCol) {
        this.tableName = tableName;
        this.col = col;
        this.inputTableName = inputTableName;
        this.inputCol = inputCol;
    }

}
