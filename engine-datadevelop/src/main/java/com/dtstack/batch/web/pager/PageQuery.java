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

package com.dtstack.batch.web.pager;

import com.dtstack.batch.web.pager.Sort;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 10:42 2021/1/5
 * @Description：分页查询条件
 */
@Data
@NoArgsConstructor
public class PageQuery<T> {
    /**
     * 查询条件具体信息
     */
    private T model;

    /**
     * 开始
     */
    private int start;

    /**
     * 当前页
     */
    private int page = 1;

    /**
     * 分页大小
     */
    private int pageSize = 10;

    /**
     * 排序 -- 正序或者逆序
     */
    private String sort;

    /**
     * 排序规则
     */
    private String orderBy;

    public PageQuery(Integer page, Integer pageSize) {
        if (page != null && page != 0) {
            this.page = page;
        }
        if (pageSize != null && pageSize != 0) {
            this.pageSize = pageSize;
        }
        this.start = getStart();
    }

    public PageQuery(Integer page, Integer pageSize, String orderBy, String sort) {
        this(page, pageSize);
        this.orderBy = orderBy;
        setSort(sort);
    }

    public PageQuery(T model) {
        this(1, 1000);
        this.model = model;
    }

    /**
     * 获取起始位置
     *
     * @return
     */
    public int getStart() {
        start = (this.page - 1) * this.pageSize;
        return start;
    }

    /**
     * 设置排序规则
     *
     * @param sort
     */
    public void setSort(String sort) {
        if (Sort.DESC.getValue().equalsIgnoreCase(sort)) {
            this.sort = Sort.DESC.getValue();
        } else {
            this.sort = Sort.ACS.getValue();
        }
    }
}
