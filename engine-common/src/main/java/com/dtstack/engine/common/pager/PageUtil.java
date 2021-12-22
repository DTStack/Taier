package com.dtstack.engine.common.pager;

import java.util.List;

/**
 * @author: 小北(xiaobei @ dtstack.com)
 * @program: DAGScheduleX
 * @description:
 * @create: 2021-12-16 00:40
 **/
public class PageUtil {

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

    private static int maxIndexLimit(int index, int total) {
        if (index >= total) {
            index = total;
        }
        return index;
    }
}
