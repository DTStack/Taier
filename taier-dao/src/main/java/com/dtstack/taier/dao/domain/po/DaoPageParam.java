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

import java.util.Objects;

/**
 *
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a>
 * @date 2021/3/17
 * @desc 分页参数基类
 */
public class DaoPageParam {

    public static final int DEFAULT_PAGE_NO = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 1000;

    /**
     * 分页db查询，起始偏移量，limit A,B 中的A
     */
    private Integer start;
    /**
     * 分页db查询，结束偏移量，limit A,B 中的B
     */
    private Integer end;

    private Long tenantId;

    private Integer pageSize;
    private Integer currentPage;
    private String sort;
    private String sortColumn;


    /**
     * 生成mybatis-plus能访问的分页对象
     */
    public <T> Page<T> page() {
        return new Page<>(getCurrentPage() == null ? DEFAULT_PAGE_NO : getCurrentPage(), getPageSize() == null ? DEFAULT_PAGE_SIZE : getPageSize());
    }

    /**
     * turn to page param for `db-query` and `es`
     */
    public static <T extends DaoPageParam> T turn(T t) {
        if (Objects.isNull(t.getCurrentPage())) {
            t.setCurrentPage(DEFAULT_PAGE_NO);
        }
        if (Objects.isNull(t.getPageSize())) {
            t.setPageSize(DEFAULT_PAGE_SIZE);
        }
        int start = ((t.getCurrentPage() == 0 ?
                1 : t.getCurrentPage()) - 1) * t.getPageSize();
        t.setStart(start);
        t.setEnd(t.getPageSize());
        return t;
    }

    public DaoPageParam turn() {
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

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }
}
