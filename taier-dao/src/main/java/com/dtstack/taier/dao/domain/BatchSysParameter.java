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



package com.dtstack.taier.dao.domain;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * date: 2022/1/24 2:58 下午
 * author: zhaiyue
 */
@Data
public class BatchSysParameter {

    private long id;

    private String paramName;

    private String paramCommand;

    /**
     * 是否删除
     */
    private int isDeleted;

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean strIsSysParam(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        String target = String.format("${%s}", this.getParamName());
        return target.equals(str);
    }
}
