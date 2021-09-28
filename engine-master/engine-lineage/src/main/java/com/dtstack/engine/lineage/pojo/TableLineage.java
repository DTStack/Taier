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

package com.dtstack.engine.lineage.pojo;

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
