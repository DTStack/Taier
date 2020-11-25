package com.dtstack.engine.lineage;

/**
 * @author chener
 * @Classname BatchLineage
 * @Description TODO
 * @Date 2020/11/23 15:14
 * @Created chener@dtstack.com
 */
public class BatchLineage {
    private Long tenantId;
    private Long taskId;
    private Long dataSourceId;
    private String tableNam;
    private String col;
    private Long inputDataSourceId;
    private String inputTableName;
    private String inputCol;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getTableNam() {
        return tableNam;
    }

    public void setTableNam(String tableNam) {
        this.tableNam = tableNam;
    }

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public Long getInputDataSourceId() {
        return inputDataSourceId;
    }

    public void setInputDataSourceId(Long inputDataSourceId) {
        this.inputDataSourceId = inputDataSourceId;
    }

    public String getInputTableName() {
        return inputTableName;
    }

    public void setInputTableName(String inputTableName) {
        this.inputTableName = inputTableName;
    }

    public String getInputCol() {
        return inputCol;
    }

    public void setInputCol(String inputCol) {
        this.inputCol = inputCol;
    }
}
