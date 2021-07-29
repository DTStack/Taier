package com.dtstack.engine.datasource.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dtstack.dtcenter.common.pager.PageResult;
import com.dtstack.engine.datasource.common.utils.datakit.Asserts;
import com.dtstack.engine.datasource.common.utils.datakit.Collections;
import com.dtstack.engine.datasource.param.BasePageParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;


public class PageUtil {

    private final static int DEFAULT_CURRENT_PAGE = 1;
    private final static int DEFAULT_PAGE_SIZE = 10;

    private PageUtil() {
    }

    /**
     * 数据层mybatis-iPage 转为 视图层page
     *
     * @param iPage  数据层iPage
     * @param mapper 分页记录中映射 T->V
     * @return 视图层page
     */
    public static <T, V> PageResult<List<V>> transfer(IPage<T> iPage, Function<T, V> mapper) {
        if (Objects.isNull(iPage)) {
            return new PageResult<>(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE, 0, 0, Collections.emptyList());
        }
        int current = (int) iPage.getCurrent();
        int size = (int) iPage.getSize();
        int total = (int) iPage.getTotal();
        int pages = (int) iPage.getPages();
        if (iPage.getTotal() == 0) {
            return new PageResult<>(current, size, total, pages, Collections.emptyList());
        }
        return new PageResult<>(current, size, total, pages,
                Collections.mapperCollection(iPage.getRecords(), mapper, ArrayList::new));
    }

    /**
     * 视图层page
     *
     * @return 视图层page
     */
    public static <V> PageResult<List<V>> transfer(List<V> dataList, BasePageParam param, int total) {
        Asserts.notNull(param);
        int pageSize = param.getPageSize() == null || param.getPageSize() == 0 ? DEFAULT_PAGE_SIZE : param.getPageSize();
        int currentPage = param.getCurrentPage() == null ? DEFAULT_CURRENT_PAGE : param.getCurrentPage();

        if (total == 0 || Collections.isEmpty(dataList)) {
            return new PageResult<>(currentPage, pageSize, 0, 0, Collections.emptyList());
        }
        int totalPage = total / pageSize;
        totalPage = (total % pageSize == 0 ? totalPage : totalPage + 1); // 计算总页数
        return new PageResult<>(currentPage, pageSize, total, totalPage, dataList);
    }

    /**
     * 返回空的PageResult
     *
     * @param param 分页参数
     */
    public static <V> PageResult<List<V>> empty(BasePageParam param) {
        return transfer(null, param, 0);
    }

}
