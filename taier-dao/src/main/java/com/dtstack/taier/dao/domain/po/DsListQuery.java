package com.dtstack.taier.dao.domain.po;

import lombok.Data;

import java.util.List;

/**
 * 数据源列表参数类
 * @description:
 * @author: liuxx
 * @date: 2021/3/26
 */
@Data
public class DsListQuery extends DaoPageParam {
    /**
     * 搜索参数
     */
    private String search;
    /**
     * 数据源类型
     */
    private List<String> dataTypeList;
    /**
     * 产品类型
     */
    private List<Integer> appTypeList;
    /**
     * 是否显示默认数据库，0为不显示，1为显示
     */
    private Integer isMeta;
    /**
     * 连接状态
     */
    private List<Integer> status;

}
