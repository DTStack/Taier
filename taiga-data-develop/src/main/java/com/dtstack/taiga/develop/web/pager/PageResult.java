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

package com.dtstack.taiga.develop.web.pager;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 10:31 2021/1/5
 * @Description：分页结果
 */
@Data
@NoArgsConstructor
public class PageResult<T> {
    /**
     * 空分页结果
     */
    public final static PageResult EMPTY_PAGE_RESULT = new PageResult<>();

    /**
     * 当前页
     */
    private Integer currentPage = 0;

    /**
     * 分页大小
     */
    private Integer pageSize = 0;

    /**
     * 总数
     */
    private int totalCount;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 数据信息
     */
    private T data;

    /**
     * 附件
     */
    private Object attachment;

    /**
     * 分页查询通用方法
     */
    public PageResult(T data, int totalCount, PageQuery pageQuery) {
        this.data = data;
        this.totalCount = totalCount;
        this.currentPage = pageQuery.getPage();
        this.pageSize = pageQuery.getPageSize();
        int totalPage = totalCount / pageSize;
        this.totalPage = (totalCount % pageSize == 0 ? totalPage : totalPage + 1);
    }

    /**
     * 分页查询可用方法
     */
    public PageResult(int currentPage, int pageSize, int totalCount, int totalPage, T data) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPage = totalPage;
        this.data = data;
    }
}
