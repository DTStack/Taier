package com.dtstack.engine.api.vo.lineage;

import com.dtstack.engine.api.enums.TableOperateEnum;
import org.apache.commons.math3.util.Pair;

import java.util.List;

/**
 * @Author: ZYD
 * Date: 2021/4/2 10:13
 * Description: alter语句解析结果
 * @since 1.0.0
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
     * 新的表路径
     */
    private String newLocation;

    public TableOperateEnum getAlterType() {
        return alterType;
    }

    public void setAlterType(TableOperateEnum alterType) {
        this.alterType = alterType;
    }

    public String getOldDB() {
        return oldDB;
    }

    public void setOldDB(String oldDB) {
        this.oldDB = oldDB;
    }

    public String getOldTableName() {
        return oldTableName;
    }

    public void setOldTableName(String oldTableName) {
        this.oldTableName = oldTableName;
    }

    public String getNewDB() {
        return newDB;
    }

    public void setNewDB(String newDB) {
        this.newDB = newDB;
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

    public String getNewLocation() {
        return newLocation;
    }

    public void setNewLocation(String newLocation) {
        this.newLocation = newLocation;
    }
}
