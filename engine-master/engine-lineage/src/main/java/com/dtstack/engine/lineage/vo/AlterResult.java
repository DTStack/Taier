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

package com.dtstack.engine.lineage.vo;

import com.dtstack.engine.common.enums.TableOperateEnum;
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

    /**
     * 分区路径
     */
    private Pair<String,String> newLocationPart;

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

    public Pair<String, String> getNewLocationPart() {
        return newLocationPart;
    }

    public void setNewLocationPart(Pair<String, String> newLocationPart) {
        this.newLocationPart = newLocationPart;
    }
}
