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

package com.dtstack.taier.dao.domain.po;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dtstack.taier.common.param.DtInsightPageAuthParam;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;


/**
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/26
 */
public class BasePageParam extends DtInsightPageAuthParam {

    public static final int DEFAULT_PAGE_NO = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 分页db查询，起始偏移量，limit A,B 中的A
     */
    @ApiModelProperty(hidden = true)
    private Integer start;
    /**
     * 分页db查询，结束偏移量，limit A,B 中的B
     */
    @ApiModelProperty(hidden = true)
    private Integer end;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    /**
     * 生成mybatis-plus能访问的分页对象
     */
    public <T> Page<T> page() {
        if (Objects.isNull(this.getCurrentPage())) {
            super.setCurrentPage(DEFAULT_PAGE_NO);
        }
        if (Objects.isNull(this.getPageSize())) {
            super.setPageSize(DEFAULT_PAGE_SIZE);
        }
        return new Page<>(super.getCurrentPage(), super.getPageSize());
    }


    public BasePageParam turn() {
        if (Objects.isNull(this.getCurrentPage())) {
            this.setCurrentPage(DEFAULT_PAGE_NO);
        }
        if (Objects.isNull(this.getPageSize())) {
            this.setPageSize(DEFAULT_PAGE_SIZE);
        }
        int start = ((this.getCurrentPage() == 0 ? 1 : this.getCurrentPage()) - 1) * this.getPageSize();
        this.setStart(start);
        this.setEnd(this.getPageSize());
        return this;
    }

    @Override
    public Integer getStart() {
        return start;
    }

    @Override
    public void setStart(Integer start) {
        this.start = start;
    }

    @Override
    public Integer getEnd() {
        return end;
    }

    @Override
    public void setEnd(Integer end) {
        this.end = end;
    }

    @Override
    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
