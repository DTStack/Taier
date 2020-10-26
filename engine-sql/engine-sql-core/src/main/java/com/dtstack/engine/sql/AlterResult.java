package com.dtstack.engine.sql;

import org.apache.commons.math3.util.Pair;

import java.util.List;

/**
 * alter语句解析的结果
 *
 * @author jiangbo
 */
public class AlterResult {

    /**
     * alter语句的细分类型
     */
    private TableOperateEnum alterType;


    /**
     * 原DB
     */
    private String oldDB;

    /**
     * 原表名
     */
    private String oldTableName;


    /**
     * 新DB
     */
    private String newDB;


    /**
     * 新表名
     */
    private String newTableName;

    /**
     * 表属性
     */
    private List<Pair<String,String>> tableProperties;

    /**
     * 序列化属性
     */
    private List<Pair<String,String>> serdeProperties;

    /**
     * 新增加的分区
     */
    private List<Partition> newPartitions;

    /**
     * 删除的分区
     */
    private List<PartCondition> dropParts;

    /**
     * 老分区
     */
    private Pair<String,String> oldPart;

    /**
     * 新分区
     */
    private Pair<String,String> newPart;

    /**
     * 新的表路径
     */
    private String newLocation;

    /**
     * 分区路径
     */
    private Pair<String,String> newLocationPart;

    /**
     * 新增字段
     */
    private List<Column> newColumns;

    /**
     * 修改的字段信息
     */
    private AlterColumnResult alterColumnResult;

    /**
     * 修改的分区
     */
    private List<Pair<String,String>> renamePart;

    public List<Pair<String,String>> getRenamePart() {
        return renamePart;
    }

    public void setRenamePart(List<Pair<String,String>> renamePart) {
        this.renamePart = renamePart;
    }

    public TableOperateEnum getAlterType() {
        return alterType;
    }

    public void setAlterType(TableOperateEnum alterType) {
        this.alterType = alterType;
    }

    public String getOldTableName() {
        return oldTableName;
    }

    public void setOldTableName(String oldTableName) {
        this.oldTableName = oldTableName;
    }

    public String getNewTableName() {
        return newTableName;
    }

    public void setNewTableName(String newTableName) {
        this.newTableName = newTableName;
    }

    public List<Pair<String, String>> getTableProperties() {
        return tableProperties;
    }

    public void setTableProperties(List<Pair<String, String>> tableProperties) {
        this.tableProperties = tableProperties;
    }

    public List<Pair<String, String>> getSerdeProperties() {
        return serdeProperties;
    }

    public void setSerdeProperties(List<Pair<String, String>> serdeProperties) {
        this.serdeProperties = serdeProperties;
    }

    public List<Partition> getNewPartitions() {
        return newPartitions;
    }

    public void setNewPartitions(List<Partition> newPartitions) {
        this.newPartitions = newPartitions;
    }

    public List<PartCondition> getDropParts() {
        return dropParts;
    }

    public void setDropParts(List<PartCondition> dropParts) {
        this.dropParts = dropParts;
    }

    public Pair<String, String> getOldPart() {
        return oldPart;
    }

    public void setOldPart(Pair<String, String> oldPart) {
        this.oldPart = oldPart;
    }

    public Pair<String, String> getNewPart() {
        return newPart;
    }

    public void setNewPart(Pair<String, String> newPart) {
        this.newPart = newPart;
    }

    public String getNewLocation() {
        return newLocation;
    }

    public void setNewLocation(String newLocation) {
        this.newLocation = newLocation;
    }

    public Pair<String, String> getNewLocationPart() {
        return newLocationPart;
    }

    public void setNewLocationPart(Pair<String, String> newLocationPart) {
        this.newLocationPart = newLocationPart;
    }

    public List<Column> getNewColumns() {
        return newColumns;
    }

    public void setNewColumns(List<Column> newColumns) {
        this.newColumns = newColumns;
    }

    public AlterColumnResult getAlterColumnResult() {
        return alterColumnResult;
    }

    public void setAlterColumnResult(AlterColumnResult alterColumnResult) {
        this.alterColumnResult = alterColumnResult;
    }

    public String getOldDB() {
        return oldDB;
    }

    public void setOldDB(String oldDB) {
        this.oldDB = oldDB;
    }

    public String getNewDB() {
        return newDB;
    }

    public void setNewDB(String newDB) {
        this.newDB = newDB;
    }

    @Override
    public String toString() {
        return "AlterResult{" +
                "alterType=" + alterType +
                ", oldTableName='" + oldTableName + '\'' +
                ", newTableName='" + newTableName + '\'' +
                ", tableProperties=" + tableProperties +
                ", serdeProperties=" + serdeProperties +
                ", newPartitions=" + newPartitions +
                ", dropParts=" + dropParts +
                ", oldPart=" + oldPart +
                ", newPart=" + newPart +
                ", newLocation='" + newLocation + '\'' +
                ", newLocationPart=" + newLocationPart +
                ", newColumns=" + newColumns +
                ", alterColumnResult=" + alterColumnResult +
                '}';
    }
}
