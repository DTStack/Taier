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

package com.dtstack.taier.develop.sql;

/**
 * 字段修改描述类
 *
 * @author jiangbo
 * @date 2018/5/22 14:04
 */
public class AlterColumnResult {

    /**
     * 修改之前的名称
     */
    private String oldColumn;

    /**
     * 修改之后的名称
     */
    private String newColumn;

    /**
     * 修改之后的类型
     */
    private String newType;

    /**
     * 修改之后的描述信息
     */
    private String newComment;

    /**
     * 修改后在哪个字段的后面
     */
    private String afterColumn;

    /**
     * 是不是排在第一个
     */
    private boolean isFirst;

    public String getOldColumn() {
        return oldColumn;
    }

    public void setOldColumn(String oldColumn) {
        this.oldColumn = oldColumn;
    }

    public String getNewColumn() {
        return newColumn;
    }

    public void setNewColumn(String newColumn) {
        this.newColumn = newColumn;
    }

    public String getNewType() {
        return newType;
    }

    public void setNewType(String newType) {
        this.newType = newType;
    }

    public String getNewComment() {
        return newComment;
    }

    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }

    public String getAfterColumn() {
        return afterColumn;
    }

    public void setAfterColumn(String afterColumn) {
        this.afterColumn = afterColumn;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    @Override
    public String toString() {
        return "AlterColumnResult{" +
                "oldColumn='" + oldColumn + '\'' +
                ", newColumn='" + newColumn + '\'' +
                ", newType='" + newType + '\'' +
                ", newComment='" + newComment + '\'' +
                ", afterColumn='" + afterColumn + '\'' +
                ", isFirst=" + isFirst +
                '}';
    }
}
