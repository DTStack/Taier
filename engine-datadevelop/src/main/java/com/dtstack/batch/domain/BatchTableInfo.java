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

package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author jiangbo
 * @time 2018/1/8
 */
@Data
public class BatchTableInfo extends TenantProjectEntity {

    /**
     * 表名称
     */
    private String tableName;

    /***
     * 表类型
     */
    private Integer tableType;

    /**
     * 创建表的用户id
     */
    private Long userId;

    /**
     * 表负责人
     */
    private Long chargeUserId;

    private Long modifyUserId;

    /**
     * 表大小
     */
    private Long tableSize;

    /**
     * 表下文件数量
     */
    private Long tableFileCount;

    /**
     * 表大小更新时间
     */
    private Timestamp sizeUpdateTime;

    /**
     * 类目id
     */
    private Long catalogueId;

    /**
     * 类目路径
     */
    private String path;

    /**
     * hdfs路径
     */
    private String location;

    /**
     * 列分隔符
     */
    private String delim;

    /**
     * 存储格式
     */
    private String storeType;

    /**
     * 生命周期，单位：day
     */
    private Integer lifeDay;

    /**
     * 生命周期状态，0：未开始，1：存活，2：销毁，3：执行过程出现异常
     */
    private Integer lifeStatus;

    /**
     * 是否是脏数据表 0-否，1-是
     */
    private Integer isDirtyDataTable = 0;

    /**
     * 表结构最近修改时间
     */
    private Timestamp lastDdlTime;

    /**
     * 表数据最后修改时间
     */
    private Timestamp lastDmlTime;

    /**
     * 表描述
     */
    private String tableDesc;

    /**
     * 层级
     */
    private String grade;

    /**
     * 主题域
     */
    private String subject;

    /**
     * 刷新频率
     */
    private String refreshRate;

    /**
     * 增量类型
     */
    private String increType;

    /**
     * 是否忽略
     */
    private Integer isIgnore;

    private String checkResult;

    /**
     * 是否分区
     */
    private Integer isPartition;

    public Integer getIsIgnore() {
        return isIgnore;
    }

    public void setIsIgnore(Integer isIgnore) {
        this.isIgnore = isIgnore;
    }

    public Integer getIsDirtyDataTable() {
        return isDirtyDataTable;
    }

    public void setIsDirtyDataTable(Integer isDirtyDataTable) {
        this.isDirtyDataTable = isDirtyDataTable;
    }

    public Integer getIsPartition() {
        return isPartition;
    }

    public void setIsPartition(Integer isPartition) {
        this.isPartition = isPartition;
    }
}
