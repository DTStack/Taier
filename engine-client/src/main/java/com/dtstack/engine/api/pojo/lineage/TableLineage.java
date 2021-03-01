package com.dtstack.engine.api.pojo.lineage;

public class TableLineage {

    /**
     * 血缘上游schema
     */
    private String fromSchema;

    /**
     * 血缘上游db
     */
    private String fromDb;

    /**
     * 血缘上游表名
     */
    private String fromTable;

    /**
     * 血缘下游schema
     */
    private String toSchema;

    /**
     * 血缘下游db
     */
    private String toDb;

    /**
     * 血缘下游表名
     */
    private String toTable;

    public String getFromSchema() {
        return fromSchema;
    }

    public void setFromSchema(String fromSchema) {
        this.fromSchema = fromSchema;
    }

    public String getToSchema() {
        return toSchema;
    }

    public void setToSchema(String toSchema) {
        this.toSchema = toSchema;
    }

    public String getFromDb() {
        return fromDb;
    }

    public void setFromDb(String fromDb) {
        this.fromDb = fromDb;
    }

    public String getFromTable() {
        return fromTable;
    }

    public void setFromTable(String fromTable) {
        this.fromTable = fromTable;
    }

    public String getToDb() {
        return toDb;
    }

    public void setToDb(String toDb) {
        this.toDb = toDb;
    }

    public String getToTable() {
        return toTable;
    }

    public void setToTable(String toTable) {
        this.toTable = toTable;
    }
}
