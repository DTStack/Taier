package com.dtstack.batch.domain;

import com.dtstack.engine.api.domain.TenantProjectEntity;
import lombok.Data;

/**
 * @author sanyue
 */
@Data
public class BatchModelMonitorRecord extends TenantProjectEntity {

    /**
     * '不规范模型总数'
     */
    private Integer badTable;
    /**
     * '不规范字段总数'
     */
    private Integer badColumn;
    /**
     * '层级不规范数'
     */
    private Integer grade;
    /**
     * '主题域不规范数'
     */
    private Integer subject;
    /**
     * 刷新不规范数
     */
    private Integer refreshRate;
    /**
     * 增量方式不规范数
     */
    private Integer increType;
    /**
     * '字段名不规范数'
     */
    private Integer colName;
    /**
     * '字段数据类型不规范数'
     */
    private Integer dataType;
    /**
     * '字段描述不规范数'
     */
    private Integer colDesc;

    public BatchModelMonitorRecord(){

    }

    public BatchModelMonitorRecord(Integer badTable, Integer badColumn, Integer grade, Integer subject, Integer refreshRate, Integer increType, Integer colName, Integer dataType, Integer colDesc) {
        this.badTable = badTable;
        this.badColumn = badColumn;
        this.grade = grade;
        this.subject = subject;
        this.refreshRate = refreshRate;
        this.increType = increType;
        this.colName = colName;
        this.dataType = dataType;
        this.colDesc = colDesc;
    }
}
