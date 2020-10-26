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


package com.dtstack.engine.sql;

/**
 * @author jiangbo
 * @date 2019/5/25
 */
public class ColumnLineage {

    private String fromDb;

    private String fromTable;

    private String fromColumn;

    private boolean isFromTempTable;

    private String toDb;

    private String toTable;

    private String toColumn;

    private boolean isToTempTable;

    public boolean isFromTempTable() {
        return isFromTempTable;
    }

    public void setFromTempTable(boolean fromTempTable) {
        isFromTempTable = fromTempTable;
    }

    public boolean isToTempTable() {
        return isToTempTable;
    }

    public void setToTempTable(boolean toTempTable) {
        isToTempTable = toTempTable;
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

    public String getFromColumn() {
        return fromColumn;
    }

    public void setFromColumn(String fromColumn) {
        this.fromColumn = fromColumn;
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

    public String getToColumn() {
        return toColumn;
    }

    public void setToColumn(String toColumn) {
        this.toColumn = toColumn;
    }

    @Override
    public String toString() {
        return String.format("%s.%s.%s --> %s.%s.%s", fromDb, fromTable, fromColumn, toDb, toTable, toColumn);
    }
}
