package com.dtstack.batch.web.pager;

import com.dtstack.batch.web.pager.PageResult;

import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 10:47 2021/1/5
 * @Description：分页工具
 */
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
