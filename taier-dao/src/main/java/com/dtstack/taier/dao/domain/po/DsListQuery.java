package com.dtstack.taier.dao.domain.po;


import java.util.List;

/**
 * 数据源列表参数类
 * @description:
 * @author: liuxx
 * @date: 2021/3/26
 */
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

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public List<String> getDataTypeList() {
        return dataTypeList;
    }

    public void setDataTypeList(List<String> dataTypeList) {
        this.dataTypeList = dataTypeList;
    }

    public List<Integer> getAppTypeList() {
        return appTypeList;
    }

    public void setAppTypeList(List<Integer> appTypeList) {
        this.appTypeList = appTypeList;
    }

    public Integer getIsMeta() {
        return isMeta;
    }

    public void setIsMeta(Integer isMeta) {
        this.isMeta = isMeta;
    }

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }
}
