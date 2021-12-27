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

import com.dtstack.batch.utils.Asserts;
import com.dtstack.engine.common.param.DtInsightPageAuthParam;

import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 10:47 2021/1/5
 * @Description：分页工具
 */
public class PageUtil {

    private final static int DEFAULT_CURRENT_PAGE = 1;
    private final static int DEFAULT_PAGE_SIZE = 10;



    /**
     * 视图层page
     *
     * @return 视图层page
     */
    public static <V> com.dtstack.engine.common.pager.PageResult<List<V>> transfer(List<V> dataList, DtInsightPageAuthParam param, int total) {
        Asserts.notNull(param);
        int pageSize = param.getPageSize() == null || param.getPageSize() == 0 ? DEFAULT_PAGE_SIZE : param.getPageSize();
        int currentPage = param.getCurrentPage() == null ? DEFAULT_CURRENT_PAGE : param.getCurrentPage();
        int totalPage = total / pageSize;
        totalPage = (total % pageSize == 0 ? totalPage : totalPage + 1); // 计算总页数
        return new com.dtstack.engine.common.pager.PageResult <>(currentPage, pageSize, total, totalPage, dataList);
    }


    /**
     * 自定义分页器
     *
     * @param list
     * @param currentPage
     * @param pageSize
     * @return
     */
    public static PageResult getPageResult(List list, Integer currentPage, Integer pageSize) {
        Integer total = list.size();
        int toIndex = maxIndexLimit(currentPage * pageSize, total);
        int fromIndex = maxIndexLimit((currentPage - 1) * pageSize, total);

        int totalPage = list.size() / pageSize;
        if (list.size() % pageSize != 0) {
            totalPage++;
        }
        list = list.subList(fromIndex, toIndex);
        PageResult<List> pageResult = new PageResult(currentPage, pageSize, total, totalPage, list);
        return pageResult;
    }

    /**
     * 设置最大的切割位置
     *
     * @param index
     * @param total
     * @return
     */
    private static int maxIndexLimit(int index, int total) {
        if (index >= total) {
            index = total;
        }
        return index;
    }
}
