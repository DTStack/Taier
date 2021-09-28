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

import com.dtstack.batch.common.enums.TempJobType;
import com.dtstack.engine.domain.TenantProjectEntity;
import com.google.common.base.Charsets;
import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Data
public class BatchHiveSelectSql extends TenantProjectEntity {

    /**
     * 高级运行时，复杂查询所在的批量提交到engine的job的jobId
     */
    private String fatherJobId;

    private String jobId;

    private String tempTableName;

    /**
     * 当前这个名称标识taskType
     */
    private int isSelectSql;

    private String sqlText;

    private String parsedColumns;

    private Long userId;

    private int engineType;

    private Integer otherType;

    public int getIsSelectSql() {
        return isSelectSql;
    }

    public void setIsSelectSql(int isSelectSql) {
        this.isSelectSql = isSelectSql;
    }
    /**
     * 如果是数据同步任务则需要解密
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getCorrectSqlText() throws UnsupportedEncodingException {
        String sql;
        if (isSelectSql == TempJobType.SYNC_TASK.getType()) {
            sql = URLDecoder.decode(getSqlText(), Charsets.UTF_8.name());
        } else {
            sql = getSqlText();
        }
        return sql;
    }
}
